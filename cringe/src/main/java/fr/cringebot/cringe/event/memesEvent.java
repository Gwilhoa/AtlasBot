/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   memesEvent.java                                    :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 13:00:28 by gchatain          #+#    #+#             */
/*   Updated: 2021/12/05 14:48:37 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.event;

import fr.cringebot.cringe.objects.DetectorAttachment;
import fr.cringebot.cringe.objects.Emote;
import fr.cringebot.cringe.objects.imgExtenders;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

import static fr.cringebot.cringe.objects.Emote.getEmote;

public class memesEvent {
    /**
     * fonction servant si quelqu'un réagit à un même
     * suite au modifications des memes elle est a modifier
     * @param message
     * @param react
     */
    public static void addReaction(Message message, MessageReaction react) {
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
            ArrayList<Message.Attachment> attachments = new ArrayList<>(message.getAttachments());
            if (!message.getEmbeds().isEmpty()) {
                message.getGuild().getTextChannelById("911549374696411156").sendMessageEmbeds(message.getEmbeds().get(0)).queue();
                message.delete().queue();
                return;
            }
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
            } else if (DetectorAttachment.isImage(message.getContentRaw()) || DetectorAttachment.isTwitter(message.getContentRaw()) || DetectorAttachment.isVideo(message.getContentRaw()) || DetectorAttachment.isYoutube(message.getContentRaw())) {
                message.getGuild().getTextChannelById("911549374696411156").sendMessage(message.getContentRaw()).queue();
            }
            message.delete().queue();
        }
    }

    public static void postmeme(Message msg) throws IOException {
        if (DetectorAttachment.isImage(msg.getContentRaw()))
            msg = repostmemeimg(msg);
        else if (msg.getAttachments().size() == 1 && msg.getAttachments().get(0).isImage())
            msg = repostOnlyAttachment(msg);
        else if (DetectorAttachment.isVideo(msg.getContentRaw())
                && !DetectorAttachment.isYoutube(msg.getContentRaw())
                && !DetectorAttachment.isTwitter(msg.getContentRaw())
                && !DetectorAttachment.isReddit(msg.getContentRaw())
                && !DetectorAttachment.isTenor(msg.getContentRaw()))
            msg = repostmemevid(msg);
        else if (!msg.getAttachments().isEmpty() && msg.getAttachments().get(0).isVideo())
            msg = repostVidAttachment(msg);
        else if (DetectorAttachment.isTwitter(msg.getContentRaw()))
            msg = repostTwitter(msg);
        else if (DetectorAttachment.isReddit(msg.getContentRaw()))
            msg = repostReddit(msg);
        else if (DetectorAttachment.isTenor(msg.getContentRaw())) {
            String str = msg.getContentRaw();
            msg.delete().queue();
            msg = msg.getChannel().sendMessage(str).complete();
        }
        msg.addReaction(msg.getGuild().getEmoteById(Emote.rirederoite)).queue();
        msg.addReaction(msg.getGuild().getEmoteById(Emote.anto)).queue();
        msg.addReaction(msg.getGuild().getEmoteById(Emote.porte)).queue();
    }

    private static Message repostReddit(Message msg) {
        EmbedBuilder eb = new EmbedBuilder()
                .setDescription(msg.getEmbeds().get(0).getDescription())
                .setTitle(msg.getEmbeds().get(0).getAuthor().getName())
                .setColor(msg.getEmbeds().get(0).getColor())
                .setFooter(msg.getAuthor().getName(), msg.getAuthor().getEffectiveAvatarUrl())
                .setAuthor("twitter", msg.getEmbeds().get(0).getUrl(), msg.getEmbeds().get(0).getFooter().getIconUrl());
        msg.delete().queue();
        return msg.getChannel().sendMessageEmbeds(eb.build()).complete();
    }

    private static Message repostTwitter(Message msg) {
        EmbedBuilder eb = new EmbedBuilder()
                .setDescription(msg.getEmbeds().get(0).getDescription())
                .setTitle(msg.getEmbeds().get(0).getAuthor().getName())
                .setColor(msg.getEmbeds().get(0).getColor())
                .setFooter(msg.getAuthor().getName(), msg.getAuthor().getEffectiveAvatarUrl())
                .setAuthor("twitter", msg.getEmbeds().get(0).getUrl(), msg.getEmbeds().get(0).getFooter().getIconUrl());
        msg.delete().queue();
        return msg.getChannel().sendMessageEmbeds(eb.build()).complete();
    }

    private static Message repostVidAttachment(Message msg) {
        File f = msg.getAttachments().get(0).downloadToFile().join();
        Message temp;
        if (!msg.getContentRaw().isEmpty())
            temp = msg.getChannel().sendMessage("by > " + msg.getAuthor().getName() + "< >>" + msg.getContentRaw()).addFile(f).complete();
        else
            temp = msg.getChannel().sendMessage("by >" + msg.getAuthor().getName()).addFile(f).complete();
        msg.delete().queue();
        f.deleteOnExit();
        return (temp);
    }


    private static Message repostmemevid(Message msg) throws IOException {
        BufferedInputStream bs = new BufferedInputStream(new URL(msg.getContentRaw()).openStream());
        FileOutputStream fos = new FileOutputStream(msg.getContentRaw().split("/")[msg.getContentRaw().split("/").length - 1]);
        byte[] data = new byte[1024];
        int ByteContent;
        while ((ByteContent = bs.read(data, 0, 1024)) != -1) {
            fos.write(data, 0, ByteContent);
        }
        File f = new File(msg.getContentRaw().split("/")[msg.getContentRaw().split("/").length - 1]);
        msg.delete().queue();
        f.deleteOnExit();
        return msg.getChannel().sendFile(f).complete();
    }

    private static Message repostOnlyAttachment(Message msg) {
        File f = msg.getAttachments().get(0).downloadToFile().join();
        EmbedBuilder eb = new EmbedBuilder().setImage("attachment://" + f.getName())
                .setAuthor(msg.getAuthor().getName(), null, msg.getAuthor().getEffectiveAvatarUrl());
        if (!msg.getContentRaw().isEmpty())
            eb.setDescription(msg.getContentRaw());
        msg.delete().queue();
        f.deleteOnExit();
        return (msg.getChannel().sendFile(f).setEmbeds(eb.build()).complete());
    }

    private static Message repostmemeimg(Message msg) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedImage im = imgExtenders.getImage(new URL(msg.getContentRaw()));
        ImageIO.write(im, "png", baos);
        String fileName = msg.getContentRaw().split("/")[msg.getContentRaw().split("/").length - 1];
        MessageEmbed embed = new EmbedBuilder().setImage("attachment://" + fileName)
                .setAuthor(msg.getAuthor().getName(), null, msg.getAuthor().getEffectiveAvatarUrl())
                .setDescription("").build();
        Message temp = msg.getTextChannel().sendFile(baos.toByteArray(), fileName)
                .setEmbeds(embed).complete();
        msg.delete().queue();
        return temp;
    }
}
