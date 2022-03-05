/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   CommandDefault.java                                :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 12:46:08 by gchatain          #+#    #+#             */
/*   Updated: 2022/03/05 18:13:37 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.command;


import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.builder.Command.ExecutorType;
import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.objects.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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

	@Command(name = "-", type = ExecutorType.CONSOLE)
	private void stop(JDA jda) throws IOException {
		jda.getPresence().setStatus(OnlineStatus.OFFLINE);
		botDiscord.setRunning(false);
	}


	@Command(name = "info", description = "information sur un joueur", type = ExecutorType.USER)
	private void info(MessageChannel channel, Message msg, JDA jda) {
		Member mem = msg.getMember();
		if (msg.getMentionedMembers().size() != 0) {
			mem = msg.getMentionedMembers().get(0);
		}
		EmbedBuilder builder = new EmbedBuilder();
		builder.setAuthor(mem.getUser().getName(), null, mem.getUser().getAvatarUrl() + "?size=256");
		builder.setTitle("Informations");
		builder.setDescription("> surnom :" + mem.getEffectiveName() + "\n" +
				"> état :" + mem.getOnlineStatus().name() + "\n" +
				"> rejoint le serveur le " + mem.getTimeJoined().getDayOfMonth() + "/" + mem.getTimeJoined().getMonthValue() + "/" + mem.getTimeJoined().getYear() + "\n" +
				"> hypesquad " + UserExtenders.getHypesquad(mem) + "\n" +
				"> creer son compte le " + mem.getTimeCreated().getDayOfMonth() + "/" + mem.getTimeCreated().getMonthValue() + "/" + mem.getTimeCreated().getYear() + "\n" +
				"> messages total : " + UserExtenders.getAllmsg(mem));
		builder.setColor(Color.green);
	}

	@Command(name = "poll", description = "faites des sondages rapidements", type = ExecutorType.USER)
	private void poll(Message msg) {
		String message = msg.getContentRaw().substring(">poll ".length());
		String[] args = message.substring(message.split("\n")[0].length() + 1).split("\n");
		String author = msg.getAuthor().getId();
		msg.delete().queue();
		ArrayList<SelectOption> options = new ArrayList<>();
		for (String arg : args) {
			for (SelectOption op : options)
				if (op.getLabel().equals(arg)) {
					msg.getChannel().sendMessage("sex").reference(msg).queue();
					return;
				}
			options.add(new SelectOptionImpl(arg, arg));
		}
		SelectMenuImpl selectionMenu = new SelectMenuImpl(message.split("\n")[0], "selectionnez un choix", 1, 1, false, options);
		msg = msg.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription("chargement").build()).setActionRow(selectionMenu).complete();
		PollMessage pm = new PollMessage(msg.getId(), Arrays.asList(args), author, msg.getGuild(), msg.getTextChannel().getId(), message.split("\n")[0]);
		msg.editMessageEmbeds(pm.getMessageEmbed(msg.getGuild())).queue();
	}

	@Command(name = "role", description = "permettre de creer un role", type = ExecutorType.USER)
	private void role(Message msg) {
		String[] args = msg.getContentRaw().split(" ");
		if (args.length == 3) {
			msg.addReaction(args[2]).queue();
			Role r = msg.getGuild().createRole().setName("©◊ß" + args[1]).setMentionable(true).setColor(new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255))).complete();
			EmbedBuilder eb = new EmbedBuilder().setTitle("nouveau role").setDescription(r.getName().replace("©◊ß", "") + "\n" + args[2]).setFooter(r.getId());
			ArrayList<SelectOption> options = new ArrayList<>();
			for (MessageReact mr : MessageReact.message)
				options.add(new SelectOptionImpl(mr.getTitle(), mr.getTitle()));
			SelectMenuImpl selectionMenu = new SelectMenuImpl("role", "catégorie", 1, 1, false, options);
			msg.getChannel().sendMessageEmbeds(eb.build()).setActionRow(selectionMenu).complete();
		} else {
			msg.getChannel().sendMessage("erreur argument >role <nom> <emote>").queue();
		}
	}

	@Command(name = "cki", type = Command.ExecutorType.USER)
	private void cki(Message msg) {
		ckimain(msg);
	}

	@Command(name = "test", type = Command.ExecutorType.USER)
	private void test(Message msg) throws IOException {
		msg.getChannel().sendMessage("sex").queue();
	}
}