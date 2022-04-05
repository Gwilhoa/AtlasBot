/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   BotListener.java                                   :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 11:45:58 by gchatain          #+#    #+#             */
/*   Updated: 2022/04/05 12:05:40 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */


package fr.cringebot.cringe.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.cki.cki;
import fr.cringebot.cringe.cki.ckiListener;
import fr.cringebot.cringe.lol.Champion;
import fr.cringebot.cringe.objects.*;
import fr.cringebot.cringe.pokemon.objects.Attacks;
import fr.cringebot.cringe.pokemon.objects.Pokemon;
import fr.cringebot.music.MusicCommand;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GatewayPingEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.http.HttpRequestEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageEmbedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import static fr.cringebot.cringe.cki.mainCommand.MenuInteract;
import static fr.cringebot.cringe.event.MembersQuotes.MemberReact;
import static fr.cringebot.cringe.event.ReactionEvent.*;
import static fr.cringebot.cringe.event.memesEvent.postmeme;
import static fr.cringebot.cringe.objects.StringExtenders.containsIgnoreCase;
import static fr.cringebot.cringe.objects.StringExtenders.startWithIgnoreCase;

/**
 * capture tout les evenements du bot
 */
public class BotListener implements EventListener {

	private final CommandMap commandMap;
	private final BotDiscord bot;

	public BotListener(CommandMap cmd, BotDiscord bot) {
		this.commandMap = cmd;
		this.bot = bot;
	}

	public static Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(ArrayNumber.class,
			(JsonDeserializer<ArrayNumber<Integer>>) (jsonElement, type, jsonDeserializationContext) -> {
				if (!jsonElement.isJsonArray()) {
					return new ArrayNumber<>(jsonElement.getAsNumber().intValue());
				}
				ArrayNumber<Integer> n = new ArrayNumber<>();
				JsonArray ar = jsonElement.getAsJsonArray();
				for (int i = 0; i < ar.size(); i++) {
					n.li.add(ar.get(i).getAsNumber().intValue());
				}
				return n;
			}).create();

