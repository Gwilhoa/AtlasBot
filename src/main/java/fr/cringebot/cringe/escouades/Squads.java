package fr.cringebot.cringe.escouades;

import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static fr.cringebot.cringe.event.BotListener.gson;

public class Squads {
	private static final String file = "save/squads.json";
	private static final TypeToken<HashMap<String, Squads>> token = new TypeToken<HashMap<String, Squads>>() {
	};
	private static HashMap<String, Squads> squadsHashMap = new HashMap<>();
	private final String name;
	private final HashMap<String, SquadMember> MemberList;
	private Long total;
	private final String roleid;
	private String bestid;

	public Squads(Role r) {
		this.name = r.getName();
		this.MemberList = new HashMap<>();
		this.total = 0L;
		this.roleid = r.getId();
		squadsHashMap.put(roleid, this);
		save();
		for ( Member m : r.getGuild().getMembersWithRoles(r))
			this.addMember(m);
	}

	public static Squads getSquadByMember(Member m){
		return getSquadByMember(m.getId());
	}

	public static ArrayList<Squads> getAllSquads() {
		return new ArrayList<>(squadsHashMap.values());
	}

	public static void updateSquads() {
		List<Squads> squads = getAllSquads();
		for (Squads squad : squads) {
			long value = 0L;
			String bestId = null;
			long max = 0;
			for (SquadMember sm : squad.MemberList.values()) {
				if (bestId == null || max < sm.getPoints()){
					bestId = sm.getId();
					max = sm.getPoints();
				}
				value = value + sm.getPoints();
			}
			squad.bestid = bestId;
			squad.total = value;
		}
	}

	public String getBestid() {
		return bestid;
	}

	public static void addPoints(Member m, Long i){
		addPoints(m.getId(), i);
	}

	public static void addPoints(String id, Long i){
		Squads squad = getSquadByMember(id);
		if (squad == null) {
			System.out.println("error " + id);
			return;
		}
		squad.getStatMember(id).addPoint(i);
		save();
	}

	public static void removePoints(String id, Long i){
		Squads squad = getSquadByMember(id);
		if (squad == null) {
			System.out.println("error " + id);
			return;
		}
		squad.getStatMember(id).removepoint(i);
		save();
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
		updateSquads();
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
		updateSquads();
	}

	public String getName() {
		return name;
	}

	public Long getTotal() {
		return total;
	}
}
