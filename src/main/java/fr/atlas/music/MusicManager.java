package fr.atlas.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MusicManager {

    private static final String URL_REGEX = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static final Pattern p = Pattern.compile(URL_REGEX);
    private final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    private final Map<String, MusicPlayer> players = new HashMap<>();

    private static final MusicManager musicManager = new MusicManager();

    public static MusicManager getMusicManager() {
        return musicManager;
    }

    public MusicManager() {
        AudioSourceManagers.registerRemoteSources(manager);
        AudioSourceManagers.registerLocalSource(manager);
    }


    public synchronized MusicPlayer getPlayer(Guild guild) {
        if (!players.containsKey(guild.getId()))
            players.put(guild.getId(), new MusicPlayer(manager.createPlayer(), guild));
        return players.get(guild.getId());
    }


    public void loadTrack(final TextChannel channel, String source, boolean random) {
        MusicPlayer player = getPlayer(channel.getGuild());

        if (source == null) {
            player.getListener().sendMessageSong(channel);
        } else {

            channel.getGuild().getAudioManager().setSendingHandler(player.getAudioHandler());

            manager.loadItemOrdered(player, p.matcher(source).find() ? (source) : ("ytsearch:" + source), new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    player.getListener().add(track);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    if (playlist.isSearchResult()) {
                        trackLoaded(playlist.getTracks().get(0));
                    } else {
                        EmbedBuilder eb = new EmbedBuilder().setColor(Color.red).setTitle("ajout de playlist").setDescription("ajout de " + playlist.getName());
                        channel.sendMessageEmbeds(eb.build()).queue();
                        List<AudioTrack> tracks = playlist.getTracks();
                        if (random)
                            Collections.shuffle(tracks);
                        for (AudioTrack track : tracks) {
                            player.getListener().add(track);
                        }
                    }
                    player.getListener().sendMessageSong(channel);
                }


                @Override
                public void noMatches() {
                    channel.sendMessage("La piste " + source + " n'a pas été trouvé.").queue();
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    channel.sendMessage("Impossible de jouer la piste (raison:" + exception.getMessage() + ")").queue();
                }


            });
        }
    }
}

