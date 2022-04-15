/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   UtilCommand.java                                   :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 12:50:04 by gchatain          #+#    #+#             */
/*   Updated: 2022/04/09 13:51:13 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.command;

import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.objects.activity;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import static fr.cringebot.cringe.objects.imgExtenders.resize;

public class UtilCommand {
        final private BotDiscord bot;
        public UtilCommand(BotDiscord bot){
                this.bot = bot;
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
}
