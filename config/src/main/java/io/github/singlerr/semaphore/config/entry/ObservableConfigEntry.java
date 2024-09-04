/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.config.entry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ObservableConfigEntry<T> implements ConfigEntry<T> {

    private T value;

    private List<Consumer<T>> observers;

    public ObservableConfigEntry(T defaultValue) {
        this.value = defaultValue;
        this.observers = new ArrayList<>();
    }

    public ObservableConfigEntry(T defaultValue, List<Consumer<T>> listeners) {
        this.value = defaultValue;
        this.observers = listeners;
    }

    public void observe(Consumer<T> listener) {
        this.observers.add(listener);
    }

    @Override
    public void set(T val) {
        this.value = val;
        observers.forEach(o -> o.accept(val));
    }

    @Override
    public T get() {
        return value;
    }
}
