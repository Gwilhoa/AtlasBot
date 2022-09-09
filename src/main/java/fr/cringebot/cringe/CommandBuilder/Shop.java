package fr.cringebot.cringe.CommandBuilder;

import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.objects.Item;
import fr.cringebot.cringe.objects.SelectOptionImpl;
import fr.cringebot.cringe.pokemon.objects.Type;
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
import java.util.Objects;

public class Shop {

    public static EmbedBuilder ShopDisplay(Member mem) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.ORANGE).setTitle("le Shop");
        eb.setDescription("__Bonjour "+mem.getAsMention()+",que voulez vous acheter ?__\n\n" +
                "dans quelle catégorie voulez-vous rechercher...");
        eb.setFooter("vous avez " + Squads.getstats(mem).getCoins() + " B2C");
        return eb;
    }

    public static EmbedBuilder ShopWaifuDisplay(Member mem)
    {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.ORANGE).setTitle("le Shop");
        eb.setDescription("__Bonjour "+mem.getAsMention()+",que voulez vous acheter__\n" +
                "Jeton de collection : 3 de la meme catégorie donne accès a une waifu\n" +
                "Chronomètre érotique : vous enlève 30min avant votre prochaine waifu\n" +
                "(4 max)Horloge érotique : vous enlève 30min à votre timer de chaque prochaine waifu\n" +
                "Bouquets de fleur : donne 1000 d'affection aux waifus\n" +
                "boite de chocolat : donne 5000 d'affection aux waifus\n" +
                "parfum : donne 10000 d'affection aux waifus\n" +
                "bracelet : donne 16000 d'affection aux waifus\n");
        eb.setFooter("vous avez " + Squads.getstats(mem).getCoins() + " B2C");
        return eb;
    }

    public static SelectMenuImpl PrincipalMenu(Member mem)
    {
        ArrayList<SelectOption> options = new ArrayList<>();
        options.add(new SelectOptionImpl("Jeton de collection : " + 10 + " B2C", "PDCFU"));
        for (Item.type tpe : Item.type.values())
        {
            options.add(new SelectOptionImpl("shop "+tpe.getName(), "shop;"+tpe.getId()));
        }
        options.add(new SelectOptionImpl("annuler", "stop"));
        return new SelectMenuImpl("shop;"+mem.getId(), "selectionnez un choix", 1, 1, false, options);
    }

    public static SelectMenuImpl ItemMenu(Member mem, Item.type tpe) {
        ArrayList<SelectOption> options = new ArrayList<>();
        for (Item.Items item : Item.Items.getItemByType(tpe)) {
            if (item.getPrice() != -1)
                options.add(new SelectOptionImpl(item.getName() +" : " + item.getPrice() + " B2C", "buyItem;"+item.getId()));
        }
        options.add(new SelectOptionImpl("annuler", "stop"));
        return new SelectMenuImpl("shop;" + mem.getId(), "selectionnez un choix", 1, 1, false, options);
    }

    public static void ShopSelectMenu(SelectMenuInteraction event) {
        if (!event.getMember().getId().equals(event.getSelectMenu().getId().split(";")[1]))
            event.reply("tu es pas la personne attendu").setEphemeral(true).queue();
        else if (event.getSelectedOptions().get(0).getValue().equals("stop")) {
            event.getMessage().delete().queue();
            event.reply("merci, à bientot").setEphemeral(true).queue();
        }
        else if (event.getSelectedOptions().get(0).getValue().equals("PDCFU")) {
            if (Squads.getstats(event.getMember()).getCoins() < 10)
                event.reply("tu as pas assez d'argent").setEphemeral(true).queue();
            else {
                ArrayList<SelectOption> options = new ArrayList<>();
                int i = 0;
                while (10 > i) {
                    options.add(new SelectOptionImpl(Waifu.getAllOrigins().get(i), Waifu.getAllOrigins().get(i)));
                    i++;
                }
                options.add(new SelectOptionImpl("autres", "next"));
                SelectMenuImpl selectionMenu = new SelectMenuImpl("collection;"+event.getMember().getId()+";0", "selectionnez un choix", 1, 1, false, options);
                event.editMessageEmbeds(new EmbedBuilder().setTitle("Shop").setDescription("de quelle collection seriez vous interessé").build())
                        .setActionRow(selectionMenu).queue();
            }
        } else if (event.getSelectedOptions().get(0).getValue().startsWith("buyItem")) {
            if (Item.Items.getItemById(Integer.parseInt(event.getSelectedOptions().get(0).getValue().split(";")[1])).getPrice() > Squads.getstats(event.getMember()).getCoins())
                event.reply("tu as pas assez d'argent").setEphemeral(true).queue();
            else
                panelamount(event.getMember(),Item.Items.getItemById(Integer.parseInt(event.getSelectedOptions().get(0).getValue().split(";")[1])), 1, event);
        } else if (event.getSelectedOptions().get(0).getValue().startsWith("shop")) {
            Item.type tpe = Item.type.getTypeById(Integer.parseInt(event.getSelectedOptions().get(0).getValue().split(";")[1]));
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Shop");
            eb.setFooter("vous avez " + Squads.getstats(event.getMember()).getCoins() + " B2C");
            eb.setDescription("__shop "+tpe.getName() + "__\n\n");
            for (Item.Items item : Item.Items.getItemByType(tpe))
            {
                if (item.getPrice() != -1)
                    eb.appendDescription(item.getName()).appendDescription(" ").appendDescription(item.getPrice()+" B2C\n");
            }
            event.editMessageEmbeds(eb.build()).setActionRow(ItemMenu(event.getMember(), tpe)).queue();
        }
    }

    public static void buy(Member mem, Integer itemId, int prix, int amount)
    {
        Squads.getstats(mem).addItem(itemId, amount);
        Squads.getstats(mem).removeCoins(prix*amount);
    }

    public static void panelamount(Member mem, Item.Items item, int amount, ButtonInteractionEvent event)
    {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("acheter des "+ item.getName());
        eb.setDescription("voulez vous acheter " + amount + " " + item.getName() + "?\nça coutera : "+ item.getPrice()*amount + "B2C");
        eb.setFooter("tu as "+ Squads.getstats(mem).getCoins() +"B2C");
        ArrayList<ActionRow> bttns = new ArrayList<>();
        bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item.getId()+";"+item.getPrice()+";"+amount, "acheter", ButtonStyle.SUCCESS, false, null)));
        bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";0", "annuler l'achat", ButtonStyle.DANGER, false, null)));
        if (amount == 1)
            bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item.getId()+";"+item.getPrice()+";"+0, "-1", ButtonStyle.PRIMARY, true, null)));
        else
            bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item.getId()+";"+item.getPrice()+";"+(amount-1), "-1", ButtonStyle.PRIMARY, false, null)));

        if ((long) item.getPrice() * (amount+1) > Squads.getstats(mem).getCoins())
            bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item.getId()+";"+item.getPrice()+";"+(amount+1), "+1", ButtonStyle.SECONDARY, true, null)));
        else
            bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item.getId()+";"+item.getPrice()+";"+(amount+1), "+1", ButtonStyle.SECONDARY, false, null)));
        event.editMessageEmbeds(eb.build()).setActionRows(bttns).queue();
    }
    public static void panelamount(Member mem,  Item.Items item, int amount, SelectMenuInteraction event)
    {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("acheter des "+ item.getName());
        eb.setDescription("voulez vous acheter " + amount + " " + item.getName() + "?\nça coutera : "+ item.getPrice()*amount + "B2C");
        eb.setFooter("tu as "+ Squads.getstats(mem).getCoins() +"B2C");
        ArrayList<ActionRow> bttns = new ArrayList<>();
        bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item.getId()+";"+item.getPrice()+";"+amount, "acheter", ButtonStyle.SUCCESS, false, null)));
        bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";0", "annuler l'achat", ButtonStyle.DANGER, false, null)));
        if (amount == 1)
            bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item.getId()+";"+item.getPrice()+";"+0, "-1", ButtonStyle.PRIMARY, true, null)));
        else
            bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item.getId()+";"+item.getPrice()+";"+(amount-1), "-1", ButtonStyle.PRIMARY, false, null)));

        if ((long) item.getPrice() * (amount+1) > Squads.getstats(mem).getCoins())
            bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item.getId()+";"+item.getPrice()+";"+(amount+1), "+1", ButtonStyle.SECONDARY, true, null)));
        else
            bttns.add(ActionRow.of(new ButtonImpl("shop_"+mem.getId()+";"+item.getId()+";"+item.getPrice()+";"+(amount+1), "+1", ButtonStyle.SECONDARY, false, null)));
        event.editMessageEmbeds(eb.build()).setActionRows(bttns).queue();
    }

    public static void CollecSelecMenu(SelectMenuInteractionEvent event) throws InterruptedException {
        if (!event.getSelectMenu().getId().split(";")[1].equals(event.getMember().getId()))
        {
            event.reply("tu es pas la personne attendu").setEphemeral(true).queue();
        } else {
            int nb = Integer.parseInt(event.getSelectMenu().getId().split(";")[2]);
            if (event.getSelectedOptions().get(0).getValue().equals("previous")) {
                ArrayList<SelectOption> options = new ArrayList<>();
                if (nb - 1 != 0)
                    options.add(new SelectOptionImpl("page d'avant", "previous"));
                int i = (nb - 1)*10;
                int i2 = i;
                while (i2 + 10 > i) {
                    options.add(new SelectOptionImpl(Waifu.getAllOrigins().get(i), Waifu.getAllOrigins().get(i)));
                    i++;
                }
                SelectMenuImpl selectionMenu = new SelectMenuImpl("collection;"+event.getMember().getId()+";"+(nb - 1), "selectionnez un choix", 1, 1, false, options);
                event.editMessageEmbeds(new EmbedBuilder().setTitle("Shop").setDescription("Prenez votre temps !").build()).setActionRow(selectionMenu).queue();
            }
            else if (event.getSelectedOptions().get(0).getValue().equals("next")) {
                int i = (nb + 1)*10;
                int i2 = i;
                ArrayList<SelectOption> options = new ArrayList<>();
                while (i2+10 > i && Waifu.getAllOrigins().size() > i ) {
                    options.add(new SelectOptionImpl(Waifu.getAllOrigins().get(i), Waifu.getAllOrigins().get(i)));
                    i++;
                }
                if (i < Waifu.getAllOrigins().size())
                    options.add(new SelectOptionImpl("page d'après", "next"));
                SelectMenuImpl selectionMenu = new SelectMenuImpl("collection;"+event.getMember().getId()+";"+(nb + 1), "selectionnez un choix", 1, 1, false, options);
                event.editMessageEmbeds(new EmbedBuilder().setTitle("Shop").setDescription("Prenez votre temps !").build()).setActionRow(selectionMenu).queue();
            } else {
                if (Squads.getstats(event.getMember()).isCompleteCollection(event.getSelectedOptions().get(0).getLabel()) || event.getSelectedOptions().get(0).getLabel().equals("B2K")) {
                    ArrayList<SelectOption> options = new ArrayList<>();
                    options.add(new SelectOptionImpl("0", "0"));
                    SelectMenuImpl selectionMenu = new SelectMenuImpl("collection;"+event.getMember().getId()+";"+nb, "selectionnez un choix", 1, 1, true, options);
                    event.editMessageEmbeds(new EmbedBuilder().setTitle("Shop").setDescription("Désolé j'en ai plus").build()).setActionRow(selectionMenu).queue();
                } else {
                    ArrayList<SelectOption> options = new ArrayList<>();
                    options.add(new SelectOptionImpl("0", "0"));
                    EmbedBuilder eb = Squads.getstats(event.getMember()).addCollection(event.getSelectedOptions().get(0).getLabel(), event.getMember());
                    Squads.getstats(event.getMember()).removeCoins(10);
                    SelectMenuImpl selectionMenu = new SelectMenuImpl("collection;"+event.getMember().getId()+";"+nb, "selectionnez un choix", 1, 1, true, options);
                    event.editMessageEmbeds(Objects.requireNonNullElseGet(eb, () -> new EmbedBuilder().setTitle("Shop").setDescription("tu as acheté un jeton " + event.getSelectedOptions().get(0).getLabel())).build()).setActionRow(selectionMenu).queue();
                }
            }
        }

    }
}
