package org.cache.config;

import org.cache.core.BasicCleanCache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class CommonUtils {

    private final static Logger LOGGER;
    static {
        LOGGER = Logger.getLogger(BasicCleanCache.class.getName());
        LOGGER.setLevel(CommonConfig.LOGGING_LEVEL);
    }

    public static void deleteDirectory(String dir) throws IOException {

        Path path = Paths.get(dir);

        // read java doc, Files.walk need close the resources.
        // try-with-resources to ensure that the stream's open directories are closed
        if((new File(dir)).exists()) {
            try (Stream<Path> walk = Files.walk(path)) {
                walk.forEach(CommonUtils::deleteDirectoryByPath);
            }
        }

    }

    // extract method to handle exception in lambda
    public static void deleteDirectoryByPath(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            LOGGER.severe(String.format("Unable to delete this path : %s%n%s", path, e));
        }
    }

    public static void createNewDirectory(String diskCachePath) {
        File newDir = new File(diskCachePath);
        if(!newDir.exists()){
            newDir.mkdir();
        }
    }

    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
