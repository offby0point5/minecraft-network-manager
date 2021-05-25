package com.github.offby0point5.mc.plugin.hybrid.servermanager.groups;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

/**
 * JoinRules dictate the Server to connect players to when joining a Group
 */
public interface JoinRule {
    /**
     * Returns the server to connect the player to
     * @param player The player that wants to join this group
     * @param group The group the player wants to join
     * @return The server the player is to be sent
     */
    RegisteredServer getServer(Player player, ServerGroup group);
}
