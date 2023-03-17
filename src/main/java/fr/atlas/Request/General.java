package fr.atlas.Request;


import java.io.IOException;

public class General {
    private String season;

    public General(String season) {
        this.season = season;
    }

    public String getSeason() throws IOException {
        return Request.GetRequest("https://api.cringebot.fr/general/season");
    }

    public void setSeason(String season) throws IOException {
        Request.PostRequest("https://api.cringebot.fr/general/season", "season=" + season);
    }
}
