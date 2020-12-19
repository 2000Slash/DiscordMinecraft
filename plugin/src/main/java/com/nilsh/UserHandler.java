package com.nilsh;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Shuts the server down if nobody is connected
 */
public class UserHandler implements Listener {
    Logger logger;
    private long time;
    JavaPlugin plugin;

    public UserHandler(Logger logger, JavaPlugin plugin) {
        this.logger = logger;
        this.plugin = plugin;
        time = plugin.getConfig().getLong("wait-time");
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent evt) throws InterruptedException {
        logger.info("Player " + evt.getPlayer().getName() + " lefted the game.");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (Bukkit.getServer().getOnlinePlayers().size() == 0) {
                    logger.info("Server empty, Shutting down");
                    Bukkit.getServer().shutdown();
                } else {
                    logger.info("Server not empty, resuming");
                }
            }
            
        }, time*1000);
    }
}
