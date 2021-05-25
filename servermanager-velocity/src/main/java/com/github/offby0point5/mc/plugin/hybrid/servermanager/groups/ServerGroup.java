package com.github.offby0point5.mc.plugin.hybrid.servermanager.groups;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.HashMap;

public class ServerGroup {
    // All existing groups
    private static final HashMap<String, ServerGroup> groupNameDataMap = new HashMap<>();

    // A name to identify this group
    public final String name;
    // The algorithm by which servers are chosen for new players to connect
    private final JoinRule joinRule;
    // The algorithm by which groups are chosen for players kicked from a server having this group as its main group
    private final KickRule kickRule;

    /**
     * Creates a new Group if the name is not taken or returns the group with this name
     * @param name The name to identify this group
     * @param joinRule The algorithm by which servers are chosen for new players to connect
     * @param kickRule The algorithm by which groups are chosen for players kicked from a server having this group as its main group
     * @return The new group or the already existing one with its own settings
     */
    public static ServerGroup getInstance(String name, JoinRule joinRule, KickRule kickRule) {
        if (!groupNameDataMap.containsKey(name)) {
            groupNameDataMap.put(name, new ServerGroup(name, joinRule, kickRule));
        }
        return groupNameDataMap.get(name);
    }

    /**
     * Returns either the group with this name if it exists or uses the standard rules to create a new group
     * @param name The name to identify this group
     * @return The new group or the already existing one with its own settings
     */
    public static ServerGroup getInstance(String name) {
        return getInstance(name, StandardJoinRule.LEAST, StandardKickRule.KICK_TO_LOBBY);
    }

    private ServerGroup(String name, JoinRule joinRule, KickRule kickRule) {
        this.name = name;
        this.joinRule = joinRule;
        this.kickRule = kickRule;
    }

    // ====== Functional stuff =============================================

    public RegisteredServer getJoinServer(Player player) {
        return joinRule.getServer(player, this);
    }

    public ServerGroup getKickGroup(Player player) {
        return kickRule.getGroup(player, this);
    }

    // ======================================================================

    public static ServerGroup getGroup(String groupName) {
        return groupNameDataMap.get(groupName);
    }
}
