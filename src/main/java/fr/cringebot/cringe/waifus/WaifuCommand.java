package fr.cringebot.cringe.waifus;

import fr.cringebot.cringe.objects.SelectOptionImpl;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import static fr.cringebot.cringe.waifus.waifu.getAllWaifu;

public class WaifuCommand {
	public static void addwaifu(Message msg) throws ExecutionException, InterruptedException {
		String[] args = msg.getContentRaw().split("\n");
		if (!msg.getAttachments().isEmpty() && msg.getAttachments().size() == 1)
		{
			ArrayList<SelectOption> options = new ArrayList<>();
			for (waifu.Type tpe: waifu.Type.values())
				options.add(new SelectOptionImpl("Catégorie : "+tpe.name(), tpe.name()));
			options.add(new SelectOptionImpl("Annuler", "stop"));
			SelectMenuImpl selectionMenu = new SelectMenuImpl( "waifu", "selectionnez un choix", 1, 1, false, options);
			msg.getChannel().sendMessageEmbeds(new EmbedBuilder().setTitle(args[0].substring(">addwaifu ".length())).setFooter(args[1]).setDescription(msg.getContentRaw().substring(args[0].length() + args[1].length()+ 1)).build()).addFile(msg.getAttachments().get(0).downloadToFile().get()).setActionRow(selectionMenu).queue();
		}
		else
		{
			msg.getChannel().sendMessage("t'es une merde").queue();
		}
	}
	public static void infowaifu(Message msg)
	{
		ArrayList<waifu> w = waifu.getWaifubyName(msg.getContentRaw().substring(">infowaifu ".length()));
		if (w != null) {
			MessageAction ma = msg.getChannel().sendMessageEmbeds(w.get(0).EmbedWaifu(msg.getGuild()).build()).addFile(w.get(0).getProfile());
			int	i = 1;
			while (i < w.size())
			{
				ma = ma.setEmbeds(w.get(i).EmbedWaifu(msg.getGuild()).build()).addFile(w.get(i).getProfile());
				i++;
			}
			ma.queue();
		}
		else
			msg.getChannel().sendMessage("je ne connais aucune waifu a ce nom").queue();
	}

	public static void listwaifu(Message msg) {
		ArrayList<waifu> waifus = waifu.getAllWaifu();
		StringBuilder sb = new StringBuilder().append("listes des waifus\n\n");
		for (waifu w : waifus)
			sb.append(w.getId()).append(" ").append(w.getName()).append(" de ").append(w.getOrigin()).append("\n");
		MessageBuilder mb = new MessageBuilder().append(sb);
		Queue<Message> ml = mb.buildAll();
		while (!ml.isEmpty())
			msg.getChannel().sendMessage(ml.poll()).queue();
	}
}
