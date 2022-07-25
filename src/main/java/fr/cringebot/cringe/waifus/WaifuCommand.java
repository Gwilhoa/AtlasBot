package fr.cringebot.cringe.waifus;

import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.escouades.SquadMember;
import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.objects.StringExtenders;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WaifuCommand {
	private static Message msg;
	public static Lock waifuLock = new ReentrantLock();
	private static final int SECOND = 1000;
	private static final int MINUTE = 60 * SECOND;
	private static final int HOUR = 60 * MINUTE;

	public static void CommandMain(Message msg) throws ExecutionException, InterruptedException, IOException {
		if (msg.getContentRaw().split(" ").length == 1) {
			if (Waifu.timeleft(msg.getMember().getId()) > 0) {
				long t = Waifu.timeleft(msg.getMember().getId());
				long th = t / HOUR;
				t %= HOUR;
				long tmin = t / MINUTE;
				t %= MINUTE;
				long ts = t / SECOND;
				msg.getChannel().sendMessage("il te reste " + th + "h, " + tmin + "min et " + ts + " secondes avant de chercher une nouvelle Waifu").queue();
			} else {
				Waifu.setTime(msg.getMember().getId());
				SquadMember Sm = Squads.getSquadByMember(msg.getMember()).getStatMember(msg.getMember());
				Sm.getWaifu(null, msg);
				Squads.save();
			}
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("info")) {
			infowaifu(msg);
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("reset")) {
			Squads.resetWaifu();
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("setimage")) {
			setImage(msg);
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("list")) {
			EmbedBuilder eb = new EmbedBuilder();
			if (msg.getContentRaw().length() > "<waifu list ".length()){
				eb.setTitle("Listes des waifus : " + msg.getContentRaw().substring("<waifu list ".length()).toLowerCase(Locale.ROOT));
			} else {
				eb.setTitle("Listes des waifus : all");
			}
			msg = msg.getChannel().sendMessageEmbeds(
					eb.setFooter("0 " + msg.getMember().getId()).build()
			).complete();
					msg.addReaction(Emoji.fromFormatted("◀️")).and(msg.addReaction(Emoji.fromFormatted("▶️"))).queue();
			listwaifu(msg);
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("add")) {
			addwaifu(msg);
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("trade")){
			tradewaifu(msg);
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("removeat")) {
			if (msg.getMentions().getMembers().get(0) != null && msg.getContentRaw().split(" ").length == 4 && msg.getMember().getPermissions().contains(Permission.ADMINISTRATOR))
			{
				if (Squads.getstats(msg.getMentions().getMembers().get(0)).removeWaifu(Integer.parseInt(msg.getContentRaw().split(" ")[3])))
				{
					msg.getChannel().sendMessage("waifu " + Waifu.getWaifuById(Integer.parseInt(msg.getContentRaw().split(" ")[3])).getName() + " supprimé à " + msg.getMentions().getMembers().get(0).getAsMention()).queue();
				} else
				{
					msg.getChannel().sendMessage("en attente du message, mais pour x ou y raison ça a pas marché").queue();
				}
			} else {
				msg.getChannel().sendMessage("tu as pas les droits").queue();
			}
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("delete")) {
			Waifu.getWaifuById(Integer.parseInt(msg.getContentRaw().split(" ")[2])).delwaifu();
		}
	}
	private static void tradewaifu(Message msg) {
		EmbedBuilder eb = tradewaifu(msg.getMember(),Integer.parseInt(msg.getContentRaw().split(" ")[2]), Integer.parseInt(msg.getContentRaw().split(" ")[3]), msg.getMentions().getMembers().get(0));
		if (eb != null) {
			ArrayList<ButtonImpl> bttn = new ArrayList<>();
			bttn.add(new ButtonImpl("trade_ok;"+Integer.parseInt(msg.getContentRaw().split(" ")[2]) +";"+Integer.parseInt(msg.getContentRaw().split(" ")[3]) +";"+ msg.getMember().getId() + ";" + msg.getMentions().getMembers().get(0).getId(), "accepter", ButtonStyle.SUCCESS, false, null));
			bttn.add(new ButtonImpl("trade_no;"+msg.getMentions().getMembers().get(0).getId(), "refuser", ButtonStyle.DANGER, false, null));
			msg.getChannel().sendMessage(msg.getMember().getAsMention() + " veux faire un échange avec " +  msg.getMentions().getMembers().get(0).getAsMention()).setEmbeds(eb.build()).setActionRow(bttn).queue();
		}
		else {
			msg.getChannel().sendMessage("toi ou la personne demandé n'a pas les waifus demandé ou un des deux a deja la waifu").queue();
		}
	}

	private static EmbedBuilder tradewaifu(Member sender, Integer IdWaifu01, Integer IdWaifu02, Member received) {
		InvWaifu ivWaifu01 = Squads.getstats(sender).getWaifus().get(IdWaifu01);
		InvWaifu ivWaifu02 = Squads.getstats(received).getWaifus().get(IdWaifu02);

		if ( ivWaifu01 != null && ivWaifu02 != null && Squads.getstats(sender).getWaifus().get(IdWaifu02) == null && Squads.getstats(received).getWaifus().get(IdWaifu01) == null)
		{
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Requête d'échange").setDescription(ivWaifu01.getWaifu().getName() + " provenant de " + ivWaifu01.getWaifu().getOrigin() + " de niveau " + ivWaifu01.getLevel()
			+ "\ncontre\n" + ivWaifu02.getWaifu().getName()+ " provenant de " + ivWaifu02.getWaifu().getOrigin()  + " de niveau " + ivWaifu02.getLevel());
			return eb;
			}
		else {
			return null;

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
		String name;
		String[] args = msg.getContentRaw().split("\n");
		if (args[0].split(" ").length <= 2) {
			msg.getChannel().sendMessage("SOMBRE MERDE").queue();
			return;
		}
		if (args[0].length() < ">waifu add ".length())
		{
			msg.getChannel().sendMessage("nom non définis : \n>waifu add NOM\ndescription").queue();
			return;
		}
		name = args[0].substring(">waifu add ".length());
		if (!msg.getAttachments().isEmpty() && msg.getAttachments().size() == 1) {
			new Waifu(msg.getAttachments().get(0), name, msg.getContentRaw().substring(args[0].length() + args[1].length() + 2), null, args[1], false);
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
						.setFooter("niveau : " + iw.getLevel()
								+ "\naffection " + iw.getFriendlyLevel() + "%");
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
				sb.append(w.getId()).append(" ").append(w.getName()).append(" de ").append(w.getOrigin()).append("\n    niveau : ").append(invWaifus.get(i).getLevel()).append("\n");
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
		String id = tc.getEmbeds().get(0).getFooter().getText().split(" ")[1];
		Waifu w;
		int	i = f*10;
		EmbedBuilder eb = new EmbedBuilder();
		if (!tc.getEmbeds().get(0).getTitle().substring("Listes des waifus : ".length()).equals("all"))
			waifus.removeIf(waifu -> !StringExtenders.startWithIgnoreCase(waifu.getOrigin(), tc.getEmbeds().get(0).getTitle().substring("Listes des waifus : ".length())));
		if (waifus.isEmpty())
		{
			tc.editMessageEmbeds(eb.setDescription("aucune waifu sous cette origine").build()).queue();
			return;
		}
		if (i > waifus.size() || i < 0)
			return;
		eb.setTitle(tc.getEmbeds().get(0).getTitle());
		eb.setFooter(f + " " + id);
		StringBuilder sb = new StringBuilder();
		while (i < (f*10)+10)
		{
			if (i < waifus.size()) {
				w = waifus.get(i);
				if (Squads.getstats(id).getWaifus().get(w.getId()) != null)
					sb.append("__").append(w.getId()).append(" ").append(w.getName()).append(" de ").append(w.getOrigin()).append("__\n");
				else
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


