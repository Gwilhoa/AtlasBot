/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   HelpCommand.java                                   :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 12:49:58 by gchatain          #+#    #+#             */
/*   Updated: 2022/03/23 20:09:54 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.command;

import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.builder.Command.ExecutorType;
import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.builder.SimpleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.entities.UserImpl;

import java.awt.*;

public class HelpCommand {

    private final CommandMap commandMap;

    public HelpCommand(CommandMap commandMap) {
        this.commandMap = commandMap;
    }

    @Command(name = "help", type = ExecutorType.USER, description = "affiche la liste des commandes.")
    private void help(User user, MessageChannel channel, Guild guild){

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Liste des commandes.");
        builder.setColor(Color.CYAN);

        for(SimpleCommand command : commandMap.getCommands()){
            if(command.getExecutorType() == ExecutorType.CONSOLE) continue;
            builder.addField(command.getName(), command.getDescription(), false);
        }

        if(!user.hasPrivateChannel()) user.openPrivateChannel().complete();
        ((UserImpl)user).getPrivateChannel().sendMessageEmbeds(builder.build()).queue();

        channel.sendMessage(user.getAsMention()+", Tu veux rencontrer les messages d'aides les plus chauds de ta région ? Alors regarde tes DM dès maintenant. ❤️").queue();

    }

}