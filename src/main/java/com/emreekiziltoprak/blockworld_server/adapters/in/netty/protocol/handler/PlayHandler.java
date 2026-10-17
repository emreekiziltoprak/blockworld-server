package com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.handler;

import com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec.McBuf;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec.Packets;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ConnectionAttrs;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.StatePacketHandler;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.packet.ChunkData;
import com.emreekiziltoprak.blockworld_server.application.ChunkSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;
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
        ConnectionAttrs.setLastKeepAlive(ctx.channel(), 0);
        ConnectionAttrs.setLastReply(ctx.channel(), System.currentTimeMillis());
        startKeepAlive(ctx);
    }

    /** Serverbound Play packets (Keep Alive, movement, ...) are ignored for now. */
    @Override
    public void handle(ChannelHandlerContext ctx, int packetId, ByteBuf body) {
        Integer lastKeepAlive = ConnectionAttrs.getLastKeepAlive(ctx.channel());
        if(packetId == 0x00){

            int serverKeepAlive = McBuf.readVarInt(body);
            if(lastKeepAlive != null && serverKeepAlive == lastKeepAlive) {
                ConnectionAttrs.setLastReply(ctx.channel(), System.currentTimeMillis());
            }
        }

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
            Long lastKeepTime = ConnectionAttrs.getLastReply(ctx.channel());
            int id = ++counter[0];
            if(lastKeepTime != null && System.currentTimeMillis() - lastKeepTime > 30_000) {
                ctx.close();
                return;
            }
            ByteBuf out = Packets.create(ctx, 0x00);
            McBuf.writeVarInt(out,id);
            ConnectionAttrs.setLastKeepAlive(ctx.channel(), id);
            ctx.writeAndFlush(out);
        }, 10, 10, TimeUnit.SECONDS);
        ctx.channel().closeFuture().addListener(f -> task.cancel(false));
    }
}
