package fr.cringebot.cringe.objects;

import fr.cringebot.cringe.Request.Members;
import net.dv8tion.jda.api.entities.Guild;


import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class XpManager {
    private final static Long t = System.currentTimeMillis();
    public static HashMap<String, Long> lastMessage = new HashMap<>();

    public static void sendMessage(String id, Guild guild) throws IOException {
        if (Members.getMember(id) == null) return;
        if (lastMessage.get(id) == null) {
                Members.addPoints(id, getTextualPoint(System.currentTimeMillis() - t).intValue(), guild);
            lastMessage.put(id, System.currentTimeMillis());
        } else {
                Members.addPoints(id, getTextualPoint(System.currentTimeMillis() - t).intValue(), guild);
            lastMessage.replace(id, System.currentTimeMillis());
        }
    }

    private static Double getTextualPoint(Long time)
    {
        double ret = Math.round((125/sqrt(2* Math.PI)) * Math.exp(pow((time-60000),2)/-72000000) + 7.61 * pow(10, -12) * pow(time, 2));
        return Math.min(ret, 395.0);
    }
}
