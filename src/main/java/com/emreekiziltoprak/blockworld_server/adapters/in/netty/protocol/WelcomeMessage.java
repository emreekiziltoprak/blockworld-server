package com.emreekiziltoprak.blockworld_server.adapters.in.netty.protocol;

public record WelcomeMessage(String type, String playerId, double x, double y, double z) {}