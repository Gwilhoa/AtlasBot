package fr.cringebot.cringe.cki;

import com.merakianalytics.orianna.types.core.staticdata.Champion;
import com.merakianalytics.orianna.types.core.staticdata.Champions;
import fr.cringebot.cringe.objects.SelectOptionImpl;
import fr.cringebot.cringe.pokemon.objects.Pokemon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class mainCommand {

    public static void ckimain(Message msg)
    {
        ArrayList<SelectOption> options = new ArrayList<>();
        for (cki.Type tpe: cki.Type.values())
            options.add(new SelectOptionImpl("CKI édition "+tpe.name(), tpe.name()));
        options.add(new SelectOptionImpl("Aléatoire", "random"));
        SelectMenuImpl selectionMenu = new SelectMenuImpl( "cki", "selectionnez un choix", 1, 1, false, options);
        msg.getChannel().sendMessage("choisis le type de ton jeu").setActionRow(selectionMenu).queue();
    }

    public static void MenuInteract(String selection, Message msg)
    {
        int r = 0;
        if (selection.equals("random"))
            r = new Random().nextInt(2) + 1;
        if ((r == 0 && selection.equals(cki.Type.POKEMON.name())) || r == 1)
            wtpmain(msg);
        if ((r == 0 && selection.equals(cki.Type.LOL.name())) || r == 2)
            wtcmain(msg);

    }

    public static void wtpmain(Message msg) {
        new Thread(() -> {
            msg.getChannel().sendTyping().queue();
            String name = Pokemon.getRandomPokemon().getRealname();
            Document doc;
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("CKI - [Pokémon édition]");
            try {
                doc = Jsoup.connect("https://www.pokemon.com/fr/pokedex/" + name).get();
                String str = doc.select("body.fr.fluid.custom-form-elements:nth-child(2) div.container:nth-child(5) section.section.pokedex-pokemon-details:nth-child(3) div.column-6.push-7:nth-child(2) div.pokedex-pokemon-details-right div.version-descriptions.active:nth-child(1) > p.version-y").text() + "\n\n" + doc.select("body.fr.fluid.custom-form-elements:nth-child(2) div.container:nth-child(5) section.section.pokedex-pokemon-details:nth-child(3) div.column-6.push-7:nth-child(2) div.pokedex-pokemon-details-right div.version-descriptions.active:nth-child(1) > p.version-x").text();
                eb.setDescription(str.replace(name, "<ce pokémon>"));
            } catch (IOException e) {
                eb.setDescription("ce pokemon existe pas");
            }
            Message message = msg.getChannel().sendMessageEmbeds(eb.build()).complete();
            msg.delete().queue();
            ThreadChannel tc = message.createThreadChannel("Quel est ce pokémon ?").complete();
            cki.wtpThreads.put(tc.getId(), new cki(cki.Type.POKEMON,message.getId(), name, msg.getChannel().getId()));
            cki.save();
        }).start();
    }
    public static void wtcmain(Message msg) {
        new Thread(() -> {
            msg.getChannel().sendTyping().queue();
            Champions ChampionList = Champions.get();
            Champion c = ChampionList.get(new Random().nextInt(ChampionList.size()) - 1);
            String name = c.getName();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("CKI - [League of Legends]");
            eb.setDescription("c'est parti !");
            Message message = msg.getChannel().sendMessageEmbeds(eb.build()).complete();
            msg.delete().queue();
            ThreadChannel tc = message.createThreadChannel("Quel est ce champion ?").complete();
            cki.wtpThreads.put(tc.getId(), new cki(cki.Type.LOL,message.getId(), name, msg.getChannel().getId()));
            cki.save();
        }).start();
    }
}
