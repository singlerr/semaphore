/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.wrapper;

import io.github.singlerr.semaphore.network.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@Getter
@AllArgsConstructor
public class PacketWrapper<T extends Packet> {

    private final MessageContext context;

    private final T packet;
}
