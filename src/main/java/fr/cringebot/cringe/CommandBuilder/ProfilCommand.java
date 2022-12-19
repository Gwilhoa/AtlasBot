package fr.cringebot.cringe.CommandBuilder;

import fr.cringebot.cringe.Request.Achievement;
import fr.cringebot.cringe.Request.Members;
import fr.cringebot.cringe.Request.Request;
import fr.cringebot.cringe.Request.Squads;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfilCommand {

    public static EmbedBuilder CommandProfil(Member member){
        Members mem = null;
        List<Members> lst = null;
        List<Achievement> ach = null;
        try {
            mem = Members.getMember(member);
            lst = Squads.getMembers(mem.getSquad().getId());
            ach = Members.getAchievements(member);
        } catch (ConnectException e) {
            return Request.DisconnectedEmbed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mem == null) {
            return Request.MemberNotFoundEmbed();
        }
        lst.sort((o1, o2) -> o2.getPoints() - o1.getPoints());
        int i = 0;
        for (Members m : lst) {
            i++;
            if (Objects.equals(m.getId(), mem.getId())) {
                break;
            }
        }
        return new EmbedBuilder().setColor(mem.getColor())
                .setTitle("Profil de " + member.getUser().getName())
                .setThumbnail(member.getUser().getAvatarUrl())
                .addField("> Surnom :", member.getEffectiveName(), true)
                .addField("> escouade :", mem.getSquad().getName(), true)
                .addField("> Points :", String.valueOf(mem.getPoints()), true)
                .addField("> Coins :", mem.getCoins().toString(), true)
                .addField("> achievements :", ach.size() +"", true)
                .addField("> coming soon :", "coming soon", true)
                .addField("> Date d'entrée sur le serveur : ", String.format("%02d", member.getTimeJoined().getDayOfMonth()) + "/" + String.format("%02d", member.getTimeJoined().getMonthValue()) + "/" + member.getTimeJoined().getYear(), false)
                .addField("> Date de création du compte : ", String.format("%02d", member.getTimeCreated().getDayOfMonth()) + "/" + String.format("%02d", member.getTimeCreated().getMonthValue()) + "/" + member.getTimeCreated().getYear(), false)
                .setFooter("rang : "+ i);
    }

}
