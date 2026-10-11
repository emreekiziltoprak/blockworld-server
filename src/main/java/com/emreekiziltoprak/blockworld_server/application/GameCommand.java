package com.emreekiziltoprak.blockworld_server.application;

@FunctionalInterface
public interface GameCommand {
    void execute();
}