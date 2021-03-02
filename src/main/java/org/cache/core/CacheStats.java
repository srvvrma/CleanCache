package org.cache.core;

import java.util.UUID;

public class CacheStats {

    public long diskCachedNodes = 0;
    protected long accessCount = 0;
    protected long replenishCount = 0;
    protected long lruTimeSpent = 0;
    protected long replenishmentTimeSpent = 0;
    protected String uuid = UUID.randomUUID().toString();
    // Max capacity of the cache
    protected final long capacity;

    protected final long memoryThresholdSize;

    public CacheStats(Long capacity, Long memoryThresholdSize) {
        this.capacity = capacity;
        this.memoryThresholdSize = memoryThresholdSize;
    }

    public String getUuid() {
        return uuid;
    }
}
