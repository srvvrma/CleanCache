package org.cache.model;

import org.cache.interfaces.CacheValue;

import java.time.LocalDateTime;
// Not being used in current Impl.
public class SimpleCacheObject<V> implements CacheValue<V> {

    private V value;

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return LocalDateTime.now();
    }
}
