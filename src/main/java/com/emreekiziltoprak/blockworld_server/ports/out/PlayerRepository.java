package com.emreekiziltoprak.blockworld_server.ports.out;

import com.emreekiziltoprak.blockworld_server.domain.Player;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository {
    void save(Player player);
    Optional<Player> findById(UUID id);
    Collection<Player> findAll();
    void delete(UUID id);
}
