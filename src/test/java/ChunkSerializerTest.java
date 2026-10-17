import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.packet.ChunkData;
import com.emreekiziltoprak.blockworld_server.application.ChunkSerializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChunkSerializerTest {

    @Test
    void flatChunkHasOneSectionAndExpectedSize() {
        ChunkData c = ChunkSerializer.flatChunk(1, -2);
        assertEquals(1, c.x());
        assertEquals(-2, c.z());
        assertEquals(0b1, c.primaryBitMask());
        // blocks 8192 + block light 2048 + sky light 2048 + biomes 256
        assertEquals(12544, c.data().length);
    }

    @Test
    void firstBlockIsBedrockLittleEndian() {
        byte[] d = ChunkSerializer.flatChunk(0, 0).data();
        int block = (d[0] & 0xFF) | ((d[1] & 0xFF) << 8);
        assertEquals(7 << 4, block);
    }
}
