/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.callee.packet;

import io.github.singlerr.semaphore.interactors.callee.controller.data.CallResponse;
import io.github.singlerr.semaphore.network.utils.SerializationUtils;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class PacketCallResponse implements IMessage {

    private UUID callerId;
    private UUID calleeId;
    private CallResponse.Response response;

    public PacketCallResponse() {}

    public PacketCallResponse(UUID callerId, UUID calleeId, CallResponse.Response response) {
        this.callerId = callerId;
        this.calleeId = calleeId;
        this.response = response;
    }

    public UUID getCallerId() {
        return callerId;
    }

    public UUID getCalleeId() {
        return calleeId;
    }

    public CallResponse.Response getResponse() {
        return response;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.callerId = SerializationUtils.readUUID(byteBuf);
        this.calleeId = SerializationUtils.readUUID(byteBuf);
        this.response = SerializationUtils.readEnum(CallResponse.Response.class, byteBuf);
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        SerializationUtils.writeUUID(byteBuf, this.callerId);
        SerializationUtils.writeUUID(byteBuf, this.calleeId);
        SerializationUtils.writeEnum(this.response, byteBuf);
    }
}
