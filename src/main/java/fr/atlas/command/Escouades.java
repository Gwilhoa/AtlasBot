package fr.atlas.command;

import fr.atlas.Request.Achievement;
import fr.atlas.Request.User;
import fr.atlas.builder.Command;
import fr.atlas.command.CommandBuilder.ProfilCommand;
import fr.atlas.command.CommandBuilder.TopCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Escouades {
    @Command(name = "top", description = "Afficher le classement des Escouades")
    private void top(Message msg, String[] args) {
        if (args.length == 0)
            msg.replyEmbeds(TopCommand.CommandTop(null, msg.getGuild(), msg.getMember()).build()).queue();
        else if (args.length == 1)
            msg.replyEmbeds(TopCommand.CommandTop(args[0], msg.getGuild(), msg.getMember()).build()).queue();
        else
            msg.replyEmbeds(new EmbedBuilder().setColor(Color.RED).setTitle("Erreur").setDescription("Nombre d'arguments incorrect").build()).queue();
    }

    @Command(name = "profil", description = "Afficher les informations d'un membre", type = Command.ExecutorType.USER)
    private void profil(Message msg) {
        Member member = msg.getMember();
        if (msg.getMentions().getMembers().size() > 0) {
            member = msg.getMentions().getMembers().get(0);
        }
        msg.replyEmbeds(ProfilCommand.CommandProfil(member).build()).queue();
    }

    @Command(name = "gettitles", description = "Afficher nos titres [provisoire bientot remplacé par >titles]", type = Command.ExecutorType.USER)
    private void getTitles(Message msg) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Titres disponibles");
        eb.setColor(Color.GREEN);
        StringBuilder sb = new StringBuilder();
        java.util.List<String> titles = new ArrayList<>();
        try {
            titles = User.getTitles(msg.getMember());
        } catch (IOException e) {
            msg.getChannel().sendMessage("erreur de connexion").queue();
        }
        if (titles.isEmpty()) {
            eb.setDescription("Vous n'avez aucun titre");
        } else {
            for (String s : titles) {
                sb.append(s).append("\n");
            }
        }
        msg.getChannel().sendMessageEmbeds(eb.setDescription(sb.toString()).build()).queue();
    }

    @Command(name = "achievements", description = "Liste des achievements possible", type = Command.ExecutorType.USER)
    private void Achievement(Message msg) {
        java.util.List<Achievement> achievement;
        java.util.List<Achievement> memAchievement;
        try {
            achievement = Achievement.getAchievements();
            memAchievement = User.getAchievementsById(msg.getMember().getId());
        } catch (IOException e) {
            msg.getChannel().sendMessage("erreur de connexion").queue();
            return;
        }
        List<String> id = new ArrayList<>();
        ArrayList<MessageEmbed> ret = new ArrayList<>();
        for (Achievement a : memAchievement) {
            id.add(a.getId());
        }
        for (Achievement a : achievement) {
            System.out.println(a.getImage());
            EmbedBuilder eb = new EmbedBuilder().setTitle(a.getName()).setDescription(a.getDescription()).setThumbnail(a.getImage()).setFooter("Points : " + a.getPoints() + " | coins : " + a.getCoins()+ " | titre : " + a.getTitle());
            if (id.contains(a.getId()))
                eb.setColor(Color.GREEN);
            else
                eb.setColor(Color.RED);
            ret.add(eb.build());
            if (ret.size() == 10) {
                msg.getChannel().sendMessageEmbeds(ret).queue();
                ret.clear();
            }
        }
        msg.getChannel().sendMessageEmbeds(ret).queue();
    }

    @Command(name = "settitle", description = "Définir son titre principal [provisoire bientot remplacé par >titles]", type = Command.ExecutorType.USER)
    private void setTitle(String[] args, Message msg)
    {
        if (args.length == 1)
        {
            try {
                if (User.setTitle(msg.getMember(), args[0]))
                    msg.getChannel().sendMessage("titre défini").queue();
                else
                    msg.getChannel().sendMessage("vous n'avez pas ce titre").queue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            msg.getChannel().sendMessage("mauvais usage de la commande").queue();
    }
}
