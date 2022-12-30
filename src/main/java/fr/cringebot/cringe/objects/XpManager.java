package fr.cringebot.cringe.objects;

import fr.cringebot.cringe.Request.Members;


import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class XpManager {
    private final static Long t = System.currentTimeMillis();
    public static HashMap<String, Long> lastMessage = new HashMap<>();

    public static void sendMessage(String id) {
        if (lastMessage.get(id) == null) {
            try {
                Members.addPoints(id, getTextualPoint(System.currentTimeMillis() - t).intValue());
            } catch (IOException e) {
                return;
            }
            lastMessage.put(id, System.currentTimeMillis());
        } else {
            try {
                Members.addPoints(id, getTextualPoint(System.currentTimeMillis() - t).intValue());
            } catch (IOException e) {
                return;
            }
            lastMessage.replace(id, System.currentTimeMillis());
        }
    }

    private static Double getTextualPoint(Long time)
    {
        System.out.println(time);
        double ret = Math.round((125/sqrt(2* Math.PI)) * Math.exp(pow((time-60000),2)/-72000000) + 7.61 * pow(10, -12) * pow(time, 2));
        System.out.println(ret);
        return Math.min(ret, 100.0);
    }
}
