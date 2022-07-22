package fr.cringebot.cringe.slashInteraction;

import fr.cringebot.cringe.command.CommandListener;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.io.IOException;

public class SlashListener {
    public static void onSlashCommand(SlashCommandInteraction event) throws IOException, InterruptedException {
        if (event.getName().equals("gift"))
        {
            event.reply(CommandListener.gift(event.getOption("code").getAsString(), event.getMember(), event.getTextChannel())).queue();
        }
        event.reply("patience ça arrive").setEphemeral(true).queue();
    }
}
