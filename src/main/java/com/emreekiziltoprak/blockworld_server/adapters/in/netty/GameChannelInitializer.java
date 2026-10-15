package com.emreekiziltoprak.blockworld_server.adapters.in.netty;

import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ChannelRegistry;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ConnectionAttrs;
import com.emreekiziltoprak.blockworld_server.application.GameLoop;
import com.emreekiziltoprak.blockworld_server.ports.in.JoinGameUseCase;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec.VarIntFrameDecoder;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec.VarIntFrameEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import tools.jackson.databind.json.JsonMapper;

import static com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ProtocolState.HANDSHAKE;

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
        ConnectionAttrs.setState(ch, HANDSHAKE);

        ch.pipeline()
                .addLast(new VarIntFrameDecoder())
                .addLast(new VarIntFrameEncoder())
                .addLast(new GameHandler(channelRegistry, gameLoop, joinGame, jsonMapper));
    }
}
//channel -> attend to game
//one instance