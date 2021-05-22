package com.github.offby0point5.mc.plugin.hybrid.servermanager;

public class ResourceUrls {
    public static final String SERVERS = "/servers";  // get all registered server IDs
    public static final String MENU_MAIN = "/menu";  // get main page server menu entries
    public static final String MENU_GROUP = "/menu/:id";  // get group page server menu entries for group with this ID
    public static final String FLAGS = "/server/:id/flags";  // set flags for server with this ID
    public static final String GROUPS = "/server/:id/groups";  // set groups for server with this ID
    public static final String PORTS = "/server/:id/ports";  // set ports for server with this ID

    public static final String UNIREST_SERVERS = SERVERS;
    public static final String UNIREST_MENU_MAIN = MENU_MAIN;
    public static final String UNIREST_MENU_GROUP = MENU_GROUP.replace(":id", "{id}");
    public static final String UNIREST_FLAGS = FLAGS.replace(":id", "{id}");
    public static final String UNIREST_GROUPS = GROUPS.replace(":id", "{id}");
    public static final String UNIREST_PORTS = PORTS.replace(":id", "{id}");
}
