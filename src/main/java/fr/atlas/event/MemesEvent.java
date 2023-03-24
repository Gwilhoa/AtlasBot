package fr.atlas.event;

import fr.atlas.Request.Squads;
import fr.atlas.objects.DetectorAttachment;
import fr.atlas.objects.imgExtenders;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class MemesEvent {

    public static Integer UpvoteRequired = 7;
    public static String DownvoteLabel = "bof...";
    public static String UpvoteLabel = "bien";
    public static String supervoteLabel = "super";


    public static ArrayList<net.dv8tion.jda.api.interactions.components.buttons.Button> addButtonBuilder() {
        ArrayList<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
        buttons.add(Button.danger("b_memes;", DownvoteLabel));
        buttons.add(Button.success("n_memes;", UpvoteLabel));
        buttons.add(Button.primary("g_memes;", supervoteLabel));
        buttons.add(Button.danger("d_memes;" + UpvoteRequired, res(UpvoteRequired)).asDisabled());
        return buttons;
    }

    public static String res(Integer progress) {
        StringBuilder res = new StringBuilder();
        res.append("|");
        int i = 0;
        while (i < UpvoteRequired * 2) {
            if (i < progress)
                res.append("-");
            else
                res.append(" .");
            i++;
        }
        res.append("|");
        return res.toString();
    }

    public static void postmeme(Message msg) {
        try {
            if (!DetectorAttachment.isAnyLink(msg))
                msg.delete().queue();
            processmeme(msg);
        } catch (Exception e) {
            msg.getChannel().sendMessage("putain les mêmes bordel GUIGUI ! VIENS Là\ncontenu :\n" + msg.getContentRaw()).queue();
            e.printStackTrace();
        }
    }

    private static void processmeme(Message msg) throws Exception {
        String channel = "461606547064356864";
        String[] args = msg.getContentRaw().split("\\s");
        String Content = null;
        File f = null;
        if (DetectorAttachment.isTenor(msg.getContentRaw()) || DetectorAttachment.isYoutube(msg.getContentRaw())) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setImage(msg.getContentRaw());
            msg.getChannel().sendMessage("> " + msg.getMember().getUser().getName() + "\n" + msg.getContentRaw()).setActionRow(addButtonBuilder()).queue();
            msg.delete().queue();
            return;
        }
        for (String mot : args) {
            if (DetectorAttachment.isTwitter(mot)) {
                repostTwitter(msg, mot, msg.getGuild().getTextChannelById(channel));
                msg.delete().queue();
                return;
            }
            if (DetectorAttachment.isReddit(mot)) {
                repostReddit(msg, mot, msg.getGuild().getTextChannelById(channel));
                msg.delete().queue();
                return;
            }
        }
        if (!msg.getAttachments().isEmpty()) {
            f = imgExtenders.getFile(msg.getAttachments().get(0).getProxyUrl(),"meme.png", null);
            Content = msg.getContentRaw();
        } else {
            for (String mot : args) {
                if (DetectorAttachment.isImage(mot) || DetectorAttachment.isVideo(mot)) {
                    f = imgExtenders.getFile(new URL(mot), mot.replace("?", "/").split("/")[mot.split("/").length - 1], null);
                    Content = msg.getContentRaw().replace(mot, "");
                }
            }
            if (f == null)
                return;
        }
        if (DetectorAttachment.isNetExtension(f.getName())) return;
        if (f.length() >= 8000000) {
            f.delete();
            msg.delete().queue();
            msg.getChannel().sendMessage("> "+ msg.getMember().getUser().getName() + "\n" +msg.getContentRaw()).setActionRow(addButtonBuilder()).queue();
            return;
        }
        String ext = FilenameUtils.getExtension(f.getName());
        if ((ext.equals("mp4") || ext.equals("mov") || ext.equals("webm"))) {
                msg.getGuild().getTextChannelById(channel).sendMessage("> " + msg.getMember().getUser().getName() + "\n\n" + Content).setFiles(FileUpload.fromData(f)).setActionRow(addButtonBuilder()).queue();
                msg.delete().queue();
                f.delete();
        } else {
            EmbedBuilder eb = new EmbedBuilder()
                    .setImage("attachment://" + f.getName())
                    .setFooter(msg.getAuthor().getName(), msg.getAuthor().getAvatarUrl());
            try {
                eb.setColor(Squads.getSquadByMember(msg.getMember().getId()).getColor());
            } catch (Exception e) {
                eb.setColor(msg.getMember().getColor());
            }
            eb.setDescription(Content);
            msg.getGuild().getTextChannelById(channel).sendFiles(FileUpload.fromData(f)).setEmbeds(eb.build()).setActionRow(addButtonBuilder()).queue();
            msg.delete().queue();
            f.delete();
        }
    }

    public static void repostReddit(Message msg, String link, TextChannel channel) {
        if (msg.getEmbeds().isEmpty())
            return ;
        if (link == null) {
            String[] args = msg.getContentRaw().split(" ");
            for (String mot : args)
                if (DetectorAttachment.isReddit(mot))
                    link = mot;
        }
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(msg.getEmbeds().get(0).getTitle());
        if (msg.getContentRaw().split(" ").length > 1) {
            if (msg.getContentRaw().split(" ")[0].equals(link) || msg.getContentRaw().split(" ")[msg.getContentRaw().split(" ").length - 1].equals(link))
                eb.setDescription(msg.getContentRaw().replace(link, ""));
            else
                eb.setDescription(msg.getContentRaw().replace(link, " ça "));
        }
        eb.setAuthor("Reddit", link, "https://www.elementaryos-fr.org/wp-content/uploads/2019/08/logo-reddit.png");
        if (msg.getEmbeds().get(0).getFooter() == null)
            eb.setFooter(msg.getAuthor().getName(), msg.getAuthor().getEffectiveAvatarUrl());
        else
            eb.setFooter(msg.getEmbeds().get(0).getFooter().getText(),msg.getEmbeds().get(0).getFooter().getIconUrl());
        if (msg.getEmbeds().get(0).getThumbnail() != null)
            eb.setImage(msg.getEmbeds().get(0).getThumbnail().getUrl());
        else if (msg.getEmbeds().get(0).getImage() != null)
            eb.setImage(msg.getEmbeds().get(0).getImage().getUrl());
        eb.setColor(new Color(255, 69, 0));
        channel.sendMessageEmbeds(eb.build()).setActionRow(addButtonBuilder()).queue();
    }

    /**
     * renvoie un Embed de nature twitter
     *
     * @param msg
     * @return
     */
    public static void repostTwitter(Message msg, String link, TextChannel channel) {
        if (msg.getEmbeds().isEmpty())
            return ;
        if (link == null) {
            String[] args = msg.getContentRaw().split(" ");
            for (String mot : args)
                if (DetectorAttachment.isTwitter(mot))
                    link = mot;
        }
        link = link.replace("vxtwitter","twitter");
        EmbedBuilder eb = new EmbedBuilder();
        if (msg.getContentRaw().split(" ").length == 1)
            eb.setDescription(msg.getEmbeds().get(0).getDescription());
        else if (msg.getContentRaw().split(" ")[0].equals(link) || msg.getContentRaw().split(" ")[msg.getContentRaw().split(" ").length - 1].equals(link))
            eb.setDescription("> " + msg.getContentRaw().replace(link, "") + "\n\n" + msg.getEmbeds().get(0).getDescription());
        else
            eb.setDescription("> " + msg.getContentRaw().replace(link, "ça") + "\n\n" + msg.getEmbeds().get(0).getDescription());
        if (msg.getEmbeds().get(0).getFooter() == null)
            eb.setFooter(msg.getAuthor().getName(), msg.getAuthor().getEffectiveAvatarUrl());
        else
            eb.setFooter(msg.getEmbeds().get(0).getFooter().getText(),msg.getEmbeds().get(0).getFooter().getIconUrl());
        eb.setTitle(msg.getEmbeds().get(0).getAuthor().getName())
                .setColor(msg.getEmbeds().get(0).getColor())
                .setFooter(msg.getAuthor().getName(), msg.getAuthor().getEffectiveAvatarUrl());
        if (msg.getEmbeds().get(0).getImage() != null)
            eb.setImage(msg.getEmbeds().get(0).getImage().getUrl());
        channel.sendMessageEmbeds(eb.build()).setActionRow(addButtonBuilder()).queue();
    }

    public static void recupMeme(Guild g) throws InterruptedException {
        TextChannel tc = g.getTextChannelById("461606547064356864");
        ArrayList<Message> msgs = new ArrayList<>(tc.getHistory().retrievePast(100).complete());
        int i = 0;
        while(i < 100)
        {
            if (!msgs.get(i).getAuthor().isBot())
                    postmeme(msgs.get(i));
            else if (!msgs.get(i).getAuthor().isBot())
                msgs.get(i).delete().queue();
//            else if (msgs.get(i).getAuthor().isBot())
//                msgs.get(i).editMessageComponents(ActionRow.of(addButtonBuilder())).queue();
//            Thread.sleep(2000);
            i++;
        }
    }

    public static Member getMemeAuthor(Message message) {
        if (message.getEmbeds().isEmpty())
            return message.getGuild().getMembersByName(message.getContentRaw().substring(2).split("\n")[0], false).get(0);
        else if (message.getEmbeds().get(0).getAuthor() != null || !message.getContentRaw().isEmpty())
            return message.getGuild().getMembersByName(message.getContentRaw().substring(2).split("\n")[0], false).get(0);
        else
            return message.getGuild().getMembersByName(message.getEmbeds().get(0).getFooter().getText(), false).get(0);
    }
}
