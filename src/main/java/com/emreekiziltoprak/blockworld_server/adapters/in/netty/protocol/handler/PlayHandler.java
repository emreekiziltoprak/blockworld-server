package com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.handler;

import com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec.McBuf;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec.Packets;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.StatePacketHandler;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.packet.ChunkData;
import com.emreekiziltoprak.blockworld_server.application.ChunkSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.TimeUnit;

/** Play state: sends the world to a freshly logged-in client and keeps it alive. */
public class PlayHandler implements StatePacketHandler {

    private static final int VIEW_RADIUS = 2; // 5x5 chunks around spawn
    private static final double SPAWN_X = 8.5, SPAWN_Y = 4.0, SPAWN_Z = 8.5; // on top of the grass layer (y=3)

    /** Called once, right after Login Success. */
    public void onEnterPlay(ChannelHandlerContext ctx) {
        sendJoinGame(ctx);
        sendSpawnPosition(ctx);
        sendPositionAndLook(ctx);
        for (int cx = -VIEW_RADIUS; cx <= VIEW_RADIUS; cx++) {
            for (int cz = -VIEW_RADIUS; cz <= VIEW_RADIUS; cz++) {
                sendChunk(ctx, ChunkSerializer.flatChunk(cx, cz));
            }
        }
        ctx.flush();
        startKeepAlive(ctx);
    }

    /** Serverbound Play packets (Keep Alive, movement, ...) are ignored for now. */
    @Override
    public void handle(ChannelHandlerContext ctx, int packetId, ByteBuf body) {
    }

    private void sendJoinGame(ChannelHandlerContext ctx) {
        ByteBuf out = Packets.create(ctx, 0x01);
        out.writeInt(1);          // entity id
        out.writeByte(1);         // gamemode: creative
        out.writeByte(0);         // dimension: overworld
        out.writeByte(0);         // difficulty: peaceful
        out.writeByte(20);        // max players (ignored by client)
        McBuf.writeString(out, "flat");
        out.writeBoolean(false);  // reduced debug info
        ctx.write(out);
    }

    private void sendSpawnPosition(ChannelHandlerContext ctx) {
        //create packet id
        ByteBuf out = Packets.create(ctx, 0x05);
        int x = (int) SPAWN_X, y = (int) SPAWN_Y, z = (int) SPAWN_Z;
        //send x y z as one parameter
        out.writeLong(((long) (x & 0x3FFFFFF) << 38) | ((long) (y & 0xFFF) << 26) | (z & 0x3FFFFFF));
        ctx.write(out);
    }

    private void sendPositionAndLook(ChannelHandlerContext ctx) {
        ByteBuf out = Packets.create(ctx, 0x08);
        out.writeDouble(SPAWN_X);
        out.writeDouble(SPAWN_Y);
        out.writeDouble(SPAWN_Z);
        out.writeFloat(0f);  // yaw
        out.writeFloat(0f);  // pitch
        out.writeByte(0);    // flags: all absolute
        ctx.write(out);
    }

    private void sendChunk(ChannelHandlerContext ctx, ChunkData chunk) {
        ByteBuf out = Packets.create(ctx, 0x21);
        out.writeInt(chunk.x());
        out.writeInt(chunk.z());
        out.writeBoolean(chunk.groundUp());
        out.writeShort(chunk.primaryBitMask());
        McBuf.writeVarInt(out, chunk.data().length);
        out.writeBytes(chunk.data());
        ctx.write(out);
    }

    private void startKeepAlive(ChannelHandlerContext ctx) {
        int[] counter = {0};
        var task = ctx.executor().scheduleAtFixedRate(() -> {
            ByteBuf out = Packets.create(ctx, 0x00);
            McBuf.writeVarInt(out, ++counter[0]);
            ctx.writeAndFlush(out);
        }, 10, 10, TimeUnit.SECONDS);
        ctx.channel().closeFuture().addListener(f -> task.cancel(false));
    }
}
