package fr.atlas.command;

import fr.atlas.Request.Achievement;
import fr.atlas.Request.General;
import fr.atlas.Request.Members;
import fr.atlas.Request.Request;
import fr.atlas.builder.Command;
import fr.atlas.builder.CommandMap;
import fr.atlas.command.CommandBuilder.HelpCommand;
import fr.atlas.command.CommandBuilder.ProfilCommand;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static fr.atlas.Request.General.getSeason;
import static fr.atlas.objects.imgExtenders.resize;

public class Utils {
    private final CommandMap commandMap;
    public Utils(CommandMap commandMap) {
        this.commandMap = commandMap;
    }

    @Command(name = "profil", description = "information sur un joueur", type = Command.ExecutorType.USER)
    private void profil(Message msg) {
        Member member = msg.getMember();
        if (msg.getMentions().getMembers().size() > 0) {
            member = msg.getMentions().getMembers().get(0);
        }
        msg.replyEmbeds(ProfilCommand.CommandProfil(member).build()).queue();
    }

    @Command(name = "help", type = Command.ExecutorType.USER, description = "Affiche la liste des commandes.")
    private void help(User user, MessageChannel channel, Guild guild, Member mem){
        HelpCommand.help(user, channel, guild, mem, commandMap);
    }

    @Command(name = "clear", description = "Nettoyons tout ce bordel !", type = Command.ExecutorType.USER)
    private void clear(Message msg){
        if (msg.getContentRaw().split(" ").length > 1){
            new Thread(() -> {
                try {
                    try {
                        int nb = Integer.parseInt(msg.getContentRaw().split(" ")[1]);
                        if (nb > 100) {
                            msg.getChannel().sendMessage("Vous ne pouvez pas supprimer plus de 100 messages à la fois !").queue();
                            return;
                        }
                        MessageHistory history = msg.getChannel().getHistoryAfter(msg.getId(), nb).complete();
                        while (history.getRetrievedHistory().size() < nb) {
                            history.retrievePast(nb - history.getRetrievedHistory().size()).complete();
                        }
                        ArrayList<Message> msgs = new ArrayList<>(history.getRetrievedHistory());
                        int i = 0;
                        while (i < msgs.size()) {
                            if (i + 1 == msgs.size()) {
                                msgs.get(i).delete().queue();
                            }
                            else {
                                msgs.get(i).delete().and(msgs.get(i + 1).delete()).queue();
                            }
                            Thread.sleep(1000);
                        }
                    } catch (IndexOutOfBoundsException | NumberFormatException ignored) {
                        msg.getChannel().sendMessage("Mauvais usage de la commande ! il faut mettre par exemple : !clear 5").queue();
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                msg.delete().queue();
            }).start();
        }
        else {
            if (msg.getReferencedMessage() != null) {
                MessageHistory msgs = msg.getChannel().getHistoryAfter(msg.getMessageReference().getMessageId(),100).complete();
                while(msgs.getMessageById(msg.getId()) == null){
                    msgs.retrieveFuture(100).complete();
                }
                final int chunkSize = 100;
                final AtomicInteger counter = new AtomicInteger();
                for(List<Message> v : msgs.getRetrievedHistory().stream().dropWhile((m)-> !m.getId().equals(msg.getId())).collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize)).values()){
                    msg.getGuildChannel().deleteMessages(v).queue();
                }
            }
        }
    }

    @Command(name = "meteo", type = Command.ExecutorType.USER, description = "Affiche la météo d'une ville.")
    private void meteo(Guild guild, TextChannel textChannel, Message msg){
        String ville = "shrek";
        if (msg.getContentRaw().split(" ").length != 1)
            ville = msg.getContentRaw().split(" ")[1];
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            URLConnection connection = new URL("https://wttr.in/"+ville+"_3tqp_lang=fr.png").openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0");
            BufferedImage im = ImageIO.read(connection.getInputStream());
            im = resize(im, 1032*2, 546*2, 0, 0, true);
            im = resize(im, 1032*2, 546*2, 0, 0, false);
            ImageIO.write(im, "png", baos);
            File f = new File("meteo.png");
            ArrayList<FileUpload> files = new ArrayList<>();
            files.add(FileUpload.fromData(f));
            textChannel.sendFiles(files).queue();
        } catch (IOException e) {
            textChannel.sendMessage("météo introuvable").queue();
        }

    }

    @Command(name = "api", type = Command.ExecutorType.USER, description = "regarde la connexion à l'api")
    private void api(Message msg){
        if (Request.isOnline())
            msg.getChannel().sendMessage("L'api est en ligne !").queue();
        else
            msg.getChannel().sendMessage("L'api est hors ligne !").queue();
    }

}
