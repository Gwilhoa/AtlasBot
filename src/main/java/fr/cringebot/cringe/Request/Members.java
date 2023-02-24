package fr.cringebot.cringe.Request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import fr.cringebot.BotDiscord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class Members extends Request {

    private  String id;
    private  String name;
    private  Integer points;
    private  Integer coins;
    private final Squads squad;
    private  String title;

    Members (String id, String name, Integer points, Integer coins, String title, Squads squad) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.coins = coins;
        this.squad = squad;
        this.title = title;
    }

    Members (String id, String name, Integer points, Integer coins, String title) throws IOException {
        this.id = id;
        this.name = name;
        this.points = points;
        this.coins = coins;
        this.title = title;
        System.out.println(id);
        this.squad = Squads.getSquadByMember(id);
    }

    public static List<Members> getObjMembers(String data, Squads sq) {
        if (data == null) return null;
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
        if (sq == null) {
            array.forEach(jsonElement -> {
                try {
                    members.add(
                            new Members(
                                    jsonElement.getAsJsonObject().get("id").getAsString(),
                                    jsonElement.getAsJsonObject().get("name").getAsString(),
                                    jsonElement.getAsJsonObject().get("points").getAsInt(),
                                    jsonElement.getAsJsonObject().get("coins").getAsInt(),
                                    jsonElement.getAsJsonObject().get("title").getAsString()
                            ));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            array.forEach(jsonElement -> {
                members.add(
                        new Members(
                                jsonElement.getAsJsonObject().get("id").getAsString(),
                                jsonElement.getAsJsonObject().get("name").getAsString(),
                                jsonElement.getAsJsonObject().get("points").getAsInt(),
                                jsonElement.getAsJsonObject().get("coins").getAsInt(),
                                jsonElement.getAsJsonObject().get("title").getAsString(),
                                sq
                        ));
            });
        }
        return members;
    }

    //------------------------------------------------------------//

    public Member getMember(Guild guild) {
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

    public static Members getMember(String id) throws IOException
    {

        List<Members> mbrs = getObjMembers(Request.GetRequest("members/id/"+id), null);
        if (mbrs == null) return null;
        return mbrs.get(0);
    }

    public static Members getMember(Member mem) throws IOException
    {
        return getMember(mem.getId());
    }

    public static String getSquadsMembers(String id) throws IOException
    {
        return GetRequest("members/squad/"+id);
    }

    public static String setSquad(Member member, String id) throws IOException
    {
        return PostRequest("members/squads/"+member.getId(),"squadid="+id);
    }

    public static String getMemeVote(String id) throws IOException
    {
        return GetRequest("members/memevote/"+id);
    }

    public static String addMemeVote(String id) throws IOException
    {
        return PostRequest("members/memevote/"+id,"add=1");
    }

    public static Integer getBestMemes(String id) throws IOException
    {
        return Integer.parseInt(GetRequest("members/bestmeme/"+id));
    }

    public static String addBestMemes(String id) throws IOException
    {
        return PostRequest("members/bestmeme/"+id,"bestmeme=1");
    }

    public static String addBestMemes(Member mem) throws IOException
    {
        int nb = getBestMemes(mem.getId()) + 1;
        if ( nb >= 100)
            addAchievement(mem, "8", mem.getGuild().getTextChannelById(BotDiscord.AnnounceSalonId));
        else if (nb >= 50)
            addAchievement(mem, "7", mem.getGuild().getTextChannelById(BotDiscord.AnnounceSalonId));
        else if (nb >= 25)
            addAchievement(mem, "6", mem.getGuild().getTextChannelById(BotDiscord.AnnounceSalonId));
        else if (nb >= 10)
            addAchievement(mem, "5", mem.getGuild().getTextChannelById(BotDiscord.AnnounceSalonId));
        else if (nb >= 1)
            addAchievement(mem, "4", mem.getGuild().getTextChannelById(BotDiscord.AnnounceSalonId));
        addBestMemes(mem.getId());
        return "ok";
    }

    public static List<Members> getMembers() throws IOException
    {
        return getObjMembers(GetRequest("members"), null);
    }

    public static String newMembers(String id, String name, String squad) throws IOException
    {
        return PostRequest("members","id="+id+"&name="+name+"&squadid="+squad);
    }
    public static String newMembers(Member mem, String squad) throws IOException
    {
        return PostRequest("members","id="+mem.getId()+"&name="+mem.getUser().getName()+"&squadid="+squad);
    }

    public static String addCoins(String id, Integer number) throws IOException
    {
        return PostRequest("members/coins/"+id,"coins="+number);
    }

    public static void addPoints(String id, Integer number, Guild guild) throws IOException {
        Members mem = getMember(id);
        addPoints(id, number);
        if (mem == null) {
            return;
        }
        Integer Points = getMember(id).getPoints();
        if (Points >= 1000000) {
            mem.addAchievement("13", guild.getTextChannelById(BotDiscord.AnnounceSalonId), guild);
        }
        else if (Points >= 500000) {
            mem.addAchievement("12", guild.getTextChannelById(BotDiscord.AnnounceSalonId), guild);
        }

        else if (Points >= 100000) {
            mem.addAchievement("11", guild.getTextChannelById(BotDiscord.AnnounceSalonId), guild);
        }
        else if (Points >= 50000) {
            mem.addAchievement("10", guild.getTextChannelById(BotDiscord.AnnounceSalonId), guild);
        }


    }

    private static void addPoints(String id, Integer number) throws IOException
    {
        System.out.println(PostRequest("members/points/"+id,"points="+number));
    }

    public void addAchievement(String id, MessageChannel announce, Guild guild) throws IOException
    {
        Member member = guild.getMemberById(this.id);
        addAchievement(member, id, announce);
    }

    public void resetPoints() throws IOException
    {
        addPoints(this.id, -this.points);
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
                            //announcechannel.sendMessage("L'utilisateur "+mem.getAsMention()+" a déjà l'achievement "+ach.getName()).queue();
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
        Achievement ach = Achievement.getAchievement(achievement);
        Members.addTitle(mem, ach.getTitle());
        if (announcechannel != null)
        {
            Members.addPoints(mem.getId(), ach.getPoints(), mem.getGuild());
            Members.addCoins(mem.getId(), ach.getCoins());
            announcechannel.sendMessage(mem.getAsMention()+" a débloqué l'achievement **"+ach.getName()+"** !").setEmbeds(new EmbedBuilder()
                    .setTitle(ach.getName())
                    .setDescription(ach.getDescription())
                    .setThumbnail(ach.getImage())
                    .setColor(Color.GREEN)
                    .build()).queue();
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

    public static void addTitle(Member mem, String title) throws IOException
    {
        if (title == null)
            return;
        PostRequest("members/title/add/"+mem.getId(),"title="+title);
    }

    public static boolean setTitle(Member mem, String title) throws IOException
    {
        if (title == null)
            return false;
        if (!getTitles(mem).contains(title))
            return false;
        PostRequest("members/title/set/"+mem.getId(),"title="+title);
        return true;
    }

    public static List<String> getTitles(Member mem) throws IOException
    {
        String data = GetRequest("members/title/get/"+mem.getId());
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

    public String getWaifuMembers() throws IOException {
        String data = GetRequest("members/waifus/"+this.getId());
        return data;
    }

    public String getTitle() {
        if (title.equals("0"))
            return null;
        return title;
    }
}
