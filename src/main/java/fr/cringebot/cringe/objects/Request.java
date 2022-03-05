package fr.cringebot.cringe.objects;

import java.io.*;
import java.net.HttpURLConnection;
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

    public static boolean sendRequest(Type tpe, String send)
    {
        try {
            BufferedReader br = new BufferedReader(new FileReader("save/secret.txt"));
            URL url = new URL("https://play.timoreo.fr/app/"+tpe.getName());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", br.readLine());
            conn.setDoOutput(true);
            OutputStream stream = conn.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(send);
            writer.close();
            conn.connect();
            if (conn.getResponseCode() != 200) {
                System.out.println(conn.getResponseCode());
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        System.out.println("request sucessfull \n"+tpe+"\n"+send);
        return true;
    }
}
