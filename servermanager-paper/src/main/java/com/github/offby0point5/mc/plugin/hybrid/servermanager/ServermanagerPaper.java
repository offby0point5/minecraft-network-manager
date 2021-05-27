package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServermanagerPaper extends JavaPlugin {
    public static final String serverName = "server-"+ Bukkit.getServer().getPort();

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getServer().getPluginManager().registerEvents(new ServerMenu(), this);
        ProxyApi.putServerPorts(serverName,
                new ServerPorts(this.getServer().getPort(), null, null));
        ProxyApi.putServerGroups(serverName,  // todo add this to a config
                new ServerGroups("lobby", "random", "foyer"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ProxyApi.deleteServer(serverName);
        ProxyApi.shutdown();
    }
}
