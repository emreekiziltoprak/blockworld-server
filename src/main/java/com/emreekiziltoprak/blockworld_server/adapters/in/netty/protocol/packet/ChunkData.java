package com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol.packet;

public record ChunkData(
        int x,
        int z,
        boolean groundUp, //true false
        int primaryBitMask, //which section to be displayed
        byte[] data //chunk data
) {
}
