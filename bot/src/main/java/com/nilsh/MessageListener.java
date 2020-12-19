package com.nilsh;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import io.graversen.minecraft.rcon.Defaults;
import io.graversen.minecraft.rcon.MinecraftRcon;
import io.graversen.minecraft.rcon.commands.PlayerListCommand;
import io.graversen.minecraft.rcon.commands.base.ICommand;
import io.graversen.minecraft.rcon.query.playerlist.PlayerList;
import io.graversen.minecraft.rcon.query.playerlist.PlayerListMapper;
import io.graversen.minecraft.rcon.service.ConnectOptions;
import io.graversen.minecraft.rcon.service.MinecraftRconService;
import io.graversen.minecraft.rcon.service.RconDetails;

import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Simple discord bot. Only starts a minecraft server right now
 */
public class MessageListener extends ListenerAdapter {
    private static final String TOKEN = "token";
    private static final String RCON_PASSWORD = "password";
    private static final String RCON_IP = "ip";
    private static final int RCON_PORT = 25575;
    private static MinecraftRconService minecraftServer;
    private static JDA jda;
    private static Logger logger = LoggerFactory.getLogger(MessageListener.class);

    public static void main(String[] args) throws LoginException {
        jda = JDABuilder.createDefault(TOKEN).addEventListeners(new MessageListener()).build();
        reconnect();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String receivedMessage = event.getMessage().getContentRaw().toLowerCase();

        switch (receivedMessage) {
            case "-start":
                try {
                    Runtime.getRuntime().exec("start.bat");
                    event.getChannel().sendMessage("Server startet...").queue();
                } catch (IOException e) {
                    event.getChannel().sendMessage("Server konnte nicht gestartet werden --- " + e.getMessage())
                            .queue();
                }
                break;
            case "-list":
                if (!getStatus()) {
                    event.getChannel()
                            .sendMessage("Verbindung zum server konnte nicht hergestellt werden. Versuchs mit -start")
                            .queue();
                } else {
                    MinecraftRcon rcon = minecraftServer.minecraftRcon().orElseThrow(IllegalStateException::new);
                    try {
                        String response = rcon.sendAsync(() -> "list").get().getResponseString();
                        event.getChannel().sendMessage(response.substring(4)).queue();
                    } catch (InterruptedException | ExecutionException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                }
                break;
            case "-status":
                if (getStatus()) {
                    event.getChannel().sendMessage("Server läuft.").queue();
                } else {
                    event.getChannel().sendMessage("Server läuft nicht.").queue();
                }
                break;
            case "carsten?":
                event.getChannel().sendMessage("Fertig").queue();
                event.getChannel()
                        .sendFile(MessageListener.class.getClassLoader().getResourceAsStream("carsten_fertig.png"),
                                "FERTIG.png")
                        .queue();
                jda.getPresence().setStatus(OnlineStatus.OFFLINE);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        jda.getPresence().setStatus(OnlineStatus.ONLINE);
                    }
                }, 20000);
                break;
            default:
                logger.info("{} wrote: {}", event.getMessage().getAuthor(), receivedMessage);
                break;
        }
    }

    /**
     * Returns the status of the server. If the server is not found, it will try to
     * reconnect
     * 
     * @return
     */
    private static boolean getStatus() {
        if (!minecraftServer.isConnected()) {
            return reconnect();
        }
        return true;
    }

    /**
     * Reconnects with the rcon. Also updates the activity in the bot to either "Server läuft" or "Server läuft nicht"
     * 
     * @return whether or not a connection could be established
     */
    private static boolean reconnect() {
        // After a failed connection you have to create a new Service.
        // https://github.com/MrGraversen/minecraft-rcon/issues/4
        minecraftServer = new MinecraftRconService(new RconDetails(RCON_IP, RCON_PORT, RCON_PASSWORD),
            new ConnectOptions(1, Duration.ofSeconds(3), Defaults.CONNECTION_WATCHER_INTERVAL));
        minecraftServer.connectBlocking(Duration.ofSeconds(20));
        if(minecraftServer.isConnected()) {
            jda.getPresence().setActivity(Activity.playing("Server läuft"));
        } else {
            jda.getPresence().setActivity(Activity.playing("Server läuft nicht"));
        }
        return minecraftServer.isConnected();
    }
}