package org.cache.interfaces;

import java.util.Optional;

/**
 * Interface for the Cache Proxy classed
 * @param <K>
 * @param <V>
 */
public interface ICleanCache<K,V> {

    void clear();

    boolean containsKey(K key);

    long size();

    Optional<V> get(K key);

    void put(K key, V value);

    Optional<V> remove(K key);
}
