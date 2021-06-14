package com.github.offby0point5.mc.plugin.hybrid.servermanager.groups;

import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * JoinRules dictate the Group to join when players are being kicked form a Server
 */
public interface KickRule {
    /**
     * Returns the group a player should join when kicked from a server
     * @param player The kicked player
     * @param group The group from which the player was kicked
     * @return The group to join or null to kick from proxy
     */
    ServerGroup getGroup(ProxiedPlayer player, ServerGroup group);
}
