package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import java.net.InetSocketAddress;
import java.util.Set;

public interface IDataProvider {
    String getName();

    String getMainGroup();

    Set<String> getAllGroups();

    InetSocketAddress getGameAddress();
}
