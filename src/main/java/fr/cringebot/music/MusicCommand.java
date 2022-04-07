package fr.cringebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerOptions;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.builder.Command.ExecutorType;
import fr.cringebot.cringe.builder.CommandMap;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Queue;

public class MusicCommand {

    public static final MusicCommand INSTANCE;
    private static final MusicManager manager = new MusicManager();

    static {
        INSTANCE = new MusicCommand();
    }

    // Ceci stoppe les "guigui" qui font plein d'instances
    private MusicCommand() {
    }


    @Command(name = "volume", type = ExecutorType.USER, description = "changer le volume")
    private void volume(Guild guild, TextChannel textChannel, String[] args) {
        if (args.length == 0)
            volume(guild, textChannel, -1);
        else if (args[0].equalsIgnoreCase("reset"))
            volume(guild, textChannel, 50);
        else
            volume(guild, textChannel, Integer.parseInt(args[0]));
    }

    private static int volume(Guild guild, TextChannel textChannel, Integer vol) {
        MusicPlayer player = manager.getPlayer(guild);
        if (player.getListener().getCurrent() == null && textChannel != null) {
            textChannel.sendMessageEmbeds(new EmbedBuilder().setDescription("not playing").setColor(Color.red).build()).queue();
            return -1;
        }
        Field f = null;
        try {
            f = DefaultAudioPlayer.class.getDeclaredField("options");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (vol >= 0)
        {
            try {
                    vol = (vol/5);
                    ((AudioPlayerOptions) f.get(manager.getPlayer(guild).getAudioPlayer())).volumeLevel.set(vol);
            } catch (IndexOutOfBoundsException | NumberFormatException ignored) {
                textChannel.sendMessage("Mauvais usage de la commande ! il faut mettre par exemple : volume 500").queue();
            } catch (IllegalAccessException e) {
                e.printStackTrace(); //ca peut arriver en cas de mise a jour de dépendances, mais ca devrait être bon sinon
            }
        } else {
            try {
                vol = ((AudioPlayerOptions) f.get(manager.getPlayer(guild).getAudioPlayer())).volumeLevel.get();
            } catch (IndexOutOfBoundsException | NumberFormatException ignored) {
                textChannel.sendMessage("Mauvais usage de la commande ! il faut mettre par exemple : volume 500").queue();
            } catch (IllegalAccessException e) {
                e.printStackTrace(); //ca peut arriver en cas de mise a jour de dépendances, mais ca devrait être bon sinon
            }
        }
        if (textChannel != null)
        {
            EmbedBuilder eb = getVolumeEmbed(vol*5);
            Message ret = textChannel.sendMessageEmbeds(eb.build()).complete();
            ret.addReaction("\uD83D\uDD3C").and(ret.addReaction("\uD83D\uDD3D")).queue();
        }
        System.out.println(vol*5);
        return vol*5;
    }

    @Command(name="joue",type=ExecutorType.USER, description = "ajoute une musique a la playlist")
    private void joue(Guild guild, TextChannel textChannel, User user, Message msg)
    {
        play(guild, textChannel, user, msg);
    }

    @Command(name="play",type=ExecutorType.USER, description = "ajoute une musique a la playlist")
    private void play(Guild guild, TextChannel textChannel, User user, Message msg) {
        if (guild == null) return;
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
        EmbedBuilder eb = new EmbedBuilder().setColor(Color.cyan).setTitle("les prochaines musique...");
        if (at.isEmpty() && player.getListener().getCurrent() == null)
            eb.setDescription("Offline").setColor(Color.red);
        else if (at.isEmpty())
            eb.setDescription("il n'y a pas de musique pour la suite").setColor(Color.BLUE);
        else {
            AudioTrack[] track = at.toArray(new AudioTrack[]{});
            int i = 0;
            while (i < 10 && i < track.length) {
                eb.appendDescription(track[i].getInfo().title + "\n");
                i++;
            }
        }
        if (player.getListener().getCurrent() != null)
            tc.sendMessage(player.getListener().getCurrent().getInfo().title).setEmbeds(eb.build()).queue();
        else
            tc.sendMessageEmbeds(eb.build()).queue();
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

    //--//

    public static EmbedBuilder getVolumeEmbed(Integer vol) {
        EmbedBuilder eb = new EmbedBuilder();
        if ((vol > 100)&&(vol < 199))
            eb.setColor(Color.ORANGE);
        else if (vol > 200)
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
        while (i < 100)
        {
            eb.appendDescription(" I ");
            i = i + 10;
        }
        eb.appendDescription("|");
        eb.setFooter("MusicManager");
        return eb;
    }

    public static void onAddReact(MessageReactionAddEvent event) {
        if (event.getMember().getUser().isBot())
            return;
        Message msg = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        if (!msg.getEmbeds().isEmpty() && msg.getEmbeds().get(0).getFooter().getText().equals("MusicManager")) {
            if (!event.getGuild().getAudioManager().isConnected()) {
                msg.editMessageEmbeds(new EmbedBuilder().setDescription("not playing").setColor(Color.red).build()).complete().clearReactions().queue();
            }
            else if (msg.getEmbeds().get(0).getTitle().equals("Volume")) {
                System.out.println(event.getReaction().getReactionEmote().getAsCodepoints());
                if (event.getReaction().getReactionEmote().getAsCodepoints().equals("U+1f53c")) {
                    int vol = Integer.parseInt(msg.getEmbeds().get(0).getDescription().replace("%", "").split("\n")[0]);
                    vol = volume(msg.getGuild(), null, vol + 10);
                    msg.editMessageEmbeds(getVolumeEmbed(vol).build()).queue();
                }
                if (event.getReaction().getReactionEmote().getAsCodepoints().equals("U+1f53d")) {
                    int vol = Integer.parseInt(msg.getEmbeds().get(0).getDescription().replace("%", "").split("\n")[0]);
                    vol = volume(msg.getGuild(), null, vol - 10);
                    msg.editMessageEmbeds(getVolumeEmbed(vol).build()).queue();
                }
            }
        }
    }
}
