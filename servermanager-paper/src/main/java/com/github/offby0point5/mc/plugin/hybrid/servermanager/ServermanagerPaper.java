package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import org.apache.http.conn.HttpHostConnectException;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import unirest.UnirestException;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class ServermanagerPaper extends JavaPlugin implements Listener {
    public static ServermanagerPaper plugin = null;
    public static final String serverName = "server-"+ Bukkit.getServer().getPort();
    public static final String mainGroup;
    public static final Set<String> allGroups = new HashSet<>();

    private static Thread serverDataSender;
    private static boolean isPinged = false;
    private static boolean running = true;

    static {
        mainGroup = "lobby";  // todo add this to a config
        allGroups.add("random");  // todo add this to a config
        allGroups.add("foyer");
    }

    public ServermanagerPaper() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getServer().getPluginManager().registerEvents(new ServerMenu(), this);
        this.getServer().getPluginManager().registerEvents(this, this);
        serverDataSender = new Thread(() -> {
            final long sleepTimeFail = 5;  // Seconds
            final long sleepTimeSuccess = 10;  // Seconds
            long sleepTime = sleepTimeFail;

            while (running) {
                try {
                    isPinged = false;  // reset pinged status
                    TimeUnit.SECONDS.sleep(sleepTime);
                    sleepTime = sleepTimeFail;
                    if (isPinged) continue;

                    this.getLogger().warning("Proxy did not send ping! Try resending data!"); // todo remove
                    ProxyApi.putServerPorts(serverName,
                            new ServerPorts(this.getServer().getPort(), null, null)); // todo get query and rcon ports too
                    ProxyApi.putServerGroups(serverName,
                            new ServerGroups(mainGroup, allGroups));
                    this.getLogger().info("Successfully sent server data.");
                    sleepTime = sleepTimeSuccess;
                } catch (UnirestException e) {
                    if (e.getCause() instanceof HttpHostConnectException) {
                        this.getLogger().warning("Proxy is not reachable!");
                    }
                } catch (InterruptedException ignore) { }
            }
        });
        serverDataSender.start();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            ProxyApi.deleteServer(serverName);
            ProxyApi.shutdown();

            running = false;
            serverDataSender.join();
        } catch (UnirestException e) {
            if (e.getCause() instanceof HttpHostConnectException) {
                this.getLogger().warning("Proxy is not reachable!");
            } else {
                e.printStackTrace();
            }
        } catch (InterruptedException ignore) { }
    }

    @EventHandler
    public static void onServerPing(ServerListPingEvent event) {
        isPinged = true;
    }
}
