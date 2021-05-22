package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import java.util.Arrays;
import java.util.HashSet;

public class ServerFlags {
    public final HashSet<String> flags;

    public ServerFlags(String... flags) {
        this.flags = new HashSet<>(Arrays.asList(flags));
    }
}
