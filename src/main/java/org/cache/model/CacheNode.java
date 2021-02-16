package org.cache.model;

import java.lang.ref.SoftReference;

/**
 * CaheNode class for storing the cached value in form doubly linked list
 * @param <K> key type of the object
 * @param <V> value type of the object
 */
public class CacheNode<K,V> {
    //Store the key of the object
    private final K key;
    //Value of the object
    private SoftReference<V> value;
    //Link to the previous node
    private CacheNode<K,V> prev;
    //Link to the next node
    private CacheNode<K,V> next;

    public CacheNode(K key, SoftReference<V> value){
        this.key = key;
        this.value = value;
    }

    public void setValue(SoftReference<V> value) {
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public SoftReference<V> getValue() {
        return value;
    }

    public CacheNode<K, V> getPrev() {
        return prev;
    }

    public void setPrev(CacheNode<K, V> prev) {
        this.prev = prev;
    }

    public CacheNode<K, V> getNext() {
        return next;
    }

    public void setNext(CacheNode<K, V> next) {
        this.next = next;
    }
}
