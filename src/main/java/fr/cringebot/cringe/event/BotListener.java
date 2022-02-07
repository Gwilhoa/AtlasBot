/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   BotListener.java                                   :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 11:45:58 by gchatain          #+#    #+#             */
/*   Updated: 2022/02/07 00:43:59 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */


package fr.cringebot.cringe.event;

import static fr.cringebot.cringe.event.ReactionEvent.feur;
import static fr.cringebot.cringe.event.ReactionEvent.hmm;
import static fr.cringebot.cringe.event.ReactionEvent.nice;
import static fr.cringebot.cringe.event.ReactionEvent.pressf;
import static fr.cringebot.cringe.event.ReactionEvent.putain;
import static fr.cringebot.cringe.event.ReactionEvent.rage;
import static fr.cringebot.cringe.event.ReactionEvent.daronned;
import static fr.cringebot.cringe.event.memesEvent.postmeme;
import static fr.cringebot.cringe.objects.StringExtenders.containsIgnoreCase;
import static fr.cringebot.cringe.objects.StringExtenders.startWithIgnoreCase;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;

import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.objects.DetectorAttachment;
import fr.cringebot.cringe.objects.Emotes;
import fr.cringebot.cringe.objects.PollMessage;
import fr.cringebot.cringe.objects.StringExtenders;
import fr.cringebot.cringe.objects.activity;
import fr.cringebot.cringe.objects.imgExtenders;
import fr.cringebot.cringe.objects.lol.Champion;
import fr.cringebot.cringe.pokemon.objects.Attacks;
import fr.cringebot.cringe.pokemon.objects.Pokemon;
import fr.cringebot.cringe.pokemon.objects.wtp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GatewayPingEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageEmbedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.managers.channel.ChannelManager;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.managers.channel.ChannelManagerImpl;
import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.static_data.constant.ChampionTags;
import net.rithms.riot.api.endpoints.static_data.constant.Locale;
import net.rithms.riot.constant.Platform;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
	/**
	 * à chaque event fonction qui sert d'aiguillage vers les fonction visée
	 */
	public void onEvent(GenericEvent event) {
		System.out.println(event.getClass().getSimpleName());
		if (event instanceof ReadyEvent) onEnable((ReadyEvent) event);
		if (event instanceof MessageReceivedEvent) {
			try {
				onMessage((MessageReceivedEvent) event);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		} else if (event instanceof GuildMemberJoinEvent) onGuildMemberJoin((GuildMemberJoinEvent) event);
		else if (event instanceof GuildMemberRemoveEvent) onGuildMemberLeave((GuildMemberRemoveEvent) event);
		else if (event instanceof MessageReactionAddEvent) onAddReact((MessageReactionAddEvent) event);
		else if (event instanceof SelectMenuInteractionEvent) onSelectMenu((SelectMenuInteractionEvent) event);
		else if (event instanceof GatewayPingEvent) onPing((GatewayPingEvent) event);
		else if (event instanceof MessageEmbedEvent) {
			try {
				onEmbed((MessageEmbedEvent) event);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * evenement quand un Embed est posté
	 * @param event
	 * @throws InterruptedException
	 */
	private void onEmbed(MessageEmbedEvent event) throws InterruptedException {
		if (event.getChannel().getId().equals("461606547064356864")) {
			Message msg = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
			if (DetectorAttachment.isReddit(msg.getContentRaw()))
				msg = memesEvent.repostReddit(msg);
			else if (DetectorAttachment.isTwitter(msg.getContentRaw()))
				msg = memesEvent.repostTwitter(msg);
			else
				return;
			msg.addReaction(msg.getGuild().getEmoteById(Emotes.rirederoite)).queue();
			msg.addReaction(msg.getGuild().getEmoteById(Emotes.anto)).queue();
			msg.addReaction(msg.getGuild().getEmoteById(Emotes.porte)).queue();
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
		if (!event.getMessage().getEmbeds().isEmpty() && event.getMessage().getEmbeds().get(0).getAuthor().getName().equals("poll")) {
			PollMessage pm = PollMessage.pollMessage.get(event.getMessageId());
			pm.newVote(event.getUser(), event.getSelectedOptions().get(0).getLabel());
			event.getMessage().editMessageEmbeds(pm.getMessageEmbed(event.getGuild())).queue();
			event.reply("ton vote a été enregistré \uD83D\uDC4D").setEphemeral(true).queue();
		}
	}

	/**
	 * au lancement
	 *
	 * @param event
	 */
	private void onEnable(ReadyEvent event) {
		new Thread(() -> {
		recupMeme(event.getJDA().getGuildById("382938797442334720"));
		}).start();
		Pokemon.pok = gson.fromJson(new BufferedReader(new InputStreamReader(BotListener.class.getClassLoader().getResourceAsStream("pokemons.json"))), new TypeToken<Collection<Pokemon>>() {
		}.getType());

		//Champion.champions = gson.fromJson(new BufferedReader(new InputStreamReader(BotListener.class.getClassLoader().getResourceAsStream("Champions.json"))), new TypeToken<Collection<Champion>>() {
		//}.getType());

		Attacks.capa = gson.fromJson(new BufferedReader(new InputStreamReader(BotListener.class.getClassLoader().getResourceAsStream("attacks.json"))), new TypeToken<Collection<Attacks>>() {
		}.getType());

		Activity act;
		if (System.getenv().get("OS").equals("Windows_NT")) {
			act = new activity("se faire retaper", null, Activity.ActivityType.PLAYING);
			bot.getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
		}
		else
		{
			act = new activity(", si tu lis ça tu es cringe", null, Activity.ActivityType.LISTENING);
			bot.getJda().getGuildById("382938797442334720").getTextChannelById("687244482739044370").sendMessage("Mise a jour effectué le patchnote sera dans annonces bot\nsi il y est pas encore, il va pas tarder").queue();
		}
		bot.getJda().getPresence().setActivity(act);
		if (new File("save").mkdir())
			System.out.println("création du directoryCentral");
		PollMessage.load();
		wtp.load();
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
			tc.sendTyping();
			if (!msgs.get(i).getAuthor().isBot() && DetectorAttachment.isAnyLink(msgs.get(i)))
			{
				try {
					memesEvent.postmeme(msgs.get(i));
					Thread.sleep(5000);
				} catch (IOException | InterruptedException e) {
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
	private void onAddReact(MessageReactionAddEvent event) {
		if (event.getChannel().getId().equals("461606547064356864")) {
			memesEvent.addReaction(event.getChannel().retrieveMessageById(event.getMessageId()).complete(), event.getReaction());
		}
	}



	/**
	 * à la recption d'un message
	 *
	 */
	private void onMessage(MessageReceivedEvent event) throws IOException, InterruptedException {
		if (event.getAuthor().equals(event.getJDA().getSelfUser())) return;
		Message msg = event.getMessage();
		if (msg.getContentRaw().startsWith(CommandMap.getTag())) {
			commandMap.commandUser(msg.getContentRaw().replaceFirst(CommandMap.getTag(), ""), event.getMessage());
			return;
		}
		if (wtp.wtpThreads.containsKey(msg.getChannel().getId()))
		{
			wtp pok = wtp.wtpThreads.get(msg.getChannel().getId());
			if (msg.getContentRaw().equalsIgnoreCase("indice"))
			{
				if (msg.getChannel().getName().equals("quel est ce pokemon ?"))
				{
					StringBuilder sb = new StringBuilder();
					sb.append(pok.getName().charAt(0));
					int	i = 1;
					while (i++ < pok.getName().length())
						sb.append(" _ ");
					msg.getGuildChannel().getManager().setName(sb.toString()).queue();
				}
				else if (pok.getIndice() == 2)
				{
					Document doc = Jsoup.connect("https://pokemondb.net/pokedex/"+ Pokemon.getByRealName(pok.getName()).getName()).get();
					String region = doc.selectXpath("/html[1]/body[1]/main[1]/div[1]/div[1]/p[1]/abbr[1]").text();
					System.out.println(region);
					msg.getChannel().sendMessage(region).queue();
				}
				else if (pok.getIndice() == 1)
				{
					msg.getChannel().sendMessage("type du pokémon : " + Pokemon.getByRealName(pok.getName()).getType()).queue();
				}
				pok.addIndice();
			}
			else if (wtp.wtpThreads.get(msg.getChannel().getId()).getName().replace('ï', 'i').replace('ô', 'o').replace('é', 'e').replace('è', 'e').replace('ç', 'c').replace('É', 'e').equalsIgnoreCase(msg.getContentRaw().replace('ï', 'i').replace('ô', 'o').replace('é', 'e').replace('è', 'e').replace('ç', 'c').replace('É', 'e')))
			{
				ThreadChannel tc = msg.getGuild().getThreadChannelById(msg.getChannel().getId());
				EmbedBuilder eb = new EmbedBuilder().setTitle("pokémon trouvé").setImage("https://assets.pokemon.com/assets/cms2/img/pokedex/detail/" + String.format("%03d", Pokemon.getByRealName(wtp.wtpThreads.get(msg.getChannel().getId()).getName()).getId()) + ".png");
				eb.setFooter("trouvé par "+ msg.getAuthor().getName(), msg.getAuthor().getEffectiveAvatarUrl());
				eb.setColor(Color.green);
				sendresponse(msg, tc, eb);
				wtp.save();
			}
			else if (msg.getContentRaw().equalsIgnoreCase("abandon"))
			{
				if (pok.getAction() < 5)
				{
					msg.getChannel().sendMessage("c'est trop tôt pour abandonner, continue !").queue();
					return;
				}
				ThreadChannel tc = msg.getGuild().getThreadChannelById(msg.getChannel().getId());
				EmbedBuilder eb = new EmbedBuilder().setTitle("Partie abandonné").setImage("https://assets.pokemon.com/assets/cms2/img/pokedex/detail/" + String.format("%03d", Pokemon.getByRealName(wtp.wtpThreads.get(msg.getChannel().getId()).getName()).getId()) + ".png");
				eb.setFooter("abandonné par "+ msg.getAuthor().getName(), msg.getAuthor().getEffectiveAvatarUrl());
				eb.setColor(new Color(255, 92, 243));
				sendresponse(msg, tc, eb);
				Message ed;
				wtp.save();
			}
			else
			{
				msg.getChannel().sendMessage("raté").reference(msg).queue();
				pok.addAction();
			}
		}


		//split le message
		String[] args = msg.getContentRaw().split(" ");

		if (msg.getContentRaw().equalsIgnoreCase("max"))
		{
			int r = new Random().nextInt(5);
			if (r == 0)
				msg.getChannel().sendMessage("la personne la plus calme et sereine que je connaisse \n\n*ou pas*").queue();
			if (r == 1)
				msg.getChannel().sendMessage("ROI DU SEL ou le chevalier").queue();
			if (r == 2)
				msg.getChannel().sendMessage("NaCl").queue();
			if (r == 3)
				msg.getChannel().sendMessage("un jour il sera calme, un jour...").queue();
			if (r == 4)
				msg.getChannel().sendMessage("ComputerSlayer").queue();
		}

		if (msg.getContentRaw().equalsIgnoreCase("roro"))
		{
			int r = new Random().nextInt(5);
			if (r == 0)
				msg.getChannel().sendMessage("shadow in the night").queue();
			if (r == 1)
				msg.getChannel().sendMessage("un jour il dira a Momo d'arreter de pomper la co").queue();
			if (r == 2)
				msg.getChannel().sendMessage("super koala de destruction massif").queue();
			if (r == 3)
				msg.getChannel().sendMessage("d'après ce qu'on m'a dit cette peronne est sacrée\ndonc j'en déduis qu'il est parlementaire").queue();
			if (r == 4)
				msg.getChannel().sendMessage("salut c'est toi, et moi\n moi c'est CringeBot et toi, c'est toi").queue();
		}

		
		if (msg.getContentRaw().equalsIgnoreCase("logan"))
		{
			int r = new Random().nextInt(4);
			if (r == 0)
				msg.getChannel().sendMessage("mais qu'est que c'est ?\nun avion ?\nun bastion ?\nune porte ?\n non c'est Logan").queue();
			if (r == 1)
				msg.getChannel().sendMessage("Grogan en action").queue();
			if (r == 2)
				msg.getChannel().sendMessage("créateur de la porte humaine").queue();
			if (r == 3)
				msg.getChannel().sendMessage("putain frérot arrete de post des mêmes j'en peux plus\nje suis pas venu ici pour souffrir").queue();
		}

		if (msg.getContentRaw().equalsIgnoreCase("guigui"))
		{
			int r = new Random().nextInt(5);
			if (r == 0)
				msg.getChannel().sendMessage("c'est quoi une vache ?").queue();
			if (r == 1)
				msg.getChannel().sendMessage("Roro :heart:").queue();
			if (r == 2)
				msg.getChannel().sendMessage("créateur de la méta Diablo backlane").queue();
			if (r == 3)
				msg.getChannel().sendMessage("Mon magnifique créateur, il est noir et PD,\nil a vraiment rien pour lui").queue();
			if (r == 4)
				msg.getChannel().sendMessage("chair slayer").queue();
		}

		if (msg.getContentRaw().equalsIgnoreCase("enki"))
		{
			int r = new Random().nextInt(6);
			if (r == 0)
				msg.getChannel().sendMessage("https://www.alcool-info-service.fr").queue();
			if (r == 1)
				msg.getChannel().sendMessage("https://www.pole-emploi.fr").queue();
			if (r == 2)
				msg.getChannel().sendMessage("https://www.caf.fr/").queue();
			if (r == 3)
				msg.getChannel().sendMessage("Enki, c'est mon hébergeur, il a chopé 2147483647 fois le covid\nil est parfois un peu con dans ses décisions").queue();
			if (r == 4)
				msg.getChannel().sendMessage("il a pas une carte fidelité burger king ?").queue();
			if (r == 5)
				msg.getChannel().sendMessage("je pense qu'il a déjà fait 4 fois le tour du monde en voiture").queue();
		}

		if (msg.getContentRaw().equalsIgnoreCase("oscar"))
		{
			int r = new Random().nextInt(5);
			if (r == 0)
				msg.getChannel().sendMessage("TRACTEUR VROUM VROUM !!!!!").queue();
			if (r == 1)
				msg.getChannel().sendMessage("vitesse moyenne en montagne : 250km/h").queue();
			if (r == 2)
				msg.getChannel().sendMessage("aparemment le porc ça le connait").queue();
			if (r == 3)
				msg.getChannel().sendMessage("hmmm, de ce qu'on m'a dis il aime bien boire, et il vole des panneaux").queue();
			if (r == 4)
				msg.getChannel().sendMessage("aparemment il sait qui gagne entre un tracteur et un mur").queue();
		}


		if (msg.getContentRaw().equalsIgnoreCase("Antonin"))
		{
			int r = new Random().nextInt(8);
			if (r == 0)
				msg.getChannel().sendMessage("Male alpha").queue();
			if (r == 1)
				msg.getChannel().sendMessage("**ahou ahou**").queue();
			if (r == 2)
				msg.getChannel().sendMessage("de ce qu'on m'a dit c'est qu'il est toujours plus fort que les autres car il est a fond\n*Un jour il se fera dépasser mais pas par enki*").queue();
			if (r == 3)
				msg.getChannel().sendMessage("Un homme musculeux !").queue();
			if (r == 4)
				msg.getChannel().sendMessage("Son deuxième prénom est discrétion !").queue();
			if (r == 5)
				msg.getChannel().sendMessage("le pussy slayer par excellence !").queue();
			if (r == 6)
				msg.getChannel().sendMessage("***Errape.mp3***").queue();
			if (r == 7)
				msg.getChannel().sendMessage("https://www.youtube.com/watch?v=DeumyOzKqgI").queue();
		}

		
		if (msg.getContentRaw().equalsIgnoreCase("jonathan"))
		{
			int r = new Random().nextInt(5);
			if (r == 0)
				msg.getChannel().sendMessage("Obsédé de saint pierre d'allevard").queue();
			if (r == 1)
				msg.getChannel().sendMessage("Qu'est ce qui est jaune et qui attends ?").queue();
			if (r == 2)
				msg.getChannel().sendMessage("De ce qu'on m'a dit il a un humour fin et raffinés et aussi un peu maigre et musculeux\n\nhmmmmm je crois que j'ai menti").queue();
			if (r == 3)
				msg.getChannel().sendFile(imgExtenders.getFile("jojo.png")).queue();
			if (r == 4)
				msg.getChannel().sendMessage("Un petit fortnite ?").queue();
		}

		

		if (msg.getContentRaw().equalsIgnoreCase("virgile"))
		{
			int r = new Random().nextInt(7);
			if (r == 0)
				msg.getChannel().sendMessage("swain lover").queue();
			if (r == 1)
				msg.getChannel().sendMessage("Starcraft II c'est mieux !").queue();
			if (r == 2)
				msg.getChannel().sendMessage("Starcraft I c'est bien !").queue();
			if (r == 3)
				msg.getChannel().sendMessage("hmmm un jour il saura que jouer n'est pas que swain et SCII\net oui mon coco").queue();
			if (r == 4)
				msg.getChannel().sendMessage("Starcraft III c'est plus fort que toi !").queue();
			if (r == 5)
				msg.getChannel().sendMessage("1 millions de points de maitrise sur swain mais toujours pas top 400 Eu West").queue();
			if (r == 6)
				msg.getChannel().sendMessage("you are going to mordor").queue();
		}



		if (msg.getContentRaw().equalsIgnoreCase("timthée"))
		{
			int r = new Random().nextInt(6);
			if (r == 0)
				msg.getChannel().sendFile(imgExtenders.getFile("timthee.png")).queue();
			if (r == 1)
				msg.getChannel().sendMessage("riz-volution lustucru").queue();
			if (r == 2)
				msg.getChannel().sendMessage("qu'est ce que que c'est que ces simagrées").queue();
			if (r == 3)
				msg.getChannel().sendMessage("on m'a dit, face à lui faut que je m'incline\nmême si je prend possession du monde je m'inclinerai face à cet homme").queue();
			if (r == 4)
				msg.getChannel().sendMessage("grand écrivain du grand récit de l'odyssey du bitume").queue();
			if (r == 5)
			msg.getChannel().sendMessage("Choisir Timthée comme Président de notre République en 2022\n"+
			"porteur d’une vision réformatrice universaliste et complète de notre pays aux multiples individualités et possédant une grande diversité.\n\n"+
			"Mon programme s’inscrit dans la ligne claire des grands réformateurs politiques de notre temps,\n"+
			"de Margaret Thatcher à Emmanuel Macron en passant par François Hollande, Jean-Yves le Drian ou encore Gérard Collomb.\n\n"+
			"Notre pays a besoin d’être poussé dans les réformes qui le porteront vers l’avenir.\n"+
			"Nous avons un passé de baroudeurs, de gaulois réfractaires, de gilets jaunes sur les rond-points et d’Amishs qui refusent le changement et l’avance vers le futur que nous souhaitons mener, au nom de principes dépassés et sclérosés.\n\n"+
			"Ces personnes ne veulent pas transformer la France\n"+
			"à l’heure des grands bouleversements sociétaux, écologiques et économiques qui frappent l’humanité et notre planète.\n\n"+
			"C’est pourquoi je vous propose à vous, françaises, français, de métropole, d’outre-mer et l’étranger\n"+
			"une liste de mesures ambitieuses et réformatrices, que nous voulons salvatrices et qui sont à l’attention du plus grand nombre, dans le but de ne laisser personne de côté.\n\n\nTimthée 2022").queue();

		}
		
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

		if (containsIgnoreCase(msg.getContentRaw(), "societer"))
			msg.getChannel().sendFile(imgExtenders.getFile("societer.png")).queue();
		if (containsIgnoreCase(msg.getContentRaw(), "putain")){
			putain(msg);
		}

		if (containsIgnoreCase(msg.getContentRaw(), "hmm")){
			for (String split : msg.getContentRaw().split(" "))
				if (StringExtenders.startWithIgnoreCase(split,"hmm"))
					hmm(msg, split);
		}

		if (containsIgnoreCase(msg.getContentRaw(), "cringe")) {
			if (!DetectorAttachment.isAnyLink(msg))
				msg.getTextChannel().sendMessage("https://tenor.com/view/oh-no-cringe-cringe-oh-no-kimo-kimmo-gif-23168319").queue();
		}
		if (msg.getChannel().getId().equals("461606547064356864") && (DetectorAttachment.isAnyLink(msg)))
			postmeme(msg);

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
			msg.getChannel().sendMessage( msg.getMember().getAsMention() + " est à l'aise financièrement").queue();

	}

	private void sendresponse(Message msg, ThreadChannel tc, EmbedBuilder eb) {
		Message ed = msg.getGuild().getTextChannelById(tc.getParentChannel().getId()).retrieveMessageById(wtp.wtpThreads.get(msg.getChannel().getId()).getMessage()).complete();
		eb.setDescription(ed.getEmbeds().get(0).getDescription().replace("<ce pokémon>", wtp.wtpThreads.get(msg.getChannel().getId()).getName()) + "\n\nle pokémon était : " + wtp.wtpThreads.get(msg.getChannel().getId()).getName() + "\n\nNombre d'échec : " + wtp.wtpThreads.get(msg.getChannel().getId()).getAction() + "\nNombre d'indice utilisé : " + wtp.wtpThreads.get(msg.getChannel().getId()).getIndice());
		wtp.wtpThreads.remove(msg.getChannel().getId());
		msg.getChannel().delete().queue();
		msg.getChannel().sendMessage(ed.getId()).queue();
		ed.editMessageEmbeds(eb.build()).queue();
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
