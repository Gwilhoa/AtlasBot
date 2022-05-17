package fr.cringebot.cringe.waifus;

import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static fr.cringebot.cringe.event.BotListener.gson;

public class waifu {
	private static final String file = "save/waifus.json";
	private static final TypeToken<HashMap<Integer, waifu>> token = new TypeToken<HashMap<Integer, waifu>>() {
	};
	private static HashMap<Integer, waifu> waifuList = new HashMap<>();
	private final String profile;
	private String name;
	private String description;
	private String owner;
	private String type;
	private Integer id;
	private String origin;

	public String getType() {
		return type;
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

	public waifu(Message.Attachment f, String name, String description, String type, String origin)
	{
		int	i = 0;
		this.id = -1;
		this.description = description;
		this.name = name;
		this.owner = null;
		this.type = type;
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

	public void delwaifu()
	{
		waifuList.remove(this.id);
	}

	public static ArrayList<waifu> getAllWaifu(){
		return new ArrayList<>(waifuList.values());
	}

	public static ArrayList<waifu> getWaifubyName(String name){
		ArrayList<waifu> list = new ArrayList<>();
		if (!waifuList.isEmpty())
		{
			for (waifu waifu : waifuList.values())
			{
				if (waifu.getName().equalsIgnoreCase(name))
					list.add(waifu);
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

	public static waifu getWaifuById(Integer id)
	{
		for (waifu waifu : waifuList.values())
		{
			if (waifu.getId().equals(id))
				return waifu;
		}
		return null;
	}

	public EmbedBuilder EmbedWaifu(Guild guild) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setImage("attachment://" + this.profile.split("/")[this.profile.split("/").length - 1]);
		eb.setDescription("nom : " + this.name +" de "+ this.origin +"\nid : "+this.id+"\ndescription :\n"+this.description);
		if (this.owner == null)
			eb.setFooter("disponible");
		else
			eb.setFooter("appartient à "+ guild.getMemberById(this.owner).getEffectiveName(), guild.getMemberById(this.owner).getUser().getAvatarUrl());
		return eb;
	}

	public File getProfile() {
		return new File(profile);
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
	}

	public Integer getId() {
		return id;
	}

	public String getOrigin() {
		return origin;
	}



	private void save() {
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
}
