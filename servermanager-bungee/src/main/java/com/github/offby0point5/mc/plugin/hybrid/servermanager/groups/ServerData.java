package com.github.offby0point5.mc.plugin.hybrid.servermanager.groups;

import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServerAddresses;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServerFlags;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServerGroups;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServermanagerBungee;
import com.pequla.server.ping.ServerPing;
import com.pequla.server.ping.StatusResponse;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ServerData {
    private static final Map<String, ServerData> serverNameDataMap = new HashMap<>();
    private static final Map<String, Integer> serverPingFailCounters = new HashMap<>();

    public final String name;
    public final ServerAddresses ports;
    public ServerGroups groups = new ServerGroups("none");
    public ServerFlags flags = new ServerFlags();

    static {
        ServermanagerBungee.proxy.getScheduler().schedule(ServermanagerBungee.plugin, () -> {
            for (ServerData server : serverNameDataMap.values()) {
                if (server.name.equals("fallback")) return;  // do not ping fallback server
                InetSocketAddress address = server.ports.game;
                try {
                    ServerPing serverPing = new ServerPing(address);
                    serverPing.fetchData();
                    ServermanagerBungee.logger.info("Server "+address+" online.");
                    serverPingFailCounters.put(server.name, 0);
                } catch (ConnectException e) {
                    ServermanagerBungee.logger.warning("Server "+address+" not reachable.");
                    // remove server if more than 3 times not reachable
                    serverPingFailCounters.putIfAbsent(server.name, 0);
                    serverPingFailCounters.computeIfPresent(server.name, (__, v) -> v+1);
                    if (serverPingFailCounters.get(server.name) > 3) {
                        serverPingFailCounters.remove(server.name);
                        removeServer(server.name);
                        ServerInfo serverInfo = ServermanagerBungee.proxy.constructServerInfo(server.name, address,
                                "", false);
                        ServermanagerBungee.proxy.getServers().put(server.name, serverInfo);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 10L, 5L, TimeUnit.SECONDS);
    }

    public ServerData(String serverId, ServerAddresses ports) {
        this.name = serverId;
        this.ports = ports;
        serverNameDataMap.put(serverId, this);
    }

    public static void setData(String serverId, ServerGroups groups) {
        serverNameDataMap.get(serverId).groups = groups;
    }

    public static void setData(String serverId, ServerFlags flags) {
        serverNameDataMap.get(serverId).flags = flags;
    }

    public static void setData(String serverId, ServerGroups groups, ServerFlags flags) {
        serverNameDataMap.get(serverId).groups = groups;
        serverNameDataMap.get(serverId).flags = flags;
    }

    public static ServerData getServer(String serverName) {
        return serverNameDataMap.get(serverName);
    }

    public static boolean hasServer(String serverName) {
        return serverNameDataMap.containsKey(serverName);
    }

    public static void removeServer(String serverName) {
        serverNameDataMap.remove(serverName);
    }

    public static Set<ServerData> getServersInGroup(String groupName) {
        HashSet<ServerData> serverData = new HashSet<>();
        for (ServerData server : ServerData.serverNameDataMap.values()) {
            if (server.groups.groups.contains(groupName)) {
                serverData.add(server);
            }
        }
        return serverData;
    }

    public static Set<ServerData> getServersWithMainGroup(String groupName) {
        HashSet<ServerData> serverData = new HashSet<>();
        for (ServerData server : ServerData.serverNameDataMap.values()) {
            if (server.groups.main.equals(groupName)) {
                serverData.add(server);
            }
        }
        return serverData;
    }
}
