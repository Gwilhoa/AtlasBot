package fr.cringebot.cringe.slashInteraction;

import fr.cringebot.cringe.CommandBuilder.Gift;
import fr.cringebot.cringe.CommandBuilder.Info;
import fr.cringebot.cringe.CommandBuilder.Shop;
import fr.cringebot.cringe.CommandBuilder.Top;
import fr.cringebot.cringe.Polls.PollMain;
import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.waifus.InvWaifu;
import fr.cringebot.cringe.waifus.ListWaifu;
import fr.cringebot.cringe.waifus.Waifu;
import fr.cringebot.cringe.waifus.WaifuCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static fr.cringebot.cringe.waifus.WaifuCommand.capturedWaifu;

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
            event.replyEmbeds(Shop.ShopDisplay(event.getMember()).build()).addActionRow(Shop.PrincipalMenu(event.getMember())).queue();
        }
        else if (event.getName().equals("harem"))
        {
            if (event.getOption("nom") == null) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Waifu de " + event.getMember().getEffectiveName());
                getMessage(event, eb, event.getMember().getId());
            }
            else
            {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Waifu de " + event.getOption("nom").getAsMember().getEffectiveName());
                getMessage(event, eb,event.getOption("nom").getAsMember().getId());
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
                event.replyEmbeds(Top.top(event.getOption("squadname").getAsString(), event.getGuild()).build()).queue();
        }
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
        else if (event.getName().equals("waifu")){
            EmbedBuilder eb = capturedWaifu(event.getMember().getId(), event.getGuild());
            if (!Objects.equals(eb.build().getColor(), Color.black) && !Objects.equals(eb.build().getColor(), Color.WHITE))
                event.replyEmbeds(eb.build()).queue();
            else
            {
                if (Squads.getstats(event.getMember()).getCoins() >= Shop.getCEPRICE())
                    event.replyEmbeds(eb.build()).addActionRow(new ButtonImpl("CEFUBUY;"+event.getMember().getId(), "Acheter un Chronomètre érotique", ButtonStyle.SUCCESS,false, null)).queue();
                else
                    event.replyEmbeds(eb.build()).addActionRow(new ButtonImpl("CEFUBUY;"+event.getMember().getId(), "Acheter un Chronomètre érotique", ButtonStyle.SUCCESS,true, null)).queue();
            }}
        else if (event.getName().equals("waifu_list")) {
            String SearchKey;
            if (event.getOption("nom") == null)
                SearchKey = "all";
            else
                SearchKey = event.getOption("nom").getAsString();
            event.replyEmbeds(ListWaifu.listwaifu(event.getGuild(), event.getMember().getId(), SearchKey).build()).addActionRows(WaifuCommand.generateButtonList(event.getMember().getId(), SearchKey, 0)).queue();
        }
        else if (event.getName().equals("waifu_search")) {
            event.replyEmbeds(WaifuCommand.waifuSearching(event.getMember(), event.getTextChannel()).build()).queue();
        }
        else
            event.reply("patience ça arrive").setEphemeral(true).queue();
    }

    private static void getMessage(SlashCommandInteraction event, EmbedBuilder eb, String memId) {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        ArrayList<Waifu> waifus = new ArrayList<>();
        ArrayList<InvWaifu> invWaifus = new ArrayList<>(Squads.getstats(memId).getWaifus().values());
        Waifu w;
        for (InvWaifu inw : invWaifus)
            waifus.add(Waifu.getWaifuById(inw.getId()));
        if (invWaifus.isEmpty())
            event.replyEmbeds(eb.setDescription("tu as pas de waifu").build()).queue();
        else {
            while (i < 10) {
                if (i < waifus.size()) {
                    w = waifus.get(i);
                    sb.append(w.getId()).append(" ").append(w.getName()).append(" de ").append(w.getOrigin()).append("\n    niveau : ").append(invWaifus.get(i).getLevel()).append("\n");
                }
                i++;
            }
            eb.setDescription(sb);
            eb.setColor(Squads.getSquadByMember(memId).getSquadRole(event.getGuild()).getColor());
            ArrayList<ButtonImpl> bttn = new ArrayList<>();
            bttn.add(new ButtonImpl("harem_" + memId + ";" + "-1", "page précédente", ButtonStyle.PRIMARY, true, null));
            bttn.add(new ButtonImpl("harem_" + memId + ";" + "1", "page suivante", ButtonStyle.SECONDARY, false, null));
            event.replyEmbeds(eb.build()).addActionRow(bttn).queue();
        }
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
