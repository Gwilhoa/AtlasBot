package fr.cringebot.cringe.CommandBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import fr.cringebot.cringe.Request.Members;
import fr.cringebot.cringe.Request.Squads;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

public class top {
    public static void CommandTop(Message msg) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Classement des escouades");
        List<Squads> sq = new ArrayList<>();
        try {
            sq = Squads.getSquads();
        }
        catch (ConnectException e) {
            embedBuilder.setDescription("disconnected").setColor(Color.RED);
            msg.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        sq.sort((o1, o2) -> o2.getPointsTotal() - o1.getPointsTotal());
        sq.forEach(squads -> {
            List<Members> mem = null;
            try {
                mem = Squads.getMembers(squads.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mem == null)
                return;
            mem.sort((o1, o2) -> o2.getPoints() - o1.getPoints());
            embedBuilder.addField(squads.getName() + " - Top 1 : "+ mem.get(0).getName(), "Points : " + squads.getPointsTotal() + " | Points d'équipe : "+ squads.getPointsGiven(), false);
        });
        embedBuilder.setColor(sq.get(0).getColor());
        msg.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
