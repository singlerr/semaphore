/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.list;

import de.maxhenkel.voicechat.gui.widgets.ListScreenListBase;
import io.github.singlerr.semaphore.gui.PhoneScreenLegacy;
import io.github.singlerr.semaphore.regisries.CommonRegistries;
import io.github.singlerr.semaphore.state.State;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AddressList extends ListScreenListBase<AddressListEntry> {

    private final PhoneScreenLegacy parent;

    private final int start;

    public AddressList(PhoneScreenLegacy parent, int start, int width, int height, int top, int size) {
        super(width, height, top, size);
        this.parent = parent;
        this.start = start;
        this.left = start;
        updateList();
    }

    @Override
    public int getListWidth() {
        return width;
    }

    @Override
    protected int getScrollBarX() {
        return width + start;
    }

    public void updateList() {
        Set<Map.Entry<UUID, State<?>>> states = CommonRegistries.getStatePool().getStates().stream()
                .filter(e -> e.getValue() instanceof PlayerContext
                        && !e.getKey().equals(mc.getSession().getProfile().getId()))
                .collect(Collectors.toSet());
        for (Map.Entry<UUID, State<?>> state : states) {
            PlayerContext ctx = (PlayerContext) state.getValue();
            addEntry(new AddressListEntry(parent, start, state.getKey(), ctx));
        }
    }
}
