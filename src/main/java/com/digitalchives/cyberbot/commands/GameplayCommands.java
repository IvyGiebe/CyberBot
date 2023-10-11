package com.digitalchives.cyberbot.commands;

import com.digitalchives.cyberbot.enums.CheckResult;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class GameplayCommands {


    public void check(int abilityPoints, boolean bonusDice, boolean penaltyDice, SlashCommandInteractionEvent event){
        String roller = event.getGuild().getMember(event.getUser()).getEffectiveName();

        double roll = Math.floor(Math.random() * 100 + 1);
        int extraRoll = bonusDice || penaltyDice ? (int) Math.floor(Math.random() * 10) : 0, adjustedRoll = (int) roll;

        boolean adjusted = false;
        if (bonusDice){
            if (Math.floor((roll-1)/10) > extraRoll){
                adjustedRoll = (extraRoll * 10) + ((int)(roll % 10));
                adjusted = true;
            }
        }
        else if (penaltyDice){
            if (Math.floor((roll-1)/10) < extraRoll) {
                adjustedRoll = (extraRoll * 10) + ((int)(roll % 10));
                adjusted = true;
            }
        }

        CheckResult result;
        int magnitude = Math.abs(adjustedRoll - abilityPoints);
        result = adjustedRoll <= abilityPoints ? magnitude >= 20 ? CheckResult.EXCESS_SUCCESS : CheckResult.SUCCESS :
                magnitude >= 20 ? CheckResult.EXCESS_FAILURE : CheckResult.FAILURE;
        String resultString = "";
        switch (result) {
            case EXCESS_SUCCESS -> resultString = "Excess Success";
            case SUCCESS -> resultString = "Success";
            case FAILURE -> resultString = "Failue";
            case EXCESS_FAILURE -> resultString = "Excess Failure";
        }

        String output = bonusDice || penaltyDice ? roller + "\nResult: " + resultString + "\nMagnitude: " + magnitude + "\nOriginal roll: " + (int) roll + "\nExtra dice: " + extraRoll + "\nUsed roll: " + adjustedRoll : roller + "\nResult: " + resultString + "\nMagnitude: " + magnitude + "\nRoll: " + adjustedRoll;

        event.reply(output).queue();
    }

    public void roll(int diceValue, int diceQuantity, SlashCommandInteractionEvent event){
        int result = 0;

        for (int i = 0; i < diceQuantity; i++) {
            result += (int) Math.floor(Math.random() * diceValue + 1);
        }

        event.reply("Rolled: " + diceQuantity + "d" + diceValue + "\nResult: " + result).queue();
    }
}
