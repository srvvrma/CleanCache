package org.cache.core;

import org.cache.interfaces.EvictionCallback;
import org.cache.interfaces.ReplenishCallback;

import java.io.Serializable;

/**
 * BasicClean Cache Proxy object
 * @param <K>
 * @param <V>
 */
public class BasicCleanCacheProxy<K,V extends Serializable> extends CacheProxy<K,V> {

    public BasicCleanCacheProxy(Long cacheTimeout, Integer capacity,Integer memoryThresholdSize, ReplenishCallback<K, V> replenishCallback, EvictionCallback<K,V> evictionCallback) {
        super(cacheTimeout,capacity,memoryThresholdSize,replenishCallback,evictionCallback);
    }

    @Override
    public void put(K key, V value) {
        if(cleanCache == null){
            cleanCache = new BasicCleanCache(this.cacheTimeout,this.capacity,this.memoryThresholdSize,this.replenishCallback,this.evictionCallback);
        }
        cleanCache.put(key,value);
    }
}
