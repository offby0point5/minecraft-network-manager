package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
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

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }
}
