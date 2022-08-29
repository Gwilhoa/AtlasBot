package fr.cringebot.cringe.CommandBuilder;

import fr.cringebot.cringe.escouades.SquadMember;
import fr.cringebot.cringe.escouades.Squads;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.List;

public class Top {
    public static EmbedBuilder top(String SquadName, Guild guild) {
        EmbedBuilder eb = new EmbedBuilder();
        StringBuilder sb = new StringBuilder();
        if (SquadName == null) {
            List<Squads> squads = Squads.getSortedSquads();
            eb.setTitle("Classement :");
            for (Squads sq : squads)
                sb.append(sq.getSquadRole(guild).getAsMention()).append(" : ").append(sq.getTotal()).append('\n')
                        .append(" meilleur : ")
                        .append(guild.getMemberById(sq.getBestid()).getAsMention())
                        .append(" avec ").append(sq.getStatMember(sq.getBestid()).getPoints()).append('\n');
            eb.setFooter(squads.get(0).getName() + " a un bonus de gains d'argent");
            eb.setColor(squads.get(0).getSquadRole(guild).getColor());
            eb.setDescription(sb);
        } else if (SquadName.equalsIgnoreCase("general")) {
            ArrayList<SquadMember> AllSm = Squads.getSortedAllSquadMember();
            eb.setTitle("Classement Général :");
            int i = 1;
            for (SquadMember sm : AllSm) {
                sb.append("#").append(i).append(" ").append(guild.getMemberById(sm.getId()).getAsMention()).append(" avec ").append(sm.getPoints()).append(" points\n");
                i++;
                if (i == 21)
                    break;
            }
            eb.setDescription(sb);
        } else if (SquadName.equalsIgnoreCase("coins")) {
            ArrayList<SquadMember> sq = Squads.getAllSquadMember();
            ArrayList<SquadMember> ret = new ArrayList<>();
            long best;
            SquadMember sm = null;
            while (!sq.isEmpty())
            {
                best = -10000000L;
                for (SquadMember squadmember : sq)
                {
                    if (squadmember.getCoins() > best) {
                        sm = squadmember;
                        best = sm.getCoins();
                    }
                }
                ret.add(sm);
                sq.remove(sm);
            }
            eb.setTitle("Classement d'argent :");
            int i = 1;
            for (SquadMember sm2 : ret) {
                sb.append("#").append(i).append(" ").append(guild.getMemberById(sm2.getId()).getAsMention()).append(" avec ").append(sm2.getCoins()).append(" B2C\n");
                i++;
                if (i == 21)
                    break;
            }
            eb.setDescription(sb);
        } else {
            Squads squad = Squads.getSquadByName(SquadName);
            if (squad == null)
                return new EmbedBuilder().setTitle("escouade "+ SquadName).setDescription("cette escouade n'existe pas");
            List<SquadMember> sm = squad.getSortedSquadMember();
            eb.setColor(squad.getSquadRole(guild).getColor());
            eb.setTitle("escouade "+ squad.getName());
            int i = 1;
            for (SquadMember s : sm)
            {
                sb.append("n°")
                        .append(i).append(" : ");
                if (guild.getMemberById(s.getId()) != null)
                    sb.append(guild.getMemberById(s.getId()).getAsMention());
                else
                    sb.append("Inconnu");
                sb.append(" avec ").append(s.getPoints()).append("\n");
                i++;
            }
            eb.setDescription(sb.toString());
        }
        return eb;
    }
}
