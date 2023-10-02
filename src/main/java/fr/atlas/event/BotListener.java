/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   BotListener.java                                   :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 11:45:58 by gchatain          #+#    #+#             */
/*   Updated: 2022/12/10 23:56:01 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */


package fr.atlas.event;

import com.diogonunes.jcolor.Attribute;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import fr.atlas.BotDiscord;
import fr.atlas.Request.*;
import fr.atlas.Request.User;
import fr.atlas.builder.CommandMap;
import fr.atlas.objects.StringExtenders;
import fr.atlas.experiences.TextualExperience;
import fr.atlas.objects.imgExtenders;
import fr.atlas.music.MusicPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GatewayPingEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageEmbedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.diogonunes.jcolor.Ansi.colorize;
import static fr.atlas.BotDiscord.setError;
import static fr.atlas.event.MemesEvent.*;


/**
 * capture tout les evenements du bot
 */
public class BotListener implements EventListener {

	private final CommandMap commandMap;
	private final BotDiscord bot;
	private final HashMap<String, Long> connectedUsers;


	public BotListener(CommandMap cmd, BotDiscord bot) {
		this.commandMap = cmd;
		this.bot = bot;
		this.connectedUsers = new HashMap<>();
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



	public static Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(ArrayNumber.class,
			(JsonDeserializer<ArrayNumber<Integer>>) (jsonElement, type, jsonDeserializationContext) -> {
				if (!jsonElement.isJsonArray())
					return new ArrayNumber<>(jsonElement.getAsNumber().intValue());
				ArrayNumber<Integer> n = new ArrayNumber<>();
				JsonArray ar = jsonElement.getAsJsonArray();
				for (int i = 0; i < ar.size(); i++)
					n.li.add(ar.get(i).getAsNumber().intValue());
				return n;
			}).create();


	@Override
	public void onEvent(GenericEvent event) {
		System.out.println(colorize( "[Event] "+ event.getClass().getSimpleName(), Attribute.TEXT_COLOR(169, 169, 169)));
		try {
			if (event instanceof ReadyEvent) onEnable((ReadyEvent) event);
			else if (event instanceof StringSelectInteractionEvent) onMenuClick((StringSelectInteractionEvent) event);
			else if (event instanceof GuildUpdateNameEvent) onUpdateNameEvent((GuildUpdateNameEvent) event);
			else if (event instanceof MessageReceivedEvent) onMessage((MessageReceivedEvent) event);
			else if (event instanceof GuildMemberJoinEvent) onGuildMemberJoin((GuildMemberJoinEvent) event);
			else if (event instanceof GuildMemberRemoveEvent) onGuildMemberLeave((GuildMemberRemoveEvent) event);
			else if (event instanceof MessageReactionAddEvent) onAddReact((MessageReactionAddEvent) event);
			else if (event instanceof GatewayPingEvent) onPing((GatewayPingEvent) event);
			else if (event instanceof RoleCreateEvent) onCreateRole((RoleCreateEvent) event);
			else if (event instanceof RoleDeleteEvent) onDeleteRole((RoleDeleteEvent) event);
			else if (event instanceof MessageReactionRemoveEvent) onRemoveReact((MessageReactionRemoveEvent) event);
			else if (event instanceof GuildVoiceUpdateEvent) onVoiceUpdate((GuildVoiceUpdateEvent) event);
//			else if (event instanceof GuildMemberJoinEvent) onConnect((GuildVoiceJoinEvent) event);
//			else if (event instanceof GuildVoiceLeaveEvent) onDisconnect((GuildVoiceLeaveEvent) event);
			else if (event instanceof MessageEmbedEvent) onEmbed((MessageEmbedEvent) event);
//			else if (event instanceof GuildVoiceMoveEvent) onMove((GuildVoiceMoveEvent) event);
//			else if (event instanceof SlashCommandInteraction) onSlashCommand((SlashCommandInteraction) event);
			else if (event instanceof CommandAutoCompleteInteraction) onAutoComplete((CommandAutoCompleteInteraction) event);
//			else if (event instanceof SelectMenuInteractionEvent) onSelectMenu((SelectMenuInteractionEvent) event);
			else if (event instanceof ButtonInteractionEvent) onButton((ButtonInteractionEvent) event);
			else if (event instanceof GuildMemberRoleAddEvent) onRoleAdd((GuildMemberRoleAddEvent) event);
			else if (event instanceof MessageDeleteEvent) onDeleteMessage((MessageDeleteEvent) event);
		} catch (IOException | InterruptedException | IllegalAccessException | NoSuchFieldException e) {
			setError(e);
			event.getJDA().getGuilds().get(0).getMemberById("315431392789921793").getUser().openPrivateChannel().complete().sendMessage("erreur sur " + event.getClass().getSimpleName()).queue();
		}
	}

	private void onMenuClick(StringSelectInteractionEvent event) throws IOException {
		if (!event.getMember().getId().equals(event.getValues().get(0).split(";")[1]))
		{
			event.reply("Vous n'avez pas le droit de faire ça \nJe parles à votre camarade, pas vous").setEphemeral(true).queue();
		}
		else
		{
			Item item = Item.getItemById(event.getValues().get(0).split(";")[0]);
			String quote = item.getName() + " ? bon choix !, combien en voulez vous ?";
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Acheter");
			eb.setDescription( 1 + "x " + item.getName() + " pour " + item.getPrice() + " B2C");
			ArrayList<Button> buttons = new ArrayList<>();
			buttons.add(Button.danger("buy;"+event.getMember().getId() + ";" + item.getId() + ";-1", "-1").asDisabled());
			buttons.add(Button.success("buy;"+event.getMember().getId() + ";" + item.getId() + ";_1", "acheter"));
			buttons.add(Button.primary("buy;"+event.getMember().getId() + ";" + item.getId() + ";2", "+1"));
			event.getMessage().editMessage(quote).setEmbeds(eb.build()).setActionRow(buttons).queue();
			event.reply(".").complete().deleteOriginal().queue();
		}
	}


	private void onUpdateNameEvent(GuildUpdateNameEvent event) throws IOException {
		General.setSeason(event.getNewName());
	}

	private void onVoiceUpdate(GuildVoiceUpdateEvent event) throws IOException {
		if (event.getOldValue() == null) {
			connectedUsers.put(event.getMember().getId(), System.currentTimeMillis());
		} else if (event.getNewValue() == null) {
			if (connectedUsers.get(event.getMember().getId()) != null && connectedUsers.get(event.getMember().getId()) + 86400000L < System.currentTimeMillis())
			{
				User.addAchievement(event.getMember().getId(), "9");
			}
			connectedUsers.remove(event.getMember().getId());
		} else {

		}
	}

	private void onDeleteMessage(MessageDeleteEvent event) {
	}

	private void onRoleAdd(GuildMemberRoleAddEvent event) throws IOException {
		Guild guild = event.getGuild();
		User mem = User.getMember(event.getMember());
		if (event.getRoles().contains(event.getGuild().getRoleById("680431143283458077")))
		{
			User.addAchievement(mem.getId(),"3");
		}
		if (mem.getMember(guild).getRoles().contains(guild.getRoleById("849925828069687296")))
		{
			User.addAchievement(mem.getId(), "2");
		}
	}

	private void onButton(ButtonInteractionEvent event) throws IOException {
		if (event.getComponentId().startsWith("squads")) {
			if (event.getMember().equals(event.getComponentId().split(";")[2])) {
				event.reply("ce message ne vous concernes pas").setEphemeral(true).queue();
			} else {
				event.getGuild().getTextChannelById("947564791759777792").sendMessage(event.getMember().getAsMention() + " a rejoint l'escouade "+ event.getGuild().getRoleById(event.getComponentId().split(";")[1]).getName()).queue();
				event.getGuild().retrieveMemberById(event.getComponentId().split(";")[2]).queue(member -> {
					event.getGuild().addRoleToMember(member, event.getGuild().getRoleById(event.getComponentId().split(";")[1])).queue();
				});
				event.reply("vous avez rejoint la squad").setEphemeral(true).queue();
				User.newMembers(event.getMember(), event.getComponentId().split(";")[1]);
			}
		} else if (event.getComponentId().substring(2).startsWith("memes")) {
			User user;
			try {
				user = User.getMember(event.getMember());
			} catch (IOException e) {
				setError(e);
				return;
			}
			if (false && getMemeAuthor(event.getMessage()).getId().equals(event.getMember().getId()))
			{
				event.reply("tu ne peux pas voter pour tes propres memes").setEphemeral(true).queue();
				return;
			}
			ArrayList<String> ids = new ArrayList<>(List.of(event.getComponentId().split(";")));
			if (ids.contains(event.getMember().getId())) {
				event.reply("tu as déjà voté ceci").setEphemeral(true).queue();
			} else {
				String bid = event.getMessage().getActionRows().get(0).getButtons().get(0).getId();
				String nid = event.getMessage().getActionRows().get(0).getButtons().get(1).getId();
				String gid = event.getMessage().getActionRows().get(0).getButtons().get(2).getId();
				String did = event.getMessage().getActionRows().get(0).getButtons().get(3).getId();
				ArrayList<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
				if (event.getComponentId().contains("b")) {
					bid = bid + ";" + event.getMember().getId();
					did = "d_memes;" + (Integer.parseInt(did.split(";")[1]) - 1);
				} else if (event.getComponentId().contains("n")) {
					nid = nid + ";" + event.getMember().getId();
					did = "d_memes;" + (Integer.parseInt(did.split(";")[1]) + 1);
				} else if (event.getComponentId().contains("g")) {
					if (user.getMemevote() <= 0) {
						event.reply("tu n'as plus de super vote").setEphemeral(true).queue();
						return;
					}
					user = User.addMemeVote(event.getMember().getId());
					gid = gid + ";" + event.getMember().getId();
					did = "d_memes;" + (Integer.parseInt(did.split(";")[1]) + 2);
				}
				buttons.add(Button.danger(bid, DownvoteLabel));
				buttons.add(Button.success(nid, UpvoteLabel));
				buttons.add(Button.primary(gid, supervoteLabel));
				buttons.add(Button.danger(did, MemesEvent.res(Integer.parseInt(did.split(";")[1]))).asDisabled());
				event.getMessage().editMessageComponents(ActionRow.of(buttons)).queue();
				event.reply("tu as bien voté pour ce meme\n il te reste " + user.getMemevote() + " super vote").setEphemeral(true).queue();
				if (Integer.parseInt(did.split(";")[1]) >= UpvoteRequired * 2) {
					Message message = event.getMessage();
					if (message.getEmbeds().isEmpty()) {
						File f = imgExtenders.getFile(message.getAttachments().get(0).getProxyUrl(), message.getAttachments().get(0).getFileName(), null);
						message.getGuild().getTextChannelById("911549374696411156").sendMessage(MessageCreateData.fromMessage(message)).setFiles(FileUpload.fromData(f)).queue();
						f.delete();
						User.addBestMemes(getMemeAuthor(message));
						message.delete().queue();
					} else {
						if (message.getEmbeds().get(0).getAuthor() != null && message.getEmbeds().get(0).getAuthor().getName().equalsIgnoreCase("reddit")) {
							repostReddit(message, null, message.getGuild().getTextChannelById("911549374696411156"));
							User.addBestMemes(getMemeAuthor(message));
							message.delete().queue();
						}
						else if (message.getEmbeds().get(0).getAuthor() != null && message.getEmbeds().get(0).getAuthor().getName().equalsIgnoreCase("twitter")) {
							repostTwitter(message, null, message.getGuild().getTextChannelById("911549374696411156"));
							User.addBestMemes(getMemeAuthor(message));
							message.delete().queue();
						}
						else if (!message.getContentRaw().isEmpty()) {
							message.getGuild().getTextChannelById("911549374696411156").sendMessage(message.getContentRaw()).queue();
							User.addBestMemes(getMemeAuthor(message));
							message.delete().queue();
						} else {
							String name = message.getEmbeds().get(0).getImage().getUrl().split(" ")[0].split("/")[message.getEmbeds().get(0).getImage().getUrl().split("/").length - 1];
							name = name.substring(0, StringExtenders.firstsearch(name, "?"));
							try (BufferedInputStream bis = new BufferedInputStream(new URL(message.getEmbeds().get(0).getImage().getUrl()).openStream());
								 FileOutputStream fos = new FileOutputStream(name)) {
								byte[] data = new byte[1024];
								int byteContent;
								while ((byteContent = bis.read(data, 0, 1024)) != -1) {
									fos.write(data, 0, byteContent);
								}
							} catch (IOException e) {
								setError(e);
							}
							File f = new File(name);
							message.getGuild().getTextChannelById("911549374696411156").sendFiles(FileUpload.fromData(f)).setEmbeds(
									new EmbedBuilder()
											.setDescription(message.getEmbeds().get(0).getDescription())
											.setImage("attachment://" + f.getName())
											.setFooter(message.getEmbeds().get(0).getFooter().getText(), message.getEmbeds().get(0).getFooter().getIconUrl())
											.setColor(Color.GREEN)
											.build()
							).queue();
							f.delete();
							User.addBestMemes(getMemeAuthor(message));
							message.delete().queue();
							return;
						}
					}
				}
			}
		}
		else if (event.getComponentId().startsWith("waifu")) {
			int page = Integer.parseInt(event.getComponentId().split(";")[2]);
			if (event.getComponentId().contains("next")) {
				page++;
			} else if (event.getComponentId().contains("prev")) {
				page--;
			}
			ArrayList<Button> buttons = new ArrayList<>();
			ArrayList<MessageEmbed> embeds = new ArrayList<>();
			List<Waifu> waifus = Waifu.getWaifus();
			if (page != 0)
				buttons.add(Button.primary("waifu;prev;" + page, "page précédents"));
			int i = page * 5;
			while (i < page * 5 + 5 && i < waifus.size()) {
				embeds.add(waifus.get(i).getEmbed().build());
				i++;
			}
			if (page * 5 + 5 < waifus.size())
				buttons.add(Button.primary("waifu;next;" + page, "page suivante"));
			event.editMessageEmbeds(embeds).setActionRow(buttons).queue();
		}
		else if (event.getComponentId().startsWith("harem")) {
			String id = event.getComponentId().split(";")[3];
			int page = Integer.parseInt(event.getComponentId().split(";")[2]);
			if (event.getComponentId().contains("next")) {
				page++;
			} else if (event.getComponentId().contains("prev")) {
				page--;
			}
			ArrayList<Button> buttons = new ArrayList<>();
			ArrayList<MessageEmbed> embeds = new ArrayList<>();
			List<WaifuMembers> waifus = User.getWaifuMembers(id);
			if (page != 0)
				buttons.add(Button.primary("harem;prev;" + page + ";" + id, "page précédents"));

			int i = page * 5;
			while (i < page * 5 + 5 && i < waifus.size()) {
				embeds.add(waifus.get(i).getEmbed().build());
				i++;
			}
			if (page * 5 + 5 < waifus.size())
				buttons.add(Button.primary("harem;next;" + page + ";" + id, "page suivante"));
			event.editMessageEmbeds(embeds).setActionRow(buttons).queue();
		}
		else if (event.getComponentId().startsWith("zik")){
			String[] args = event.getComponentId().split(";");
			MusicPlayer player = MusicPlayer.getMusicPlayer(event.getGuild());
			if (args[1].equals("stop")) {
				player.getListener().stop();
				event.reply("ok j'arrete la musique").setEphemeral(true).queue();
			} else if (args[1].equals("next")) {
				player.getListener().nextTrack();
				player.getListener().editMessageSong(event.getMessage());
				event.reply("ok je passe a la musique suivante").setEphemeral(true).queue();
			} else if (args[1].equals("loop")) {
				player.getListener().nowLoop();
				player.getListener().editMessageSong(event.getMessage());
				if (player.getListener().isLoop())
					event.reply("ok je loop la musique").setEphemeral(true).queue();
				else
					event.reply("ok je ne loop plus la musique").setEphemeral(true).queue();
			} else if (args[1].equals("volume")) {
				if (args.length == 3)
				{
					switch (args[2]) {
						case "+":
							player.getListener().addvolume(10);
							break;
						case "-":
							player.getListener().addvolume(-10);
							break;
						case "reset":
							player.getListener().setvolume(50);
							break;
						case "quit":
							player.getListener().editMessageSong(event.getMessage());
							event.reply("retour au menu").setEphemeral(true).queue();
							return;
					}

				}
				event.editMessageEmbeds(player.getListener().getVolumeEmbed().build()).setActionRow(player.getListener().getVolumeButtons()).queue();
			}

		}
		else if (event.getComponentId().startsWith("buy"))
		{
			String splited[] = event.getComponentId().split(";");
			if (!splited[1].equals(event.getMember().getId())) {
				event.reply("je ne te parles pas, toi").setEphemeral(true).queue();
				return;
			}
			Item item = Item.getItemById(splited[2]);
			if (splited[3].startsWith("_")) {
				User user = User.getMember(event.getMember());
				EmbedBuilder buyed = new EmbedBuilder();
				if (user.buy(item.getId(), Integer.parseInt(splited[3].substring(1))))
				{
					buyed.setColor(Color.GREEN);
					buyed.setTitle("achat effectué");
					buyed.setDescription("tu as acheté " + item.getName() + " x" + splited[3].substring(1));
				}
				else {
					buyed.setTitle("achat impossible");
					buyed.setDescription("tu n'as pas assez d'argent pour acheter " + item.getName() + " x" + splited[3].substring(1));
					buyed.setColor(Color.RED);
				}
				event.editMessage("merci au revoir !").setEmbeds(buyed.build()).setActionRow(Button.danger("/", "achat effectué").asDisabled()).queue();
			}
			else {
				int number = Integer.parseInt(splited[3]);
				String quote = item.getName() + " ? bon choix !, combien en voulez vous ?";
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("Acheter");
				eb.setDescription( number + "x " + item.getName() + " pour " + (item.getPrice()*number)+ " B2C");
				ArrayList<Button> buttons = new ArrayList<>();
				Button b = Button.danger("buy;"+event.getMember().getId() + ";" + item.getId() + ";" + (number - 1), "-1");
				if (number == 1)
					b = b.asDisabled();
				buttons.add(b);
				buttons.add(Button.success("buy;"+event.getMember().getId() + ";" + item.getId() + ";_"+ number, "acheter"));
				buttons.add(Button.primary("buy;"+event.getMember().getId() + ";" + item.getId() + ";" + (number + 1), "+1"));
				event.getMessage().editMessage(quote).setEmbeds(eb.build()).setActionRow(buttons).queue();
				event.reply(".").complete().deleteOriginal().queue();
			}
		}
		else
		{
			event.reply("button not implemented").setEphemeral(true).queue();
		}
	}

	private void onAutoComplete(CommandAutoCompleteInteraction event) throws IOException {
		if (event.getOptions().get(0).getName().equals("squad"))
		{
			List<Squads> squads = null;
			ArrayList<String> ret = new ArrayList<>();
			try {
				 squads = Squads.getSquads();
			} catch (ConnectException e) {
				event.replyChoiceStrings(List.of("erreur de connection")).queue();
				return;
			} catch (IOException e) {
				event.replyChoiceStrings(List.of("erreur de lecture")).queue();
				return;
			}
			ret.add("scoreboard");
			for (Squads squad : squads) {
				ret.add(squad.getName());
			}
			event.replyChoiceStrings(ret).queue();
		}
	}

//	private void onSlashCommand(SlashCommandInteraction event) {
//		if (event.getName().equals("top")) {
//			event.replyEmbeds(TopCommand.CommandTop(event.getOption("squad").getAsString(), event.getGuild(), event.getMember()).build()).queue();
//		}
//		else if (event.getName().equals("profil")) {
//			if (event.getOption("pseudo") == null)
//				event.replyEmbeds(ProfilCommand.CommandProfil(event.getMember()).build()).queue();
//			else
//				event.replyEmbeds(ProfilCommand.CommandProfil(event.getGuild().getMemberById(event.getOption("pseudo").getAsUser().getId())).build()).queue();
//		} else
//			event.reply("coming soon").queue();
//	}

//	private void onMove(GuildVoiceMoveEvent event) throws IOException {
//		if (event.getChannelJoined().getId().equals(BotDiscord.AFKSalonId) && connectedUsers.get(event.getMember().getId()) != null && connectedUsers.get(event.getMember().getId()) + 86400000L < System.currentTimeMillis())
//		{
//			User.addAchievement(event.getMember(), "10", event.getGuild().getTextChannelById(BotDiscord.AnnounceSalonId));
//		}
//		connectedUsers.remove(event.getMember().getId());
//	}



//	private void onDisconnect(GuildVoiceLeaveEvent event) throws IOException {
//		if (connectedUsers.get(event.getMember().getId()) != null && connectedUsers.get(event.getMember().getId()) + 86400000L < System.currentTimeMillis())
//		{
//			User.addAchievement(event.getMember(), "9", event.getGuild().getTextChannelById(BotDiscord.AnnounceSalonId));
//		}
//		connectedUsers.remove(event.getMember().getId());
//	}

//	private void onConnect(GuildVoiceJoinEvent event) {
//		if (!event.getChannelJoined().getId().equals(BotDiscord.AFKSalonId))
//			connectedUsers.put(event.getMember().getId(), System.currentTimeMillis());
//	}


	private void onRemoveReact(MessageReactionRemoveEvent event) {

	}

	private void onDeleteRole(RoleDeleteEvent event) {

	}

	private void onCreateRole(RoleCreateEvent event) {

	}

	/**
	 * evenement quand un Embed est posté
	 *
	 * @param event
	 */
	private void onEmbed(MessageEmbedEvent event) {

	}

	/**
	 * evenement quand discord cherche si le bot existe encore
	 * @param event
	 */
	private void onPing(GatewayPingEvent event) throws IOException {

	}

	/**
	 * au lancement
	 *
	 * @param event
	 */
	private void onEnable(ReadyEvent event) throws IOException {
		event.getJDA().getPresence().setActivity(Activity.playing(BotDiscord.activity));
		event.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);
		new Thread(() -> {
			try {
				recupMeme(event.getJDA().getGuildById("382938797442334720"));
			} catch (InterruptedException e) {
				setError(e);
			}
		}).start();

	}



