/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   CommandDefault.java                                :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 12:46:08 by gchatain          #+#    #+#             */
/*   Updated: 2022/05/27 01:24:00 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.command;


import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.Polls.PollMain;
import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.builder.Command.ExecutorType;
import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.objects.SelectOptionImpl;
import fr.cringebot.cringe.objects.UserExtenders;
import fr.cringebot.cringe.reactionsrole.MessageReact;
import fr.cringebot.cringe.waifus.WaifuCommand;
import fr.cringebot.cringe.waifus.waifu;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static fr.cringebot.BotDiscord.isMaintenance;
import static fr.cringebot.cringe.cki.mainCommand.ckimain;

/**
 * fichier de commandes de base
 */
public class CommandDefault {

	private final BotDiscord botDiscord;
	/**
	 * intialisation de l'objet
	 *
	 * @param botDiscord
	 * @param commandMap
	 */
	public CommandDefault(BotDiscord botDiscord, CommandMap commandMap) {
		this.botDiscord = botDiscord;
	}

	/**
	 * arreter le bot depuis la console NORMALEMENT
	 *
	 * @param jda le bot a arreter
	 */
	@Command(name = "-", type = ExecutorType.CONSOLE)
	private void stop(JDA jda) {
		jda.getPresence().setStatus(OnlineStatus.OFFLINE);
		botDiscord.setRunning(false);
	}

	/**
	 * donne les informations sur une personne
	 *
	 * @param channel channel du message
	 * @param msg     message de l'envoyeur
	 */
	@Command(name = "info", description = "information sur un joueur", type = ExecutorType.USER)
	private void info(MessageChannel channel, Message msg) {
		Member mem = msg.getMember();
		if (msg.getMentions().getMembers().size() != 0) {
			mem = msg.getMentions().getMembers().get(0);
		}
		EmbedBuilder builder = new EmbedBuilder()
				.setAuthor(mem.getUser().getName(), null, mem.getUser().getAvatarUrl() + "?size=256")
				.setTitle("Informations")
				.setDescription("> surnom :" + mem.getEffectiveName() + "\n" +
						"> état :" + mem.getOnlineStatus().name() + "\n" +
						"> rejoint le serveur le " + mem.getTimeJoined().getDayOfMonth() + "/" + mem.getTimeJoined().getMonthValue() + "/" + mem.getTimeJoined().getYear() + "\n" +
						"> hypesquad " + UserExtenders.getHypesquad(mem) + "\n" +
						"> creer son compte le " + mem.getTimeCreated().getDayOfMonth() + "/" + mem.getTimeCreated().getMonthValue() + "/" + mem.getTimeCreated().getYear() + "\n" +
						"> messages total : " + UserExtenders.getAllmsg(mem))
				.setColor(mem.getColor());
		channel.sendMessageEmbeds(builder.build()).queue();
	}

	@Command(name = "top", description = "regarder le classement des escouades")
	private void top(Message msg){
		List<Squads> squads = Squads.getAllSquads();
		StringBuilder sb = new StringBuilder();
		sb.append(squads.get(0).getName()).append(" ").append(squads.get(0).getTotal()).append(" meilleur : ").append(msg.getGuild().getMemberById(squads.get(0).getBestid()).getAsMention()).append("\n");
		sb.append(squads.get(1).getName()).append(" ").append(squads.get(1).getTotal()).append(" meilleur : ").append(msg.getGuild().getMemberById(squads.get(1).getBestid()).getAsMention()).append("\n");
		sb.append(squads.get(2).getName()).append(" ").append(squads.get(2).getTotal()).append(" meilleur : ").append(msg.getGuild().getMemberById(squads.get(2).getBestid()).getAsMention());

		EmbedBuilder eb = new EmbedBuilder().setTitle("Classement :");
		eb.setDescription(sb);
		msg.getChannel().sendMessageEmbeds(eb.build()).queue();
	}

	@Command(name = "poll", description = "faites des sondages rapidements", type = ExecutorType.USER)
	private void poll(Message msg) {
		PollMain.PollMain(msg);
	}

	@Command(name = "role", description = "permettre de creer un role", type = ExecutorType.USER)
	private void role(Message msg) {
		String[] args = msg.getContentRaw().split(" ");
		ArrayList<SelectOption> options = new ArrayList<>();
		if (args.length == 3) {
			msg.addReaction(args[2]).queue();
			Role r = msg.getGuild().createRole().setName("©◊ß" + args[1]).setMentionable(true).setColor(new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255))).complete();
			EmbedBuilder eb = new EmbedBuilder()
					.setTitle("nouveau role")
					.setDescription(r.getName().replace("©◊ß", "") + "\n" + args[2])
					.setFooter(r.getId());
			for (MessageReact mr : MessageReact.message)
				options.add(new SelectOptionImpl(mr.getTitle(), mr.getTitle()));
			SelectMenuImpl selectionMenu = new SelectMenuImpl("role", "catégorie", 1, 1, false, options);
			msg.getChannel().sendMessageEmbeds(eb.build()).setActionRow(selectionMenu).complete();
		} else {
			msg.getChannel().sendMessage("erreur argument >role <nom> <emote>").queue();
		}
	}

	@Command(name = "harem", description = "la listes des waifus", type = ExecutorType.USER)
	private void harem(Message msg){
		String id = msg.getMember().getId();
		if (!msg.getMentions().getMembers().isEmpty())
			id = msg.getMentions().getMembers().get(0).getId();
		ArrayList<waifu> waifus = waifu.getAllWaifu();
		String finalId = id;
		waifus.removeIf(w -> w.getOwner() == null || !w.getOwner().equals(finalId));
		if (waifus.isEmpty())
		{
			msg.getChannel().sendMessage("\uD83D\uDE2D tu n'as pas de waifus, fais >waifu pour en avoir une !").queue();
			return;
		}
		StringBuilder sb = new StringBuilder().append("waifus de ").append(msg.getGuild().getMemberById(id).getEffectiveName()).append("\n\n");
		for (waifu w : waifus)
			sb.append(w.getId()).append(" ").append(w.getName()).append(" de ").append(w.getOrigin()).append("\n");
		MessageBuilder mb = new MessageBuilder().append(sb);
		Queue<Message> ml = mb.buildAll();
		while (!ml.isEmpty())
			msg.getChannel().sendMessage(ml.poll()).queue();
	}
	@Command(name = "waifu", description = "instance des waifus", type = ExecutorType.USER)
	private void waifu(Message msg) throws ExecutionException, InterruptedException {
		if (isMaintenance) {
			msg.getChannel().sendMessage("le bot est actuellement en maintenance").queue();
			return;
		}
		WaifuCommand.CommandMain(msg);
	}

	@Command(name = "cki", description = "mais qui est-il !", type = ExecutorType.USER)
	private void cki(Message msg){
		ckimain(msg);
	}

	@Command(name = "test", type = Command.ExecutorType.USER)
	private void test(Message msg) throws IOException {
		if (msg.getMember().getPermissions().contains(Permission.ADMINISTRATOR))
		{
			ArrayList<waifu> waifus = waifu.getAllWaifu();
			for (waifu waifu : waifus)
			{
				if (waifu.getOwner().equals("420655502276558848"))
					waifu.setOwner(null);
			}
		}
	}
}