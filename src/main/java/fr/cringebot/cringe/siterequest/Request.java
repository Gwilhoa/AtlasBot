package fr.cringebot.cringe.siterequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Request {

    public enum Type{
        SETSEASON("setSeason");

        private final String name;
        Type(String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static String GetRequest(String route)
    {
        try {
            URL url = new URL("http://api.bitume2000.fr:5000/v1/" + route);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("token", "b2k-admin205.1-2022-gwil42lyon-nekixilam");
            con.connect();
            if (con.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + con.getResponseCode());
            }
            return new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean PostRequest(String route, String content)
    {
        try {
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
                System.out.println("Failed : HTTP error code : " + con.getResponseCode() +","+con.getResponseMessage()  );
                return false;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean RemoveSquads(String id)
    {
        return PostRequest("squads/remove", "id="+id);
    }

    public static boolean newSquads(String name, String id)
    {
        return PostRequest("squads","name="+name+"&id="+id);
    }

    public static String getSquads()
    {
        return GetRequest("squads");
    }

    public static String getSquads(String id)
    {
        return GetRequest("squads/"+id);
    }

    public static boolean addManualPoint(String id, Integer number)
    {
        return PostRequest("squads/"+id, "points="+number);
    }
}
