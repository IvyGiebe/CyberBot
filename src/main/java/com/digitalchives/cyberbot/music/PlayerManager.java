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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void loadAndPlay(TextChannel textChannel, String trackURL, SlashCommandInteractionEvent event){
        final MusicManager musicManager = this.getMusicManager(textChannel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.trackScheduler.queue(track);
                event.reply("Adding to queue **'" + track.getInfo().title + "'**").setEphemeral(true).queue();
//                textChannel.sendMessage("Adding to queue **'" + track.getInfo().title + "'** by **'" + track.getInfo().author + "'**").queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();
                if(!tracks.isEmpty()){
                    musicManager.trackScheduler.queue(tracks.get(0));
                    event.reply("Adding to queue **'" + tracks.get(0).getInfo().title + "'**").setEphemeral(true).queue();
//                    textChannel.sendMessage("Adding to queue **'" + tracks.get(0).getInfo().title + "'** by **'" + tracks.get(0).getInfo().author + "'**").queue();
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {

            }
        });

    }

    public void pause(TextChannel textChannel){
        final MusicManager musicManager = this.getMusicManager(textChannel.getGuild());

        musicManager.trackScheduler.pause(true);
        //textChannel.sendMessage("Music is paused. Use /unpause to resume playing.").queue();
    }

    public void unpause(TextChannel textChannel){
        final MusicManager musicManager = this.getMusicManager(textChannel.getGuild());

        musicManager.trackScheduler.pause(false);
        //textChannel.sendMessage("Play Resuming").queue();
    }

    public void skip(TextChannel textChannel) {
        final MusicManager musicManager = this.getMusicManager(textChannel.getGuild());

        musicManager.trackScheduler.nextTrack();
    }

    public static PlayerManager getINSTANCE(){

        if(INSTANCE == null)
            INSTANCE = new PlayerManager();

        return INSTANCE;
    }
}
