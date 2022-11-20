package fr.cringebot.cringe.CommandBuilder;

import fr.cringebot.cringe.Request.Members;
import fr.cringebot.cringe.Request.Request;
import fr.cringebot.cringe.Request.Squads;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TopCommand {
    public static EmbedBuilder CommandTop(String name, Guild guild) {
        if (name != null && name.equals("scoreboard"))
            name = null;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (name == null) {
            embedBuilder.setTitle("Classement des escouades");
            List<Squads> sq = null;
            try {
                sq = Squads.getSquads();
            } catch (ConnectException e) {
                return Request.DisconnectedEmbed();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (sq != null) {
                sq.sort((o1, o2) -> o2.getPointsTotal() - o1.getPointsTotal());
                EmbedBuilder finalEmbedBuilder = embedBuilder;
                sq.forEach(squads -> {
                    List<Members> mem = null;
                    try {
                        mem = Squads.getMembers(squads.getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (mem != null) {
                        mem.sort((o1, o2) -> o2.getPoints() - o1.getPoints());
                        if (squads.getPointsTotal() == 0) {
                            finalEmbedBuilder.addField(squads.getName() + " - Top 1 : ---", "Points : pas encore de point | Points d'équipe : " + squads.getPointsGiven(), false);

                        } else {
                            finalEmbedBuilder.addField(squads.getName() + " - Top 1 : " + mem.get(0).getName(), "Points : " + squads.getPointsTotal() + " | Points d'équipe : " + squads.getPointsGiven(), false);
                        }
                    } else {
                        finalEmbedBuilder.addField(squads.getName(), "Points : " + squads.getPointsTotal() + " | Points d'équipe : " + squads.getPointsGiven(), false);
                    }
                });
                embedBuilder.setColor(sq.get(0).getColor());
            } else {
                embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Classement des escouades");
                embedBuilder.setDescription("aucune escouade n'a été trouvée");
                embedBuilder.setColor(Color.RED);
            }
        } else {
            List<Role> roleList = guild.getRolesByName(name, true);
            if (roleList.size() != 1)
            {
                embedBuilder.setTitle("Classement de l'escouade " + name);
                embedBuilder.setDescription("L'escouade n'a pas été trouvée ou plusieurs role tiens le meme nom");
                embedBuilder.setColor(Color.RED);
            }
            else
            {
                Role role = roleList.get(0);
                List<Members> mem = null;
                try {
                    mem = Squads.getMembers(role.getId());
                } catch (ConnectException e) {
                    return Request.DisconnectedEmbed();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mem != null) {
                    mem.sort((o1, o2) -> o2.getPoints() - o1.getPoints());
                    embedBuilder.setTitle("Classement de l'escouade " + name);
                    embedBuilder.setColor(role.getColor());
                    EmbedBuilder finalEmbedBuilder = embedBuilder;
                    AtomicInteger i = new AtomicInteger(1);
                    mem.forEach(members -> {
                        if (i.get() <= 10) {
                            finalEmbedBuilder.addField("#" + i.get() + " " + members.getName(), "Points : " + members.getPoints(), false);
                            i.getAndIncrement();
                        }
                    });
                } else {
                    embedBuilder.setTitle("Classement de l'escouade " + name);
                    embedBuilder.setDescription("aucun membre n'a été trouvé");
                    embedBuilder.setColor(Color.RED);
                }
            }
        }
        return embedBuilder;
    }
    public ArrayList<Button> getButtons(String name, Integer page) {
        ArrayList<Button> buttons = new ArrayList<>();
        return buttons;
    }
}

