package fr.cringebot.cringe.command;


import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.builder.Command.ExecutorType;
import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.objects.UserExtenders;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;

public class CommandDefault {

	private final BotDiscord botDiscord;
	private final CommandMap commandMap;

	public CommandDefault(BotDiscord botDiscord, CommandMap commandMap){
		this.botDiscord = botDiscord;
		this.commandMap = commandMap;
	}

	@Command(name="stop",type=ExecutorType.CONSOLE)
	private void stop(){
		botDiscord.setRunning(false);
	}


	@Command(name="info",type=ExecutorType.USER)
	private void info(MessageChannel channel, Message msg){
		Member mem = msg.getMember();
		if (msg.getMentionedMembers().size() != 0){
			mem = msg.getMentionedMembers().get(0);
		}

		EmbedBuilder builder = new EmbedBuilder();
		builder.setAuthor(mem.getUser().getName(), null, mem.getUser().getAvatarUrl()+"?size=256");
		builder.setTitle("Informations");
		builder.setDescription("> surnom :"+ mem.getEffectiveName() +"\n"+
				               "> état :"+ mem.getOnlineStatus().name()+ "\n"+
							   "> rejoint le serveur le "+ mem.getTimeJoined().getDayOfMonth()+"/"+mem.getTimeJoined().getMonthValue()+"/"+mem.getTimeJoined().getYear()+"\n"+
				               "> hypesquad "+ UserExtenders.getHypesquad(mem) +"\n"+
							   "> creer son compte le "+ mem.getTimeCreated().getDayOfMonth()+"/"+mem.getTimeCreated().getMonthValue()+"/"+mem.getTimeCreated().getYear()+"\n"+
							   "> messages total : " + UserExtenders.getAllmsg(mem));
		builder.setColor(Color.green);

		channel.sendMessageEmbeds(builder.build()).queue();
	}

}