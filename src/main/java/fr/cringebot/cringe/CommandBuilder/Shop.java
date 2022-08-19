package fr.cringebot.cringe.CommandBuilder;

import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.objects.Item;
import fr.cringebot.cringe.objects.SelectOptionImpl;
import fr.cringebot.cringe.waifus.Waifu;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.awt.*;
import java.util.ArrayList;

public class Shop {
    private final static Integer PDCPRICE = 10;
    private final static Integer CEPRICE = 20;
    private final static Integer HEPRICE = 100;
    private final static Integer PBPRICE = 5;
    private final static Integer BFPRICE = 10;
    private final static Integer SBPKMPRICE = 0;


    public static Integer getCEPRICE() {
        return CEPRICE;
    }

    public static EmbedBuilder ShopDisplay(Member mem)
    {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.ORANGE).setTitle("le Shop");
        eb.setDescription("__Bonjour "+mem.getAsMention()+",que voulez vous acheter__\n\n" +
                "Pass-Brésil : direction le Brésil\n" +
                "Super bonbon : monte d'un niveau un pokemon");
        eb.setFooter("vous avez " + Squads.getstats(mem).getCoins() + " B2C");
        return eb;
    }

    public static EmbedBuilder ShopWaifuDisplay(Member mem)
    {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.ORANGE).setTitle("le Shop");
        eb.setDescription("__Bonjour "+mem.getAsMention()+",que voulez vous acheter__\n" +
                "Jeton de collection : 3 de la meme catégorie donne accès a une waifu\n" +
                "Chronomètre érotique : vous enlève 1h avant votre prochaine waifu\n" +
                "(4 max)Horloge érotique : vous enlève 30min à votre timer de chaque prochaine waifu\n" +
                "Bouquets de fleur : Augmente l'affection de votre waifu\n" +
                "boite de chocolat : Augmente l'affection de votre waifu\n" +
                "parfum : augmente l'affection de votre waifu\n" +
                "bracelet : augmente beaucoup l'affection de votre waifu\n");
        eb.setFooter("vous avez " + Squads.getstats(mem).getCoins() + " B2C");
        return eb;
    }

    public static SelectMenuImpl PrincipalMenu(Member mem)
    {
        ArrayList<SelectOption> options = new ArrayList<>();
        options.add(new SelectOptionImpl("pass-brésil : "+PBPRICE+ " B2C", "YAGTB"));
        options.add(new SelectOptionImpl("Super Bonbon : " + SBPKMPRICE + " B2C", "SBPKM"));
        options.add(new SelectOptionImpl("Shop Waifu", "SHOP_2"));
        options.add(new SelectOptionImpl("annuler", "stop"));
        return new SelectMenuImpl("shop;"+mem.getId(), "selectionnez un choix", 1, 1, false, options);
    }

    public static SelectMenuImpl WaifuMenu(Member mem)
    {
        ArrayList<SelectOption> options = new ArrayList<>();
        options.add(new SelectOptionImpl("Jeton de collection : "+PDCPRICE+" B2C", "PDCFU"));
        options.add(new SelectOptionImpl("Chronomètre érotique : "+CEPRICE+" B2C", "RDTPFU"));
        options.add(new SelectOptionImpl("horloge érotique : "+HEPRICE+" B2C", "RDTDFU"));
        options.add(new SelectOptionImpl("Bouquet de fleur : "+ BFPRICE +" B2C", "BFFU"));
        options.add(new SelectOptionImpl("annuler", "stop"));
        return new SelectMenuImpl("shop;"+mem.getId(), "selectionnez un choix", 1, 1, false, options);
    }

    public static void ShopSelectMenu(SelectMenuInteraction event) {
        if (!event.getMember().getId().equals(event.getSelectMenu().getId().split(";")[1]))
            event.reply("tu es pas la personne attendu").setEphemeral(true).queue();
        else if (event.getSelectedOptions().get(0).getValue().equals("stop")) {
            event.getMessage().delete().queue();
            event.reply("merci, à bientot").setEphemeral(true).queue();
        }
        else if (event.getSelectedOptions().get(0).getValue().equals("shop_2"))
        {
            event.editMessageEmbeds(ShopWaifuDisplay(event.getMember()).build()).setActionRow(WaifuMenu(event.getMember())).queue();
        }
        else if (event.getSelectedOptions().get(0).getValue().equals("YAGTB"))
        {
            if (Squads.getstats(event.getMember()).getCoins() >= PBPRICE) {
                panelamount(event.getMember(), Item.Items.PB.getStr(), PBPRICE, 1, event);
            }
            else
                event.reply("tu as pas assez d'argent").setEphemeral(true).queue();
        }
        else if (event.getSelectedOptions().get(0).getValue().equals("RDTDFU"))
        {
            if (Squads.getstats(event.getMember()).getAmountItem(Item.Items.HE.getStr()) >= 4)
            {
                event.reply("désolé je n'en n'ai plus").setEphemeral(true).queue();
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
                panelamount(event.getMember(), Item.Items.CE.getStr(), CEPRICE, 1, event);
            } else
            {
                event.reply("tu as pas assez d'argent").setEphemeral(true).queue();
            }
        } else if (event.getSelectedOptions().get(0).getValue().equals("BFFU")) {
            if (Squads.getstats(event.getMember()).getCoins() >= BFPRICE.longValue())
            {
                panelamount(event.getMember(), Item.Items.BF.getStr(), BFPRICE, 1, event);
            } else
            {
                event.reply("tu as pas assez d'argent").setEphemeral(true).queue();
            }
        }
        else {
            event.reply("coming soon").setEphemeral(true).queue();
        }
    }

    public static void buy(Member mem, String item, int prix, int amount)
    {
        Squads.getstats(mem).addItem(item, amount);
        Squads.getstats(mem).removeCoins(prix*amount);
    }

    public static void panelamount(Member mem, String item, int prix, int amount, ButtonInteractionEvent event)
    {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("acheter des "+ item);
        eb.setDescription("voulez vous acheter " + amount + " " + item + "?\nça coutera : "+ prix*amount + "B2C");
        eb.setFooter("tu as "+ Squads.getstats(mem).getCoins() +"B2C");
        ArrayList<ActionRow> bttns = new ArrayList<>();
        bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item+";"+prix+";"+amount, "acheter", ButtonStyle.SUCCESS, false, null)));
        bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";stop", "annuler l'achat", ButtonStyle.DANGER, false, null)));
        if (amount == 1)
            bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item+";"+prix+";"+0, "-1", ButtonStyle.PRIMARY, true, null)));
        else
            bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item+";"+prix+";"+(amount-1), "-1", ButtonStyle.PRIMARY, false, null)));

        if ((long) prix * (amount+1) > Squads.getstats(mem).getCoins())
            bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item+";"+prix+";"+(amount+1), "+1", ButtonStyle.SECONDARY, true, null)));
        else
            bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item+";"+prix+";"+(amount+1), "+1", ButtonStyle.SECONDARY, false, null)));
        event.editMessageEmbeds(eb.build()).setActionRows(bttns).queue();
    }
    public static void panelamount(Member mem, String item, int prix, int amount, SelectMenuInteraction event)
    {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("acheter des "+ item);
        eb.setDescription("voulez vous acheter " + amount + " " + item + "?\nça coutera : "+ prix*amount + "B2C");
        eb.setFooter("tu as "+ Squads.getstats(mem).getCoins() +"B2C");
        ArrayList<ActionRow> bttns = new ArrayList<>();
        bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item+";"+prix+";"+amount, "acheter", ButtonStyle.SUCCESS, false, null)));
        bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";stop", "annuler l'achat", ButtonStyle.DANGER, false, null)));
        if (amount == 1)
            bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item+";"+prix+";"+0, "-1", ButtonStyle.PRIMARY, true, null)));
        else
            bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item+";"+prix+";"+(amount-1), "-1", ButtonStyle.PRIMARY, false, null)));

        if ((long) prix * (amount+1) > Squads.getstats(mem).getCoins())
            bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item+";"+prix+";"+(amount+1), "+1", ButtonStyle.SECONDARY, true, null)));
        else
            bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item+";"+prix+";"+(amount+1), "+1", ButtonStyle.SECONDARY, false, null)));
        event.editMessageEmbeds(eb.build()).setActionRows(bttns).queue();
    }

    public static void CollecSelecMenu(SelectMenuInteractionEvent event) throws InterruptedException {
        if (!event.getMember().getId().equals(event.getMessage().getContentRaw().split("\n")[1]))
        {
            event.reply("tu es pas la personne attendu").setEphemeral(true).queue();
            return;
        }
        if (event.getSelectedOptions().get(0).getValue().equals("previous"))
        {
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
            event.editMessage(event.getMessage().getContentRaw()).setActionRow(selectionMenu).queue();
        }
        if (event.getSelectedOptions().get(0).getValue().equals("next"))
        {
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
            event.editMessage(event.getMessage().getContentRaw()).setActionRow(selectionMenu).queue();
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
