/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network;

import lombok.extern.log4j.Log4j2;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.concurrent.atomic.AtomicInteger;

public final class NetworkManager {

    private final SimpleNetworkWrapper internalChannel;
    private final AtomicInteger packetId;

    public NetworkManager(String channelId) {
        this.internalChannel = NetworkRegistry.INSTANCE.newSimpleChannel(channelId);
        this.packetId = new AtomicInteger();
    }

    public <T extends IMessage> void registerClientboundPacket(
            Class<T> msgClass, ClientboundPacketHandler<T, T> packetHandler) {
        this.internalChannel.registerMessage(packetHandler, msgClass, packetId.getAndIncrement(), Side.CLIENT);
        this.internalChannel.registerMessage(
                new DummyServerboundPacketHandler<>(), msgClass, packetId.getAndIncrement(), Side.SERVER);
    }

    public <T extends IMessage> void registerClientboundPacket(Class<T> msgClass) {
        this.internalChannel.registerMessage(
                new DummyClientboundPacketHandler<>(), msgClass, packetId.getAndIncrement(), Side.CLIENT);
        this.internalChannel.registerMessage(
                new DummyServerboundPacketHandler<>(), msgClass, packetId.getAndIncrement(), Side.SERVER);
    }

    public <T extends IMessage> void registerServerboundPacket(
            Class<T> msgClass, ServerboundPacketHandler<T, T> packetHandler) {
        this.internalChannel.registerMessage(packetHandler, msgClass, packetId.getAndIncrement(), Side.SERVER);
        this.internalChannel.registerMessage(
                new DummyClientboundPacketHandler<>(), msgClass, packetId.getAndIncrement(), Side.CLIENT);
    }

    public <T extends IMessage> void registerServerboundPacket(Class<T> msgClass) {
        this.internalChannel.registerMessage(
                new DummyServerboundPacketHandler<>(), msgClass, packetId.getAndIncrement(), Side.SERVER);
        this.internalChannel.registerMessage(
                new DummyClientboundPacketHandler<>(), msgClass, packetId.getAndIncrement(), Side.CLIENT);
    }

    public <T extends IMessage> void registerPacket(
            Class<T> msgClass,
            ClientboundPacketHandler<T, T> clientPacketHandler,
            ServerboundPacketHandler<T, T> serverPacketHandler) {
        this.internalChannel.registerMessage(clientPacketHandler, msgClass, packetId.getAndIncrement(), Side.CLIENT);
        this.internalChannel.registerMessage(serverPacketHandler, msgClass, packetId.getAndIncrement(), Side.SERVER);
    }

    public void sendToServer(IMessage message) {
        this.internalChannel.sendToServer(message);
    }

    public void sendTo(IMessage message, EntityPlayerMP player) {
        this.internalChannel.sendTo(message, player);
    }

    public void sendToAll(IMessage message) {
        this.internalChannel.sendToAll(message);
    }

    @Log4j2
    private static class DummyClientboundPacketHandler<REQ extends IMessage, REPLY extends IMessage>
            extends ClientboundPacketHandler<REQ, REPLY> {
        @Override
        public REPLY handleClient(REQ packet, ClientboundPacketContext context) {
            log.warn("Packet {} is not clientbound packet but {} received from server", packet.getClass(), getClass());
            return null;
        }
    }

    @Log4j2
    private static class DummyServerboundPacketHandler<REQ extends IMessage, REPLY extends IMessage>
            extends ServerboundPacketHandler<REQ, REPLY> {
        @Override
        public REPLY handleServer(REQ packet, ServerboundPacketContext context) {
            log.warn("Packet {} is not serverbound packet but {} received from server", packet.getClass(), getClass());
            return null;
        }
    }
}
