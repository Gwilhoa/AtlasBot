package fr.cringebot.cringe.lol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Champion {
    public static HashMap<String, Champion> champions;

    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("blurb")
    @Expose
    private String blurb;
    @SerializedName("info")
    @Expose
    private Info info;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;
    @SerializedName("partype")
    @Expose
    private String partype;
    @SerializedName("stats")
    @Expose
    private Stats stats;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getPartype() {
        return partype;
    }

    public void setPartype(String partype) {
        this.partype = partype;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public static Champion getChampionById(Integer i)
    {
        for ( Champion c : Champion.champions.values())
        {
            if (c.key.equals(i.toString()))
                return c;
        }
        return null;
    }

    public static Champion getRandomChampion()
    {
        ArrayList<Champion> champions = new ArrayList<>(Champion.champions.values());
        return (champions.get(new Random().nextInt(champions.size())));
    }
}

class Info {

    @SerializedName("attack")
    @Expose
    private int attack;
    @SerializedName("defense")
    @Expose
    private int defense;
    @SerializedName("magic")
    @Expose
    private int magic;
    @SerializedName("difficulty")
    @Expose
    private int difficulty;

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getMagic() {
        return magic;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }


}

class Stats {

    @SerializedName("hp")
    @Expose
    private float hp;
    @SerializedName("hpperlevel")
    @Expose
    private int hpperlevel;
    @SerializedName("mp")
    @Expose
    private float mp;
    @SerializedName("mpperlevel")
    @Expose
    private float mpperlevel;
    @SerializedName("movespeed")
    @Expose
    private int movespeed;
    @SerializedName("armor")
    @Expose
    private int armor;
    @SerializedName("armorperlevel")
    @Expose
    private double armorperlevel;
    @SerializedName("spellblock")
    @Expose
    private float spellblock;
    @SerializedName("spellblockperlevel")
    @Expose
    private double spellblockperlevel;
    @SerializedName("attackrange")
    @Expose
    private int attackrange;
    @SerializedName("hpregen")
    @Expose
    private float hpregen;
    @SerializedName("hpregenperlevel")
    @Expose
    private float hpregenperlevel;
    @SerializedName("mpregen")
    @Expose
    private float mpregen;
    @SerializedName("mpregenperlevel")
    @Expose
    private float mpregenperlevel;
    @SerializedName("crit")
    @Expose
    private int crit;
    @SerializedName("critperlevel")
    @Expose
    private int critperlevel;
    @SerializedName("attackdamage")
    @Expose
    private float attackdamage;
    @SerializedName("attackdamageperlevel")
    @Expose
    private float attackdamageperlevel;
    @SerializedName("attackspeedperlevel")
    @Expose
    private double attackspeedperlevel;
    @SerializedName("attackspeed")
    @Expose
    private double attackspeed;

    public float getHp() {
        return hp;
    }

    public int getHpperlevel() {
        return hpperlevel;
    }

    public float getMp() {
        return mp;
    }

    public float getMpperlevel() {
        return mpperlevel;
    }

    public int getMovespeed() {
        return movespeed;
    }

    public int getArmor() {
        return armor;
    }

    public double getArmorperlevel() {
        return armorperlevel;
    }

    public double getSpellblock() {
        return spellblock;
    }

    public double getSpellblockperlevel() {
        return spellblockperlevel;
    }

    public int getAttackrange() {
        return attackrange;
    }

    public float getHpregen() {
        return hpregen;
    }

    public float getHpregenperlevel() {
        return hpregenperlevel;
    }

    public float getMpregen() {
        return mpregen;
    }

    public float getMpregenperlevel() {
        return mpregenperlevel;
    }

    public int getCrit() {
        return crit;
    }

    public int getCritperlevel() {
        return critperlevel;
    }

    public float getAttackdamage() {
        return attackdamage;
    }

    public float getAttackdamageperlevel() {
        return attackdamageperlevel;
    }

    public double getAttackspeedperlevel() {
        return attackspeedperlevel;
    }

    public double getAttackspeed() {
        return attackspeed;
    }


}