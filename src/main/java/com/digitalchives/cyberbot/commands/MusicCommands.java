package com.digitalchives.cyberbot.commands;

import com.digitalchives.cyberbot.UtilMethods;
import com.digitalchives.cyberbot.enums.Playlists;
import com.digitalchives.cyberbot.music.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.RestAction;

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

        if(utilMethods.isURL(song))
            PlayerManager.getINSTANCE().loadAndPlay(song, false, event);
        else
            PlayerManager.getINSTANCE().loadAndPlay(song, true, event);
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
                if(queueString.length() + track.getInfo().title.length() < 1998) {
                    queueString += ("\n" + position + ": " + track.getInfo().title);
                    position++;
                }
                else
                    break;
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

    //Playlists

    public void playPlaylist(Playlists playlist, boolean clearPrevious, SlashCommandInteractionEvent event){

        boolean randomOrder = playlist != Playlists.PREGAME;

        final String silenceURL = "https://www.youtube.com/watch?v=wu2djWZzmz0";
        String playlistURL = switch (playlist) {
            case Playlists.PREGAME ->
                    "https://youtube.com/playlist?list=PLK2pjaIEGMgujgT5mPQS3FIgJP2nBQZJY&si=d3p7CfbJ2V3z1Eno";
            case Playlists.RUSSIAN_HARDSTYLE ->
                    "https://youtube.com/playlist?list=PLK2pjaIEGMguc5OCoga9dTOP9K8XUCaKT&si=MvhGiNMUKGk_QgOd";
            case Playlists.NIGHTCORE ->
                    "https://youtube.com/playlist?list=PLK2pjaIEGMguW4bWzL41TJbZ3-VUsNuRK&si=FTs95fP8Jsqd_e0v";
            case Playlists.LEAGUE ->
                    "https://youtube.com/playlist?list=PLK2pjaIEGMgu08CLmMTEthaDupQod6NqA&si=FGq4MVTHbhVBGh5k";
            case EMPTY ->
                    silenceURL;
        };

        if (clearPrevious){
            PlayerManager.getINSTANCE().emptyQueue(event);
            PlayerManager.getINSTANCE().skip(event);
        }

        final AudioManager audioManager = event.getGuild().getAudioManager();
        final VoiceChannel voiceChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel().asVoiceChannel();
        audioManager.openAudioConnection(voiceChannel);
        if(!randomOrder)
            PlayerManager.getINSTANCE().loadAndPlay(playlistURL, false, false, true, event);
        else
            PlayerManager.getINSTANCE().loadAndPlay(playlistURL, false, true, true, event);


        if(playlist != Playlists.EMPTY) {
            if (clearPrevious)
                event.reply("Playlist playing").setEphemeral(true).queue();
            else
                event.reply("Playlist added to queue").setEphemeral(true).queue();
        }
        else
            event.reply("Please select a playlist from the options").setEphemeral(true).queue();
    }


}