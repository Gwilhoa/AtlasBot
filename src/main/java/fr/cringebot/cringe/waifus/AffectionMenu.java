package fr.cringebot.cringe.waifus;

import fr.cringebot.cringe.escouades.SquadMember;
import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.objects.Item;
import fr.cringebot.cringe.objects.SelectOptionImpl;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.util.ArrayList;

public class AffectionMenu {
    public static SelectMenuImpl getMenu(Member mem, Waifu w) {
        ArrayList<SelectOption> options = new ArrayList<>();
        options.add(new SelectOptionImpl("Bouquet de fleur", "BFFU"));
        options.add(new SelectOptionImpl("Boite de chocolat", "BDCFU"));
        options.add(new SelectOptionImpl("Parfum", "PRFU"));
        options.add(new SelectOptionImpl("Bracelet", "BRCFU"));
        options.add(new SelectOptionImpl("Collier", "CLLFU"));
        options.add(new SelectOptionImpl("Bague", "BGEFU"));
        options.add(new SelectOptionImpl("Une soirée au restaurant", "RESFU"));
        options.add(new SelectOptionImpl("nuit d'hotel", "HOFU"));
        return new SelectMenuImpl("AFF;" + mem.getId() + ";" + w.getId(), "rendre heureux", 1, 1, false, options);
    }

    public static void SelectMenu(SelectMenuInteractionEvent event) throws InterruptedException {
        if (event.getSelectMenu().getId().split(";")[1].equals(event.getMember().getId())) {
            String id = event.getSelectedOptions().get(0).getValue();
            SquadMember sm = Squads.getstats(event.getMember());
            InvWaifu iw = sm.getWaifus().get(Integer.parseInt(event.getSelectMenu().getId().split(";")[2]));
            if (id.equals("BFFU")) {
                if (sm.getAmountItem(Item.Items.BF.getStr()) > 0) {
                    iw.addXp(1000);
                    sm.removeItem(Item.Items.BF.getStr());
                    event.editMessageEmbeds(WaifuCommand.EmbedInfo(iw.getWaifu(), event.getMember()).build()).setActionRow(getMenu(event.getMember(), iw.getWaifu())).queue();
                } else {
                    event.reply("tu as pas de bouquet de fleur").setEphemeral(true).queue();
                }
            } else if (id.equals("BDCFU")) {
                if (sm.getAmountItem(Item.Items.BDCFU.getStr()) > 0) {
                    iw.addXp(5000);
                    sm.removeItem(Item.Items.BDCFU.getStr());
                    event.editMessageEmbeds(WaifuCommand.EmbedInfo(iw.getWaifu(), event.getMember()).build()).setActionRow(getMenu(event.getMember(), iw.getWaifu())).queue();
                } else {
                    event.reply("tu as pas de boite de chocolat").setEphemeral(true).queue();
                }
            } else if (id.equals("PRFU")) {
                if (sm.getAmountItem(Item.Items.PRFU.getStr()) > 0) {
                    iw.addXp(10000);
                    sm.removeItem(Item.Items.PRFU.getStr());
                    event.editMessageEmbeds(WaifuCommand.EmbedInfo(iw.getWaifu(), event.getMember()).build()).setActionRow(getMenu(event.getMember(), iw.getWaifu())).queue();
                } else {
                    event.reply("tu as pas de flacon de parfum").setEphemeral(true).queue();
                }
            } else if (id.equals("BRCFU")) {
                if (sm.getAmountItem(Item.Items.BRFU.getStr()) > 0) {
                    iw.addXp(16000);
                    sm.removeItem(Item.Items.BRFU.getStr());
                    event.editMessageEmbeds(WaifuCommand.EmbedInfo(iw.getWaifu(), event.getMember()).build()).setActionRow(getMenu(event.getMember(), iw.getWaifu())).queue();
                } else {
                    event.reply("tu as pas de flacon de parfum").setEphemeral(true).queue();
                }
            } else {
                event.reply("coming soon").setEphemeral(true).queue();
            }
        } else {
            event.reply("tu es pas la personne attendu").setEphemeral(true).queue();
        }
    }
}
