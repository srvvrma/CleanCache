package org.cache.factory;

import org.cache.config.CommonConfig;
import org.cache.interfaces.EvictionCallback;
import org.cache.interfaces.ReplenishCallback;

import java.io.Serializable;

/**
 * Cache Factory for the cache init
 * @param <K>
 * @param <V>
 */
public class CacheFactory<K,V extends Serializable> {

    protected ReplenishCallback<K,V> replenishCallback = null;
    protected EvictionCallback<K,V> evictionCallback = null;
    protected Long capacity = CommonConfig.DEFAULT_CACHE_SIZE;
    protected Long cacheTimeout = CommonConfig.DEFAULT_CACHE_OBJECT_TIMEOUT;
    protected Long memoryThresholdSize = null;

    public BasicCleanCacheFactory<K,V> basicCleanCache(){
        return new BasicCleanCacheFactory<>();
    }


}
