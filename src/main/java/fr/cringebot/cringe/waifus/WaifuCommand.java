package fr.cringebot.cringe.waifus;

import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.escouades.SquadMember;
import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.objects.SelectOptionImpl;
import fr.cringebot.cringe.objects.StringExtenders;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static fr.cringebot.BotDiscord.isMaintenance;

public class WaifuCommand {
	private static Message msg;
	public static Lock waifuLock = new ReentrantLock();
	private static final int SECOND = 1000;
	private static final int MINUTE = 60 * SECOND;
	private static final int HOUR = 60 * MINUTE;

	public static void CommandMain(Message msg) throws ExecutionException, InterruptedException, IOException {
		if (msg.getContentRaw().split(" ").length == 1) {
			if (Waifu.timeleft(msg.getMember().getId()) < 0) {
				long t = -Waifu.timeleft(msg.getMember().getId());
				long th = t / HOUR;
				t %= HOUR;
				long tmin = t / MINUTE;
				t %= MINUTE;
				long ts = t / SECOND;
				msg.getChannel().sendMessage("il te reste " + th + "h, " + tmin + "min et " + ts + " secondes avant de chercher une nouvelle Waifu").queue();
			} else {
				Waifu.setTime(msg.getMember().getId());
				SquadMember Sm = Squads.getSquadByMember(msg.getMember()).getStatMember(msg.getMember());
				ArrayList<String> origins = Waifu.getAllOrigins();
				String origin = origins.get(new Random().nextInt(origins.size() - 1));
				Sm.addCollection(origin, msg);
				Squads.save();
			}
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("info")) {
			infowaifu(msg);
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("reset")) {
			Squads.resetWaifu();
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("setimage")) {
			setImage(msg);
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("list")) {
			msg = msg.getChannel().sendMessageEmbeds(new EmbedBuilder().setFooter(msg.getContentRaw().substring("<waifu list ".length()).toLowerCase(Locale.ROOT)).build()).complete();
			listwaifu(msg);
		}
	}

	private static void stats(Message msg) {
		ArrayList<Waifu> waifus = Waifu.getAllWaifu();
		ArrayList<String> str = new ArrayList<>();
		for (Waifu w : waifus) {
			if (!str.contains(w.getOrigin())) {
				str.add(w.getOrigin());
			}
		}
		str.sort(Comparator.naturalOrder());
		StringBuilder sb = new StringBuilder();
		for (String ret : str)
			sb.append(ret).append("   ");
		MessageBuilder mb = new MessageBuilder();
		mb.append(sb);
		Queue<Message> mq =  mb.buildAll();
		for (Message ms : mq)
			msg.getChannel().sendMessage(ms).queue();
	}

