package com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;

import java.nio.charset.StandardCharsets;

/**
 * Minecraft wire-type helpers. Fixed-width types map directly to ByteBuf (big-endian):
 * unsigned short = readUnsignedShort/writeShort, long = readLong/writeLong,
 * boolean = readBoolean, byte = readByte, int = readInt, float/double = readFloat/readDouble.
 */
public final class McBuf {

    private static final int MAX_STRING_BYTES = 32767 * 4;

    private McBuf() {}

    public static int readVarInt(ByteBuf in) {
        return VarInt.read(in);
    }

    public static void writeVarInt(ByteBuf out, int value) {
        VarInt.write(out, value);
    }

    /** VarInt byte-length prefix + standard UTF-8. */
    public static String readString(ByteBuf in) {
        int length = VarInt.read(in);
        if (length < 0 || length > MAX_STRING_BYTES) {
            throw new CorruptedFrameException("invalid string length: " + length);
        }
        String s = in.toString(in.readerIndex(), length, StandardCharsets.UTF_8);
        in.skipBytes(length);
        return s;
    }

    public static void writeString(ByteBuf out, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        VarInt.write(out, bytes.length);
        out.writeBytes(bytes);
    }
}
