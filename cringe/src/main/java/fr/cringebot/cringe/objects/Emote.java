package fr.cringebot.cringe.objects;

import net.dv8tion.jda.api.entities.MessageReaction;

public class Emote {
    public static String rirederoite = "803733271628349460";
    public static String porte = "777551253990801409";
    public static String anto = "701002092193775676";
    public static String getEmote(MessageReaction.ReactionEmote em)
    {
        if (em.getAsReactionCode().length() > 7)
            return em.getAsReactionCode().substring(0, StringExtenders.firstsearch(em.getAsReactionCode(), ":"));
        return em.getAsCodepoints();
    }

}
