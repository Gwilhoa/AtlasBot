package fr.cringebot.cringe.Request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WaifuMembers {
    private final Color Common = Color.GRAY;
    private final Color Rare = Color.CYAN;
    private final Color Epic = Color.MAGENTA;
    private final Color Legendary = Color.ORANGE;
    private final Waifu waifu;
    private final String id;
    private final int level;

    private final int xp;
    private final int rarity;




    public WaifuMembers(String id, int level, int xp, int rarity, Waifu waifu) {
        this.id = id;
        this.level = level;
        this.xp = xp;
        this.rarity = rarity;
        this.waifu = waifu;
    }

    public static List<WaifuMembers> getObjWaifuMembers(String data) {
        ArrayList<WaifuMembers> waifuMembers = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        JsonArray array = null;
        array = gson.fromJson(data, JsonArray.class);
        builder.create().fromJson(data, JsonArray.class).forEach( jsonElement -> {
            Waifu waifu = Waifu.getWaifuObj("["+ jsonElement.getAsJsonObject().get("waifu").getAsJsonObject().toString() + "]");

            waifuMembers.add(new WaifuMembers(
                    jsonElement.getAsJsonObject().get("id").getAsString(),
                    jsonElement.getAsJsonObject().get("level").getAsInt(),
                    jsonElement.getAsJsonObject().get("exp").getAsInt(),
                    jsonElement.getAsJsonObject().get("rarety").getAsInt(),
                    waifu
            ));
        });
        return waifuMembers;
    }

    public EmbedBuilder getEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(this.waifu.getName() + " de " + this.waifu.getOrigin());
        eb.setDescription(this.waifu.getDescription());
        eb.setFooter("Level: " + this.level + "\n" +
                "XP: " + this.xp + "\n" +
                "Rareté: " + this.getRarity());
        eb.setThumbnail(this.waifu.getImageurl());
        eb.setColor(this.getColor());
        return eb;
    }

    public Color getColor() {
        switch (this.rarity) {
            case 1:
                return Rare;
            case 2:
                return Epic;
            case 3:
                return Legendary;
            default:
                return Common;
        }
    }

    public Waifu getWaifu() {
        return waifu;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }


    public String getRarity() {
        switch (rarity) {
            case 1:
                return "Rare";
            case 2:
                return "Epic";
            case 3:
                return "Legendary";
            default:
                return "Unknown";
        }
    }

    @Override
    public String toString() {
        return "WaifuMembers{" +
                "waifu=" + waifu +
                ", id='" + id + '\'' +
                ", level=" + level +
                ", xp=" + xp +
                ", rarity=" + getRarity() +
                '}';
    }
}