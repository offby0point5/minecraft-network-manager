package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class ServermanagerPaper extends JavaPlugin {
    public static final String serverName = "server-"+ Bukkit.getServer().getPort();
    public static final String mainGroup = "lobby";
    public static final Set<String> allGroups = new HashSet<>();

    static {
        allGroups.add("random");
        allGroups.add("foyer");
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getServer().getPluginManager().registerEvents(new ServerMenu(), this);
        ProxyApi.putServerPorts(serverName,
                new ServerPorts(this.getServer().getPort(), null, null));
        ProxyApi.putServerGroups(serverName,  // todo add this to a config
                new ServerGroups(mainGroup, allGroups));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ProxyApi.deleteServer(serverName);
        ProxyApi.shutdown();
    }
}
