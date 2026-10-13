package com.emreekiziltoprak.blockworld_server.adapters.in.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

public class NettyServer implements SmartLifecycle {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private final int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private volatile boolean running;

    private GameChannelInitializer gameChannelInitializer;

    public NettyServer(int port, GameChannelInitializer gameChannelInitializer){
        this.port = port;

        this.gameChannelInitializer = gameChannelInitializer;
    }

    @Override
    public void start(){
        bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

        try {
            serverChannel = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(gameChannelInitializer)
                    .bind(port)
                    .sync()
                    .channel();
            running = true;
            log.info("Netty game server listening on port {}", port);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted when starting Netty", e);
        }
    }

    @Override
    public void stop() {
        if(serverChannel != null) {
            serverChannel.close().syncUninterruptibly();
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        running = false;

        log.info("Netty game server stopped");
    }

    @Override
    public boolean isRunning() {
        return running;
    }

}
