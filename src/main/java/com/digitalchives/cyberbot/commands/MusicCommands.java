package com.digitalchives.cyberbot.commands;

import com.digitalchives.cyberbot.UtilMethods;
import com.digitalchives.cyberbot.music.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.ArrayList;

public class MusicCommands {

    UtilMethods utilMethods = new UtilMethods();


    /*
    These are all commands that affect what's playing
     */

    public void play(String song, SlashCommandInteractionEvent event) {
        if(!event.getMember().getVoiceState().inAudioChannel()){
            event.reply("You must be in a voice channel").setEphemeral(true).queue();
        }

        final AudioManager audioManager = event.getGuild().getAudioManager();
        final VoiceChannel voiceChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel().asVoiceChannel();
        audioManager.openAudioConnection(voiceChannel);

        song = utilMethods.isURL(song) ? song : "ytsearch:" + song;

        PlayerManager.getINSTANCE().loadAndPlay(song, event);
    }

    public void pause(boolean pause, SlashCommandInteractionEvent event) {
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.reply("You must be in a voice channel to use this command.").setEphemeral(true).queue();
        }

        PlayerManager.getINSTANCE().pause(pause, event);

        if(pause)
            event.reply("Pausing music").setEphemeral(true).queue();
        else
            event.reply("Resuming music").setEphemeral(true).queue();
    }

    public void skip(SlashCommandInteractionEvent event) {
        if(!event.getMember().getVoiceState().inAudioChannel()){
            event.reply("You must be in a voice channel").setEphemeral(true).queue();
        }

        PlayerManager.getINSTANCE().skip(event);
        event.reply("Skipping to the next song in the queue").setEphemeral(true).queue();
    }

    public void disconnect(SlashCommandInteractionEvent event) {
        final AudioManager audioManager = event.getGuild().getAudioManager();
        PlayerManager.getINSTANCE().emptyQueue(event);
        PlayerManager.getINSTANCE().skip(event);
        audioManager.closeAudioConnection();
        event.reply("Bye").setEphemeral(true).queue();
    }


    /*
    These are all commands focused on the queue
     */

    public void displayQueue(SlashCommandInteractionEvent event) {
        AudioTrack currentTrack = PlayerManager.getINSTANCE().nowPlaying(event);
        ArrayList<AudioTrack> queueList = PlayerManager.getINSTANCE().queue(event);

        String queueString;

        if (currentTrack != null) {
            queueString = ("Now Playing: " + currentTrack.getInfo().title);
            int position = 1;
            for (AudioTrack track : queueList) {
                queueString += ("\n" + position + ": " + track.getInfo().title);
                position++;
            }
        }
        else
            queueString = "The queue is currently empty.";

        event.reply(queueString).setEphemeral(true).queue();
    }

    public void nowPlaying(SlashCommandInteractionEvent event) {
        AudioTrack currentTrack = PlayerManager.getINSTANCE().nowPlaying(event);
        if (currentTrack != null)
            event.reply("I'm playing: " + currentTrack.getInfo().title + " right now").setEphemeral(true).queue();
        else
            event.reply("I'm not currently playing anything.").setEphemeral(true).queue();
    }

    public void shuffle(SlashCommandInteractionEvent event) {
        PlayerManager.getINSTANCE().shuffleQueue(event);

        event.reply("The queue is shuffled").setEphemeral(true).queue();
    }

    public void remove(int position, SlashCommandInteractionEvent event) {
        PlayerManager.getINSTANCE().removeTrack(position, event);
        event.reply("Track Removed").setEphemeral(true).queue();
    }

    public void clearQueue(SlashCommandInteractionEvent event) {
        PlayerManager.getINSTANCE().emptyQueue(event);
        event.reply("Queue Emptied").setEphemeral(true).queue();
    }

}