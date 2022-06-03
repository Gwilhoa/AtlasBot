

package fr.cringebot.cringe.command;


import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.Polls.PollMain;
import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.builder.Command.ExecutorType;
import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.objects.SelectOptionImpl;
import fr.cringebot.cringe.objects.UserExtenders;
import fr.cringebot.cringe.reactionsrole.MessageReact;
import fr.cringebot.cringe.waifus.Waifu;
import fr.cringebot.cringe.waifus.WaifuCommand;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static fr.cringebot.cringe.cki.mainCommand.ckimain;

/**
 * fichier de commandes de base
 */
public class CommandListener {

	private final BotDiscord botDiscord;
	/**
	 * intialisation de l'objet
	 *
	 * @param botDiscord
	 * @param commandMap
	 */
	public CommandListener(BotDiscord botDiscord, CommandMap commandMap) {
		this.botDiscord = botDiscord;
	}

	/**
	 * arreter le bot depuis la console NORMALEMENT
	 *
	 * @param jda le bot a arreter
	 */
	@Command(name = "-", type = ExecutorType.CONSOLE)
	private void stop(JDA jda) {
		jda.getPresence().setStatus(OnlineStatus.OFFLINE);
		botDiscord.setRunning(false);
	}

	/**
	 * donne les informations sur une personne
	 *
	 * @param channel channel du message
	 * @param msg     message de l'envoyeur
	 */
	@Command(name = "info", description = "information sur un joueur", type = ExecutorType.USER)
	private void info(MessageChannel channel, Message msg) {
		Member mem = msg.getMember();
		if (msg.getMentions().getMembers().size() != 0) {
			mem = msg.getMentions().getMembers().get(0);
		}
		EmbedBuilder builder = new EmbedBuilder()
				.setAuthor(mem.getUser().getName(), null, mem.getUser().getAvatarUrl() + "?size=256")
				.setTitle("Informations")
				.setDescription("> surnom :" + mem.getEffectiveName() + "\n" +
						"> état :" + mem.getOnlineStatus().name() + "\n" +
						"> rejoint le serveur le " + mem.getTimeJoined().getDayOfMonth() + "/" + mem.getTimeJoined().getMonthValue() + "/" + mem.getTimeJoined().getYear() + "\n" +
						"> hypesquad " + UserExtenders.getHypesquad(mem) + "\n" +
						"> creer son compte le " + mem.getTimeCreated().getDayOfMonth() + "/" + mem.getTimeCreated().getMonthValue() + "/" + mem.getTimeCreated().getYear() + "\n" +
						"> messages total : " + UserExtenders.getAllmsg(mem))
				.setColor(mem.getColor());
		channel.sendMessageEmbeds(builder.build()).queue();
	}

	/**
	 * Donne le classement des escoudes
	 * en fonction de leurs point respectif
	 *
	 * @param msg	message de l'envoyeur
	 */
	@Command(name = "top", description = "regarder le classement des escouades")
	private void top(Message msg){
		List<Squads> squads = Squads.getAllSquads();
		StringBuilder sb = new StringBuilder();

		// a b c sont des string pour pour faciliter l'assemblage du message en fonction du classement
		String a = squads.get(0).getName() + " " + squads.get(0).getTotal() + " meilleur : " + msg.getGuild().getMemberById(squads.get(0).getBestid()).getAsMention();
		a = a + " avec " + squads.get(0).getStatMember(msg.getGuild().getMemberById(squads.get(0).getBestid())).getPoints() + " pts\n";
		String b = squads.get(1).getName() + " " + squads.get(1).getTotal() + " meilleur : " + msg.getGuild().getMemberById(squads.get(1).getBestid()).getAsMention();
		b = b + " avec " + squads.get(1).getStatMember(msg.getGuild().getMemberById(squads.get(1).getBestid())).getPoints() + " pts\n";
		String c = squads.get(2).getName() + " " + squads.get(2).getTotal() + " meilleur : " + msg.getGuild().getMemberById(squads.get(2).getBestid()).getAsMention();
		c = c + " avec " + squads.get(2).getStatMember(msg.getGuild().getMemberById(squads.get(2).getBestid())).getPoints() + " pts\n";

		EmbedBuilder eb = new EmbedBuilder().setTitle("Classement :");
		if ((squads.get(0).getTotal() >= squads.get(1).getTotal()) && (squads.get(1).getTotal() >= squads.get(2).getTotal())) // guild 0 > 1 > 2
		{
			sb.append(a).append(b).append(c);
			eb.setColor(squads.get(0).getSquadRole(msg.getGuild()).getColor());
		}
		else if ((squads.get(0).getTotal() >= squads.get(2).getTotal()) && (squads.get(2).getTotal() >= squads.get(1).getTotal())) {// guild 0 > 2 > 1
			sb.append(a).append(c).append(b);
			eb.setColor(squads.get(0).getSquadRole(msg.getGuild()).getColor());
		}
		else if ((squads.get(1).getTotal() >= squads.get(0).getTotal()) && (squads.get(0).getTotal() >= squads.get(2).getTotal())) {// guild 1 > 0 > 2
			sb.append(b).append(a).append(c);
			eb.setColor(squads.get(1).getSquadRole(msg.getGuild()).getColor());
		}
		else if ((squads.get(1).getTotal() >= squads.get(2).getTotal()) && (squads.get(2).getTotal() >= squads.get(0).getTotal())) { // guild 1 > 2 > 0
			sb.append(b).append(c).append(a);
			eb.setColor(squads.get(1).getSquadRole(msg.getGuild()).getColor());
		}
		else if ((squads.get(2).getTotal() >= squads.get(0).getTotal()) && (squads.get(0).getTotal() >= squads.get(1).getTotal())) { // guild 2 > 0 > 1
			sb.append(c).append(a).append(b);
			eb.setColor(squads.get(2).getSquadRole(msg.getGuild()).getColor());
		}
		else  // guild 2 > 1 > 0
		{
			sb.append(c).append(b).append(a);
			eb.setColor(squads.get(2).getSquadRole(msg.getGuild()).getColor());
		}
		eb.setDescription(sb);
		msg.getChannel().sendMessageEmbeds(eb.build()).queue();
	}

