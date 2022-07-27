package fr.cringebot.cringe.slashInteraction;

import fr.cringebot.cringe.CommandBuilder.Gift;
import fr.cringebot.cringe.CommandBuilder.Info;
import fr.cringebot.cringe.CommandBuilder.Shop;
import fr.cringebot.cringe.CommandBuilder.Top;
import fr.cringebot.cringe.Polls.PollMain;
import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.waifus.WaifuCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.io.IOException;
import java.util.ArrayList;

public class SlashListener {
    public static void onSlashCommand(SlashCommandInteraction event) throws IOException, InterruptedException {
        if (event.getName().equals("gift"))
        {
            Gift ret = Gift.sendGift(event.getOption("code").getAsString(), event.getMember());
            EmbedBuilder eb = ret.getEmbedBuilder();
            if (ret.getId() == null)
                event.replyEmbeds(eb.build()).queue();
            else {
                ButtonImpl bttn = new ButtonImpl("gift_" + ret.getId(), "ouvrir", ButtonStyle.SUCCESS, false, null);
                event.replyEmbeds(eb.build()).addActionRow(bttn).queue();
            }
        }
        else if (event.getName().equals("shop"))
        {
            event.replyEmbeds(Shop.ShopDisplay(event.getMember()).build()).addActionRow(Shop.PrincipalMenu()).queue();
        }
        else if (event.getName().equals("harem"))
        {
            Message msg;
            if (event.getOption("nom") == null) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Waifu de " + event.getMember().getEffectiveName());
                msg = getMessage(event, eb);
                WaifuCommand.haremEmbed(msg, 0, event.getMember().getId());
            }
            else
            {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Waifu de " + event.getOption("nom").getAsMember().getEffectiveName());
                msg = getMessage(event, eb);
                WaifuCommand.haremEmbed(msg, 0, event.getOption("nom").getAsMember().getId());
            }
        }
        else if (event.getName().equals("info"))
        {
            if (event.getOption("nom") == null)
                event.replyEmbeds(Info.info(event.getMember()).build()).queue();
            else
                event.replyEmbeds(Info.info(event.getOption("nom").getAsMember()).build()).queue();
        }
        else if (event.getName().equals("top")) {
            if (event.getOption("squadname") == null)
                event.replyEmbeds(Top.top(null, event.getGuild()).build()).queue();
            else
                event.replyEmbeds(Top.top(event.getOption("nom").getAsString(), event.getGuild()).build()).queue();
        } else if (event.getName().equals("squadname"))
            event.replyEmbeds(Shop.ShopDisplay(event.getMember()).build()).addActionRow(Shop.PrincipalMenu()).queue();
        else if (event.getName().equals("poll"))
        {
            ArrayList<String> args = new ArrayList<>();
            String name = event.getOption("nom").getAsString();
            args.add(event.getOption("arg01").getAsString());
            args.add(event.getOption("arg02").getAsString());
            if (event.getOption("arg03") != null)
                args.add(event.getOption("arg03").getAsString());
            if (event.getOption("arg04") != null)
                args.add(event.getOption("arg04").getAsString());
            PollMain.PollMain(args.toArray(new String[0]), name, event.getTextChannel(), event.getMember());
        }
        else
            event.reply("patience ça arrive").setEphemeral(true).queue();
    }

    private static Message getMessage(SlashCommandInteraction event, EmbedBuilder eb) {
        eb.setDescription("chargement...");
        ArrayList<ButtonImpl> bttn = new ArrayList<>();
        bttn.add(new ButtonImpl("harem_"+event.getMember()+";"+"-1", "page précédente", ButtonStyle.PRIMARY ,true, null));
        bttn.add(new ButtonImpl("harem_"+event.getMember()+";"+"1", "page suivante",ButtonStyle.SECONDARY ,false, null));
        InteractionHook ih = event.replyEmbeds(eb.build()).addActionRow(bttn).complete();
        return (Message) ih;
    }

    public static void autoComplete(CommandAutoCompleteInteraction event) {
        if (event.getOptions().get(0).getName().equals("squadname")) {
            ArrayList<String> ret = new ArrayList<>();
            for (Squads sq : Squads.getAllSquads())
                ret.add(sq.getName());
            event.replyChoiceStrings(ret).queue();
        }
    }
}
