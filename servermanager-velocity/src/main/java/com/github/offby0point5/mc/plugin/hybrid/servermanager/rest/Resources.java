package com.github.offby0point5.mc.plugin.hybrid.servermanager.rest;

import com.github.offby0point5.mc.plugin.hybrid.servermanager.ServermanagerVelocity;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.ResourceUrls;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.HttpMethod;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;

import java.util.stream.Collectors;

public class Resources {
    @OpenApi(
            path = ResourceUrls.SERVERS,
            summary = "Returns all server names registered on this proxy.",
            tags = {"Server"},
            method = HttpMethod.GET,
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public static void getServers(Context ctx) {
        ctx.json(ServermanagerVelocity.proxy.getAllServers().stream().map(
                (RegisteredServer server) -> server.getServerInfo().getName()).collect(Collectors.toSet()));
    }
}
