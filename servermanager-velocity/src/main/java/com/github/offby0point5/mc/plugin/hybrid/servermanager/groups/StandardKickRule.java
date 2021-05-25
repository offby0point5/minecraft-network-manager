package com.github.offby0point5.mc.plugin.hybrid.servermanager.groups;

import com.velocitypowered.api.proxy.Player;

public enum StandardKickRule implements KickRule {
    KICK_PROXY((player, group) -> null),
    KICK_TO_LOBBY((player, group) -> ServerGroup.getGroup("lobby"));

    final KickRule rule;

    StandardKickRule(KickRule rule) {
        this.rule = rule;
    }

    @Override
    public ServerGroup getGroup(Player player, ServerGroup group) {
        return rule.getGroup(player, group);
    }
}
