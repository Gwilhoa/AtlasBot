package fr.cringebot.cringe.objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Locale;

/**
 * sert à garder tout les objets qui sont pokemon vont bientot etre modif
 */
public class Pokemon {
    public static String getRandomPokemon(){
        Document doc;
        try {
            doc = Jsoup.connect("https://pokestrat.io/pokemon-aleatoire/").get();
        } catch (IOException e) {
            return null;
        }
        System.out.println(doc.select("main.cadre:nth-child(5) section.espacement.aleat:nth-child(6) div.block-base1:nth-child(2) a:nth-child(1) div.block-base2.aleatoire.details.effet-hover div.inline-block-middle.nom > span.big-font:nth-child(3)").text());
        return doc.select("main.cadre:nth-child(5) section.espacement.aleat:nth-child(6) div.block-base1:nth-child(2) a:nth-child(1) div.block-base2.aleatoire.details.effet-hover div.inline-block-middle.nom > span.big-font:nth-child(3)").text();
    }
}
