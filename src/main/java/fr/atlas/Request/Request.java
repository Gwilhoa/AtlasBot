package fr.atlas.Request;

import com.diogonunes.jcolor.Attribute;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.atlas.BotDiscord;
import net.dv8tion.jda.api.EmbedBuilder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import static com.diogonunes.jcolor.Ansi.colorize;
import static fr.atlas.BotDiscord.token;

public class Request {
    private static final String apiurl = "http://localhost:42000/v2/"; //"https://api.bitume2000.fr/v2/";
    private static String accessToken = null;
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json");

    public static void login() throws IOException {
        String response = PostRequest("auth/login", "{\"username\":\""+ "AtlasBot" +"\",\"password\":\""+ BotDiscord.token +"\"}");
        Gson gson = new Gson();
        accessToken = gson.fromJson(response, JsonObject.class).get("token").getAsString();
    }
    public static String GetRequest(String route) throws IOException {
        URL url = new URL(apiurl + route);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("authorization", "Bearer " + accessToken);
        con.connect();
        if (con.getResponseCode() == 401)
        {
            login();
            return GetRequest(route);
        }
        String response = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        if (con.getResponseCode() == 502)
            throw new ConnectException("API offline" + con.getResponseCode());
        System.out.println(colorize("[API] GET "+ apiurl + route + "\n" + con.getResponseCode() + " RESPONSE : "+ response , Attribute.TEXT_COLOR(25,99, 100)));
        return response;
    }

//    public static String PostRequest(String route, String content) throws IOException {
//        URL url = new URL(apiurl + route);
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("POST");
//        con.setRequestProperty("content-type", "application/json");
//        con.setRequestProperty("authorization", "Bearer " + accessToken);
//        con.setDoOutput(true);
//        OutputStream stream = con.getOutputStream();
//        OutputStreamWriter writer = new OutputStreamWriter(stream);
//        writer.write(content);
//        writer.close();
//        stream.close();
//        con.connect();
//        if (con.getResponseCode() == 502)
//            throw new ConnectException("API offline" + con.getResponseCode());
//        if (con.getResponseCode() == 401)
//        {
//            login();
//            return PostRequest(route, content);
//        }
//        String response = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
//        System.out.println(colorize("[API] POST "+ apiurl + route + "\n" + con.getResponseCode() + " RESPONSE : "+ response , Attribute.TEXT_COLOR(25,99, 100)));
//        return response;
//    }

    public static String PostRequest(String route, String content) throws IOException {

        String ApiUrl = apiurl + route;
        RequestBody body = RequestBody.create(JSON, content);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(ApiUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            int responseCode = response.code();
            if (responseCode == 502) {
                throw new ConnectException("API offline " + responseCode);
            } else if (responseCode == 401) {
                login();
                return PatchRequest(route, content);
            } else if (response.isSuccessful()) {
                String responseString = response.body().string();
                System.out.println(colorize("[API] POST "+ ApiUrl +"\n" + response.code() + " RESPONSE : "+ responseString, Attribute.TEXT_COLOR(25,99, 100)));
                return responseString;
            } else {
                String responseString = response.body().string();
                System.out.println(colorize("[API] POST " + ApiUrl + " failed with response code: " + response.code() +"\n" + responseString, Attribute.RED_TEXT()));
                return responseString;
            }
        }
    }
    public static String PatchRequest(String route, String content) throws IOException {

        String ApiUrl = apiurl + route;
        RequestBody body = RequestBody.create(JSON, content);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(ApiUrl)
                .patch(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            int responseCode = response.code();
            if (responseCode == 502) {
                throw new ConnectException("API offline " + responseCode);
            } else if (responseCode == 401) {
                login();
                return PatchRequest(route, content);
            } else if (response.isSuccessful()) {
                String responseString = response.body().string();
                System.out.println(colorize("[API] PATCH "+ ApiUrl +"\n" + response.code() + " RESPONSE : "+ responseString, Attribute.TEXT_COLOR(25,99, 100)));
                return responseString;
            } else {
                String responseString = response.body().string();
                System.out.println(colorize("[API] PATCH " + ApiUrl + " failed with response code: " + response.code() +"\n" + responseString, Attribute.RED_TEXT()));
                return responseString;
            }
        }
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
