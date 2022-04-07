/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   ReactionEvent.java                                 :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 13:00:42 by gchatain          #+#    #+#             */
/*   Updated: 2022/04/07 22:21:53 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.event;

import fr.cringebot.cringe.objects.DetectorAttachment;
import fr.cringebot.cringe.objects.MessageConsumer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static fr.cringebot.cringe.objects.imgExtenders.getImage;
import static fr.cringebot.cringe.objects.imgExtenders.resize;

public class ReactionEvent {

    public static void sus(Message msg) {
        if (new Random().nextInt(100) < 95)
            msg.getChannel().sendMessage(". 　　　*   。　　　　•　 　ﾟ　　。 　　. °  ,  *\n" +
                    "\n" +
                    "　　　.　　　* 　　.　　　　　。　　 。　. 　.  °  ,  *\n" +
                    "\n" +
                    ".　　 。　　　　　 ඞ 。 . 　　 • 　　   °　　•   °  . \n" +
                    "\n" +
                    "　　ﾟ　　 " + msg.getMember().getAsMention() + " was not An Impostor.　 。　.\n" +
                    "\n" +
                    "　　'　　　 1 Impostor remains 　 　　。   . °  , \n" +
                    "\n" +
                    "　　ﾟ　　　.　　　. ,　　　　.　 .   *  °  . °  ").queue();
        else
            msg.getChannel().sendMessage(". 　　　*   。　　　　•　 　ﾟ　　。 　　. °  ,  *\n" +
                    "\n" +
                    "　　　.　　　* 　　.　　　　　。　　 。　. 　.  °  ,  *\n" +
                    "\n" +
                    ".　　 。　　　　　 ඞ 。 . 　　 • 　　   °　　•   °  . \n" +
                    "\n" +
                    "　　ﾟ　　 " + msg.getMember().getAsMention() + " was the Impostor.　 。　.\n" +
                    "\n" +
                    "　　'　　　*  ° .  *   。　　　 。　. 　. 。   . °  , \n" +
                    "\n" +
                    "　　ﾟ　　　.　　　. ,　　　　.　 .   *  °  . °  ").queue();
    }

    /**
     * quelqu'un veut rendre respect ? press f to respect
     *
     * @param msg
     */
    public static void pressf(Message msg) {
        String[] args = msg.getContentRaw().split(" ");
        if (msg.getContentRaw().length() < 60) {
            if (args.length != 1) {
                if ((msg.getContentRaw().contains("@here") || msg.getContentRaw().contains("@everyone"))) {
                    msg.getChannel().sendMessage("tu as rendu respect a tout le monde").queue();
                    return;
                }
                List<Member> mbrs = msg.getGuild().getMembers();
                for (Member m : mbrs) {
                    if (args[1].equalsIgnoreCase(m.getUser().getName())) {
                        msg.getChannel().sendMessage("tu as rendu respect a " + m.getAsMention()).queue();
                        return;
                    }
                }
                if (msg.getMentionedMembers().size() >= 1) {
                    msg.getChannel().sendMessage("tu as rendu respect à " + msg.getMentionedMembers().get(0).getAsMention()).queue();
                } else {
                    msg.getChannel().sendMessage("tu as rendu respect à " + msg.getContentDisplay().substring(2)).queue();
                }
            } else {
                msg.getChannel().sendMessage("tu lui as rendu respect").queue();
            }
        } else {
            msg.getChannel().sendMessage("trop long, donc je te respecterai pas").queue();
        }
    }

    /**
     * on vous demande d'éviter de crier
     * @param msg
     */
    public static void rage(Message msg) {
        if (msg.getAuthor().getId().equals("358659144330248193")) {
            msg.getTextChannel().sendMessage("aaaaah on le reconnait bien").reference(msg).queue();
        } else {
            msg.getTextChannel().sendMessage("oula on va se calmer tu vas détroner le ROI DU SEL").reference(msg).queue();
        }
    }

    /**
     * nice
     * récupère les données sur un cite
     * @param msg
     * @throws IOException
     */
    public static void nice(Message msg) throws IOException {
        Document doc = Jsoup.connect("https://randomcity.net").get();
        EmbedBuilder eb = new EmbedBuilder();
        String str = doc.select("body:nth-child(2) div.pure-g:nth-child(1) div.pure-u-1:nth-child(2) > h1:nth-child(1)").text();
        eb.setTitle(str).setColor(new Color(255,215,0));
        str = doc.select("body:nth-child(2) div.pure-g:nth-child(1) div.pure-u-1:nth-child(2) h1:nth-child(1) > img.flag").attr("src");
        eb.setImage("https://randomcity.net/" + str);
        msg.getChannel().sendMessageEmbeds(eb.build()).queue();
    }

    /**
     * on évite de casser une table à chaque putain bordel
     * @param msg
     */
    public static void putain(Message msg){
        msg.getChannel().sendMessage("┬─┬ノ( º _ ºノ)").queue(new MessageConsumer("(°-°)\\ ┬─┬", 100, new MessageConsumer("(╯°□°)╯    ]", 100, new MessageConsumer("(╯°□°)╯  ︵  ┻━┻", 100, null))));
    }

    /**
     * va te coiffer, tu me fais peur
     * @param msg
     */
    public static void feur(Message msg){
        msg.getTextChannel().sendMessage("feur").queue();
    }

    /**
     * hmmm dépend du nombre de m
     * @param msg
     * @param rec
     */
    public static void hmm(Message msg,String rec){
        int i = 1;
        while ( i < rec.length() && rec.charAt(i) == 'm')
            i++;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedImage im = getImage("hmmm.png");
            if (DetectorAttachment.percent(2)) {
                msg.getChannel().sendMessage("https://cdn.discordapp.com/emojis/439511275437948938.gif?size=4096").queue();
                return;
            }
            int w = im.getWidth() + i*2;
            int h = im.getHeight() + i*2;
            im = resize(im, w, h, 0, 0, true);
            im = resize(im, w, h, 0, 0, false);
            ImageIO.write(im, "png", baos);
            msg.getTextChannel().sendFile(baos.toByteArray(), "thunk.png").queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void rip(Message msg,String avatarUrl) {
        BufferedImage avatar = getImage(avatarUrl);

    }

    public static void daronned(Message msg)
    {
        String[] args = msg.getContentRaw().split(" ");
        if (args.length != 1) {
            if ((msg.getContentRaw().contains("@here") || msg.getContentRaw().contains("@everyone"))) {
                msg.getChannel().sendMessage("tout le monde se fait daronné").queue();
                return;
            }
            List<Member> mbrs = msg.getGuild().getMembers();
            for (Member m : mbrs) {
                if (args[1].equalsIgnoreCase(m.getUser().getName())) {
                    msg.getChannel().sendMessage(m.getAsMention() + "s'est fait darroné").queue();
                    return;
                }
            }
            if (msg.getMentionedMembers().size() >= 1)
                msg.getChannel().sendMessage(msg.getMentionedMembers().get(0).getAsMention() + "s'est fait daronné").queue();
    }
    else
        msg.getChannel().sendMessage("il s'est fait daronné").queue();
}
}