	@Override
	public void onEvent(GenericEvent event) {
		System.out.println(event.getClass().getSimpleName());
		if (event instanceof ReadyEvent) {
			try {
				onEnable((ReadyEvent) event);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (event instanceof MessageReceivedEvent) {
			try {
				onMessage((MessageReceivedEvent) event);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		} else if (event instanceof GuildMemberJoinEvent) onGuildMemberJoin((GuildMemberJoinEvent) event);
		else if (event instanceof GuildMemberRemoveEvent) onGuildMemberLeave((GuildMemberRemoveEvent) event);
		else if (event instanceof MessageReactionAddEvent) {
			try {
				onAddReact((MessageReactionAddEvent) event);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		else if (event instanceof SelectMenuInteractionEvent) onSelectMenu((SelectMenuInteractionEvent) event);
		else if (event instanceof GatewayPingEvent) onPing((GatewayPingEvent) event);
		else if (event instanceof RoleCreateEvent) onCreateRole((RoleCreateEvent) event);
		else if (event instanceof RoleDeleteEvent) onDeleteRole((RoleDeleteEvent) event);
		else if (event instanceof MessageReactionRemoveEvent) onRemoveReact((MessageReactionRemoveEvent) event);
		else if (event instanceof GuildUpdateNameEvent) onChangeServerName((GuildUpdateNameEvent) event);
		else if (event instanceof GuildVoiceJoinEvent) onConnect((GuildVoiceJoinEvent) event);
		else if (event instanceof GuildVoiceLeaveEvent) onDisconnect((GuildVoiceLeaveEvent) event);
		else if (event instanceof MessageEmbedEvent) {
			try {
				onEmbed((MessageEmbedEvent) event);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else if (event instanceof HttpRequestEvent) onRequest((HttpRequestEvent) event);
	}

	private void onDisconnect(GuildVoiceLeaveEvent event) {
		if (event.getMember().getUser().equals(event.getJDA().getSelfUser()))
			MusicCommand.stop(event.getGuild());
		XP.disconnecting(event);
		System.out.println(event.getMember().getUser().getName() + " s'est déconnecté");
	}

	private void onConnect(GuildVoiceJoinEvent event) {
		XP.connecting(event);
		System.out.println(event.getMember().getUser().getName() + " s'est connecté");
	}

	private void onChangeServerName(GuildUpdateNameEvent event) {
		if (event.getGuild().getId().equals("382938797442334720"))
			Request.sendRequest(Request.Type.SETSEASON, event.getNewName());
	}

	private void onRequest(HttpRequestEvent event){
		System.out.println(event.getRequestRaw().toString());
	}
	private void onRemoveReact(MessageReactionRemoveEvent event) {
		if (event.getChannel().getId().equals("853210283480055809")) {
			for (MessageReact mr : MessageReact.message)
				if (event.getMessageId().equals(mr.getId())) {
					for (RoleReaction rr : mr.list) {
						if (event.getReactionEmote().getAsReactionCode().equals(rr.getEmote())) {
							event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById(rr.getId())).queue();
							return;
						}
					}
				}
		}
	}

	private void onDeleteRole(RoleDeleteEvent event) {
		for (MessageReact mr : MessageReact.message)
			for (RoleReaction rr : mr.list)
				if (rr.getId().equals(event.getRole().getId())) {
					mr.list.remove(rr);
					mr.refresh(event.getGuild());
				}
	}

	private void onCreateRole(RoleCreateEvent event) {
		new Thread(() -> {
			for (MessageReact mr : MessageReact.message)
				mr.refresh(event.getJDA().getGuildById("382938797442334720"));
		}).start();
		if (!event.getRole().getName().startsWith("©◊ß"))
			event.getRole().delete().queue();
		else
			event.getRole().getManager().setName(event.getRole().getName().replace("©◊ß", "")).queue();
	}

	/**
	 * evenement quand un Embed est posté
	 *
	 * @param event
	 * @throws InterruptedException
	 */
	private void onEmbed(MessageEmbedEvent event) throws InterruptedException {
		if (event.getChannel().getId().equals("461606547064356864")) {
			Message ret;
			Message msg = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
			if (DetectorAttachment.isReddit(msg.getContentRaw()))
				ret = memesEvent.repostReddit(msg, null, msg.getTextChannel());
			else if (DetectorAttachment.isTwitter(msg.getContentRaw()))
				ret = memesEvent.repostTwitter(msg, null, msg.getTextChannel());
			else
				return;
			ret.addReaction(ret.getGuild().getEmoteById(Emotes.rirederoite)).and(ret.addReaction(msg.getGuild().getEmoteById(Emotes.anto))).and(ret.addReaction(msg.getGuild().getEmoteById(Emotes.porte))).queue();
			msg.delete().queue();
		}
	}

	/**
	 * evenement quand discord cherche si le bot existe encore
	 * @param event
	 */
	private void onPing(GatewayPingEvent event) {
		long time = System.currentTimeMillis();
		if (PollMessage.pollMessage == null)
			return;
		for (PollMessage pm : PollMessage.pollMessage.values())
			if (pm.getTime() + 86400000 < time)
				pm.unactive(event.getJDA());
	}

	private void onSelectMenu(SelectMenuInteractionEvent event) {
		if (!event.getMessage().getEmbeds().isEmpty() && event.getMessage().getEmbeds().get(0).getAuthor() != null && event.getMessage().getEmbeds().get(0).getAuthor().getName().equals("poll")) {
			PollMessage pm = PollMessage.pollMessage.get(event.getMessageId());
			pm.newVote(event.getUser(), event.getSelectedOptions().get(0).getLabel());
			event.getMessage().editMessageEmbeds(pm.getMessageEmbed(event.getGuild())).queue();
			event.reply("Ton vote a été enregistré \uD83D\uDC4D").setEphemeral(true).queue();
		}
		if (event.getComponent().getId().equals("cki")) {
			MenuInteract(event.getSelectedOptions().get(0).getValue(), event.getMessage());
		}
		if (event.getComponent().getId().equals("role")) {
			String[] args = event.getMessage().getEmbeds().get(0).getDescription().split("\n");
			for (MessageReact mr : MessageReact.message)
				if (mr.getTitle().equals(event.getSelectedOptions().get(0).getLabel())) {
					mr.addRole(new RoleReaction(args[0], event.getMessage().getEmbeds().get(0).getFooter().getText(), args[1]), event.getGuild());
					MessageReact.save();
				}
			event.getMessage().delete().queue();
		}
	}

	/**
	 * au lancement
	 *
	 * @param event
	 */
	private void onEnable(ReadyEvent event) throws IOException {
		if (new File("save").mkdir())
			System.out.println("création du directoryCentral");
		Request.sendRequest(Request.Type.SETSEASON, event.getJDA().getGuildById("382938797442334720").getName());
		//MessageReact.load();
		PollMessage.load();
		cki.load();
		XP.loadValue();
		XP.load();
		if (System.getenv().get("OS") != null && !System.getenv().get("OS").equals("Windows_NT"))
		{
			for (MessageReact mr : MessageReact.message)
				mr.refresh(event.getJDA().getGuildById("382938797442334720"));
			new Thread(() -> {
				for (Member mem : event.getJDA().getGuildById("382938797442334720").getMembers()) {
					if (mem.getRoles().get(0).getPosition() < mem.getGuild().getRoleById("734011696242360331").getPosition())
						event.getJDA().getGuildById("382938797442334720").addRoleToMember(mem, event.getJDA().getGuildById("382938797442334720").getRoleById("734011696242360331")).and(event.getJDA().getGuildById("382938797442334720").addRoleToMember(mem, event.getJDA().getGuildById("382938797442334720").getRoleById("634839000644845619"))).and(event.getJDA().getGuildById("382938797442334720").addRoleToMember(mem, event.getJDA().getGuildById("382938797442334720").getRoleById("734012661494317077"))).queue();
				}
			}).start();
			new Thread(() -> recupMeme(event.getJDA().getGuildById("382938797442334720"))).start();
		}
		new Thread(() -> recupMeme(event.getJDA().getGuildById("382938797442334720"))).start();
		Pokemon.pok = gson.fromJson(new BufferedReader(new InputStreamReader(BotListener.class.getClassLoader().getResourceAsStream("pokemons.json"))), new TypeToken<Collection<Pokemon>>() {
		}.getType());

		Champion.champions = gson.fromJson(new BufferedReader(new InputStreamReader(BotListener.class.getClassLoader().getResourceAsStream("Champions.json"))), new TypeToken<HashMap<String, Champion>>() {
		}.getType());

		Attacks.capa = gson.fromJson(new BufferedReader(new InputStreamReader(BotListener.class.getClassLoader().getResourceAsStream("attacks.json"))), new TypeToken<Collection<Attacks>>() {
		}.getType());

		Activity act;
		act = new activity(", si tu lis ça tu es cringe", null, Activity.ActivityType.LISTENING);
		bot.getJda().getPresence().setActivity(act);
	}

	/**
	 * sert à faire une vérification du salon meme
	 * @param g
	 */
	private void recupMeme(Guild g) {
		TextChannel tc = g.getTextChannelById("461606547064356864");
		List<Message> msgs = tc.getHistory().retrievePast(100).complete();
		int i = 0;
		while(i < 100)
		{
			if (!msgs.get(i).getAuthor().isBot() && DetectorAttachment.isAnyLink(msgs.get(i)))
			{
				try {
					memesEvent.postmeme(msgs.get(i));
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else if (!msgs.get(i).getAuthor().isBot())
				msgs.get(i).delete().queue();
			i++;
		}
	}

	/**
	 * si une nouvelle personne a rejoint
	 *
	 * @param event
	 */
	private void onGuildMemberJoin(GuildMemberJoinEvent event) {
		event.getGuild().getTextChannelById("687244482739044370").sendMessage(event.getUser().getAsMention() + " a rejoint la Guild.").queue();
		event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("734011696242360331")).and(event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("634839000644845619"))).and(event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("734012661494317077"))).queue();
	}

	/**
	 * si une personne nous as quitté
	 *
	 */
	private void onGuildMemberLeave(GuildMemberRemoveEvent event) {
		event.getGuild().getTextChannelById("687244482739044370").sendMessage(event.getUser().getAsMention() + " a quitté la Guild.").queue();
	}

	/**
	 * si une personne ajoute une réaction
	 *
	 */
	private void onAddReact(MessageReactionAddEvent event) throws NoSuchFieldException, IllegalAccessException {
		if (event.getChannel().getId().equals("461606547064356864")) {
			memesEvent.addReaction(event.getChannel().retrieveMessageById(event.getMessageId()).complete(), event.getReaction());
		}
		if (event.getChannel().getId().equals("853210283480055809")) {
			for (MessageReact mr : MessageReact.message)
				if (event.getMessageId().equals(mr.getId())) {
					for (RoleReaction rr : mr.list) {
						if (event.getReactionEmote().getAsReactionCode().equals(rr.getEmote())) {
							event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(rr.getId())).queue();
							return;
						}
					}
				}
		}
		MusicCommand.onAddReact(event);
	}



	/**
	 * à la recption d'un message
	 *
	 */
	private void onMessage(MessageReceivedEvent event) throws IOException, InterruptedException {
		if (event.getAuthor().equals(event.getJDA().getSelfUser())) return;
		Message msg = event.getMessage();
		if (msg.getMentionedMembers().contains(msg.getGuild().getMemberById(event.getJDA().getSelfUser().getId())))
			msg.getChannel().sendMessage("Hé oh t'es qui a me ping, tu veux te battre ?\nfais un ping everyone pendant que t'y est").queue();
		if (msg.getContentRaw().startsWith(CommandMap.getTag())) {
			commandMap.commandUser(msg.getContentRaw().replaceFirst(CommandMap.getTag(), ""), event.getMessage());
			return;
		}
		if (cki.wtpThreads.containsKey(msg.getChannel().getId())) {
			new ckiListener(msg, cki.wtpThreads.get(msg.getChannel().getId()));
		}


		//split le message
		String[] args = msg.getContentRaw().split(" ");
		XP.getXpByMessage(msg.getContentRaw(), msg.getMember());

		if (MemberReact(msg))
			return;
		
		//tous les events mis sans le prefix les reactions en gros
		if (args[0].equalsIgnoreCase("f")) {
			pressf(msg);
		}

		if (msg.getContentRaw().equalsIgnoreCase("ping"))
		{
			long time = System.currentTimeMillis();
			msg.getChannel().sendMessage("pong").complete().editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
		}

		if (startWithIgnoreCase(msg.getContentRaw(),"AAA") && containsIgnoreCase(msg.getContentRaw(), "AHH")){
			rage(msg);
		}

		if (containsIgnoreCase(msg.getContentRaw(), "nice")) {
			if (!DetectorAttachment.isAnyLink(msg)) {
				try {
					nice(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		String[] s = msg.getContentRaw().replace("?","").replace(".","").split(" ");
		if (containsIgnoreCase(s[s.length - 1], "quoi" )){
			feur(msg);
		}

		if (msg.getContentRaw().equals("rip"))
			rip(msg, msg.getMember().getUser().getAvatarUrl());

		if (containsIgnoreCase(msg.getContentRaw().replace('é', 'e'), "societer"))
			msg.getChannel().sendFile(imgExtenders.getFile("societer.png")).queue();
		if (containsIgnoreCase(msg.getContentRaw(), "putain")) {
			putain(msg);
		}

		if (containsIgnoreCase(msg.getContentRaw(), "hmm")){
			for (String split : msg.getContentRaw().split(" "))
				if (StringExtenders.startWithIgnoreCase(split,"hmm"))
					hmm(msg, split);
		}

		if (containsIgnoreCase(msg.getContentRaw(), "je lag"))
		{
			File f = imgExtenders.getFile("internet.png");
			msg.getChannel().sendFile(f).queue();
			f.delete();
		}

		if (containsIgnoreCase(msg.getContentRaw(), "cringe")) {
			if (!DetectorAttachment.isAnyLink(msg))
				msg.getTextChannel().sendMessage("https://tenor.com/view/oh-no-cringe-cringe-oh-no-kimo-kimmo-gif-23168319").queue();
		}
		if (msg.getChannel().getId().equals("461606547064356864"))
			new Thread(() -> {
					postmeme(msg);
			}).start();

		if (containsIgnoreCase(msg.getContentRaw(), "stonks")) {
			if (new Random().nextInt(100) >= 95)
			{
				BufferedImage bi = imgExtenders.getImage("not stonks.png");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(bi, "gif", baos);
				msg.getChannel().sendFile(baos.toByteArray(), "not stonks.png").queue();
				return;
			}
			File f = imgExtenders.getFile("stonks.gif");
			msg.getChannel().sendFile(f).queue();
		}

		if (startWithIgnoreCase(msg.getContentRaw(), "daronned") || startWithIgnoreCase(msg.getContentRaw(), "daronné"))
			daronned(msg);

		if (msg.getContentRaw().equalsIgnoreCase("saint pierre"))
		{
			int r = new Random().nextInt(2);
			if (r == 1)
				msg.getChannel().sendMessage("cret-en-belledonne, nuance.\nsi ça se trouve les habitants sont les crétins").queue();
			if (r == 0)
				msg.getChannel().sendMessage("village préféré de mon conseillé principal, il a souvent des idées de merde et des blagues beauf\nmais bon on l'aime quand meme").queue();
		}
		if (msg.getContentRaw().equalsIgnoreCase("allevard"))
		{
			int r = new Random().nextInt(2);
			if (r == 1)
				msg.getChannel().sendMessage("village de dealers").queue();
			if (r == 0)
				msg.getChannel().sendMessage("village natal de mon créateur, il est un peu tete en l'air mais ça va").queue();
		}
		if (containsIgnoreCase(msg.getContentRaw(), "michel"))
			msg.getChannel().sendMessage( msg.getGuild().getMemberById("282859044593598464").getAsMention()+ ", eh oh je crois qu'on parle de toi").queue();
		if (containsIgnoreCase(msg.getContentRaw(), "shadow hunter"))
			msg.getChannel().sendMessage("*shadow **in**ter*").reference(msg).queue();
		if (msg.getContentRaw().equalsIgnoreCase("creeper"))
			msg.getChannel().sendMessage("Aww man").queue();
		if (msg.getContentRaw().equalsIgnoreCase("i am a dwarf"))
			msg.getChannel().sendMessage("and I'm digging a hole").queue();
		if (msg.getContentRaw().equalsIgnoreCase("je suis un nain"))
			msg.getChannel().sendMessage("et je creuse un gros trou").queue();
		if (msg.getContentRaw().equalsIgnoreCase("mové salon"))
		{
			if (msg.getMember().getId().equals("280959408119349248"))
				{
				msg.getChannel().sendMessage("Franchement si j'étais vous **je me tairais**").queue();
					return;
				}
			msg.getChannel().sendMessage("eh oh tu t'es pris pour Logan ou quoi ? \n**MDR**").queue();
		}

		if (containsIgnoreCase(msg.getContentRaw(), "je possede des thunes") || containsIgnoreCase(msg.getContentRaw(), "je possède des thunes"))
			msg.getChannel().sendMessage(msg.getMember().getAsMention() + " est à l'aise financièrement").queue();

	}

	public static class ArrayNumber<E extends Number> {
		public ArrayList<E> li = new ArrayList<>();

		public ArrayNumber() {
		}

		public ArrayNumber(E element) {
			li.add(element);
		}

		@Override
		public String toString() {
			return li.toString();
		}
	}

}
