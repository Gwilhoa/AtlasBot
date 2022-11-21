package fr.cringebot.cringe.Request;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request {

    public static String GetRequest(String route) throws IOException {
        System.out.println("http://api.bitume2000.fr:5000/v1/" + route);
        URL url = new URL("http://api.bitume2000.fr:5000/v1/" + route);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("token", "b2k-admin205.1-2022-gwil42lyon-nekixilam");
        con.connect();
        if (con.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + con.getResponseCode());
        }
        return new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
    }

    public static boolean PostRequest(String route, String content) throws IOException {
        URL url = new URL("http://api.bitume2000.fr:5000/v1/" + route);
        System.out.println(url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("token", "b2k-admin205.1-2022-gwil42lyon-nekixilam");
        con.setDoOutput(true);
        OutputStream stream = con.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        writer.write(content);
        writer.close();
        stream.close();
        con.connect();
        if (con.getResponseCode() != 200) {
            System.out.println("Failed : HTTP error code : " + con.getResponseCode() + "," + con.getResponseMessage());
            return false;
        }
        return true;
    }

    public static EmbedBuilder DisconnectedEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Erreur");
        embed.setDescription("Le serveur est actuellement indisponible");
        embed.setColor(Color.RED);
        return embed;
    }

    public static EmbedBuilder MemberNotFoundEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Erreur");
        embed.setDescription("Le membre n'a pas été trouvé");
        embed.setColor(Color.RED);
        return embed;
    }

}
