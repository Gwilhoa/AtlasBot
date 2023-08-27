package fr.atlas.command.CommandBuilder;

import fr.atlas.Request.User;
import fr.atlas.Request.Request;
import fr.atlas.Request.Squads;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static fr.atlas.BotDiscord.setError;

public class TopCommand {
    public static EmbedBuilder CommandTop(String name, Guild guild, net.dv8tion.jda.api.entities.Member sender) {
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
                setError(e);
            }
            if (sq != null) {
                sq.remove(0);
                sq.sort((o1, o2) -> Math.toIntExact(o2.getPointsTotal() - o1.getPointsTotal()));
                EmbedBuilder finalEmbedBuilder = embedBuilder;
                sq.forEach(squads -> {
                    List<User> mem = null;
                    try {
                        mem = Squads.getMembers(squads.getId());
                    } catch (IOException e) {
                        setError(e);
                    }
                    if (mem != null) {
                        if (squads.getPointsTotal() == 0) {
                            finalEmbedBuilder.addField(squads.getName() + " - Top 1 : ---", "Points : pas encore de point | Points d'équipe : " + squads.getPointsGiven(), false);

                        } else {
                            if (mem.get(0).getTitle() != null)
                                finalEmbedBuilder.addField(squads.getName() + " - Top 1 : "  + mem.get(0).getTitle() + " " + mem.get(0).getName(), "Points : " + squads.getPointsTotal() + " | Points d'équipe : " + squads.getPointsGiven(), false);
                            else
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
        } else if (name.equalsIgnoreCase("general")) {
            List<User> mem = null;
            try {
                mem = User.getMembers();
            } catch (ConnectException e) {
                return Request.DisconnectedEmbed();
            } catch (Exception e) {
                setError(e);
            }
            mem.sort((o1, o2) -> Math.toIntExact(o2.getPoints() - o1.getPoints()));
            embedBuilder.setTitle("Classement général");
            AtomicInteger i = new AtomicInteger(1);
            EmbedBuilder finalEmbedBuilder1 = embedBuilder;
            displaymember(sender, mem, i, finalEmbedBuilder1);
            embedBuilder.setColor(mem.get(0).getSquad().getColor());
            return embedBuilder;
        }  else {
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
                List<User> mem = null;
                try {
                    mem = Squads.getMembers(role.getId());
                } catch (ConnectException e) {
                    return Request.DisconnectedEmbed();
                } catch (IOException e) {
                    setError(e);
                }
                if (mem != null) {
                    embedBuilder.setTitle("Classement de l'escouade " + name);
                    embedBuilder.setColor(role.getColor());
                    EmbedBuilder finalEmbedBuilder = embedBuilder;
                    AtomicInteger i = new AtomicInteger(1);
                    displaymember(sender, mem, i, finalEmbedBuilder);
                } else {
                    embedBuilder.setTitle("Classement de l'escouade " + name);
                    embedBuilder.setDescription("aucun membre n'a été trouvé");
                    embedBuilder.setColor(Color.RED);
                }
            }
        }
        return embedBuilder;
    }

    private static void displaymember(net.dv8tion.jda.api.entities.Member sender, List<User> mem, AtomicInteger i, EmbedBuilder finalEmbedBuilder1) {
        mem.forEach(members -> {
            if (i.get() <= 10) {
                String name = members.getName();
                if (members.getTitle() != null)
                    name = "__" + members.getTitle() + "__ " + name;
                if (members.getId().equals(sender.getId()))
                    finalEmbedBuilder1.addField("▶️  "+i.get() + " - " + name + "  ◀️", "**Points : " + members.getPoints() + "**", false);
                else
                    finalEmbedBuilder1.addField(i.get() + " - " + name, "Points : " + members.getPoints(), false);
                i.getAndIncrement();
            }
        });
    }
}

