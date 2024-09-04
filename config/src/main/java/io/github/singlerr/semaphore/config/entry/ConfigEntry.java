/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.config.entry;

public interface ConfigEntry<T> {

    void set(T val);

    T get();
}
