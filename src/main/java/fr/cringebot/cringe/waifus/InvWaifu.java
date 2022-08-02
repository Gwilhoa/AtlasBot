package fr.cringebot.cringe.waifus;

import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.escouades.Squads;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.awt.*;
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
		this.FriendlyLevel = 0;
	}

	public Integer getFriendlyLevel() {
		if (this.FriendlyLevel == 0)
			return 0;
		float div = (float)this.FriendlyLevel/((float)(this.level + 1) * 5000);
		div = div * 100;
		return (int) div;
	}

	public void setLevel(Long level) {
		this.level = level;
	}

	public Long getLevel() {
		return this.level;
	}

	public Integer getId() {
		return Id;
	}

	public void addXp(Integer xp)
	{
		if (this.FriendlyLevel == null)
			this.FriendlyLevel = 0;
		this.FriendlyLevel = this.FriendlyLevel + xp;
		if (this.FriendlyLevel >= 5000 * (this.level + 1))
		{
			this.level++;
			this.FriendlyLevel = 0;
		}
		Squads.save();
	}

	public static EmbedBuilder catchWaifu(Integer id, String memID, Guild g) throws InterruptedException {
		if (isMaintenance) {
			return new EmbedBuilder().setTitle("Raté, revenez plus tard").setColor(Color.red).setDescription("actuellement en maintenance");
		}
		else if (Squads.getstats(memID) == null){
			return new EmbedBuilder().setTitle("Mais tu es qui ?").setDescription("tu n'as pas d'escouade, c'est peut etre volontaire").setColor(Color.RED);
		}
		Waifu w = Waifu.getWaifuById(id);
		File f = new File(w.getProfile());
		EmbedBuilder eb = new EmbedBuilder();
		eb.setImage("attachment://"+f.getName());
		eb.setTitle("Nouvelle Waifu !");
		eb.setDescription("ta nouvelle Waifu est " + w.getName() + " de " + w.getOrigin());
		eb.setFooter("id : " + w.getId());
		eb.setColor(Squads.getSquadByMember(memID).getSquadRole(g).getColor());
		return eb;
	}


	public static void downloadWaifu(File f, MessageAction toSend) {
		waifuLock.lock();
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
