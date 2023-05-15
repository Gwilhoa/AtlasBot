package fr.atlas.command;

import com.jcraft.jsch.JSchException;
import fr.atlas.Request.User;
import fr.atlas.Request.Waifu;
import fr.atlas.Request.WaifuMembers;
import fr.atlas.builder.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Waifus {
    @Command(name = "harem", description = "Donne les Waifus attrapé par une personne", type = Command.ExecutorType.USER)
    private void harem(Message msg) throws IOException {
        User mem = User.getMember(msg.getMember());
        List<WaifuMembers> waifuMembers = mem.getWaifuMembers();
        System.out.println(waifuMembers);
        ArrayList<MessageEmbed> embeds = new ArrayList<>();
        int i = 0;
        if (waifuMembers.size() == 0)
        {
            msg.getChannel().sendMessage("vous n'avez pas de waifu").queue();
            return;
        }
        while (i < 5 && i < waifuMembers.size())
        {
            embeds.add(waifuMembers.get(i).getEmbed().build());
            i++;
        }
        Button button = Button.primary("harem;next;0;"+mem.getId(), "page suivante");
        msg.getChannel().sendMessageEmbeds(embeds).setActionRow(button).queue();
    }

    @Command(name = "waifu", description = "Instance des Waifus", type = Command.ExecutorType.USER)
    private void waifu(Message msg) throws ExecutionException, InterruptedException, IOException, JSchException {
        String args[] = msg.getContentRaw().split(" ");
        if (args.length == 1)
        {
            WaifuMembers w = null;
            try {
                w = User.catchwaifu(msg.getMember());
            } catch (IOException error) {
                try {
                    long waitingTime = Long.parseLong(error.getMessage()) - System.currentTimeMillis();
                    msg.getChannel().sendMessage("vous devez attendre " + waitingTime/1000 + " secondes avant de pouvoir récuperer une nouvelle waifu").queue();
                } catch (NumberFormatException e) {
                    msg.getChannel().sendMessage(error.getMessage()).queue();
                }
                return;
            }
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor(msg.getMember().getEffectiveName(), null, msg.getMember().getUser().getAvatarUrl());
            embedBuilder.setTitle("Nouvelle Waifu ! : " + w.getWaifu().getName() + " Debug mode : ID " + w.getWaifu().getId());
            embedBuilder.setDescription(w.getWaifu().getDescription());
            embedBuilder.setThumbnail(w.getWaifu().getImageurl());
            embedBuilder.setColor(w.getColor());
            embedBuilder.setFooter("Origine de " + w.getWaifu().getOrigin());
            msg.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        }
        else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("list")) {
                ArrayList<MessageEmbed> embeds = new ArrayList<>();
                List<Waifu> waifus = Waifu.getWaifus();
                int i = 0;
                while (i < 5 && i < waifus.size()) {
                    embeds.add(waifus.get(i).getEmbed().build());
                    i++;
                }
                net.dv8tion.jda.api.interactions.components.buttons.Button button = net.dv8tion.jda.api.interactions.components.buttons.Button.primary("waifu;next;0", "Suivant");
                msg.getChannel().sendMessageEmbeds(embeds).setActionRow(button).queue();
            }
        }
        else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("id")) {
                Waifu waifu = null;
                try {
                    waifu = Waifu.getWaifuById(Integer.parseInt(args[2]));
                } catch (NumberFormatException e) {
                    msg.getChannel().sendMessage("L'ID doit être un nombre").queue();
                    return;
                } catch (IOException e) {
                    if (e.getMessage().equals("null response"))
                        msg.getChannel().sendMessage("Cette waifu n'existe pas").queue();
                    else
                        msg.getChannel().sendMessage("Une erreur est survenue").queue();
                    return;
                }
                if (waifu == null) {
                    msg.getChannel().sendMessage("Cette waifu n'existe pas").queue();
                    return;
                }
                msg.getChannel().sendMessageEmbeds(waifu.getEmbed().build()).queue();
            }
            if (args[1].equalsIgnoreCase("search")) {
                ArrayList<MessageEmbed> embeds = new ArrayList<>();
                List<Waifu> waifus = Waifu.getWaifuByName(args[2]);
                System.out.println(waifus);
                if (waifus.isEmpty())
                {
                    msg.getChannel().sendMessage("Aucune waifu ne correspond à votre recherche").queue();
                    return;
                }
                int i = 0;
                while (i < 5 && i < waifus.size()) {
                    embeds.add(waifus.get(i).getEmbed().build());
                    i++;
                }
                msg.getChannel().sendMessageEmbeds(embeds).queue();
            }
        } else {
            msg.getChannel().sendMessage("mauvaise utilisation de la commande\n>waifu [list|id|search] [id|name]").queue();
        }
    }
}
