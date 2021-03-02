package org.cache.config;

import java.math.RoundingMode;
import java.util.logging.Level;

/**
 *  Common config file for Cache
 */
public class CommonConfig {

    public static final Long DEFAULT_CACHE_OBJECT_TIMEOUT = 60000L; //millis
    public static final Long DEFAULT_CACHE_SIZE = 100L; //Default cache size
    public static final Level LOGGING_LEVEL = Level.INFO; //Default logging level Refer the JAVA logging API
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    public static final int SCALE = 7;
    public static final String DISK_CACHE_PATH = "temp-cache";
}
