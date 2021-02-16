package org.cache.core;


import org.cache.config.LogFormatter;
import org.cache.core.BasicCleanCache;
import org.cache.factory.CacheFactory;
import org.cache.interfaces.ICleanCache;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;

import static org.cache.config.CommonConfig.LOGGING_LEVEL;

/**
 * Unit test for Basic Cache App.
 */
public class AppTest {

    private final static Logger LOGGER;

    static {
        LOGGER = Logger.getLogger(BasicCleanCache.class.getName());
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();

        Formatter formatter = new LogFormatter();
        handler.setFormatter(formatter);

        LOGGER.addHandler(handler);
        LOGGER.setLevel(LOGGING_LEVEL);
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    public void cacheTest1() throws InterruptedException {
        CacheFactory<String,String> cacheFactory = new CacheFactory<>();
        ICleanCache<String,String> cacheCache = cacheFactory.basicCleanCache().setCacheTimeout(1000L).setCapacity(10).build();

        FillCache(cacheCache,10);
        Thread.sleep(500);
        FillCache(cacheCache,5);
        Thread.sleep(500);
        Assert.assertEquals(cacheCache.size(),5);
        LOGGER.severe(String.format("Cache Size : %s",cacheCache.size()));
    }

    private void FillCache(ICleanCache<String, String> cacheCache,int size) {
        String key;
        String value;
        for (int i = 0; i<size ; i++){
            key = givenRandomAlphabeticString();
            value = givenRandomAlphabeticString();
            cacheCache.put(key,value);
            LOGGER.info(String.format("%s. Key = %s | value = %s",i,key, value));
        }
    }


    private String givenRandomAlphabeticString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }
}
