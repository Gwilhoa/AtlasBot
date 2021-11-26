package fr.cringebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import javax.sound.midi.Track;

public class MusicManager {

    private boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private static final String URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static final Pattern p = Pattern.compile(URL_REGEX);
    private final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    private final Map<String, MusicPlayer> players = new HashMap<>();

    public MusicManager() {
        AudioSourceManagers.registerRemoteSources(manager);
        AudioSourceManagers.registerLocalSource(manager);
    }

    public synchronized MusicPlayer getPlayer(Guild guild) {
        if (!players.containsKey(guild.getId()))
            players.put(guild.getId(), new MusicPlayer(manager.createPlayer(), guild));
        return players.get(guild.getId());
    }

    public void loadTrack(final TextChannel channel, final String source){
        MusicPlayer player = getPlayer(channel.getGuild());

        channel.getGuild().getAudioManager().setSendingHandler(player.getAudioHandler());

        manager.loadItemOrdered(player, p.matcher(source).find() ? (source) : ("ytsearch:" + source), new AudioLoadResultHandler(){
                @Override
                public void trackLoaded(AudioTrack track) {
                    EmbedBuilder eb = new EmbedBuilder().setColor(Color.red).setTitle("ajout de titre").setDescription("ajout de "+track.getInfo().title+"\n de "+track.getInfo().author);
                    channel.sendMessageEmbeds(eb.build()).queue();
                    player.getListener().add(track);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    if (playlist.isSearchResult()){
                        trackLoaded(playlist.getTracks().get(0));
                        return;
                    }
                    EmbedBuilder eb = new EmbedBuilder().setColor(Color.red).setTitle("ajout de playlist").setDescription("ajout de "+playlist.getName());
                    channel.sendMessageEmbeds(eb.build()).queue();
                    final List<AudioTrack> tracks = playlist.getTracks();
                    for (final AudioTrack track : tracks){
                        player.getListener().add(track);
                    }
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
