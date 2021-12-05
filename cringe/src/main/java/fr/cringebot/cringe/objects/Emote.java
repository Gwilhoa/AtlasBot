/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   Emote.java                                         :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 13:06:31 by gchatain          #+#    #+#             */
/*   Updated: 2021/12/05 13:07:02 by gchatain         ###   ########lyon.fr   */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.objects;

import net.dv8tion.jda.api.entities.MessageReaction;

/**
 * je vous déteste
 */
public class Emote {
    public static String rirederoite = "803733271628349460";
    public static String porte = "777551253990801409";
    public static String anto = "701002092193775676";
    
    /**
     * sert a différencier les emote de base et les emotes modif
     * @param em
     * @return
     */
    public static String getEmote(MessageReaction.ReactionEmote em)
    {
        if (em.getAsReactionCode().length() > 7)
            return em.getAsReactionCode().substring(0, StringExtenders.firstsearch(em.getAsReactionCode(), ":"));
        return em.getAsCodepoints();
    }

}
