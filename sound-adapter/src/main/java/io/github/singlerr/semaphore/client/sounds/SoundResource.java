/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.sounds;

public enum SoundResource {
    CALL_ACCEPT("semaphore:sound_call_accept", 0),
    CALL_REJECT("semaphore:sound_call_reject", 0),
    CALL_CLOSE("semaphore:sound_call_close", 0),
    BELL("semaphore:sound_bell", 0),
    REQUESTING_CALL("semaphore:sound_requesting_call", 0),
    RECEIVING_CALL("semaphore:sound_receiving_call", 0),
    INTERACTION("semaphore:sound_interaction", 1),
    VIBRATION("semaphore:sound_vibration", 0),
    TARGET_UNAVAILABLE("semaphore:sound_target_unavailable", 0);

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
