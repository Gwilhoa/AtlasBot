/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   memesEvent.java                                    :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 13:00:28 by gchatain          #+#    #+#             */
/*   Updated: 2022/06/06 22:31:58 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.event;

import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.objects.DetectorAttachment;
import fr.cringebot.cringe.objects.Emotes;
import fr.cringebot.cringe.objects.imgExtenders;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import static fr.cringebot.cringe.objects.Emotes.getEmote;

public class memesEvent {
	public static void addReaction(Message message, MessageReaction react) {
		int u = 0;
		int d = 0;
		for ( MessageReaction reaction : message.getReactions()) {
			if (getEmote(reaction.getReactionEmote()).equals("rirededroite"))
				u = reaction.getCount();
			else if (getEmote(reaction.getReactionEmote()).equals("porte"))
				d = reaction.getCount();
		}
		if (u - d <= -5)
			message.delete().queue();
		else if (u - d >= 5) {
			if (DetectorAttachment.isYoutube(message.getContentRaw()) || DetectorAttachment.isTenor(message.getContentRaw())) {
				message.getGuild().getTextChannelById("911549374696411156").sendMessage(message).queue();
			} else if (message.getEmbeds().isEmpty()) {
				File f = message.getAttachments().get(0).getProxy().downloadToPath().join().toFile();
				message.getGuild().getTextChannelById("911549374696411156").sendMessage(message).addFile(f).queue();
				f.delete();
			} else {
				if (message.getEmbeds().get(0).getAuthor() != null && message.getEmbeds().get(0).getAuthor().getName().equalsIgnoreCase("reddit"))
					repostReddit(message, null, message.getGuild().getTextChannelById("911549374696411156"));
				else if (message.getEmbeds().get(0).getAuthor() != null &&message.getEmbeds().get(0).getAuthor().getName().equalsIgnoreCase("twitter"))
					repostTwitter(message, null, message.getGuild().getTextChannelById("911549374696411156"));
				else {
					String name = message.getEmbeds().get(0).getImage().getUrl().split(" ")[0].split("/")[message.getEmbeds().get(0).getImage().getUrl().split("/").length - 1];
					try (BufferedInputStream bis = new BufferedInputStream(new URL(message.getEmbeds().get(0).getImage().getUrl()).openStream());
						 FileOutputStream fos = new FileOutputStream(name)) {
						byte[] data = new byte[1024];
						int byteContent;
						while ((byteContent = bis.read(data, 0, 1024)) != -1) {
							fos.write(data, 0, byteContent);
						}
					} catch (IOException e) {
						e.printStackTrace(System.out);
					}
					File f = new File(name);
					message.getGuild().getTextChannelById("911549374696411156").sendFile(f).setEmbeds(
							new EmbedBuilder()
									.setDescription(message.getEmbeds().get(0).getDescription())
									.setImage("attachment://" + f.getName())
									.setFooter(message.getEmbeds().get(0).getFooter().getText(), message.getEmbeds().get(0).getFooter().getIconUrl())
									.setColor(Color.GREEN)
									.build()
					).queue();
					f.delete();
				}
			}
			message.delete().queue();
		}
	}

	/**
	 * fonction qui va analyser les messages pour préciser la nature d'un meme
	 *
	 * @param msg
	 */
	public static void postmeme(Message msg) {
		try {
			processmeme(msg);
		} catch (Exception e) {
			msg.getChannel().sendMessage("putain les mêmes bordel GUIGUI ! VIENS Là\ncontenu :\n" + msg.getContentRaw()).queue();
			e.printStackTrace();
		}
	}

	public static void processmeme(Message msg) throws IOException {
		String channel = "461606547064356864";
		String[] args = msg.getContentRaw().split("\\s");
		Message ret = null;
		String Content = null;
		String ext = null;
		File f = null;
		int i = 0;
		if (DetectorAttachment.isYoutube(msg.getContentRaw()) || DetectorAttachment.isTenor(msg.getContentRaw())) {
			ret = msg.getGuild().getTextChannelById(channel).sendMessage(msg.getContentRaw()).complete();
			ret.addReaction(msg.getGuild().getEmoteById(Emotes.rirederoite)).and(ret.addReaction(msg.getGuild().getEmoteById(Emotes.anto))).and(ret.addReaction(msg.getGuild().getEmoteById(Emotes.porte))).queue();
			msg.delete().queue();
			return;
		}
		for (String mot : args) {
			if (DetectorAttachment.isTwitter(mot)) {
				ret = repostTwitter(msg, mot, msg.getGuild().getTextChannelById(channel));
				ret(msg, ret);
				return;
			}
			if (DetectorAttachment.isReddit(mot)) {
				ret = repostReddit(msg, mot, msg.getGuild().getTextChannelById(channel));
				ret(msg, ret);
			}
		}
		if (ret == null) {
			if (!msg.getAttachments().isEmpty()) {
				f = msg.getAttachments().get(0).getProxy().downloadToPath().join().toFile();
				Content = msg.getContentRaw();
			} else {
				for (String mot : args) {
					if (DetectorAttachment.isImage(mot) || DetectorAttachment.isVideo(mot)) {
						f = imgExtenders.getFile(new URL(mot), mot.replace("?", "/").split("/")[mot.split("/").length - 1], null);
						Content = msg.getContentRaw().replace(mot, "");
						break;
					}
				}
				if (f == null)
					return;
			}
			ext = FilenameUtils.getExtension(f.getName());
			if (ext.equals("fr") || ext.equals("com") || ext.equals("net") || ext.equals("org"))
				return;
		}
		if (f != null && f.length() >= 8000000) {
			f.delete();
			if (msg.getReactions().isEmpty())
				msg.addReaction(msg.getGuild().getEmoteById(Emotes.anto)).queue();
			return;
		}
		if (ext.equals("mp4") || ext.equals("mov") || ext.equals("webm")) {
			if (Content == null)
				ret = msg.getGuild().getTextChannelById(channel).sendMessage("> " + msg.getMember().getUser().getName()).addFile(f).complete();
			else
				ret = msg.getGuild().getTextChannelById(channel).sendMessage("> " + msg.getMember().getUser().getName() + "\n\n" + Content).addFile(f).complete();
		} else {
			EmbedBuilder eb = new EmbedBuilder()
					.setImage("attachment://" + f.getName())
					.setFooter(msg.getAuthor().getName(), msg.getAuthor().getAvatarUrl())
					.setColor(Squads.getSquadByMember(msg.getMember()).getSquadRole(msg.getGuild()).getColor());
			if (Content != null)
				eb.setDescription(Content);
			ret = msg.getGuild().getTextChannelById(channel).sendFile(f).setEmbeds(eb.build()).complete();
		}
		if (ret != null)
			ret.addReaction(msg.getGuild().getEmoteById(Emotes.rirederoite)).and(ret.addReaction(msg.getGuild().getEmoteById(Emotes.anto))).and(ret.addReaction(msg.getGuild().getEmoteById(Emotes.porte))).queue();
		msg.delete().queue();
		f.delete();
	}

	private static void ret(Message msg, Message ret) {
		if (ret == null)
			return;
		ret.addReaction(msg.getGuild().getEmoteById(Emotes.rirederoite)).and(ret.addReaction(msg.getGuild().getEmoteById(Emotes.anto))).and(ret.addReaction(msg.getGuild().getEmoteById(Emotes.porte))).queue();
		msg.delete().queue();
		return;
	}

	/**
	 * renvoie un Embed sur un meme de nature reddit
	 *
	 * @param msg
	 * @return
	 */
	public static Message repostReddit(Message msg, String link, TextChannel channel) {
		if (msg.getEmbeds().isEmpty())
			return null;
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
		return channel.sendMessageEmbeds(eb.build()).complete();
	}

	/**
	 * renvoie un Embed de nature twitter
	 *
	 * @param msg
	 * @return
	 */
	public static Message repostTwitter(Message msg, String link, TextChannel channel) {
		if (msg.getEmbeds().isEmpty())
			return null;
		if (link == null) {
			String[] args = msg.getContentRaw().split(" ");
			for (String mot : args)
				if (DetectorAttachment.isTwitter(mot))
					link = mot;
		}
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
				.setAuthor("Twitter", msg.getEmbeds().get(0).getUrl(), msg.getEmbeds().get(0).getFooter().getIconUrl());
		if (msg.getEmbeds().get(0).getImage() != null)
			eb.setImage(msg.getEmbeds().get(0).getImage().getUrl());
		return channel.sendMessageEmbeds(eb.build()).complete();
	}
}
