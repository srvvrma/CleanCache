package org.cache.core;


import org.cache.config.CommonConfig;
import org.cache.config.CommonMessage;
import org.cache.factory.CacheFactory;
import org.cache.interfaces.ICleanCache;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;
import java.util.logging.Logger;


/**
 * Unit test for Basic Cache App.
 */
public class AppTest {

    private final static Logger LOGGER;

    private int count = 0;

    static {
        LOGGER = Logger.getLogger(BasicCleanCache.class.getName());
        LOGGER.setLevel(CommonConfig.LOGGING_LEVEL);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * Memory Threshold Size Test
     */
    @Test
    public void MemoryThresholdSizeTest() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(CommonMessage.MEMORY_THRESHOLD_VALUE_IS_GREATER_THAN_TOTAL_CACHE_CAPACITY);
        CacheFactory<String,String> cacheFactory = new CacheFactory<>();
        ICleanCache<String,String> cleanCache = cacheFactory.basicCleanCache().setCacheTimeout(100000L).setMemoryThresholdSize(10000L).setCapacity(100L).build();

    }

    /**
     * Memory Threshold Negative Validation Test
     */
    @Test
    public void MemoryThresholdNegativeValidationTest() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(CommonMessage.MEMORY_THRESHOLD_VALUE_CAN_NOT_BE_NEGATIVE);
        CacheFactory<String,String> cacheFactory = new CacheFactory<>();
        ICleanCache<String,String> cleanCache = cacheFactory.basicCleanCache().setCacheTimeout(100000L).setMemoryThresholdSize(-1L).setCapacity(100L).build();
    }

    /**
     * Capacity Negative Validation Test
     */

    @Test
    public void CapacityNegativeValidationTest() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(CommonMessage.CAPACITY_CAN_NOT_BE_LESS_THAN_EQUAL_TO_ZERO);
        CacheFactory<String,String> cacheFactory = new CacheFactory<>();
        ICleanCache<String,String> cleanCache = cacheFactory.basicCleanCache().setCacheTimeout(100000L).setMemoryThresholdSize(100L).setCapacity(-1L).build();
    }

    /**
     * Cache Expiry Test :-)
     */
    @Test
    public void cacheTimeOutTest1() throws InterruptedException {
        count = 0;
        CacheFactory<String,String> cacheFactory = new CacheFactory<>();
        ICleanCache<String,String> cleanCache = cacheFactory.basicCleanCache().setCacheTimeout(1000L).setCapacity(20L).build();
        fillCache(cleanCache,10);
        Thread.sleep(600);
        fillCache(cleanCache,5);
        Thread.sleep(500);
        Assert.assertEquals(5,cleanCache.size());
        LOGGER.severe(String.format("Cache Statistics : %s",cleanCache.getCacheStatistics()));
    }

    /**
     * Cache Expiry Test :-)
     */
    @Test
    public void cacheTimeOutTest2() throws InterruptedException {
        count = 0;
        CacheFactory<String,String> cacheFactory = new CacheFactory<>();
        ICleanCache<String,String> cleanCache = cacheFactory.basicCleanCache().setCacheTimeout(1L).setCapacity(1L).build();
        fillCache(cleanCache,10000);
        Thread.sleep(100);
        Assert.assertEquals(1,cleanCache.size());
        LOGGER.severe(String.format("Cache Statistics : %s",cleanCache.getCacheStatistics()));
    }

    /**
     * LRU Test 1 :-)
     */
    @Test
    public void lruTest1() throws InterruptedException {
        count = 0;
        CacheFactory<String,String> cacheFactory = new CacheFactory<>();
        ICleanCache<String,String> cleanCache = cacheFactory.basicCleanCache().setCacheTimeout(10000000L).setMemoryThresholdSize(100L).setCapacity(100L).build();
        fillCache(cleanCache,100L);
        Optional<String> value = cleanCache.get("0");
        if(!value.isPresent()){
            throw new RuntimeException("Error in test case data.");
        }
        fillCache(cleanCache,1);
        value = cleanCache.get("1");
        Assert.assertFalse(value.isPresent());
        LOGGER.severe(cleanCache.getCacheStatistics().toString());
    }

    /**
     * full disk LRU Test 1 :-)
     */
    @Test
    public void fullDiskLruTest1() {
        count = 0;
        long inputElement = 10L;
        CacheFactory<String,String> cacheFactory = new CacheFactory<>();
        ICleanCache<String,String> cleanCache = cacheFactory.basicCleanCache().setCacheTimeout(100000L).setMemoryThresholdSize(0L).setCapacity(10L).build();
        fillCache(cleanCache,inputElement);
        CacheStatistics cacheStatistics = cleanCache.getCacheStatistics();
        Assert.assertEquals(inputElement,cacheStatistics.getCurrentDiskSize());
        LOGGER.severe(cleanCache.getCacheStatistics().toString());
    }

    /**
     * full disk LRU Test 2 :-)
     */
    @Test
    public void fullDiskLruTest2() {
        count = 0;
        long inputElement = 10L;
        CacheFactory<String,String> cacheFactory = new CacheFactory<>();
        ICleanCache<String,String> cleanCache = cacheFactory.basicCleanCache().setCacheTimeout(100000L).setMemoryThresholdSize(1L).setCapacity(10L).build();
        fillCache(cleanCache,inputElement);
        cleanCache.get("0");
        CacheStatistics cacheStatistics = cleanCache.getCacheStatistics();
        Assert.assertEquals(inputElement-1,cacheStatistics.getCurrentDiskSize());
        LOGGER.severe(cleanCache.getCacheStatistics().toString());
    }

    private void fillCache(ICleanCache<String, String> cacheCache,long size) {
        String key;
        String value;
        for (int i = 0; i<size ; i++){
            key = String.valueOf(count);
            value = String.valueOf(count++);
            cacheCache.put(key,value);
            LOGGER.info(String.format("%s. Key = %s | value = %s",i,key, value));
        }
    }
}
