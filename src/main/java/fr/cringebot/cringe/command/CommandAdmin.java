package fr.cringebot.cringe.command;

import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.escouades.Squads;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

public class CommandAdmin {
    @Command(name = "removepoints", description = "enlever des points", type = Command.ExecutorType.USER)
    private void removepoints(Message msg)
    {
        if (msg.getMember().getPermissions().contains(Permission.ADMINISTRATOR) && !msg.getMentions().getMembers().isEmpty())
        {
            Long points;
            String str = msg.getContentRaw().substring(">removepoints ".length());
            str = str.split(" ")[1];
            try {
                points = Long.parseLong(str);
            } catch (NumberFormatException e) {
                msg.getChannel().sendMessage("les points donné ne sont pas des nombres").queue();
                return;
            }
            msg.getChannel().sendMessage("moins "+ points + " points pour " + Squads.getSquadByMember(msg.getMentions().getMembers().get(0)).getName()).queue();
            Squads.removePoints(msg.getMentions().getMembers().get(0).getId(), points);
        } else {
            msg.getChannel().sendMessage("tu n'as pas les droits").queue();
        }
    }
}
