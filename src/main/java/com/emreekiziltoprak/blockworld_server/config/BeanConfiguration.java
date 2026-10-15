package com.emreekiziltoprak.blockworld_server.config;

import com.emreekiziltoprak.blockworld_server.adapters.in.netty.GameChannelInitializer;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.NettyServer;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ChannelRegistry;
import com.emreekiziltoprak.blockworld_server.adapters.in.scheduling.GameLoopScheduler;
import com.emreekiziltoprak.blockworld_server.adapters.out.notification.NettyPlayerNotifier;
import com.emreekiziltoprak.blockworld_server.adapters.out.persistence.InMemoryPlayerRepository;
import com.emreekiziltoprak.blockworld_server.application.GameLoop;
import com.emreekiziltoprak.blockworld_server.application.JoinGameService;
import com.emreekiziltoprak.blockworld_server.domain.World;
import com.emreekiziltoprak.blockworld_server.ports.in.JoinGameUseCase;
import com.emreekiziltoprak.blockworld_server.ports.out.PlayerNotifierPort;
import com.emreekiziltoprak.blockworld_server.ports.out.PlayerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

@Configuration
public class BeanConfiguration {

    private static final UUID MAIN_WORLD_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Bean
    World world() {
        return new World(-64, 320, MAIN_WORLD_ID);
    }

    @Bean
    GameLoop gameLoop() {
        return new GameLoop();
    }

    @Bean
    ChannelRegistry channelRegistry() {
        return new ChannelRegistry();
    }

    @Bean
    PlayerRepository playerRepository() {
        return new InMemoryPlayerRepository();
    }

    @Bean
    PlayerNotifierPort playerNotifier(ChannelRegistry registry, JsonMapper mapper) {
        return new NettyPlayerNotifier(registry, mapper);
    }

    @Bean
    JoinGameUseCase joinGameUseCase(World world, PlayerNotifierPort notifier,
                                    PlayerRepository repository) {
        return new JoinGameService(world, notifier, repository);
    }

    @Bean
    GameLoopScheduler gameLoopScheduler(GameLoop gameLoop,
                                        @Value("${blockworld.tick-millis:50}") long tickMillis) {
        return new GameLoopScheduler(gameLoop, tickMillis);
    }

    @Bean
    GameChannelInitializer gameChannelInitializer(ChannelRegistry registry, GameLoop gameLoop,
                                                  JoinGameUseCase joinGame, JsonMapper mapper) {
        return new GameChannelInitializer(registry, gameLoop, joinGame, mapper);
    }

    @Bean
    NettyServer nettyServer(@Value("${blockworld.netty.port:25565}") int port,
                            GameChannelInitializer initializer) {
        return new NettyServer(port, initializer);
    }
}