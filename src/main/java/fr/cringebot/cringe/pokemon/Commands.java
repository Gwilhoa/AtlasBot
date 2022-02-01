package fr.cringebot.cringe.pokemon;

import java.awt.Color;
import java.io.IOException;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.pokemon.objects.Pokemon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class Commands {
	@Command(name = "pokedex", type = Command.ExecutorType.USER)
	private void pokedex(Message msg) {
		new Thread(() -> {
			msg.getChannel().sendTyping().queue();
			String str = Pokemon.getRandomPokemon().getRealname();
			if (msg.getContentRaw().split(" ").length == 2) {
				str = msg.getContentRaw().split(" ")[1];
			}
			Document doc;
			EmbedBuilder eb = new EmbedBuilder();
			try {
				doc = Jsoup.connect("https://www.pokemon.com/fr/pokedex/" + str).get();
				eb.setTitle(str);
				str = doc.select("body.fr.fluid.custom-form-elements:nth-child(2) div.container:nth-child(5) section.section.pokedex-pokemon-details:nth-child(3) div.column-6.push-7:nth-child(2) div.pokedex-pokemon-details-right div.version-descriptions.active:nth-child(1) > p.version-y").text();
				eb.setDescription(str);
				eb.setImage("https://assets.pokemon.com/assets/cms2/img/pokedex/detail/" + doc.selectXpath("//body[1]/div[4]/section[1]/div[2]/div[1]/span[1]").text().substring(3) + ".png");
			} catch (IOException e) {
				eb.setTitle(msg.getContentRaw().split(" ")[1]);
				eb.setDescription("ce pokemon existe pas");
			}
			msg.getChannel().sendMessageEmbeds(eb.build()).queue();
		}).start();
	}
	@Command(name = "pokemon", type = Command.ExecutorType.USER)
	private void pokemon(Message msg) {
		new Thread(() -> {
			Pokemon po = Pokemon.getRandomPokemon();
			Boolean isShiney = new Random().nextInt(100) > 95;
			EmbedBuilder eb = new EmbedBuilder().setDescription(msg.getMember().getAsMention() + " a attrapé " + po.getRealname() + " #" + po.getId() + " proba de " + (po.getProbability() + 1) + (isShiney ? " shiney" : "") + " il est de niveau 'NOT IMPLEMENTED'").setImage("http://play.pokemonshowdown.com/sprites/ani" + (isShiney ? "-shiny" : "") + "/" + po.getName() + ".gif").setColor(Color.YELLOW).setFooter("rien n'est enregistré Version Beta 0.1", msg.getMember().getAvatarUrl());
			msg.getChannel().sendMessageEmbeds(eb.build()).queue();
		}).start();
	}
}
