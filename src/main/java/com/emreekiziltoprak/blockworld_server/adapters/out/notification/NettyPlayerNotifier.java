package com.emreekiziltoprak.blockworld_server.adapters.out.notification;

import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ChannelRegistry;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.WelcomeMessage;
import com.emreekiziltoprak.blockworld_server.domain.Position;
import com.emreekiziltoprak.blockworld_server.ports.out.PlayerNotifierPort;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

public class NettyPlayerNotifier implements PlayerNotifierPort {

    private static final Logger log = LoggerFactory.getLogger(NettyPlayerNotifier.class);

    private final ChannelRegistry registry;
    private final JsonMapper mapper;

    public NettyPlayerNotifier(ChannelRegistry registry, JsonMapper mapper) {
        this.registry = registry;
        this.mapper = mapper;
    }

    @Override
    public void sendWelcome(UUID playerId, Position spawn) {

        registry.find(playerId).ifPresentOrElse(channel -> {

            String json = mapper.writeValueAsString(new WelcomeMessage(
                    "WELCOME", playerId.toString(), spawn.x(), spawn.y(), spawn.z()));

            channel.writeAndFlush(new TextWebSocketFrame(json));

        }, () -> log.warn("No open channel for player {}", playerId));

    }
}