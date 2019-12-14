package com.mcres.dolphin.randomteleport;

import com.mcres.dolphin.randomteleport.utils.Config;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;
    @Getter
    private static Config language;
    @Getter
    private static Config counter;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        counter = Config.registerConfig(this, "counter");
        language = Config.registerConfig(this, "language");
        Commands.init();
        getCommand("randomteleport")
                .setExecutor(new Commands());
        getServer().getPluginManager().registerEvents(new Events(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
