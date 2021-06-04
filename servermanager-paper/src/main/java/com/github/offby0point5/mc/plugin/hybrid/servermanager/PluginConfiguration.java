package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import org.bukkit.Bukkit;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PluginConfiguration {
    public static final String NAME = "name";
    public static final String MAIN_GROUP = "main-group";
    public static final String GROUPS = "groups";

    private final String name;
    private final String mainGroup;
    private final Set<String> allGroups;

    private PluginConfiguration(String name, String mainGroup, Set<String> allGroups) {
        this.name = name;
        this.mainGroup = mainGroup;
        this.allGroups = allGroups;
    }

    public String getName() {
        return name;
    }

    public String getMainGroup() {
        return mainGroup;
    }

    public Set<String> getAllGroups() {
        return allGroups;
    }

    public static PluginConfiguration read(Path path) {
        Config config = new TomlParser().parse(path, FileNotFoundAction.CREATE_EMPTY);

        ConfigSpec configSpec = new ConfigSpec();
        configSpec.defineOfClass(NAME, "lobby", String.class);
        configSpec.defineOfClass(MAIN_GROUP, "lobby", String.class);
        configSpec.defineList(GROUPS, Collections.singletonList("lobby"), o -> o instanceof String);

        if (configSpec.correct(config) > 0) {
            // write out corrected config version
            new TomlWriter().write(config, path, WritingMode.REPLACE);
            ServermanagerPaper.plugin.getLogger().warning("Configuration was automatically corrected! " +
                    "Make sure it does what you want it to do.");
        }

        String name = String.format("%05d-%s", Bukkit.getServer().getPort(), config.get(NAME));
        String mainGroup = config.get(MAIN_GROUP);
        Set<String> allGroups = new HashSet<>(config.get(GROUPS));

        return new PluginConfiguration(name, mainGroup, allGroups);
    }
}