	private static void setImage(Message msg) throws IOException {
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
		if (!w.setFile(msg.getAttachments().get(0)))
			msg.getChannel().sendMessage("l'image est trop grande").queue();
		else
			msg.addReaction(Emoji.fromFormatted("\uD83D\uDC4C")).queue();
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
			msg.getChannel().sendMessage("coming soon").queue();
		} else {
			msg.getChannel().sendMessage("t'es une merde").queue();
		}
	}
	private static void sendEmbedInfo(Waifu w, TextChannel tc, Member m) throws InterruptedException {
		EmbedBuilder eb = new EmbedBuilder();
		File f = new File(w.getProfile());
		eb.setAuthor(w.getOrigin());
		eb.setTitle("Information : " + w.getName() + "\nIdentifiant : " + w.getId());
		eb.setImage("attachment://"+f.getName());
		eb.setDescription(w.getDescription());
		for (InvWaifu iw : Squads.getstats(m).getWaifus().values())
		{
			if (iw.getId().equals(w.getId()))
			{
				eb.setColor(Squads.getSquadByMember(m).getSquadRole(m.getGuild()).getColor())
						.setFooter("\naffection " + iw.getFriendlyLevel() + "%");
			}
		}
		waifuLock.lock();
		Thread.sleep(100);
		MessageAction toSend = tc.sendMessageEmbeds(eb.build());
		InvWaifu.downloadWaifu(f, toSend);
	}

	public static void infowaifu(Message msg) throws InterruptedException {
		if (msg.getContentRaw().split(" ").length <= 2) {
			msg.getChannel().sendMessage(">waifu info <nom>").queue();
			return;
		}
		ArrayList<Waifu> w = Waifu.getWaifubyName(msg.getContentRaw().substring(">Waifu info ".length()));
		if (w != null && !msg.getContentRaw().split(" ")[2].equals("0")) {
			for (Waifu waif : w) {
				sendEmbedInfo(waif, msg.getTextChannel(), msg.getMember());
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
				sendEmbedInfo(wid, msg.getTextChannel(), msg.getMember());
			else {
				msg.getChannel().sendMessage("je ne connais pas de Waifu à ce nom ou cet id").queue();
			}
		}
	}


	public static void haremEmbed(Message msg){
		haremEmbed(msg, 0);
	}
	public static void haremEmbed(Message msg, Integer f) {
		ArrayList<Waifu> waifus = new ArrayList<>();
		Waifu w;
		EmbedBuilder eb = new EmbedBuilder();
		String MemberID = msg.getEmbeds().get(0).getAuthor().getName();
		ArrayList<InvWaifu> invWaifus = new ArrayList<>(Squads.getSquadByMember(MemberID).getStatMember(MemberID).getWaifus().values());
		for (InvWaifu inw : invWaifus)
			waifus.add(Waifu.getWaifuById(inw.getId()));
		int	i = f*10;
		if (waifus.isEmpty())
		{
			msg.editMessageEmbeds(eb.setDescription("tu as pas de waifu").setColor(Color.RED).build()).queue();
			return;
		}
		if (i > waifus.size() || i < 0)
			return;
		eb.setFooter(f.toString()).setTitle(msg.getEmbeds().get(0).getTitle());
		StringBuilder sb = new StringBuilder();
		while (i < (f*10)+10)
		{
			if (i < waifus.size()) {
				w = waifus.get(i);
				sb.append(w.getId()).append(" ").append(w.getName()).append(" de ").append(w.getOrigin()).append(" affection : ").append(invWaifus.get(i).getFriendlyLevel()).append("%\n");
			}
			i++;
		}
		eb.setColor(Squads.getSquadByMember(MemberID).getSquadRole(msg.getGuild()).getColor());
		eb.setDescription(sb);
		eb.setAuthor(MemberID);
		msg.editMessageEmbeds(eb.build()).queue();
	}




	public static void listwaifu(Message msg){
		listwaifu(msg, 0);
	}
	public static void listwaifu(Message tc, Integer f) {
		ArrayList<Waifu> waifus = Waifu.getAllWaifu();
		Waifu w;
		EmbedBuilder eb = new EmbedBuilder();
		waifus.removeIf(waifu -> !waifu.getOrigin().startsWith(tc.getEmbeds().get(0).getFooter().getText()));
		int	i = f*10;
		if (waifus.isEmpty())
		{
			tc.editMessageEmbeds(eb.setDescription("aucune waifu sous cette origine").build()).queue();
			return;
		}
		if (i > waifus.size() || i < 0)
			return;
		eb.setFooter(tc.getEmbeds().get(0).getFooter().getText());
		StringBuilder sb = new StringBuilder();
		while (i < (f*10)+10)
		{
			if (i < waifus.size()) {
				w = waifus.get(i);
				sb.append(w.getId()).append(" ").append(w.getName()).append(" de ").append(w.getOrigin()).append("\n");
			}
			i++;
		}
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
		msg.addReaction(Emoji.fromFormatted("\uD83D\uDC4C")).queue();
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
		msg.addReaction(Emoji.fromFormatted("\uD83D\uDC4C")).queue();
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
		msg.addReaction(Emoji.fromFormatted("\uD83D\uDC4C")).queue();
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
		msg.addReaction(Emoji.fromFormatted("\uD83D\uDC4C")).queue();
	}

	private static void release(Message msg) {
		if (!msg.getChannel().getId().equals(BotDiscord.FarmingSalonId)) {
			msg.getChannel().sendMessage("Mové salon comme dirait l'autre").queue();
			return;
		}
		return;
	}
}


