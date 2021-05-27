package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ServerGroups {
    public final String main;
    public final Set<String> groups;

    public ServerGroups(String main, Set<String> groups) {
        this.main = main;
        this.groups = groups;
        this.groups.add(main);
    }

    public ServerGroups(String main, String... groups) {
        this.main = main;
        this.groups = new HashSet<>(Arrays.asList(groups));
        this.groups.add(main);
    }
}
