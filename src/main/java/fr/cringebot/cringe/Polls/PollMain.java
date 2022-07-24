package fr.cringebot.cringe.Polls;

import fr.cringebot.cringe.objects.SelectOptionImpl;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PollMain {
    public static boolean PollMain(String[] args, String name, TextChannel tc, Member author) {
        ArrayList<SelectOption> options = new ArrayList<>();
        for (String arg : args) {
            for (SelectOption op : options) {
                if (op.getLabel().equals(arg)) {
                    return false;
                }
            }
            options.add(new SelectOptionImpl(arg, arg));
        }
        SelectMenuImpl selectionMenu = new SelectMenuImpl(name, "selectionnez un choix", 1, 1, false, options);
        Message msg = tc.sendMessageEmbeds(new EmbedBuilder().setDescription("chargement").build()).setActionRow(selectionMenu).complete();
        PollMessage pm = new PollMessage(msg.getId(), List.of(args), author.getId(), msg.getGuild(), msg.getTextChannel().getId(), name);
        msg.editMessageEmbeds(pm.getMessageEmbed(msg.getGuild())).queue();
        return true;
    }
}
