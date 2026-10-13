package com.emreekiziltoprak.blockworld_server.application;

import com.emreekiziltoprak.blockworld_server.domain.Player;
import com.emreekiziltoprak.blockworld_server.domain.Position;
import com.emreekiziltoprak.blockworld_server.domain.World;
import com.emreekiziltoprak.blockworld_server.ports.in.JoinGameUseCase;
import com.emreekiziltoprak.blockworld_server.ports.out.PlayerNotifierPort;
import com.emreekiziltoprak.blockworld_server.ports.out.PlayerRepository;

import java.util.Objects;
import java.util.UUID;

public class JoinGameService  implements JoinGameUseCase {

    private final World world;
    private final Position spawnPos;
    private final PlayerNotifierPort notifierPort;
    private final PlayerRepository playerRepository;

    public JoinGameService(World world, PlayerNotifierPort notifierPort, PlayerRepository playerRepository) {
    this.world = world;
    this.spawnPos = new Position(world.getId(), 0, 64,0, 40, 40);
    this.notifierPort = notifierPort;
    this.playerRepository = playerRepository;
    }

    @Override
    public void join(UUID playerId, String username) {

        Player player = new Player(
                Objects.requireNonNull(playerId, "playerId null olamaz"),
                Objects.requireNonNull(username, "username null olamaz"),
                spawnPos
        );
        notifierPort.sendWelcome(playerId, spawnPos);
        playerRepository.save(player);
    }
}
