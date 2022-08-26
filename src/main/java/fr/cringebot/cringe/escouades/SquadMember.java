package fr.cringebot.cringe.escouades;

import fr.cringebot.cringe.objects.Item;
import fr.cringebot.cringe.waifus.InvWaifu;
import fr.cringebot.cringe.waifus.Waifu;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SquadMember {

	private ArrayList<String> CompleteCollection;
	private Long points;
	private final String id;
	private Long timer;
	private Long searchingtimer;
	private HashMap<String, Integer> collections;
	private HashMap<String, Integer> inventory;
	private HashMap<Integer, InvWaifu> waifus;
	private Long coins;
	private Integer bank;

	public SquadMember(String id) {
		this.id = id;
		this.points = 0L;
		this.collections = new HashMap<>();
		this.waifus = new HashMap<>();
		this.coins = 0L;
		this.bank = 0;
	}

	//---coins---//
	public void addBank(Integer points)
	{
		int lim = 1000;
		if (this.bank == null)
			bank = 0;
		this.bank = this.bank + points;
		if (Squads.getSortedSquads().get(0).equals(Squads.getSquadByMember(this.id)))
			lim = 833;
		if (this.bank > lim) {
			addCoins(1L);
			this.bank = this.bank - lim;
		}
	}

	public void addItem(String item)
	{
		addItem(item, 1);
	}
	public void addItem(String item, int amount){
		if (inventory == null)
			inventory = new HashMap<>();
		inventory.putIfAbsent(item, 0);
		inventory.put(item, inventory.getOrDefault(item, 0) + amount);
		Squads.save();
	}

	public Integer getAmountItem(String item)
	{
		if (inventory == null)
			inventory = new HashMap<>();
		inventory.putIfAbsent(item, 0);
		return inventory.getOrDefault(item, 0);
	}

	public Boolean removeItem(String item) {
		return removeItem(item, 1);
	}

	public Boolean removeItem(String item, int i) {
		if (inventory == null)
			inventory = new HashMap<>();
		inventory.putIfAbsent(item, 0);
		if (inventory.getOrDefault(item, 0) > 0) {
			inventory.put(item, inventory.get(item) - i);
			Squads.save();
			return true;
		}
		return false;
	}

	public void addCoins(Long coins) {
		if (this.coins == null)
			this.coins = 0L;
		this.coins = coins + this.coins;
		Squads.save();
	}

	public boolean CompleteCollection(String collection) {
		if (CompleteCollection.contains(collection))
			return false;
		CompleteCollection.add(collection);
		return true;
	}

	public boolean removeCoins(Integer coins){
		return removeCoins(coins.longValue());
	}
	public boolean removeCoins(Long coins) {
		if (this.coins == null)
			this.coins = 0L;
		if (this.coins - coins < 0)
			return false;
		this.coins = this.coins - coins;
		Squads.save();
		return true;
	}

	public Long getCoins() {
		if (coins == null)
			coins = 0L;
		return coins;
	}

	//---waifu---//

	public EmbedBuilder newWaifu(Integer id, String memId, Guild g) throws InterruptedException {
		EmbedBuilder eb = InvWaifu.catchWaifu(id, memId, g);
		if (Waifu.getWaifuById(id).isLegendary()) {
			Waifu.getWaifuById(id).setIstaken(true);
			Waifu.save();
		}
		this.addPoint(150L);
		this.waifus.put(id, new InvWaifu(id));
		if (isCompleteCollection(Waifu.getWaifuById(id).getOrigin()) && isCompleteCollection(Waifu.getWaifuById(id).getOrigin())) {
			Integer pts = Waifu.getWaifusByOrigin(Waifu.getWaifuById(id).getOrigin()).size();
			pts = pts*100/2;
			eb.appendDescription("\nFélicitation tu as finis la collection "+pts+" points pour "+ Squads.getSquadByMember(memId).getSquadRole(g).getAsMention());

			Squads.getstats(memId).addPoint(pts.longValue());
		}
		Squads.save();
		return eb;
	}

	public void addInvWaifu(InvWaifu ivW)
	{
		this.waifus.put(ivW.getId(), ivW);
		Squads.save();
	}

	public InvWaifu popInvWaifu (Integer id)
	{
		InvWaifu ivW = this.waifus.get(id);
		if (ivW != null)
			this.waifus.remove(id);
		Squads.save();
		return ivW;
	}

	public boolean isCompleteCollection(String collection)
	{
		return Waifu.getWaifusByOrigin(collection).size() <= getWaifubyOrigin(collection).size();
	}

	public void initCollections() {
		this.collections = new HashMap<>();
	}

	public void initwaifus() {
		this.waifus = new HashMap<>();
	}

	public boolean removeWaifu(Integer i)
	{
		if (this.waifus.get(i) != null) {
			this.waifus.remove(i);
			return true;
		}
		return false;
	}

	public ArrayList<Waifu> getWaifubyOrigin(String origin)
	{
		ArrayList<Waifu> ret = new ArrayList<>();
		for (InvWaifu iw : this.getWaifus().values())
			if (iw.getWaifu().getOrigin().equalsIgnoreCase(origin))
				ret.add(iw.getWaifu());
			return ret;
	}

	public EmbedBuilder getWaifu(String str, String memId, Guild g) throws InterruptedException {
		ArrayList<Waifu> waifus;
		if (str == null)
			waifus = Waifu.getAllWaifu();
		else
			waifus = Waifu.getWaifusByOrigin(str);
		waifus.removeIf(waifu -> this.waifus.containsKey(waifu.getId()) || waifu.getOrigin().equals("B2K"));
		return newWaifu(waifus.get(new Random().nextInt(waifus.size() - 1)).getId(), memId, g);
	}

	public EmbedBuilder addCollection(String str, Member mem) throws InterruptedException {
		if (this.collections.getOrDefault(str, 0) == 2) {
			this.collections.put(str, 0);
			return this.getWaifu(str, mem.getId(), mem.getGuild());
		}
		else
			this.collections.put(str, this.collections.getOrDefault(str, 0) + 1);
		Squads.save();
		return null;
	}

	public EmbedBuilder addCollection(String str, Message msg) throws InterruptedException {
		if (this.collections.getOrDefault(str, 0) == 2) {
			this.collections.put(str, 0);
			return this.getWaifu(str, msg.getMember().getId(), msg.getGuild());
		}
		else
			this.collections.put(str, this.collections.getOrDefault(str, 0) + 1);
		Squads.save();
		return null;
	}

	public HashMap<String, Integer> getInventory() {
		return inventory;
	}

	public Integer getCollection(String str)
	{
		return this.collections.getOrDefault(str, 0);
	}
	public HashMap<String, Integer> getCollection()
	{
		return this.collections;
	}

	public HashMap<Integer, InvWaifu> getWaifus() {
		return waifus;
	}

	public void setWaifus(HashMap<Integer, InvWaifu> iv) {
		waifus = iv;
	}


	//---squads---//
	public void addPoint(Long nb) {
		addBank(nb.intValue());
		this.points = this.points + nb;
	}

	public void resetPoint()
	{
		this.points = 0L;
	}
	public void removepoint(Long nb) {
		this.points = this.points - nb;
	}

	public Long getPoints() {
		return points;
	}

	public String getId() {
		return id;
	}

	//---timer---//

	public void setSearchingtimer() {
		searchingtimer = System.currentTimeMillis();
		Squads.save();
	}

	public Long SearchingTimeleft() {
		if (searchingtimer == null)
			searchingtimer = 0L;
		return ((System.currentTimeMillis() - (searchingtimer + 10800000L)) * -1);
	}

	public void setTime() {
		timer = System.currentTimeMillis();
		removeTime(1800000L * (this.getAmountItem(Item.Items.HE.getStr())).longValue());
		Squads.save();
	}

	public boolean removeTime(Long time) {
		if (this.timeleft() > 0) {
			timer = timer - time;
			Squads.save();
			return true;
		}
		return false;
	}

	public Long timeleft() {
		if (timer == null)
			timer = 0L;
		return ((System.currentTimeMillis() - (timer + 25200000L)) * -1);
	}
}
