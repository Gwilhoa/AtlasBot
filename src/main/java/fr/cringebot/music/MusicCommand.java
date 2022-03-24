package fr.cringebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerOptions;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.builder.Command.ExecutorType;
import fr.cringebot.cringe.builder.CommandMap;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Queue;

public class MusicCommand {

    private final MusicManager manager = new MusicManager();
    @Command(name="volume",type = ExecutorType.USER, description = "changer le volume")
    private void volume(Guild guild, TextChannel textChannel, String[] args){
        if (args[0].equalsIgnoreCase("reset"))
            volume(guild, textChannel, 50);
        else
            volume(guild, textChannel, Integer.parseInt(args[0]));
    }

    private void volume(Guild guild, TextChannel textChannel, Integer vol){
        vol = (vol/5);
        try {
            Field f = DefaultAudioPlayer.class.getDeclaredField("options");
            f.setAccessible(true);
            ((AudioPlayerOptions) f.get(manager.getPlayer(guild).getAudioPlayer())).volumeLevel.set(vol);
            if (textChannel != null)
                textChannel.sendMessage("mise du volume à " + vol*5).queue();
        } catch (IndexOutOfBoundsException | NumberFormatException ignored) {
            textChannel.sendMessage("Mauvais usage de la commande ! il faut mettre par exemple : volume 500").queue();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace(); //ca peut arriver en cas de mise a jour de dépendances, mais ca devrait être bon sinon
        }
    }

    @Command(name="joue",type=ExecutorType.USER, description = "ajoute une musique a la playlist")
    private void joue(Guild guild, TextChannel textChannel, User user, Message msg)
    {
        play(guild, textChannel, user, msg);
    }

    @Command(name="play",type=ExecutorType.USER, description = "ajoute une musique a la playlist")
    private void play(Guild guild, TextChannel textChannel, User user, Message msg) {

        if(guild == null) return;
        if (!guild.getAudioManager().isConnected()) {
            AudioChannel voiceChannel = guild.getMember(user).getVoiceState().getChannel();
            if (voiceChannel == null) {
                textChannel.sendMessage("Vous devez être connecté à un salon vocal.").queue();
                return;
            }
            guild.getAudioManager().openAudioConnection(voiceChannel);
            volume(guild, null, 50);
        }
        manager.loadTrack(textChannel, msg.getContentRaw().replaceFirst(CommandMap.getTag(),"").replaceFirst("play ", ""));
    }


    @Command(name="skip",type=ExecutorType.USER, description = "musique suivante")
    private void skip(Guild guild, TextChannel textChannel){
        if(!guild.getAudioManager().isConnected() && !guild.getAudioManager().isConnected()){
            textChannel.sendMessage("error").queue();
            return;
        }
        manager.getPlayer(guild).skipTrack(textChannel);
    }


    @Command(name="queue",type = ExecutorType.USER,description = "montre la liste en cours")
    private void queue(TextChannel tc){
        MusicPlayer player = manager.getPlayer(tc.getGuild());
        Queue<AudioTrack> at = player.getListener().getTracks();
        AudioTrack[] track = at.toArray(new AudioTrack[]{});

        EmbedBuilder eb = new EmbedBuilder().setColor(Color.cyan).setTitle("les prochaines musique...");
        if (at.isEmpty()){
            eb.setDescription("Il n'y a pas de musique pour la suite");
        }
        else {
            System.out.println(track.length);
            int i = 0;
            while (i < 10 && i < track.length) {
                eb.appendDescription(track[i].getInfo().title + "\n");
                i++;
            }
        }
        tc.sendMessage(player.getListener().getCurrent().getInfo().title).setEmbeds(eb.build()).queue();
    }


    @Command(name="clear",type=ExecutorType.USER)
    private void clear(TextChannel textChannel){
        MusicPlayer player = manager.getPlayer(textChannel.getGuild());

        if(player.getListener().getTracks().isEmpty()){
            textChannel.sendMessage("Il n'y a pas de piste dans la liste d'attente.").queue();
            return;
        }

        player.getListener().clear();
        textChannel.sendMessage("La liste d'attente à été vidé.").queue();
    }
    @Command(name = "random", type = ExecutorType.USER, description = "mélange les futurs musique")
    private void random(TextChannel textChannel){
        if (manager.getPlayer(textChannel.getGuild()).getListener().getTracks().size() > 1) {
            manager.getPlayer(textChannel.getGuild()).getListener().randomise();
            textChannel.sendMessage("playlist mélangé").queue();
            return;
        }
        textChannel.sendMessage("pas assez de morceau pour mélanger").queue();
    }

    public static void stop(Guild guild)
    {
        new MusicManager().getPlayer(guild).getListener().stop();
    }
    @Command(name = "stop",type = ExecutorType.USER, description = "arrete la musique")
    private void stop(Message msg, Guild guild){
        manager.getPlayer(guild).getListener().stop();
    }

    @Command(name = "nowplaying", type = ExecutorType.USER, description = "néttoie la playlist pour mettre la prochaine musique")
    private void np(Message msg, Guild guild){
        manager.getPlayer(guild).getListener().nowplaying(msg.getTextChannel(), msg.getContentRaw().replaceFirst(CommandMap.getTag(),"").replaceFirst("nowplaying ", ""),manager);
    }

    @Command(name = "loop", type = ExecutorType.USER,description = "met en boucle la première musique")
    private void loop(Message msg, Guild guild){
        manager.getPlayer(guild).getListener().nowLoop(msg.getTextChannel());
    }
}