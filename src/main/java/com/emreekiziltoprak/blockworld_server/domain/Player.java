package com.emreekiziltoprak.blockworld_server.domain;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Player {
    private final UUID id;
    private final String username;

    private Position position;


    public Player(UUID id, String username, Position spawnPos) {
        this.id = id;
        this.username = username;
        this.position = spawnPos;
    }

    public void movePlayer(double x, double y, double z) {
        position = position.movePosition(x, y, z);
    }

    public void movePlayerTo(double newX, double newY, double newZ){
        position = position.moveTo(newX, newY, newZ);
    }
}
