package fr.atlas.Request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import fr.atlas.BotDiscord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static fr.atlas.BotDiscord.debug;
import static fr.atlas.BotDiscord.setError;


public class User extends Request {

    private final String id;
    private final String name;
    private final Integer points;
    private  Integer coins;
    private final Squads squad;
    private final String title;

    private Integer memevote;

    private Integer bestmeme;

    private long waifutime;

    User(String id, String name, Integer points, Integer coins, String title, Squads squad, Integer memevote, Integer bestmeme, long waifutime) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.coins = coins;
        this.squad = squad;
        this.title = title;
        this.memevote = memevote;
        this.bestmeme = bestmeme;
        this.waifutime = waifutime;
    }

    public static List<User> getObjMembers(String data) {
        if (data == null) return null;
        ArrayList<User> users = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        JsonArray array;
        try {
            array = gson.fromJson(data, JsonArray.class);
        } catch (Exception e) {
            data = "["+data+"]";
            array = gson.fromJson(data, JsonArray.class);
        }
        array.forEach(jsonElement -> {
            Squads sq = null;
            if (jsonElement.getAsJsonObject().get("squad") != null)
                sq = Squads.getObjSquads(jsonElement.getAsJsonObject().get("squad").toString()).get(0);
            users.add(
                    new User(
                            jsonElement.getAsJsonObject().get("id").getAsString(),
                            jsonElement.getAsJsonObject().get("name").getAsString(),
                            jsonElement.getAsJsonObject().get("points").getAsInt(),
                            jsonElement.getAsJsonObject().get("coins").getAsInt(),
                            jsonElement.getAsJsonObject().get("title").getAsString(),
                            sq,
                            jsonElement.getAsJsonObject().get("memevotes").getAsInt(),
                            jsonElement.getAsJsonObject().get("bestmeme").getAsInt(),
                            jsonElement.getAsJsonObject().get("waifutime").getAsLong()
                    )
            );
        });
        return users;
    }

    public static WaifuMembers catchwaifu(net.dv8tion.jda.api.entities.Member member) throws IOException {
        WaifuMembers waifu = null;
        String data = Request.PostRequest("members/waifus/" + member.getId(), "");
        try {
            System.out.println("[WAIFU] new catch by "+member.getUser().getName()+" : " + data);
            waifu = WaifuMembers.getObjWaifuMembers("[" + data + "]").get(0);
        } catch (Exception e) {
            throw new IOException(data);
        }
        return waifu;
    }

    //------------------------------------------------------------//

    public net.dv8tion.jda.api.entities.Member getMember(Guild guild) {
        return guild.getMemberById(this.id);
    }

    public String getName() {
        return name;
    }

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

    public static User getMember(String id) throws IOException
    {

        List<User> mbrs = getObjMembers(Request.GetRequest("members/id/"+id));
        if (mbrs == null) return null;
        return mbrs.get(0);
    }

    public static User getMember(net.dv8tion.jda.api.entities.Member mem) throws IOException
    {
        return getMember(mem.getId());
    }

    public void setName(String name) throws IOException
    {
        PostRequest("members/modifyname/"+this.id,"name="+name);
    }

    public static String getSquadsMembers(String id) throws IOException
    {
        return GetRequest("members/squad/"+id);
    }

    public static String setSquad(net.dv8tion.jda.api.entities.Member member, String id) throws IOException
    {
        return PostRequest("members/squads/"+member.getId(),"squadid="+id);
    }

    public static User addMemeVote(String id) throws IOException
    {
        return getObjMembers(PatchRequest("members/memesvotes/"+id,"")).get(0);
    }

    public static User addBestMemes(String id) throws IOException
    {
        return getObjMembers(PatchRequest("members/memes/"+id,"")).get(0);
    }

    public static String addBestMemes(net.dv8tion.jda.api.entities.Member mem) throws IOException
    {
        User user = User.addBestMemes(mem.getId());
        int nb = user.getBestmeme();
        if ( nb >= 100)
            addAchievement(user.id, "8");
        else if (nb >= 50)
            addAchievement(user.id, "7");
        else if (nb >= 25)
            addAchievement(user.id, "6");
        else if (nb >= 10)
            addAchievement(user.id, "5");
        else if (nb >= 1)
            addAchievement(user.id, "4");

        return "ok";
    }

    public static List<User> getMembers() throws IOException
    {
        return getObjMembers(GetRequest("members"));
    }

    public static String newMembers(String id, String name, String squad) throws IOException
    {
        return PostRequest("members","id="+id+"&name="+name+"&squadid="+squad);
    }
    public static String  newMembers(net.dv8tion.jda.api.entities.Member mem, String squad) throws IOException
    {
        return PostRequest("members","id="+mem.getId()+"&name="+mem.getUser().getName()+"&squadid="+squad);
    }

    public static String addCoins(String id, Integer number) throws IOException
    {
        return PostRequest("members/coins/"+id,"coins="+number);
    }


    public static void addPoints(String id, Integer number) throws IOException
    {
        User user = getObjMembers(PatchRequest("members/points/"+id,"{ \"points\" : \""+number+"\" }")).get(0);
        Integer Points = user.getPoints();
        if (Points >= 1000000) {
            User.addAchievement(user.id ,"13");
        }
        else if (Points >= 500000) {
            User.addAchievement(user.id ,"12");
        }
        else if (Points >= 100000) {
            User.addAchievement(user.id ,"11");
        }
        else if (Points >= 50000) {
            User.addAchievement(user.id ,"10");
        }
    }

    public void resetPoints() throws IOException
    {
        addPoints(this.id, -this.points);
    }

    public static void addAchievement(String id, String achievementId) throws IOException
    {
        try {
            PostRequest("members/achievements/" + id, "{\"achievement_id\" : \"" + achievementId + "\"}");
        } catch (IOException e) {
            setError(e);
        }
    }

    public List<Achievement> getAchievements() throws IOException
    {
        return Achievement.getObjAchievement(GetRequest("members/achievements/"+
                this.getId()));
    }

    public static List<Achievement> getAchievementsById(String id) throws IOException
    {
        return Achievement.getObjAchievement(GetRequest("members/achievements/"+ id));
    }

    public static void revokeAchievement(net.dv8tion.jda.api.entities.Member mem, String achievement) throws IOException
    {
        PostRequest("members/achievements/revoke/"+mem.getId(),"achievement="+achievement);
    }

    public static void addTitle(net.dv8tion.jda.api.entities.Member mem, String title) throws IOException
    {
        if (title == null)
            return;
        PostRequest("members/title/add/"+mem.getId(),"title="+title);
    }

    public static boolean setTitle(net.dv8tion.jda.api.entities.Member mem, String title) throws IOException
    {
        if (title == null)
            return false;
        if (!getTitles(mem).contains(title))
            return false;
        PostRequest("members/title/set/"+mem.getId(),"title="+title);
        return true;
    }

    public static List<String> getTitles(net.dv8tion.jda.api.entities.Member mem) throws IOException
    {
        String data = GetRequest("members/title/"+mem.getId());
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        JsonArray array = null;
        try {
            array = gson.fromJson(data, JsonArray.class);
        } catch (Exception e) {
            data = "["+data+"]";
            array = gson.fromJson(data, JsonArray.class);
        }
        ArrayList<String> titles = new ArrayList<>();
        array.forEach(jsonElement -> titles.add(jsonElement.getAsJsonObject().get("name").getAsString()));
        titles.remove("null");
        return titles;
    }

    public static List<WaifuMembers> getWaifuMembers(String id) throws IOException {

        String data = GetRequest("members/waifus/"+id);
        List<WaifuMembers> waifus = WaifuMembers.getObjWaifuMembers(data);
        return waifus;
    }



    public String getTitle() {
        if (title.equals("0"))
            return null;
        return title;
    }

    public Integer getMemevote() {
        return memevote;
    }

    public Integer getBestmeme() {
        return bestmeme;
    }

    public long getWaifutime() {
        return waifutime;
    }

    public boolean buy(String id, int quantity) throws IOException {

        String ret = PostRequest("members/inventory/"+this.id,"{ \"item_id\" : \""+id+"\", \"quantity\" : \""+quantity+"\" }");
        return !ret.startsWith("{\"message_code");
    }

    public static ArrayList<MyItem> getInventory(String id) throws IOException {
        return MyItem.getItemsObj(GetRequest("members/inventory/"+id));
    }
}
