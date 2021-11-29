package fr.cringebot.cringe.event;

import fr.cringebot.cringe.objects.UtilFunction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageActivity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.internal.requests.restaction.MessageActionImpl;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import static fr.cringebot.cringe.objects.Emote.getEmote;

public class memesEvent {
    public static void addReaction(Message message, MessageReaction react) {
        System.out.println("test");
        int u = 0;
        int d = 0;
        for ( MessageReaction reaction : message.getReactions()) {
            if (getEmote(reaction.getReactionEmote()).equals("rirededroite"))
                u = reaction.getCount();
            else if (getEmote(reaction.getReactionEmote()).equals("porte"))
                d = reaction.getCount();
            else if (!getEmote(reaction.getReactionEmote()).equals("anto"))
                react.removeReaction().queue();
        }
        int nb = u-d;
        if (nb <= -5)
            message.delete().queue();
        else if (nb >= 1) {
            ArrayList<Message.Attachment> attachments = new ArrayList<>(message.getAttachments());
            if ((message.getAttachments().size() >= 1 && message.getAttachments().get(0).isImage()) || (message.getAttachments().size() >= 1 && message.getAttachments().get(0).isVideo())) {
                MessageAction msg;
                if (!message.getContentRaw().isEmpty())
                    msg = message.getGuild().getTextChannelById("911549374696411156").sendMessage(message.getContentRaw());
                else {
                    File f = attachments.get(0).downloadToFile(message.getAttachments().get(0).getFileName()).join();
                    msg = message.getGuild().getTextChannelById("911549374696411156").sendFile(f);
                    f.deleteOnExit();
                    attachments.remove(0);
                }
                for ( Message.Attachment ac : attachments) {
                    File file = ac.downloadToFile(ac.getFileName()).join();
                    msg.addFile(file);
                    file.deleteOnExit();
                }
                msg.queue();
            }
            else if (UtilFunction.isImage(message.getContentRaw()) || UtilFunction.isTwitter(message.getContentRaw()) || UtilFunction.isVideo(message.getContentRaw()) || UtilFunction.isYoutube(message.getContentRaw())) {
                message.getGuild().getTextChannelById("911549374696411156").sendMessage(message.getContentRaw()).queue();
            }
            message.delete().queue();
        }
    }
}
