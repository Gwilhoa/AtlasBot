package fr.cringebot.cringe.cki;

import fr.cringebot.cringe.objects.SelectOptionImpl;
import fr.cringebot.cringe.objects.lol.Champion;
import fr.cringebot.cringe.pokemon.objects.Pokemon;
import fr.cringebot.cringe.objects.cki;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class mainCommand {

    public static void ckimain(Message msg)
    {
        ArrayList<SelectOption> options = new ArrayList<>();
        options.add(new SelectOptionImpl("Quel est ce Pokémon", "quel est ce pokémon"));
        options.add(new SelectOptionImpl("Quel est ce Champion", "quel est ce champion"));
        options.add(new SelectOptionImpl("Coming soon", "coming soon"));
        SelectMenuImpl selectionMenu = new SelectMenuImpl( "cki", "selectionnez un choix", 1, 1, false, options);
        msg.getChannel().sendMessage("choisis le type de ton jeu").setActionRow(selectionMenu).queue();
    }
    public static void wtpmain(Message msg) {
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
            msg.delete().queue();
            ThreadChannel tc = message.createThreadChannel("quel est ce pokemon ?").complete();
            cki.wtpThreads.put(tc.getId(), new cki("pokemon",message.getId(), name, msg.getChannel().getId()));
            cki.save();
        }).start();
    }
    public static void wtcmain(Message msg) {
        new Thread(() -> {
            msg.getChannel().sendTyping().queue();
            Champion c = Champion.getRandomChampion();
            String name = c.getName();
            Document doc;
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("who's that champion");
            eb.setDescription(c.getBlurb().replace(c.getName(), "<ce champion>").replace("Sarah Fortune", "<ce champion>"));
            Message message = msg.getChannel().sendMessageEmbeds(eb.build()).complete();
            msg.delete().queue();
            ThreadChannel tc = message.createThreadChannel("quel est ce champion ?").complete();
            cki.wtpThreads.put(tc.getId(), new cki("champion",message.getId(), name, msg.getChannel().getId()));
            cki.save();
        }).start();
    }
}
