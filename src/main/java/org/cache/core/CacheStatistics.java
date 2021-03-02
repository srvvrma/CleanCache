package org.cache.core;

import java.math.BigDecimal;
import java.util.UUID;

public final class CacheStatistics {

    private long totalCacheSize;

    private long memorySize;

    private long currentDiskSize;

    private long totalAccessCount;

    private BigDecimal hitRatio;

    private BigDecimal missRatio;

    private BigDecimal avgLruOptimizationTimeSpent;

    private BigDecimal avgValueReplenishmentTimeSpent;

    public long getTotalCacheSize() {
        return totalCacheSize;
    }

    public long getMemorySize() {
        return memorySize;
    }

    public long getCurrentDiskSize() {
        return currentDiskSize;
    }

    public long getTotalAccessCount() {
        return totalAccessCount;
    }

    public BigDecimal getHitRatio() {
        return hitRatio;
    }

    public BigDecimal getMissRatio() {
        return missRatio;
    }

    public BigDecimal getAvgLruOptimizationTimeSpent() {
        return avgLruOptimizationTimeSpent;
    }

    public BigDecimal getAvgValueReplenishmentTimeSpent() {
        return avgValueReplenishmentTimeSpent;
    }

    protected CacheStatistics setTotalCacheSize(long totalCacheSize) {
        this.totalCacheSize = totalCacheSize;
        return this;
    }

    protected CacheStatistics setMemorySize(long memorySize) {
        this.memorySize = memorySize;
        return this;
    }

    protected CacheStatistics setCurrentDiskSize(long currentDiskSize) {
        this.currentDiskSize = currentDiskSize;
        return this;
    }

    protected CacheStatistics setTotalAccessCount(long totalAccessCount) {
        this.totalAccessCount = totalAccessCount;
        return this;
    }

    protected CacheStatistics setHitRatio(BigDecimal hitRatio) {
        this.hitRatio = hitRatio;
        return this;
    }

    protected CacheStatistics setMissRatio(BigDecimal missRatio) {
        this.missRatio = missRatio;
        return this;
    }

    protected CacheStatistics setAvgLruOptimizationTimeSpent(BigDecimal avgLruOptimizationTimeSpent) {
        this.avgLruOptimizationTimeSpent = avgLruOptimizationTimeSpent;
        return this;
    }

    protected CacheStatistics setAvgValueReplenishmentTimeSpent(BigDecimal avgValueReplenishmentTimeSpent) {
        this.avgValueReplenishmentTimeSpent = avgValueReplenishmentTimeSpent;
        return this;
    }

    @Override
    public String toString() {
        return "CacheStatistics{" +
                "totalCacheSize=" + totalCacheSize +
                ", memorySize=" + memorySize +
                ", currentDiskSize=" + currentDiskSize +
                ", totalAccessCount=" + totalAccessCount +
                ", hitRatio=" + hitRatio +
                ", missRatio=" + missRatio +
                ", avgLruOptimizationTimeSpent=" + avgLruOptimizationTimeSpent +
                ", avgValueReplenishmentTimeSpent=" + avgValueReplenishmentTimeSpent +
                '}';
    }
}
