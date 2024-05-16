/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.events;

import io.github.singlerr.semaphore.state.player.PlayerContext;
import lombok.Data;
import net.minecraftforge.fml.relauncher.Side;

@Data
public final class PlayerStateChangeEvent {

    private final PlayerContext state;

    private Side side = Side.CLIENT;
}
