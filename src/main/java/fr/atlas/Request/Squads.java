package fr.atlas.Request;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;

import static fr.atlas.BotDiscord.debug;

public class Squads extends Request {
    private  String id;
    private  String name;
    private  Integer PointsGiven;
    private  Long PointsTotal;
    private  Color color;

    /**
     *
     * @param id
     * @param name
     * @param pointsGiven
     * @param pointsTotal
     * @param color
     */
    public Squads(String id, String name, Integer pointsGiven, Long pointsTotal, Color color) {
        this.id = id;
        this.name = name;
        this.PointsGiven = pointsGiven;
        this.PointsTotal = pointsTotal;
        this.color = color;
    }

    public static List<Squads> getObjSquads(String data) {
        ArrayList<Squads> squads = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        JsonArray array = null;
        try {
            array = gson.fromJson(data, JsonArray.class);
        } catch (Exception e) {
            data = "[" + data + "]";
            array = gson.fromJson(data, JsonArray.class);
        }
        array.forEach(jsonElement -> {
            squads.add(
                    new Squads(
                    jsonElement.getAsJsonObject().get("id").getAsString(),
                    jsonElement.getAsJsonObject().get("name").getAsString(),
                    jsonElement.getAsJsonObject().get("PointsGiven").getAsInt(),
                    jsonElement.getAsJsonObject().get("PointsTotal").getAsLong(),
                    Color.decode(String.format("%6s", jsonElement.getAsJsonObject().get("color").getAsString()).replace(' ', '0'))
                    ));
        });
        return squads;
    }



    //------------------------------------------------------------//

    public static List<User> getMembers(String id) throws IOException {
        return User.getObjMembers(Request.GetRequest("squads/members/" + id));
    }

    public String getName() {
        return name;
    }

    public Long getPointsTotal() {
        return PointsTotal;
    }

    public Integer getPointsGiven() {
        return PointsGiven;
    }

    public Color getColor() {
        return color;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Squads{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", PointsGiven=" + PointsGiven +
                ", PointsTotal=" + PointsTotal +
                ", color=" + color +
                '}';
    }

    //------------------------------------------------------------//
    public static String RemoveSquads(String id) throws IOException
    {
        return GetRequest("squads/remove/"+id);
    }

    public static String newSquads(String name, String id, Color color) throws IOException
    {
        return PostRequest("squads","name="+name+"&id="+id+"&color="+color.getRGB());
    }

    public static List<Squads> getSquads() throws IOException
    {
        return getObjSquads(GetRequest("squads"));
    }

    public static Squads getSquads(String id) throws IOException
    {

        return getObjSquads('['+GetRequest("squads/id/"+id) + ']').get(0);
    }

    public static String addManualPoint(String id, Integer number) throws IOException {
        return PostRequest("squads/id/" + id, "points=" + number);
    }
    public static String addManualPoint(String id, Double number) throws IOException {
        return PostRequest("squads/id/" + id, "points=" + number.intValue());
    }

    public static String removeManualPoint(String id, Integer number) throws IOException {
        return PostRequest("squads/id/" + id, "points=" + number * -1);
    }

    public static String updateSquad() throws IOException {
        return GetRequest("squads/update");
    }

    public static Squads getSquadByMember(String id) throws IOException {
        return getObjSquads('['+GetRequest("squads/member/"+id)+ ']').get(0);
    }
}
