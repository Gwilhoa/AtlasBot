package fr.cringebot.cringe.Request;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Achievement {

    private final String id;
    private final String name;
    private final String description;
    private final String image;
    private final Integer points;
    private final Integer coins;
    private final String title;

    public Achievement(String id, String name, String description, String image, Integer points, Integer coins, String title) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.points = points;
        this.coins = coins;
        this.title = title;
    }

    public static List<Achievement> getObjAchievement(String data) throws IOException {
        ArrayList<Achievement> achievements = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        builder.create().fromJson(data, JsonArray.class).forEach(jsonElement -> {
            achievements.add(
                    new Achievement(
                            jsonElement.getAsJsonObject().get("id").getAsString(),
                            jsonElement.getAsJsonObject().get("name").getAsString(),
                            jsonElement.getAsJsonObject().get("description").getAsString(),
                            jsonElement.getAsJsonObject().get("imgurl").getAsString(),
                            jsonElement.getAsJsonObject().get("pointprice").getAsInt(),
                            jsonElement.getAsJsonObject().get("coinsprice").getAsInt(),
                            jsonElement.getAsJsonObject().get("titleprice").getAsString()
                    ));
        });
        return achievements;
    }

    public static boolean createAchievement(String name, String description, String imgurl, int points, int coins, String title) throws IOException {
        Request.PostRequest("achievement", "name=" + name + "&description=" + description + "&imgurl=" + imgurl + "&points=" + points + "&coins=" + coins + "&title=" + title);
        return true;
    }

    public static List<Achievement> getAchievements() throws IOException {
        return getObjAchievement(Request.GetRequest("achievement"));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public Integer getPoints() {
        return points;
    }

    public Integer getCoins() {
        return coins;
    }
}
