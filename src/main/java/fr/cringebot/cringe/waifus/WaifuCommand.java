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
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
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
			infowaifu(msg);
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
			msg.getChannel().sendMessageEmbeds(listwaifu(msg.getGuild(), msg.getMember().getId(), SearchKey).build()).setActionRows(generateButtonList(msg.getMember().getId(), SearchKey, 0)).queue();
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
			Integer pts = 0;
			ArrayList<InvWaifu> harem = new ArrayList<>(Squads.getstats(member).getWaifus().values());
			StringBuilder sb = new StringBuilder();
			boolean f = false;
			for (InvWaifu w : harem) {
				if (w.getLevel() > 0 && w.getLevel() <= 20) {
					pts += 20;
					f = true;
					sb.append(w.getWaifu().getName()).append(" ").append(getSearching(member, 1, w.getWaifu(), tc)).append('\n');
				}
			}
			if (!f)
				sb.append("tu as pas de waifu prete a chercher");
			else
				Squads.getstats(member).setSearchingtimer();
			Squads.getstats(member).addPoint(pts.longValue());
			return new EmbedBuilder().setDescription(sb).setTitle("résultat de la recherche").setFooter("tu as gagné "+ pts + "points d'escouade").setColor(Color.pink);
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
				if (Squads.getstats(member).isCompleteCollection(w.getOrigin()))
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
				Squads.getstats(member).addItem(Item.Items.UF.getStr());
				if (Squads.getstats(member).getAmountItem(Item.Items.UF.getStr()) >= 5) {
					Squads.getstats(member).addItem(Item.Items.BF.getStr());
					Squads.getstats(member).removeItem(Item.Items.UF.getStr(), 5);
					return "a trouvé une fleur, vous avez un nouveau bouquet";
				}
				return "a trouvé une fleur";
			}
		}
		return "coming soon";
	}

	private static void tradewaifu(Message msg) {
		EmbedBuilder eb = tradewaifu(msg.getMember(), Integer.parseInt(msg.getContentRaw().split(" ")[2]), Integer.parseInt(msg.getContentRaw().split(" ")[3]), msg.getMentions().getMembers().get(0));
		if (Objects.equals(eb.build().getColor(), Color.WHITE)) {
			ArrayList<ButtonImpl> bttn = new ArrayList<>();
			bttn.add(new ButtonImpl("trade_ok;" + Integer.parseInt(msg.getContentRaw().split(" ")[2]) + ";" + Integer.parseInt(msg.getContentRaw().split(" ")[3]) + ";" + msg.getMember().getId() + ";" + msg.getMentions().getMembers().get(0).getId(), "accepter", ButtonStyle.SUCCESS, false, null));
			bttn.add(new ButtonImpl("trade_no;" + msg.getMentions().getMembers().get(0).getId(), "refuser", ButtonStyle.DANGER, false, null));
			msg.getChannel().sendMessage(msg.getMember().getAsMention() + " veux faire un échange avec " + msg.getMentions().getMembers().get(0).getAsMention()).setEmbeds(eb.build()).setActionRow(bttn).queue();
		} else {
			msg.getChannel().sendMessageEmbeds(eb.build()).queue();
		}
	}

	private static EmbedBuilder tradewaifu(Member sender, Integer IdWaifu01, Integer IdWaifu02, Member received) {
		InvWaifu ivWaifu01 = Squads.getstats(sender).getWaifus().get(IdWaifu01);
		InvWaifu ivWaifu02 = Squads.getstats(received).getWaifus().get(IdWaifu02);
		EmbedBuilder eb = new EmbedBuilder();
		if (ivWaifu01 != null && ivWaifu02 != null && Squads.getstats(sender).getWaifus().get(IdWaifu02) == null && Squads.getstats(received).getWaifus().get(IdWaifu01) == null)
		{
			eb.setTitle("Requête d'échange").setDescription(ivWaifu01.getWaifu().getName() + " provenant de " + ivWaifu01.getWaifu().getOrigin() + " de niveau " + ivWaifu01.getLevel()
			+ "\ncontre\n" + ivWaifu02.getWaifu().getName()+ " provenant de " + ivWaifu02.getWaifu().getOrigin()  + " de niveau " + ivWaifu02.getLevel());
			eb.setColor(Color.WHITE);
		}
		else
		{
			eb.setTitle("Échec");
			if (ivWaifu01 == null) {
				eb.setDescription("tu n'as pas " + Waifu.getWaifuById(IdWaifu01));
			} else if (ivWaifu02 == null) {
				eb.setDescription(received.getAsMention() + " n'a pas " + Waifu.getWaifuById(IdWaifu02).getName());
			} else if (Squads.getstats(sender).getWaifus().get(IdWaifu02) != null) {
				eb.setDescription("tu as déja "+ Waifu.getWaifuById(IdWaifu02).getName());
			} else if (Squads.getstats(received).getWaifus().get(IdWaifu01) != null) {
				eb.setDescription(received.getAsMention() + " a déjà " + Waifu.getWaifuById(IdWaifu01).getName());
			}
			eb.setColor(Color.red);
		}
		return eb;
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

	public static EmbedBuilder EmbedInfo(Waifu w, Member m) throws InterruptedException {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(w.getOrigin());
		eb.setTitle("Information : " + w.getName() + "\nIdentifiant : " + w.getId());
		eb.setImage(w.getProfile());
		eb.setDescription(w.getDescription());
		eb.setColor(Color.black);
		for (InvWaifu iw : Squads.getstats(m).getWaifus().values()) {
			if (iw.getId().equals(w.getId())) {
				eb.setColor(Squads.getSquadByMember(m).getSquadRole(m.getGuild()).getColor())
						.setFooter("niveau : " + iw.getLevel()
								+ "\naffection " + iw.getFriendlyLevel() + "%");
			}
		}
		return eb;
	}

	public static void infowaifu(Message msg) throws InterruptedException {
		if (msg.getContentRaw().split(" ").length <= 2) {
			msg.getChannel().sendMessage(">waifu info <nom>").queue();
			return;
		}
		ArrayList<Waifu> w = Waifu.getWaifubyName(msg.getContentRaw().substring(">Waifu info ".length()));
		if (w != null && !msg.getContentRaw().split(" ")[2].equals("0")) {
			for (Waifu waif : w) {
				MessageEmbed me = EmbedInfo(waif, msg.getMember()).build();
				MessageAction ma = msg.getChannel().sendMessageEmbeds(me);
				if (!me.getColor().equals(Color.black))
					ma = ma.setActionRow(AffectionMenu.getMenu(msg.getMember(), waif));
				ma.queue();
			}
		}
		else {
			Waifu wid;
			try {
				wid = Waifu.getWaifuById(Integer.parseInt(msg.getContentRaw().split(" ")[2]));
			} catch (NumberFormatException e) {
				msg.getChannel().sendMessage("je ne connais pas de Waifu à ce nom ou cet id").queue();
				return;
			}
			if (wid != null) {
				MessageEmbed me = EmbedInfo(wid, msg.getMember()).build();
				MessageAction ma = msg.getChannel().sendMessageEmbeds(me);
				if (!me.getColor().equals(Color.black))
					ma = ma.setActionRow(AffectionMenu.getMenu(msg.getMember(), wid));
				ma.queue();
			} else {
				msg.getChannel().sendMessage("je ne connais pas de Waifu à ce nom ou cet id").queue();
			}
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




	public static EmbedBuilder listwaifu(Guild g, String MemberId, String key){
		return listwaifu(g, MemberId, key, 0);
	}
	public static EmbedBuilder listwaifu(Guild g, String MemberId, String key, Integer f) {
		ArrayList<Waifu> waifus = Waifu.getAllWaifu();
		Waifu w;
		int	i = f*10;
		EmbedBuilder eb = new EmbedBuilder();
		if (!key.equals("all"))
			waifus.removeIf(waifu -> !StringExtenders.startWithIgnoreCase(waifu.getOrigin(), key));
		if (waifus.isEmpty())
		{
			return new EmbedBuilder().setColor(Color.RED).setTitle("Listes des waifus en "+ key).setDescription("Aucune waifu à une origine similaire");
		}
		eb.setTitle("Listes des waifus en "+ key);
		StringBuilder sb = new StringBuilder();
		while (i < (f*10)+10)
		{
			if (i < waifus.size()) {
				w = waifus.get(i);
				if (Squads.getstats(MemberId).getWaifus().get(w.getId()) != null)
					sb.append("__").append(w.getId()).append(" ").append(w.getName()).append(" de ").append(w.getOrigin()).append("__\n");
				else
					sb.append(w.getId()).append(" ").append(w.getName()).append(" de ").append(w.getOrigin()).append("\n");
			}
			i++;
		}
		eb.setDescription(sb);
		eb.setColor(Squads.getSquadByMember(MemberId).getSquadRole(g).getColor());
		eb.setFooter(f.toString());
		return eb;
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


