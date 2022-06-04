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

	public void removeMember(Member m) {
		removeMember(m.getId());
	}

	public void removeMember(String id) {
		this.MemberList.remove(id);
	}

	public void ResetPoint() {
		for (SquadMember sm : this.MemberList.values())
			sm.resetPoint();
		save();
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
		long cal = Math.round( i * Squads.equilibrage(id) );
		squad.getStatMember(id).addPoint(cal);
		save();
	}

	public Role getSquadRole(Guild g)
	{
		return g.getRoleById(this.roleid);
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

	public static float equilibrage(String id) // id correspond a l'id de l'expediteut du msg
	{
		List<Squads> squads = Squads.getAllSquads();
		Squads squad = getSquadByMember(id);

		ArrayList<Long> pts_squads = new ArrayList<>(); // pts_squad est une liste qui
		pts_squads.add(squads.get(0).getTotal());       // stock les points de chaque
		pts_squads.add(squads.get(1).getTotal());       // escouade en position 0/1/2
		pts_squads.add(squads.get(2).getTotal());

		float r = 1;

		if(squads.get(0).getName() == squad.getName()) {
			r = calcule(pts_squads,0);
		}
		else if(squads.get(1).getName() == squad.getName()) {
			r = calcule(pts_squads,1);
		}
		else if(squads.get(2).getName() == squad.getName()) {
			r = calcule(pts_squads,2);
		}
		return r;
	}

	private static float calcule(ArrayList L, Integer n) //fonction de calcule pour l'equilibrage
	{
		float cal = 0;

		for(int i = 0; i <= 3 ; i++) {

			long b = (long) L.get(n);
			long c = (long) L.get(i);

			if (1 != (b / c))
				cal += (b / c);
		}
		return 2/cal;
	}
}
