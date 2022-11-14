package fr.cringebot.cringe.command;

import fr.cringebot.cringe.Request.Squads;
import fr.cringebot.cringe.builder.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.net.ConnectException;

public class CommandAdmin {
    @Command(name = "removepoints", description = "enlever des points", type = Command.ExecutorType.USER)
    private void removepoints(Message msg)
    {
        if (msg.getMember().getPermissions().contains(Permission.ADMINISTRATOR) && !msg.getMentions().getRoles().isEmpty())
        {
            try {
                Squads.addManualPoint(msg.getMentions().getRoles().get(0).getId(), (Integer.parseInt(msg.getContentRaw().split(" ")[1])) * -1);
            } catch (ConnectException e) {
            msg.getChannel().sendMessage("disconnected").queue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            msg.getChannel().sendMessage("tu n'as pas les droits").queue();
        }
    }
}
