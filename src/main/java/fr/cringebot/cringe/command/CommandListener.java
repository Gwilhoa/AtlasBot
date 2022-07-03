

package fr.cringebot.cringe.command;


import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.Polls.PollMain;
import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.builder.Command.ExecutorType;
import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.escouades.SquadMember;
import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.event.memesEvent;
import fr.cringebot.cringe.objects.DetectorAttachment;
import fr.cringebot.cringe.objects.SelectOptionImpl;
import fr.cringebot.cringe.objects.StringExtenders;
import fr.cringebot.cringe.objects.UserExtenders;
import fr.cringebot.cringe.reactionsrole.MessageReact;
import fr.cringebot.cringe.waifus.Waifu;
import fr.cringebot.cringe.waifus.WaifuCommand;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
		if (msg.getContentRaw().length() > ">top ".length())
		{
			Guild guild = msg.getGuild();
			StringBuilder sb = new StringBuilder();
			Squads squad = Squads.getSquadByName(msg.getContentRaw().substring(">top ".length()));
			if (squad == null)
				return;
			List<SquadMember> sm = squad.getSortedSquadMember();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("escouade "+ squad.getName());
			int i = 1;
			for (SquadMember s : sm)
			{
				sb.append("n°").append(i).append(" : ").append(guild.getMemberById(s.getId()).getAsMention()).append(" avec ").append(s.getPoints()).append("\n");
				i++;
			}
		}
		else
		{
			List<Squads> squads = Squads.getSortedSquads();
			StringBuilder sb = new StringBuilder();
			EmbedBuilder eb = new EmbedBuilder().setTitle("Classement :");
			for (Squads sq : squads)
				sb.append(sq.getName()).append(' ').append(sq.getTotal()).append('\n')
						.append(" meilleur : ").append(msg.getGuild().getMemberById(sq.getBestid()).getAsMention()).append(" avec ").append(sq.getStatMember(sq.getBestid()).getPoints()).append('\n');
			eb.setColor(squads.get(0).getSquadRole(msg.getGuild()).getColor());
			eb.setDescription(sb);
			msg.getChannel().sendMessageEmbeds(eb.build()).queue();
		}
	}

	@Command(name = "rank", description = "rang")
	private void rank(Message msg){
		Member mb;
		EmbedBuilder eb = new EmbedBuilder();
		mb = msg.getMember();
		if (!msg.getMentions().getMembers().isEmpty())
			mb = msg.getMentions().getMembers().get(0);
		Squads squad = Squads.getSquadByMember(mb);
		eb.setColor(squad.getSquadRole(msg.getGuild()).getColor());
		eb.setTitle(msg.getMember().getEffectiveName());
		eb.setDescription("Nombres de points : " + squad.getStatMember(mb).getPoints().toString() + "\ntotal de l'escouade : " + squad.getTotal().toString());
		eb.setFooter("rang : coming soon");
		msg.getChannel().sendMessageEmbeds(eb.build()).queue();
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
			msg.addReaction(Emoji.fromFormatted(args[2])).queue();
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
		if (!msg.getChannel().getId().equals(BotDiscord.FarmingSalonId)) {
			msg.getChannel().sendMessage("non").queue();
			return;
		}
		String id = msg.getMember().getId();
		if (!msg.getMentions().getMembers().isEmpty())
			id = msg.getMentions().getMembers().get(0).getId();
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Waifu de " + msg.getGuild().getMemberById(id).getEffectiveName());
		eb.setAuthor(id);
		eb.setDescription("chargement...");
		msg = msg.getChannel().sendMessageEmbeds(eb.build()).complete();
		msg.addReaction(Emoji.fromFormatted("◀️")).and(msg.addReaction(Emoji.fromFormatted("▶️"))).queue();
		WaifuCommand.haremEmbed(msg);
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

	@Command(name = "clear", description = "let's clean", type = ExecutorType.USER)
	private void clear(Message msg){
		if (msg.getContentRaw().split(" ").length > 1){
			try {
				int arg = Integer.parseInt(msg.getContentRaw().split(" ")[1]);
				msg.getChannel().getHistoryBefore(msg, arg).queue((mh) -> msg.getChannel().purgeMessages(mh.getRetrievedHistory()));
				msg.delete().queue();
			} catch (IndexOutOfBoundsException | NumberFormatException ignored) {
				msg.getChannel().sendMessage("Mauvais usage de la commande ! il faut mettre par exemple : !clear 5").queue();
			}

		}
		else {
			if (msg.getReferencedMessage() != null) {
				MessageHistory msgs = msg.getChannel().getHistoryAfter(msg.getMessageReference().getMessageId(),100).complete();
				while(msgs.getMessageById(msg.getId()) == null){
					msgs.retrieveFuture(100).complete();
				}
				final int chunkSize = 100;
				final AtomicInteger counter = new AtomicInteger();
				for(List<Message> v : msgs.getRetrievedHistory().stream().dropWhile((m)-> !m.getId().equals(msg.getId())).collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize)).values()){
					msg.getGuildChannel().deleteMessages(v).queue();
				}
			}
		}
	}

	@Command(name = "test", description = "commande provisoire", type = ExecutorType.USER)
	private void test(Message msg) throws IOException {
		msg.getChannel().sendFile(new File("save.zip")).queue();
	}
}