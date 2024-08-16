/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.FMLServerHandler;

public abstract class ServerboundPacketHandler<REQ extends IMessage, REPLY extends IMessage>
        implements IMessageHandler<REQ, REPLY> {

    public abstract REPLY handleServer(REQ packet, ServerboundPacketContext context);

    @Override
    public REPLY onMessage(REQ iMessage, MessageContext messageContext) {
        return handleServer(
                iMessage, new ServerboundPacketContext(messageContext.getServerHandler(), messageContext.netHandler));
    }

    public static class ServerboundPacketContext {

        private final NetHandlerPlayServer serverHandler;
        private final INetHandler netHandler;

        public ServerboundPacketContext(NetHandlerPlayServer serverHandler, INetHandler netHandler) {
            this.serverHandler = serverHandler;
            this.netHandler = netHandler;
        }

        public NetHandlerPlayServer getServerHandler() {
            return serverHandler;
        }

        public INetHandler getNetHandler() {
            return netHandler;
        }

        public FMLServerHandler getFMLServerHandler() {
            return FMLServerHandler.instance();
        }
    }
}
