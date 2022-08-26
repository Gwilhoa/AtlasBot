package fr.cringebot.cringe.waifus;

import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.objects.StringExtenders;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import java.awt.*;
import java.util.ArrayList;

public class ListWaifu {
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
}
