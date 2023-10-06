package com.digitalchives.cyberbot.commands;

import com.digitalchives.cyberbot.enums.CheckResult;

public class GameplayCommands {


    public String check(int abilityPoints, boolean bonusDice, boolean penaltyDice, String roller){
        //rolling the dice
        double roll = Math.floor(Math.random() * 100 + 1);
        int extraRoll = bonusDice || penaltyDice ? (int) Math.floor(Math.random() * 10) : 0, adjustedRoll = (int) roll;

        //bonus dice logic
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

        //result logic
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

        String output;
        //Readable
            /*
            if(bonusDice || penaltyDice){
                output = adjusted ?
                        "You rolled a(n) " + (int) roll + " and your extra dice was a " + extraRoll + ", meaning your roll changed to " + adjustedRoll + ", resulting in a " + resultString + " with a magnitude of " + magnitude :
                        "You rolled a(n) " + (int) roll + " and your extra dice was a " + extraRoll + ", meaning your roll stayed " + adjustedRoll + ", resulting in a " + resultString + " with a magnitude of " + magnitude;
            }
            else {
                output = ("You rolled a(n) " + adjustedRoll + ", resulting in a " + resultString + " with a magnitude of " + magnitude);
            }
            */
        //Concise
        output = bonusDice || penaltyDice ? roller + "\nResult: " + resultString + "\nMagnitude: " + magnitude + "\nOriginal roll: " + (int) roll + "\nExtra dice: " + extraRoll + "\nUsed roll: " + adjustedRoll : roller + "\nResult: " + resultString + "\nMagnitude: " + magnitude + "\nRoll: " + adjustedRoll;

        return output;
    }
}
