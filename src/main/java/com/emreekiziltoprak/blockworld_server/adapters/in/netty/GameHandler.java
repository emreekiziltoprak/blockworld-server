package com.emreekiziltoprak.blockworld_server.adapters.in.netty;

import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ChannelRegistry;
import com.emreekiziltoprak.blockworld_server.application.GameLoop;
import com.emreekiziltoprak.blockworld_server.ports.in.JoinGameUseCase;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

// Receives VarInt-deframed packets (id + payload). Packet decoding/handshake comes in Stage 2.
public class GameHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final Logger log = LoggerFactory.getLogger(GameHandler.class);
    private final UUID playerId = UUID.randomUUID();
    private final ChannelRegistry registry;
    private final GameLoop gameLoop;
    private final JoinGameUseCase joinGame;
    private final JsonMapper mapper;

    public GameHandler(ChannelRegistry registry, GameLoop gameLoop, JoinGameUseCase joinGameUseCase, JsonMapper mapper) {
        this.registry = registry;
        this.gameLoop = gameLoop;
        this.joinGame = joinGameUseCase;
        this.mapper = mapper;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf frame) {
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
