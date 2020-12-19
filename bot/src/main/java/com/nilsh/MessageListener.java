package com.nilsh;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Simple discord bot. Only starts a minecraft server right now
 */
public class MessageListener extends ListenerAdapter {
    private static final String token = "token";

    public static void main(String[] args) throws LoginException {
        JDABuilder.createDefault(token).addEventListeners(new MessageListener()).build();
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
                    event.getChannel().sendMessage("Server konnte nicht gestartet werden --- " + e.getMessage()).queue();
                }
                break;
            case "-restart":
                System.out.println("Restartet");
                event.getChannel().sendMessage("Server restartet...").queue();
                break;
            default:
                System.out.println(receivedMessage);
                break;
        }
    }
}