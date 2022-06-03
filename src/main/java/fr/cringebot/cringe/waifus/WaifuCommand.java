package fr.cringebot.cringe.waifus;

import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.objects.SelectOptionImpl;
import fr.cringebot.cringe.objects.StringExtenders;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static fr.cringebot.BotDiscord.isMaintenance;

public class WaifuCommand {
	private static Message msg;
	public static Lock waifuLock = new ReentrantLock();

	public static void CommandMain(Message msg) throws ExecutionException, InterruptedException {

		if (msg.getContentRaw().split(" ").length == 1) {
			newWaifu(msg);
			return;
		}
		if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("add"))
			addwaifu(msg);
		else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("list")){
			EmbedBuilder eb = new EmbedBuilder().setTitle("Listes des waifus").setDescription("chargement...");
			if (msg.getContentRaw().split(" ").length > 2)
				eb.setAuthor(msg.getContentRaw().substring(">Waifu list ".length()));
			Message ls = msg.getChannel().sendMessageEmbeds(eb.build()).complete();
			ls.addReaction("◀️").and(ls.addReaction("▶️")).queue();
			listwaifu(ls);
		}
		else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("info"))
			infowaifu(msg);
		else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("setdescription"))
			setDescription(msg);
		else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("delete"))
			delwaifu(msg);
		else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("setname"))
			setName(msg);
		else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("reset"))
			reset(msg);
		else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("setimage"))
			setImage(msg);
		else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("setorigin"))
			setOrigin(msg);
		else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("trade"))
			trade(msg);
		else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("release"))
			release(msg);
		else
			newWaifu(msg);

	}

	private static void trade(Message msg) {
		String id = msg.getContentRaw().split(" ")[2];
		String id2 = msg.getContentRaw().split(" ")[3];
		Waifu w1 = Waifu.getWaifuById(Integer.parseInt(id));
		Waifu w2 = Waifu.getWaifuById(Integer.parseInt(id2));
		if (w1.getOwner() == null || !w1.getOwner().equals(msg.getMember().getId()))
			msg.getChannel().sendMessage("tu n'es pas le propriétaire de "+ w1.getName()).queue();
		else if (w2.getOwner() == null)
			msg.getChannel().sendMessage("cette Waifu appartient à personne").queue();
		else {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor(id + " " + id2);
			eb.setTitle("Demande d'échange");
			eb.setDescription(msg.getMember().getAsMention() + " demande " + w2.getName() + " contre " + w1.getName() + "\n");
			eb.setFooter(w2.getOwner());
			ArrayList<ActionRow> bttn = new ArrayList<>();
			bttn.add(ActionRow.of(Button.primary("waifutrade_oui", "oui")));
			bttn.add(ActionRow.of(Button.danger("waifutrade_non", "non")));
			msg.getChannel().sendMessage(msg.getGuild().getMemberById(w2.getOwner()).getAsMention()).setEmbeds(eb.build()).setActionRows(bttn).queue();
		}
	}

	private static void setImage(Message msg) {
		if (!msg.getChannel().getId().equals("975087822618910800")) {
			msg.getChannel().sendMessage("non").queue();
			return;
		}
		String id = msg.getContentRaw().split(" ")[2];
		Waifu w = Waifu.getWaifuById(Integer.parseInt(id));
		if (w == null) {
			msg.getChannel().sendMessage("id non défini").queue();
			return;
		}
		if (msg.getAttachments().isEmpty()){
			msg.getChannel().sendMessage("t'es une merde").queue();
			return;
		}
		w.setFile(msg.getAttachments().get(0));
		msg.addReaction("\uD83D\uDC4C").queue();
	}

	private static void reset(Message msg) {
		if (!msg.getMember().getId().equals("315431392789921793"))
		{
			msg.getChannel().sendMessage("https://tenor.com/view/fanta-pas-toi-qui-d%C3%A9cide-serious-selfie-gif-13900956").queue();
			return;
		}
		ArrayList<Waifu> waifus = Waifu.getAllWaifu();
		for (Waifu w : waifus)
			w.setOwner(null);
	}

	private static void newWaifu(Message msg) throws InterruptedException {
		if (isMaintenance) {
			msg.getChannel().sendMessage("le bot est actuellement en maintenance").queue();
			return;
		}
		else if (msg.getMember().getRoles().contains(msg.getGuild().getRoleById(BotDiscord.SecondaryRoleId))){
			msg.getChannel().sendMessage("Tu es un compte secondaire et mo, j'aime pas les comptes secondaires").queue();
			return;
		}
		else if (Waifu.timeleft(msg.getMember().getId()) < 0){
			long t = Waifu.timeleft(msg.getMember().getId());
			long th = (10800000 - t) / 3600000;
			long tmin = (10800000 - th * 3600000 - t) / 60000;
			long ts = (10800000 - th * 3600000 - tmin * 60000 - t) / 1000;
			msg.getChannel().sendMessage("il te reste " + th + "h, " + tmin + "min et " + ts + " secondes avant de chercher une nouvelle Waifu").queue();
			return;

		}
		Waifu.setTime(msg.getMember().getId());
		Waifu w;
		w = Waifu.getAvailableWaifu().get(new Random().nextInt(Waifu.getAvailableWaifu().size() - 1));
		File f = new File(w.getProfile());
		w.setOwner(msg.getMember().getId());
		EmbedBuilder eb = new EmbedBuilder();
		eb.setImage("attachment://"+f.getName());
		eb.setTitle("Nouvelle Waifu !");
		eb.setDescription("ta nouvelle Waifu est " + w.getName() + " de " + w.getOrigin());
		eb.setFooter("félicitation !!");
		waifuLock.lock();
		Thread.sleep(100);
		MessageAction toSend = msg.getChannel().sendMessageEmbeds(eb.build());
		try(DataInputStream str = new DataInputStream(new FileInputStream(f))){
			byte[] bytes = new byte[(int) f.length()];
			str.readFully(bytes);
			toSend.addFile(bytes, f.getName()).complete();
		} catch (IOException e) {
			//Wrap et remonter
			throw new RuntimeException(e);
		}
		waifuLock.unlock();
	}

