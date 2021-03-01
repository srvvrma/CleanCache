package org.cache.factory;

import org.cache.core.BasicCleanCacheProxy;
import org.cache.core.CacheProxy;
import org.cache.interfaces.EvictionCallback;
import org.cache.interfaces.ReplenishCallback;

import java.io.Serializable;

/**
 * Factory for the BasicCleanCache proxy object
 * @param <K>
 * @param <V>
 */
public class BasicCleanCacheFactory<K,V extends Serializable > extends CacheFactory<K,V>{

    //Package private Don't change
    BasicCleanCacheFactory(){}

    /**
     * set the capacity of the cache
     * @param capacity
     * @return
     */
    public BasicCleanCacheFactory<K,V> setCapacity(int capacity) {
        super.capacity = capacity;
        return this;
    }

    /**
     * Set the timeout time for the cached object
     * @param cacheTimeout
     * @return
     */
    public BasicCleanCacheFactory<K,V> setCacheTimeout(Long cacheTimeout) {
        super.cacheTimeout = cacheTimeout;
        return this;
    }

    /**
     * Set the replenish Callback method
     * @param replenishCallback
     * @return
     */
    public BasicCleanCacheFactory<K,V> setReplenishCallback(ReplenishCallback<K,V> replenishCallback) {
        super.replenishCallback = replenishCallback;
        return this;
    }

    /**
     * Set the eviction Callback method
     * @param evictionCallback
     * @return
     */
    public BasicCleanCacheFactory<K,V> setEvictionCallback(EvictionCallback<K,V> evictionCallback) {
        super.evictionCallback = evictionCallback;
        return this;
    }

    // Create an instance for the BasicCleanCache Proxy
    public CacheProxy<K,V> build(){
        return new BasicCleanCacheProxy<>(super.cacheTimeout, super.capacity, super.replenishCallback,this.evictionCallback);
    }



}
