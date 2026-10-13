package com.emreekiziltoprak.blockworld_server.adapters.out.persistence;

import com.emreekiziltoprak.blockworld_server.domain.Player;
import com.emreekiziltoprak.blockworld_server.ports.out.PlayerRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPlayerRepository implements PlayerRepository {

    private final ConcurrentHashMap<UUID, Player> map = new ConcurrentHashMap<>();

    @Override
    public void save(Player player) {
        map.put(player.getId(), player);
    }

    @Override
    public Optional<Player> findById(UUID id) {
        Player player = map.get(id);
        if(player != null) {
            return Optional.of(player);
        }
        else {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Player> findAll() {
        return List.copyOf(map.values());
    }

    @Override
    public void delete(UUID id) {
        map.remove(id);
    }
}