public static void addwaifu(Message msg) throws ExecutionException, InterruptedException {
		if (!msg.getChannel().getId().equals("975087822618910800")) {
			msg.getChannel().sendMessage("non").queue();
			return;
		}
		String[] args = msg.getContentRaw().split("\n");
		if (args[0].split(" ").length <= 2) {
			msg.getChannel().sendMessage("SOMBRE MERDE").queue();
			return;
		}
		if (!msg.getAttachments().isEmpty() && msg.getAttachments().size() == 1) {
		ArrayList<SelectOption> options = new ArrayList<>();
		for (Waifu.Type tpe : Waifu.Type.values())
			options.add(new SelectOptionImpl("Catégorie : " + tpe.name(), tpe.name()));
		options.add(new SelectOptionImpl("Annuler", "stop"));
		SelectMenuImpl selectionMenu = new SelectMenuImpl("Waifu", "selectionnez un choix", 1, 1, false, options);
			MessageAction toSend = msg.getChannel().sendMessageEmbeds(new EmbedBuilder().setTitle(args[0].substring(">Waifu add".length())).setFooter(args[1]).setDescription(msg.getContentRaw().substring(args[0].length() + args[1].length() + 1)).build());
			File f = new File(msg.getAttachments().get(0).downloadToFile().get().getName());
			try(DataInputStream str = new DataInputStream(new FileInputStream(f))){
				byte[] bytes = new byte[(int) f.length()];
				str.readFully(bytes);
				toSend.addFile(bytes, f.getName()).setActionRow(selectionMenu).complete();
			} catch (IOException e) {
				//Wrap et remonter
				throw new RuntimeException(e);
			}
		} else {
			msg.getChannel().sendMessage("t'es une merde").queue();
		}
	}
	private static void sendEmbedInfo(Waifu w, TextChannel tc) throws InterruptedException {
		EmbedBuilder eb = new EmbedBuilder();
		File f = new File(w.getProfile());
		eb.setAuthor(w.getOrigin());
		eb.setTitle("Information : " + w.getName() + "\nIdentifiant : " + w.getId());
		eb.setImage("attachment://"+f.getName());
		if (w.getOwner() != null)
			eb.setFooter("appartient à "+ tc.getGuild().getMemberById(w.getOwner()).getEffectiveName(), tc.getGuild().getMemberById(w.getOwner()).getUser().getAvatarUrl());
		else
			eb.setFooter("disponible");
		eb.setDescription(w.getDescription());
		waifuLock.lock();
		Thread.sleep(100);
		MessageAction toSend = tc.sendMessageEmbeds(eb.build());
		try(DataInputStream str = new DataInputStream(new FileInputStream(f))){
			byte[] bytes = new byte[(int) f.length()];
			str.readFully(bytes);
			toSend.addFile(bytes, f.getName()).complete();
		} catch (IOException e) {
			//Wrap et remonter
			throw new RuntimeException(e);
		}
		waifuLock.unlock();
	}

	public static void infowaifu(Message msg) throws InterruptedException {
		ArrayList<Waifu> w = Waifu.getWaifubyName(msg.getContentRaw().substring(">Waifu info ".length()));
		if (w != null) {
			for (Waifu waif : w) {
				sendEmbedInfo(waif, msg.getTextChannel());
			}
		}
		else
		{
			Waifu wid;
			try {
				wid = Waifu.getWaifuById(Integer.parseInt(msg.getContentRaw().split(" ")[2]));
			}
			catch (NumberFormatException e){
				msg.getChannel().sendMessage("je ne connais pas de Waifu à ce nom ou cet id").queue();
				return;
			}
			if (wid != null)
				sendEmbedInfo(wid, msg.getTextChannel());
			else {
				msg.getChannel().sendMessage("je ne connais pas de Waifu à ce nom ou cet id").queue();
			}
		}
	}

	public static void listwaifu(Message msg){
		listwaifu(msg, 0);
	}
	public static void listwaifu(Message tc, Integer f) {
		ArrayList<Waifu> waifus = Waifu.getAllWaifu();
		Waifu w;
		String search = null;
		if (tc.getEmbeds().get(0).getAuthor() != null)
			search = tc.getEmbeds().get(0).getAuthor().getName();
		EmbedBuilder eb = new EmbedBuilder();
		if (search != null) {
			eb.setAuthor(search);
			String finalSearch = search;
			waifus.removeIf(wai -> !StringExtenders.startWithIgnoreCase(wai.getOrigin(), finalSearch));
		}
		int	i = f*10;
		if (i > waifus.size() || i < 0)
			return;
		eb.setFooter(f.toString()).setTitle(tc.getEmbeds().get(0).getTitle());
		StringBuilder sb = new StringBuilder();
		String combo = waifus.get(i).getOwner();
		while (i < (f*10)+10)
		{
			if (i < waifus.size()) {
				w = waifus.get(i);
				if (w.getOwner() == null || !w.getOwner().equals(combo))
					combo = null;
				if (w.getOwner() == null)
					sb.append(w.getId()).append(" ").append(w.getName()).append(" de ").append(w.getOrigin()).append("\n");
				else
					sb.append(w.getId()).append(" ").append(w.getName()).append(" de ").append(w.getOrigin()).append(" __").append(tc.getGuild().getMemberById(w.getOwner()).getEffectiveName()).append("__\n");
			}
			i++;
		}
		if (combo != null)
			eb.setColor(Squads.getSquadByMember(combo).getSquadRole(tc.getGuild()).getColor());
		eb.setDescription(sb);
		tc.editMessageEmbeds(eb.build()).queue();
	}

	public static void setDescription(Message msg)
	{
		if (!msg.getChannel().getId().equals("975087822618910800")) {
			msg.getChannel().sendMessage("non").queue();
			return;
		}
		String id = msg.getContentRaw().split(" ")[2];
		Waifu w = Waifu.getWaifuById(Integer.parseInt(id));
		if (w == null) {
			msg.getChannel().sendMessage("id non défini").queue();
			return;
		}
		String name = msg.getContentRaw().substring(">Waifu setdescription  ".length() + id.length());
		w.setDescription(name);
		msg.addReaction("\uD83D\uDC4C").queue();
	}

	public static void setOrigin(Message msg)
	{
		if (!msg.getChannel().getId().equals("975087822618910800")) {
			msg.getChannel().sendMessage("non").queue();
			return;
		}
		String id = msg.getContentRaw().split(" ")[2];
		Waifu w = Waifu.getWaifuById(Integer.parseInt(id));
		if (w == null) {
			msg.getChannel().sendMessage("id non défini").queue();
			return;
		}
		String name = msg.getContentRaw().substring(">Waifu setOrigin  ".length() + id.length());
		w.setOrigin(name);
		msg.addReaction("\uD83D\uDC4C").queue();
	}

	public static void delwaifu(Message msg)
	{
		if (!msg.getChannel().getId().equals("975087822618910800")) {
			msg.getChannel().sendMessage("non").queue();
			return;
		}
		String id = msg.getContentRaw().split("\n")[0];
		Waifu w = Waifu.getWaifuById(Integer.parseInt(id.substring(">Waifu delete ".length())));
		if (w == null) {
			msg.getChannel().sendMessage("id non défini").queue();
			return;
		}
		w.delwaifu();
		msg.addReaction("\uD83D\uDC4C").queue();
	}

	public static void setName(Message msg)
	{
		if (!msg.getChannel().getId().equals("975087822618910800")) {
			msg.getChannel().sendMessage("non").queue();
			return;
		}
		String id = msg.getContentRaw().split(" ")[2];
		Waifu w = Waifu.getWaifuById(Integer.parseInt(id));
		if (w == null) {
			msg.getChannel().sendMessage("id non défini").queue();
			return;
		}
		String name = msg.getContentRaw().substring(">Waifu setname  ".length() + id.length());
		w.setName(name);
		msg.addReaction("\uD83D\uDC4C").queue();
	}

	private static void release(Message msg) {
		if (true)
		{
			msg.getChannel().sendMessage("tu ne peux pas relacher de Waifu pour le moment").queue();
			return;
		}
		String id = msg.getContentRaw().split(" ")[2];
		Waifu w = Waifu.getWaifuById(Integer.parseInt(id));
		if (w.getOwner() == null)
			msg.getChannel().sendMessage(w.getName() + " n'appartient a personne").queue();
		else if (!w.getOwner().equals(msg.getMember().getId()))
			msg.getChannel().sendMessage("tu n'est pas le propriétaire de " + w.getName()).queue();
		else {
			w.setOwner(null);
			msg.getChannel().sendMessage(w.getName() + " a été relâcher").queue();
		}
	}
}


