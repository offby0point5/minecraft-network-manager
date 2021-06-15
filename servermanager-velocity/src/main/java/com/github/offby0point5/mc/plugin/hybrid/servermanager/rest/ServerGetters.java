package com.github.offby0point5.mc.plugin.hybrid.servermanager.rest;

import com.github.offby0point5.mc.plugin.hybrid.servermanager.ResourceUrls;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServerAddresses;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServerFlags;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServerGroups;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.groups.ServerData;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;

public class ServerGetters {
    @OpenApi(
            path = ResourceUrls.PORTS,
            summary = "Get port numbers of the server with this id.",
            tags = {"Get server data"},
            method = HttpMethod.GET,
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = ServerAddresses.class)}),
            responses = {
                    @OpenApiResponse(status = "404"),
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void getServerPorts(Context context) {
        String serverId = context.pathParam("id");
        if (!ServerData.hasServer(serverId)) {
            context.status(404);
            return;
        }
        context.json(ServerData.getServer(serverId).ports);
    }

    @OpenApi(
            path = ResourceUrls.GROUPS,
            summary = "Get groups of the server with this id.",
            tags = {"Get server data"},
            method = HttpMethod.GET,
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = ServerGroups.class)}),
            responses = {
                    @OpenApiResponse(status = "404"),
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void getServerGroups(Context context) {
        String serverId = context.pathParam("id");
        if (!ServerData.hasServer(serverId)) {
            context.status(404);
            return;
        }
        context.json(ServerData.getServer(serverId).groups);
    }

    @OpenApi(
            path = ResourceUrls.FLAGS,
            summary = "Get flags of the server with this id.",
            tags = {"Get server data"},
            method = HttpMethod.GET,
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = ServerFlags.class)}),
            responses = {
                    @OpenApiResponse(status = "404"),
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void getServerFlags(Context context) {
        String serverId = context.pathParam("id");
        if (!ServerData.hasServer(serverId)) {
            context.status(404);
            return;
        }
        context.json(ServerData.getServer(serverId).flags);
    }
}
