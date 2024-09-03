/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;

public final class CallTimeoutHandler {

    private static CallTimeoutHandler instance;

    public static CallTimeoutHandler getInstance() {
        if (instance == null) return (instance = new CallTimeoutHandler(60, TimeUnit.SECONDS));

        return instance;
    }

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    private final Map<Key, ScheduledFuture<?>> pendingTimeouts;

    private final long timeout;
    private final TimeUnit timeUnit;

    private CallTimeoutHandler(long timeout, TimeUnit timeUnit) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.pendingTimeouts = new ConcurrentHashMap<>();
    }

    public void startTimeout(Key key, Runnable callback) {
        ScheduledFuture<?> task = this.pendingTimeouts.get(key);
        if (task != null) {
            task.cancel(true);
            this.pendingTimeouts.remove(key);
        }

        task = executorService.schedule(callback, timeout, timeUnit);
        this.pendingTimeouts.put(key, task);
    }

    public void cancelTimeout(Key key) {
        ScheduledFuture<?> task = this.pendingTimeouts.remove(key);
        if (task != null) {
            task.cancel(true);
        }
    }

    public static class Key {

        private final UUID callerId;
        private final UUID calleeId;

        public Key(UUID callerId, UUID calleeId) {
            this.callerId = callerId;
            this.calleeId = calleeId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return Objects.equals(callerId, key.callerId) && Objects.equals(calleeId, key.calleeId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(callerId, calleeId);
        }

        @Override
        public String toString() {
            return "Key{" + "callerId=" + callerId + ", calleeId=" + calleeId + '}';
        }
    }
}
