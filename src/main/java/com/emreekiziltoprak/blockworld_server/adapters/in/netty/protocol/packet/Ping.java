package com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.packet;

import io.netty.buffer.ByteBuf;

public record Ping(long payload) {
    public static Ping read(ByteBuf in) {
        return new Ping(in.readLong());
    }
}
