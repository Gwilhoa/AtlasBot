package fr.cringebot.cringe.xp;

import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;

import java.io.*;
import java.util.HashMap;

import static fr.cringebot.cringe.event.BotListener.gson;

public class XP {
    private static final String fileXp = "save/xp.json";
    private static final TypeToken<HashMap<String, XP>> typeXp = new TypeToken<HashMap<String, XP>>() {
    };

    private static final String fileValue = "save/value.json";
    private static final TypeToken<HashMap<Character, Long>> typeValue = new TypeToken<HashMap<Character, Long>>() {
    };
    public static HashMap<String, XP> list = new HashMap<>();
    public static HashMap<String, Long> time = new HashMap<>();
    public static HashMap<String, Long> value = new HashMap<String, Long>();
    private final String name;
    private Long vocal;
    private Long text;

    public static void startTime(Member m)
    {
        if (!time.containsKey(m.getId()))
            time.put(m.getId(), System.currentTimeMillis());
    }

    public static Long getTotalValue()
    {
        long resultat = 0L;
        for (Long v : value.values())
            resultat = resultat + v;
        return (resultat);
    }

    public static float getXpByChar(String c)
    {
        addchar(c);
        return (((float)value.get(c)/(float)getTotalValue())-1) * -1;
    }

    public static void getXpByMessage(String msg, Member m)
    {
        float total = 0;
        for (char c : msg.toCharArray())
            total = total + getXpByChar(c+"");
        XP xp = getXp(m);
        xp.addText((long)total);
        XP.saveValue();
    }

    public static void addchar(String c)
    {
        if (!value.containsKey(c))
            value.put(c, 1L);
        else
            value.put(c, value.get(c)+1L);
    }

    public static void stopTime(Member m)
    {
        if (time.containsKey(m.getId()))
        {
            System.out.println( m.getUser().getName() +" "+ getXpByTime(System.currentTimeMillis() - time.get(m.getId())));
            XP xp = getXp(m);
            xp.addVoc(getXpByTime(System.currentTimeMillis() - time.get(m.getId())));
            time.remove(m.getId());
        }
    }

    public static void connecting(GuildVoiceJoinEvent event) {
        int i = 0;
        for (Member m : event.getChannelJoined().getMembers())
            if (!m.getUser().isBot())
                i++;
        if (i > 1) {
            for (Member m : event.getChannelJoined().getMembers())
                XP.startTime(m);
        }
    }

    public static void disconnecting(GuildVoiceLeaveEvent event)
    {
        stopTime(event.getMember());
        if (event.getChannelLeft().getMembers().size() == 1)
            stopTime(event.getChannelLeft().getMembers().get(0));
    }

    public XP(String id, String name) {
        this.name = name;
        this.vocal = 0L;
        this.text = 0L;
        list.put(id, this);
        save();
    }

    public void setText(Long text) {
        this.text = text;
    }

    public static Long getXpByTime(Long l)
    {
        long ret = (long) Math.pow(l,(float) 1/3) - 35;
        if (ret <= 0)
            return ((long)0);
        return ret;
    }

    public void setVocal(Long vocal) {
        this.vocal = vocal;
    }

    public void addVoc(Long xp)
    {
        this.vocal = this.vocal + xp;
        save();
    }

    public void addText(Long xp)
    {
        this.text = this.text + xp;
        save();
    }

    public static XP getXp(Member m)
    {
        return getXp(m.getId(), m.getUser().getName());
    }

    private static XP getXp(String id, String name)
    {
        if (!list.containsKey(id))
            list.put(id, new XP(id, name));
        return list.get(id);
    }

    public static void load() {
        if (new File(fileXp).exists()) {
            try {
                list = gson.fromJson(new BufferedReader(new InputStreamReader(new FileInputStream(fileXp))), typeXp.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new File(fileXp).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (list == null)
            list = new HashMap<>();
    }

    public static void loadValue() {
        if (new File(fileValue).exists()) {
            try {
                value = gson.fromJson(new BufferedReader(new InputStreamReader(new FileInputStream(fileValue))), typeValue.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new File(fileValue).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (value == null)
            value = new HashMap<>();
    }

    private static void saveValue() {
        if (!new File(fileValue).exists()) {
            try {
                new File(fileValue).createNewFile();
            } catch (IOException e) {
                return;
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileValue)));
            gson.toJson(value, typeValue.getType(), bw);
            bw.flush();
            bw.close();
        } catch (IOException e) {
        }
    }

    private void save() {
        if (!new File(fileXp).exists()) {
            try {
                new File(fileXp).createNewFile();
            } catch (IOException e) {
                return;
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileXp)));
            gson.toJson(list, typeXp.getType(), bw);
            bw.flush();
            bw.close();
        } catch (IOException e) {
        }
    }

}
