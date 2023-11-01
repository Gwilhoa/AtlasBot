package fr.atlas.fonctionnalities.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class MusicPlayer {

    private final AudioPlayer audioPlayer;
    private final AudioListener listener;
    private final Guild guild;
    private final JDA jda;

    public MusicPlayer(AudioPlayer audioPlayer, Guild guild){
        this.audioPlayer = audioPlayer;
        this.guild = guild;
        this.jda = this.guild.getJDA();
        listener = new AudioListener(this);
        audioPlayer.addListener(listener);
    }

    public static MusicPlayer getMusicPlayer(Guild guild) {

        MusicPlayer player = MusicManager.getMusicManager().getPlayer(guild);
        return player;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public Guild getGuild() {
        return guild;
    }

    public AudioListener getListener() {
        return listener;
    }

    public JDA getJda() {
        return jda;
    }

    public AudioHandler getAudioHandler(){
        return new AudioHandler(audioPlayer);
    }

    public synchronized void playTrack(AudioTrack track){
        listener.add(track);
    }

    public synchronized void skipTrack(){
        listener.nextTrack();
    }
}