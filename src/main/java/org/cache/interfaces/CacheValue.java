package org.cache.interfaces;

import java.time.LocalDateTime;

public interface CacheValue<V> {
    V getValue();
    LocalDateTime getCreatedAt();
}