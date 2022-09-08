package fr.cringebot.cringe.CommandBuilder;

import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.objects.Item;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.util.*;

public class Inventory {
    public static EmbedBuilder getInventory(Member member)
    {
        int i = 0;
        HashMap<Integer, Integer> inv = Squads.getstats(member).getInventory02();
        EmbedBuilder eb = new EmbedBuilder();
        StringBuilder sb = new StringBuilder();
        eb.setTitle("Inventaire de " + member.getEffectiveName());
        eb.setColor(Squads.getSquadByMember(member).getSquadRole(member.getGuild()).getColor());
        int[] items = inv.keySet().stream().mapToInt(Number::intValue).toArray();
        Arrays.sort(items);
        for (int id : items) {
            sb.append(Item.Items.getItemById(id).getName()).append(" x").append(inv.get(id));
            if (i == 2) {
                sb.append('\n');
                i = 0;
            } else {
                i++;
                sb.append(" | ");
            }
        }
        eb.setDescription(sb);
        return eb;
    }

    public static void setCollectionInv(ButtonInteractionEvent event) {
        if (!event.getButton().getId().split("_")[1].equals(event.getMember().getId()))
        {
            event.reply("tu es pas la personne attendu").setEphemeral(true).queue();
        } else {
            int i = 0;
            HashMap<String, Integer> inv = Squads.getstats(event.getMember()).getCollection();
            EmbedBuilder eb = new EmbedBuilder();
            StringBuilder sb = new StringBuilder();
            eb.setTitle("Inventaire de " + event.getMember().getEffectiveName());
            eb.setColor(Squads.getSquadByMember(event.getMember()).getSquadRole(event.getMember().getGuild()).getColor());
            for (String str : inv.keySet()) {
                sb.append(str).append(" x").append(inv.get(str));
                if (i == 2) {
                    sb.append('\n');
                    i = 0;
                } else {
                    i++;
                    sb.append(" | ");
                }
            }
            eb.setDescription(sb);
            event.editMessageEmbeds(eb.build()).setActionRow(new ButtonImpl("inv0_" + event.getMember().getId(), "retour", ButtonStyle.SUCCESS, false, null)).queue();
        }
    }
}
