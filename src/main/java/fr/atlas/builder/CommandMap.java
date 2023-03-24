/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   CommandMap.java                                    :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 12:09:51 by gchatain          #+#    #+#             */
/*   Updated: 2022/10/09 19:56:43 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.atlas.builder;

import fr.atlas.BotDiscord;
import fr.atlas.builder.Command.ExecutorType;
import fr.atlas.command.*;
import fr.atlas.command.CommandBuilder.HelpCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;


/**
 * objet ciblant les commandes
 */
public final class CommandMap {

	/**
	 * prefix du bot
	 */
	private final static String tag = ">";

	public ArrayList<SimpleCommand> getCommandsByType(String type) {
		ArrayList<SimpleCommand> commands = new ArrayList<>();
		for (SimpleCommand command : this.commands.values()) {
			if (command.getType().equals(type)) {
				commands.add(command);
			}
		}
		return commands;
	}

	/**
	 * instance du bot ce qui veut dire que on peut creer plusieurs bots différent avec ce fichier.
	 */
	private final BotDiscord botDiscord;

	/**
	 * variable contenant les commandes
	 */
	private final Map<String, SimpleCommand> commands = new HashMap<>();

	/**
	 * initialisation de l'enregistrement de commande
	 *
	 * @param botDiscord cringebot
	 */
	public CommandMap(BotDiscord botDiscord) {
		this.botDiscord = botDiscord;
		registerCommands(new Listener(botDiscord, this), new Music(), new Admin(), new Waifus(), new Escouades(), new Utils(this));
	}

	/**
	 * static gardant le prefix comme ça on peut tout le temps le retrouver
	 *
	 * @return prefix
	 */
	public static String getTag() {
		return tag;
	}

	/**
	 * sert a récuperer toutes les commandes
	 *
	 * @return tag command
	 */
	public Collection<SimpleCommand> getCommands() {
		return commands.values();
	}

	/**
	 * utilise la fonction registerCommand avec chaque fonction du fichier
	 *
	 * @param objects tout les fichiers avec des commandes
	 */
	public void registerCommands(Object... objects) {
		for (Object object : objects) registerCommand(object);
	}

	/**
	 * va chercher dans chaque fonctions si il est régie par la méthode @Command
	 * va mettre dans la variable commands le nom de la commande
	 *
	 * @param object class
	 */
	public void registerCommand(Object object) {
		for (Method method : object.getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(Command.class)) {
				Command command = method.getAnnotation(Command.class);
				method.setAccessible(true);
				SimpleCommand simpleCommand = new SimpleCommand(command.name(), command.description(), command.type(), object, method, command.permission(), object.getClass().getName());
				commands.put(command.name(), simpleCommand);
			}
		}
	}

	/**
	 * sert à voir par son nom si c'est une command qui est executé depuis la console
	 * si c'est le cas, elle va etre executée
	 *
	 * @param command name
	 */
	public void commandConsole(String command) {
		Object[] object = getCommand(command);
		if (object[0] == null || ((SimpleCommand) object[0]).getExecutorType() == ExecutorType.USER) {
			System.out.println("Commande inconnue.");
			return;
		}
		try {
			execute(((SimpleCommand) object[0]), command, (String[]) object[1], null);
		} catch (Exception exception) {
			System.out.println("La methode " + ((SimpleCommand) object[0]).getMethod().getName() + " n'est pas correctement initialisé.");
			exception.printStackTrace();

		}
	}

	/**
	 * sert à voir par son nom si c'est une command qui est executable par un joueur
	 * si c'est le cas, elle va etre executee avec le message de l'utilisateur
	 *
	 * @param command name
	 * @param message (objet retenue par discord comme les messages sur discord)
	 */
	public void commandUser(String command, Message message) {
		Object[] object = getCommand(command);
		if (object[0] == null || ((SimpleCommand) object[0]).getExecutorType() == ExecutorType.CONSOLE) return;

		message.getGuild();

		try {
			execute(((SimpleCommand) object[0]), command.toLowerCase(), (String[]) object[1], message);
		} catch (Exception exception) {
			System.out.println("La methode " + ((SimpleCommand) object[0]).getMethod().getName() + " n'est pas correctement initialisé.");
			exception.printStackTrace();
			message.getChannel().sendMessage(Objects.requireNonNull(message.getGuild().getMemberById("315431392789921793")).getAsMention() + " sale batard, la commande " + ((SimpleCommand) object[0]).getMethod().getName() + " est mal faites").queue();
		}
	}

	/**
	 * servant a séparer le nom et les argument
	 *
	 * @param command name
	 * @return {command, args}
	 */
	private Object[] getCommand(String command) {
		String[] commandSplit = command.split(" ");
		String[] args = new String[commandSplit.length - 1];
		System.arraycopy(commandSplit, 1, args, 0, commandSplit.length - 1);
		SimpleCommand simpleCommand = commands.get(commandSplit[0].toLowerCase(Locale.ROOT));
		return new Object[]{simpleCommand, args};
	}

	/**
	 * sert a éxecuter une commande
	 *
	 * @param simpleCommand la commande
	 * @param command       le nom de la commande
	 * @param args          les arguments
	 * @param message       messages d'utilisateur
	 */
	private void execute(SimpleCommand simpleCommand, String command, String[] args, Message message) throws Exception {
		if (BotDiscord.isMaintenance)
		{
			message.getChannel().sendMessage("je fais grève, je veux mon api et ma base de donnée").queue();
			return;
		}
		Parameter[] parameters = simpleCommand.getMethod().getParameters();
		Object[] objects = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i].getType() == String[].class) objects[i] = args;
			else if (parameters[i].getType() == User.class) objects[i] = message == null ? null : message.getAuthor();
			else if (parameters[i].getType() == TextChannel.class)
				objects[i] = message == null ? null : message.getChannel();
			else if (parameters[i].getType() == PrivateChannel.class)
				objects[i] = message == null ? null : message.getAuthor().openPrivateChannel().complete();
			else if (parameters[i].getType() == Guild.class) objects[i] = message == null ? null : message.getGuild();
			else if (parameters[i].getType() == String.class) objects[i] = command;
			else if (parameters[i].getType() == Message.class) objects[i] = message;
			else if (parameters[i].getType() == JDA.class) objects[i] = botDiscord.getJda();
			else if (parameters[i].getType() == MessageChannel.class)
				objects[i] = message == null ? null : message.getChannel();
		}
		if (message.getMember().hasPermission(simpleCommand.getPermission()))
			simpleCommand.getMethod().invoke(simpleCommand.getObject(), objects);
		else
			message.getChannel().sendMessage("Vous n'avez pas la permission d'utiliser cette commande").queue();
	}

	public ArrayList<String> allTypes() {
		ArrayList<String> types = new ArrayList<>();
		for (SimpleCommand simpleCommand : commands.values()) {
			if (!types.contains(simpleCommand.getType())) types.add(simpleCommand.getType());
		}
		return types;
	}
}