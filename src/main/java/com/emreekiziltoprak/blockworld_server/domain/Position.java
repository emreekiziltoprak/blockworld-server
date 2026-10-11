package com.emreekiziltoprak.blockworld_server.domain;

import java.util.Objects;
import java.util.UUID;


public record Position(
        UUID worldId,
        double x,
        double y,
        double z,
        float yaw,
        float pitch
) {
    public Position {

        Objects.requireNonNull(worldId);

        if(!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z) || !Float.isFinite(yaw) || !Float.isFinite(pitch)) {
            throw new IllegalArgumentException("sayılar geçersiz");
        }

        if (pitch < -90.0f || pitch > 90.0f) {
            throw new IllegalArgumentException("Dikey bakış açısı (pitch) -90 ile 90 arasında olmalıdır.");
        }

    }

    public Position movePosition(double dx, double dy, double dz) {
        return new Position(this.worldId, this.x + dx, this.y + dy, this.z + dz, this.yaw, this.pitch);
    }

    public Position moveTo(double newX, double newY, double newZ) {
        return new Position(this.worldId, newX, newY, newZ, this.yaw, this.pitch);
    }

}
