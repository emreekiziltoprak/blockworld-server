package com.emreekiziltoprak.blockworld_server.config;

import com.emreekiziltoprak.blockworld_server.adapters.in.netty.NettyServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    NettyServer nettyServer(@Value("${blockworld.netty.port:9090}") int port){
        return new NettyServer(port);
    }
}
