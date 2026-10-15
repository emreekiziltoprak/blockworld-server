package com.emreekiziltoprak.blockworld_server.ports.in;

public interface GetServerStatusUseCase {
    ServerStatus getStatus();

    record ServerStatus(String motd, int onlinePlayers, int maxPlayers){}
}
