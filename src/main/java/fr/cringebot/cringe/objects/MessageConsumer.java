/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   MessageConsumer.java                               :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 13:11:59 by gchatain          #+#    #+#             */
/*   Updated: 2022/03/23 16:15:23 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.objects;

import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * sert a simplifier la modification des messages en static
 */
public class MessageConsumer implements Consumer<Message> {
    public String edit;
    public int time;
    public Consumer<Message> next;
    public MessageConsumer(String edit, int time, @Nullable Consumer<Message> next){
        this.edit = edit;
        this.time = time;
        this.next = next;
    }

    @Override
    public void accept(Message m) {
        m.editMessage(edit).delay(time, TimeUnit.MILLISECONDS).queue(next);
    }
}