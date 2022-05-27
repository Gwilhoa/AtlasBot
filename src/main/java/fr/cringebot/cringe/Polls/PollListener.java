package fr.cringebot.cringe.Polls;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

public class PollListener {
    /**
     * event quand une personne change de vote ou en ajoute un nouveau
     *
     * @param pollMessage  le message du poll
     * @param Voter        la personne concerné
     * @param selectOption le choix qu'il a fait
     */
    public static void reactSelectMenu(Message pollMessage, Member Voter, SelectOption selectOption) {
        PollMessage pm = PollMessage.pollMessage.get(pollMessage.getId());
        pm.newVote(Voter, selectOption.getLabel());
        pollMessage.editMessageEmbeds(pm.getMessageEmbed(pollMessage.getGuild())).queue();
    }

    /**
     * verifie les polls si ils sont deprecated
     *
     * @param jda le bot
     */
    public static void verifTimePoll(JDA jda) {
        long time = System.currentTimeMillis();
        if (PollMessage.pollMessage == null)
            return;
        for (PollMessage pm : PollMessage.pollMessage.values())
            if (pm.getTime() + 86400000 < time)
                pm.unactive(jda);
    }
}
