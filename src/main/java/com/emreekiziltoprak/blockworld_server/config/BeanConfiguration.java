package com.emreekiziltoprak.blockworld_server.config;

import com.emreekiziltoprak.blockworld_server.adapters.in.netty.NettyServer;
import com.emreekiziltoprak.blockworld_server.adapters.in.scheduling.GameLoopScheduler;
import com.emreekiziltoprak.blockworld_server.application.GameLoop;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    NettyServer nettyServer(@Value("${blockworld.netty.port:9090}") int port){
        return new NettyServer(port);
    }

    @Bean
    GameLoop gameLoop() {
        return new GameLoop();
    }

    @Bean
    GameLoopScheduler gameLoopScheduler(GameLoop gameLoop, @Value("${blockworld.tick-millis:50}") long tickMillis) {
        return new GameLoopScheduler(gameLoop, tickMillis);
    }
}
