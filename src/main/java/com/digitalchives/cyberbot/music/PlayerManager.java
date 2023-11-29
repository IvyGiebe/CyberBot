package com.digitalchives.cyberbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import javax.sound.midi.Track;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class PlayerManager {

    private static PlayerManager INSTANCE;
    private final Map<Long, MusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager(){
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public MusicManager getMusicManager(Guild guild){
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final MusicManager musicManager = new MusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
            return musicManager;
        });
    }

    public void loadAndPlay(String trackURL, boolean isSearch, SlashCommandInteractionEvent event){
        loadAndPlay(trackURL, isSearch, false, false, event);

    }

    public void loadAndPlay(String trackURL, boolean isSearch, boolean shufflePlaylist, boolean silent, SlashCommandInteractionEvent event){
        final MusicManager musicManager = this.getMusicManager(event.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.trackScheduler.queue(track);
                if(!silent)
                    event.reply("Adding to queue **'" + track.getInfo().title + "'**").setEphemeral(true).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();
                final String playlistName = playlist.getName();
                if(!tracks.isEmpty()){
                    if(shufflePlaylist){
                        Collections.shuffle(tracks);
                    }
                    if(!isSearch) {
                        for (AudioTrack track : tracks) {
                            musicManager.trackScheduler.queue(track);
                        }
                        if(!silent)
                            event.reply("Added to queue **'" + playlistName + "'** playlist").setEphemeral(true).queue();
                            //event.reply("Added to queue **'" + playlistName + "'** playlist").setEphemeral(false).queue();
                    }
                    else {
                        musicManager.trackScheduler.queue(tracks.get(0));
                        if(!silent)
                            event.reply("Adding to queue **'" + tracks.get(0).getInfo().title + "'**").setEphemeral(true).queue();
                            //event.reply("Added to queue **'" + playlistName + "'** playlist").setEphemeral(false).queue();
                    }
                }
            }
            //these are needed for the AudioLoadResultHandler
            @Override
            public void noMatches() {}
            @Override
            public void loadFailed(FriendlyException exception) {}
        });

    }

    public boolean finishedLoadAndPlay(String trackURL, boolean isSearch, SlashCommandInteractionEvent event, Boolean silent){
        final MusicManager musicManager = this.getMusicManager(event.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.trackScheduler.queue(track);
                if(!silent)
                    event.reply("Adding to queue **'" + track.getInfo().title + "'**").setEphemeral(true).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();
                final String playlistName = playlist.getName();
                if(!tracks.isEmpty()){
                    if(!isSearch) {
                        for (AudioTrack track : tracks) {
                            musicManager.trackScheduler.queue(track);
                        }
                        if(!silent)
                            event.reply("Added to queue **'" + playlistName + "'** playlist").setEphemeral(true).queue();
                        //event.reply("Added to queue **'" + playlistName + "'** playlist").setEphemeral(false).queue();
                    }
                    else {
                        musicManager.trackScheduler.queue(tracks.get(0));
                        if(!silent)
                            event.reply("Adding to queue **'" + tracks.get(0).getInfo().title + "'**").setEphemeral(true).queue();
                        //event.reply("Added to queue **'" + playlistName + "'** playlist").setEphemeral(false).queue();
                    }
                }
            }
            //these are needed for the AudioLoadResultHandler
            @Override
            public void noMatches() {}
            @Override
            public void loadFailed(FriendlyException exception) {}
        });

        return true;

    }



    public void pause(boolean pause, SlashCommandInteractionEvent event){
        final MusicManager musicManager = this.getMusicManager(event.getGuild());
        musicManager.trackScheduler.pause(pause);
    }

    public void skip(SlashCommandInteractionEvent event) {
        final MusicManager musicManager = this.getMusicManager(event.getGuild());
        musicManager.trackScheduler.nextTrack();
    }

    public void loop(SlashCommandInteractionEvent event){
        final MusicManager musicManager = this.getMusicManager(event.getGuild());
        musicManager.trackScheduler.setLooping(!musicManager.trackScheduler.getLooping());
        if(musicManager.trackScheduler.getLooping())
            event.reply("Looping the queue").setEphemeral(true).queue();
        else
            event.reply("Stopped looping the queue").setEphemeral(true).queue();
    }

    public AudioTrack nowPlaying(SlashCommandInteractionEvent event){
        final MusicManager musicManager = this.getMusicManager(event.getGuild());
        return musicManager.trackScheduler.getCurrentTrack();
    }

    public ArrayList<AudioTrack> queue(SlashCommandInteractionEvent event){
        final MusicManager musicManager = this.getMusicManager(event.getGuild());

        ArrayList<AudioTrack> queueList = new ArrayList<>();

        queueList.addAll(musicManager.trackScheduler.getQueue());

        return queueList;
    }

    public void emptyQueue(SlashCommandInteractionEvent event) {
        final MusicManager musicManager = this.getMusicManager(event.getGuild());

        musicManager.trackScheduler.emptyQueue();
    }

    public void removeTrack(int position, SlashCommandInteractionEvent event){
        final MusicManager musicManager = this.getMusicManager(event.getGuild());

        musicManager.trackScheduler.removeTrack(position);
    }

    public void shuffleQueue(SlashCommandInteractionEvent event) {
        final MusicManager musicManager = this.getMusicManager(event.getGuild());

        musicManager.trackScheduler.shuffleQueue();
    }

    public static PlayerManager getINSTANCE(){

        if(INSTANCE == null)
            INSTANCE = new PlayerManager();

        return INSTANCE;
    }
}
