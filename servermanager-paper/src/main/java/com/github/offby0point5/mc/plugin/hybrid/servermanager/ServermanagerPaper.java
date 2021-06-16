package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import org.apache.http.conn.HttpHostConnectException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import unirest.UnirestException;

import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.Set;
import java.util.logging.Level;

public final class ServermanagerPaper extends JavaPlugin implements Listener {
    public static ServermanagerPaper plugin = null;
    public static DataSender dataSender;

    public static PluginConfiguration config;

    public ServermanagerPaper() {
        plugin = this;
        ILogger iLogger = new ILogger() {
            @Override
            public void debug(String string) {
                plugin.getLogger().log(Level.OFF, string);
            }

            @Override
            public void info(String string) {
                plugin.getLogger().info(string);
            }

            @Override
            public void warning(String string) {
                plugin.getLogger().warning(string);
            }

            @Override
            public void error(String string) {
                plugin.getLogger().log(Level.SEVERE, string);
            }

            @Override
            public void error(String string, Throwable e) {
                plugin.getLogger().log(Level.SEVERE, string, e);
            }

        };

        IDataProvider iDataProvider = new IDataProvider() {
            @Override
            public String getName() {
                return config.getName();
            }

            @Override
            public String getMainGroup() {
                return config.getMainGroup();
            }

            @Override
            public Set<String> getAllGroups() {
                return config.getAllGroups();
            }

            @Override
            public InetSocketAddress getGameAddress() {
                return new InetSocketAddress(plugin.getServer().getIp(),
                        plugin.getServer().getPort());
            }
        };

        dataSender = new DataSender(iLogger, iDataProvider);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Read the config
        config = PluginConfiguration.read(Paths.get("servermanager.toml"));

        // Add the event listeners
        this.getServer().getPluginManager().registerEvents(new ServerMenu(), this);
        this.getServer().getPluginManager().registerEvents(this, this);
        dataSender.start();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            dataSender.stop();
            ProxyApi.deleteServer(config.getName());
            ProxyApi.shutdown();
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
        dataSender.gotPinged();
    }
}
