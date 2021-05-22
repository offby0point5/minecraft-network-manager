package com.github.offby0point5.mc.plugin.hybrid.servermanager;

public class ServerPorts {
    public final int game;
    public final Integer query;
    public final Integer rcon;

    public ServerPorts(int game, Integer query, Integer rcon) {
        this.game = game;
        this.query = query;
        this.rcon = rcon;
    }
}
