/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.state.player;

import io.github.singlerr.semaphore.events.PlayerStateChangeEvent;
import io.github.singlerr.semaphore.registries.CommonRegistries;
import io.github.singlerr.semaphore.utils.EventPool;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class PlayerContextHandler {

    public void register(EventPool eventPool) {
        eventPool.subscribe(PlayerStateChangeEvent.class, PlayerContextHandler::updatePlayerState);
    }

    private void updatePlayerState(PlayerStateChangeEvent event) {
        CommonRegistries.getStatePool().submit(event.getState().getOwner(), event.getState());
    }
}
