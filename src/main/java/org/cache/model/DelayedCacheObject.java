package org.cache.model;

import java.lang.ref.SoftReference;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 *  Delayed object for the maintaining the non expired object
 * @param <K>
 * @param <V>
 */
public class DelayedCacheObject<K,V> implements Delayed {

    private final K key;
    private final long expiryTime;

    public DelayedCacheObject(K key, long expiryTime) {
        this.key = key;
        this.expiryTime = expiryTime;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(expiryTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    public K getKey() {
        return key;
    }



    @Override
    public int compareTo(Delayed o) {
        return Long.compare(expiryTime, ((DelayedCacheObject) o).expiryTime);
    }

}
