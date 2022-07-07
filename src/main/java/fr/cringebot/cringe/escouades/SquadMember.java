package fr.cringebot.cringe.escouades;

import fr.cringebot.cringe.waifus.InvWaifu;
import fr.cringebot.cringe.waifus.Waifu;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SquadMember {

	private Long points;
	private final String id;
	private HashMap<String, Integer> collections;
	private HashMap<Integer, InvWaifu> waifus;

	public SquadMember(String id) {
		this.id = id;
		this.points = 0L;
		this.collections = new HashMap<>();
	}
	//---waifu---//

	public void newWaifu(Integer id, Message msg) throws InterruptedException {
			InvWaifu.catchWaifu(msg, id);
			this.addPoint(50L);
			this.waifus.put(id, new InvWaifu(id));
	}

	public void initCollections() {
		this.collections = new HashMap<>();
	}

	public void initwaifus() {
		this.waifus = new HashMap<>();
	}

	private void getWaifu(String str, Message msg) throws InterruptedException {
		ArrayList<Waifu> waifus = Waifu.getWaifusByOrigin(str);
		waifus.removeIf(waifu -> this.waifus.containsKey(waifu.getId()));
		newWaifu(waifus.get(new Random().nextInt(waifus.size() - 1)).getId(), msg);
	}

	public void addCollection(String str, Message msg) throws InterruptedException {
		/**
		if (!this.collections.containsKey(str))
			this.collections.put(str, 1);
		else
			this.collections.put(str, this.collections.get(str) + 1);
		if (this.collections.getOrDefault(str, 0) >= 3 && !str.equals("B2K") || (str.equals("B2K") && this.collections.getOrDefault(str, 0) >= 10))
		{
			this.getWaifu(str, msg);
		}**/
		this.getWaifu(str, msg);
		Squads.save();
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

	//---squads---//
	public void addPoint(Long nb) {
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
}
