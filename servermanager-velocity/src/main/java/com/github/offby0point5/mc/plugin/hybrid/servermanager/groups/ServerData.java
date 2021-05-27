package com.github.offby0point5.mc.plugin.hybrid.servermanager.groups;

import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServerFlags;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServerGroups;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServerPorts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ServerData {
    private static final HashMap<String, ServerData> serverNameDataMap = new HashMap<>();

    public final String name;
    public final ServerPorts ports;
    public ServerGroups groups = new ServerGroups("none");
    public ServerFlags flags = new ServerFlags();
    // todo add server ping data and refresh it every X seconds

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
