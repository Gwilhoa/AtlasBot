package fr.cringebot.cringe.pokemon;

import java.awt.Color;
import java.io.IOException;
import java.util.Random;

import fr.cringebot.cringe.pokemon.objects.Type;
import fr.cringebot.cringe.pokemon.objects.wtp;
import net.dv8tion.jda.api.entities.ThreadChannel;
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

	@Command(name = "wtp", type = Command.ExecutorType.USER)
	private void wtp(Message msg) {
		new Thread(() -> {
			msg.getChannel().sendTyping().queue();

			String name = Pokemon.getRandomPokemon().getRealname();
			String str = null;
			Document doc;
			EmbedBuilder eb = new EmbedBuilder();
			try {
				doc = Jsoup.connect("https://www.pokemon.com/fr/pokedex/" + name).get();
				eb.setTitle("who's that pokemon");
				str = doc.select("body.fr.fluid.custom-form-elements:nth-child(2) div.container:nth-child(5) section.section.pokedex-pokemon-details:nth-child(3) div.column-6.push-7:nth-child(2) div.pokedex-pokemon-details-right div.version-descriptions.active:nth-child(1) > p.version-y").text() + "\n\n" + doc.select("body.fr.fluid.custom-form-elements:nth-child(2) div.container:nth-child(5) section.section.pokedex-pokemon-details:nth-child(3) div.column-6.push-7:nth-child(2) div.pokedex-pokemon-details-right div.version-descriptions.active:nth-child(1) > p.version-x").text();
				eb.setDescription(str.replace(name, "<ce pokémon>"));
			} catch (IOException e) {
				eb.setTitle("who's that pokemon");
				eb.setDescription("ce pokemon existe pas");
			}
			Message message = msg.getChannel().sendMessageEmbeds(eb.build()).complete();
			ThreadChannel tc = message.createThreadChannel("quel est ce pokemon ?").complete();
			wtp.wtpThreads.put(tc.getId(), new wtp(message.getId(), name));
			wtp.save();
		}).start();
	}

	@Command(name = "pokemon", type = Command.ExecutorType.USER)
	private void pokemon(Message msg) {
		new Thread(() -> {
			Pokemon po = Pokemon.getRandomPokemon();
			Type t = Type.Types.byName(po.getType().split(" ")[new Random().nextInt(po.getType().split(" ").length)]).tpe;
			int level = new Random().nextInt(po.getLevels().get(1) - po.getLevels().get(0)) + po.getLevels().get(0);
			boolean isShiney = new Random().nextInt(1000) > 995;
			Message m = msg.getChannel().sendMessageEmbeds(new EmbedBuilder().setTitle("capture...").build()).complete();
			if (po.getProbability() == 0){
				m.editMessageEmbeds(new EmbedBuilder().setImage("http://play.pokemonshowdown.com/sprites/itemicons/poke-ball.png").build()).complete();
			}
			if (po.getProbability() == 1){
				m.editMessageEmbeds(new EmbedBuilder().setImage("http://play.pokemonshowdown.com/sprites/itemicons/great-ball.png").setColor(Color.red).build()).queue();
			}
			if (po.getProbability() == 2){
				m.editMessageEmbeds(new EmbedBuilder().setImage("http://play.pokemonshowdown.com/sprites/itemicons/quick-ball.png").setColor(Color.BLUE).build()).queue();
			}
			if (po.getProbability() == 3){
				m.editMessageEmbeds(new EmbedBuilder().setImage("http://play.pokemonshowdown.com/sprites/itemicons/premier-ball.png").setColor(new Color(0, 254, 4)).build()).queue();
			}
			if (po.getProbability() == 4){
				m.editMessageEmbeds(new EmbedBuilder().setImage("http://play.pokemonshowdown.com/sprites/itemicons/ultra-ball.png").setColor(new Color(254, 231, 0)).build()).queue();
			}
			if (po.getProbability() == 5){
				m.editMessageEmbeds(new EmbedBuilder().setImage("http://play.pokemonshowdown.com/sprites/itemicons/master-ball.png").setColor(new Color(161, 51, 233)).build()).queue();
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			EmbedBuilder eb = new EmbedBuilder().setDescription(m.getMember().getAsMention() + " a attrapé " + po.getRealname() + " #" + po.getId() + (isShiney ? " shiney" : "") + " il est de niveau " + level ).setImage("http://play.pokemonshowdown.com/sprites/ani" + (isShiney ? "-shiny" : "") + "/" + po.getName() + ".gif").setColor(t.c).setFooter("rien n'est enregistré Version Beta 0.3.1", m.getMember().getAvatarUrl());
			m.editMessageEmbeds(eb.build()).queue();
		}).start();
	}
}
