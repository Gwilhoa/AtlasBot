package fr.atlas.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import fr.atlas.BotDiscord;
import fr.atlas.objects.activity;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.*;
import java.util.List;

import static fr.atlas.BotDiscord.setError;
import static java.lang.Thread.sleep;
import static net.dv8tion.jda.api.interactions.components.buttons.Button.primary;

public class AudioListener extends AudioEventAdapter {
    private static final ArrayList<Message> SongMessage = new ArrayList<>();

    private final Queue<AudioTrack> loopedplaylist = new LinkedList<>();
    private final MusicPlayer player;
    private boolean loop = false;
    private Queue<AudioTrack> tracks = new LinkedList<>();
    private AudioTrack current = null;
    private AudioTrack actual = null;

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (this.loop) {
            player.startTrack(actual, false);
            return;
        }
        current = null;
        new Thread(() -> {
            resetActivity();
            try {
                sleep(60000);
                if (current == null) {
                    this.stop();
                }
            } catch (InterruptedException ignored) {
            }
        }).start();
        if (endReason.mayStartNext) nextTrack();
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        exception.printStackTrace();
    }

    public void addSongMessage(Message msg) {
        SongMessage.add(msg);
    }


    public void clearSongMessage() {
        for (Message msg : SongMessage) {
            try {
                msg.editMessageEmbeds(new EmbedBuilder().setTitle("B2KMusique").setDescription("disconnected").setColor(Color.red).build()).setActionRow(primary("nothing", "arrêté").asDisabled()).queue();
            } catch (Exception e) {
                setError(e);
            }
        }
        SongMessage.clear();
    }

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
            this.nowLoop();
        clearSongMessage();
        current = null;
        player.getGuild().getAudioManager().closeAudioConnection();
        player.getJda().getPresence().setActivity(new activity(BotDiscord.activity, null, Activity.ActivityType.PLAYING));
    }

    public void randomise() {
        if (!tracks.isEmpty() || (current != null && current.getState() != AudioTrackState.FINISHED && current.getPosition() != current.getDuration())) {
            Collections.shuffle((List<?>) tracks);
        }
    }

    public boolean isLoop() {
        return loop;
    }

    public void nowLoop() {
        this.loop = !this.loop;
    }


    public void nextTrack() {
        current = null;
        if (tracks.isEmpty()) {
            player.getAudioPlayer().stopTrack();
            new Thread(() -> {
                resetActivity();
                try {
                    sleep(60000);
                    if (current == null) {
                        this.stop();
                    }
                } catch (InterruptedException ignored) {
                }
            }).start();
        } else {
            current = tracks.poll();
            player.getJda().getPresence().setActivity(new activity(this.current.getInfo().title, null, Activity.ActivityType.LISTENING));
            player.getAudioPlayer().startTrack(current, false);
        }
    }

    private void resetActivity() {
        player.getJda().getPresence().setActivity(new activity(BotDiscord.activity, null, Activity.ActivityType.PLAYING));
    }


    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        current = track;
        actual = track.makeClone();
    }

    public int add(AudioTrack t) {
        if (current == null || current.getState() == AudioTrackState.FINISHED || current.getPosition() == current.getDuration()) {
            player.getAudioPlayer().playTrack(t);
            player.getJda().getPresence().setActivity(new activity(this.current.getInfo().title, null, Activity.ActivityType.LISTENING));
            return 1;
        } else {
            tracks.add(t);
            return 0;
        }
    }

    public void setvolume(int vol)
    {
        player.getAudioPlayer().setVolume(vol/5);
    }

    public void addvolume(int vol)
    {
        int newvol = player.getAudioPlayer().getVolume() + vol/5;
        player.getAudioPlayer().setVolume(newvol);
    }
    public EmbedBuilder getVolumeEmbed()
    {
        int vol = player.getAudioPlayer().getVolume() * 5;
        EmbedBuilder eb = new EmbedBuilder();
        if ((vol >= 100) && (vol < 199))
            eb.setColor(Color.ORANGE);
        else if (vol >= 200)
            eb.setColor(Color.RED);
        else
            eb.setColor(Color.GREEN);
        eb.setTitle("Volume");
        int i = 0;
        eb.setDescription("\n" + vol + "%\n\n");
        if (vol > 10) {
            eb.appendDescription("||");
            while (i < vol) {
                eb.appendDescription(" . ");
                i = i + 10;
            }
            eb.appendDescription("||");
        }
        while (i < 100) {
            eb.appendDescription(" I ");
            i = i + 10;
        }
        eb.appendDescription("|");
        eb.setFooter("MusicManager");
        return eb;
    }
    private EmbedBuilder getEmbed()
    {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.green).setTitle("B2KMusique");
        if (current != null)
            eb.addField("Musique en cours", current.getInfo().title, false).setThumbnail("https://i.ytimg.com/vi/" + current.getIdentifier() + "/maxresdefault.jpg");
        else
            eb.addField("Musique en cours", "aucune", false);
        if (isLoop()) {
            eb.addField("Musique suivante", "\uD83D\uDD03", false);
        }
        else if (tracks.size() > 0) {
            AudioTrack[] tracks = this.tracks.toArray(new AudioTrack[0]);
            eb.addField("Musique suivante", tracks[0].getInfo().title, false);
        }
        else {
            eb.addField("Musique suivante", "aucune", false);
        }
        eb.addField("volume", String.valueOf(player.getAudioPlayer().getVolume() * 5), false);
        return eb;
    }
    public void sendMessageSong(TextChannel c) {
        addSongMessage(c.sendMessageEmbeds(getEmbed().build()).setActionRow(getButtons()).complete());
    }

    public void editMessageSong(Message msg)
    {
        msg.editMessageEmbeds(getEmbed().build()).setActionRow(getButtons()).queue();
    }

    public Collection<? extends ItemComponent> getButtons() {
        ArrayList<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(primary("zik;stop", "⏹"));
        if (tracks.size() > 0)
            buttons.add(primary("zik;next", "⏭"));
        else
            buttons.add(primary("zik;next", "⏭").asDisabled());
        buttons.add(primary("zik;loop", "🔁"));
        buttons.add(primary("zik;volume", "🔊"));
        if (tracks.size() > 2)
            buttons.add(primary("zik;lister", "📜"));
        else
            buttons.add(primary("zik;lister", "📜").asDisabled());
        return (buttons);
    }

    public ArrayList<Button> getVolumeButtons() {
        ArrayList<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(primary("zik;volume;-", "-10"));
        buttons.add(primary("zik;volume;reset", "par défaut"));
        buttons.add(primary("zik;volume;+", "+10"));
        buttons.add(primary("zik;volume;quit", "quitter"));
        return (buttons);
    }
}