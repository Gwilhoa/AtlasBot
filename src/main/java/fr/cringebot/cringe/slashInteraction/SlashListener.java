package fr.cringebot.cringe.slashInteraction;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class SlashListener {
    public static void onSlashCommand(SlashCommandInteraction event) {
        event.reply("patience ça arrive").setEphemeral(true).queue();
    }
}
