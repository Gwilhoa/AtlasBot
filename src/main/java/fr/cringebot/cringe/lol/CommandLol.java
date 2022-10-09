package fr.cringebot.cringe.lol;

import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import fr.cringebot.cringe.builder.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;


import java.awt.*;
import java.util.List;

public class CommandLol {
    @Command(name = "getprofil_lol", type = Command.ExecutorType.USER, description = "récupère les donnée d'un summoner")
    private void getstats(Message msg) {
        final Summoner summoner = Summoner.named(msg.getContentRaw().substring(">getprofil_lol ".length())).get();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("profil de " + summoner.getName());
        eb.setFooter("id : "+ summoner.getId());
        eb.setDescription("niveau : "+ summoner.getLevel() + "\n");
        if (summoner.isInGame())
            eb.appendDescription("en jeu...").setColor(Color.GREEN);
        else
            eb.setColor(Color.RED);
        eb.setAuthor("league of legends", summoner.getProfileIcon().getImage().getURL());
        msg.getChannel().sendMessageEmbeds(eb.build()).queue();

    }
}
