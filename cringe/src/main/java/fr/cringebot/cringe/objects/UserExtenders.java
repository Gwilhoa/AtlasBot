package fr.cringebot.cringe.objects;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;


public class UserExtenders {
    public static User.UserFlag getHypesquad(Member mem){
            if (mem.getUser().getFlags().contains(User.UserFlag.HYPESQUAD_BALANCE)){
                return User.UserFlag.HYPESQUAD_BALANCE;
            } else if (mem.getUser().getFlags().contains(User.UserFlag.HYPESQUAD_BRAVERY)){
                return User.UserFlag.HYPESQUAD_BRAVERY;
            } else if (mem.getUser().getFlags().contains(User.UserFlag.HYPESQUAD_BRILLIANCE)) {
                return User.UserFlag.HYPESQUAD_BRILLIANCE;
            }
            return null;
        }
        public static Integer getAllmsg(Member mem){
        Integer i = 0;
            for ( TextChannel ch : mem.getGuild().getTextChannels()){
                System.out.println(ch.getName());
                for (Message msg : ch.getHistory().getRetrievedHistory()) {
                    System.out.println(msg.getAuthor().getName() +"\n\n");
                    if (msg.getMember().getId().equals(mem.getId())){
                        i++;
                    }
                }
            }
            return i;
        }

    }

