package fr.cringebot.cringe.waifus;

import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.escouades.Squads;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static fr.cringebot.BotDiscord.isMaintenance;
import static fr.cringebot.cringe.waifus.WaifuCommand.waifuLock;
import static java.lang.Math.pow;

public class InvWaifu {
	private final Integer Id;
	private Integer FriendlyLevel;
	private Long level;

	public InvWaifu(Integer id) {
		Id = id;
		this.level = 0L;
	}

	public Integer getFriendlyLevel() {
		double ret = (0.0004 * Math.pow(this.level, 2) + 999 * this.level);
		ret = ret - (int) ret;
		ret = ret * 100;
		return (int) ret;
	}

	public void setLevel(Long level) {
		this.level = level;
	}

	public Integer getLevel() {
		this.FriendlyLevel = null;
		double ret = (0.0004 * Math.pow(this.level, 2) + 999 * this.level);
		return (int) ret;
	}

	public Integer getId() {
		return Id;
	}

	public void addXp(Long xp)
	{
		this.level = this.level + xp;
		Squads.save();
	}

	public static void catchWaifu(Message msg, Integer id) throws InterruptedException {
		if (isMaintenance) {
			msg.getChannel().sendMessage("le bot est actuellement en maintenance").queue();
			return;
		}
		else if (msg.getMember().getRoles().contains(msg.getGuild().getRoleById(BotDiscord.SecondaryRoleId))){
			msg.getChannel().sendMessage("Tu es un compte secondaire et moi, j'aime pas les comptes secondaires").queue();
			return;
		}
		Waifu w = Waifu.getWaifuById(id);
		File f = new File(w.getProfile());
		EmbedBuilder eb = new EmbedBuilder();
		eb.setImage("attachment://"+f.getName());
		eb.setTitle("Nouvelle Waifu !");
		eb.setDescription("ta nouvelle Waifu est " + w.getName() + " de " + w.getOrigin());
		eb.setFooter("id : " + w.getId());
		if (!msg.getMember().getId().equals("881962597526696038"))
			eb.setColor(Squads.getSquadByMember(msg.getMember()).getSquadRole(msg.getGuild()).getColor());
		else
			eb.setColor(Squads.getSquadByMember(msg.getEmbeds().get(0).getFooter().getText()).getSquadRole(msg.getGuild()).getColor());
		waifuLock.lock();
		Thread.sleep(100);
		MessageAction toSend = msg.getChannel().sendMessageEmbeds(eb.build());
		downloadWaifu(f, toSend);
	}


	static void downloadWaifu(File f, MessageAction toSend) {
		try(DataInputStream str = new DataInputStream(new FileInputStream(f))){
			byte[] bytes = new byte[(int) f.length()];
			str.readFully(bytes);
			toSend.addFile(bytes, f.getName()).complete();
		} catch (IOException e) {
			//Wrap et remonter
			throw new RuntimeException(e);
		}
		waifuLock.unlock();
	}

	public Waifu getWaifu() {
		return Waifu.getWaifuById(this.Id);
	}
}
