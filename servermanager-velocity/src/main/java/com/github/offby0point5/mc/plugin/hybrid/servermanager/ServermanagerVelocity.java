package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import com.github.offby0point5.mc.plugin.hybrid.servermanager.groups.*;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.rest.RestServer;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;

import java.util.Collections;

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
        // Create standard groups // todo add those to config
        ServerGroup.getInstance("lobby", StandardJoinRule.LEAST, StandardKickRule.KICK_PROXY,
                new MenuData.Entry("MAGMA_CREAM", 1,
                        MiniMessage.get().serialize(Component.text("Lobby", NamedTextColor.GREEN)),
                        Collections.emptyList(), 10, MenuData.Status.ONLINE));
        ServerGroup.getInstance("random", StandardJoinRule.RANDOM, StandardKickRule.KICK_TO_LOBBY,
                new MenuData.Entry("DRAGON_EGG", 1,
                        MiniMessage.get().serialize(Component.text("Zufälliger Server", NamedTextColor.LIGHT_PURPLE)),
                        Collections.singletonList(MiniMessage.get().serialize(Component.text("Leitet dich auf einen zufälligen Server weiter", NamedTextColor.GRAY))),
                        0, MenuData.Status.ONLINE));
        // todo servers that are added by proxy config -> add them to groups by config
        proxy.getEventManager().register(this, new GroupEvents());
        // Register all servers from config to ServerData
        for (RegisteredServer server : proxy.getAllServers()) {
            new ServerData(server.getServerInfo().getName(),
                    new ServerPorts(server.getServerInfo().getAddress().getPort(), null, null));
        }
        this.logger.info("Start REST server");
        RestServer.init(25564);
    }

    @Subscribe public void onShutdown(ProxyShutdownEvent event) {
        this.logger.info("Stop REST server");
        RestServer.stop();
    }
}
