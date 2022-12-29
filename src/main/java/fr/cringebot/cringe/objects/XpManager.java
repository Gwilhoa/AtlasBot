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
        double ret = (750000/(6000 * sqrt(2* Math.PI)))*Math.exp((-1/2) * pow(((time-60000)/(6000)), 2)) + 7.61 * pow(10, -12) * pow(time, 2);
        if (ret > 100)
            return 100.0;
        return ret;
    }
}
