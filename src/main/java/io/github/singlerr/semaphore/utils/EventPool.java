/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class EventPool {

    private final Map<Class<?>, List<Consumer<?>>> listeners;

    public EventPool() {
        this.listeners = new ConcurrentHashMap<>();
    }

    public <T> void invoke(T message) {
        if (!listeners.containsKey(message.getClass())) {
            log.warn("Ignored invocation of {} because no listeners had subscribed to it", message.getClass());
            return;
        }

        List<Consumer<?>> subscribers = getSubscribers(message.getClass());
        subscribers.forEach(c -> {
            Consumer<T> subscriber = (Consumer<T>) c;
            subscriber.accept(message);
        });
    }

    public <T> void subscribe(Class<T> messageClass, Consumer<T> subscriber) {
        List<Consumer<?>> subscribers = getSubscribers(messageClass);
        subscribers.add(subscriber);
    }

    private <T> List<Consumer<?>> getSubscribers(Class<T> cls) {
        List<Consumer<?>> subscribers;

        if (!listeners.containsKey(cls)) {
            subscribers = new ArrayList<>();
            listeners.put(cls, subscribers);
        } else {
            subscribers = listeners.get(cls);
        }

        return subscribers;
    }
}
