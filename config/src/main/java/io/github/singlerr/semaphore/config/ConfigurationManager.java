/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ConfigurationManager {

    private static ConfigurationManager instance;
    private final Configuration config;

    private final Registry registry;
    private final List<Consumer<Registry>> registryConsumers;

    private ConfigurationManager() {
        this.config = new Configuration();
        this.registryConsumers = new ArrayList<>();
        this.registry = new Registry(config);
    }

    public static ConfigurationManager getInstance() {
        if (instance == null) return (instance = new ConfigurationManager());
        return instance;
    }

    public void register(Consumer<Registry> registry, boolean deferred) {
        if (deferred) registryConsumers.add(registry);
        else registry.accept(this.registry);
    }

    public void invokeRegistration() {
        registryConsumers.forEach(r -> r.accept(registry));
    }

    @Data
    public static final class Registry {

        private final Configuration config;
    }
}
