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


package fr.cringebot.cringe.event;

import com.diogonunes.jcolor.Attribute;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.CommandBuilder.ProfilCommand;
import fr.cringebot.cringe.CommandBuilder.TopCommand;
import fr.cringebot.cringe.Request.Achievement;
import fr.cringebot.cringe.Request.Members;
import fr.cringebot.cringe.Request.Squads;
import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.objects.StringExtenders;
import fr.cringebot.cringe.objects.XpManager;
import fr.cringebot.music.MusicCommand;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GatewayPingEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageEmbedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.diogonunes.jcolor.Ansi.colorize;


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
		System.out.println(colorize(event.getClass().getSimpleName(), Attribute.TEXT_COLOR(169, 169, 169)));

		try {
			if (event instanceof ReadyEvent) onEnable((ReadyEvent) event);
			else if (event instanceof MessageReceivedEvent) onMessage((MessageReceivedEvent) event);
			else if (event instanceof GuildMemberJoinEvent) onGuildMemberJoin((GuildMemberJoinEvent) event);
			else if (event instanceof GuildMemberRemoveEvent) onGuildMemberLeave((GuildMemberRemoveEvent) event);
			else if (event instanceof MessageReactionAddEvent) onAddReact((MessageReactionAddEvent) event);
			else if (event instanceof GatewayPingEvent) onPing((GatewayPingEvent) event);
			else if (event instanceof RoleCreateEvent) onCreateRole((RoleCreateEvent) event);
			else if (event instanceof RoleDeleteEvent) onDeleteRole((RoleDeleteEvent) event);
			else if (event instanceof MessageReactionRemoveEvent) onRemoveReact((MessageReactionRemoveEvent) event);
			else if (event instanceof GuildVoiceJoinEvent) onConnect((GuildVoiceJoinEvent) event);
			else if (event instanceof GuildVoiceLeaveEvent) onDisconnect((GuildVoiceLeaveEvent) event);
			else if (event instanceof MessageEmbedEvent) onEmbed((MessageEmbedEvent) event);
			else if (event instanceof GuildVoiceMoveEvent) onMove((GuildVoiceMoveEvent) event);
			else if (event instanceof SlashCommandInteraction) onSlashCommand((SlashCommandInteraction) event);
			else if (event instanceof CommandAutoCompleteInteraction) onAutoComplete((CommandAutoCompleteInteraction) event);
			else if (event instanceof SelectMenuInteractionEvent) onSelectMenu((SelectMenuInteractionEvent) event);
			else if (event instanceof ButtonInteractionEvent) onButton((ButtonInteractionEvent) event);
			else if (event instanceof GuildMemberRoleAddEvent) onRoleAdd((GuildMemberRoleAddEvent) event);
		} catch (IOException | InterruptedException | IllegalAccessException | NoSuchFieldException e) {

			e.printStackTrace();
			event.getJDA().getGuilds().get(0).getMemberById("315431392789921793").getUser().openPrivateChannel().complete().sendMessage("erreur sur " + event.getClass().getSimpleName()).queue();
		}
	}

	private void onRoleAdd(GuildMemberRoleAddEvent event) throws IOException {
		Guild guild = event.getGuild();
		Members mem = Members.getMember(event.getMember());
		if (event.getRoles().contains(event.getGuild().getRoleById("680431143283458077")))
		{
			mem.addAchievement("3", guild.getTextChannelById(BotDiscord.AnnounceSalonId), guild);
		}
		if (mem.getMember(guild).getRoles().contains(guild.getRoleById("849925828069687296")))
		{
			mem.addAchievement("2", guild.getTextChannelById(BotDiscord.AnnounceSalonId), guild);
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
				Members.newMembers(event.getMember(), event.getComponentId().split(";")[1]);
			}

		}
	}

	private void onSelectMenu(SelectMenuInteractionEvent event) {
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

	private void onSlashCommand(SlashCommandInteraction event) {
		if (event.getName().equals("top")) {
			event.replyEmbeds(TopCommand.CommandTop(event.getOption("squad").getAsString(), event.getGuild(), event.getMember()).build()).queue();
		}
		else if (event.getName().equals("profil")) {
			if (event.getOption("pseudo") == null)
				event.replyEmbeds(ProfilCommand.CommandProfil(event.getMember()).build()).queue();
			else
				event.replyEmbeds(ProfilCommand.CommandProfil(event.getGuild().getMemberById(event.getOption("pseudo").getAsUser().getId())).build()).queue();
		} else
			event.reply("coming soon").queue();
	}

	private void onMove(GuildVoiceMoveEvent event) throws IOException {
		if (event.getChannelJoined().getId().equals(BotDiscord.AFKSalonId) && connectedUsers.get(event.getMember().getId()) != null && connectedUsers.get(event.getMember().getId()) + 86400000L < System.currentTimeMillis())
		{
			Members.addAchievement(event.getMember(), "10", event.getGuild().getTextChannelById(BotDiscord.AnnounceSalonId));
		}
		connectedUsers.remove(event.getMember().getId());
	}



	private void onDisconnect(GuildVoiceLeaveEvent event) throws IOException {
		if (connectedUsers.get(event.getMember().getId()) != null && connectedUsers.get(event.getMember().getId()) + 86400000L < System.currentTimeMillis())
		{
			Members.addAchievement(event.getMember(), "10", event.getGuild().getTextChannelById(BotDiscord.AnnounceSalonId));
		}
		connectedUsers.remove(event.getMember().getId());
	}

	private void onConnect(GuildVoiceJoinEvent event) {
		if (!event.getChannelJoined().getId().equals(BotDiscord.AFKSalonId))
			connectedUsers.put(event.getMember().getId(), System.currentTimeMillis());
	}


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
		System.out.println("bot ready");
		event.getJDA().getPresence().setActivity(Activity.playing("en dev"));
		event.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);
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
			e.printStackTrace();
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
		MusicCommand.MusicAddReact(event);
	}



	/**
	 * à la recption d'un message
	 *
	 */
	private void onMessage(MessageReceivedEvent event) throws IOException, InterruptedException {
		Message msg = event.getMessage();
		if (event.getAuthor().equals(event.getJDA().getSelfUser())) return;
		if (!event.getGuild().getId().equals("382938797442334720")) return;
		XpManager.sendMessage(event.getMember().getId(), event.getGuild());
		if (msg.getContentRaw().startsWith(CommandMap.getTag())) {
			commandMap.commandUser(msg.getContentRaw().replaceFirst(CommandMap.getTag(), ""), event.getMessage());
			return;
		}


		ReactionEvent.reactionevent(msg, msg.getJDA());
		//tous les events mis sans le prefix les reactions en gros


	}
}
