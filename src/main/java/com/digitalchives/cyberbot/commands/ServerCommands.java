package com.digitalchives.cyberbot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ServerCommands {

    public void myName(String playerName, SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();

        guild.createRole()
                .setName(playerName)
                .setColor(Color.WHITE)
                .setHoisted(false)
                .setMentionable(false)
                .setPermissions(Permission.EMPTY_PERMISSIONS)
                .complete();

        guild.addRoleToMember(event.getMember(), guild.getRolesByName(playerName, true).get(0)).queue();

        event.reply("Nice to meet you " + playerName + ". You now have your name as a role.").setEphemeral(true).queue();
    }

    public void names(SlashCommandInteractionEvent event) {
        ArrayList<String> characterNames = new ArrayList<>();
        ArrayList<String> playerNames = new ArrayList<>();

        for(Member member : event.getGuild().getMembers()){
            if(!member.getUser().isBot()) {
                characterNames.add(member.getEffectiveName());
                playerNames.add(member.getRoles().getFirst().getName());
            }
        }

        String output = "";
        for(int i = 0; i < characterNames.size(); i++){
            output += characterNames.get(i) + " -> " + playerNames.get(i) + "\n";
        }

        event.reply(output).setEphemeral(true).queue();
    }
}
