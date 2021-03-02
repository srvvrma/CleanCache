package org.cache.model;

import org.cache.config.CommonConfig;
import org.cache.config.CommonUtils;
import org.cache.config.LogFormatter;
import org.cache.core.BasicCleanCache;
import org.cache.core.CacheStats;

import java.io.*;
import java.lang.ref.SoftReference;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;

import static org.cache.config.CommonConfig.DISK_CACHE_PATH;

/**
 * CaheNode class for storing the cached value in form doubly linked list
 * @param <K> key type of the object
 * @param <V> value type of the object
 */
public class CacheNode<K,V extends Serializable> {

    //Store the key of the object
    private final K key;
    //Value of the object
    private SoftReference<V> value;
    //Link to the previous node
    private CacheNode<K,V> prev;

    private PersistentState persistentState = PersistentState.IN_MEMORY;
    //Link to the next node
    private CacheNode<K,V> next;

    private final static Logger LOGGER;
    static {
        LOGGER = Logger.getLogger(BasicCleanCache.class.getName());
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();

        Formatter formatter = new LogFormatter();
        handler.setFormatter(formatter);

        LOGGER.addHandler(handler);
        LOGGER.setLevel(CommonConfig.LOGGING_LEVEL);

        try {
            CommonUtils.deleteDirectory(DISK_CACHE_PATH);
            CommonUtils.createNewDirectory(DISK_CACHE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CacheNode(K key, SoftReference<V> value){
        this.key = key;
        this.value = value;
    }

    public void setValue(SoftReference<V> value) {
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public SoftReference<V> getValue(CacheStats cacheStats) {
        if(this.persistentState == PersistentState.DISK){
            try {
                V cachedValue = this.getCacheNodeFromDisk(cacheStats.getUuid());
                value = new SoftReference<V>(cachedValue);
                this.removeCacheNodeFromDisk(cacheStats);
                cacheStats.diskCachedNodes--;
                this.persistentState = PersistentState.IN_MEMORY;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                value = new SoftReference<V>(null);
            }
        }
        return value;
    }

    public void clearValue(CacheStats cacheStats){
        if(this.persistentState == PersistentState.DISK){
            this.removeCacheNodeFromDisk(cacheStats);
            cacheStats.diskCachedNodes--;
        }else{
            value.clear();
        }
    }

    public CacheNode<K, V> getPrev() {
        return prev;
    }

    public void setPrev(CacheNode<K, V> prev) {
        this.prev = prev;
    }

    public CacheNode<K, V> getNext() {
        return next;
    }

    public void setNext(CacheNode<K, V> next) {
        this.next = next;
    }

    public void flushToDisk(CacheStats cacheStats){
        //No need to persist if node already in disk
        if(this.persistentState == PersistentState.DISK) return;

        try {
            this.flushCacheNodeToDisk(cacheStats.getUuid());
            value = null;
            cacheStats.diskCachedNodes++;
            this.persistentState = PersistentState.DISK;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PersistentState getPersistentState() {
        return persistentState;
    }

    private void flushCacheNodeToDisk(String uniqueDirName) throws IOException {
        CommonUtils.createNewDirectory(DISK_CACHE_PATH + "/" + uniqueDirName); //TODO: Need to fix this
        FileOutputStream f = new FileOutputStream(getFilePath(uniqueDirName));
        ObjectOutputStream o = new ObjectOutputStream(f);

        // Write objects to file
        o.writeObject(value.get());
        o.close();
        f.close();
    }

    private String getFilePath(String uniqueDirName) {
        return CommonConfig.DISK_CACHE_PATH +  "/" + uniqueDirName + "/" + key.hashCode();
    }

    public V getCacheNodeFromDisk(String uniqueDirName) throws IOException, ClassNotFoundException {

        FileInputStream fi = new FileInputStream(getFilePath(uniqueDirName));
        ObjectInputStream oi = new ObjectInputStream(fi);

        // Read objects
        V value = (V) oi.readObject();
        oi.close();
        fi.close();
        return value;

    }

    public void removeCacheNodeFromDisk(CacheStats cacheStats) {

        File file = new File(getFilePath(cacheStats.getUuid()));
        if (file.delete()) {
            LOGGER.info(String.format("cache node with key %s removed from disk",key.toString()));
        } else {
            LOGGER.info(String.format("Cache node with key %s not exist on disk.",key.toString()));
        }
    }
}
