package com.emreekiziltoprak.blockworld_server.application;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class GameLoop {

    private final Queue<GameCommand> commands = new ConcurrentLinkedDeque<>();
    private long tickCount;

    public void submit(GameCommand command) {
        commands.add(command);
    }

    public void tick() {
        GameCommand command;
        while((command = commands.poll()) != null){
            command.execute();
        }
        tickCount++;
    }

    public long tickCount() {
        return tickCount;
    }
}
