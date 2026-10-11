package com.emreekiziltoprak.blockworld_server.adapters.in.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class EchoHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        ctx.channel().writeAndFlush(new TextWebSocketFrame("echo:" + frame.text()));
    }
}
