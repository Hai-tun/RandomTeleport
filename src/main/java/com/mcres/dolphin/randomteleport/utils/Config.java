package com.mcres.dolphin.randomteleport.utils;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;

/**
 * @author dolphin
 * @Description:
 * @date 2019/12/14
 */
@AllArgsConstructor
public class Config {
    private static final HashSet<Config> configs = Sets.newHashSet();
    @Getter
    private final String name;
    @Getter
    private final File file;
    @Getter
    private YamlConfiguration configuration;
    @NotNull
    public static Config registerConfig(@NotNull Plugin plugin, @NotNull String name)
    {
        if (exists(name)) throw new RuntimeException("Config " + name + " already exists");
        name = name.toLowerCase();
        File file;
        if (!(file =new File(plugin.getDataFolder().getPath() + File.separator + name + ".yml")).exists())
        {
            plugin.saveResource(name + ".yml", false);
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        Config config = new Config(name, file, configuration);
        configs.add(config);
        return config;
    }

    @NotNull
    public YamlConfiguration reload()
    {
        return this.configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void save()
    {
        try
        {
            configuration.save(file);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static boolean exists(@NotNull String name)
    {
        return configs.stream().anyMatch(config -> StringUtils.equals(config.name, name));
    }

    @NotNull
    public static Optional<Config> findConfig(@NotNull String name)
    {
        return configs.stream().filter(config -> StringUtils.equals(config.name, name)).findAny();
    }

}
