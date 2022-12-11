package fr.cringebot.cringe.objects;

import fr.cringebot.cringe.Request.Members;


import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;

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
        if (time < 60000)
            return 0D;
        Double ret = (100 - Math.exp((double)(-time - 60000)/ 702000));
        System.out.println(ret);
        return ret;
    }
}
