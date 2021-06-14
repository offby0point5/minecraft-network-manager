package com.github.offby0point5.mc.plugin.hybrid.servermanager.groups;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * This class handles events relevant for the server groups
 */
public class GroupEvents implements Listener {
    // todo event when player requests server change

    /**
     * Execute the KickRule
     */
    @EventHandler
    public void onPlayerKicked(ServerKickEvent event) {
        String serverId = event.getKickedFrom().getName();
        String mainGroupName = ServerData.getServer(serverId).groups.main;
        ServerGroup group = ServerGroup.getInstance(mainGroupName);  // if group does not exist, create it
        ServerGroup newGroup = group.getKickGroup(event.getPlayer());
        if (newGroup != null) {
            ServerInfo serverInfo = newGroup.getJoinServer(event.getPlayer());
            event.setCancelServer(serverInfo);
            event.setCancelled(true);
            return;
        }
        event.getPlayer().disconnect(event.getKickReasonComponent());
    }

    /**
     * Always send players to a lobby server on join
     */
    @EventHandler
    public void onPlayerJoin(ServerConnectEvent event) {
        ServerGroup lobbyGroup = ServerGroup.getGroup("lobby");
        ServerInfo serverInfo = lobbyGroup.getJoinServer(event.getPlayer());
        event.setTarget(serverInfo);
    }
}
