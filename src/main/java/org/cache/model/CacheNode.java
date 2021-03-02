package org.cache.model;

import org.cache.config.CommonConfig;
import org.cache.config.LogFormatter;
import org.cache.core.BasicCleanCache;

import java.io.*;
import java.lang.ref.SoftReference;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;

import static org.cache.config.CommonConfig.LOGGING_LEVEL;

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

    public SoftReference<V> getValue() {
        if(this.persistentState == PersistentState.DISK){
            try {
                V cachedValue = this.getCacheNodeFromDisk();
                value = new SoftReference<V>(cachedValue);
                this.removeCacheNodeFromDisk();
                this.persistentState = PersistentState.IN_MEMORY;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                value = new SoftReference<V>(null);
            }
        }
        return value;
    }

    public void clearValue(){
        if(this.persistentState == PersistentState.DISK){
            this.removeCacheNodeFromDisk();
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

    public void flushToDisk(){
        //No need to persist if node already in disk
        if(this.persistentState == PersistentState.DISK) return;

        try {
            this.flushCacheNodeToDisk();
            value = null;
            this.persistentState = PersistentState.DISK;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void flushCacheNodeToDisk() throws IOException {
        File newDir = new File(key.getClass().getCanonicalName());
        if(!newDir.exists()){
            newDir.mkdir();
        }

        FileOutputStream f = new FileOutputStream(new File(key.getClass().getCanonicalName()+"/" + key.hashCode()));
        ObjectOutputStream o = new ObjectOutputStream(f);

        // Write objects to file
        o.writeObject(value.get());
        o.close();
        f.close();
    }

    public V getCacheNodeFromDisk() throws IOException, ClassNotFoundException {

        FileInputStream fi = new FileInputStream(new File(key.getClass().getCanonicalName()+"/"  + key.hashCode()));
        ObjectInputStream oi = new ObjectInputStream(fi);

        // Read objects
        V value = (V) oi.readObject();
        oi.close();
        fi.close();
        return value;

    }

    public void removeCacheNodeFromDisk() {

        File file = new File(key.getClass().getCanonicalName()+"/"  + key.hashCode());
        if (file.delete()) {
            LOGGER.info(String.format("cache node with key %s removed from disk",key.toString()));
        } else {
            LOGGER.info(String.format("Cache node with key %s not exist on disk.",key.toString()));
        }
    }
}
