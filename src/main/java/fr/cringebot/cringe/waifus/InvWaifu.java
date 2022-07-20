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

public class InvWaifu {
	private final Integer Id;
	private Integer FriendlyLevel;
	private Long level;

	public InvWaifu(Integer id) {
		Id = id;
		this.FriendlyLevel = 0;
		this.level = 0L;
	}

	public Integer getFriendlyLevel() {
		return FriendlyLevel;
	}

	public void setFriendlyLevel(Integer friendlyLevel) {
		FriendlyLevel = friendlyLevel;
	}

	public void setLevel(Long level) {
		this.level = level;
	}

	public Long getLevel() {
		return level;
	}

	public Integer getId() {
		return Id;
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
			eb.setColor(Squads.getSquadByMember(msg.getContentRaw().split("\n")[1]).getSquadRole(msg.getGuild()).getColor());
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
