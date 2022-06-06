package fr.cringebot.cringe.xp;

import fr.cringebot.cringe.escouades.Squads;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.HashMap;

public class XP {
    private static final HashMap<String, Long> vocalTime = new HashMap<>();

    public static Boolean isVoiceXp(AudioChannel vc){
        return (vc.getMembers().size() >= 2 || !vc.getId().equals("979859652848283748"));
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
        System.out.println("tps en millis : "+ time);
        long pts = calcul(time);
        System.out.println("pts gagné : "+ pts);
        Squads.addPoints(id, pts);
    }

    private static Long calcul(Long time){
        double d = time/3600;
        return Math.round(d);
    }
}