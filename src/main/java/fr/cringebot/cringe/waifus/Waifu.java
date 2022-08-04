package fr.cringebot.cringe.waifus;

import com.google.gson.reflect.TypeToken;
import com.jcraft.jsch.*;
import fr.cringebot.cringe.objects.StringExtenders;
import fr.cringebot.cringe.objects.imgExtenders;
import net.dv8tion.jda.api.entities.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static fr.cringebot.BotDiscord.mdp;
import static fr.cringebot.BotDiscord.user;
import static fr.cringebot.cringe.event.BotListener.gson;

public class Waifu {
	private static final String file = "save/waifus.json";
	private static final TypeToken<HashMap<Integer, Waifu>> token = new TypeToken<HashMap<Integer, Waifu>>() {
	};
	private static HashMap<Integer, Waifu> waifuList = new HashMap<>();
	private final String profile;
	private boolean legendary;
	private boolean istaken = false;
	private String name;
	private String description;
	private String type;
	private Integer id;
	private String origin;
	private ArrayList<String> nickname;

	public static ArrayList<String> getAllOrigins() {
		ArrayList<Waifu> waifus = getAllWaifu();
		ArrayList<String> ret = new ArrayList<>();
		for (Waifu waifu : waifus)
		{
			if (!ret.contains(waifu.getOrigin()))
				ret.add(waifu.getOrigin());
		}
		return ret;
	}

	public String getType() {
		return type;
	}

	public void setOrigin(String name) {
		this.origin = name;
		save();
	}

	public Waifu(Message.Attachment f, String name, String description, String type, String origin, boolean legendary)
	{
		this.legendary = legendary;
		int	i = 0;
		this.id = -1;
		this.description = description;
		this.name = name;
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
		this.profile = "https://cdn.bitume2000.fr/apps/waifu/waifu_"+this.getId()+".png";
		this.origin = origin;
		try {
			this.setFile(f);
		}catch (JSchException | IOException e) {
			e.printStackTrace();
		}
		waifuList.put(this.id, this);
		save();
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean setFile(Message.Attachment f) throws IOException, JSchException {
		String REMOTE_HOST = "45.90.161.144";
		String USERNAME = user;
		String PASSWORD = mdp;
		int REMOTE_PORT = 22;
		int SESSION_TIMEOUT = 10000;
		int CHANNEL_TIMEOUT = 5000;
		Session jschSession = null;
		try {
			JSch jsch = new JSch();
			jsch.setKnownHosts("/home/.ssh/known_hosts");
			jschSession = jsch.getSession(USERNAME, REMOTE_HOST, REMOTE_PORT);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			jschSession.setConfig(config);
			jschSession.setPassword(PASSWORD);
			jschSession.connect(SESSION_TIMEOUT);
			Channel sftp = jschSession.openChannel("sftp");
			sftp.connect(CHANNEL_TIMEOUT);
			ChannelSftp channelSftp = (ChannelSftp) sftp;
			channelSftp.put(imgExtenders.getFile(f.getUrl(),"waifu_"+this.getId(), null).getPath(), "/www/apps/waifu/waifu_"+this.getId()+".png");
			channelSftp.exit();
			} catch (JSchException | SftpException e) {
				e.printStackTrace();
			} finally {
				if (jschSession != null) {
					jschSession.disconnect();
				}
			}
			return true;
		}

	public void delwaifu()
	{
		waifuList.remove(this.id);
		save();
	}

	public void setLegendary(boolean legendary) {
		this.legendary = legendary;
	}

	public static ArrayList<Waifu> getAllWaifu(){
		return new ArrayList<>(waifuList.values());
	}

	public static ArrayList<Waifu> getWaifusByOrigin(String str)
	{
		ArrayList<Waifu> waifus = getAllWaifu();
		waifus.removeIf(w -> !w.getOrigin().equalsIgnoreCase(str));
		return waifus;
	}

	public static ArrayList<Waifu> getWaifubyName(String name){
		ArrayList<Waifu> list = new ArrayList<>();
		if (!waifuList.isEmpty())
		{
			for (Waifu waifu : waifuList.values())
			{
				if (waifu.getName() == null || StringExtenders.startWithIgnoreCase(waifu.getName(),name))
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

	public Integer getId() {
		return id;
	}

	public String getOrigin() {
		return origin;
	}



	public static void save() {
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

	public void setIstaken(boolean istaken) {
		this.istaken = istaken;
	}

	public boolean isIstaken() {
		return istaken;
	}

	public boolean isLegendary() {
		return legendary;
	}
}
