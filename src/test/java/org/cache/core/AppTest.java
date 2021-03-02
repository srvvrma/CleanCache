package org.cache.core;


import org.cache.config.CommonConfig;
import org.cache.config.LogFormatter;
import org.cache.factory.CacheFactory;
import org.cache.interfaces.ICleanCache;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;


/**
 * Unit test for Basic Cache App.
 */
public class AppTest {

    private final static Logger LOGGER;

    private int count = 0;

    static {
        LOGGER = Logger.getLogger(BasicCleanCache.class.getName());
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();

        Formatter formatter = new LogFormatter();
        handler.setFormatter(formatter);

        LOGGER.addHandler(handler);
        LOGGER.setLevel(CommonConfig.LOGGING_LEVEL);
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    public void cacheTimeOutTest() throws InterruptedException {
        count = 0;
        CacheFactory<String,String> cacheFactory = new CacheFactory<>();
        ICleanCache<String,String> cleanCache = cacheFactory.basicCleanCache().setCacheTimeout(1000L).setCapacity(20).build();
        fillCache(cleanCache,10);
        Thread.sleep(500);
        fillCache(cleanCache,5);
        Thread.sleep(500);
        Assert.assertEquals(5,cleanCache.size());
        LOGGER.severe(String.format("Cache Size : %s",cleanCache.size()));
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    public void lruTest() throws InterruptedException {
        count = 0;
        CacheFactory<String,String> cacheFactory = new CacheFactory<>();
        ICleanCache<String,String> cleanCache = cacheFactory.basicCleanCache().setCacheTimeout(100000L).setCapacity(10).build();
        fillCache(cleanCache,10);
        Optional<String> value = cleanCache.get("0");
        if(!value.isPresent()){
            throw new RuntimeException("Error in test case data.");
        }
        fillCache(cleanCache,1);
        value = cleanCache.get("1");
        Assert.assertFalse(value.isPresent());
    }

    private void fillCache(ICleanCache<String, String> cacheCache,int size) {
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
