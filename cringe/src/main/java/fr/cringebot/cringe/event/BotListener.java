/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   BotListener.java                                   :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 11:45:58 by gchatain          #+#    #+#             */
/*   Updated: 2021/12/05 11:47:26 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */


package fr.cringebot.cringe.event;

import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.objects.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import okhttp3.internal.Util;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

import static fr.cringebot.cringe.event.ReactionEvent.*;
import static fr.cringebot.cringe.objects.StringExtenders.containsIgnoreCase;
import static fr.cringebot.cringe.objects.StringExtenders.startWithIgnoreCase;

/**
 * capture tout les evenements du bot
 */
public class BotListener implements EventListener {

    private final CommandMap commandMap;
    private final BotDiscord bot;


    public BotListener(CommandMap cmd, BotDiscord bot) {
        this.commandMap = cmd;
        this.bot = bot;
    }

    @Override
    /**
     * à chaque event fonction qui sert d'aiguillage vers les fonction visée
     */
    public void onEvent(@NotNull GenericEvent event){
        System.out.println(event.getClass().getSimpleName());
        if (event instanceof ReadyEvent) onEnable((ReadyEvent)event);
        if (event instanceof MessageReceivedEvent) {
            try {
                onMessage((MessageReceivedEvent)event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(event instanceof GuildMemberJoinEvent) onGuildMemberJoin((GuildMemberJoinEvent) event);
        else if(event instanceof GuildMemberRemoveEvent) onGuildMemberLeave((GuildMemberRemoveEvent) event);
        else if(event instanceof MessageReactionAddEvent) onAddReact((MessageReactionAddEvent) event);
    }

    /**
     * au lancement
     * @param event
     */
    private void onEnable(ReadyEvent event){
        Activity act = new activity(", si tu lis ça tu es cringe", null ,Activity.ActivityType.LISTENING);
        bot.getJda().getPresence().setActivity(act);
    }

    /**
     * à la recption d'un message
     * @param event
     * @throws IOException
     */
    private void onMessage(MessageReceivedEvent event) throws IOException {
        if(event.getAuthor().equals(event.getJDA().getSelfUser())) return;

        Message msg = event.getMessage();

        //si ça commence par le prefix, chercher et executer la commande en passant par Commandmap
        if(msg.getContentRaw().startsWith(CommandMap.getTag())){
            commandMap.commandUser(msg.getContentRaw().replaceFirst(CommandMap.getTag(), ""), event.getMessage());
            return;
        }


        //split le message
        String[] args = msg.getContentRaw().split(" ");

        //tous les events mis sans le prefix les reactions en gros
        if (args[0].equalsIgnoreCase("f")) {
            pressf(msg);
        }

        if (msg.getContentRaw().equalsIgnoreCase("ping"))
        {
            long time = System.currentTimeMillis();
            msg.getChannel().sendMessage("pong").complete().editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
        }

        if (startWithIgnoreCase(msg.getContentRaw(),"AAA") && containsIgnoreCase(msg.getContentRaw(), "AHH")){
            rage(msg);
        }

        if (containsIgnoreCase(msg.getContentRaw(), "nice")) {
            if (!DetectorAttachment.isAnyLink(msg)) {
                try {
                    nice(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String[] s = msg.getContentRaw().replace("?","").replace(".","").split(" ");
        if (containsIgnoreCase(s[s.length - 1], "quoi" )){
            feur(msg);
        }

        if (containsIgnoreCase(msg.getContentRaw(), "putain")){
            putain(msg);
        }

        if (containsIgnoreCase(msg.getContentRaw(), "hmm")){
            for (String split : msg.getContentRaw().split(" "))
                if (StringExtenders.startWithIgnoreCase(split,"hmm"))
                    hmm(msg, split);
        }

        if (containsIgnoreCase(msg.getContentRaw(),"cringe")){
            if (!DetectorAttachment.isAnyLink(msg))
                msg.getTextChannel().sendMessage("https://tenor.com/view/oh-no-cringe-cringe-oh-no-kimo-kimmo-gif-23168319").queue();
        }

        //fonction mêmes quand un memes est postée (pas finie)
        if (msg.getChannel().getId().equals("461606547064356864") && (DetectorAttachment.isAnyLink(msg)))
        {
            if (DetectorAttachment.isImage(msg.getContentRaw())) {
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
                msg = temp;
            }
            if (msg.getAttachments().size() == 1 && msg.getAttachments().get(0).isImage())
            {
                File f = msg.getAttachments().get(0).downloadToFile().join();
                EmbedBuilder eb = new EmbedBuilder().setImage("attachment://"+f.getName())
                        .setAuthor(msg.getAuthor().getName(), null, msg.getAuthor().getEffectiveAvatarUrl());
                if (!msg.getContentRaw().isEmpty())
                        eb.setDescription(msg.getContentRaw());
                msg.delete().queue();
                msg = msg.getChannel().sendFile(f).setEmbeds(eb.build()).complete();
                f.deleteOnExit();

            }
            if (DetectorAttachment.isVideo(msg.getContentRaw())
                    && !DetectorAttachment.isYoutube(msg.getContentRaw())
                    && !DetectorAttachment.isTwitter(msg.getContentRaw())
                    && !DetectorAttachment.isReddit(msg.getContentRaw())
                    && !DetectorAttachment.isTenor(msg.getContentRaw()))
            {
                BufferedInputStream bs = new BufferedInputStream(new URL(msg.getContentRaw()).openStream());
                FileOutputStream fos = new FileOutputStream(msg.getContentRaw().split("/")[msg.getContentRaw().split("/").length - 1]);
                byte[] data = new byte[1024];
                int ByteContent;
                while ((ByteContent = bs.read(data,0,1024)) != -1){
                    fos.write(data, 0, ByteContent);
                }
                File f = new File(msg.getContentRaw().split("/")[msg.getContentRaw().split("/").length - 1]);
                msg.delete().queue();
                msg = msg.getChannel().sendFile(f).complete();
                f.deleteOnExit();
            }
            if (!msg.getAttachments().isEmpty() && msg.getAttachments().get(0).isVideo())
            {
                File f = msg.getAttachments().get(0).downloadToFile().join();
                Message temp;
                if (!msg.getContentRaw().isEmpty())
                    temp = msg.getChannel().sendMessage("by > "+msg.getAuthor().getName()+"< >>"+ msg.getContentRaw()).addFile(f).complete();
                else
                    temp = msg.getChannel().sendMessage("by >"+msg.getAuthor().getName()).addFile(f).complete();
                msg.delete().queue();
                msg = temp;
                f.deleteOnExit();
            }
            if (DetectorAttachment.isTwitter(msg.getContentRaw()))
            {
                EmbedBuilder eb = new EmbedBuilder()
                        .setDescription(msg.getEmbeds().get(0).getDescription())
                        .setTitle(msg.getEmbeds().get(0).getAuthor().getName())
                        .setColor(msg.getEmbeds().get(0).getColor())
                        .setFooter(msg.getAuthor().getName(), msg.getAuthor().getEffectiveAvatarUrl())
                        .setAuthor("twitter", msg.getEmbeds().get(0).getUrl(), msg.getEmbeds().get(0).getFooter().getIconUrl());
                msg.delete().queue();
                msg = msg.getChannel().sendMessageEmbeds(eb.build()).complete();
            }
            if (DetectorAttachment.isReddit(msg.getContentRaw()))
            {
                EmbedBuilder eb = new EmbedBuilder()
                        .setTitle(msg.getEmbeds().get(0).getTitle());
                        eb.setAuthor("reddit",msg.getContentRaw(),"https://www.elementaryos-fr.org/wp-content/uploads/2019/08/logo-reddit.png");
                        eb.setFooter(msg.getAuthor().getName(), msg.getAuthor().getEffectiveAvatarUrl());
                        eb.setImage(msg.getEmbeds().get(0).getThumbnail().getUrl());
                msg.delete().queue();
                msg = msg.getChannel().sendMessageEmbeds(eb.build()).complete();
            }
            if (DetectorAttachment.isTenor(msg.getContentRaw()))
            {
                String str = msg.getContentRaw();
                msg.delete().queue();
                msg = msg.getChannel().sendMessage(str).complete();
            }
            msg.addReaction(msg.getGuild().getEmoteById(Emote.rirederoite)).queue();
            msg.addReaction(msg.getGuild().getEmoteById(Emote.anto)).queue();
            msg.addReaction(msg.getGuild().getEmoteById(Emote.porte)).queue();
        }

    }

    /**
     * si une nouvelle personne a rejoint
     * @param event
    */
    private void onGuildMemberJoin(GuildMemberJoinEvent event){
        event.getGuild().getTextChannelById("687244482739044370").sendMessage(event.getUser().getAsMention()+" a rejoint la Guild.").queue();
    }

    /**
     * si une personne nous as quitté
     * @param event
     */
    private void onGuildMemberLeave(GuildMemberRemoveEvent event){
        event.getGuild().getTextChannelById("687244482739044370").sendMessage(event.getUser().getAsMention()+" a quitté la Guild.").queue();
    }

    /**
     * si une personne ajoute une réaction
     * @param event
     */
    private void onAddReact(MessageReactionAddEvent event){
        if (event.getChannel().getId().equals("461606547064356864"))
        {
            memesEvent.addReaction(event.getChannel().retrieveMessageById(event.getMessageId()).complete(),event.getReaction());
        }
    }
}
