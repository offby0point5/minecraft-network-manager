package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import java.util.Arrays;
import java.util.HashSet;

public class ServerGroups {
    public final String main;
    public final HashSet<String> groups;

    public ServerGroups(String main, HashSet<String> groups) {
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
