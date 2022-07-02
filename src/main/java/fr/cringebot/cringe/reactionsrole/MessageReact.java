package fr.cringebot.cringe.reactionsrole;

import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import static fr.cringebot.cringe.event.BotListener.gson;

public class MessageReact {
    private static final String channel = "853210283480055809";

    private static final String file = "save/MessageReaction.json";
    private static final TypeToken<ArrayList<MessageReact>> typeToken = new TypeToken<ArrayList<MessageReact>>() {
    };
    private Color color;
    public static ArrayList<MessageReact> message;
    private final String Title;
    private final String Id;
    public ArrayList<RoleReaction> list;


    public MessageReact(String title, Guild guild, Color color) {
        load();
        this.color = color;
        list = new ArrayList<>();
        Title = title;
        Id = guild.getTextChannelById(channel).sendMessageEmbeds(new EmbedBuilder().setColor(color).setTitle(title).build()).complete().getId();
        message.add(this);
        save();
    }

    public static void load() {
        if (new File(file).exists()) {
            try {
                message = gson.fromJson(new BufferedReader(new InputStreamReader(new FileInputStream(file))), typeToken.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new File(file).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (message == null)
            message = new ArrayList<>();
    }

    public static void save() {
        if (!new File(file).exists()) {
            try {
                new File(file).createNewFile();
            } catch (IOException e) {
                return;
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            gson.toJson(message, typeToken.getType(), bw);
            bw.flush();
            bw.close();
        } catch (IOException e) {
        }
    }

    public String getTitle() {
        return Title;
    }

    public String getId() {
        return Id;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void addRole(RoleReaction rr, Guild guild) {
        this.list.add(rr);
        save();
        refresh(guild);
    }

    public void refresh(Guild guild) {
        Message msg = guild.getTextChannelById(channel).retrieveMessageById(this.Id).complete();
        StringBuilder sb = new StringBuilder();
        for (RoleReaction rr : this.list) {
            guild.getRoleById(rr.getId()).getManager().setColor(this.color).queue();
            sb.append(rr.getName()).append(" ").append(rr.getEmote()).append('\n');
            msg.addReaction(Emoji.fromFormatted(rr.getName())).queue();
        }
        EmbedBuilder eb = new EmbedBuilder().setTitle(this.Title);
        if (this.color != null)
            eb.setColor(color);
        msg.editMessageEmbeds(eb.setDescription(sb.toString()).build()).queue();
    }
}

