/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.config.utils;

import java.util.*;

public class ObservableMap<K, V> implements Map<K, V> {

    private final Map<K, V> source;

    private final List<MapListener<K, V>> listeners;

    public ObservableMap(Map<K, V> source) {
        this.source = source;
        this.listeners = new ArrayList<>();
    }

    public void addListener(MapListener<K, V> listener) {
        this.listeners.add(listener);
    }

    @Override
    public int size() {
        return this.source.size();
    }

    @Override
    public boolean isEmpty() {
        return this.source.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.source.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.source.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return this.source.get(key);
    }

    @Override
    public V put(K key, V value) {
        V result = this.source.put(key, value);
        this.listeners.forEach(listener -> listener.onEntryAdded(key, value));
        return result;
    }

    @Override
    public V remove(Object key) {
        V value = this.source.remove(key);

        this.listeners.forEach(listener -> listener.onEntryRemoved((K) key, value));
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.source.putAll(m);
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            this.listeners.forEach(l -> l.onEntryAdded(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public void clear() {
        for (Entry<K, V> entry : this.source.entrySet()) {
            this.listeners.forEach(l -> l.onEntryRemoved(entry.getKey(), entry.getValue()));
        }
        this.source.clear();
    }

    @Override
    public Set<K> keySet() {
        return this.source.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.source.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.source.entrySet();
    }
}
