package com.emreekiziltoprak.blockworld_server.adapters.in.netty;

import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ChannelRegistry;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ClientMessage;
import com.emreekiziltoprak.blockworld_server.application.GameLoop;
import com.emreekiziltoprak.blockworld_server.ports.in.JoinGameUseCase;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ThreadPerChannelEventLoop;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;


public class GameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger log = LoggerFactory.getLogger(GameHandler.class);
    private final UUID playerId = UUID.randomUUID();
    private final ChannelRegistry registry;
    private final GameLoop gameLoop;
    private final JoinGameUseCase joinGame;
    private final JsonMapper mapper;
    private boolean joined;

    public GameHandler(ChannelRegistry registry, GameLoop gameLoop, JoinGameUseCase joinGameUseCase, JsonMapper mapper) {
        this.registry = registry;
        this.gameLoop = gameLoop;
        this.joinGame = joinGameUseCase;
        this.mapper = mapper;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        ClientMessage message;
        try {
            message = mapper.readValue(frame.text(), ClientMessage.class);
        }
        catch(Exception e){
            reject(ctx, "invalid mesage");
            return;
        }

        if("JOIN".equals(message.type())){
            handleJoin(ctx, message);
        }
        else {
            reject(ctx, "unkonwn_type");
        }

    }

    private void handleJoin(ChannelHandlerContext ctx, ClientMessage message) {
        if(joined){
            reject(ctx, "already_joined");
            return;
        }
        String username = message.username();
        if(username == null || username.isBlank()) {
            reject(ctx, "invalid_username");
            return;
        }
        joined = true;
        gameLoop.submit(() -> joinGame.join(playerId,username));
    }

    private void reject(ChannelHandlerContext ctx, String reason) {
        ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"ERROR\",\"reason\":\"" + reason + "\"}"));
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
