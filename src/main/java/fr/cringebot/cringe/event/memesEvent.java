/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   memesEvent.java                                    :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 13:00:28 by gchatain          #+#    #+#             */
/*   Updated: 2022/02/01 09:45:38 by gchatain         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.event;

import fr.cringebot.cringe.objects.DetectorAttachment;
import fr.cringebot.cringe.objects.Emotes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;

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
		if (u-d <= -5)
			message.delete().queue();
		else if (u-d >= 5) {
			if (message.getEmbeds().isEmpty()){
				message.getGuild().getTextChannelById("911549374696411156").sendMessage(message).addFile(message.getAttachments().get(0).downloadToFile().join()).queue();
			} else {
				String name = message.getEmbeds().get(0).getImage().getUrl().split(" ")[0].split("/")[message.getEmbeds().get(0).getImage().getUrl().split("/").length - 1];
		try (BufferedInputStream bis = new BufferedInputStream(new URL(message.getEmbeds().get(0).getImage().getUrl()).openStream());
		FileOutputStream fos = new FileOutputStream(name)){
			byte data[] = new byte[1024];
			int byteContent;
			while ((byteContent = bis.read(data, 0, 1024)) != -1){
					fos.write(data, 0, byteContent);
				}
			}
		catch (IOException e) {
   		e.printStackTrace(System.out);}
				File f = new File(name);
				message.getGuild().getTextChannelById("911549374696411156").sendFile(f).setEmbeds(
				new EmbedBuilder()
				.setDescription(message.getEmbeds().get(0).getDescription())
				.setImage("attachment://" + f.getName())
				.setFooter(message.getEmbeds().get(0).getFooter().getText(), message.getEmbeds().get(0).getFooter().getIconUrl())
				.setColor(Color.GREEN)
				.build()
				).queue();
			}
			message.delete().queue();
		}
	}
	/**
	 * fonction qui va analyser les messages pour préciser la nature d'un meme
	 * @param msg
	 * @throws IOException
	 */
	public static void postmeme(Message msg) throws IOException {
		if (DetectorAttachment.isTwitter(msg.getContentRaw()) || DetectorAttachment.isReddit(msg.getContentRaw()) || DetectorAttachment.isYoutube(msg.getContentRaw()))
		{
			if (!msg.getEmbeds().isEmpty())
			{
				Message temp = null;
				if (DetectorAttachment.isTwitter(msg.getContentRaw()))
					temp = repostTwitter(msg);
				if (DetectorAttachment.isReddit(msg.getContentRaw()))
					temp = repostReddit(msg);
				if (temp != null)
				{
					msg.delete().queue();
					msg = temp;
				}
				msg.addReaction(msg.getGuild().getEmoteById(Emotes.rirederoite))
				.and(msg.addReaction(msg.getGuild().getEmoteById(Emotes.anto)))
				.and(msg.addReaction(msg.getGuild().getEmoteById(Emotes.porte))).queue();
			}
		return;
		}
	String name;
	String content = null;
	if (msg.getAttachments().isEmpty())
	{
		System.out.print(msg.getContentRaw().split(" ")[0]);
		content = msg.getContentRaw().substring(msg.getContentRaw().split(" ")[0].length());
		name = msg.getContentRaw().split(" ")[0].split("/")[msg.getContentRaw().split("/").length - 1];
		try (BufferedInputStream bis = new BufferedInputStream(new URL(msg.getContentRaw().split(" ")[0]).openStream());
		FileOutputStream fos = new FileOutputStream(name)) {
			byte data[] = new byte[1024];
			int byteContent;
			while ((byteContent = bis.read(data, 0, 1024)) != -1) {
				fos.write(data, 0, byteContent);
			}
		}
		catch (IOException e) {
   		e.printStackTrace(System.out);}
	}
	else 
	{
		content = msg.getContentRaw();
		name = msg.getAttachments().get(0).getFileName();
		msg.getAttachments().get(0).downloadToFile().join();
	}
	File f = new File(name);
	if (f.length() >= 8000000)
	{
		f.delete();
		if (msg.getReactions().isEmpty())
			msg.addReaction(msg.getGuild().getEmoteById(Emotes.anto)).queue();
		return;
	}
	String avatar = msg.getAuthor().getEffectiveAvatarUrl();
	name = msg.getAuthor().getName();
	msg.delete().and(msg.getChannel().sendTyping()).queue();
	if (FilenameUtils.getExtension(f.getName()).equals("mp4") || FilenameUtils.getExtension(f.getName()).equals("mov") || FilenameUtils.getExtension(f.getName()).equals("webm"))
	{
		if (content == null)
			msg = msg.getChannel().sendMessage("by > "+ name).addFile(f).complete();
		else
			msg = msg.getChannel().sendMessage("by > "+ name + "\n" + content).addFile(f).complete();
	}
	else
		msg = msg.getChannel().sendFile(f).setEmbeds(
			new EmbedBuilder()
			.setDescription(content)
			.setImage("attachment://" + f.getName())
			.setFooter(name, avatar)
			.setColor(Color.RED)
			.build()
			).complete();
	f.delete();
	msg.addReaction(msg.getGuild().getEmoteById(Emotes.rirederoite))
	.and(msg.addReaction(msg.getGuild().getEmoteById(Emotes.anto)))
	.and(msg.addReaction(msg.getGuild().getEmoteById(Emotes.porte))).queue();
}

	/**
	 * renvoie un Embed sur un meme de nature reddit
	 * @param msg
	 * @return
	 */
	public static Message repostReddit(Message msg) {
		EmbedBuilder eb = new EmbedBuilder()
				.setTitle(msg.getEmbeds().get(0).getTitle());
		eb.setAuthor("reddit", msg.getContentRaw(), "https://www.elementaryos-fr.org/wp-content/uploads/2019/08/logo-reddit.png");
		eb.setFooter(msg.getAuthor().getName(), msg.getAuthor().getEffectiveAvatarUrl());
		eb.setImage(msg.getEmbeds().get(0).getThumbnail().getUrl());
		eb.setColor(new Color(255, 69, 0));
		return msg.getChannel().sendMessageEmbeds(eb.build()).complete();
	}

	/**
	 * renvoie un Embed de nature twitter
	 * @param msg
	 * @return
	 */
	public static Message repostTwitter(Message msg) {
		EmbedBuilder eb = new EmbedBuilder()
				.setDescription(msg.getEmbeds().get(0).getDescription())
				.setTitle(msg.getEmbeds().get(0).getAuthor().getName())
				.setColor(msg.getEmbeds().get(0).getColor())
				.setFooter(msg.getAuthor().getName(), msg.getAuthor().getEffectiveAvatarUrl())
				.setAuthor("twitter", msg.getEmbeds().get(0).getUrl(), msg.getEmbeds().get(0).getFooter().getIconUrl());
		return msg.getChannel().sendMessageEmbeds(eb.build()).complete();
	}
}
