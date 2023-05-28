package fr.atlas.Request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.IOException;
import java.util.ArrayList;

public class MyItem {

    private final String id;
    private final String quantity;
    private final Item item;

    public MyItem(String id, String quantity, Item item) {
        this.id = id;
        this.quantity = quantity;
        this.item = item;
    }

    public static MyItem getItemObj(String data) {
        return getItemsObj(data).get(0);
    }

    public static ArrayList<MyItem> getItemsObj(String data) {
        ArrayList<MyItem> ret = new ArrayList<>();
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
                    new MyItem(
                            jsonElement.getAsJsonObject().get("id").getAsString(),
                            jsonElement.getAsJsonObject().get("quantity").getAsString(),
                            Item.getItemObj(jsonElement.getAsJsonObject().get("item").toString())
                    )
            );
        });
        return ret;
    }

    public String getId() {
        return id;
    }

    public String getQuantity() {
        return quantity;
    }

    public Item getItem() {
        return item;
    }
}
