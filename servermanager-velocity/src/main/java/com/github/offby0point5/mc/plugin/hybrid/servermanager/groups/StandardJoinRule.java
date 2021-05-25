package com.github.offby0point5.mc.plugin.hybrid.servermanager.groups;

import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServermanagerVelocity;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public enum StandardJoinRule implements JoinRule {
    MOST(((player, group) -> {
        int mostPlayers = 0;
        RegisteredServer preferredServer = null;
        for (ServerData server : ServerData.getServersInGroup(group.name)) {
            Optional<RegisteredServer> optionalRegisteredServer = ServermanagerVelocity.proxy.getServer(server.name);
            if (!optionalRegisteredServer.isPresent()) continue;
            RegisteredServer registeredServer = optionalRegisteredServer.get();
            if (registeredServer.getPlayersConnected().size() > mostPlayers) {
                mostPlayers = registeredServer.getPlayersConnected().size();
                preferredServer = registeredServer;
            }
        }
        return preferredServer;
    })),
    LEAST(((player, group) -> {
        int leastPlayers = Integer.MAX_VALUE;
        RegisteredServer preferredServer = null;
        for (ServerData server : ServerData.getServersInGroup(group.name)) {
            Optional<RegisteredServer> optionalRegisteredServer = ServermanagerVelocity.proxy.getServer(server.name);
            if (!optionalRegisteredServer.isPresent()) continue;
            RegisteredServer registeredServer = optionalRegisteredServer.get();
            if (registeredServer.getPlayersConnected().size() < leastPlayers) {
                leastPlayers = registeredServer.getPlayersConnected().size();
                preferredServer = registeredServer;
            }
        }
        return preferredServer;
    })),
    RANDOM(((player, group) -> {
        ArrayList<RegisteredServer> registeredServers = new ArrayList<>();
        for (ServerData server : ServerData.getServersInGroup(group.name)) {
            Optional<RegisteredServer> optionalRegisteredServer = ServermanagerVelocity.proxy.getServer(server.name);
            if (!optionalRegisteredServer.isPresent()) continue;
            RegisteredServer registeredServer = optionalRegisteredServer.get();
            registeredServers.add(registeredServer);
        }
        return registeredServers.get(new Random().nextInt(registeredServers.size()));
    })),
    FIRST(((player, group) -> {
        for (ServerData server : ServerData.getServersInGroup(group.name)) {
            Optional<RegisteredServer> optionalRegisteredServer = ServermanagerVelocity.proxy.getServer(server.name);
            if (!optionalRegisteredServer.isPresent()) continue;
            return optionalRegisteredServer.get();
        }
        return null;
    })),
    LAST(((player, group) -> {
        RegisteredServer lastServer = null;
        for (ServerData server : ServerData.getServersInGroup(group.name)) {
            Optional<RegisteredServer> optionalRegisteredServer = ServermanagerVelocity.proxy.getServer(server.name);
            if (!optionalRegisteredServer.isPresent()) continue;
            lastServer = optionalRegisteredServer.get();
        }
        return lastServer;
    }));

    final JoinRule rule;

    StandardJoinRule(JoinRule rule) {
        this.rule = rule;
    }

    @Override
    public RegisteredServer getServer(Player player, ServerGroup group) {
        return rule.getServer(player, group);
    }
}
