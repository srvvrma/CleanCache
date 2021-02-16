package org.cache.core;

import org.cache.config.CommonConfig;
import org.cache.interfaces.ICleanCache;
import org.cache.interfaces.ReplenishCallback;
import org.cache.model.CacheNode;
import org.cache.model.DelayedCacheObject;

import java.lang.ref.SoftReference;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.logging.Logger;

import static org.cache.config.CommonConfig.LOGGING_LEVEL;

/**
 * A Basic simple cache implementation
 * @param <K> Cache Key Type
 * @param <V> Cache Value object
 */
public class BasicCleanCache<K,V> implements ICleanCache<K,V> {

    private final static Logger LOGGER;

    static {
        LOGGER = Logger.getLogger(BasicCleanCache.class.getName());
        LOGGER.setLevel(LOGGING_LEVEL);
    }
    // Call back method and will be call while the cache replenish a value
    private ReplenishCallback<K,V> callback = null;

    // Max capacity of the cache
    private final int capacity;

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


    //Don't change use Factory method for cache instance
    private BasicCleanCache() {
        this(CommonConfig.DEFAULT_CACHE_OBJECT_TIMEOUT,CommonConfig.DEFAULT_CACHE_SIZE,null);
    }

    /**
     *
     * @param cacheTimeout cache time out time in millis
     * @param cacheSize Cache capacity
     * @param callback Callback method
     */
    protected BasicCleanCache(Long cacheTimeout, Integer cacheSize,ReplenishCallback<K,V> callback) {
        startCleanerThread();
        this.cacheTimeout = cacheTimeout;
        this.capacity = cacheSize;
        this.callback = callback;
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
        if(cache.containsKey(key)){
            CacheNode<K,V> cacheNode = cache.get(key);
            delete(cacheNode);
            setHead(cacheNode);
            return Optional.ofNullable(cacheNode.getValue()).map(SoftReference::get);
        }else{
            return Optional.empty();
        }
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
            long expiryTime = System.currentTimeMillis() + this.cacheTimeout;
            set(key, reference);
            cleaningUpQueue.put(new DelayedCacheObject<>(key, reference, expiryTime));
        }
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
            delete(removedNode);
            return Optional.ofNullable(removedNode.getValue()).map(SoftReference::get);
        }else{
            return empty;
        }
    }

    /**
     * This mehtod will return the total capacity of the cache
     * @return
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Clear thread for removing the expired keys
     */
    private void startCleanerThread() {
        Thread cleanerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    DelayedCacheObject<K,V> delayedCacheObject = cleaningUpQueue.take();
                    remove(delayedCacheObject.getKey());
                    LOGGER.info(String.format("Expired Key = %s | Expired Value = %s",delayedCacheObject.getKey().toString(), delayedCacheObject.getReference().get().toString()));
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
                K removedKey = end.getKey();
                V removedValue = end.getValue().get();
                cache.remove(end.getKey());
                // remove last node
                delete(end);
                if(this.callback != null){
                    this.callback.call(removedKey,removedValue);
                }
            }
            setHead(newNode);

            cache.put(key, newNode);
        }
    }

}