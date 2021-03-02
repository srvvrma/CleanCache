package org.cache.factory;

import org.cache.config.CommonMessage;
import org.cache.core.BasicCleanCacheProxy;
import org.cache.core.CacheProxy;
import org.cache.interfaces.EvictionCallback;
import org.cache.interfaces.ReplenishCallback;

import java.io.Serializable;

/**
 * Factory for the BasicCleanCache proxy object
 * @param <K>
 * @param <V>
 */
public class BasicCleanCacheFactory<K,V extends Serializable > extends CacheFactory<K,V>{

    //Package private Don't change
    BasicCleanCacheFactory(){}

    /**
     * set the capacity of the cache
     * @param capacity
     * @return
     */
    public BasicCleanCacheFactory<K,V> setCapacity(Long capacity) {
        super.capacity = capacity;
        if(super.memoryThresholdSize == null) super.memoryThresholdSize = capacity;
        return this;
    }

    /**
     * Set the timeout time for the cached object
     * @param cacheTimeout
     * @return
     */
    public BasicCleanCacheFactory<K,V> setCacheTimeout(Long cacheTimeout) {
        super.cacheTimeout = cacheTimeout;
        return this;
    }

    /**
     * Set the memory Threshold Size time for the cached object
     * @param memoryThresholdSize
     * @return
     */
    public BasicCleanCacheFactory<K,V> setMemoryThresholdSize(Long memoryThresholdSize) {
        super.memoryThresholdSize = memoryThresholdSize;
        return this;
    }

    /**
     * Set the replenish Callback method
     * @param replenishCallback
     * @return
     */
    public BasicCleanCacheFactory<K,V> setReplenishCallback(ReplenishCallback<K,V> replenishCallback) {
        super.replenishCallback = replenishCallback;
        return this;
    }

    /**
     * Set the eviction Callback method
     * @param evictionCallback
     * @return
     */
    public BasicCleanCacheFactory<K,V> setEvictionCallback(EvictionCallback<K,V> evictionCallback) {
        super.evictionCallback = evictionCallback;
        return this;
    }

    // Create an instance for the BasicCleanCache Proxy
    public CacheProxy<K,V> build(){
        validateData();
        return new BasicCleanCacheProxy<>(super.cacheTimeout, super.capacity,super.memoryThresholdSize, super.replenishCallback,this.evictionCallback);
    }

    private void validateData() {
        if(this.memoryThresholdSize < 0) throw new RuntimeException(CommonMessage.MEMORY_THRESHOLD_VALUE_CAN_NOT_BE_NEGATIVE);
        if(this.capacity <= 0) throw new RuntimeException(CommonMessage.CAPACITY_CAN_NOT_BE_LESS_THAN_EQUAL_TO_ZERO);
        if(this.memoryThresholdSize > this.capacity) throw new RuntimeException(CommonMessage.MEMORY_THRESHOLD_VALUE_IS_GREATER_THAN_TOTAL_CACHE_CAPACITY);
    }


}
