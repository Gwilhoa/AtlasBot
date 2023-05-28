

package fr.atlas.command;


import com.jcraft.jsch.JSchException;
import fr.atlas.BotDiscord;
import fr.atlas.Request.*;
import fr.atlas.Request.User;
import fr.atlas.builder.CommandMap;
import fr.atlas.builder.SimpleCommand;
import fr.atlas.command.CommandBuilder.HelpCommand;
import fr.atlas.command.CommandBuilder.ProfilCommand;
import fr.atlas.command.CommandBuilder.TopCommand;
import fr.atlas.builder.Command;
import fr.atlas.builder.Command.ExecutorType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.internal.entities.UserImpl;
import net.dv8tion.jda.internal.interactions.component.StringSelectMenuImpl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static fr.atlas.objects.imgExtenders.resize;

/**
 * fichier de commandes de base
 */
public class Listener {

	private final BotDiscord botDiscord;
	/**
	 * intialisation de l'objet
	 *
	 * @param botDiscord
	 * @param commandMap
	 */
	public Listener(BotDiscord botDiscord, CommandMap commandMap) {
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



	@Command(name = "bresil", description= "You are going to brazil", type = ExecutorType.USER)
	private void bresil(Message msg) {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "shop", description = "Permet d'acheter des items")
	private void shop(Message msg) {
		ArrayList<String> quotes = new ArrayList<>();
		quotes.add(" pas cher");
		quotes.add(" de dernier cri");
		quotes.add(" de qualité");
		quotes.add(" de qualité supérieur");
		quotes.add(" vous en trouverez pas mieux ailleurs");
		StringSelectMenu.Builder dropdown = StringSelectMenu.create("shop")
				.setPlaceholder("Que voulez-vous acheter ?");
		for (Item item : Item.getItems()) {
			dropdown.addOptions(SelectOption.of(item.getName(), item.getId() + ";" + msg.getMember().getId() +";"+ 1));
		}
		StringBuilder sb = new StringBuilder();
		sb.append("alors voyons voir ce que j'ai ... \n");
		for (Item item : Item.getItems()) {
			sb.append(item.getName()).append(" à ").append(item.getPrice()).append(" B2C\n");
			sb.append("> ").append(quotes.get((int) (Math.random() * quotes.size()))).append("\n");
		}
		msg.getChannel().sendMessage(sb).setActionRow(dropdown.build()).queue();
	}

	@Command(name = "gift", description = "Entrez un code cadeau !", type = ExecutorType.USER)
	private void gift(Message msg) {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "pay", description = "Donner de l'argent a un ami", type = ExecutorType.USER)
	private void pay(Message msg) {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "poll", description = "Faire des sondages facilement [reviendra avec le site]", type = ExecutorType.USER)
	private void poll(Message msg) {
		msg.getChannel().sendMessage("coming soon").queue();
	}

	@Command(name = "role", description = "Permettre d'ajouter un role a la base des roles obtenable [reviendra avec le site]", type = ExecutorType.USER)
	private void role(Message msg) {
		StringBuilder builder = new StringBuilder();
		for (Role role : msg.getGuild().getRoles())
			builder.append(role.getName()).append("\n");
		msg.getChannel().sendMessage(builder.toString()).queue();
	}


	@Command(name = "inventory", description = "afficher ton inventaire", type = ExecutorType.USER)
	private void inventory(Message msg) throws IOException {
		ArrayList<MyItem> inventory = User.getInventory(msg.getMember().getId());
		if (inventory.isEmpty()) {
			msg.getChannel().sendMessage("Vous n'avez pas d'inventaire !").queue();
			return;
		}
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Inventaire de " + msg.getMember().getEffectiveName());
		embed.setColor(Color.CYAN);
		for (MyItem item : inventory) {
			embed.addField(item.getItem().getName(), "x" + item.getQuantity(), true);
		}
		msg.getChannel().sendMessageEmbeds(embed.build()).queue();
	}

	@Command(name = "cki", description = "mais qui est-il !", type = ExecutorType.USER)
	private void cki(Message msg){
		msg.getChannel().sendMessage("coming soon").queue();
	}
	@Command(name = "items", type = Command.ExecutorType.USER, description = "voir tout les items")
	private void items(Message message) {
		ArrayList<Item> items = Item.getItems();
		ArrayList<MessageEmbed> embeds = new ArrayList<>();
		for (Item item : items) {
			embeds.add(item.getEmbed().build());
			if (embeds.size() == 10) {
				message.getChannel().sendMessageEmbeds(embeds).queue();
				embeds.clear();
			}
		}
		if (embeds.size() != 0)
			message.getChannel().sendMessageEmbeds(embeds).queue();
	}


}