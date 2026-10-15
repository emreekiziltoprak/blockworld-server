package com.emreekiziltoprak.blockworld_server.application;

import com.emreekiziltoprak.blockworld_server.ports.in.GetServerStatusUseCase;
import com.emreekiziltoprak.blockworld_server.ports.out.PlayerRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerStatusService implements GetServerStatusUseCase {

    private final PlayerRepository playerRepository;
    private final String motd;
    private final int maxPlayers;


    @Override
    public ServerStatus getStatus() {
        return new ServerStatus(motd, playerRepository.findAll().size(), maxPlayers);
    }
}
