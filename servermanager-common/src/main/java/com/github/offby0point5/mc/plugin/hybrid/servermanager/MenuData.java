package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuData {
    public final Entry headerEntry;
    public final Map<String, Entry> entries = new HashMap<>();  // server/group name -> displaying item

    public MenuData(Entry headerEntry, Map<String, Entry> entries) {
        this.headerEntry = headerEntry;
        this.entries.putAll(entries);
    }

    public static class Entry {  // todo add priority value for sorting the entries
        public final String itemMaterial;  // as bukkit Material name
        public final int itemAmount;
        public final String displayName;  // as MiniMessage string
        public final List<String> lore;  // as MiniMessage strings

        public Entry(String itemMaterial, int itemAmount, String displayName, List<String> lore) {
            this.itemMaterial = itemMaterial;
            this.itemAmount = itemAmount;
            this.displayName = displayName;
            this.lore = lore;
        }
    }
}
