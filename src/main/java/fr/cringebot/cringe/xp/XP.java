package fr.cringebot.cringe.xp;

import java.util.ArrayList;

public class XP {
    private static final ArrayList<Integer> freqmsg = new ArrayList<>();
    private static Integer nbmsg = 0;
    private static Long starttime;

    public static void addmsg(){
        nbmsg++;
    }

    public static void startloop() throws InterruptedException {
        starttime = System.currentTimeMillis();
        nbmsg = 0;
        while (starttime + 1800000 > System.currentTimeMillis())
            Thread.sleep(1000);
        freqmsg.add(nbmsg);
        System.out.println(freqmsg);
    }
}
