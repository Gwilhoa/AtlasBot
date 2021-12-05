/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   CommandPokemon.java                                :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 12:47:09 by gchatain          #+#    #+#             */
/*   Updated: 2021/12/05 12:47:09 by gchatain         ###   ########lyon.fr   */
/*                                                                            */
/* ************************************************************************** */
package fr.cringebot.cringe.command;

import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.objects.Pokemon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
toutes les commandes concernant les pokemon
*/
public class CommandPokemon {
    @Command(name="pokedex", description = "la connaissances sur les pokémons n'ont plus de limite, cherche le pokemon que tu veux !",type= Command.ExecutorType.USER)
    private void pokedex(Message msg){
        new Thread(()->{
            msg.getChannel().sendTyping().queue();
            String str = Pokemon.getRandomPokemon();
            if (msg.getContentRaw().split(" ").length == 2)
                str = msg.getContentRaw().split(" ")[1];
            Document doc;
            EmbedBuilder eb = new EmbedBuilder();
            try {
                doc = Jsoup.connect("https://www.pokemon.com/fr/pokedex/" + str).get();
                eb.setTitle(str);
                str = doc.select("body.fr.fluid.custom-form-elements:nth-child(2) div.container:nth-child(5) section.section.pokedex-pokemon-details:nth-child(3) div.column-6.push-7:nth-child(2) div.pokedex-pokemon-details-right div.version-descriptions.active:nth-child(1) > p.version-y").text();
                eb.setDescription(str);
                eb.setImage("https://assets.pokemon.com/assets/cms2/img/pokedex/detail/"+doc.selectXpath("//body[1]/div[4]/section[1]/div[2]/div[1]/span[1]").text().substring(3)+".png");
            } catch (IOException e) {
                eb.setTitle(msg.getContentRaw().split(" ")[1]);
                eb.setDescription("ce pokemon existe pas");
            }
            msg.getChannel().sendMessageEmbeds(eb.build()).queue();
        }).start();
    }

}