	/**
	 * si une nouvelle personne a rejoint
	 *
	 * @param event
	 */
	private void onGuildMemberJoin(GuildMemberJoinEvent event) {
		ArrayList<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = new ArrayList<>();
		try {
			Squads.getSquads().forEach(squad -> buttons.add(Button.primary("squad;"+squad.getId() +";" + event.getMember().getId(), squad.getName())));
		} catch (IOException e) {
			setError(e);
		}
		event.getGuild().getTextChannelById("947564791759777792").sendMessage("Bonjour "+ event.getMember() + "Bienvenue dans le monde du bitume, choisis ton escouade !").setActionRow(buttons).queue();
		event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("734011696242360331")).and(event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("634839000644845619"))).and(event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("734012661494317077"))).queue();
	}

	/**
	 * si une personne nous as quitté
	 *
	 */
	private void onGuildMemberLeave(GuildMemberRemoveEvent event) {
		event.getGuild().getTextChannelById("947564791759777792").sendMessage(event.getUser().getAsMention() + " a quitté la Guild.").queue();
	}

	/**
	 * si une personne ajoute une réaction
	 *
	 */
	private void onAddReact(MessageReactionAddEvent event) throws NoSuchFieldException, IllegalAccessException, IOException {
		if (event.getJDA().getSelfUser().equals(event.getMember().getUser()))
			return;

		else if (event.getReaction().getEmoji().getAsReactionCode().equals("⬆️") ) {
			Message msg = event.retrieveMessage().complete();
			int u = 0;
			for (MessageReaction reaction : msg.getReactions()) {
				if (reaction.getEmoji().getAsReactionCode().equals("⬆️"))
					u = reaction.getCount() - 1;
			}
			if (msg.getAuthor().equals(event.getJDA().getSelfUser()))
				if (msg.getContentRaw().startsWith("Ratio") && u >= 2)
				msg.editMessage("Turbo ratio by " +msg.getContentRaw().substring(StringExtenders.firstsearch(msg.getContentRaw(),"y") + 1)).queue();
			else if (msg.getContentRaw().startsWith("Turbo ratio") && u >= 4)
					msg.editMessage("Super ratio by "+msg.getContentRaw().substring(StringExtenders.firstsearch(msg.getContentRaw(),"y") + 1)).queue();
			else if (msg.getContentRaw().startsWith("Super ratio") && u >= 6)
					msg.editMessage("Hyper ratio by "+msg.getContentRaw().substring(StringExtenders.firstsearch(msg.getContentRaw(),"y") + 1)).queue();
			else if (msg.getContentRaw().startsWith("Hyper ratio") && u >= 8)
					msg.editMessage("Méga ratio by "+msg.getContentRaw().substring(StringExtenders.firstsearch(msg.getContentRaw(),"y") + 1)).queue();
			else if (msg.getContentRaw().startsWith("Méga ratio") && u >= 11)
					msg.editMessage("Giga ratio by "+msg.getContentRaw().substring(StringExtenders.firstsearch(msg.getContentRaw(),"y") + 1)).queue();
			else if (msg.getContentRaw().startsWith("Giga ratio") && u >= 14)
					msg.editMessage("RATIO ÉPIQUE by "+msg.getContentRaw().substring(StringExtenders.firstsearch(msg.getContentRaw(),"y") + 1)).queue();
			else if (msg.getContentRaw().startsWith("RATIO ÉPIQUE") && u >= 17)
					msg.editMessage("RATIO MYTHIQUE by "+msg.getContentRaw().substring(StringExtenders.firstsearch(msg.getContentRaw(),"y") + 1)).queue();
			else if (msg.getContentRaw().startsWith("RATIO MYTHIQUE") && u >= 20)
					msg.editMessage("**RATIO LÉGENDAIRE** by "+msg.getContentRaw().substring(StringExtenders.firstsearch(msg.getContentRaw(),"y") + 1)).queue();
			if (u == 2 || u == 4 || u == 6 || u == 8 || u == 11 || u == 14 || u == 17 || u == 20) {
				//coming soon
				System.out.println("xp ratio");
			}
		}
	}



	/**
	 * à la recption d'un message
	 *
	 */
	private void onMessage(MessageReceivedEvent event) throws IOException, InterruptedException {
		Message msg = event.getMessage();
		if (msg.getAuthor().isBot()) return;
		if (event.getAuthor().equals(event.getJDA().getSelfUser())) return;
		if (!event.getGuild().getId().equals("382938797442334720")) return;
		try {
			TextualExperience.sendMessage(event.getMember().getId(), event.getGuild());
		} catch (IOException e) {
			System.out.println("erreur d'écriture");
		}
		if (msg.getChannel().getId().equals("461606547064356864")) {
			postmeme(msg);
		}
		if (msg.getContentRaw().startsWith(CommandMap.getTag())) {
			commandMap.commandUser(msg.getContentRaw().replaceFirst(CommandMap.getTag(), ""), event.getMessage());
			return;
		}


		ReactionEvent.reactionevent(msg, msg.getJDA());
		//tous les events mis sans le prefix les reactions en gros


	}
}
