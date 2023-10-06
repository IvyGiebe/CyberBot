package com.digitalchives.cyberbot.commands;

import com.digitalchives.cyberbot.enums.CheckResult;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.handle.GuildRoleCreateHandler;
import org.apache.commons.collections4.functors.TruePredicate;

import javax.swing.text.html.Option;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    GameplayCommands gameplayCommands = new GameplayCommands();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        //roll (d100 by default, variable dice value option)
        if (command.equals("roll")) {
            OptionMapping diceValeOption = event.getOption("dice_value"), diceQuantityOption = event.getOption("dice_quantity");
            int diceValue = 100, diceQuantity = 1;
            if (diceValeOption != null)
                diceValue = diceValeOption.getAsInt();
            if (diceQuantityOption != null)
                diceQuantity = diceQuantityOption.getAsInt();

            int result = 0;

            for  (int i = 0; i < diceQuantity; i++) {
                result += (int) Math.floor(Math.random() * diceValue + 1);
            }

            event.reply("Rolled: " +diceQuantity + "d" + diceValue + "\nResult: " + result).queue();
        }

        //playerName (makes role for the player's name)
        else if (command.equals("my_name")) {
            OptionMapping playerNameOption = event.getOption("my_name");
            String playerName = playerNameOption.getAsString();

            Guild guild = event.getGuild();

            guild.createRole()
                    .setName(playerName)
                    .setColor(Color.WHITE)
                    .setHoisted(false)
                    .setMentionable(false)
                    .setPermissions(Permission.EMPTY_PERMISSIONS)
                    .complete();

            //while(guild.getRolesByName(playerName, true).get(0) == null){System.out.println("Waiting");}

            guild.addRoleToMember(event.getMember(), guild.getRolesByName(playerName, true).get(0)).queue();


            event.reply("Congrats you now have your name").setEphemeral(true).queue();

        }

        //names (gives ephemeral message saying names of all players)
        else if (command.equals("names")) {

            List<String> characterNames = new ArrayList<>();
            List<String> playerNames = new ArrayList<>();

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

        //check <value> (does a check and returns scale of success and differential)
        else if (command.equals("check")) {
            OptionMapping abilityPointsOption = event.getOption("ability_points"), bonusDiceOption = event.getOption("bonus_dice"), penaltyDiceOption = event.getOption("penalty_dice");
            int abilityPoints = abilityPointsOption.getAsInt();
            boolean bonusDice = false, penaltyDice = false;
            if (bonusDiceOption != null)
                bonusDice = bonusDiceOption.getAsBoolean();
            else if (penaltyDiceOption != null)

                penaltyDice = penaltyDiceOption.getAsBoolean();

            String roller = event.getGuild().getMember(event.getUser()).getEffectiveName();

            String output = gameplayCommands.check(abilityPoints, bonusDice, penaltyDice, roller);
            event.reply(output).queue();
        }

        //level (rolls for how much you level skill by; depends on how I do my leveling)
        else if (command.equals("level")) {

        }

        //sheets (will return all character sheets to look at)
        else if (command.equals("sheets")) {

        }

        //mysheet (returns just the sheet matching the character's name)
        else if (command.equals("mySheet")) {

        }

    }



    public List<CommandData> commandList(){
        List<CommandData> commandData = new ArrayList<>();

        //roll command
        OptionData dice_value = new OptionData(OptionType.INTEGER, "dice_value", "The size of the dice you're rolling (100 by default)", false);
        OptionData dice_quantity = new OptionData(OptionType.INTEGER, "dice_quantity", "The amount of dice you're rolling (1 by default)", false);
        commandData.add(Commands.slash("roll", "Roll some dice").addOptions(dice_value, dice_quantity));

        //check command
        OptionData ability_points = new OptionData(OptionType.INTEGER, "ability_points", "Amount of points you need to roll under to succeed", true);
        OptionData bonus_dice = new OptionData(OptionType.BOOLEAN, "bonus_dice", "Roll with a bonus dice (false by default)", false);
        OptionData penalty_dice = new OptionData(OptionType.BOOLEAN, "penalty_dice", "Roll with a penalty dice (false by default)", false);
        commandData.add(Commands.slash("check", "Roll for an action check").addOptions(ability_points, bonus_dice, penalty_dice));

        //player_name command
        OptionData player_name = new OptionData(OptionType.STRING, "my_name", "Please write your real name", true);
        commandData.add(Commands.slash("my_name", "Please input your real name").addOptions(player_name));

        //names command
        commandData.add(Commands.slash("names", "Gives a reference list of player and character names"));

        return commandData;
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        event.getGuild().updateCommands().addCommands(commandList()).queue();
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        event.getGuild().updateCommands().addCommands(commandList()).queue();
    }
}