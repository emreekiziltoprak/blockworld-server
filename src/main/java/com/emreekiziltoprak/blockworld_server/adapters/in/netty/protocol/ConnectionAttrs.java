package com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class ConnectionAttrs {

    private static final AttributeKey<ProtocolState> STATE =
            AttributeKey.valueOf("protocol.state");

    private ConnectionAttrs(){}

    public static ProtocolState getState(Channel ch){
        return ch.attr(STATE).get();
    };

    public static void setState(Channel ch, ProtocolState state){
        ch.attr(STATE).set(state);
    }
}
