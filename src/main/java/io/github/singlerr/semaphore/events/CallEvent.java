/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.events;

import io.github.singlerr.semaphore.state.player.PlayerContext;
import java.util.UUID;
import javax.annotation.Nullable;
import lombok.Data;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@Data
public class CallEvent {
    private final UUID callee;

    private final UUID caller;

    private final PlayerContext.CallAction action;

    @Nullable
    private MessageContext messageContext;
}
