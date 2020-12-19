package com.nilsh;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Multipurpose minecraft papermc plugin.
 */
public class PluginEntry extends JavaPlugin {

    @Override
    public void onEnable() {
        // Copy the config.yml in the plugin configuration folder if it doesn't exist.
        this.saveDefaultConfig();
        // Register the UserHandler
        Bukkit.getServer().getPluginManager().registerEvents(new UserHandler(getLogger(), this), this);
    }

    @Override
    public void onDisable() {
        
    }

}
