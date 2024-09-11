/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.sounds;

public enum SoundResource {
    CALL_ACCEPT("semaphore:sound_call_accept"),
    CALL_REJECT("semaphore:sound_call_reject"),
    CALL_CLOSE("semaphore:sound_call_close"),
    BELL("semaphore:sound_bell"),
    REQUESTING_CALL("semaphore:sound_requesting_call"),
    RECEIVING_CALL("semaphore:sound_receiving_call"),
    INTERACTION("semaphore:sound_interaction"),
    VIBRATION("semaphore:sound_vibration"),
    TARGET_UNAVAILABLE("semaphore:sound_target_unavailable");

    private String name;

    SoundResource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
