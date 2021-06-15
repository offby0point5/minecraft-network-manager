package com.github.offby0point5.mc.plugin.hybrid.servermanager.groups;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;

/**
 * This class handles events relevant for the server groups
 */
public class GroupEvents {
    /**
     * Execute the KickRule
     */
    @Subscribe public void onPlayerKicked(KickedFromServerEvent event) {
        if (event.kickedDuringServerConnect()) return; // do nothing if kicked due to whitelist, version or something
        String serverId = event.getServer().getServerInfo().getName();
        String mainGroupName = ServerData.getServer(serverId).groups.main;
        ServerGroup group = ServerGroup.getInstance(mainGroupName);  // if group does not exist, create it
        ServerGroup newGroup = group.getKickGroup(event.getPlayer());
        if (newGroup == null) {
            // do not kick from proxy if reason is not server shutdown or restart
            Component reason = event.getServerKickReason().orElse(Component.empty());
            if (reason.contains(Component.text("Server is restarting"))
                    || reason.contains(Component.text("Server closed"))) {
                event.setResult(KickedFromServerEvent.RedirectPlayer.create(group.getJoinServer(event.getPlayer())));
            }
            event.setResult(KickedFromServerEvent.DisconnectPlayer.create(
                    event.getServerKickReason().orElse(Component.text("Kicked from server "+serverId+"! (For no reason)"))));
            return;
        }
        RegisteredServer server = newGroup.getJoinServer(event.getPlayer());
        event.setResult(KickedFromServerEvent.RedirectPlayer.create(server,
                event.getServerKickReason().orElse(Component.empty())));
    }

    /**
     * Always send players to a lobby server on join
     */
    @Subscribe public void onPlayerJoin(PlayerChooseInitialServerEvent event) {
        ServerGroup lobbyGroup = ServerGroup.getGroup("lobby");
        RegisteredServer server = lobbyGroup.getJoinServer(event.getPlayer());
        event.setInitialServer(server);
    }
}
