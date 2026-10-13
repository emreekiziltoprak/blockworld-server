package com.emreekiziltoprak.blockworld_server.ports.in;

import java.util.UUID;

public interface JoinGameUseCase {
    void join(UUID playerId, String username);
}
