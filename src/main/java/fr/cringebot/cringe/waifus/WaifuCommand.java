package fr.cringebot.cringe.waifus;

import fr.cringebot.cringe.objects.SelectOptionImpl;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import static fr.cringebot.cringe.waifus.waifu.getAllWaifu;

public class WaifuCommand {
	public static void CommandMain(Message msg) throws ExecutionException, InterruptedException {
		if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("add"))
			addwaifu(msg);
		else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("list"))
			listwaifu(msg.getTextChannel());
		else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("info"))
			infowaifu(msg);
		else if (msg.getContentRaw().split(" ")[1].equalsIgnoreCase("setdescription"))
			setDescription(msg);

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
	public static void infowaifu(Message msg)
	{
		ArrayList<waifu> w = waifu.getWaifubyName(msg.getContentRaw().substring(">waifu info".length()));
		if (w != null) {
			msg.getChannel().sendMessageEmbeds(w.get(0).EmbedWaifu(msg.getGuild()).build()).addFile(w.get(0).getProfile()).queue();
			int	i = 1;
			while (i < w.size())
			{
				msg.getChannel().sendMessageEmbeds(w.get(i).EmbedWaifu(msg.getGuild()).build()).addFile(w.get(0).getProfile()).queue();
				i++;
			}
		}
		else
			msg.getChannel().sendMessage("je ne connais aucune waifu a ce nom").queue();
	}

	public static void listwaifu(TextChannel tc) {
		ArrayList<waifu> waifus = waifu.getAllWaifu();
		StringBuilder sb = new StringBuilder().append("listes des waifus\n\n");
		for (waifu w : waifus)
			sb.append(w.getId()).append(" ").append(w.getName()).append(" de ").append(w.getOrigin()).append("\n");
		MessageBuilder mb = new MessageBuilder().append(sb);
		Queue<Message> ml = mb.buildAll();
		while (!ml.isEmpty())
			tc.sendMessage(ml.poll()).queue();
	}

	public static void setDescription(Message msg)
	{
		if (!msg.getChannel().getId().equals("975087822618910800")) {
			msg.getChannel().sendMessage("non").queue();
			return;
		}
		String id = msg.getContentRaw().split("\n")[0];
		waifu w = waifu.getWaifuById(Integer.parseInt(id.substring(">waifu setdescription ".length())));
		w.setDescription(msg.getContentRaw().substring(id.length()));
	}

	public static void delwaifu(Message msg)
	{
		if (!msg.getChannel().getId().equals("975087822618910800")) {
			msg.getChannel().sendMessage("non").queue();
			return;
		}
		String id = msg.getContentRaw().split("\n")[0];
		waifu w = waifu.getWaifuById(Integer.parseInt(id.substring(">waifu delete ".length())));
		w.delwaifu();
	}

	public static void setName(Message msg)
	{
		if (!msg.getChannel().getId().equals("975087822618910800")) {
			msg.getChannel().sendMessage("non").queue();
			return;
		}
		String id = msg.getContentRaw().split("\n")[0];
		waifu w = waifu.getWaifuById(Integer.parseInt(id.substring(">waifu setname ".length())));
		w.setName(msg.getContentRaw().substring(id.length()));
	}
}
