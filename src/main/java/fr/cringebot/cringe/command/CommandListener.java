

package fr.cringebot.cringe.command;


import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.CommandBuilder.Gift;
import fr.cringebot.cringe.CommandBuilder.Info;
import fr.cringebot.cringe.CommandBuilder.Shop;
import fr.cringebot.cringe.CommandBuilder.Top;
import fr.cringebot.cringe.Polls.PollMain;
import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.builder.Command.ExecutorType;
import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.escouades.SquadMember;
import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.objects.Item;
import fr.cringebot.cringe.objects.SelectOptionImpl;
import fr.cringebot.cringe.reactionsrole.MessageReact;
import fr.cringebot.cringe.slashInteraction.slashCommand;
import fr.cringebot.cringe.waifus.Waifu;
import fr.cringebot.cringe.waifus.WaifuCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import net.dv8tion.jda.internal.interactions.component.ButtonInteractionImpl;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static fr.cringebot.cringe.cki.mainCommand.ckimain;
import static fr.cringebot.cringe.objects.imgExtenders.resize;

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

	@Command(name = "bresil", description = "you are going to brazil", type = ExecutorType.USER)
	private void bresil(Message msg) {
		if (msg.getMentions().getMembers().get(0) == null)
		{
			msg.getChannel().sendMessage("tu veux emmener qui ?").queue();
		} else {
			Member member = msg.getMentions().getMembers().get(0);
			if (Squads.getstats(msg.getMember()).getAmountItem(Item.Items.PB.getStr()) > 0)
			{
				if (msg.getMember().getVoiceState().inAudioChannel() && member.getVoiceState().inAudioChannel())
				{
					msg.getChannel().sendMessage(member.getAsMention() + " you are going to brazil with "+ msg.getMember().getAsMention()).queue();
					msg.getGuild().moveVoiceMember(member, msg.getGuild().getVoiceChannelById("974740318413025340")).queue();
					msg.getGuild().moveVoiceMember(msg.getMember(), msg.getGuild().getVoiceChannelById("974740318413025340")).queue();
					Squads.getstats(msg.getMember()).removeItem(Item.Items.PB.getStr());
				}
			} else {
				msg.getChannel().sendMessage("tu n'as pas de ticket").queue();
			}
		}
	}

	@Command(name = "info", description = "information sur un joueur", type = ExecutorType.USER)
	private void info(MessageChannel channel, Message msg) {
		Member mem = msg.getMember();
		if (msg.getMentions().getMembers().size() != 0)
			mem = msg.getMentions().getMembers().get(0);
		channel.sendMessageEmbeds(Info.info(mem).build()).queue();
	}

	@Command(name = "shop", description = "ouvrir le shopping")
	private void shop(Message msg) {
		msg.getChannel().sendMessageEmbeds(Shop.ShopDisplay(msg.getMember()).build()).setActionRow(Shop.PrincipalMenu()).queue();
	}

	@Command(name = "gift", description = "des cadeaux ?", type = ExecutorType.USER)
	private void gift(Message msg) throws InterruptedException, IOException {
		if (msg.getContentRaw().split(" ").length <= 1)
			return;
		String code = msg.getContentRaw().substring(">gift ".length());
		Gift ret = Gift.sendGift(code, msg.getMember());
		EmbedBuilder eb = ret.getEmbedBuilder();
		if (ret.getId() == null)
			msg.getChannel().sendMessageEmbeds(eb.build()).queue();
		else {
			ButtonImpl bttn = new ButtonImpl("gift_" + ret.getId(), "ouvrir", ButtonStyle.SUCCESS, false, null);
			msg.getChannel().sendMessageEmbeds(eb.build()).setActionRow(bttn).queue();
		}
	}

	@Command(name = "top", description = "regarder le classement des escouades")
	private void top(Message msg){
		if (msg.getContentRaw().length() > ">top ".length())
			msg.getChannel().sendMessageEmbeds(Top.top(msg.getContentRaw().substring(">top ".length()), msg.getGuild()).build()).queue();
		else
			msg.getChannel().sendMessageEmbeds(Top.top(null, msg.getGuild()).build()).queue();
	}

	@Command(name = "poll", description = "faites des sondages rapidements", type = ExecutorType.USER)
	private void poll(Message msg) {
		String[] args = msg.getContentRaw().split("\n");
		String name = args[0].substring(">poll ".length());
		args = msg.getContentRaw().substring(name.length() + 1).split("\n");
		PollMain.PollMain(args, name, msg.getTextChannel(), msg.getMember());
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
	private void waifu(Message msg) throws ExecutionException, InterruptedException, IOException {
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

	@Command(name = "meteo", type = Command.ExecutorType.USER, description = "donne la météo")
	private void meteo(Guild guild, TextChannel textChannel, Message msg){
		String ville = "shrek";
		if (msg.getContentRaw().split(" ").length != 1)
			ville = msg.getContentRaw().split(" ")[1];
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			URLConnection connection = new URL("https://wttr.in/"+ville+"_3tqp_lang=fr.png").openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0");
			BufferedImage im = ImageIO.read(connection.getInputStream());
			im = resize(im, 1032*2, 546*2, 0, 0, true);
			im = resize(im, 1032*2, 546*2, 0, 0, false);
			ImageIO.write(im, "png", baos);
			textChannel.sendFile(baos.toByteArray(), "meteo.png").queue();
		} catch (IOException e) {
			textChannel.sendMessage("météo introuvable").queue();
		}

	}

	@Command(name = "test", description = "commande provisoire", type = ExecutorType.USER)
	private void test(Message msg) throws IOException, InterruptedException {
		slashCommand.load(msg.getJDA());
	}
}