package com.github.offby0point5.mc.plugin.hybrid.servermanager.rest;

import com.github.offby0point5.mc.plugin.hybrid.servermanager.*;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.groups.ServerData;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.groups.ServerGroup;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.*;

public class Resources {  // todo add all other resources  // todo add response bodies to swagger docs
    @OpenApi(
            path = ResourceUrls.SERVERS,
            summary = "Returns all server names registered on this proxy.",
            tags = {"Proxy get info"},
            method = HttpMethod.GET,
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void getServers(Context ctx) {
        Set<String> serverNames = ServermanagerBungee.proxy.getServers().keySet();
        ctx.json(serverNames);
    }

    // ==============================================================================
    // =============== Server data setters ==========================================
    @OpenApi(
            path = ResourceUrls.PORTS,
            summary = "Put port numbers for the server with this id.",
            tags = {"Set server data"},
            method = HttpMethod.PUT,
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = ServerAddresses.class)}),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void putPorts(Context ctx) {  // todo send correct http status codes
        String serverId = ctx.pathParam("id");
        // If server already known, abort
        if (ServerData.hasServer(serverId)) {
            ctx.json("exists");
            return;
        }
        // Try parse ports object
        try {
            ServerAddresses ports = new Gson().fromJson(ctx.body(), ServerAddresses.class);
            if (ports == null) {
                ctx.json("invalid object");
                return;
            }
            // If object is correctly deserialized, register new server
            new ServerData(serverId, ports);
            ServerInfo serverInfo = ServermanagerBungee.proxy.constructServerInfo(
                    serverId, ports.game, "", false);
            ServermanagerBungee.proxy.getServers().put(serverInfo.getName(), serverInfo);
            ctx.json("success");
        } catch (JsonSyntaxException e) {
            ctx.json("invalid object");
        }
    }

    @OpenApi(
            path = ResourceUrls.GROUPS,
            summary = "Put groups for the server with this id.",
            tags = {"Set server data"},
            method = HttpMethod.PUT,
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = ServerGroups.class)}),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void putGroups(Context ctx) {  // todo send correct http status codes
        String serverId = ctx.pathParam("id");
        // If server not known, abort
        if (!ServerData.hasServer(serverId)) {
            ctx.json("not found");
            return;
        }
        // Try parse groups object
        try {
            ServerGroups groups = new Gson().fromJson(ctx.body(), ServerGroups.class);
            if (groups == null) {
                ctx.json("invalid object");
                return;
            }
            // If object is correctly deserialized, set servers groups
            ServerData.setData(serverId, groups);
            ctx.json("success");
        } catch (JsonSyntaxException e) {
            ctx.json("invalid object");
        }
    }

    @OpenApi(
            path = ResourceUrls.FLAGS,
            summary = "Put flags for the server with this id.",
            tags = {"Set server data"},
            method = HttpMethod.PUT,
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = ServerFlags.class)}),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void putFlags(Context ctx) {  // todo send correct http status codes
        String serverId = ctx.pathParam("id");
        // If server not known, abort
        if (!ServerData.hasServer(serverId)) {
            ctx.json("not found");
            return;
        }
        // Try parse flags object
        try {
            ServerFlags flags = new Gson().fromJson(ctx.body(), ServerFlags.class);
            if (flags == null) {
                ctx.json("invalid object");
                return;
            }
            // If object is correctly deserialized, set servers flags
            ServerData.setData(serverId, flags);
            ctx.json("success");
        } catch (JsonSyntaxException e) {
            ctx.json("invalid object");
        }
    }

    @OpenApi(
            path = ResourceUrls.DELETE,
            summary = "Delete the server with this id.",
            tags = {"Set server data"},
            method = HttpMethod.DELETE,
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void deleteServer(Context ctx) {  // todo send correct http status codes
        String serverId = ctx.pathParam("id");
        // If server not known, abort
        if (!ServerData.hasServer(serverId)) {
            ctx.json("not found");
            return;
        }
        // Unregister and delete server
        ServermanagerBungee.proxy.getServers().remove(serverId);
        ServerData.removeServer(serverId);
        ctx.json("success");
    }

    // =================================================================================
    // ============== GET == server menu entries =======================================
    @OpenApi(
            path = ResourceUrls.MENU_MAIN,
            summary = "Returns the data necessary for a inventory main server menu.",
            tags = {"Get switching menu data"},
            method = HttpMethod.GET,
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void getMenuMain(Context ctx) {  // todo send correct http status codes
        Map<String, MenuData.Entry> entryMap = new HashMap<>();
        for (ServerGroup group : ServerGroup.getAllGroups()) {
            if (group.representation == null) {
                ServermanagerBungee.logger.warning("Group " + group.name + " has no representation!");
                continue;
            }
            entryMap.put(group.name, group.representation);
        }
        MenuData menuData = new MenuData(
                new MenuData.Entry(
                        "BIRCH_SAPLING", 1,
                        MiniMessage.get().serialize(Component.text("Übersicht", NamedTextColor.YELLOW)),
                        Collections.singletonList(MiniMessage.get().serialize(Component.text("Hier sind alle Servergruppen aufgelistet.", NamedTextColor.GRAY))),
                        0, MenuData.Status.ONLINE),
                entryMap);
        ctx.json(menuData);
    }

    @OpenApi(
            path = ResourceUrls.MENU_GROUP,
            summary = "Returns the data necessary for a inventory group server menu for group with this ID.",
            tags = {"Get switching menu data"},
            method = HttpMethod.GET,
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void getMenuGroup(Context ctx) {  // todo send correct http status codes
        String groupId = ctx.pathParam("id");
        Map<String, MenuData.Entry> entryMap = new HashMap<>();
        for (ServerData serverData : ServerData.getServersInGroup(groupId)) {
            if (ServerGroup.getGroup(serverData.groups.main).representation == null) {
                ServermanagerBungee.logger.warning("Server " +serverData.groups.main + " has no representation!");
                continue;
            }
            entryMap.put(serverData.name, ServerGroup.getGroup(serverData.groups.main).representation);
        }
        MenuData menuData = new MenuData(
                ServerGroup.getGroup(groupId).representation,
                entryMap);
        ctx.json(menuData);
    }

    // =================================================================================
    // ============== POST == send player to server ====================================
    @OpenApi(
            path = ResourceUrls.SEND_PLAYER_TO_SERVER,
            summary = "Sends the player with this name to the server with this ID.",
            tags = {"Send players"},
            method = HttpMethod.POST,
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void postSendPlayerServer(Context ctx) {  // todo send correct http status codes
        String serverId = ctx.pathParam("id");
        ServerInfo serverInfo = ServermanagerBungee.proxy.getServerInfo(serverId);
        String playerName = ctx.pathParam("player");
        ProxiedPlayer player = ServermanagerBungee.proxy.getPlayer(playerName);
        player.connect(serverInfo);
        ctx.json("success");
    }

    @OpenApi(
            path = ResourceUrls.SEND_PLAYER_TO_GROUP,
            summary = "Sends the player with this name to the group with this ID.",
            tags = {"Send players"},
            method = HttpMethod.POST,
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void postSendPlayerGroup(Context ctx) {  // todo send correct http status codes
        String groupId = ctx.pathParam("id");
        String playerName = ctx.pathParam("player");
        ProxiedPlayer player = ServermanagerBungee.proxy.getPlayer(playerName);
        // If group not known, abort
        // todo implement
        ctx.json("success");
    }
}
