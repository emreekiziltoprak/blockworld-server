package com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface StatePacketHandler {

    /**
     * @param packetId id VarInt, already read by the dispatcher
     * @param body     the rest of the frame; reader index is right after the id
     */
    void handle(ChannelHandlerContext ctx, int packetId, ByteBuf body);
}