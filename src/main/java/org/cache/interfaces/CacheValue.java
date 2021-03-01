package org.cache.interfaces;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface CacheValue<V extends Serializable> {
    V getValue();
    LocalDateTime getCreatedAt();
}