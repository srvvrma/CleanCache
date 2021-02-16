package org.cache.core;

import org.cache.interfaces.ReplenishCallback;

/**
 * BasicClean Cache Proxy object
 * @param <K>
 * @param <V>
 */
public class BasicCleanCacheProxy<K,V> extends CacheProxy<K,V> {

    public BasicCleanCacheProxy(Long cacheTimeout, Integer capacity, ReplenishCallback<K, V> callback) {
        super(cacheTimeout,capacity,callback);
    }

    @Override
    public void put(K key, V value) {
        if(cleanCache == null){
            cleanCache = new BasicCleanCache(this.cacheTimeout,this.capacity,this.callback);
        }
        cleanCache.put(key,value);
    }
}
