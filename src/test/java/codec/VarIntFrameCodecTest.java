import com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec.VarIntFrameDecoder;
import com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec.VarIntFrameEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CorruptedFrameException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VarIntFrameCodecTest {

    private static ByteBuf framed(byte[] body) {
        ByteBuf buf = Unpooled.buffer();
        new EmbeddedChannel(new VarIntFrameEncoder()) {{
            writeOutbound(Unpooled.wrappedBuffer(body));
            ByteBuf encoded = readOutbound();
            buf.writeBytes(encoded);
            encoded.release();
        }};
        return buf;
    }

    @Test
    void decodesWholeFrame() {
        EmbeddedChannel ch = new EmbeddedChannel(new VarIntFrameDecoder());
        assertTrue(ch.writeInbound(framed(new byte[]{1, 2, 3})));
        ByteBuf frame = ch.readInbound();
        assertEquals(3, frame.readableBytes());
        frame.release();
    }

    @Test
    void decodesFrameFragmentedByteByByte() {
        byte[] body = new byte[300]; // 2-byte length VarInt
        ByteBuf data = framed(body);
        EmbeddedChannel ch = new EmbeddedChannel(new VarIntFrameDecoder());
        int total = data.readableBytes();
        for (int i = 0; i < total - 1; i++) {
            assertFalse(ch.writeInbound(data.readBytes(1)));
        }
        assertTrue(ch.writeInbound(data.readBytes(1)));
        ByteBuf frame = ch.readInbound();
        assertEquals(300, frame.readableBytes());
        frame.release();
    }

    @Test
    void splitsCoalescedFrames() {
        ByteBuf both = Unpooled.buffer();
        both.writeBytes(framed(new byte[]{1})).writeBytes(framed(new byte[]{2, 3}));
        EmbeddedChannel ch = new EmbeddedChannel(new VarIntFrameDecoder());
        ch.writeInbound(both);
        ByteBuf a = ch.readInbound();
        ByteBuf b = ch.readInbound();
        assertEquals(1, a.readableBytes());
        assertEquals(2, b.readableBytes());
        a.release();
        b.release();
    }

    @Test
    void rejectsOverlongLengthVarInt() {
        EmbeddedChannel ch = new EmbeddedChannel(new VarIntFrameDecoder());
        assertThrows(CorruptedFrameException.class, () -> ch.writeInbound(
                Unpooled.wrappedBuffer(new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, 1})));
    }
}
