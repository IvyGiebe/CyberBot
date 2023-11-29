package com.digitalchives.cyberbot.commands;

import com.digitalchives.cyberbot.enums.Playlists;
import com.digitalchives.cyberbot.music.AudioPlayerSendHandler;
import com.digitalchives.cyberbot.music.PlayerManager;
import com.digitalchives.cyberbot.music.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.swing.text.html.Option;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.digitalchives.cyberbot.enums.Playlists.*;

public class CommandManager extends ListenerAdapter {

    GameplayCommands gameplayCommands = new GameplayCommands();
    MusicCommands musicCommands = new MusicCommands();
    ServerCommands serverCommands = new ServerCommands();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        String playlist;
        Playlists playlistSelection;
        OptionMapping playlistOption;

        switch(command) {

            /*
            Gameplay Commands
             */
            case ("check"):
                OptionMapping abilityPointsOption = event.getOption("ability_points"), bonusDiceOption = event.getOption("bonus_dice"), penaltyDiceOption = event.getOption("penalty_dice");
                int abilityPoints = abilityPointsOption.getAsInt();
                boolean bonusDice = false, penaltyDice = false;
                if (bonusDiceOption != null)
                    bonusDice = bonusDiceOption.getAsBoolean();
                else if (penaltyDiceOption != null)
                    penaltyDice = penaltyDiceOption.getAsBoolean();

                gameplayCommands.check(abilityPoints, bonusDice, penaltyDice, event);
                break;

            case("roll"):
                OptionMapping diceValeOption = event.getOption("dice_value"), diceQuantityOption = event.getOption("dice_quantity");
                int diceValue = 100, diceQuantity = 1;
                if (diceValeOption != null)
                    diceValue = diceValeOption.getAsInt();
                if (diceQuantityOption != null)
                    diceQuantity = diceQuantityOption.getAsInt();

                gameplayCommands.roll(diceValue, diceQuantity, event);
                break;

            /*
            Music Commands
            */
            case("play"):
                OptionMapping songOption = event.getOption("song");
                String song = songOption.getAsString();
                musicCommands.play(song, event);
                break;
            case("pause"):
                musicCommands.pause(true, event);
                break;
            case("unpause"):
                musicCommands.pause(false, event);
                break;
            case("skip"):
                musicCommands.skip(event);
                break;
            case("disconnect"), ("dc"):
                musicCommands.disconnect(event);
                break;
            case("queue"):
                musicCommands.displayQueue(event);
                break;
            case("now_playing"):
                musicCommands.nowPlaying(event);
                break;
            case("shuffle"):
                musicCommands.shuffle(event);
                break;
            case("remove"):
                OptionMapping positionOption = event.getOption("position");
                int position = positionOption.getAsInt();
                musicCommands.remove(position, event);
                break;
            case("clear_queue"):
                musicCommands.clearQueue(event);
                break;

            //playlist commands for specific scenarios

            case("playlist"):
                playlistOption = event.getOption("playlist");
                playlist = playlistOption.getAsString();
                playlistSelection = switch (playlist) {
                    case "Pregame" ->
                            PREGAME;
                    case "Russian Hardstyle" ->
                            RUSSIAN_HARDSTYLE;
                    case "Nightcore" ->
                            NIGHTCORE;
                    case "League" ->
                            LEAGUE;
                    default ->
                            EMPTY;

                };
                musicCommands.playPlaylist(playlistSelection, true, event);
                break;

            case("queue_playlist"):
                playlistOption = event.getOption("playlist");
                playlist = playlistOption.getAsString();
                playlistSelection = switch (playlist) {
                    case "Pregame" ->
                            PREGAME;
                    case "Russian Hardstyle" ->
                            RUSSIAN_HARDSTYLE;
                    case "Nightcore" ->
                            NIGHTCORE;
                    case "League" ->
                            LEAGUE;
                    default ->
                            EMPTY;
                };
                musicCommands.playPlaylist(playlistSelection, false, event);
                break;

            /*
            Server Commands
             */
            case("my_name"):
                OptionMapping playerNameOption = event.getOption("my_name");
                String playerName = playerNameOption.getAsString();

                serverCommands.myName(playerName, event);
                break;

            case("names"):
                serverCommands.names(event);
                break;
        }
    }


    public List<CommandData> commandList(){
        List<CommandData> commandData = new ArrayList<>();


        //Gameplay Commands
        OptionData ability_points = new OptionData(OptionType.INTEGER, "ability_points", "Amount of points you need to roll under to succeed", true);
        OptionData bonus_dice = new OptionData(OptionType.BOOLEAN, "bonus_dice", "Roll with a bonus dice (false by default)", false);
        OptionData penalty_dice = new OptionData(OptionType.BOOLEAN, "penalty_dice", "Roll with a penalty dice (false by default)", false);
        OptionData dice_value = new OptionData(OptionType.INTEGER, "dice_value", "The size of the dice you're rolling (100 by default)", false);
        OptionData dice_quantity = new OptionData(OptionType.INTEGER, "dice_quantity", "The amount of dice you're rolling (1 by default)", false);

        commandData.add(Commands.slash("check", "Roll for an action check").addOptions(ability_points, bonus_dice, penalty_dice));
        commandData.add(Commands.slash("roll", "Roll some dice").addOptions(dice_value, dice_quantity));


        //Music Commands
        OptionData song = new OptionData(OptionType.STRING, "song", "Either use a link or I'll use youtube search", true);
        OptionData position = new OptionData(OptionType.INTEGER, "position", "Position of song you want to remove", true);
        OptionData playlist = new OptionData(OptionType.STRING, "playlist", "Pick a playlist from the available options", true);
        playlist.addChoice("Pregame", "Pregame");
        playlist.addChoice("Russian Hardstyle", "Russian Hardstyle");
        playlist.addChoice("Nightcore", "Nightcore");
        playlist.addChoice("League", "League");


        commandData.add(Commands.slash("play", "Plays music").addOptions(song));
        commandData.add(Commands.slash("pause", "Pauses the music"));
        commandData.add(Commands.slash("unpause", "resumes playing the music"));
        commandData.add(Commands.slash("skip", "Goes to next song in queue"));
        commandData.add(Commands.slash("disconnect", "Stop playing music and disconnect from voice channel"));
        commandData.add(Commands.slash("dc", "Stop playing music and disconnect from voice channel"));

        commandData.add(Commands.slash("queue", "Displays the current queue"));
        commandData.add(Commands.slash("now_playing", "Displays the current track"));
        commandData.add(Commands.slash("shuffle", "Re-orders the current queue randomly"));
        commandData.add(Commands.slash("remove", "Removes track from given position").addOptions(position));
        commandData.add(Commands.slash("clear_queue", "Clears current queue"));

        commandData.add(Commands.slash("playlist", "Plays selected playlist").addOptions(playlist));
        commandData.add(Commands.slash("queue_playlist", "Adds selected playlist to queue").addOptions(playlist));


        //Server Commands
        //OptionData player_name = new OptionData(OptionType.STRING, "my_name", "Please write your real name", true);
        OptionData color = new OptionData(OptionType.STRING, "color", "Please pick a color for your name", true);

        //commandData.add(Commands.slash("my_name", "Please input your real name").addOptions(player_name));
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