package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import org.bukkit.plugin.java.JavaPlugin;

public final class ServermanagerPaper extends JavaPlugin {
    public final String serverName = "server-"+this.getServer().getPort();

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getServer().getPluginManager().registerEvents(new ServerMenu(), this);
        ProxyApi.putServerPorts(this.serverName,
                new ServerPorts(this.getServer().getPort(), null, null));
        ProxyApi.putServerGroups(this.serverName,  // todo add this to a config
                new ServerGroups("lobby", "random", "foyer"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ProxyApi.deleteServer(this.serverName);
        ProxyApi.shutdown();
    }
}
