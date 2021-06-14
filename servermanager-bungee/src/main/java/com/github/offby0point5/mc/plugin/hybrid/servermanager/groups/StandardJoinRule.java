package com.github.offby0point5.mc.plugin.hybrid.servermanager.groups;

import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServermanagerBungee;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Random;

public enum StandardJoinRule implements JoinRule {
    MOST(((player, group) -> {
        int mostPlayers = 0;
        ServerInfo preferredServer = null;
        for (ServerData server : ServerData.getServersInGroup(group.name)) {
            ServerInfo serverInfo = ServermanagerBungee.proxy.getServerInfo(server.name);
            if (serverInfo.getPlayers().size() > mostPlayers) {
                mostPlayers = serverInfo.getPlayers().size();
                preferredServer = serverInfo;
            }
        }
        return preferredServer;
    })),
    LEAST(((player, group) -> {
        int leastPlayers = Integer.MAX_VALUE;
        ServerInfo preferredServer = null;
        for (ServerData server : ServerData.getServersInGroup(group.name)) {
            ServerInfo serverInfo = ServermanagerBungee.proxy.getServerInfo(server.name);
            if (serverInfo.getPlayers().size() < leastPlayers) {
                leastPlayers = serverInfo.getPlayers().size();
                preferredServer = serverInfo;
            }
        }
        return preferredServer;
    })),
    RANDOM(((player, group) -> {
        ArrayList<ServerInfo> registeredServers = new ArrayList<>();
        for (ServerData server : ServerData.getServersInGroup(group.name)) {
            ServerInfo serverInfo = ServermanagerBungee.proxy.getServerInfo(server.name);
            registeredServers.add(serverInfo);
        }
        return registeredServers.get(new Random().nextInt(registeredServers.size()));
    })),
    FIRST(((player, group) -> {
        for (ServerData server : ServerData.getServersInGroup(group.name)) {
            return ServermanagerBungee.proxy.getServerInfo(server.name);
        }
        return null;
    })),
    LAST(((player, group) -> {
        ServerInfo lastServer = null;
        for (ServerData server : ServerData.getServersInGroup(group.name)) {
            lastServer = ServermanagerBungee.proxy.getServerInfo(server.name);
        }
        return lastServer;
    }));

    final JoinRule rule;

    StandardJoinRule(JoinRule rule) {
        this.rule = rule;
    }

    @Override
    public ServerInfo getServer(ProxiedPlayer player, ServerGroup group) {
        return rule.getServer(player, group);
    }
}
