package fr.cringebot.cringe.waifus;

import com.google.gson.reflect.TypeToken;
import fr.cringebot.cringe.objects.StringExtenders;
import net.dv8tion.jda.api.entities.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static fr.cringebot.cringe.event.BotListener.gson;

public class Waifu {
	private static final String file = "save/waifus.json";
	private static final TypeToken<HashMap<Integer, Waifu>> token = new TypeToken<HashMap<Integer, Waifu>>() {
	};
	private static HashMap<Integer, Waifu> waifuList = new HashMap<>();
	private final static HashMap<String, Long> timer = new HashMap<>();
	private final String profile;
	private String name;
	private String description;
	private String owner;
	private final String type;
	private Integer id;
	private String origin;
	private ArrayList<String> nickname;

	public String getType() {
		return type;
	}

	public void setOrigin(String name) {
		this.origin = name;
		save();
	}

	public enum Type
	{
		JEUX_VIDEO,
		FILM,
		ANIME,
		B2K,
		SERIE,
		IRL,
		AUTRES
	}

	public Waifu(Message.Attachment f, String name, String description, String type, String origin)
	{
		int	i = 0;
		this.id = -1;
		this.description = description;
		this.name = name;
		this.owner = null;
		this.type = type;
		this.nickname = null;
		while (i < waifuList.size())
		{
			if (waifuList.get(i) == null) {
				this.id = i;
				break;
			}
			i++;
		}
		if (this.id == -1)
			this.id = waifuList.size();
		this.profile = "save/waifu/waifu_"+this.id +".png";
		this.origin = origin;
		f.downloadToFile(this.profile);
		waifuList.put(this.id, this);
		save();
	}

	public void setFile(Message.Attachment f){
		f.downloadToFile(this.profile);
	}

	public void delwaifu()
	{
		waifuList.remove(this.id);
		save();
	}

	public static void trade(Integer w1, Integer w2) {
		Waifu w01 = getWaifuById(w1);
		Waifu w02 = getWaifuById(w2);
		String temp = w02.getOwner();
		w02.setOwner(w01.getOwner());
		w01.setOwner(temp);
		save();
	}

	public static ArrayList<Waifu> getAllWaifu(){
		return new ArrayList<>(waifuList.values());
	}

	public static ArrayList<Waifu> getAvailableWaifu() {
		ArrayList<Waifu> waifus = getAllWaifu();
		waifus.removeIf(w -> w.getOwner() != null);
		return waifus;
	}
	public static ArrayList<Waifu> getWaifubyName(String name){
		ArrayList<Waifu> list = new ArrayList<>();
		if (!waifuList.isEmpty())
		{
			for (Waifu waifu : waifuList.values())
			{
				if (StringExtenders.startWithIgnoreCase(waifu.getName(),name))
					list.add(waifu);
				else
					System.out.println(waifu.id);
			}
		}
		if (list.isEmpty())
			return null;
		return list;
	}

	public void setDescription(String description) {
		this.description = description;
		save();
	}

	public void setName(String name) {
		this.name = name;
		save();
	}

	public static Waifu getWaifuById(Integer id)
	{
		for (Waifu waifu : waifuList.values())
		{
			if (waifu.getId().equals(id))
				return waifu;
		}
		return null;
	}


	public String getProfile() {
		return profile;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
		save();
	}

	public Integer getId() {
		return id;
	}

	public String getOrigin() {
		return origin;
	}



	private static void save() {
		if (!new File(file).exists()) {
			try {
				new File(file).createNewFile();
			} catch (IOException e) {
				return;
			}
		}
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			gson.toJson(waifuList, token.getType(), bw);
			bw.flush();
			bw.close();
		} catch (IOException e) {
		}
	}

	public static void load() {
		if (new File(file).exists()) {
			try {
				waifuList = gson.fromJson(new BufferedReader(new InputStreamReader(new FileInputStream(file))), token.getType());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				new File(file).createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (waifuList == null)
			waifuList = new HashMap<>();
	}

	public static void setTime(String id)
	{
		timer.put(id, System.currentTimeMillis());
	}
	public static Long timeleft(String id){
		if (timer.get(id) == null)
			return (1L);
		return ( System.currentTimeMillis() - (timer.get(id) + 21600000L));
	}
}
