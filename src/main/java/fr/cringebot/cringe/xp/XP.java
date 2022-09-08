package fr.cringebot.cringe.xp;

import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.escouades.Squads;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Member;

import java.util.HashMap;

public class XP {
    private static final HashMap<String, Long> vocalTime = new HashMap<>();

    public static Boolean isVoiceXp(AudioChannel vc){
        int i = 0;
        for (Member m : vc.getMembers())
        {
            if (!m.getRoles().contains(vc.getGuild().getRoleById(BotDiscord.SecondaryRoleId)) && !m.getRoles().contains(vc.getGuild().getRoleById("502530450279890945")) && !m.getVoiceState().isMuted())
                i++;
        }
        return (i >= 2 && !vc.getId().equals("979859652848283748"));
    }


    public static void start(Member m) {
        start(m.getId());
    }

    public static void start(String id){
        if (vocalTime.get(id) == null)
            vocalTime.put(id, System.currentTimeMillis());
    }

    public static void end(Member m) {
        end(m.getId());
    }
    public static void end(String id) {
        if (vocalTime.get(id) == null)
            return;
        Long time = System.currentTimeMillis() - vocalTime.get(id);
        vocalTime.remove(id);
        time = calcul(time);
        Squads.addPoints(id, time);
    }

    private static Long calcul(Long time){
        double d = time/3600;
        return Math.round(d);
    }
}
