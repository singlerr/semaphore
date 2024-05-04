/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public abstract class PacketHandler<T extends Packet> implements IMessageHandler<T, Packet> {

    protected abstract Packet handleC2S(MessageContext ctx, T packet);

    protected abstract Packet handleS2C(MessageContext ctx, T packet);

    @Override
    public Packet onMessage(T message, MessageContext ctx) {
        return ctx.side == Side.SERVER ? handleC2S(ctx, message) : handleS2C(ctx, message);
    }
}
