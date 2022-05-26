package fr.cringebot.cringe.escouades;

import com.google.gson.reflect.TypeToken;
import fr.cringebot.cringe.waifus.waifu;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static fr.cringebot.cringe.event.BotListener.gson;

public class Squads {
	private static final String file = "save/squads.json";
	private static final TypeToken<HashMap<String, Squads>> token = new TypeToken<HashMap<String, Squads>>() {
	};
	private static HashMap<String, Squads> squadsHashMap = new HashMap<>();
	private String name;
	private HashMap<String, SquadMember> MemberList;
	private Long total;
	private String roleid;

	public Squads(String name, Guild guild) {
		this.name = name;
		this.MemberList = new HashMap<>();
		this.total = 0L;
		this.roleid = guild.createRole().setName("©◊ß" + name).complete().getId();
		squadsHashMap.put(roleid, this);
		save();
	}

	public static Squads getSquadByMember(Member m){
		return getSquadByMember(m.getId());
	}

	public static ArrayList<Squads> getAllSquads() {
		return new ArrayList<>(squadsHashMap.values());
	}

	public static Squads getSquadByMember(String id) {
		for (Squads squad : squadsHashMap.values())
		{
			if (squad.MemberList.get(id) != null)
				return squad;
		}
		return null;
	}

	public SquadMember getStatMember(Member m) {
		return getStatMember(m.getId());
	}
	public SquadMember getStatMember(String id) {
		return this.MemberList.get(id);
	}

	public static Squads getSquadByRole(Role role){
		return squadsHashMap.get(role.getId());
	}

	public void addMember(Member m) {
		addMember(m.getId());
		m.getGuild().addRoleToMember(m, m.getGuild().getRoleById(this.roleid)).queue();
		save();
	}
	public void addMember(String id) {
		this.MemberList.put(id, new SquadMember(id));
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
			gson.toJson(squadsHashMap, token.getType(), bw);
			bw.flush();
			bw.close();
		} catch (IOException e) {
		}
	}

	public static void load() {
		if (new File(file).exists()) {
			try {
				squadsHashMap = gson.fromJson(new BufferedReader(new InputStreamReader(new FileInputStream(file))), token.getType());
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
		if (squadsHashMap == null)
			squadsHashMap = new HashMap<>();
	}

}
