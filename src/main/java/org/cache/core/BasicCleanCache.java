package org.cache.core;

import org.cache.config.CommonConfig;
import org.cache.config.CommonUtils;
import org.cache.interfaces.EvictionCallback;
import org.cache.interfaces.ICleanCache;
import org.cache.interfaces.ReplenishCallback;
import org.cache.model.CacheNode;
import org.cache.model.DelayedCacheObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.logging.Logger;

import static org.cache.config.CommonConfig.DISK_CACHE_PATH;
import static org.cache.config.CommonConfig.LOGGING_LEVEL;

/**
 * A Basic simple cache implementation
 * @param <K> Cache Key Type
 * @param <V> Cache Value object
 */
public class BasicCleanCache<K,V extends Serializable> extends CacheStats implements ICleanCache<K,V> {


    private final static Logger LOGGER;
    static {
        LOGGER = Logger.getLogger(BasicCleanCache.class.getName());
        LOGGER.setLevel(LOGGING_LEVEL);
    }
    // Call back method and will be call while the cache replenish a value
    private ReplenishCallback<K,V> replenishCallback = null;

    private EvictionCallback<K,V> evictionCallback = null;


    //Cache object expire time
    private final Long cacheTimeout;

    //Map for storing the key and value
    private final ConcurrentHashMap<K, CacheNode<K,V>> cache = new ConcurrentHashMap<>();

    //Delay Queue for maintaining the non expire object
    private final DelayQueue<DelayedCacheObject<K,V>> cleaningUpQueue = new DelayQueue<>();

    // most recent element of the cache
    private CacheNode<K,V> head;

    //least recent element of the cache
    private CacheNode<K,V> end;

    private CacheNode<K,V> nextDiskCacheNode;


    //Don't change use Factory method for cache instance
    private BasicCleanCache() {
        super(0L, 0L);
        this.cacheTimeout = 0L;
    }

    /**
     *
     * @param cacheTimeout cache time out time in millis
     * @param cacheSize Cache capacity
     * @param replenishCallback Callback method
     * @param evictionCallback Callback method
     */
    protected BasicCleanCache(Long cacheTimeout, Long cacheSize,Long memoryThresholdSize,
                              ReplenishCallback<K,V> replenishCallback, EvictionCallback<K,V> evictionCallback) {
        super(cacheSize, memoryThresholdSize);
        startCleanerThread();
        this.cacheTimeout = cacheTimeout;
        this.replenishCallback = replenishCallback;
        this.evictionCallback = evictionCallback;
        this.clear();
    }

    /**
     *  Clear the whole cache
     */
    @Override
    public void clear() {
        cache.clear();
        cleaningUpQueue.clear();
        end = null;
        head = null;
    }

    /**
     * This method check if the key exist in the cache
     * @param key key of the cached object
     * @return
     */
    @Override
    public boolean containsKey(K key) {
        return this.cache.containsKey(key);
    }

    /**
     *  This method will return the size of the cache
     * @return size of cache
     */
    @Override
    public long size() {
        return cache.size();
    }

    /**
     * This method will return the cached object for the key provided
     * @param key key of the cached object
     * @return cached object for the key provided
     */
    @Override
    public Optional<V> get(K key) {
        Optional<V> value = Optional.empty();
        this.increaseAccessCount();
        if(cache.containsKey(key)){
            CacheNode<K,V> cacheNode = cache.get(key);
            long start = getCurrentTimeMillis();
            if(cacheNode == this.nextDiskCacheNode){
                this.nextDiskCacheNode = this.nextDiskCacheNode.getPrev();
            }
            pushCacheToDisk();
            delete(cacheNode);
            setHead(cacheNode);
            value = Optional.ofNullable(cacheNode.getValue(this)).map(SoftReference::get);
            long end = getCurrentTimeMillis();
            increaseLruTimeSpentBy(end-start);
        }else{
            if(this.replenishCallback != null){
                this.replenishCount();
                value = callReplenishPolicy(key);
                value.ifPresent(v -> this.put(key, v));
            }
        }
        return value;
    }

    private Optional<V> callReplenishPolicy(K key) {
        Optional<V> value;
        long start = getCurrentTimeMillis();
        //Get the value using the replenish callback and add add key,value to cache
        value = this.replenishCallback.call(key);
        long end = getCurrentTimeMillis();
        increaseReplenishmentTimeSpentBy(end-start);
        return value;
    }

    private long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Add a new object in the cache
     * @param key key for the object
     * @param value object
     */
    @Override
    public void put(K key, V value) {
        if(key == null) return;

        if(value != null){
            SoftReference<V> reference = new SoftReference<>(value);
            long expiryTime = getCurrentTimeMillis() + this.cacheTimeout;
            set(key, reference);
            cleaningUpQueue.put(new DelayedCacheObject<>(key, expiryTime));
        }
        pushCacheToDisk();
    }

    /**
     * This method will remove the object mapped with key provided
     * @param key key of the cached object need to be removed
     * @return Removed object
     */
    @Override
    public Optional<V> remove(K key) {
        Optional<V> empty = Optional.empty();
        if(key == null){
            return empty;
        }
        CacheNode<K,V> removedNode =  cache.remove(key);
        if(removedNode != null){
            if(removedNode == this.nextDiskCacheNode){
                this.nextDiskCacheNode = this.nextDiskCacheNode.getPrev();
            }
            delete(removedNode);
            removedNode.clearValue(this);
            popCacheToMemory();
            return Optional.ofNullable(removedNode.getValue(this)).map(SoftReference::get);
        }else{
            return empty;
        }
    }

