package fr.cringebot.cringe.xp;

import fr.cringebot.cringe.escouades.Squads;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.HashMap;

public class XP {
    private static final HashMap<String, Long> vocalTime = new HashMap<>();


    public static void start(Member m) {
        start(m.getId());
    }

    public static void start(String id){
        vocalTime.put(id, System.currentTimeMillis());
    }

    public static void end(Member m) {
        end(m.getId());
    }
    public static void end(String id) {
        if (vocalTime.get(id) == null)
            return;
        Long time = System.currentTimeMillis() - vocalTime.get(id);
        time = calcul(time);
        System.out.println(id + "+"+ time);
        Squads.addPoints(id, time);
    }

    private static Long calcul(Long time){
        double d = Math.pow(time, 2)/ 500000000;
        return Double.doubleToLongBits(d);
    }
}