package com.digitalchives.cyberbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    public final AudioPlayer audioPlayer;
    public final BlockingQueue<AudioTrack> queue;
    public boolean looping = false;

    public TrackScheduler(AudioPlayer audioPlayer){
        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingQueue<>();
    }

    public BlockingQueue<AudioTrack> getQueue(){
        return queue;
    }

    public void emptyQueue(){
        queue.clear();
    }

    public void removeTrack(int position) {
        int currentPosition = 1;
        BlockingQueue<AudioTrack> replacementQueue = new LinkedBlockingQueue<>();
        for (AudioTrack track : queue) {
            if(currentPosition != position)
                replacementQueue.offer(track);
            currentPosition++;
        }
        queue.clear();
        for (AudioTrack track : replacementQueue){
            queue.offer(track);
        }
    }

    public void shuffleQueue() {
        ArrayList<AudioTrack> queueArray = new ArrayList<>();

        for (AudioTrack track : queue) {
            queueArray.add(track);
        }
        Collections.shuffle(queueArray);
        queue.clear();
        for (AudioTrack track : queueArray){
            queue.offer(track);
        }
    }

    public AudioTrack getCurrentTrack(){
        return(audioPlayer.getPlayingTrack());
    }

    public void queue(AudioTrack track){
        if(!this.audioPlayer.startTrack(track, true)){
            this.queue.offer(track);
        }
    }

    public void pause(boolean pause){
        audioPlayer.setPaused(pause);
    }

    public void nextTrack(){
        this.audioPlayer.startTrack(this.queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(looping)
            this.queue.offer(track);
        if (endReason.mayStartNext)
            nextTrack();
    }

    public boolean getLooping(){
        return looping;
    }

    public void setLooping(boolean looping){
        this.looping = looping;
    }
}