    @Override
    public CacheStatistics getCacheStatistics() {
        return new CacheStatistics().setTotalCacheSize(this.getCapacity())
                .setMemorySize(this.size() - this.diskCachedNodes)
                .setCurrentDiskSize(this.diskCachedNodes)
                .setTotalAccessCount(this.accessCount)
                .setHitRatio(this.calculateHitRatio())
                .setMissRatio(this.calculateMissRatio())
                .setAvgValueReplenishmentTimeSpent(this.calculateAvgReplenishmentTimeSpent())
                .setAvgLruOptimizationTimeSpent(this.calculateAvgLruOptimizationTimeSpent());
    }

    /**
     * This method will return the total capacity of the cache
     * @return
     */
    public long getCapacity() {
        return this.capacity;
    }

    /**
     * Clear thread for removing the expired keys
     */
    private void startCleanerThread() {
        Thread cleanerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    DelayedCacheObject<K,V> delayedCacheObject = cleaningUpQueue.take();
                    callEvictionPolicy(delayedCacheObject.getKey(), cache.get(delayedCacheObject.getKey()).getValue(this).get());
                    remove(delayedCacheObject.getKey());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        cleanerThread.setDaemon(true);
        cleanerThread.start();
    }



    /**
     * Set the Head of the linked list
     * @param cacheNode
     */
    private void setHead(CacheNode<K,V> cacheNode){
        cacheNode.setNext(head);
        cacheNode.setPrev(null);

        if(head!=null)
            head.setPrev(cacheNode);

        head = cacheNode;

        if(end ==null)
            end = head;
    }

    /**
     * This method will delete node
    */
    private void delete(CacheNode<K,V> cacheNode){
        if(cacheNode.getPrev()!=null){
            cacheNode.getPrev().setNext(cacheNode.getNext());
        }else{
            head = cacheNode.getNext();
        }

        if(cacheNode.getNext()!=null){
            cacheNode.getNext().setPrev(cacheNode.getPrev());
        }else{
            end = cacheNode.getPrev();
        }
    }

    /**
     *  Add a new reference in the linked list
     * @param key key
     * @param reference reference need to be add in the linked list
     */
    private void set(K key, SoftReference<V> reference) {
        if(cache.containsKey(key)){
            // update the old value
            CacheNode<K,V> old = cache.get(key);
            old.setValue(reference);
            delete(old);
            setHead(old);
        }else{
            CacheNode<K,V> newNode = new CacheNode<>(key, reference);
            if(cache.size()>=capacity){
                //Remove last node if the cache is full
                K removedKey = end.getKey();
                V removedValue = end.getValue(this).get();
                callEvictionPolicy(removedKey, removedValue);
                if(end != null) {
                    cache.remove(end.getKey());
                    // remove last node
                    delete(end);
                }
            }
            setHead(newNode);

            cache.put(key, newNode);
        }
    }

    /**
     * Eviction Policy for the removed object
     * @param removedKey
     * @param removedValue
     */
    private void callEvictionPolicy(K removedKey, V removedValue) {
        if (evictionCallback != null) {
            evictionCallback.call(removedKey, removedValue);
        } else {
            LOGGER.info(String.format("Expired Key = %s | Expired Value = %s", removedKey.toString(), Objects.requireNonNull(removedValue).toString()));
        }
    }

    private void increaseReplenishmentTimeSpentBy(long elapsedTime) {
        this.replenishmentTimeSpent+=elapsedTime;
    }

    private void increaseLruTimeSpentBy(long elapsedTime) {
        this.lruTimeSpent+=elapsedTime;
    }

    private void replenishCount() {
        this.replenishCount++;
    }

    private void increaseAccessCount() {
        this.accessCount++;
    }

    private BigDecimal calculateAvgLruTimeSpent() {
        if(this.accessCount != 0) {
            return BigDecimal.valueOf(lruTimeSpent).divide(BigDecimal.valueOf(this.accessCount), CommonConfig.SCALE, CommonConfig.ROUNDING_MODE);
        }else{
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal calculateAvgReplenishmentTimeSpent() {
        if(this.replenishCount != 0) {
            return BigDecimal.valueOf(replenishmentTimeSpent).divide(BigDecimal.valueOf(this.replenishCount), CommonConfig.SCALE, CommonConfig.ROUNDING_MODE);
        }else{
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal calculateAvgLruOptimizationTimeSpent() {
        if(this.replenishCount != 0) {
            return BigDecimal.valueOf(lruTimeSpent).divide(BigDecimal.valueOf(this.accessCount), CommonConfig.SCALE, CommonConfig.ROUNDING_MODE);
        }else{
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal calculateMissRatio() {
        if(this.accessCount != 0 ) {
            return BigDecimal.valueOf(this.accessCount).divide(BigDecimal.valueOf(this.accessCount), CommonConfig.SCALE, CommonConfig.ROUNDING_MODE);
        }else{
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal calculateHitRatio() {
        if(this.accessCount != 0) {
            return BigDecimal.valueOf(this.accessCount - this.replenishCount).divide(BigDecimal.valueOf(this.accessCount), CommonConfig.SCALE, CommonConfig.ROUNDING_MODE);
        }else{
            return BigDecimal.ZERO;
        }
    }

    private void pushCacheToDisk() {
        if(cache.size() > this.memoryThresholdSize) {
            if(this.memoryThresholdSize == 0){
                nextDiskCacheNode = head;
            }else{
                if (nextDiskCacheNode == null) {
                    nextDiskCacheNode = end;
                }
            }
            nextDiskCacheNode.flushToDisk(this);
            nextDiskCacheNode = nextDiskCacheNode.getPrev();
        }
    }

    private void popCacheToMemory() {

    }

}