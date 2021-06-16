package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;

public class ServermanagerMinestom extends Extension {
    @Override
    public void initialize() {
        MinecraftServer.LOGGER.info("Servermanager loaded");
    }

    @Override
    public void terminate() {
        MinecraftServer.LOGGER.info("Servermanager unloaded");
    }
}
