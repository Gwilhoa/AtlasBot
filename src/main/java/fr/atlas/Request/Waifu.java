package fr.atlas.Request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.IOException;
import java.util.ArrayList;

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
        return getWaifusObj(data).get(0);
    }

    public static ArrayList<Waifu> getWaifusObj(String data) {
        System.out.println(data);
        ArrayList<Waifu> ret = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        JsonArray array = null;
        array = gson.fromJson(data, JsonArray.class);
        array.forEach(jsonElement -> {
            ret.add(
                    new Waifu(
                            jsonElement.getAsJsonObject().get("name").getAsString(),
                            jsonElement.getAsJsonObject().get("id").getAsInt(),
                            jsonElement.getAsJsonObject().get("description").getAsString(),
                            jsonElement.getAsJsonObject().get("origin").getAsString(),
                            jsonElement.getAsJsonObject().get("rare").getAsInt(),
                            jsonElement.getAsJsonObject().get("epic").getAsInt(),
                            jsonElement.getAsJsonObject().get("legendary").getAsInt()
                    )
            );
        });
        return ret;
    }

    public static ArrayList<Waifu> getWaifus() throws IOException {
        return getWaifusObj(Request.GetRequest("waifus"));
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

    public static Waifu getWaifuById(int id) throws IOException {
        return getWaifuObj("[" + Request.GetRequest("waifus/id/" + id) + "]");
    }

    public static ArrayList<Waifu> getWaifuByName(String name) throws IOException {
        System.out.println(Request.GetRequest("waifus/search/" + name));
        return getWaifusObj(Request.GetRequest("waifus/search/" + name));
    }

    public EmbedBuilder getEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(this.name + " de " + this.origin);
        eb.setDescription(this.description);
        eb.appendDescription("\n\nRare trouvée: " + this.rare + "\n" +
                "Epic trouvée: " + this.epic + "\n" +
                "Legendaire trouvée: " + this.legendary);
        eb.setFooter("ID: " + this.id);
        eb.setImage(this.imageurl);
        return eb;
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