package fr.atlas.command.CommandBuilder;

import fr.atlas.Request.Achievement;
import fr.atlas.Request.Request;
import fr.atlas.Request.Squads;
import fr.atlas.Request.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.net.ConnectException;
import java.util.List;

import static fr.atlas.BotDiscord.debug;


public class ProfilCommand {

    public static EmbedBuilder CommandProfil(Member member){
        User mem = null;
        List<User> lst = null;
        List<Achievement> ach = null;
        List<Achievement> achs = null;
        try {
            achs = Achievement.getAchievements();
            mem = User.getMember(member);
            ach = mem.getAchievements();
            lst = Squads.getMembers(mem.getSquad().getId());
        } catch (ConnectException e) {
            return Request.DisconnectedEmbed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int i = 0;
        for (User user : lst) {
            i++;
            if (user.getId().equals(mem.getId()))
                break;
        }
        EmbedBuilder eb = new EmbedBuilder().setColor(mem.getSquad().getColor())
                .setThumbnail(member.getUser().getAvatarUrl())
                .addField("> Surnom :", member.getEffectiveName(), true)
                .addField("> escouade :", mem.getSquad().getName(), true)
                .addField("> Points :", String.valueOf(mem.getPoints()), true)
                .addField("> Coins :", mem.getCoins().toString(), true)
                .addField("> achievements :", ach.size() +" / " + achs.size(), true)
                .addField("> memes approuvé :", mem.getMemevote().toString(), true)
                .addField("> Date d'entrée sur le serveur : ", String.format("%02d", member.getTimeJoined().getDayOfMonth()) + "/" + String.format("%02d", member.getTimeJoined().getMonthValue()) + "/" + member.getTimeJoined().getYear(), false)
                .addField("> Date de création du compte : ", String.format("%02d", member.getTimeCreated().getDayOfMonth()) + "/" + String.format("%02d", member.getTimeCreated().getMonthValue()) + "/" + member.getTimeCreated().getYear(), false)
                .setFooter("rang : "+ i);
        System.out.println("\""+mem.getTitle()+"\"");
        if (mem.getTitle() != null) {
            eb.setTitle("Profil de __" + mem.getTitle() + "__ " + member.getUser().getName());
        } else {
            eb.setTitle("Profil de " + member.getUser().getName());
        }
        return eb;
    }

}
