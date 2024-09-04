/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.config.utils;

public interface MapListener<K, V> {

    void onEntryRemoved(K key, V value);

    void onEntryAdded(K key, V value);
}
