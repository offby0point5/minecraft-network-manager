package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import com.github.offby0point5.mc.plugin.hybrid.servermanager.rest.RestServer;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(
        id = "servermanager-velocity",
        name = "Servermanager",
        version = "1.0-SNAPSHOT",
        description = "Automatically create server menu, add new servers to network, check their online state",
        url = "https://github.com/off-by-0point5",
        authors = {"offbyone"}
)
public class ServermanagerVelocity {

    private final Logger logger;
    public static ProxyServer proxy;

    @Inject public ServermanagerVelocity(ProxyServer proxyServer, Logger logger) {
        this.logger = logger;
        proxy = proxyServer;
    }

    @Subscribe public void onProxyInitialization(ProxyInitializeEvent event) {
        this.logger.info("Start REST server");
        RestServer.init(25564);
    }

    @Subscribe public void onShutdown(ProxyShutdownEvent event) {
        this.logger.info("Stop REST server");
        RestServer.stop();
    }
}
