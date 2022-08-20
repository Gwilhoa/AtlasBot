package fr.cringebot.cringe.CommandBuilder;

import fr.cringebot.cringe.escouades.Squads;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.util.HashMap;

public class Inventory {
    public static EmbedBuilder getInventory(Member member)
    {
        int i = 0;
        HashMap<String, Integer> inv = Squads.getstats(member).getInventory();
        EmbedBuilder eb = new EmbedBuilder();
        StringBuilder sb = new StringBuilder();
        eb.setTitle("Inventaire de " + member.getEffectiveName());
        eb.setColor(Squads.getSquadByMember(member).getSquadRole(member.getGuild()).getColor());
        for (String str : inv.keySet()) {
            sb.append(str).append(" x").append(inv.get(str));
            if (i == 2) {
                sb.append('\n');
                i = 0;
            } else {
                i++;
                sb.append("  ");
            }
        }
        eb.setDescription(sb);
        return eb;
    }
}
