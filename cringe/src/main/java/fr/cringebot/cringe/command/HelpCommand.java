package fr.cringebot.cringe.command;

import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.builder.Command.ExecutorType;
import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.builder.SimpleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.entities.UserImpl;

import java.awt.*;

public class HelpCommand {

    private final CommandMap commandMap;

    public HelpCommand(CommandMap commandMap) {
        this.commandMap = commandMap;
    }

    @Command(name = "help", type = ExecutorType.USER, description = "affiche la liste des commandes.")
    private void help(User user, MessageChannel channel, Guild guild){

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Liste des commandes.");
        builder.setColor(Color.CYAN);

        for(SimpleCommand command : commandMap.getCommands()){
            if(command.getExecutorType() == ExecutorType.CONSOLE) continue;

            builder.addField(command.getName(), command.getDescription(), false);
        }

        if(!user.hasPrivateChannel()) user.openPrivateChannel().complete();
        ((UserImpl)user).getPrivateChannel().sendMessage(builder.build()).queue();

        channel.sendMessage(user.getAsMention()+", veuillez regarder vos message privé.").queue();

    }

}