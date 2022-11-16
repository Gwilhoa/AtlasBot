

package fr.cringebot.cringe.command;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.jcraft.jsch.JSchException;
import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.CommandBuilder.top;
import fr.cringebot.cringe.Request.Members;
import fr.cringebot.cringe.Request.Squads;
import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.builder.Command.ExecutorType;
import fr.cringebot.cringe.builder.CommandMap;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static fr.cringebot.cringe.objects.imgExtenders.resize;

/**
 * fichier de commandes de base
 */
public class CommandListener {

	private final BotDiscord botDiscord;
	/**
	 * intialisation de l'objet
	 *
	 * @param botDiscord
	 * @param commandMap
	 */
	public CommandListener(BotDiscord botDiscord, CommandMap commandMap) {
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

	@Command(name = "newsquad", description = "ajouter une escouade", type = ExecutorType.USER)
	private void addsquad(Message msg) {
		if (msg.getMember().getPermissions().contains(Permission.ADMINISTRATOR) && !msg.getMentions().getRoles().isEmpty()) {
			try {
				Squads.newSquads(msg.getMentions().getRoles().get(0).getName(), msg.getMentions().getRoles().get(0).getId(), msg.getMentions().getRoles().get(0).getColor());
			} catch (ConnectException e) {
				msg.getChannel().sendMessage("disconnected").queue();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Command(name = "newmember", description = "ajouter un membre inexistant à une escouade", type = ExecutorType.USER)
	private void newMember(Message msg)
	{
		msg.getChannel().sendMessage("coming soon").queue();
	}
	@Command(name = "changesquad", description = "changer d'escouade quelqu'un", type = ExecutorType.USER)
	private void ChangeSquad(Message msg)
	{
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "bresil", description= "you are going to brazil", type = ExecutorType.USER)
	private void bresil(Message msg) {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "info", description = "information sur un joueur", type = ExecutorType.USER)
	private void info(MessageChannel channel, Message msg) {
		Member member = msg.getMember();
		if (msg.getMentions().getMembers().size() > 0) {
			member = msg.getMentions().getMembers().get(0);
		}
		try {
			Members.getMember(member);
		} catch (ConnectException e) {
			channel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.RED).setTitle("Erreur").setDescription("Déconnecté").build()).queue();
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "shop", description = "ouvrir le shopping")
	private void shop(Message msg) {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "gift", description = "des cadeaux ?", type = ExecutorType.USER)
	private void gift(Message msg) throws InterruptedException, IOException {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "top", description = "regarder le classement des escouades")
	private void top(Message msg, String[] args) {
		if (args.length == 0)
			msg.replyEmbeds(top.CommandTop(null, msg.getGuild()).build()).queue();
		else if (args.length == 1)
			msg.replyEmbeds(top.CommandTop(args[0], msg.getGuild()).build()).queue();
		else
			msg.replyEmbeds(new EmbedBuilder().setColor(Color.RED).setTitle("Erreur").setDescription("Nombre d'arguments incorrect").build()).queue();
	}

	@Command(name = "pay", description = "payer un ami", type = ExecutorType.USER)
	private void pay(Message msg) {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "poll", description = "faites des sondages rapidements", type = ExecutorType.USER)
	private void poll(Message msg) {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "role", description = "permettre de creer un role", type = ExecutorType.USER)
	private void role(Message msg) {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "harem", description = "la listes des waifus", type = ExecutorType.USER)
	private void harem(Message msg){
		msg.getChannel().sendMessage("coming soon").queue();
	}
	@Command(name = "waifu", description = "instance des waifus", type = ExecutorType.USER)
	private void waifu(Message msg) throws ExecutionException, InterruptedException, IOException, JSchException {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "inventory", description = "afficher ton inventaire", type = ExecutorType.USER)
	private void inventory(Message msg) {
		msg.getChannel().sendMessage("coming soon").queue();}

	@Command(name = "cki", description = "mais qui est-il !", type = ExecutorType.USER)
	private void cki(Message msg){
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "reset", type = Command.ExecutorType.USER)
	private void reset(Message msg) throws IOException {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "clear", description = "let's clean", type = ExecutorType.USER)
	private void clear(Message msg){
		if (msg.getContentRaw().split(" ").length > 1){
			try {
				int arg = Integer.parseInt(msg.getContentRaw().split(" ")[1]);
				msg.getChannel().getHistoryBefore(msg, arg).queue((mh) -> msg.getChannel().purgeMessages(mh.getRetrievedHistory()));
				msg.delete().queue();
			} catch (IndexOutOfBoundsException | NumberFormatException ignored) {
				msg.getChannel().sendMessage("Mauvais usage de la commande ! il faut mettre par exemple : !clear 5").queue();
			}

		}
		else {
			if (msg.getReferencedMessage() != null) {
				MessageHistory msgs = msg.getChannel().getHistoryAfter(msg.getMessageReference().getMessageId(),100).complete();
				while(msgs.getMessageById(msg.getId()) == null){
					msgs.retrieveFuture(100).complete();
				}
				final int chunkSize = 100;
				final AtomicInteger counter = new AtomicInteger();
				for(List<Message> v : msgs.getRetrievedHistory().stream().dropWhile((m)-> !m.getId().equals(msg.getId())).collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize)).values()){
					msg.getGuildChannel().deleteMessages(v).queue();
				}
			}
		}
	}

	@Command(name = "meteo", type = Command.ExecutorType.USER, description = "donne la météo")
	private void meteo(Guild guild, TextChannel textChannel, Message msg){
		String ville = "shrek";
		if (msg.getContentRaw().split(" ").length != 1)
			ville = msg.getContentRaw().split(" ")[1];
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			URLConnection connection = new URL("https://wttr.in/"+ville+"_3tqp_lang=fr.png").openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0");
			BufferedImage im = ImageIO.read(connection.getInputStream());
			im = resize(im, 1032*2, 546*2, 0, 0, true);
			im = resize(im, 1032*2, 546*2, 0, 0, false);
			ImageIO.write(im, "png", baos);
			textChannel.sendFile(baos.toByteArray(), "meteo.png").queue();
		} catch (IOException e) {
			textChannel.sendMessage("météo introuvable").queue();
		}

	}

	@Command(name = "removesquad", description = "supprimer une escouade", type = ExecutorType.USER)
	private void RemoveSquad(Message msg) {
		if (msg.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
		}
	}


	@Command(name = "test", description = "commande provisoire", type = ExecutorType.USER)
	private void test(Message msg) throws IOException, InterruptedException {}
}