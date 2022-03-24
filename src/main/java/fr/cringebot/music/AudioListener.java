package fr.cringebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import fr.cringebot.cringe.objects.activity;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.lang.Thread.sleep;

public class AudioListener extends AudioEventAdapter {

    private final Queue<AudioTrack> tracks2 = new LinkedList<>();
    private final MusicPlayer player;
    private boolean loop = false;
    private Queue<AudioTrack> tracks = new LinkedList<>();
    private AudioTrack current = null;


    public AudioListener(MusicPlayer player) {
        this.player = player;
    }

    public AudioTrack getCurrent() {
        return current;
    }

    public Queue<AudioTrack> getTracks() {
        return tracks;
    }

    public int getTrackSize() {
        return tracks.size();
    }

    public void clear() {
        tracks.clear();
    }

    public void stop() {
        this.clear();
        if (this.loop)
            this.nowLoop(null);
        current = null;
        player.getGuild().getAudioManager().closeAudioConnection();
        player.getJda().getPresence().setActivity(new activity(", si tu lis ça tu es cringe", null, Activity.ActivityType.LISTENING));
    }

    public void randomise() {
        if (!tracks.isEmpty() || (current != null && current.getState() != AudioTrackState.FINISHED && current.getPosition() != current.getDuration())) {
            Collections.shuffle((List<?>) tracks);
        }
    }

    public void nowLoop(TextChannel tc) {
        EmbedBuilder eb = new EmbedBuilder();
        this.loop = !this.loop;
        if (this.loop) {
            if (this.current != null)
                eb.setColor(Color.green).setDescription("la musique " + this.current.getInfo().title + " est désormais mis en boucle").setTitle("loop Activé");
            else
                eb.setColor(Color.red).setDescription("mais tu veux loop quoi salope met une musique et on en reparle").setTitle("t'es une merde");
        }
        else
        {
            if (tracks.peek() == null)
                eb.setColor(Color.red).setDescription("il n'y aura pas de musique suivante\n");
            else
                eb.setColor(Color.red).setDescription("la playlist reprend son cours la prochaine musique est :\n" + tracks.peek().getInfo().title);
            eb.setTitle("loop désactivé");
        }
        tc.sendMessageEmbeds(eb.build()).queue();
    }

    public void nowplaying(TextChannel tc, String source, MusicManager mn) {
        Queue<AudioTrack> t = this.tracks;
        this.clear();
        mn.loadTrack(tc, source);
        this.tracks = t;
    }

    public void nextTrack(TextChannel textChannel) {
        current = null;
        if (this.loop)
            nowLoop(textChannel);
        if (tracks.isEmpty()) {
            player.getAudioPlayer().stopTrack();
            new Thread(() -> {
                StopMusic();
                try {
                    sleep(60000);
                    if (current == null) {
                        this.stop();
                    }
                } catch (InterruptedException ignored) {
                }
            }).start();
            textChannel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.red).setTitle("fini !").setDescription("j'ai plus de musique a mettre,\n si vous avez rien a mettre moi je m'en vais").build()).queue();
        } else {
            current = tracks.poll();
            if (textChannel != null) {
                EmbedBuilder eb = new EmbedBuilder().setColor(Color.green).setTitle("lancement de musique").setDescription("la musique : " + current.getInfo().title + "\n de " + current.getInfo().author);
                textChannel.sendMessageEmbeds(eb.build()).queue();
            }
            player.getJda().getPresence().setActivity(new activity(this.current.getInfo().title, null, Activity.ActivityType.LISTENING));
            player.getAudioPlayer().startTrack(current, false);
        }
    }

    private void StopMusic() {
        player.getJda().getPresence().setActivity(new activity(" plus rien", null, Activity.ActivityType.LISTENING));
    }


    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (this.loop) {
            player.startTrack(current, false);
            return;
        }
        current = null;
        new Thread(() -> {
            StopMusic();
            try {
                sleep(60000);
                if (current == null) {
                    this.stop();
                }
            } catch (InterruptedException ignored) {
            }
        }).start();
        if (endReason.mayStartNext) nextTrack(null);
    }

    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        current = track.makeClone();
    }

    public void add(AudioTrack t) {
        if (current == null || current.getState() == AudioTrackState.FINISHED || current.getPosition() == current.getDuration()) {
            player.getAudioPlayer().playTrack(t);
            player.getJda().getPresence().setActivity(new activity(this.current.getInfo().title, null, Activity.ActivityType.LISTENING));
        } else {
            tracks.add(t);
        }
    }

}