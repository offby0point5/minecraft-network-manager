package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import com.github.offby0point5.mc.plugin.hybrid.servermanager.groups.GroupEvents;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.rest.RestServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.nio.file.Paths;
import java.util.logging.Logger;

public final class ServermanagerBungee extends Plugin {
    public static ServermanagerBungee plugin;
    public static ProxyServer proxy;
    public static Logger logger;
    public static PluginConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        proxy = this.getProxy();
        logger = this.getLogger();

        this.getProxy().getPluginManager().registerListener(this, new GroupEvents());
        config = PluginConfiguration.read(Paths.get("servermanager.toml"));
        logger.info("Start REST server");
        RestServer.init(config.getHost().getHostString(), config.getHost().getPort());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        logger.info("Stop REST server");
        RestServer.stop();
    }
}
