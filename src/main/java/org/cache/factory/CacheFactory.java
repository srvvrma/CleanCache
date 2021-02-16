package org.cache.factory;

import org.cache.config.CommonConfig;
import org.cache.interfaces.ReplenishCallback;

/**
 * Cache Factory for the cache init
 * @param <K>
 * @param <V>
 */
public class CacheFactory<K,V> {

    protected ReplenishCallback<K,V> callback = null;
    protected Integer capacity = CommonConfig.DEFAULT_CACHE_SIZE;
    protected Long cacheTimeout = CommonConfig.DEFAULT_CACHE_OBJECT_TIMEOUT;

    public BasicCleanCacheFactory<K,V> basicCleanCache(){
        return new BasicCleanCacheFactory<>();
    }

}
