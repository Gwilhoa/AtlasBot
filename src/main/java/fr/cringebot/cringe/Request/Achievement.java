package fr.cringebot.cringe.Request;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

    public Achievement(String id, String name, String description, Integer points, Integer coins, String title) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.points = points;
        this.coins = coins;
        this.title = title;

        try {
            URL url = new URL("https://cdn.bitume2000.fr/achievement/" + id + ".png");
            url.openConnection();
            url.getContent();
        } catch (IOException e) {
            this.image = "https://cdn.bitume2000.fr/achievement/0.png";
            return;
        }
        this.image = "https://cdn.bitume2000.fr/achievement/" + id + ".png";
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
                            jsonElement.getAsJsonObject().get("pointprice").getAsInt(),
                            jsonElement.getAsJsonObject().get("coinsprice").getAsInt(),
                            jsonElement.getAsJsonObject().get("titleprice").getAsString()
                    ));
        });
        return achievements;
    }

    public static boolean createAchievement(String name, String description, int points, int coins, String title) throws IOException {
        Request.PostRequest("achievement", "name=" + name + "&description=" + description + "&points=" + points + "&coins=" + coins + "&title=" + title);
        return true;
    }

    public static List<Achievement> getAchievements() throws IOException {
        return getObjAchievement(Request.GetRequest("achievement"));
    }

    public static Achievement getAchievement(String id) throws IOException {
        return getObjAchievement("[" + Request.GetRequest("achievement/id/" + id) + "]").get(0);
    }

    public static boolean removeAchievement(String id) throws IOException {
        Request.GetRequest("achievement/remove/" + id);
        return true;
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

    @Override
    public String toString() {
        return "Achievement{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", points=" + points +
                ", coins=" + coins +
                ", title='" + title + '\'' +
                '}';
    }
}
