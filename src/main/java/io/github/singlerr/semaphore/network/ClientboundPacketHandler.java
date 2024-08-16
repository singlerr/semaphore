/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.INetHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class ClientboundPacketHandler<REQ extends IMessage, REPLY extends IMessage>
        implements IMessageHandler<REQ, REPLY> {

    public abstract REPLY handleClient(REQ packet, ClientboundPacketContext context);

    @Override
    public REPLY onMessage(REQ iMessage, MessageContext messageContext) {
        return handleClient(
                iMessage, new ClientboundPacketContext(messageContext.getClientHandler(), messageContext.netHandler));
    }

    public static class ClientboundPacketContext {

        private final NetHandlerPlayClient clientHandler;
        private final INetHandler netHandler;

        public ClientboundPacketContext(NetHandlerPlayClient clientHandler, INetHandler netHandler) {
            this.clientHandler = clientHandler;
            this.netHandler = netHandler;
        }

        public NetHandlerPlayClient getClientHandler() {
            return clientHandler;
        }

        public INetHandler getNetHandler() {
            return netHandler;
        }
    }
}
