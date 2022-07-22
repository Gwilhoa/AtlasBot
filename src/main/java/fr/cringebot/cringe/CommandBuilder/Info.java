package fr.cringebot.cringe.CommandBuilder;

import fr.cringebot.cringe.escouades.Squads;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

public class Info {
    public static EmbedBuilder info(Member mem)
    {
        Squads squads = Squads.getSquadByMember(mem);
        String sb = "> surnom : " + mem.getEffectiveName() + '\n' +
                "> état : " + mem.getOnlineStatus().name() + '\n' +
                "> rejoint le serveur le " + mem.getTimeJoined().getDayOfMonth() + "/" + mem.getTimeJoined().getMonthValue() + "/" + mem.getTimeJoined().getYear() + "\n" +
                "> creer son compte le " + mem.getTimeCreated().getDayOfMonth() + "/" + mem.getTimeCreated().getMonthValue() + "/" + mem.getTimeCreated().getYear() + "\n" +
                "> B2C : " + squads.getStatMember(mem).getCoins() + '\n' +
                "> escouade : " + squads.getName() + "\n" +
                "> points : " + squads.getStatMember(mem).getPoints() + "\n" +
                "> rang : " + squads.getRank(mem.getId());
        return new EmbedBuilder()
                .setColor(squads.getSquadRole(mem.getGuild()).getColor())
                .setAuthor(mem.getUser().getName(), null, mem.getUser().getAvatarUrl() + "?size=256")
                .setTitle("Informations")
                .setDescription(sb);
    }
}
