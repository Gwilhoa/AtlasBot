package fr.cringebot.cringe.slashInteraction;

import fr.cringebot.cringe.CommandBuilder.Gift;
import fr.cringebot.cringe.command.CommandListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.io.IOException;
import java.util.Hashtable;

public class SlashListener {
    public static void onSlashCommand(SlashCommandInteraction event) throws IOException, InterruptedException {
        if (event.getName().equals("gift"))
        {
            Hashtable<EmbedBuilder, String> ret = Gift.sendGift(event.getOption("code").getAsString(), event.getMember());
            EmbedBuilder eb = ret.keys().nextElement();
            ButtonImpl bttn = new ButtonImpl("gift_"+ret.get(eb), "ouvrir", ButtonStyle.SUCCESS, false, null);
            event.replyEmbeds(eb.build()).addActionRow(bttn).queue();
        }
        event.reply("patience ça arrive").setEphemeral(true).queue();
    }
}
