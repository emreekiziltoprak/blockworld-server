import com.emreekiziltoprak.blockworld_server.adapters.in.netty.codec.McBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.CorruptedFrameException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class McBufTest {

    private static String hex(int value) {
        ByteBuf b = Unpooled.buffer();
        McBuf.writeVarInt(b, value);
        return ByteBufUtil.hexDump(b);
    }

    @Test
    void varIntKnownEncodings() {
        assertEquals("00", hex(0));
        assertEquals("01", hex(1));
        assertEquals("7f", hex(127));
        assertEquals("8001", hex(128));
        assertEquals("ff01", hex(255));
        assertEquals("ffffffff07", hex(Integer.MAX_VALUE));
        assertEquals("ffffffff0f", hex(-1));
        assertEquals("8080808008", hex(Integer.MIN_VALUE));
    }

    @Test
    void varIntRoundTrip() {
        int[] values = {0, 1, 2, 127, 128, 255, 25565, 2097151, Integer.MAX_VALUE, -1, -128, Integer.MIN_VALUE};
        for (int v : values) {
            ByteBuf b = Unpooled.buffer();
            McBuf.writeVarInt(b, v);
            assertEquals(v, McBuf.readVarInt(b));
            assertFalse(b.isReadable());
        }
    }

    @Test
    void varIntTooLongRejected() {
        ByteBuf b = Unpooled.wrappedBuffer(new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, 0x01});
        assertThrows(CorruptedFrameException.class, () -> McBuf.readVarInt(b));
    }

    @Test
    void stringRoundTrip() {
        String[] values = {"", "hello", "blockworld", "çğış ÖÜ", "日本語", "😀 emoji"};
        for (String s : values) {
            ByteBuf b = Unpooled.buffer();
            McBuf.writeString(b, s);
            assertEquals(s, McBuf.readString(b));
            assertFalse(b.isReadable());
        }
    }

    @Test
    void stringUsesStandardUtf8NotModified() {
        ByteBuf b = Unpooled.buffer();
        McBuf.writeString(b, "\0");
        assertEquals("0100", ByteBufUtil.hexDump(b)); // modified UTF-8 would be C0 80
        ByteBuf emoji = Unpooled.buffer();
        McBuf.writeString(emoji, "😀");
        assertEquals("04f09f9880", ByteBufUtil.hexDump(emoji)); // 4 bytes, not surrogate pair 6
    }

    @Test
    void invalidStringLengthRejected() {
        ByteBuf b = Unpooled.buffer();
        McBuf.writeVarInt(b, -1);
        assertThrows(CorruptedFrameException.class, () -> McBuf.readString(b));
    }

    @Test
    void fixedWidthTypesAreBigEndian() {
        ByteBuf b = Unpooled.buffer();
        b.writeShort(25565).writeLong(0x0102030405060708L);
        assertEquals("63dd0102030405060708", ByteBufUtil.hexDump(b));
        assertEquals(25565, b.readUnsignedShort());
        assertEquals(0x0102030405060708L, b.readLong());
    }
}
