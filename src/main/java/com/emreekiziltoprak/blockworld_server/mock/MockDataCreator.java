package com.emreekiziltoprak.blockworld_server.mock;

import java.util.Arrays;

public final class MockDataCreator {
    public static Chunk flatChunk(int chunkX, int chunkZ) {
        Chunk chunk = new Chunk(chunkX, chunkZ);
        Section s = new Section();

        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                s.setBlock(x, 0, z, 7, 0);   // bedrock
                s.setBlock(x, 1, z, 1, 0);   // stone
                s.setBlock(x, 2, z, 1, 0);   // stone
                s.setBlock(x, 3, z, 2, 0);   // grass
            }
        }

        //1111 1111 --> 2 nibble
        Arrays.fill(s.skyLight, (byte) 0xFF);   // her nibble 15: tam aydınlık
        // blockLight zaten 0 (Java diziyi sıfırla başlatır)

        chunk.sections[0] = s;
        Arrays.fill(chunk.biomes, (byte) 1);    // plains
        return chunk;
    }
}
