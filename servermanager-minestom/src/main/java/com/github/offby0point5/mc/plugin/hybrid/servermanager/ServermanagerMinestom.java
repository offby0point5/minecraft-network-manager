package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.extensions.Extension;
import net.minestom.server.ping.ServerListPingType;
import unirest.UnirestException;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class ServermanagerMinestom extends Extension {
    private DataSender dataSender;

    private String name = UUID.randomUUID().toString();  // todo read from config
    private String mainGroup = "lobby";  // todo read from config
    private Set<String> allGroups = Collections.emptySet();  // todo read from config

    private EventNode<Event> rootEventNode = this.getEventNode();

    @Override
    public void initialize() {
        ILogger iLogger = new ILogger() {
            @Override
            public void debug(String string) {
                MinecraftServer.LOGGER.debug(string);
            }

            @Override
            public void info(String string) {
                MinecraftServer.LOGGER.info(string);
            }

            @Override
            public void warning(String string) {
                MinecraftServer.LOGGER.warn(string);
            }

            @Override
            public void error(String string) {
                MinecraftServer.LOGGER.error(string);
            }

            @Override
            public void error(String string, Throwable e) {
                MinecraftServer.LOGGER.error(string, e);
            }
        };

        IDataProvider iDataProvider = new IDataProvider() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getMainGroup() {
                return mainGroup;
            }

            @Override
            public Set<String> getAllGroups() {
                return allGroups;
            }

            @Override
            public InetSocketAddress getGameAddress() {
                return MinecraftServer.getNettyServer().getServerChannel().localAddress();
            }
        };

        dataSender = new DataSender(iLogger, iDataProvider);
        dataSender.start();
        MinecraftServer.LOGGER.info("Servermanager loaded");

        rootEventNode.addListener(ServerListPingEvent.class, serverListPingEvent -> {
            if (serverListPingEvent.getPingType().equals(ServerListPingType.OPEN_TO_LAN)) return;
            dataSender.gotPinged();
        });
    }

    @Override
    public void terminate() {
        try {
            dataSender.stop();
            ProxyApi.deleteServer(name);
            ProxyApi.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnirestException ignore) { }
        MinecraftServer.LOGGER.info("Servermanager unloaded");
    }
}