	@Command(name = "rank", description = "rang")
	private void rank(Message msg){
		Member mb;
		mb = msg.getMember();
		if (!msg.getMentions().getMembers().isEmpty()) {
			mb = msg.getMentions().getMembers().get(0);
		}
		Long p = Squads.getSquadByMember(mb).getStatMember(mb).getPoints();
		msg.getChannel().sendMessage("classement de "+ mb.getAsMention() + "\n" + p.toString()).queue();
	}

	@Command(name = "poll", description = "faites des sondages rapidements", type = ExecutorType.USER)
	private void poll(Message msg) {
		PollMain.PollMain(msg);
	}

	@Command(name = "role", description = "permettre de creer un role", type = ExecutorType.USER)
	private void role(Message msg) {
		String[] args = msg.getContentRaw().split(" ");
		ArrayList<SelectOption> options = new ArrayList<>();
		if (args.length == 3) {
			msg.addReaction(args[2]).queue();
			Role r = msg.getGuild().createRole().setName("©◊ß" + args[1]).setMentionable(true).setColor(new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255))).complete();
			EmbedBuilder eb = new EmbedBuilder()
					.setTitle("nouveau role")
					.setDescription(r.getName().replace("©◊ß", "") + "\n" + args[2])
					.setFooter(r.getId());
			for (MessageReact mr : MessageReact.message)
				options.add(new SelectOptionImpl(mr.getTitle(), mr.getTitle()));
			SelectMenuImpl selectionMenu = new SelectMenuImpl("role", "catégorie", 1, 1, false, options);
			msg.getChannel().sendMessageEmbeds(eb.build()).setActionRow(selectionMenu).complete();
		} else {
			msg.getChannel().sendMessage("erreur argument >role <nom> <emote>").queue();
		}
	}

	@Command(name = "harem", description = "la listes des waifus", type = ExecutorType.USER)
	private void harem(Message msg){
		String id = msg.getMember().getId();
		if (!msg.getMentions().getMembers().isEmpty())
			id = msg.getMentions().getMembers().get(0).getId();
		ArrayList<Waifu> waifus = Waifu.getAllWaifu();
		String finalId = id;
		waifus.removeIf(w -> w.getOwner() == null || !w.getOwner().equals(finalId));
		if (waifus.isEmpty())
		{
			msg.getChannel().sendMessage("\uD83D\uDE2D tu n'as pas de waifus, fais >Waifu pour en avoir une !").queue();
			return;
		}
		StringBuilder sb = new StringBuilder().append("waifus de ").append(msg.getGuild().getMemberById(id).getEffectiveName()).append("\n\n");
		for (Waifu w : waifus)
			sb.append(w.getId()).append(" ").append(w.getName()).append(" de ").append(w.getOrigin()).append("\n");
		MessageBuilder mb = new MessageBuilder().append(sb);
		Queue<Message> ml = mb.buildAll();
		while (!ml.isEmpty())
			msg.getChannel().sendMessage(ml.poll()).queue();
	}
	@Command(name = "waifu", description = "instance des waifus", type = ExecutorType.USER)
	private void waifu(Message msg) throws ExecutionException, InterruptedException {
		WaifuCommand.CommandMain(msg);
	}

	@Command(name = "cki", description = "mais qui est-il !", type = ExecutorType.USER)
	private void cki(Message msg){
		ckimain(msg);
	}

	@Command(name = "reset", type = Command.ExecutorType.USER)
	private void reset(Message msg) throws IOException {
		if (msg.getMember().getId().equals("315431392789921793"))
		{
			ArrayList<Squads> squads = Squads.getAllSquads();
			for (Squads squad : squads)
				squad.ResetPoint();
		}
	}

	@Command(name = "test", description = "commande provisoire", type = ExecutorType.USER)
	private void test(Message msg) {
		for (Member m : msg.getGuild().getMembersWithRoles(msg.getGuild().getRoleById(BotDiscord.SecondaryRoleId))){
			ArrayList<Waifu> waifus = Waifu.getAllWaifu();
			waifus.removeIf(w -> w.getOwner() == null || !w.getOwner().equals(m.getId()));
			for (Waifu w : waifus)
				w.setOwner(null);
		}
	}
}