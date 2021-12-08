/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   CommandDefault.java                                :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 12:46:08 by gchatain          #+#    #+#             */
/*   Updated: 2021/12/08 11:46:32 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.command;


import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.builder.Command.ExecutorType;
import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.objects.PollMessage;
import fr.cringebot.cringe.objects.SelectOptionImpl;
import fr.cringebot.cringe.objects.UserExtenders;
import fr.cringebot.cringe.pokemon.objects.Pokemon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.SelectionMenuImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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

	@Command(name = "arret", type = ExecutorType.CONSOLE)
	private void stop() {
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
		SelectionMenuImpl selectionMenu = new SelectionMenuImpl(message.split("\n")[0], "selectionnez un choix", 1, 1, false, options);
		msg = msg.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription("chargement").build()).setActionRow(selectionMenu).complete();
		PollMessage pm = new PollMessage(msg.getId(), Arrays.asList(args), author, msg.getGuild(), msg.getTextChannel().getId(), message.split("\n")[0]);
		msg.editMessageEmbeds(pm.getMessageEmbed(msg.getGuild())).queue();
	}

	@Command(name = "pokedex", type = Command.ExecutorType.USER)
	private void pokedex(Message msg) {
		new Thread(() -> {
			msg.getChannel().sendTyping().queue();
			String str = Pokemon.getRandomPokemon().getRealname();
			if (msg.getContentRaw().split(" ").length == 2) {
				str = msg.getContentRaw().split(" ")[1];
			}
			Document doc;
			EmbedBuilder eb = new EmbedBuilder();
			try {
				doc = Jsoup.connect("https://www.pokemon.com/fr/pokedex/" + str).get();
				eb.setTitle(str);
				str = doc.select("body.fr.fluid.custom-form-elements:nth-child(2) div.container:nth-child(5) section.section.pokedex-pokemon-details:nth-child(3) div.column-6.push-7:nth-child(2) div.pokedex-pokemon-details-right div.version-descriptions.active:nth-child(1) > p.version-y").text();
				eb.setDescription(str);
				eb.setImage("https://assets.pokemon.com/assets/cms2/img/pokedex/detail/" + doc.selectXpath("//body[1]/div[4]/section[1]/div[2]/div[1]/span[1]").text().substring(3) + ".png");
			} catch (IOException e) {
				eb.setTitle(msg.getContentRaw().split(" ")[1]);
				eb.setDescription("ce pokemon existe pas");
			}
			msg.getChannel().sendMessageEmbeds(eb.build()).queue();
		}).start();
	}
}