package fr.atlas.command;

import fr.atlas.builder.Command;
import fr.atlas.builder.Command.ExecutorType;
import fr.atlas.music.MusicManager;
import fr.atlas.music.MusicPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

import java.awt.*;


public class Music {


    public Music() {
    }

    @Command(name="joue",type=ExecutorType.USER, description = "Instance principale de la musique")
    private void joue(Guild guild, TextChannel textChannel, User user, Message msg)
    {
        play(guild, textChannel, user, msg, false);
    }

    @Command(name="play",type=ExecutorType.USER, description = "Instance principale de la musique")
    private void play(Guild guild, TextChannel textChannel, User user, Message msg)
    {
        play(guild, textChannel, user, msg, false);
    }

    @Command(name = "playmerde", type = ExecutorType.USER, description = "ajoute la playlist pourrie")
    private void playmerde(Guild guild, TextChannel textChannel, User user, Message msg) {
        play(guild, textChannel, user,"https://www.youtube.com/playlist?list=PLXNtHjjUh7HwLdiCIwSrGiiTAv95GTBjy" , true);
    }

    @Command(name = "jouemerde", type = ExecutorType.USER, description = "ajoute la playlist pourrie")
    private void jouemerde(Guild guild, TextChannel textChannel, User user, Message msg) {
        play(guild, textChannel, user,"https://www.youtube.com/playlist?list=PLXNtHjjUh7HwLdiCIwSrGiiTAv95GTBjy" , true);
    }

    private void play(Guild guild, TextChannel textChannel, User user, Message msg, boolean random) {
        play(guild, textChannel, user, msg.getContentRaw(), random);
    }

    private void play(Guild guild, TextChannel textChannel, User user, String str, boolean random) {
        if (guild == null) return;
        if (!guild.getAudioManager().isConnected()) {
            AudioChannel voiceChannel = guild.getMember(user).getVoiceState().getChannel();
            if (voiceChannel == null) {
                textChannel.sendMessageEmbeds(new EmbedBuilder().setTitle("Error").setDescription("tu dois etre connecté a un salon vocal").setColor(Color.red).build()).queue();
                return;
            }
            guild.getAudioManager().openAudioConnection(voiceChannel);
            MusicManager.getMusicManager().getPlayer(guild).getListener().setvolume(50);
        }
        if (str.split(" ").length == 1 && str.split(" ")[0].equalsIgnoreCase(">play"))
            MusicManager.getMusicManager().loadTrack(textChannel, null, random);
        else {
            str = str.replace(">play ", "");
            MusicManager.getMusicManager().loadTrack(textChannel, str, random);
        }
    }


    @Command(name="rewind", type = ExecutorType.USER, description = "et si on rembobinait ?")
    private void rewind(TextChannel tc)
    {
        if (MusicManager.getMusicManager().getPlayer(tc.getGuild()).getListener().getCurrent() == null)
        {
            tc.sendMessage("bah non tu peux pas rewind le vide").queue();
            return;
        }
        MusicManager.getMusicManager().getPlayer(tc.getGuild()).getListener().getCurrent().setPosition(0);
        tc.sendMessage("han han it's rewind time").queue();
    }

    @Command(name = "random", type = ExecutorType.USER, description = "mélange la playlist, si c'est une playlist donner en argument ça la mélange avant de la lancer")
    private void random(TextChannel textChannel, Message msg, Guild guild){
        if (msg.getContentRaw().split(" ").length > 1) {
            play(guild, textChannel, msg.getMember().getUser(), msg, true);
        }
        else if (MusicManager.getMusicManager().getPlayer(textChannel.getGuild()).getListener().getTracks().size() > 1) {
            MusicManager.getMusicManager().getPlayer(textChannel.getGuild()).getListener().randomise();
            textChannel.sendMessage("playlist mélangé").queue();
        }
        else
            textChannel.sendMessage("pas assez de morceau pour mélanger").queue();
    }
}
