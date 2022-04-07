package fr.cringebot.cringe.Polls;

import fr.cringebot.cringe.objects.SelectOptionImpl;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.util.ArrayList;
import java.util.Arrays;

public class PollMain {
    /**
     * creation d'un poll
     *
     * @param msg message d'argument
     */
    public static void PollMain(Message msg) {
        String message = msg.getContentRaw().substring(">poll".length());
        String[] args = message.split("\n");
        String name = args[0];
        args = message.substring(name.length() + 1).split("\n");
        String author = msg.getAuthor().getId();
        if (args.length < 2 || name.equals(" ") || name.isEmpty()) {
            msg.getChannel().sendMessage("usage : >poll <titre>\nargument\nargument...").queue();
            return;
        }
        msg.delete().queue();
        ArrayList<SelectOption> options = new ArrayList<>();
        for (String arg : args) {
            for (SelectOption op : options) {
                if (op.getLabel().equals(arg)) {
                    msg.getChannel().sendMessage("pourquoi des arguments similaire, c'est cringe").reference(msg).queue();
                    return;
                }
            }
            options.add(new SelectOptionImpl(arg, arg));
        }
        SelectMenuImpl selectionMenu = new SelectMenuImpl(name, "selectionnez un choix", 1, 1, false, options);
        msg = msg.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription("chargement").build()).setActionRow(selectionMenu).complete();
        PollMessage pm = new PollMessage(msg.getId(), Arrays.asList(args), author, msg.getGuild(), msg.getTextChannel().getId(), name);
        msg.editMessageEmbeds(pm.getMessageEmbed(msg.getGuild())).queue();
    }
}
