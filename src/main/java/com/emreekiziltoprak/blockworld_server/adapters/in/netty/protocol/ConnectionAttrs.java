package com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.UUID;

public class ConnectionAttrs {

    private static final AttributeKey<ProtocolState> STATE =
            AttributeKey.valueOf("protocol.state");

    private static final AttributeKey<UUID> UUID_ATTRIBUTE_KEY =
            AttributeKey.valueOf("player.uuid");

    private ConnectionAttrs(){}

    public static ProtocolState getState(Channel ch){
        return ch.attr(STATE).get();
    };

    public static UUID getUuid(Channel ch) {
        return ch.attr(UUID_ATTRIBUTE_KEY).get();
    }

    public static void setState(Channel ch, ProtocolState state){
        ch.attr(STATE).set(state);
    }

    public static void setUuid(Channel ch, UUID uuid){
        ch.attr(UUID_ATTRIBUTE_KEY).set(uuid);
    }

}
