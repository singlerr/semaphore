/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.sounds;

public enum SoundResource {
    CALL_ACCEPT("semaphore:sound_call_accept", 1),
    CALL_REJECT("semaphore:sound_call_reject", 1),
    CALL_CLOSE("semaphore:sound_call_close", 1),
    BELL("semaphore:sound_bell", 1),
    REQUESTING_CALL("semaphore:sound_requesting_call", 1),
    RECEIVING_CALL("semaphore:sound_receiving_call", 1),
    INTERACTION("semaphore:sound_interaction", 5),
    VIBRATION("semaphore:sound_vibration", 5),
    TARGET_UNAVAILABLE("semaphore:sound_target_unavailable", 1);

    private String name;
    private int repeatDelay;

    SoundResource(String name, int repeatDelay) {
        this.name = name;
        this.repeatDelay = repeatDelay;
    }

    public String getName() {
        return name;
    }

    public int getRepeatDelay() {
        return repeatDelay;
    }
}
