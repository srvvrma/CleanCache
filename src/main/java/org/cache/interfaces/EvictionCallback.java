package org.cache.interfaces;

import java.io.Serializable;

/**
 * the cache entry eviction callback
 * @param <K>
 * @param <V>
 */
@FunctionalInterface
public interface EvictionCallback<K,V extends Serializable> {

    void call(K key, V value);

}
