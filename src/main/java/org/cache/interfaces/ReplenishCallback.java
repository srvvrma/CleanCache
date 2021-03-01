package org.cache.interfaces;

import java.io.Serializable;
import java.util.Optional;

/**
 * Callback method
 * @param <K>
 * @param <V>
 */
@FunctionalInterface
public interface ReplenishCallback<K,V extends Serializable> {
    Optional<V> call(K key);
}
