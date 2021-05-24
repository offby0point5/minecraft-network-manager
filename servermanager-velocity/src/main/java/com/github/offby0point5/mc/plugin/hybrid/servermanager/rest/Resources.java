package com.github.offby0point5.mc.plugin.hybrid.servermanager.rest;

import com.github.offby0point5.mc.plugin.hybrid.servermanager.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;

import java.net.InetSocketAddress;
import java.util.stream.Collectors;

public class Resources {  // todo add all other resources  // todo add request body to swagger docs
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
        ctx.json(ServermanagerVelocity.proxy.getAllServers().stream().map(
                (RegisteredServer server) -> server.getServerInfo().getName()).collect(Collectors.toSet()));
    }

    // ==============================================================================
    // =============== Server data setters ==========================================
    @OpenApi(
            path = ResourceUrls.PORTS,
            summary = "Put port numbers for the server with this id.",
            tags = {"Set server data"},
            method = HttpMethod.PUT,
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = ServerPorts.class)}),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void putPorts(Context ctx) {  // todo send correct http status codes
        String serverId = ctx.pathParam("id");
        // If server already known, abort
        if (ServerData.serverNameDataMap.containsKey(serverId)) {
            ctx.json("exists");
            return;
        }
        // Try parse ports object
        try {
            ServerPorts ports = new Gson().fromJson(ctx.body(), ServerPorts.class);
            if (ports == null) {
                ctx.json("invalid object");
                return;
            }
            // If object is correctly deserialized, register new server
            new ServerData(serverId, ports);
            ServermanagerVelocity.proxy.registerServer(new ServerInfo(serverId, new InetSocketAddress(ports.game)));
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
        if (!ServerData.serverNameDataMap.containsKey(serverId)) {
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
        if (!ServerData.serverNameDataMap.containsKey(serverId)) {
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
        if (!ServerData.serverNameDataMap.containsKey(serverId)) {
            ctx.json("not found");
            return;
        }
        // Unregister and delete server
        ServermanagerVelocity.proxy.unregisterServer(new ServerInfo(serverId,
                new InetSocketAddress(ServerData.serverNameDataMap.get(serverId).ports.game)));
        ServerData.serverNameDataMap.remove(serverId);
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
        // todo implement
        // Return menu data
        ctx.json("menu_data");
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
        // todo implement
        // If group not known, abort
        if (groupId.equals("")) {
            ctx.json("not found");
            return;
        }
        // Return menu data
        ctx.json("menu_data");
    }
}
