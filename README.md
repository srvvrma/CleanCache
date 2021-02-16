# CleanCache
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

This is a simple cache library in java. It is based on ConcurrentHashMap and delayed queue.

## Build and Test
  
  To run the build and run test cases
```
  mvn clean install
```

## Code Example

a) Create a cache with String keys and store String values in cache.
```java 
CacheFactory<String,String> cacheFactory = new CacheFactory<>();
ICleanCache<String,String> cacheCache = cacheFactory.basicCleanCache()
                                    .setCacheTimeout(1000L).setCapacity(10).build();
``` 

b) Storing and accessing values
```java
String key = "ObjectKey";
String value = "CacheOjbject";
cacheCache.put(key,value); // insert a key value pair in cache
```