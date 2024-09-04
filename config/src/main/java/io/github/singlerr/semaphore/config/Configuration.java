/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.config;

import io.github.singlerr.semaphore.config.entry.ObservableConfigEntry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

@Getter
public final class Configuration {

    /***
     * Key of unique id(UUID)
     * Value of volume(0.0~1.0)
     */
    private final ObservableConfigEntry<Map<String, Double>> volumes =
            new ObservableConfigEntry<>(new ConcurrentHashMap<>());
}
