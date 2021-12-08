/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   BotListener.java                                   :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 11:45:58 by gchatain          #+#    #+#             */
/*   Updated: 2021/12/08 01:52:01 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */


package fr.cringebot.cringe.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.objects.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GatewayPingEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.message.MessageEmbedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static fr.cringebot.cringe.event.ReactionEvent.*;
import static fr.cringebot.cringe.event.memesEvent.postmeme;
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

    public static Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(ArrayNumber.class,
            (JsonDeserializer<ArrayNumber<Integer>>) (jsonElement, type, jsonDeserializationContext) -> {
                if (!jsonElement.isJsonArray()) {
                    return new ArrayNumber<>(jsonElement.getAsNumber().intValue());
                }
                ArrayNumber<Integer> n = new ArrayNumber<>();
                JsonArray ar = jsonElement.getAsJsonArray();
                for (int i = 0; i < ar.size(); i++) {
                    n.li.add(ar.get(i).getAsNumber().intValue());
                }
                return n;
            }).create();

    @Override
    /**
     * à chaque event fonction qui sert d'aiguillage vers les fonction visée
     */
    public void onEvent(@NotNull GenericEvent event) {
        System.out.println(event.getClass().getSimpleName());
        if (event instanceof ReadyEvent) onEnable((ReadyEvent) event);
        if (event instanceof MessageReceivedEvent) {
            try {
                onMessage((MessageReceivedEvent) event);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else if (event instanceof GuildMemberJoinEvent) onGuildMemberJoin((GuildMemberJoinEvent) event);
        else if (event instanceof GuildMemberRemoveEvent) onGuildMemberLeave((GuildMemberRemoveEvent) event);
        else if (event instanceof MessageReactionAddEvent) onAddReact((MessageReactionAddEvent) event);
        else if (event instanceof SelectionMenuEvent) onSelectMenu((SelectionMenuEvent) event);
        else if (event instanceof GatewayPingEvent) onPing((GatewayPingEvent) event);
        else if (event instanceof MessageEmbedEvent) {
            try {
                onEmbed((MessageEmbedEvent) event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void onEmbed(MessageEmbedEvent event) throws InterruptedException {
        if (event.getChannel().getId().equals("461606547064356864")) {
            Message msg = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            if (DetectorAttachment.isReddit(msg.getContentRaw()))
                msg = memesEvent.repostReddit(msg);
            if (DetectorAttachment.isTwitter(msg.getContentRaw()))
                msg = memesEvent.repostTwitter(msg);
            msg.addReaction(msg.getGuild().getEmoteById(Emote.rirederoite)).queue();
            msg.addReaction(msg.getGuild().getEmoteById(Emote.anto)).queue();
            msg.addReaction(msg.getGuild().getEmoteById(Emote.porte)).queue();
        }
    }

    private void onPing(GatewayPingEvent event) {
        long time = System.currentTimeMillis();
        if (PollMessage.pollMessage == null)
            return;
        for (PollMessage pm : PollMessage.pollMessage.values())
            if (pm.getTime() + 86400000 < time)
                pm.unactive(event.getJDA());
    }

    private void onSelectMenu(SelectionMenuEvent event) {
        if (!event.getMessage().getEmbeds().isEmpty() && event.getMessage().getEmbeds().get(0).getAuthor().getName().equals("poll")) {
            PollMessage pm = PollMessage.pollMessage.get(event.getMessageId());
            if (!pm.newVote(event.getUser(), event.getSelectedOptions().get(0).getLabel()))
                event.reply("tu as déja voté, voter 2 fois, c'est cringe").setEphemeral(true).queue();
            event.getMessage().editMessageEmbeds(pm.getMessageEmbed(event.getGuild())).queue();
            event.reply("ton vote a été enregistré \uD83D\uDC4D").setEphemeral(true).queue();
        }
    }

    /**
     * au lancement
     *
     * @param event
     */
    private void onEnable(ReadyEvent event) {
        Activity act = new activity(", si tu lis ça tu es cringe", null, Activity.ActivityType.LISTENING);
        bot.getJda().getPresence().setActivity(act);
        if (new File("save").mkdir())
            System.out.println("création du directoryCentral");
        PollMessage.load();
    }

    /**
     * si une nouvelle personne a rejoint
     *
     * @param event
     */
    private void onGuildMemberJoin(GuildMemberJoinEvent event) {
        event.getGuild().getTextChannelById("687244482739044370").sendMessage(event.getUser().getAsMention() + " a rejoint la Guild.").queue();
    }

    /**
     * si une personne nous as quitté
     *
     * @param event
     */
    private void onGuildMemberLeave(GuildMemberRemoveEvent event) {
        event.getGuild().getTextChannelById("687244482739044370").sendMessage(event.getUser().getAsMention() + " a quitté la Guild.").queue();
    }

    /**
     * si une personne ajoute une réaction
     *
     * @param event
     */
    private void onAddReact(MessageReactionAddEvent event) {
        if (event.getChannel().getId().equals("461606547064356864")) {
            memesEvent.addReaction(event.getChannel().retrieveMessageById(event.getMessageId()).complete(), event.getReaction());
        }
    }

    /**
     * à la recption d'un message
     *
     * @param event
     * @throws IOException
     */
    private void onMessage(MessageReceivedEvent event) throws IOException, InterruptedException {
        if (event.getAuthor().equals(event.getJDA().getSelfUser())) return;
        Message msg = event.getMessage();
        if (msg.getContentRaw().startsWith(CommandMap.getTag())) {
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

        if (containsIgnoreCase(msg.getContentRaw(), "cringe")) {
            if (!DetectorAttachment.isAnyLink(msg))
                msg.getTextChannel().sendMessage("https://tenor.com/view/oh-no-cringe-cringe-oh-no-kimo-kimmo-gif-23168319").queue();
        }

        //fonction mêmes quand un memes est postée (pas finie)
        if (msg.getChannel().getId().equals("461606547064356864") && (DetectorAttachment.isAnyLink(msg)))
            postmeme(msg);

    }

    public static class ArrayNumber<E extends Number> {
        public ArrayList<E> li = new ArrayList<>();

        public ArrayNumber() {
        }

        public ArrayNumber(E element) {
            li.add(element);
        }

        @Override
        public String toString() {
            return li.toString();
        }
    }

}
