import com.emreekiziltoprak.blockworld_server.adapters.in.netty.GameHandler;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec.McBuf;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec.VarIntFrameEncoder;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ChannelRegistry;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ConnectionAttrs;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ProtocolState;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.handler.HandshakeHandler;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.handler.LoginHandler;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.handler.StatusHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LoginHandlerTest {

    private static EmbeddedChannel loginChannel() {
        EmbeddedChannel ch = new EmbeddedChannel(new VarIntFrameEncoder(),
                new GameHandler(new ChannelRegistry(), new HandshakeHandler(),
                        new StatusHandler(null, null), new LoginHandler()));
        ConnectionAttrs.setState(ch, ProtocolState.LOGIN);
        return ch;
    }

    private static ByteBuf loginStart(String name) {
        ByteBuf buf = Unpooled.buffer();
        McBuf.writeVarInt(buf, 0x00);
        McBuf.writeString(buf, name);
        return buf;
    }

    @Test
    void sendsLoginSuccessAndEntersPlay() {
        EmbeddedChannel ch = loginChannel();
        ch.writeInbound(loginStart("Steve"));

        ByteBuf out = ch.readOutbound();
        McBuf.readVarInt(out); // frame length
        assertEquals(0x02, McBuf.readVarInt(out));
        String uuid = McBuf.readString(out);
        assertEquals("Steve", McBuf.readString(out));
        assertFalse(out.isReadable());
        assertEquals(LoginHandler.offlineUuid("Steve").toString(), uuid);
        assertEquals(ProtocolState.PLAY, ConnectionAttrs.getState(ch));
        out.release();
    }

    @Test
    void offlineUuidIsNameBasedV3() {
        UUID id = LoginHandler.offlineUuid("Notch");
        assertEquals(3, id.version());
        assertEquals(id, LoginHandler.offlineUuid("Notch"));
        assertNotEquals(id, LoginHandler.offlineUuid("notch"));
    }

    @Test
    void closesOnInvalidUsername() {
        EmbeddedChannel ch = loginChannel();
        ch.writeInbound(loginStart("ThisNameIsWayTooLongForMinecraft"));
        assertFalse(ch.isOpen());
        assertNull(ch.readOutbound());
    }
}
