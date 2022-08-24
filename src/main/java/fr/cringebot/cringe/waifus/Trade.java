package fr.cringebot.cringe.waifus;

import fr.cringebot.cringe.escouades.Squads;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class Trade {
    public static void tradewaifu(Message msg) {
        EmbedBuilder eb = tradewaifu(msg.getMember(), Integer.parseInt(msg.getContentRaw().split(" ")[2]), Integer.parseInt(msg.getContentRaw().split(" ")[3]), msg.getMentions().getMembers().get(0));
        if (Objects.equals(eb.build().getColor(), Color.WHITE)) {
            ArrayList<ButtonImpl> bttn = new ArrayList<>();
            bttn.add(new ButtonImpl("trade_ok;" + Integer.parseInt(msg.getContentRaw().split(" ")[2]) + ";" + Integer.parseInt(msg.getContentRaw().split(" ")[3]) + ";" + msg.getMember().getId() + ";" + msg.getMentions().getMembers().get(0).getId(), "accepter", ButtonStyle.SUCCESS, false, null));
            bttn.add(new ButtonImpl("trade_no;" + msg.getMentions().getMembers().get(0).getId(), "refuser", ButtonStyle.DANGER, false, null));
            msg.getChannel().sendMessage(msg.getMember().getAsMention() + " veux faire un échange avec " + msg.getMentions().getMembers().get(0).getAsMention()).setEmbeds(eb.build()).setActionRow(bttn).queue();
        } else {
            msg.getChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }

    public static EmbedBuilder tradewaifu(Member sender, Integer IdWaifu01, Integer IdWaifu02, Member received) {
        InvWaifu ivWaifu01 = Squads.getstats(sender).getWaifus().get(IdWaifu01);
        InvWaifu ivWaifu02 = Squads.getstats(received).getWaifus().get(IdWaifu02);
        EmbedBuilder eb = new EmbedBuilder();
        if (ivWaifu01 != null && ivWaifu02 != null && Squads.getstats(sender).getWaifus().get(IdWaifu02) == null && Squads.getstats(received).getWaifus().get(IdWaifu01) == null)
        {
            eb.setTitle("Requête d'échange").setDescription(ivWaifu01.getWaifu().getName() + " provenant de " + ivWaifu01.getWaifu().getOrigin() + " de niveau " + ivWaifu01.getLevel()
                    + "\ncontre\n" + ivWaifu02.getWaifu().getName()+ " provenant de " + ivWaifu02.getWaifu().getOrigin()  + " de niveau " + ivWaifu02.getLevel());
            eb.setColor(Color.WHITE);
        }
        else
        {
            eb.setTitle("Échec");
            if (ivWaifu01 == null) {
                eb.setDescription("tu n'as pas " + Waifu.getWaifuById(IdWaifu01));
            } else if (ivWaifu02 == null) {
                eb.setDescription(received.getAsMention() + " n'a pas " + Waifu.getWaifuById(IdWaifu02).getName());
            } else if (Squads.getstats(sender).getWaifus().get(IdWaifu02) != null) {
                eb.setDescription("tu as déja "+ Waifu.getWaifuById(IdWaifu02).getName());
            } else if (Squads.getstats(received).getWaifus().get(IdWaifu01) != null) {
                eb.setDescription(received.getAsMention() + " a déjà " + Waifu.getWaifuById(IdWaifu01).getName());
            }
            eb.setColor(Color.red);
        }
        return eb;
    }
}
