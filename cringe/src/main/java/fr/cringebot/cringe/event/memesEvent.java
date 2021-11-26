package fr.cringebot.cringe.event;

import fr.cringebot.cringe.objects.UtilFunction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageActivity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

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
        else if (nb >= 5) {
            if ((message.getAttachments().size() >= 1 && message.getAttachments().get(0).isImage()) || (message.getAttachments().size() >= 1 && message.getAttachments().get(0).isVideo())) {
                MessageAction msg = message.getGuild().getTextChannelById("911549374696411156").sendMessage(message.getContentRaw());
                for ( Message.Attachment ac : message.getAttachments()) {
                    File file = ac.downloadToFile(ac.getFileName()).join();
                    msg = msg.addFile(file);
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
