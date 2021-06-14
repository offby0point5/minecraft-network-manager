package com.github.offby0point5.mc.plugin.hybrid.servermanager.groups;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public enum StandardKickRule implements KickRule {
    KICK_PROXY((player, group) -> null),
    KICK_TO_LOBBY((player, group) -> ServerGroup.getGroup("lobby"));

    final KickRule rule;

    StandardKickRule(KickRule rule) {
        this.rule = rule;
    }

    @Override
    public ServerGroup getGroup(ProxiedPlayer player, ServerGroup group) {
        return rule.getGroup(player, group);
    }
}
