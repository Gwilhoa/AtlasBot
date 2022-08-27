package fr.cringebot.cringe.waifus;

import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.escouades.SquadMember;
import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.objects.Item;
import fr.cringebot.cringe.objects.StringExtenders;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WaifuCommand {
	public static Lock waifuLock = new ReentrantLock();
	private static final int SECOND = 1000;
	private static final int MINUTE = 60 * SECOND;
	private static final int HOUR = 60 * MINUTE;

	public static EmbedBuilder capturedWaifu(String id, Guild g) throws InterruptedException {
		if (Squads.getstats(id).timeleft() > 0) {
			long t = Squads.getstats(id).timeleft();
			long th = t / HOUR;
			t %= HOUR;
			long tmin = t / MINUTE;
			t %= MINUTE;
			long ts = t / SECOND;
			return new EmbedBuilder().setTitle("Raté, reviens plus tard").setColor(Color.black).setDescription("il te reste " + th + "h, " + tmin + "min et " + ts + " secondes avant de chercher une nouvelle waifu");
		} else if (BotDiscord.isMaintenance) {
			return new EmbedBuilder().setTitle("Maintenance en cours").setColor(Color.WHITE).setDescription("une maintenance est en cours, la raison doit etre dans annonce");
		} else {
			Squads.getstats(id).setTime();
			SquadMember Sm = Squads.getstats(id);
			EmbedBuilder eb = Sm.getWaifu(null, id, g);
			Squads.save();
			return eb;
		}
	}

	public static void CommandMain(Message msg) throws ExecutionException, InterruptedException, IOException {
		if (msg.getContentRaw().split(" ").length == 1) {
			EmbedBuilder eb = capturedWaifu(msg.getMember().getId(), msg.getGuild());
			if (!Objects.equals(eb.build().getColor(), Color.black) && !Objects.equals(eb.build().getColor(), Color.WHITE))
				msg.getChannel().sendMessageEmbeds(eb.build()).queue();
			else
			{
				if (Squads.getstats(msg.getMember()).getAmountItem(Item.Items.CE.getStr()) > 0)
					msg.getChannel().sendMessageEmbeds(eb.build()).setActionRow(new ButtonImpl("USECE;"+msg.getMember().getId(), "utiliser un Chronomètre érotique", ButtonStyle.SUCCESS,false, null)).queue();
				else
					msg.getChannel().sendMessageEmbeds(eb.build()).setActionRow(new ButtonImpl("USECE;"+msg.getMember().getId(), "utiliser un Chronomètre érotique", ButtonStyle.SUCCESS,true, null)).queue();
			}
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("info")) {
			Info.infowaifu(msg);
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("reset")) {
			Squads.resetWaifu();
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("setimage")) {
			if (msg.getContentRaw().split(" ").length == 2)
				msg.getChannel().sendMessage("argument insuffisant").queue();
			else {
				String text = msg.getContentRaw().substring(">waifu setimage ".length());
				if (msg.getAttachments().isEmpty())
					msg.getChannel().sendMessage("tu n'as pas mis d'image").queue();
				else {
					try {
						if (setImage(Integer.parseInt(text), msg.getAttachments().get(0)))
							msg.reply("nouvelle image pour l'id" + text).queue();
						else
							msg.reply("identifiant incorrect").queue();
					} catch (NumberFormatException e) {
						msg.reply("identifiant incorrect").queue();
					}

				}
			}
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("list")) {
			String SearchKey;
			if (msg.getContentRaw().length() <= ">waifu list ".length())
				SearchKey = "all";
			else
				SearchKey = msg.getContentRaw().substring(">waifu list ".length());
			msg.getChannel().sendMessageEmbeds(ListWaifu.listwaifu(msg.getGuild(), msg.getMember().getId(), SearchKey).build()).setActionRows(generateButtonList(msg.getMember().getId(), SearchKey, 0)).queue();
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("add")) {
			addwaifu(msg);
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("trade")){
			Trade.tradewaifu(msg);
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
			delwaifu(msg);
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("release")) {
			if (release(msg.getMember(), Integer.parseInt(msg.getContentRaw().split(" ")[2]))) {
				Waifu w = Waifu.getWaifuById(Integer.parseInt(msg.getContentRaw().split(" ")[2]));
				EmbedBuilder eb = Squads.getstats(msg.getMember()).addCollection(w.getOrigin(), msg);
				if (eb == null)
					msg.getChannel().sendMessage(w.getName() + " a été relaché, vous gagné une pièce de " + w.getOrigin()).queue();
				else {
					eb.setTitle("une waifu est apparue");
					msg.getChannel().sendMessageEmbeds(eb.build()).queue();
				}
			}
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("search")) {
			msg.getChannel().sendMessageEmbeds(waifuSearching(msg.getMember(), msg.getTextChannel()).build()).queue();
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("setorigin")) {
			if (msg.getContentRaw().split(" ").length <= 3) {
				msg.getChannel().sendMessage("argument insuffisant").queue();
			}
			else {
				String id = msg.getContentRaw().split(" ")[2];
				String text = msg.getContentRaw().substring(">waifu setorigin  ".length() + id.length());
					try {
						if (setOrigin(Integer.parseInt(id), text))
							msg.reply("nouvelle origine pour l'id" + id).queue();
						else
							msg.reply("identifiant incorrect").queue();
					} catch (NumberFormatException e) {
						msg.reply("identifiant incorrect").queue();
					}
			}
		} else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("setdescription")) {
			if (msg.getContentRaw().split(" ").length <= 3) {
				msg.getChannel().sendMessage("argument insuffisant").queue();
			}
			else {
				String id = msg.getContentRaw().split(" ")[2];
				String text = msg.getContentRaw().substring(">waifu setdescription  ".length() + id.length());
				try {
					if (setDescription(Integer.parseInt(id), text))
						msg.reply("nouvelle description pour l'id" + id).queue();
					else
						msg.reply("identifiant incorrect").queue();
				} catch (NumberFormatException e) {
					msg.reply("identifiant incorrect").queue();
				}
			}
		}
	}

	public static Collection<? extends ActionRow> generateButtonList(String id, String searchKey, int i) {
		ArrayList<Waifu> waifus = Waifu.getAllWaifu();
		ArrayList<ActionRow> bttns = new ArrayList<>();
		if (!searchKey.equals("all"))
			waifus.removeIf(waifu -> !StringExtenders.startWithIgnoreCase(waifu.getOrigin(), searchKey));
		if (i >= 10) {
			bttns.add(ActionRow.of(new ButtonImpl("list_"+id+";"+(i-10)+";"+searchKey, "10 page en arrière", ButtonStyle.PRIMARY, false, null)));
		} else {
			bttns.add(ActionRow.of(new ButtonImpl("list_"+id+";"+(i-10)+";"+searchKey, "10 page en arrière", ButtonStyle.PRIMARY, true, null)));
		}
		if (i != 0) {
			bttns.add(ActionRow.of(new ButtonImpl("list_"+id+";"+(i-1)+";"+searchKey, "la page en précédente", ButtonStyle.SECONDARY, false, null)));
		} else {
			bttns.add(ActionRow.of(new ButtonImpl("list_"+id+";"+(i-1)+";"+searchKey, "la page précédente", ButtonStyle.SECONDARY, true, null)));
		}
		if ((i+1)*10 < waifus.size()) {
			bttns.add(ActionRow.of(new ButtonImpl("list_"+id+";"+(i+1)+";"+searchKey, "la page suivante", ButtonStyle.PRIMARY, false, null)));
		} else {
			bttns.add(ActionRow.of(new ButtonImpl("list_"+id+";"+(i+1)+";"+searchKey, "la page suivante", ButtonStyle.PRIMARY, true, null)));
		}
		if ((i+10)*10 < waifus.size()) {
			bttns.add(ActionRow.of(new ButtonImpl("list_"+id+";"+(i+10)+";"+searchKey, "10 page en avant", ButtonStyle.SECONDARY, false, null)));
		}
		else {
			bttns.add(ActionRow.of(new ButtonImpl("list_"+id+";"+(i+10)+";"+searchKey, "10 page en avant", ButtonStyle.SECONDARY, true, null)));
		}
		return bttns;
	}

	public static EmbedBuilder waifuSearching(Member member, TextChannel tc) throws InterruptedException {
		if (Squads.getstats(member).SearchingTimeleft() < 0) {
			Integer pts = 50;
			ArrayList<InvWaifu> harem = new ArrayList<>(Squads.getstats(member).getWaifus().values());
			StringBuilder sb = new StringBuilder();
			boolean f = false;
			for (InvWaifu w : harem) {
				if (w.getLevel() > 0 && w.getLevel() <= 20) {
					f = true;
					sb.append(w.getWaifu().getName()).append(" ").append(getSearching(member, 1, w.getWaifu(), tc)).append('\n');
				}
			}
			if (!f)
				sb.append("tu as pas de waifu prete a chercher");
			else
				Squads.getstats(member).setSearchingtimer();
			Squads.getstats(member).addPoint(pts.longValue());
			return new EmbedBuilder().setDescription(sb).setTitle("Résultat de la recherche...").setFooter("Tu as gagné "+ pts + "points d'escouade !").setColor(Color.pink);
		}
		long t = Squads.getstats(member).SearchingTimeleft();
		long th = t / HOUR;
		t %= HOUR;
		long tmin = t / MINUTE;
		t %= MINUTE;
		long ts = t / SECOND;
		return new EmbedBuilder().setTitle("Raté, reviens plus tard").setColor(Color.black).setDescription("il te reste " + th + "h, " + tmin + "min et " + ts + " secondes avant de partir à la recherche");

	}

	private static String getSearching(Member member, int i, Waifu w, TextChannel tc) throws InterruptedException {
		int r = new Random().nextInt(100) + 1;
		if (i == 1) {
			if (r < 15) {
				if (Squads.getstats(member).isCompleteCollection(w.getOrigin()) || w.getOrigin().equals("B2K"))
					return "a rien trouvé";
				EmbedBuilder eb = Squads.getstats(member).addCollection(w.getOrigin(),member);
				if (eb != null)
					tc.sendMessageEmbeds(eb.build()).queue();
				return "a trouvé un jeton de " +  w.getOrigin();
			}
			if (r < 30) {
				return "a rien trouvé";
			} else if (r < 60) {
				Squads.getstats(member).addCoins(1L);
				return "a trouvé 1 B2C";
			} else {
				Squads.getstats(member).addItem(Item.Items.UFFU.getStr());
				if (Squads.getstats(member).getAmountItem(Item.Items.UFFU.getStr()) >= 5) {
					Squads.getstats(member).addItem(Item.Items.BFFU.getStr());
					Squads.getstats(member).removeItem(Item.Items.UFFU.getStr(), 5);
					return "a trouvé une fleur, vous avez un nouveau bouquet !";
				}
				return "a trouvé une fleur";
			}
		}
		return "coming soon";
	}

	private static boolean setImage(Integer id, Message.Attachment f) throws  IOException {
		Waifu w = Waifu.getWaifuById(id);
		if (w == null)
			return false;
		w.setFile(f);
		return true;
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
			Waifu waifu = new Waifu(msg.getAttachments().get(0), name, msg.getContentRaw().substring(args[0].length() + args[1].length() + 2), null, args[1], false);
			Squads.getstats(msg.getMember()).addPoint(1000L);
			msg.getChannel().sendMessage("waifu bien ajouté #" + waifu.getId()).reference(msg).queue();
		} else {
			msg.getChannel().sendMessage("t'es une merde").queue();
		}
	}




	public static void haremEmbed(Message msg, Integer f, String id) {
		ArrayList<ButtonImpl> bttn = new ArrayList<>();
		ArrayList<Waifu> waifus = new ArrayList<>();
		Waifu w;
		EmbedBuilder eb = new EmbedBuilder();
		ArrayList<InvWaifu> invWaifus = new ArrayList<>(Squads.getstats(id).getWaifus().values());
		for (InvWaifu inw : invWaifus)
			waifus.add(Waifu.getWaifuById(inw.getId()));
		int	i = f*10;
		if (waifus.isEmpty())
		{
			bttn.add(new ButtonImpl("harem_"+id+";"+(f-1), "page précédente",ButtonStyle.PRIMARY ,true, null));
			bttn.add(new ButtonImpl("harem_"+id+";"+(f+1), "page suivante",ButtonStyle.SECONDARY ,true, null));
			msg.editMessageEmbeds(eb.setDescription("tu as pas de waifu").setColor(Color.RED).build()).setActionRow(bttn).queue();
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
		eb.setColor(Squads.getSquadByMember(id).getSquadRole(msg.getGuild()).getColor());
		eb.setDescription(sb);
		if (f == 0)
			bttn.add(new ButtonImpl("harem_"+id+";"+(f-1), "page précédente",ButtonStyle.PRIMARY ,true, null));
		else
			bttn.add(new ButtonImpl("harem_"+id+";"+(f-1), "page précédente",ButtonStyle.PRIMARY ,false, null));
		if (i >= waifus.size())
			bttn.add(new ButtonImpl("harem_"+id+";"+(f+1), "page suivante",ButtonStyle.SECONDARY ,true, null));
		else
			bttn.add(new ButtonImpl("harem_"+id+";"+(f+1), "page suivante",ButtonStyle.SECONDARY ,false, null));
		msg.editMessageEmbeds(eb.build()).setActionRow(bttn).queue();
	}

	public static void haremEmbed(ButtonInteractionEvent event, Integer f, String id) {
		ArrayList<ButtonImpl> bttn = new ArrayList<>();
		ArrayList<Waifu> waifus = new ArrayList<>();
		Waifu w;
		EmbedBuilder eb = new EmbedBuilder();
		ArrayList<InvWaifu> invWaifus = new ArrayList<>(Squads.getstats(id).getWaifus().values());
		for (InvWaifu inw : invWaifus)
			waifus.add(Waifu.getWaifuById(inw.getId()));
		int	i = f*10;
		if (waifus.isEmpty())
		{
			bttn.add(new ButtonImpl("harem_"+id+";"+(f-1), "page précédente",ButtonStyle.PRIMARY ,true, null));
			bttn.add(new ButtonImpl("harem_"+id+";"+(f+1), "page suivante",ButtonStyle.SECONDARY ,true, null));
			event.editMessageEmbeds(eb.setDescription("tu as pas de waifu").setColor(Color.RED).build()).setActionRow(bttn).queue();
			return;
		}
		if (i > waifus.size() || i < 0)
			return;
		eb.setFooter(f.toString()).setTitle(event.getMessage().getEmbeds().get(0).getTitle());
		StringBuilder sb = new StringBuilder();
		while (i < (f*10)+10)
		{
			if (i < waifus.size()) {
				w = waifus.get(i);
				sb.append(w.getId()).append(" ").append(w.getName()).append(" de ").append(w.getOrigin()).append("\n    niveau : ").append(invWaifus.get(i).getLevel()).append("\n");
			}
			i++;
		}
		eb.setColor(Squads.getSquadByMember(id).getSquadRole(event.getGuild()).getColor());
		eb.setDescription(sb);
		if (f == 0)
			bttn.add(new ButtonImpl("harem_"+id+";"+(f-1), "page précédente",ButtonStyle.PRIMARY ,true, null));
		else
			bttn.add(new ButtonImpl("harem_"+id+";"+(f-1), "page précédente",ButtonStyle.PRIMARY ,false, null));
		if (i >= waifus.size())
			bttn.add(new ButtonImpl("harem_"+id+";"+(f+1), "page suivante",ButtonStyle.SECONDARY ,true, null));
		else
			bttn.add(new ButtonImpl("harem_"+id+";"+(f+1), "page suivante",ButtonStyle.SECONDARY ,false, null));
		event.editMessageEmbeds(eb.build()).setActionRow(bttn).queue();
	}




	public static boolean setDescription(Integer id, String desc)
	{
		Waifu w = Waifu.getWaifuById(id);
		if (w == null)
			return false;
		w.setDescription(desc);
		return true;
	}

	public static boolean setOrigin(Integer id, String origin)
	{
		Waifu w = Waifu.getWaifuById(id);
		if (w == null)
			return false;
		w.setOrigin(origin);
		return true;
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

	private static boolean release(Member m, Integer id) {
		return Squads.getstats(m).removeWaifu(id);
	}
}


