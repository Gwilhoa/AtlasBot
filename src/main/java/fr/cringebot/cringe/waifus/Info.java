package fr.cringebot.cringe.waifus;

import fr.cringebot.cringe.escouades.Squads;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.awt.*;
import java.util.ArrayList;

public class Info {
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
								+ "\naffection " + iw.getFriendlyLevel() + "/" + (iw.getLevel() + 1) * 2000);
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

}
