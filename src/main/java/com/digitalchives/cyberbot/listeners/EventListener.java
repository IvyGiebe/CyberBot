package com.digitalchives.cyberbot.listeners;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        String user = event.getUser().getAsMention();

        String welcomeMessage = "Welcome to " + event.getGuild().getName() + user + "! Please use the  /my_name <my name>  command " +
                "with your real name. Once you've made a character, please use the  /nick <character name>  command " +
                "with your character's name.";
        event.getGuild().getTextChannelsByName("Welcome", true).get(0).sendMessage(welcomeMessage).queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw().toLowerCase();
        if (message.contains("hi vira") || message.contains("hello vira") || message.contains("hey vira"))
            event.getMessage().reply("Hey " + event.getMember().getEffectiveName()).queue();
        if (message.contains("thanks vira") || message.contains("thank you vira"))
            event.getMessage().reply("You're welcome " + event.getMember().getEffectiveName() + " :)").queue();

        super.onMessageReceived(event);
    }
}
