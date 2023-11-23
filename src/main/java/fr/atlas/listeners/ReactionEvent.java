/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   ReactionEvent.java                                 :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 13:00:42 by gchatain          #+#    #+#             */
/*   Updated: 2023/01/16 23:38:35 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.atlas.listeners;

import fr.atlas.objects.DetectorAttachment;
import fr.atlas.objects.MessageConsumer;
import fr.atlas.objects.StringExtenders;
import fr.atlas.objects.imgExtenders;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static fr.atlas.BotDiscord.setError;
import static fr.atlas.objects.StringExtenders.*;
import static fr.atlas.objects.imgExtenders.getImage;
import static fr.atlas.objects.imgExtenders.resize;

public class ReactionEvent {

    public static void reactionevent(Message msg, JDA jda) throws IOException {
        if (msg.getChannel().getId().equals("461606547064356864"))
            return;

        if (msg.getContentRaw().split(" ")[0].equalsIgnoreCase("f")) {
            pressf(msg);
        }

        if (StringExtenders.containsWord(msg.getContentRaw(),"lyon"))
        {
            msg.getChannel().sendMessage("Les portes s'ouvriront à droite dans le sens de la circulation").queue();
        }
        if (containsIgnoreCase(msg.getContentRaw(), "je suis"))
        {
            int i = firstsearch(msg.getContentRaw().toLowerCase(Locale.ROOT), "je suis");
            String str = msg.getContentRaw().substring(i + 2);
            if (str.split(" ").length == 1) {
                str = str.replace("@everyone", "tout le monde");
                if (new Random().nextInt(2) == 0 && msg.getGuild().getMember(jda.getSelfUser()) != null)
                    msg.getChannel().sendMessage("Bonjour " + str + ". Moi c'est " + msg.getGuild().getMember(jda.getSelfUser()).getEffectiveName()).queue();
                else
                    msg.getChannel().sendMessage("Bonjour " + str + ". Moi c'est " + jda.getSelfUser().getName()).queue();
            }
        }

        if (msg.getContentRaw().equalsIgnoreCase("ratio") )
        {
            if (msg.getReferencedMessage() != null) {
                msg.getChannel().sendMessage("Ratio by " + msg.getMember().getAsMention()).setMessageReference(msg.getReferencedMessage()).complete().addReaction(Emoji.fromFormatted("⬆️")).queue();
                msg.delete().queue();
            }
        }

        if (StringExtenders.containsWord(msg.getContentRaw(), "sus")) {
            boolean sus = new Random().nextInt(100) > 95;
            if (msg.getReferencedMessage() != null)
                sus(msg.getChannel().asTextChannel(), msg.getReferencedMessage().getMember(), sus);
            else if (!msg.getMentions().getMembers().isEmpty() && msg.getMentions().getMembers().get(0) != null)
                sus(msg.getChannel().asTextChannel(), msg.getMentions().getMembers().get(0), sus);
            else
                sus(msg.getChannel().asTextChannel(), msg.getMember(), sus);
        }
        if (msg.getContentRaw().equalsIgnoreCase("ping"))
        {
            long time = System.currentTimeMillis();
            msg.getChannel().sendMessage("pong").complete().editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
        }

        String s = msg.getContentRaw().replace("?","").replace(".","").replace(" ","");
        HashMap<String, String> map = new HashMap<>();
        map.put("pourquoi", "parceque feur");
        map.put("quoi", "feur,feur,quoicoubeh");
        map.put("what", "ibulle");
        map.put("oui", "stiti");
        map.put("non", "bril");
        map.put("ouais", "stern");
        map.put("ouai", "stern"); //pour les gens qui savent pas ecrire
        map.put("hein", "2,deux,2,deux,apanyan,dien");


        for (String str : map.keySet()) {
            if (s.substring(Math.max(0, s.length() - str.length())).equalsIgnoreCase(str)) {
                String[] splitedResponse = map.get(str).split(",");
                int r = new Random().nextInt(100);
                if (r < 15)
                    msg.getChannel().sendMessage(splitedResponse[new Random().nextInt(splitedResponse.length)]).queue();
                return;
            }
        }



        if (containsIgnoreCase(msg.getContentRaw().replace('é', 'e'), "societer"))
            msg.getChannel().sendFiles(FileUpload.fromData(imgExtenders.getFile("societer.png"))).queue();
        if (msg.getContentRaw().startsWith("repose"))
            retakethis(msg);
        if (containsIgnoreCase(msg.getContentRaw(), "putain")) {
            putain(msg);
        }

        if (containsIgnoreCase(msg.getContentRaw(), "hmm")){
            for (String split : msg.getContentRaw().split(" "))
                if (StringExtenders.startWithIgnoreCase(split,"hmm"))
                    hmm(msg, split);
        }

        if (containsIgnoreCase(msg.getContentRaw(), "je lag"))
        {
            File f = imgExtenders.getFile("internet.png");
            msg.getChannel().sendFiles(FileUpload.fromData(f)).queue();
            f.delete();
        }

        if (containsIgnoreCase(msg.getContentRaw(), "cringe")) {
            if (!DetectorAttachment.isAnyLink(msg))
                msg.getChannel().sendMessage("https://tenor.com/view/oh-no-cringe-cringe-oh-no-kimo-kimmo-gif-23168319").queue();
        }

        if (containsIgnoreCase(msg.getContentRaw(), "stonks")) {
            if (new Random().nextInt(100) >= 95)
            {
                BufferedImage bi = imgExtenders.getImage("not stonks.png");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bi, "gif", baos);
                msg.getChannel().sendFiles(FileUpload.fromData(baos.toByteArray(), "not stonks.png")).queue();
                return;
            }
            File f = imgExtenders.getFile("stonks.gif");
            msg.getChannel().sendFiles(FileUpload.fromData(f)).queue();
        }

        if (startWithIgnoreCase(msg.getContentRaw(), "daronned") || startWithIgnoreCase(msg.getContentRaw(), "daronné"))
            daronned(msg);

        if (msg.getContentRaw().equalsIgnoreCase("saint pierre"))
        {
            int r = new Random().nextInt(2);
            if (r == 1)
                msg.getChannel().sendMessage("cret-en-belledonne, nuance.\nsi ça se trouve les habitants sont les crétins").queue();
            if (r == 0)
                msg.getChannel().sendMessage("village préféré de mon conseillé principal, il a souvent des idées de merde et des blagues beauf\nmais bon on l'aime quand meme").queue();
        }
        if (msg.getContentRaw().equalsIgnoreCase("allevard"))
        {
            int r = new Random().nextInt(2);
            if (r == 1)
                msg.getChannel().sendMessage("village de dealers").queue();
            if (r == 0)
                msg.getChannel().sendMessage("village natal de mon créateur, il est un peu tete en l'air mais ça va").queue();
        }
        if (containsIgnoreCase(msg.getContentRaw(), "michel"))
            msg.getChannel().sendMessage( msg.getGuild().getMemberById("282859044593598464").getAsMention()+ ", eh oh je crois qu'on parle de toi").queue();
        if (containsIgnoreCase(msg.getContentRaw(), "shadow hunter"))
            msg.getChannel().sendMessage("*shadow **in**ter*").setMessageReference(msg).queue();
        if (msg.getContentRaw().equalsIgnoreCase("creeper"))
            msg.getChannel().sendMessage("Aww man").queue();
        if (msg.getContentRaw().equalsIgnoreCase("i am a dwarf"))
            msg.getChannel().sendMessage("and I'm digging a hole").queue();
        if (msg.getContentRaw().equalsIgnoreCase("je suis un nain"))
            msg.getChannel().sendMessage("et je creuse un gros trou").queue();
        if (msg.getContentRaw().equalsIgnoreCase("mové salon"))
        {
            if (msg.getMember().getId().equals("280959408119349248"))
                msg.getChannel().sendMessage("Franchement si j'étais vous **je me tairais**").queue();
            else
                msg.getChannel().sendMessage("eh oh tu t'es pris pour Logan ou quoi ? \n**MDR**").queue();
        }

        if (containsIgnoreCase(msg.getContentRaw(), "je possede des thunes") || containsIgnoreCase(msg.getContentRaw(), "je possède des thunes"))
            msg.getChannel().sendMessage(msg.getMember().getAsMention() + " est à l'aise financièrement").queue();
        if (StringExtenders.startWithIgnoreCase(msg.getContentRaw(), "@league of"))
            msg.getChannel().sendMessage(msg.getGuild().getRoleById("1114306111357599804").getAsMention() + " https://tenor.com/view/league-of-legends-gif-25112659").setMessageReference(msg).queue();
        MembersQuotes.MemberReact(msg);
    }


    public static void sus(TextChannel tc, Member mem, boolean issus) {

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                URLConnection connection = new URL(mem.getUser().getEffectiveAvatarUrl()).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0");
                BufferedImage im = ImageIO.read(connection.getInputStream());
                im = resize(im, 128, 128, 0, 0, true);
                im = imgExtenders.makeRoundedCorner(im, 150);
                BufferedImage ret = imgExtenders.getImage("spacebackground.png");
                Graphics2D g = ret.createGraphics();
                g.drawImage(im, 370, 130,  null);

                g.setColor(Color.WHITE);
                g.setFont(new Font("comic sans ms", Font.ITALIC, 25));
                if (!issus) {
                g.drawString(mem.getEffectiveName() + " was not the imposter", 300 - (mem.getEffectiveName().length()*5), 300);
                } else {
                    g.drawString(mem.getEffectiveName() + " was the imposter", 300  - (mem.getEffectiveName().length()*5), 300);
                }
                g.dispose();
                ImageIO.write(ret, "png", baos);
                tc.sendFiles(FileUpload.fromData(baos.toByteArray(), "sus.png")).queue();
            } catch (IOException e) {
                setError(e);
            }
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
                if (msg.getMentions().getMembers().size() >= 1) {
                    msg.getChannel().sendMessage("tu as rendu respect à " + msg.getMentions().getMembers().get(0).getAsMention()).queue();
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
            msg.getChannel().sendMessage("aaaaah on le reconnait bien").setMessageReference(msg).queue();
        } else {
            msg.getChannel().sendMessage("oula on va se calmer tu vas détroner le ROI DU SEL").setMessageReference(msg).queue();
        }
    }

    /**
     * nice
     * récupère les données sur un cite
     * @param msg
     * @throws IOException
     */


    /**
     * on évite de casser une table à chaque putain bordel
     * @param msg
     */
    public static void putain(Message msg){
        msg.getChannel().sendMessage("┬─┬ノ( º _ ºノ)").queue(new MessageConsumer("(°-°)\\ ┬─┬", 100, new MessageConsumer("(╯°□°)╯    ]", 100, new MessageConsumer("(╯°□°)╯  ︵  ┻━┻", 100, null))));
    }

    public static void retakethis(Message msg) {
        if (msg.getReferencedMessage() != null && msg.getContentRaw().startsWith("repose"))
        {
            msg.getReferencedMessage().editMessage("(╯°□°)╯  ︵  ┻━┻").queue(new MessageConsumer("(╯°□°)╯    ]", 100, new MessageConsumer("(°-°)\\ ┬─┬", 100, new MessageConsumer("┬─┬ノ( º _ ºノ)", 100, null))));
        }
    }

    /**
     * va te coiffer, tu me fais peur
     * @param msg
     */


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
            msg.getChannel().sendFiles(FileUpload.fromData(baos.toByteArray(), "thunk.png")).queue();
        } catch (IOException e) {
            setError(e);
        }
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
            if (msg.getMentions().getMembers().size() >= 1)
                msg.getChannel().sendMessage(msg.getMentions().getMembers().get(0).getAsMention() + "s'est fait daronné").queue();
    }
    else {
        msg.getChannel().sendMessage("il s'est fait daronné").queue();
    }
}
}


