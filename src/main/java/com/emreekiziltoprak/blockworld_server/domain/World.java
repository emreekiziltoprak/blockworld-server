package com.emreekiziltoprak.blockworld_server.domain;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class World {

    private double minY;
    private double maxY;
    private final UUID id;

    public World(double minY,double maxY,UUID id) {


        if(minY>maxY) {
            throw new IllegalArgumentException("minimum yükseklik maksimum yükseklikten büyük olamaz");
        }
        this.id = Objects.requireNonNull(id);
        this.minY = Objects.requireNonNull(minY);
        this.maxY = Objects.requireNonNull(maxY);
    }

    public boolean ifInWorld(Position position) {
        return position.y() <= this.maxY && position.y() >= this.minY;
    }
}
