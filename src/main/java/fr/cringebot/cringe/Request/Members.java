package fr.cringebot.cringe.Request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class Members extends Squads {

    private  String id;
    private  String name;
    private  Integer points;
    private  Integer coins;
    private  Squads squad;


    Members (String id, String name, Integer points, Integer coins, Squads squad) {
        super(squad.getId(), squad.getName(), squad.getPointsGiven(), squad.getPointsTotal(), squad.getColor());
        this.id = id;
        this.name = name;
        this.points = points;
        this.coins = coins;
        this.squad = squad;
    }

    public static List<Members> getObjMembers(String data) {

        ArrayList<Members> members = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        JsonArray array = null;
        try {
            array = gson.fromJson(data, JsonArray.class);
        } catch (Exception e) {
            data = "["+data+"]";
            array = gson.fromJson(data, JsonArray.class);
        }
        array.forEach(jsonElement -> {
            members.add(
                    new Members(
                            jsonElement.getAsJsonObject().get("id").getAsString(),
                            jsonElement.getAsJsonObject().get("name").getAsString(),
                            jsonElement.getAsJsonObject().get("points").getAsInt(),
                            jsonElement.getAsJsonObject().get("coins").getAsInt(),
                            new Squads(
                                    jsonElement.getAsJsonObject().get("squad").getAsJsonObject().get("id").getAsString(),
                                    jsonElement.getAsJsonObject().get("squad").getAsJsonObject().get("name").getAsString(),
                                    jsonElement.getAsJsonObject().get("squad").getAsJsonObject().get("PointsGiven").getAsInt(),
                                    jsonElement.getAsJsonObject().get("squad").getAsJsonObject().get("PointsTotal").getAsInt(),
                                    jsonElement.getAsJsonObject().get("squad").getAsJsonObject().get("color").getAsInt()
                            )));
        });
        return members;
    }

    //------------------------------------------------------------//

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    public Integer getPoints() {
        return points;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public Squads getSquad() {
        return squad;
    }

    //------------------------------------------------------------//

    public static Members getMember(String id) throws IOException
    {
        return getObjMembers(Request.GetRequest("members/id/"+id)).get(0);
    }

    public static Members getMember(Member mem) throws IOException
    {
        return getMember(mem.getId());
    }

    public static String getSquadsMembers(String id) throws IOException
    {
        return GetRequest("members/squad/"+id);
    }

    public static boolean setSquad(Member member, String id) throws IOException
    {
        return PostRequest("members/squads/"+member.getId(),"squadid="+id);
    }

    public static String getMembers() throws IOException
    {
        return GetRequest("members");
    }

    public static boolean newMembers(String id, String name, String squad) throws IOException
    {
        return PostRequest("members","id="+id+"&name="+name+"&squadid="+squad);
    }
    public static boolean newMembers(Member mem, String squad) throws IOException
    {
        return PostRequest("members","id="+mem.getId()+"&name="+mem.getUser().getName()+"&squadid="+squad);
    }

    public static boolean addCoins(String id, Integer number) throws IOException
    {
        return PostRequest("members/coins/"+id,"coins="+number);
    }

    public static void addPoints(String id, Integer number) throws IOException
    {
        PostRequest("members/points/"+id,"points="+number);
    }

    public static void addAchievement(Member mem, String achievement, MessageChannel announcechannel) throws IOException
    {
        AtomicBoolean b = new AtomicBoolean(false);
        getAchievements(mem).forEach(
                ach -> {
                    if(ach.getId().equals(achievement))
                    {
                        if (announcechannel != null)
                        {
                            announcechannel.sendMessage("L'utilisateur "+mem.getAsMention()+" a déjà l'achievement "+ach.getName()).queue();
                            b.set(true);
                        }
                    }
                }
        );
        if (b.get())
            return;
        try {
            PostRequest("members/achievements/" + mem.getId(), "achievement=" + achievement);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (announcechannel != null)
        {
            Achievement ach = Achievement.getAchievement(achievement);
            Members.addPoints(mem.getId(), ach.getPoints());
            Members.addCoins(mem.getId(), ach.getCoins());
            announcechannel.sendMessage(mem.getAsMention()+" a débloqué l'achievement **"+ach.getName()+"** !").queue();
        }
    }

    public static List<Achievement> getAchievements(Member mem) throws IOException
    {
        return Achievement.getObjAchievement(GetRequest("members/achievements/"+mem.getId()));
    }

    public static void revokeAchievement(Member mem, String achievement) throws IOException
    {
        PostRequest("members/achievements/revoke/"+mem.getId(),"achievement="+achievement);
    }
}
