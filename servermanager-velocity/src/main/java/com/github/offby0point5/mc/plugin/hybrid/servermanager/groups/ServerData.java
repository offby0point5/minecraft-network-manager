package com.github.offby0point5.mc.plugin.hybrid.servermanager.groups;

import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServerFlags;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServerGroups;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServerPorts;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServermanagerVelocity;
import com.pequla.server.ping.ServerPing;
import com.pequla.server.ping.StatusResponse;

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

    public final String name;
    public final ServerPorts ports;
    public ServerGroups groups = new ServerGroups("none");
    public ServerFlags flags = new ServerFlags();

    static {
        ServermanagerVelocity.proxy.getScheduler().buildTask(ServermanagerVelocity.plugin, () -> {
            // todo add server ping data and refresh it every X seconds
            for (ServerData server : serverNameDataMap.values()) {
                InetSocketAddress address = new InetSocketAddress(server.ports.game);
                try {
                    ServerPing serverPing = new ServerPing(address);
                    StatusResponse response = serverPing.fetchData();
                    // todo use the response data for ServerData
                    ServermanagerVelocity.plugin.logger.info("Server "+address+" online."); // todo remove
                } catch (ConnectException e) {
                    ServermanagerVelocity.plugin.logger.warn("Server "+address+" not reachable."); // todo remove
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).delay(10L, TimeUnit.SECONDS).repeat(5L, TimeUnit.SECONDS).schedule();
    }

    public ServerData(String serverId, ServerPorts ports) {
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
