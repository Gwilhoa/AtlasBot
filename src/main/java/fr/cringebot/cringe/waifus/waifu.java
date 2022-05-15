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
	private final String name;
	private final String description;
	private String owner;
	private final String type;
	private final Integer id;

	public String getType() {
		return type;
	}

	public enum Type
	{
		JEUX_VIDEO,
		FILM,
		ANIME,
		B2K
	}

	public waifu(Message.Attachment f, String name, String description, String type)
	{
		this.description = description;
		this.name = name;
		this.owner = null;
		this.type = type;
		this.id = waifuList.size();
		this.profile = "save/waifu/waifu_"+this.id +".png";
		f.downloadToFile(this.profile);
		waifuList.put(this.id, this);
		save();
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

	public EmbedBuilder EmbedWaifu(Guild guild) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setImage("attachment://" + this.profile);
		eb.setDescription("nom : " + this.name +"\n\ndescription :\n"+this.description);
		if (this.owner == null)
			eb.setFooter("disponible");
		else
			eb.setFooter("appartient à "+ guild.getMemberById(this.owner).getEffectiveName(), guild.getMemberById(this.owner).getAvatarUrl());
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
