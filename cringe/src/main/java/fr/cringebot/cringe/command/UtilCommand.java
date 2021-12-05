/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   UtilCommand.java                                   :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 12:50:04 by gchatain          #+#    #+#             */
/*   Updated: 2021/12/05 12:50:04 by gchatain         ###   ########lyon.fr   */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.command;

import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.objects.DetectorAttachment;
import fr.cringebot.cringe.objects.activity;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

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
        @Command(name = "weather", type = Command.ExecutorType.USER, description = "donne la météo")
        private void weather(Guild guild, TextChannel textChannel, String[] args){
                try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        URLConnection connection = new URL("https://wttr.in/"+args[0]+"_1tqp_lang=fr.png").openConnection();
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0");
                        BufferedImage im = ImageIO.read(connection.getInputStream());
                        im = resize(im, 2064, 532, 0, 0, true);
                        im = resize(im, 2064, 532, 0, 0, false);
                        ImageIO.write(im, "png", baos);
                        textChannel.sendFile(baos.toByteArray(), "meteo.png").queue();
                } catch (IOException e) {
                        e.printStackTrace();
                }

        }

        @Command(name = "activity",type = Command.ExecutorType.CONSOLE, description = "change l'activité du bot")
        private void changeactivity(String arg){
                JDA jda = bot.getJda();
                if (arg.equalsIgnoreCase("activity")){
                        if (jda.getPresence().getStatus().equals(OnlineStatus.ONLINE)){
                                jda.getGuilds().get(0).getMemberById("315431392789921793").getJDA().getPresence().setActivity(new activity("rien"));
                                jda.getPresence().setActivity(new activity("se faire retaper",null, Activity.ActivityType.DEFAULT));
                                jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                        } else {
                                jda.getPresence().setActivity(new activity("un tuto sur le cringe",null, Activity.ActivityType.LISTENING));
                                jda.getPresence().setStatus(OnlineStatus.ONLINE);
                        }
                } else {
                        jda.getPresence().setActivity(new activity(arg.replaceFirst("activity", ""), null, Activity.ActivityType.LISTENING));
                        jda.getPresence().setStatus(OnlineStatus.ONLINE);
                }
        }

        @Command(name = "test", type= Command.ExecutorType.CONSOLE, description = "juste pour les tests")
        private void test(){
                System.out.println("bref...");
        }
}
