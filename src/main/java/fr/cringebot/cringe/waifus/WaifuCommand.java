package fr.cringebot.cringe.waifus;

import fr.cringebot.cringe.objects.SelectOptionImpl;
import fr.cringebot.cringe.objects.StringExtenders;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static fr.cringebot.cringe.waifus.waifu.getAllWaifu;

public class WaifuCommand {
	private static Message msg;

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
				eb.setAuthor(msg.getContentRaw().substring(">waifu list ".length()));
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
		waifu w1 = waifu.getWaifuById(Integer.parseInt(id));
		waifu w2 = waifu.getWaifuById(Integer.parseInt(id2));
		if (w1.getOwner() == null || !w1.getOwner().equals(msg.getMember().getId()))
			msg.getChannel().sendMessage("tu n'es pas le propriétaire de "+ w1.getName()).queue();
		else if (w2.getOwner() == null)
			msg.getChannel().sendMessage("cette waifu appartient à personne").queue();
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
		waifu w = waifu.getWaifuById(Integer.parseInt(id));
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
		ArrayList<waifu> waifus = waifu.getAllWaifu();
		for (waifu w : waifus)
			w.setOwner(null);
	}

	private static void newWaifu(Message msg) {
		if (waifu.timeleft(msg.getMember().getId()) < 0){
			long t = waifu.timeleft(msg.getMember().getId());
			long th = (10800000 - t) / 3600000;
			long tmin = (10800000 - th * 3600000 - t) / 60000;
			long ts = (10800000 - th * 3600000 - tmin * 60000 - t) / 1000;
			msg.getChannel().sendMessage("il te reste " + th + "h, " + tmin + "min et " + ts + " secondes avant de chercher une nouvelle waifu").queue();
			return;

		}

		waifu.setTime(msg.getMember().getId());
		ArrayList<waifu> waifus = getAllWaifu();
		waifus.removeIf(w -> w.getOwner() == null || !w.getOwner().equals(msg.getMember().getId()));
		waifu w;
		w = waifu.getAvailableWaifu().get(new Random().nextInt(waifu.getAvailableWaifu().size() - 1));
		w.setOwner(msg.getMember().getId());
		EmbedBuilder eb = new EmbedBuilder();
		eb.setImage("Attachment://"+w.getProfile().getName());
		eb.setTitle("Nouvelle waifu !");
		eb.setDescription("ta nouvelle waifu est " + w.getName() + " de " + w.getOrigin());
		eb.setFooter("félicitation !!");
		msg.getChannel().sendMessageEmbeds(eb.build()).addFile(w.getProfile()).queue();
	}

	public static void addwaifu(Message msg) throws ExecutionException, InterruptedException {
		if (!msg.getChannel().getId().equals("975087822618910800")) {
			msg.getChannel().sendMessage("non").queue();
			return;
		}
		String[] args = msg.getContentRaw().split("\n");
		if (!msg.getAttachments().isEmpty() && msg.getAttachments().size() == 1)
		{
			ArrayList<SelectOption> options = new ArrayList<>();
			for (waifu.Type tpe: waifu.Type.values())
				options.add(new SelectOptionImpl("Catégorie : "+tpe.name(), tpe.name()));
			options.add(new SelectOptionImpl("Annuler", "stop"));
			SelectMenuImpl selectionMenu = new SelectMenuImpl( "waifu", "selectionnez un choix", 1, 1, false, options);
			msg.getChannel().sendMessageEmbeds(new EmbedBuilder().setTitle(args[0].substring(">waifu add".length())).setFooter(args[1]).setDescription(msg.getContentRaw().substring(args[0].length() + args[1].length()+ 1)).build()).addFile(msg.getAttachments().get(0).downloadToFile().get()).setActionRow(selectionMenu).queue();
		}
		else
		{
			msg.getChannel().sendMessage("t'es une merde").queue();
		}
	}
	private static void sendEmbedInfo(waifu w, TextChannel tc) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(w.getOrigin());
		eb.setTitle("Information : " + w.getName());
		eb.setImage("Attachment://"+w.getProfile().getName());
		if (w.getOwner() != null)
			eb.setFooter("appartient à "+ tc.getGuild().getMemberById(w.getOwner()).getEffectiveName(), tc.getGuild().getMemberById(w.getOwner()).getAvatarUrl());
		else
			eb.setFooter("disponible");
		eb.setDescription(w.getDescription());
		tc.sendMessageEmbeds(eb.build()).addFile(w.getProfile()).queue();
	}

	public static void infowaifu(Message msg)
	{
		ArrayList<waifu> w = waifu.getWaifubyName(msg.getContentRaw().substring(">waifu info ".length()));
		if (w != null) {
			sendEmbedInfo(w.get(0), msg.getTextChannel());
			int	i = 1;
			while (i < w.size())
			{
				sendEmbedInfo(w.get(i), msg.getTextChannel());
				i++;
			}
		}
		else
		{
			waifu wid;
			try {
				wid = waifu.getWaifuById(Integer.parseInt(msg.getContentRaw().split(" ")[2]));
			}
			catch (NumberFormatException e){
				msg.getChannel().sendMessage("je ne connais pas de waifu à ce nom ou cet id").queue();
				return;
			}
			if (wid != null)
				sendEmbedInfo(wid, msg.getTextChannel());
			else {
				msg.getChannel().sendMessage("je ne connais pas de waifu à ce nom ou cet id").queue();
			}
		}
	}

	public static void listwaifu(Message msg){
		listwaifu(msg, 0);
	}
	public static void listwaifu(Message tc, Integer f) {
		ArrayList<waifu> waifus = waifu.getAllWaifu();
		waifu w;
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
		while (i < (f*10)+10)
		{
			if (i < waifus.size()) {
				w = waifus.get(i);
				if (w.getOwner() == null)
					sb.append(w.getId()).append(" ").append(w.getName()).append(" de ").append(w.getOrigin()).append("\n");
				else
					sb.append(w.getId()).append(" ").append(w.getName()).append(" de ").append(w.getOrigin()).append(" __").append(tc.getGuild().getMemberById(w.getOwner()).getEffectiveName()).append("__\n");
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
		waifu w = waifu.getWaifuById(Integer.parseInt(id));
		if (w == null) {
			msg.getChannel().sendMessage("id non défini").queue();
			return;
		}
		String name = msg.getContentRaw().substring(">waifu setdescription  ".length() + id.length());
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
		waifu w = waifu.getWaifuById(Integer.parseInt(id));
		if (w == null) {
			msg.getChannel().sendMessage("id non défini").queue();
			return;
		}
		String name = msg.getContentRaw().substring(">waifu setOrigin  ".length() + id.length());
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
		waifu w = waifu.getWaifuById(Integer.parseInt(id.substring(">waifu delete ".length())));
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
		waifu w = waifu.getWaifuById(Integer.parseInt(id));
		if (w == null) {
			msg.getChannel().sendMessage("id non défini").queue();
			return;
		}
		String name = msg.getContentRaw().substring(">waifu setname  ".length() + id.length());
		w.setName(name);
		msg.addReaction("\uD83D\uDC4C").queue();
	}

	private static void release(Message msg) {
		if (true)
		{
			msg.getChannel().sendMessage("tu ne peux pas relacher de waifu pour le moment").queue();
			return;
		}
		String id = msg.getContentRaw().split(" ")[2];
		waifu w = waifu.getWaifuById(Integer.parseInt(id));
		if (w.getOwner() == null)
			msg.getChannel().sendMessage(w.getName() + " n'appartient a personne").queue();
		else if (w.getOwner().equals(msg.getMember().getId()))
			msg.getChannel().sendMessage("tu n'est pas le propriétaire de " + w.getName()).queue();
		else {
			w.setOwner(null);
			msg.getChannel().sendMessage(w.getName() + " a été relâcher").queue();
		}
	}
}


