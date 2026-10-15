package com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;

public final class VarInt {

    private VarInt() {}

    /** Reads a VarInt (max 5 bytes). The buffer must already contain the whole value. */
    public static int read(ByteBuf in) {
        int result = 0;
        for (int i = 0; i < 5; i++) {
            byte b = in.readByte();
            result |= (b & 0x7F) << (7 * i);
            if ((b & 0x80) == 0) {
                return result;
            }
        }
        throw new CorruptedFrameException("VarInt too long");
    }

    public static void write(ByteBuf out, int value) {
        while ((value & ~0x7F) != 0) {
            out.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.writeByte(value);
    }

    public static int size(int value) {
        int size = 1;
        while ((value & ~0x7F) != 0) {
            size++;
            value >>>= 7;
        }
        return size;
    }
}
