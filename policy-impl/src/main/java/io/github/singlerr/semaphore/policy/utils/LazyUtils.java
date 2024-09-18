/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.utils;

import java.util.Map;
import java.util.function.Consumer;

public strictfp class LazyUtils {

    public static <K, V> Map<K, V> compute(Map<K, V> map, Consumer<Map<K, V>> consumer) {
        consumer.accept(map);
        return map;
    }
}
