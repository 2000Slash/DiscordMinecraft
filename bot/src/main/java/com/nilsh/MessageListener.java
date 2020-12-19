package com.nilsh;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Simple discord bot. Only starts a minecraft server right now
 */
public class MessageListener extends ListenerAdapter {
    private static final String token = "token";
    private static JDA jda;

    public static void main(String[] args) throws LoginException {
        jda = JDABuilder.createDefault(token).addEventListeners(new MessageListener()).build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String receivedMessage = event.getMessage().getContentRaw().toLowerCase();

        switch (receivedMessage) {
            case "-start":
                try {
                    Runtime.getRuntime().exec("start.bat");
                    event.getChannel().sendMessage("Server startet...").queue();
                    jda.getPresence().setActivity(Activity.playing("Server läuft"));
                } catch (IOException e) {
                    event.getChannel().sendMessage("Server konnte nicht gestartet werden --- " + e.getMessage())
                            .queue();

                    jda.getPresence().setActivity(Activity.playing("Server läuft nicht"));
                }
                break;
            case "-restart":
                System.out.println("Restartet");
                event.getChannel().sendMessage("Server restartet...").queue();
                jda.getPresence().setActivity(Activity.playing("Mensch, Ärger dich nicht!"));
                break;
            case "carsten?":
                System.out.println("Fertig");
                event.getChannel().sendMessage("Fertig").queue();
                event.getChannel().sendFile(MessageListener.class.getClassLoader().getResourceAsStream("carsten_fertig.png"),"FERTIG.png").queue();
                jda.getPresence().setStatus(OnlineStatus.OFFLINE);
                System.out.println(jda.getPresence().getStatus());
                new Timer().schedule(new TimerTask() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        jda.getPresence().setStatus(OnlineStatus.ONLINE);
                    }

                }, 20000);
                break;

            default:
                System.out.println(receivedMessage);
                break;
        }
    }
}