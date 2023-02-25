package fr.cringebot.cringe.Request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

public class Waifu {
    private final String name;
    private final int id;
    private final String description;
    private final String imageurl;
    private final int rare;
    private final int epic;
    private final int legendary;
    private final String origin;


    public Waifu(String name, int id, String description, String origin, int rare, int epic, int legendary) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.imageurl = "https://cdn.bitume2000.fr/waifu/"+ id+".png";
        this.rare = rare;
        this.epic = epic;
        this.legendary = legendary;
        this.origin = origin;
    }

    public static Waifu getWaifuObj(String data) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        JsonArray array = null;
        array = gson.fromJson(data, JsonArray.class);
        return new Waifu(
                array.get(0).getAsJsonObject().get("name").getAsString(),
                array.get(0).getAsJsonObject().get("id").getAsInt(),
                array.get(0).getAsJsonObject().get("description").getAsString(),
                array.get(0).getAsJsonObject().get("origin").getAsString(),
                array.get(0).getAsJsonObject().get("rare").getAsInt(),
                array.get(0).getAsJsonObject().get("epic").getAsInt(),
                array.get(0).getAsJsonObject().get("legendary").getAsInt()
        );
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getOrigin() {
        return origin;
    }

    public int getRare() {
        return rare;
    }

    public int getEpic() {
        return epic;
    }

    public int getLegendary() {
        return legendary;
    }

    @Override
    public String toString() {
        return "Waifu{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", imageurl='" + imageurl + '\'' +
                ", rare=" + rare +
                ", epic=" + epic +
                ", legendary=" + legendary +
                '}';
    }
}