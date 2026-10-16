package com.emreekiziltoprak.blockworld_server.mock;

public class Chunk {
    public final int x, z; //16x16

    //each session is 16 height
    public final Section[] sections = new Section[16];
    //biome types
    public final byte[] biomes = new byte[256];

    public Chunk(int x, int z) { this.x = x; this.z = z; }
}
