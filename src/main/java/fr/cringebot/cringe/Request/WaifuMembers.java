package fr.cringebot.cringe.Request;

public class WaifuMembers {
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
}