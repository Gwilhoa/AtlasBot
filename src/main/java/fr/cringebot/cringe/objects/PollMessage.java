package fr.cringebot.cringe.objects;


import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.List;

import static fr.cringebot.cringe.event.BotListener.gson;

public class PollMessage {
    private static final String file = "save/pollmsg.json";
    private static final TypeToken<HashMap<String, PollMessage>> type = new TypeToken<HashMap<String, PollMessage>>() {
    };
    public static HashMap<String, PollMessage> pollMessage = null;
    private final String title;
    private final String guild;
    private final String tchannel;
    private final String author;
    private final HashMap<String, String> MemberVote;
    private final String messageId;
    private final long time;
    private final HashMap<String, Integer> args;
    private boolean active;

    public PollMessage(String messageId, List<String> arguments, String author, Guild g, String tc, String title) {
        this.title = title;
        this.guild = g.getId();
        this.author = author;
        this.messageId = messageId;
        this.MemberVote = new HashMap<>();
        this.args = new HashMap<>();
        this.time = System.currentTimeMillis();
        this.tchannel = tc;
        for (String arg : arguments)
            args.put(arg, 0);
        if (pollMessage == null)
            load();
        this.newPoll();
        this.active = true;
    }

    /**
     * sert a charger la variable des polls selon le fichier
     */
    public static void load() {
        if (new File(file).exists()) {
            try {
                pollMessage = gson.fromJson(new BufferedReader(new InputStreamReader(new FileInputStream(file))), type.getType());
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
        if (pollMessage == null)
            pollMessage = new HashMap<>();
    }

    /**
     * quand un vote est ajouté
     * @param user
     * @param arg
     * @return
     */
    public boolean newVote(User user, String arg) {
        PollMessage pm = pollMessage.get(this.messageId);
        if ( pm.MemberVote.get(user.getId()) == null || pm.MemberVote.get(user.getId()).isEmpty())
        {
            pm.MemberVote.put(user.getId(), arg);
            pm.getArgs().put(arg, pm.getArgs().get(arg) + 1);
        }
        else
        {
            String temp = pm.MemberVote.get(user.getId());
            pm.MemberVote.put(user.getId(), arg);
            pm.getArgs().put(arg, pm.getArgs().get(arg) + 1);
            pm.getArgs().put(temp, pm.getArgs().get(temp) - 1);
        }
        this.save();
        return true;
    }

    /**
     * renvoie la version embed d'un poll
     * @param g
     * @return
     */
    public MessageEmbed getMessageEmbed(Guild g) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(this.title);
        if (active)
            eb.setAuthor("poll").setColor(Color.green);
        else
            eb.setColor(Color.red);
        StringBuilder sb = new StringBuilder();
        System.out.println(this.MemberVote.size());
        for (String key : this.getArgs().keySet()) {
            if (this.getArgs().get(key) == 0)
                sb.append(key).append(" -> ").append(0).append("\n");
            else
            {
                sb.append(key).append(" -> ").append((int)((this.getArgs().get(key).floatValue() / this.MemberVote.size()) * 100)).append('%').append("\n").append("||");
                int i = 0;
                while (i*5 < (int)((this.getArgs().get(key).floatValue() / this.MemberVote.size()) * 100))
                {
                    sb.append(" . ");
                    i++;
                }
                sb.append("||\n");
            }
        }
        eb.setDescription(sb);
        eb.setFooter("un sondage posé par " + g.getMemberById(this.author).getUser().getName(), g.getMemberById(this.author).getUser().getEffectiveAvatarUrl());
        return eb.build();
    }

    public String getMessageId() {
        return messageId;
    }

    public long getTime() {
        return time;
    }

    private void newPoll() {
        if (pollMessage == null)
            return;
        pollMessage.put(this.getMessageId(), this);
        this.save();
    }

    /**
     * sert a modifier le fichier selon la variable des polls
     */
    private void save() {
        if (!new File(file).exists()) {
            try {
                new File(file).createNewFile();
            } catch (IOException e) {
                return;
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            gson.toJson(pollMessage, type.getType(), bw);
            bw.flush();
            bw.close();
        } catch (IOException e) {
        }
    }

    public HashMap<String, Integer> getArgs() {
        return args;
    }

    public void unactive(JDA jda) {
        pollMessage.remove(this.messageId);
        this.active = false;
        jda.getGuildById(this.guild).getTextChannelById(this.tchannel).retrieveMessageById(this.messageId).complete().delete().complete();
        jda.getGuildById(this.guild).getTextChannelById(this.tchannel).sendMessage("fin d'un poll, voici les résultats").setEmbeds(this.getMessageEmbed(jda.getGuildById(this.guild))).queue();
        this.save();
    }
}
