package fr.cringebot.cringe.command;

import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.objects.Pokemon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class CommandPokemon {
    @Command(name="pokedex",type= Command.ExecutorType.USER)
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
		/*
		Document doc = Jsoup.connect("https://pokestrat.io/pokemon-aleatoire/").get();
		String str = doc.select("main.cadre:nth-child(5) section.espacement.aleat:nth-child(6) div.block-base1:nth-child(2) a:nth-child(1) div.block-base2.aleatoire.details.effet-hover div.inline-block-middle.nom > span.big-font:nth-child(3)").text();
		String nameFr = str;
		str = "https://www.pokemon.com/fr/pokedex/"+str;
		doc = Jsoup.connect(str).get();
		str = "https://www.pokepedia.fr/"+str;
		doc = Jsoup.connect(str).get();
		str = doc.selectXpath("(//th[contains(text(),'Nom anglais')]/following::td)[1]").text();
		String nameAgl = str;
		EmbedBuilder eb = new EmbedBuilder().setColor(Color.red).setTitle(nameFr).setImage("https://play.pokemonshowdown.com/sprites/ani/"+nameAgl.toLowerCase(Locale.ROOT)+".gif");
		msg.getChannel().sendMessageEmbeds(eb.build()).queue();*/
    }

}
