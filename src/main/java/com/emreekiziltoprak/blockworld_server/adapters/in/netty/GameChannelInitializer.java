package com.emreekiziltoprak.blockworld_server.adapters.in.netty;

import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ChannelRegistry;
import com.emreekiziltoprak.blockworld_server.application.GameLoop;
import com.emreekiziltoprak.blockworld_server.ports.in.JoinGameUseCase;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import tools.jackson.databind.json.JsonMapper;

public class GameChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelRegistry channelRegistry;
    private final GameLoop gameLoop;
    private final JoinGameUseCase joinGame;
    private final JsonMapper jsonMapper;

    public GameChannelInitializer(ChannelRegistry registry, GameLoop gameLoop,
                                  JoinGameUseCase joinGame, JsonMapper mapper) {
        this.channelRegistry = registry;
        this.gameLoop = gameLoop;
        this.joinGame = joinGame;
        this.jsonMapper = mapper;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new HttpServerCodec())
                .addLast(new HttpObjectAggregator(65536))
                .addLast(new WebSocketServerProtocolHandler("/ws"))
                .addLast(new GameHandler(channelRegistry, gameLoop, joinGame, jsonMapper));
    }
}
//channel -> attend to game
//one instance