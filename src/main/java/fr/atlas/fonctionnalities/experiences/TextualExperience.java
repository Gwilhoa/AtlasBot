package fr.atlas.fonctionnalities.experiences;

import fr.atlas.Request.User;
import net.dv8tion.jda.api.entities.Guild;


import java.io.IOException;
import java.util.HashMap;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class TextualExperience {
    private final static Long t = System.currentTimeMillis();
    public static HashMap<String, Long> lastMessage = new HashMap<>();

    public static void sendMessage(String id, Guild guild) throws IOException {
        if (lastMessage.get(id) == null) {
                User.addPoints(id, getTextualPoint(System.currentTimeMillis() - t).intValue());
            lastMessage.put(id, System.currentTimeMillis());
        } else {
                User.addPoints(id, getTextualPoint(System.currentTimeMillis() - t).intValue());
            lastMessage.replace(id, System.currentTimeMillis());
        }
    }

    private static Double getTextualPoint(Long time)
    {
        double ret = Math.round((125/sqrt(2* Math.PI)) * Math.exp(pow((time-60000),2)/-72000000) + 7.61 * pow(10, -12) * pow(time, 2));
        return Math.min(ret, 395.0);
    }
}
