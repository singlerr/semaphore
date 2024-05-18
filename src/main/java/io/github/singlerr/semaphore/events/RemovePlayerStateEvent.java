/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.events;

import java.util.UUID;
import lombok.Data;

@Data
public class RemovePlayerStateEvent {

    private final UUID id;
}
