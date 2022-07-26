package fr.cringebot.cringe.CommandBuilder;

import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.objects.Item;
import fr.cringebot.cringe.objects.SelectOptionImpl;
import fr.cringebot.cringe.waifus.Waifu;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.awt.*;
import java.util.ArrayList;

public class Shop {
    private final static Integer PDCPRICE = 10;
    private final static Integer CEPRICE = 20;
    private final static Integer HEPRICE = 100;
    private final static Integer PBPRICE = 5;
    private final static Integer JJFUPRICE = 0;
    private final static Integer SBPKMPRICE = 0;

    public static EmbedBuilder ShopDisplay(Member mem)
    {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.ORANGE).setTitle("le Shop");
        eb.setDescription("Bonjour que voulez vous acheter\n" +
                "Pièce de collection : 3 de la meme catégorie donne accès a une waifu\n" +
                "Chronomètre érotique : vous enlève 30min avant votre prochaine waifu\n" +
                "(4 max)Horloge érotique : vous enlève 30min à votre timer de chaque prochaine waifu\n" +
                "Pass-Brésil : you are going to bresil\n" +
                "joujou pour waifu : augmente l'affection de votre waifu\n" +
                "Super bonbon : monte d'un niveau un pokemon");
        eb.setFooter("vous avez " + Squads.getstats(mem).getCoins() + " B2C");
        return eb;
    }

    public static SelectMenuImpl PrincipalMenu()
    {
        ArrayList<SelectOption> options = new ArrayList<>();
        options.add(new SelectOptionImpl("Pièce de collection : "+PDCPRICE+" B2C", "PDCFU"));
        options.add(new SelectOptionImpl("Chronomètre érotique : "+CEPRICE+" B2C", "RDTPFU"));
        options.add(new SelectOptionImpl("horloge érotique : "+HEPRICE+" B2C", "RDTDFU"));
        options.add(new SelectOptionImpl("pass-brésil : "+PBPRICE+ " B2C", "YAGTB"));
        options.add(new SelectOptionImpl("Joujou pour waifu : "+ JJFUPRICE+" B2C", "JJFU"));
        options.add(new SelectOptionImpl("Super Bonbon : " + SBPKMPRICE + " B2C", "SBPKM"));
        options.add(new SelectOptionImpl("annuler", "stop"));
        return new SelectMenuImpl("shop", "selectionnez un choix", 1, 1, false, options);
    }

    public static void ShopSelectMenu(SelectMenuInteraction event) {
        if (event.getSelectedOptions().get(0).getValue().equals("stop")) {
            event.getMessage().delete().queue();
            event.reply("merci, à bientot").setEphemeral(true).queue();
        }
        else if (event.getSelectedOptions().get(0).getValue().equals("YAGTB"))
        {
            if (Squads.getstats(event.getMember()).getCoins() >= PBPRICE) {
                Squads.getstats(event.getMember()).removeCoins(PBPRICE.longValue());
                Squads.getstats(event.getMember()).addItem(Item.Items.PB.getStr());
                event.reply("tu as acheté un pass brésil tu en as désormais " + Squads.getstats(event.getMember()).getAmountItem(Item.Items.PB.getStr())).queue();
            }
            else
                event.reply("tu as pas assez d'argent").setEphemeral(true).queue();
        }
        else if (event.getSelectedOptions().get(0).getValue().equals("RDTDFU"))
        {
            if (Squads.getstats(event.getMember()).getAmountItem(Item.Items.HE.getStr()) >= 4)
            {
                event.reply("désolé je n'en n'ai plus").queue();
            }
            else if (Squads.getstats(event.getMember()).getCoins() >= HEPRICE) {
                Squads.getstats(event.getMember()).removeCoins(HEPRICE.longValue());
                Squads.getstats(event.getMember()).addItem(Item.Items.HE.getStr());
                event.reply("tu as acheté une horloge érotique tu en as désormais " + Squads.getstats(event.getMember()).getAmountItem(Item.Items.HE.getStr())).queue();
            }
            else
                event.reply("tu as pas assez d'argent").setEphemeral(true).queue();
        }
        else if (event.getSelectedOptions().get(0).getValue().equals("PDCFU"))
        {
            if (Squads.getstats(event.getMember()).getCoins() < PDCPRICE)
                event.reply("tu as pas assez d'argent").setEphemeral(true).queue();
            else {
                ArrayList<SelectOption> options = new ArrayList<>();
                int i = 0;
                while (10 > i)
                {
                    options.add(new SelectOptionImpl(Waifu.getAllOrigins().get(i), Waifu.getAllOrigins().get(i)));
                    i++;
                }
                options.add(new SelectOptionImpl("autres", "next"));
                SelectMenuImpl selectionMenu = new SelectMenuImpl("collection0", "selectionnez un choix", 1, 1, false, options);
                event.reply("de quelle collection votre pièce ?\n"+event.getMember().getId())
                        .addActionRow(selectionMenu).queue();
            }
        } else if (event.getSelectedOptions().get(0).getValue().equals("RDTPFU")){
            if (Squads.getstats(event.getMember()).getCoins() >= CEPRICE.longValue())
            {
                if (Waifu.removeTime(event.getMember().getId(), 1800000L))
                {
                    event.reply("Votre chronomètre a fait effet, je ressens une excitation").queue();
                    Squads.getstats(event.getMember()).removeCoins(CEPRICE.longValue());
                } else {
                    event.reply("Tiens ça a pas marché, réessayez plus tard").queue();
                }
            } else
            {
                event.reply("tu as pas assez d'argent").setEphemeral(true).queue();
            }
        }
        else {
            event.reply("coming soon").setEphemeral(true).queue();
        }
    }

    public static void CollecSelecMenu(SelectMenuInteractionEvent event) throws InterruptedException {
        if (!event.getMember().getId().equals(event.getMessage().getContentRaw().split("\n")[1]))
        {
            event.reply("tu es pas la personne attendu").setEphemeral(true).queue();
            return;
        }
        if (event.getSelectedOptions().get(0).getValue().equals("previous"))
        {
            event.reply("oui").complete().deleteOriginal().queue();
            int nb = (Integer.parseInt(event.getSelectMenu().getId().substring("collection".length())) - 1) * 10;
            int i = nb;
            ArrayList<SelectOption> options = new ArrayList<>();
            while (i < nb + 10)
            {
                options.add(new SelectOptionImpl(Waifu.getAllOrigins().get(i), Waifu.getAllOrigins().get(i)));
                i++;
            }
            options.add(new SelectOptionImpl("autres", "next"));
            if (nb != 0)
                options.add(new SelectOptionImpl("page d'avant", "previous"));
            SelectMenuImpl selectionMenu = new SelectMenuImpl("collection"+nb+"", "selectionnez un choix", 1, 1, false, options);
            event.getMessage().editMessage(event.getMessage().getContentRaw()).setActionRow(selectionMenu).queue();
        }
        if (event.getSelectedOptions().get(0).getValue().equals("next"))
        {
            event.reply("oui").complete().deleteOriginal().queue();
            int nb = (Integer.parseInt(event.getSelectMenu().getId().substring("collection".length())) + 1) * 10;
            int i = nb;
            ArrayList<SelectOption> options = new ArrayList<>();
            while (i < nb + 10 && i < Waifu.getAllOrigins().size())
            {
                options.add(new SelectOptionImpl(Waifu.getAllOrigins().get(i), Waifu.getAllOrigins().get(i)));
                i++;
            }
            if (i >= Waifu.getAllOrigins().size())
                options.add(new SelectOptionImpl("autres", "next"));
            options.add(new SelectOptionImpl("page d'avant", "previous"));
            SelectMenuImpl selectionMenu = new SelectMenuImpl("collection"+nb+"", "selectionnez un choix", 1, 1, false, options);
            event.getMessage().editMessage(event.getMessage().getContentRaw()).setActionRow(selectionMenu).queue();
        }
        else {
            if (event.getSelectedOptions().get(0).getValue().equals("B2K") || Squads.getstats(event.getMember()).getWaifubyOrigin(event.getSelectedOptions().get(0).getValue()).size() == Waifu.getWaifusByOrigin(event.getSelectedOptions().get(0).getValue()).size())
            {
                event.getMessage().delete().queue();
                event.reply("désolé j'en ai plus").queue();
                return;
            }
            Squads.getstats(event.getMember()).addCollection(event.getSelectedOptions().get(0).getValue(), event.getMessage());
            event.getMessage().delete().queue();
            event.reply("tu as acheté un jeton "+ event.getSelectedOptions().get(0).getValue() + "\ntu en as " + Squads.getstats(event.getMember()).getCollection().get(event.getSelectedOptions().get(0).getValue())).queue();
            Squads.getstats(event.getMember()).removeCoins(PDCPRICE.longValue());
        }
    }
}
