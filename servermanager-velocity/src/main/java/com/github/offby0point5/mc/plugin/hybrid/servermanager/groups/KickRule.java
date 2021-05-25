package com.github.offby0point5.mc.plugin.hybrid.servermanager.groups;

import com.velocitypowered.api.proxy.Player;

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
    ServerGroup getGroup(Player player, ServerGroup group);
}
