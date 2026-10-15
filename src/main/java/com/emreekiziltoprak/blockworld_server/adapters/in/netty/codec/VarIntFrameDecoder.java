package com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

/** Splits the inbound stream into frames prefixed by a VarInt length (Minecraft framing). */
public class VarIntFrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        in.markReaderIndex();

        int length = 0;
        for (int i = 0; i < 5; i++) {
            if (!in.isReadable()) {
                in.resetReaderIndex();
                return; // length VarInt incomplete
            }
            byte b = in.readByte();
            length |= (b & 0x7F) << (7 * i);
            if ((b & 0x80) == 0) {
                if (length < 0) {
                    throw new CorruptedFrameException("negative frame length: " + length);
                }
                if (in.readableBytes() < length) {
                    in.resetReaderIndex(); // body not fully buffered
                    return;
                }
                out.add(in.readRetainedSlice(length));
                return;
            }
        }
        throw new CorruptedFrameException("length VarInt too long");
    }
}
