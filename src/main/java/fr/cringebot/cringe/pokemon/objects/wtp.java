package fr.cringebot.cringe.pokemon.objects;

import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.HashMap;

import static fr.cringebot.cringe.event.BotListener.gson;

public class wtp {
    private static final String file = "save/wtp.json";
    private static final TypeToken<HashMap<String, wtp>> type = new TypeToken<HashMap<String, wtp>>() {
    };
    private String Message;
    private String Name;
    public static HashMap<String, wtp> wtpThreads = null;


    public wtp(String message, String name)
    {
        Message = message;
        Name = name;
    }

    public String getMessage() {
        return Message;
    }

    public String getName() {
        return Name;
    }

    public static void load() {
        if (new File(file).exists()) {
            try {
                wtpThreads = gson.fromJson(new BufferedReader(new InputStreamReader(new FileInputStream(file))), type.getType());
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
            gson.toJson(wtpThreads, type.getType(), bw);
            bw.flush();
            bw.close();
        } catch (IOException e) {
        }
    }
}
