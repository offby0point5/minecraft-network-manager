package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.github.offby0point5.mc.plugin.hybrid.servermanager.groups.*;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

public class PluginConfiguration {
    public static final String GROUPS = "groups";
    public static final String SERVERS = "servers";
    public static final String REST_HOST = "restapi";

    private final RestHost host;
    private final Servers servers;
    private final Groups groups;

    private PluginConfiguration(RestHost restHost, Servers servers, Groups groups) {
        this.host = restHost;
        this.servers = servers;
        this.groups = groups;
    }

    public static PluginConfiguration read(Path path) {
        // todo resaveNeeded ?
        URL defaultConfigLocation = PluginConfiguration.class.getClassLoader()
                .getResource("default-config.toml");
        if (defaultConfigLocation == null) {
            throw new RuntimeException("Default configuration file does not exist.");
        }

        CommentedFileConfig config = CommentedFileConfig.builder(path)
                .defaultData(defaultConfigLocation)
                .preserveInsertionOrder()
                .sync()
                .build();
        config.load();

        // Read the rest of the config
        CommentedConfig serversConfig = config.get(SERVERS);
        CommentedConfig groupsConfig = config.get(GROUPS);
        CommentedConfig restConfig = config.get(REST_HOST);

        RestHost restHost = new RestHost(restConfig);
        Servers servers = new Servers(serversConfig);
        Groups groups = new Groups(groupsConfig);

        return new PluginConfiguration(
                restHost,
                servers,
                groups
        );
    }

    public InetSocketAddress getHost() {
        return host.address;
    }

    public Servers getServers() {
        return servers;
    }

    public Groups getGroups() {
        return groups;
    }

    private static class RestHost {
        private InetSocketAddress address;

        private RestHost(CommentedConfig config) {
            if (config == null) throw new IllegalArgumentException("No REST host configuration");
            try {
                String[] addr = config.getOrElse("bind", "127.0.0.1:25564").split(":", 2);
                address = new InetSocketAddress(addr[0], Integer.parseInt(addr[1]));
            } catch (IllegalArgumentException e) {
                ServermanagerVelocity.plugin.logger.error("'bind' option does not specify a valid IP address.", e);
            }
        }
    }

    private static class Servers {
        private Servers(CommentedConfig config) {
            // Add all servers from velocity config
            for (RegisteredServer server : ServermanagerVelocity.proxy.getAllServers()) {
                new ServerData(server.getServerInfo().getName(),
                        new ServerAddresses(server.getServerInfo().getAddress(), null, null));

            }

            if (config == null) return;

            // Add their configuration
            for (UnmodifiableConfig.Entry entry : config.entrySet()) {
                if (!ServermanagerVelocity.proxy.getServer(entry.getKey()).isPresent()) continue;
                CommentedConfig serverConfig = entry.getValue();
                String mainGroup = serverConfig.getOrElse("main", "lobby");
                Set<String> allGroups = serverConfig.getOrElse("groups", Collections.emptySet());
                ServerData.setData(entry.getKey(), new ServerGroups(mainGroup, allGroups));
            }
        }

    }
    private static class Groups {
        private Groups(CommentedConfig config) {
            if (config == null) return;
            // Get groups from config
            for (UnmodifiableConfig.Entry entry : config.entrySet()) {
                String groupName = entry.getKey();
                CommentedConfig groupConfig = entry.getValue();

                if (groupConfig == null) continue;

                JoinRule joinRule = groupConfig.getEnum("join-rule", StandardJoinRule.class);
                KickRule kickRule = groupConfig.getEnum("kick-rule", StandardKickRule.class);
                CommentedConfig menuItemConfig = groupConfig.get("menu-item");

                if (menuItemConfig != null) {
                    MenuData.Entry menuItem = new MenuData.Entry(
                            menuItemConfig.getOrElse("item-material", "BARRIER"),
                            menuItemConfig.getOrElse("item-amount", 1),
                            menuItemConfig.getOrElse("display-name", "NO_NAME"),
                            menuItemConfig.getOrElse("lore", Collections.emptyList()),
                            menuItemConfig.getOrElse("priority", 1),
                            menuItemConfig.getEnumOrElse("status", MenuData.Status.ONLINE));
                    ServerGroup.getInstance(groupName, joinRule, kickRule, menuItem);
                } else {
                    ServerGroup.getInstance(groupName, joinRule, kickRule);
                }
            }

            // Add defaults
            ServerGroup.getInstance("lobby", StandardJoinRule.LEAST, StandardKickRule.KICK_TO_LOBBY,
                    new MenuData.Entry("MAGMA_CREAM", 1,
                            MiniMessage.get().serialize(Component.text("Lobby", NamedTextColor.GREEN)),
                            Collections.singletonList(MiniMessage.get().serialize(Component.text("Alle anderen Server sind von hier zu erreichen", NamedTextColor.GRAY))),
                            10, MenuData.Status.ONLINE));
            ServerGroup.getInstance("random", StandardJoinRule.RANDOM, StandardKickRule.KICK_TO_LOBBY,
                    new MenuData.Entry("DRAGON_EGG", 1,
                            MiniMessage.get().serialize(Component.text("Zufälliger Server", NamedTextColor.LIGHT_PURPLE)),
                            Collections.singletonList(MiniMessage.get().serialize(Component.text("Leitet dich auf einen zufälligen Server weiter", NamedTextColor.GRAY))),
                            0, MenuData.Status.ONLINE));
        }
    }
}
