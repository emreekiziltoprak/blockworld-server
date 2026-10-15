package com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.handler;

import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ConnectionAttrs;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ProtocolState;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.StatePacketHandler;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.packet.Handshake;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class HandshakeHandler implements StatePacketHandler {
    @Override
    public void handle(ChannelHandlerContext ctx, int packetId, ByteBuf body) {
        if(packetId != 0x00) {
            ctx.close();
            return;
        }
        Handshake h = Handshake.read(body);
        switch (h.nextState()) {
            case 1 -> ConnectionAttrs.setState(ctx.channel(), ProtocolState.STATUS);
            case 2 -> ConnectionAttrs.setState(ctx.channel(), ProtocolState.LOGIN);
            default -> ctx.close();
        }
    }
}
