package fr.cringebot.cringe.objects.lol;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import fr.cringebot.cringe.pokemon.objects.Pokemon;

public class Champion {
    public static List<Champion> champions;

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
    private int hp;
    @SerializedName("hpperlevel")
    @Expose
    private int hpperlevel;
    @SerializedName("mp")
    @Expose
    private int mp;
    @SerializedName("mpperlevel")
    @Expose
    private int mpperlevel;
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
    private int spellblock;
    @SerializedName("spellblockperlevel")
    @Expose
    private double spellblockperlevel;
    @SerializedName("attackrange")
    @Expose
    private int attackrange;
    @SerializedName("hpregen")
    @Expose
    private int hpregen;
    @SerializedName("hpregenperlevel")
    @Expose
    private int hpregenperlevel;
    @SerializedName("mpregen")
    @Expose
    private int mpregen;
    @SerializedName("mpregenperlevel")
    @Expose
    private int mpregenperlevel;
    @SerializedName("crit")
    @Expose
    private int crit;
    @SerializedName("critperlevel")
    @Expose
    private int critperlevel;
    @SerializedName("attackdamage")
    @Expose
    private int attackdamage;
    @SerializedName("attackdamageperlevel")
    @Expose
    private int attackdamageperlevel;
    @SerializedName("attackspeedperlevel")
    @Expose
    private double attackspeedperlevel;
    @SerializedName("attackspeed")
    @Expose
    private double attackspeed;

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getHpperlevel() {
        return hpperlevel;
    }

    public void setHpperlevel(int hpperlevel) {
        this.hpperlevel = hpperlevel;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public int getMpperlevel() {
        return mpperlevel;
    }

    public void setMpperlevel(int mpperlevel) {
        this.mpperlevel = mpperlevel;
    }

    public int getMovespeed() {
        return movespeed;
    }

    public void setMovespeed(int movespeed) {
        this.movespeed = movespeed;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public double getArmorperlevel() {
        return armorperlevel;
    }

    public void setArmorperlevel(double armorperlevel) {
        this.armorperlevel = armorperlevel;
    }

    public int getSpellblock() {
        return spellblock;
    }

    public void setSpellblock(int spellblock) {
        this.spellblock = spellblock;
    }

    public double getSpellblockperlevel() {
        return spellblockperlevel;
    }

    public void setSpellblockperlevel(double spellblockperlevel) {
        this.spellblockperlevel = spellblockperlevel;
    }

    public int getAttackrange() {
        return attackrange;
    }

    public void setAttackrange(int attackrange) {
        this.attackrange = attackrange;
    }

    public int getHpregen() {
        return hpregen;
    }

    public void setHpregen(int hpregen) {
        this.hpregen = hpregen;
    }

    public int getHpregenperlevel() {
        return hpregenperlevel;
    }

    public void setHpregenperlevel(int hpregenperlevel) {
        this.hpregenperlevel = hpregenperlevel;
    }

    public int getMpregen() {
        return mpregen;
    }

    public void setMpregen(int mpregen) {
        this.mpregen = mpregen;
    }

    public int getMpregenperlevel() {
        return mpregenperlevel;
    }

    public void setMpregenperlevel(int mpregenperlevel) {
        this.mpregenperlevel = mpregenperlevel;
    }

    public int getCrit() {
        return crit;
    }

    public void setCrit(int crit) {
        this.crit = crit;
    }

    public int getCritperlevel() {
        return critperlevel;
    }

    public void setCritperlevel(int critperlevel) {
        this.critperlevel = critperlevel;
    }

    public int getAttackdamage() {
        return attackdamage;
    }

    public void setAttackdamage(int attackdamage) {
        this.attackdamage = attackdamage;
    }

    public int getAttackdamageperlevel() {
        return attackdamageperlevel;
    }

    public void setAttackdamageperlevel(int attackdamageperlevel) {
        this.attackdamageperlevel = attackdamageperlevel;
    }

    public double getAttackspeedperlevel() {
        return attackspeedperlevel;
    }

    public void setAttackspeedperlevel(double attackspeedperlevel) {
        this.attackspeedperlevel = attackspeedperlevel;
    }

    public double getAttackspeed() {
        return attackspeed;
    }

    public void setAttackspeed(double attackspeed) {
        this.attackspeed = attackspeed;
    }

}