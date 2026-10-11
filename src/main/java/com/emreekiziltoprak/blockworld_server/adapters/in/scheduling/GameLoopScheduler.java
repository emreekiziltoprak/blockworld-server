package com.emreekiziltoprak.blockworld_server.adapters.in.scheduling;

import com.emreekiziltoprak.blockworld_server.application.GameLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameLoopScheduler implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(GameLoopScheduler.class);

    private final GameLoop gameLoop;
    private final long tickMillis;
    private ScheduledExecutorService executor;
    private volatile boolean running;

    public GameLoopScheduler(GameLoop gameLoop, long tickMillis) {
        this.gameLoop = gameLoop;
        this.tickMillis = tickMillis;
    }

    @Override
    public void start() {
        executor = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "game-loop"));
        executor.scheduleAtFixedRate(this::safeTick, 0, tickMillis, TimeUnit.MILLISECONDS);
        running = true;
        log.info("Game loop started, tick = {} ms", tickMillis);
    }

    private void safeTick() {
        try {
            gameLoop.tick();
        } catch (Exception e) {
            log.error("Tick failed", e);
        }
    }

    @Override
    public void stop() {
        executor.shutdownNow();
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}