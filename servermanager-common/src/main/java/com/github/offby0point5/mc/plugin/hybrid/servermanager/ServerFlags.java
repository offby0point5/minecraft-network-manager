package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ServerFlags {
    public final Set<String> flags;

    public ServerFlags(String... flags) {
        this.flags = new HashSet<>(Arrays.asList(flags));
    }
}
