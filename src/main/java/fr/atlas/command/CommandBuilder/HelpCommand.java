/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   HelpCommand.java                                   :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 12:49:58 by gchatain          #+#    #+#             */
/*   Updated: 2022/06/19 11:22:52 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.atlas.command.CommandBuilder;

import fr.atlas.builder.Command.ExecutorType;
import fr.atlas.builder.CommandMap;
import fr.atlas.builder.SimpleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.internal.entities.UserImpl;

import java.awt.*;
import java.util.Random;

public class HelpCommand {


    public static void help(User user, MessageChannel channel, Guild guild, Member mem, CommandMap commandMap) {
        for (String type : commandMap.allTypes()) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Liste des commandes de type " + type.replace("fr.atlas.command.", "") + ".");
            builder.setColor(new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)));
            for(SimpleCommand command : commandMap.getCommandsByType(type)){
                if(command.getExecutorType() == ExecutorType.CONSOLE) continue;
                builder.addField(command.getName(), command.getDescription(), false);
            }
            if(!user.hasPrivateChannel()) user.openPrivateChannel().complete();
            ((UserImpl)user).getPrivateChannel().sendMessageEmbeds(builder.build()).queue();
        }
        channel.sendMessage(user.getAsMention()+", tu peux retrouver les commandes disponibles dans t'es mp").queue();
    }
}