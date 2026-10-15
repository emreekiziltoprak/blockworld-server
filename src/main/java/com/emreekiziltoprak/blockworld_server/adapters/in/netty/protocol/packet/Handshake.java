package com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.packet;

import com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec.McBuf;
import io.netty.buffer.ByteBuf;
/*
* Protocol vers. -> VarInt
* Server Address -> String
* Server Port -> Unsigned Short
* Next State -> VarInt
*
* */
//decode handshake frame
public record Handshake(int protocolVersion, String address, int port, int nextState) {
    public static Handshake read(ByteBuf in) {
        return new Handshake(
                McBuf.readVarInt(in),
                McBuf.readString(in),
                in.readUnsignedShort(),
                McBuf.readVarInt(in));
    }
}

