package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import unirest.Unirest;

import java.util.Set;
import java.util.stream.Collectors;

public class ProxyApi {
    // GET requests ================================================
    public static Set<String> getServers() {
        return Unirest.get(ResourceUrls.UNIREST_SERVERS).asJson().getBody().getArray().toList()
                .stream().map(element -> (String) element).collect(Collectors.toSet());
    }

    public static MenuData getMenuMain() {
        return Unirest.get(ResourceUrls.UNIREST_MENU_MAIN)
                .asObject(MenuData.class).getBody();
    }

    public static MenuData getMenuGroup(String groupName) {
        return Unirest.get(ResourceUrls.UNIREST_MENU_GROUP)
                .routeParam("id", groupName)
                .asObject(MenuData.class).getBody();
    }

    // POST requests ================================================
    public static void postSendPlayerServer(String serverName, String playerName) {
        Unirest.post(ResourceUrls.UNIREST_SEND_PLAYER_TO_SERVER)
                .routeParam("player", playerName)
                .routeParam("id", serverName).asEmpty();
    }

    public static void postSendPlayerGroup(String groupName, String playerName) {
        Unirest.post(ResourceUrls.UNIREST_SEND_PLAYER_TO_GROUP)
                .routeParam("player", playerName)
                .routeParam("id", groupName).asEmpty();
    }

    // PUT requests ================================================

    /**
     * Requests initial registration on the proxy. Should only be sent once.
     * @param serverName The name this server should have on the proxy
     * @param ports      The ServerPorts object describing all ports this server provides
     */
    public static void putServerPorts(String serverName, ServerPorts ports) {
        Unirest.put(ResourceUrls.UNIREST_PORTS)
                .routeParam("id", serverName)
                .body(ports).asEmpty();
    }

    public static void putServerGroups(String serverName, ServerGroups groups) {
        Unirest.put(ResourceUrls.UNIREST_GROUPS)
                .routeParam("id", serverName)
                .body(groups).asEmpty();
    }

    public static void putServerFlags(String serverName, ServerFlags flags) {
        Unirest.put(ResourceUrls.UNIREST_FLAGS)
                .routeParam("id", serverName)
                .body(flags).asEmpty();
    }

    // DELETE requests ================================================
    public static void deleteServer(String serverName) {
        Unirest.delete(ResourceUrls.UNIREST_DELETE)
                .routeParam("id", serverName).asEmpty();
    }
}
