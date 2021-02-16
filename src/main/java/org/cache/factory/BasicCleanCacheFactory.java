package org.cache.factory;

import org.cache.core.BasicCleanCacheProxy;
import org.cache.core.CacheProxy;
import org.cache.interfaces.ReplenishCallback;

/**
 * Factory for the BasicCleanCache proxy object
 * @param <K>
 * @param <V>
 */
public class BasicCleanCacheFactory<K,V> extends CacheFactory<K,V>{

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
     * Set the callback method
     * @param callback
     * @return
     */
    public BasicCleanCacheFactory<K,V> setReplenishCallback(ReplenishCallback<K,V> callback) {
        super.callback = callback;
        return this;
    }

    // Create an instance for the BasicCleanCache Proxy
    public CacheProxy<K,V> build(){
        return new BasicCleanCacheProxy<>(super.cacheTimeout, super.capacity, super.callback);
    }

}
