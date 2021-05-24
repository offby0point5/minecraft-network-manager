package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import java.util.HashMap;

public class ServerData {
    public static final HashMap<String, ServerData> serverNameDataMap = new HashMap<>();

    public final ServerPorts ports;
    public ServerGroups groups;
    public ServerFlags flags;

    public ServerData(String serverId, ServerPorts ports) {
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
}
