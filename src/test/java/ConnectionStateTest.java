import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ConnectionAttrs;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.ProtocolState;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConnectionStateTest {

    @Test
    void newConnectionStartsInHandshaking() {
        EmbeddedChannel ch = new EmbeddedChannel(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel c) {
                ConnectionAttrs.setState(c, ProtocolState.HANDSHAKE);
            }
        });

        assertEquals(ProtocolState.HANDSHAKE, ConnectionAttrs.getState(ch));
    }

    @Test
    void connectionsDoNotShareState() {
        EmbeddedChannel a = new EmbeddedChannel();
        EmbeddedChannel b = new EmbeddedChannel();

        ConnectionAttrs.setState(a, ProtocolState.HANDSHAKE);
        ConnectionAttrs.setState(b, ProtocolState.STATUS);
        ConnectionAttrs.setState(a, ProtocolState.LOGIN);

        assertEquals(ProtocolState.LOGIN, ConnectionAttrs.getState(a));
        assertEquals(ProtocolState.STATUS, ConnectionAttrs.getState(b));
    }
}