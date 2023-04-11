package fr.atlas.Request;


import java.io.IOException;

public class General {
    private String season;


    public static String getSeason() throws IOException {
        return Request.GetRequest("general/season");
    }

    public static void setSeason(String season) throws IOException {
        Request.PostRequest("general/season", "season=" + season);
    }
}
