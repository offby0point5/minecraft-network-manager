package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import org.apache.http.conn.HttpHostConnectException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import unirest.UnirestException;

import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public final class ServermanagerPaper extends JavaPlugin implements Listener {
    public static ServermanagerPaper plugin = null;
    private static Thread serverDataSender;
    private static boolean isPinged = false;
    private static boolean running = true;

    public static PluginConfiguration config;

    public ServermanagerPaper() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Read the config
        config = PluginConfiguration.read(Paths.get("servermanager.toml"));

        // Add the event listeners
        this.getServer().getPluginManager().registerEvents(new ServerMenu(), this);
        this.getServer().getPluginManager().registerEvents(this, this);

        // Start the data sender thread
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

                    this.getLogger().log(Level.OFF, "Proxy did not send ping! Try resending data!");
                    ProxyApi.putServerPorts(config.getName(),
                            new ServerAddresses(
                                    new InetSocketAddress(this.getServer().getIp(), this.getServer().getPort()),
                                    null, null));
                    ProxyApi.putServerGroups(config.getName(),
                            new ServerGroups(config.getMainGroup(), config.getAllGroups()));
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
            ProxyApi.deleteServer(config.getName());
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
