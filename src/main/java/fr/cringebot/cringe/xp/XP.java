package fr.cringebot.cringe.xp;

import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static fr.cringebot.cringe.event.BotListener.gson;

public class XP {
    private static ArrayList<Integer> freqmsg = new ArrayList<>();
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
