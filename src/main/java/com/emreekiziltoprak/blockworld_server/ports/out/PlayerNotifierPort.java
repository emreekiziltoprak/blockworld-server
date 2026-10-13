package com.emreekiziltoprak.blockworld_server.ports.out;

import com.emreekiziltoprak.blockworld_server.domain.Position;

import java.util.UUID;

public interface PlayerNotifierPort {
    void sendWelcome(UUID playerId, Position spawn);
}