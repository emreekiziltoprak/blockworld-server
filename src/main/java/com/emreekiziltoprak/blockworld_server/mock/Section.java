package com.emreekiziltoprak.blockworld_server.mock;

//vertical side of chunk
//each 16 height
public class Section {
    public final short[] blocks = new short[4096]; //16x16x16

    //light types
    public final byte[] blockLight = new byte[2048];
    public final byte[] skyLight = new byte[2048];

    public void setBlock(int x, int y, int z, int id, int meta) {
        //insert block type and metadata as short to relevant block in section
        blocks[x + 16 * z + 256 * y] = (short) ((id << 4) | meta);
    }
}

