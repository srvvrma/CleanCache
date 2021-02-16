package org.cache.core;

import org.cache.config.CommonConfig;
import org.cache.interfaces.ICleanCache;
import org.cache.interfaces.ReplenishCallback;

import java.util.Optional;

/**
 * Base Cache Proxy abstract class (This will be common for other cache implementation)
 * @param <K>
 * @param <V>
 */
public abstract class CacheProxy<K,V> implements ICleanCache<K,V> {

    protected ReplenishCallback<K,V> callback = null;

    protected ICleanCache<K,V> cleanCache = null;
    protected final int capacity;
    protected final Long cacheTimeout;

    protected CacheProxy(Long cacheTimeout, Integer cacheSize, ReplenishCallback<K,V> callback) {
        this.capacity = cacheSize;
        this.cacheTimeout = cacheTimeout;
        this.callback = callback;
    }


    @Override
    public final void clear() {
        if(cleanCache != null) cleanCache.clear();
    }

    @Override
    public final boolean containsKey(K key) {
        if(cleanCache != null){
            return cleanCache.containsKey(key);
        }else{
            return false;
        }
    }

    @Override
    public final long size() {
        return (cleanCache != null) ? cleanCache.size() : 0;
    }

    @Override
    public final Optional<V> get(K key) {
        return (cleanCache != null) ? cleanCache.get(key) : Optional.empty();
    }

    @Override
    public abstract void put(K key, V value);

    @Override
    public final Optional<V> remove(K key) {
        return (cleanCache != null) ? cleanCache.remove(key) : Optional.empty();
    }
}
