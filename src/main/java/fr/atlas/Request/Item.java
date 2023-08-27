package fr.atlas.Request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import fr.atlas.BotDiscord;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.IOException;
import java.util.ArrayList;

import static fr.atlas.BotDiscord.setError;

public class Item {

    private final String id;
    private final String name;
    private final String description;
    private final int price;

    public Item(String id, String name, String description, int price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public static Item getItemObj(String data) {
        return getItemsObj(data).get(0);
    }

    public static ArrayList<Item> getItemsObj(String data) {
        ArrayList<Item> ret = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        JsonArray array = null;
        try {
            array = gson.fromJson(data, JsonArray.class);
        } catch (Exception e){
            data = "[" + data + "]";
            array = gson.fromJson(data, JsonArray.class);
        }
        array.forEach(jsonElement -> {
            ret.add(
                    new Item(
                            jsonElement.getAsJsonObject().get("id").getAsString(),
                            jsonElement.getAsJsonObject().get("name").getAsString(),
                            jsonElement.getAsJsonObject().get("description").getAsString(),
                            jsonElement.getAsJsonObject().get("price").getAsInt()
                    )
            );
        });
        return ret;
    }

    public static Item createItem(String name, String description, int price) throws IOException {
        return getItemObj(Request.PostRequest("item", "{\"name\":\"" + name + "\",\"description\":\"" + description + "\",\"price\":" + price + "}"));
    }

    public static ArrayList<Item> getItems() {
        try {
            return getItemsObj(Request.GetRequest("item"));
        } catch (IOException e) {
            setError(e);
        }
        return null;
    }

    public static Item getItemById(String id) throws IOException {
        return getItemObj(Request.GetRequest("item/id/" + id));
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }

    public String getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public EmbedBuilder getEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(name);
        eb.setDescription(description);
        eb.addField("Prix", price + " B2C", false);
        return eb;
    }
}
