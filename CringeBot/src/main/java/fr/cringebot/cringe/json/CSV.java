package fr.cringebot.cringe.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CSV {
    public static String toCSV(HashMap<String, Long> m) {
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String, Long> e : m.entrySet()) {
            buf.append(e.getKey()).append(',').append(e.getValue()).append('\n');
        }
        return buf.toString();
    }

    public static void save(HashMap<String, Long> m, String file) {
        try {
            try (PrintWriter out = new PrintWriter(file)) {
                out.print(toCSV(m));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static LinkedHashMap<String, Long> load(String file) {

        byte[] encoded = new byte[0];
        try {
            new File(file).createNewFile();
            encoded = Files.readAllBytes(Paths.get(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parseCSV(new String(encoded, StandardCharsets.UTF_8));
    }

    public static LinkedHashMap<String, Long> parseCSV(String s) {
        StringBuilder buf = new StringBuilder();
        String b = "";
        LinkedHashMap<String, Long> xps = new LinkedHashMap<>();
        boolean string = true;
        for (char c : s.toCharArray()) {
            if (c == ',') {
                if (string) {
                    string = false;
                    b = buf.toString();
                    buf = new StringBuilder();
                }
            } else if (c == '\n') {
                if (!string) {
                    string = true;
                    xps.put(b, Long.parseLong(buf.toString()));
                    b = "";
                    buf = new StringBuilder();
                }
            } else if (string) {
                buf.append(c);
            } else {
                buf.append(c);
            }
        }

        return xps;
    }
}
