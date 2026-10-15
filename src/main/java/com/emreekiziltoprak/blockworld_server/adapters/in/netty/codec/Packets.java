package com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public final class Packets {
    private Packets() {}

    public static ByteBuf create(ChannelHandlerContext ctx, int id) {
        ByteBuf buf = ctx.alloc().buffer();
        McBuf.writeVarInt(buf, id);
        return buf;
    }
}