package com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol;

import io.netty.channel.Channel;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelRegistry {

    private final ConcurrentHashMap<UUID, Channel> channels
            = new ConcurrentHashMap<>();

    public void register(UUID playerId, Channel ch){
        channels.put(playerId, ch);
    }
    public void unregister(UUID playerId) {
        channels.remove(playerId);
    }

    public Optional<Channel> find(UUID playerId){
        return Optional.ofNullable(channels.get(playerId));
    }
}
