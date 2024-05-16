/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.events;

import io.github.singlerr.semaphore.state.player.PlayerContext;
import java.util.UUID;
import javax.annotation.Nullable;
import lombok.Data;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@Data
public class CallFeedbackEvent {
    private final UUID caller;

    private final UUID callee;

    private final PlayerContext.CallFeedback feedback;

    @Nullable
    private MessageContext messageContext;
}
