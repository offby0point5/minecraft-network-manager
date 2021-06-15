package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import java.net.InetSocketAddress;
import java.util.Objects;

public class ServerAddresses {
    public final InetSocketAddress game;
    public final InetSocketAddress query;
    public final InetSocketAddress rcon;

    public ServerAddresses(InetSocketAddress game, InetSocketAddress query, InetSocketAddress rcon) {
        Objects.requireNonNull(game);
        this.game = game;
        this.query = query;
        this.rcon = rcon;
    }
}
