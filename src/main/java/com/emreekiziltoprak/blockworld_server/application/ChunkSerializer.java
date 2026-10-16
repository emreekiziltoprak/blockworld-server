package com.emreekiziltoprak.blockworld_server.application;

import com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.packet.ChunkData;
import com.emreekiziltoprak.blockworld_server.mock.Chunk;
import com.emreekiziltoprak.blockworld_server.mock.MockDataCreator;
import com.emreekiziltoprak.blockworld_server.mock.Section;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public final class ChunkSerializer {

    private ChunkSerializer() {}

    //create flat mock chunk
    public static ChunkData flatChunk(int chunkX, int chunkZ) {
        return serialize(MockDataCreator.flatChunk(chunkX, chunkZ));
    }

    /**
     * 1.8 Chunk Data layout (groundUp = true), only for sections that exist:
     * all blocks, then all block light, then all sky light, then 256 biome bytes.
     */
    public static ChunkData serialize(Chunk chunk) {
        ByteBuf buf = Unpooled.buffer();
        int mask = 0;

        for (int i = 0; i < chunk.sections.length; i++) {
            Section s = chunk.sections[i];
            if (s == null) continue;
            mask |= 1 << i;
            //write blocks to buffer as little endian
            for (short block : s.blocks) buf.writeShortLE(block); // n = x + 16*z + 256*y
        }
        for (Section s : chunk.sections) if (s != null) buf.writeBytes(s.blockLight);
        for (Section s : chunk.sections) if (s != null) buf.writeBytes(s.skyLight);
        buf.writeBytes(chunk.biomes);

        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        buf.release();
        return new ChunkData(chunk.x, chunk.z, true, mask, bytes);
    }
}
