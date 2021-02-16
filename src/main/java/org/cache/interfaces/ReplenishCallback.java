package org.cache.interfaces;

/**
 * Callback method
 * @param <K>
 * @param <V>
 */
@FunctionalInterface
public interface ReplenishCallback<K,V> {
    void call(K key, V value);
}
