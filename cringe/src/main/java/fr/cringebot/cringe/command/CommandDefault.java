/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   CommandDefault.java                                :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 12:46:08 by gchatain          #+#    #+#             */
/*   Updated: 2021/12/05 12:46:08 by gchatain         ###   ########lyon.fr   */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.command;


import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.builder.Command.ExecutorType;
import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.objects.Pokemon;
import fr.cringebot.cringe.objects.UserExtenders;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

/**
 * fichier de commandes de base
 */
public class CommandDefault {

	private final BotDiscord botDiscord;

	/**
	 * intialisation de l'objet
	 * @param botDiscord
	 * @param commandMap
	 */
	public CommandDefault(BotDiscord botDiscord, CommandMap commandMap) {
		this.botDiscord = botDiscord;
	}

	@Command(name = "arret", type = ExecutorType.CONSOLE)
	private void stop() {
		botDiscord.setRunning(false);
	}


	@Command(name = "info", description = "information sur un joueur", type = ExecutorType.USER)
	private void info(MessageChannel channel, Message msg) {
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
		channel.sendMessageEmbeds(builder.build()).queue();
	}
}