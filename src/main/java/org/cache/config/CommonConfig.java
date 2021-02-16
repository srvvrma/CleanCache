package org.cache.config;

import java.util.logging.Level;

/**
 *  Common config file for Cache
 */
public class CommonConfig {

    public static final Long DEFAULT_CACHE_OBJECT_TIMEOUT = 60000L; //millis
    public static final int DEFAULT_CACHE_SIZE = 100; //Default cache size
    public static final Level LOGGING_LEVEL = Level.INFO; //Default logging level Refer the JAVA logging API
}
