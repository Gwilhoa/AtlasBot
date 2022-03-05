package fr.cringebot.cringe.cki;

import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.HashMap;
import java.util.List;

import static fr.cringebot.cringe.event.BotListener.gson;

public class cki {
    private static final String file = "save/cki.json";
    private static final TypeToken<HashMap<String, cki>> typeToken = new TypeToken<HashMap<String, cki>>() {
    };
    private String channel;
    private Type type;
    private String Message;
    private String Name;
    private Integer action;
    private Integer indice;
    public static HashMap<String, cki> wtpThreads = null;


    public cki(Type t, String message, String name, String channels)
    {
        channel = channels;
        type = t;
        Message = message;
        Name = name;
        indice = 0;
        action = 0;
    }

    enum Type {
        POKEMON("Pokémon"),
        LOL("League of Legends");

        Type(String name) {

        }
        Type tpe;
    }

    public String getMessage() {
        return Message;
    }

    public Integer getAction() {
        return action;
    }

    public void addAction()
    {
        action++;
        save();
    }

    public String getName() {
        return Name;
    }

    public Type getType() {
        return type;
    }

    public Integer getIndice() {
        return indice;
    }

    public void addIndice()
    {
        indice++;
        save();
    }
    public static void load() {
        if (new File(file).exists()) {
            try {
                wtpThreads = gson.fromJson(new BufferedReader(new InputStreamReader(new FileInputStream(file))), typeToken.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new File(file).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (wtpThreads == null)
            wtpThreads = new HashMap<>();
    }
    public static void save() {
        if (!new File(file).exists()) {
            try {
                new File(file).createNewFile();
            } catch (IOException e) {
                return;
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            gson.toJson(wtpThreads, typeToken.getType(), bw);
            bw.flush();
            bw.close();
        } catch (IOException e) {
        }
    }

    public String getChannel() {
        return channel;
    }
}
