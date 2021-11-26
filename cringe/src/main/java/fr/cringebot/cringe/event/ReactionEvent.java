package fr.cringebot.cringe.event;

import fr.cringebot.cringe.objects.MessageConsumer;
import fr.cringebot.cringe.objects.UtilFunction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Random;

import static fr.cringebot.cringe.objects.StringExtenders.firstsearch;
import static fr.cringebot.cringe.objects.imgExtenders.getImage;
import static fr.cringebot.cringe.objects.imgExtenders.resize;

public class ReactionEvent {
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

    public static void rage(Message msg) {

        if (msg.getAuthor().getId().equals("358659144330248193")) {
            msg.getTextChannel().sendMessage("aaaaah on le reconnait bien").reference(msg).queue();
        } else {
            msg.getTextChannel().sendMessage("oula on va se calmer tu vas détroner le ROI DU SEL").reference(msg).queue();
        }
    }

    public static void randomcity(Message msg) throws IOException {

        HttpURLConnection conn = (HttpURLConnection) new URL(
                "https://randomcity.net/").openConnection();
        conn.connect();

        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());

        byte[] bytes = new byte[1024];
        int tmp;
        int i = 0;
        int j = 0;
        String chaine = "";
        while ((tmp = bis.read(bytes)) != -1 || i < 2) {
            if (i == 1) {
                chaine = new String(bytes, 0, tmp);
            }
            i++;
        }
        j = firstsearch(chaine,"h1>");
        j++;
        i = firstsearch(chaine.substring(j),"  ");
        EmbedBuilder eb =new EmbedBuilder().setTitle(chaine.substring(j,i + j)).setColor(new Color(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255)));
        j = firstsearch(chaine,"<img class=\"flag\" src=\"") + 1;
        i = firstsearch(chaine,".png\" alt=\"Flag of") - 13;
        eb.setImage("https://randomcity.net/"+chaine.substring(j,i)).setColor(Color.green);
        msg.getTextChannel().sendMessageEmbeds(eb.build()).reference(msg).queue();

    }

    public static void putain(Message msg){
        msg.getChannel().sendMessage("┬─┬ノ( º _ ºノ)").queue(new MessageConsumer("(°-°)\\ ┬─┬", 100, new MessageConsumer("(╯°□°)╯    ]", 100, new MessageConsumer("(╯°□°)╯  ︵  ┻━┻", 100, null))));
    }

    public static void feur(Message msg){
        msg.getTextChannel().sendMessage("feur").queue();
    }

    public static void hmm(Message msg,String rec){
        int i = 1;
        while ( i < rec.length() && rec.charAt(i) == 'm')
            i++;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedImage im = getImage("hmmm.png");
            if (UtilFunction.percent(2)) {
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
}


