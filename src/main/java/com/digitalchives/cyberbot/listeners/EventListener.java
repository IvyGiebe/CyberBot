package com.digitalchives.cyberbot.listeners;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
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

}
