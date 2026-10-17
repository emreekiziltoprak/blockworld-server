package com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.handler;

import com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec.McBuf;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec.Packets;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ConnectionAttrs;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ProtocolState;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.StatePacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** Offline-mode login: no Mojang auth, no encryption, no compression. */
public class LoginHandler implements StatePacketHandler {

    private static final int MAX_USERNAME_LENGTH = 16;

    private final PlayHandler playHandler;

    public LoginHandler(PlayHandler playHandler) {
        this.playHandler = playHandler;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, int packetId, ByteBuf body) {
        if (packetId != 0x00) { // Login Start; Encryption Response (0x01) is online mode only
            ctx.close();
            return;
        }
        // 1.8 Login Start carries only the name, no UUID.
        String username = McBuf.readString(body);
        if (username.isBlank() || username.length() > MAX_USERNAME_LENGTH) {
            ctx.close();
            return;
        }
        sendLoginSuccess(ctx, offlineUuid(username), username);
    }

    public static UUID offlineUuid(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }

    private void sendLoginSuccess(ChannelHandlerContext ctx, UUID uuid, String username) {
        ByteBuf out = Packets.create(ctx, 0x02);
        McBuf.writeString(out, uuid.toString()); // 1.8 sends the UUID as a dashed String
        McBuf.writeString(out, username);
        ctx.writeAndFlush(out);
        ConnectionAttrs.setState(ctx.channel(), ProtocolState.PLAY);
        playHandler.onEnterPlay(ctx);
    }
}
