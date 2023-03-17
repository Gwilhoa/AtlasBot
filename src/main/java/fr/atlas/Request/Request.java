package fr.atlas.Request;

import com.diogonunes.jcolor.Attribute;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.diogonunes.jcolor.Ansi.colorize;
import static fr.atlas.BotDiscord.token;

public class Request {
    private static final String apiurl = "https://api.bitume2000.fr/v1/";

    public static String GetRequest(String route) throws IOException {
        System.out.println(colorize("[API] GET "+ apiurl + route, Attribute.TEXT_COLOR(25,99, 100)));
        URL url = new URL(apiurl + route);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("token", token);
        con.connect();
        String response = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        if (response == null) {
            throw new IOException("null response");
        }
        return response;
    }

    public static String PostRequest(String route, String content) throws IOException {
        System.out.println(colorize("[API] POST "+ apiurl + route, Attribute.TEXT_COLOR(25,99, 100)));
        URL url = new URL(apiurl + route);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("token", token);
        con.setDoOutput(true);
        OutputStream stream = con.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        writer.write(content);
        writer.close();
        stream.close();
        con.connect();
        if (con.getResponseCode() == 500) {
            throw new RuntimeException("Failed : HTTP error code : " + con.getResponseCode());
        }
        return con.getResponseMessage();
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
