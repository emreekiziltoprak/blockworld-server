package com.emreekiziltoprak.blockworld_server.adapters.in.netty;

import com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec.McBuf;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ChannelRegistry;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ConnectionAttrs;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ProtocolState;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.handler.HandshakeHandler;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.handler.StatusHandler;
import com.emreekiziltoprak.blockworld_server.application.GameLoop;
import com.emreekiziltoprak.blockworld_server.ports.in.GetServerStatusUseCase;
import com.emreekiziltoprak.blockworld_server.ports.in.JoinGameUseCase;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;


@RequiredArgsConstructor
// Receives VarInt-deframed packets (id + payload). Packet decoding/handshake comes in Stage 2.
public class GameHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final Logger log = LoggerFactory.getLogger(GameHandler.class);
    private final UUID playerId = UUID.randomUUID();
    private final ChannelRegistry registry;

    private final HandshakeHandler handshakeHandler;
    private final StatusHandler statusHandler;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf frame) {
        //packet no
        int id = McBuf.readVarInt(frame);
        ProtocolState state = ConnectionAttrs.getState(ctx.channel());

        switch(state) {
            case HANDSHAKE -> handshakeHandler.handle(ctx, id, frame);
            case STATUS    -> statusHandler.handle(ctx, id, frame);
            default -> ctx.close();
        }

        log.debug("Frame of {} bytes from {}", frame.readableBytes(), playerId);
    }



    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        registry.unregister(playerId);
        log.info("Connection closed. {}", playerId);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("Channel error for {}", playerId, cause);
        ctx.close();
    }
}
