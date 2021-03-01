package org.cache.core;

import java.math.BigDecimal;

public final class CacheStatistics {

    private int totalCacheSize;

    private int memorySize;

    private int currentDiskSize;

    private long totalAccessCount;

    private BigDecimal hitRatio;

    private BigDecimal missRatio;

    private BigDecimal avgLruOptimizationTimeSpent;

    private BigDecimal avgValueReplenishmentTimeSpent;

    public int getTotalCacheSize() {
        return totalCacheSize;
    }

    public int getMemorySize() {
        return memorySize;
    }

    public int getCurrentDiskSize() {
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

    protected CacheStatistics setTotalCacheSize(int totalCacheSize) {
        this.totalCacheSize = totalCacheSize;
        return this;
    }

    protected CacheStatistics setMemorySize(int memorySize) {
        this.memorySize = memorySize;
        return this;
    }

    protected CacheStatistics setCurrentDiskSize(int currentDiskSize) {
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
}
