package fr.cringebot.cringe.CommandBuilder;

import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.waifus.Waifu;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class Gift {
    private String id;
    private EmbedBuilder embedBuilder;
    public Gift(String id, EmbedBuilder embedBuilder)
    {
        this.id = id;
        this.embedBuilder = embedBuilder;
    }

    public String getId() {
        return id;
    }

    public EmbedBuilder getEmbedBuilder() {
        return embedBuilder;
    }

    public static Gift sendGift(String code, Member mb) throws IOException, InterruptedException {
        File f = new File("save/gift/"+code);
        String retS;
        EmbedBuilder eb = new EmbedBuilder();
        if (f.exists() && f.isFile())
        {
            eb.setColor(Color.green).setTitle("Nouveau cadeau !")
                    .setDescription("tiens " + mb.getAsMention() + " j'ai trouvé ça");
            eb.setImage("https://png.pngtree.com/png-vector/20191122/ourlarge/pngtree-red-gift-box-vector-illustration-with-cute-design-isolated-on-white-png-image_2016770.jpg");
            retS = new BufferedReader(new FileReader(f)).readLine();
            f.delete();
        } else
        {
            eb.setColor(Color.RED).setTitle("Échec").setDescription("désolé j'ai rien trouvé");
            retS = null;
        }
        eb.setFooter(mb.getId());
        return new Gift(retS, eb);
    }
    public static void openGift(ButtonInteractionEvent e) throws InterruptedException {
        String ret = e.getButton().getId().substring("gift_".length());
        Member member = e.getMember();
        switch (ret.split(";")[0]) {
            case "coins":
                Squads.getstats(member).addCoins(Long.parseLong(ret.split(";")[1]));
                e.reply("tu as gagné " + ret.split(";")[1] + " B2C").queue();
            case "waifu":
                e.replyEmbeds(Squads.getstats(member).newWaifu(Integer.parseInt(ret.split(";")[1]), e.getMember().getId(), e.getGuild()).build()).addFile(new File(Waifu.getWaifuById(Integer.parseInt(ret.split(";")[1])).getProfile())).queue();
            case "squad":
                Squads.addPoints(member, Long.parseLong(ret.split(";")[1]));
                e.reply("l'escouade "
                        + Squads.getSquadByMember(member).getName()
                        + " gagne "
                        + Long.parseLong(ret.split(";")[1])
                        + " points").queue();
        }
        e.getMessage().editMessageEmbeds(new EmbedBuilder().setTitle("Cadeau ouvert").setDescription("ce cadeau à déja été ouvert").build()).setActionRow(new ButtonImpl("gift", "ouvrir", ButtonStyle.SUCCESS, true, null)).queue();
    }
}