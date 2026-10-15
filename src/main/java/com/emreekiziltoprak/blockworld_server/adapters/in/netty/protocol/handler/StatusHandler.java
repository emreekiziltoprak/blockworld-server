package com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.handler;

import com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec.McBuf;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec.Packets;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.StatePacketHandler;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.packet.Ping;
import com.emreekiziltoprak.blockworld_server.ports.in.GetServerStatusUseCase;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

@RequiredArgsConstructor
public class StatusHandler implements StatePacketHandler {

    private static final String VERSION_NAME = "Blockworld";
    private static final int PROTOCOL = 767;

    private final GetServerStatusUseCase statusUseCase;
    private final JsonMapper mapper;


    @Override
    public void handle(ChannelHandlerContext ctx, int packetId, ByteBuf body) {
        switch (packetId) {
            case 0x00 -> sendStatus(ctx);
            case 0x01 -> sendPong(ctx, Ping.read(body));
            default -> ctx.close();
        }
    }

    private void sendStatus(ChannelHandlerContext ctx) {
        GetServerStatusUseCase.ServerStatus s = statusUseCase.getStatus();
        String json = mapper.writeValueAsString(Map.of(
                "version", Map.of("name", VERSION_NAME, "protocol", PROTOCOL),
                "players", Map.of("max", s.maxPlayers(), "online", s.onlinePlayers()),
                "description", Map.of("text", s.motd())));

        ByteBuf out = Packets.create(ctx, 0x00);
        McBuf.writeString(out, json);
        ctx.writeAndFlush(out);
    }

    private void sendPong(ChannelHandlerContext ctx, Ping ping){
        ByteBuf out = Packets.create(ctx, 0x01);
        out.writeLong(ping.payload());
        ctx.writeAndFlush(out);
    }
}
