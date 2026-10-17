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
            //shift to left which we want to add the value, and, or for summing
            //to result
            result |= (b & 0x7F) << (7 * i);
            //if the last bit is zero then it is returning signal
            if ((b & 0x80) == 0) {
                return result;
            }
        }
        throw new CorruptedFrameException("VarInt too long");
    }

    /**
     * Writes a 32-bit integer encoded as a VarInt (variable-length integer) to the buffer.
     *
     * @param out the ByteBuf to write to
     * @param value the integer value to encode
     */
    public static void write(ByteBuf out, int value) {
        //if there is value except first 7 bits
        //Bitwise NOT ~
        while ((value & ~0x7F) != 0) {
            //take first 7 bits and add 1 flag for continuation
            out.writeByte((value & 0x7F) | 0x80);
            //shift 7 bits right and delete the read side
            value >>>= 7;
        }
        //if it is smaller than 7 bits then write directly
        out.writeByte(value);
    }

    /**
     * Calculates the number of bytes required to encode a 32-bit integer as a VarInt.
     *
     * @param value the integer value to measure
     * @return the encoded size in bytes (ranging from 1 to 5)
     * count as 7bits
     */
    public static int size(int value) {
        int size = 1;
        while ((value & ~0x7F) != 0) {
            size++;
            value >>>= 7;
        }
        return size;
    }
}
