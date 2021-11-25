package fr.cringebot.cringe.event;

import com.github.jreddit.entity.Subreddit;
import com.github.jreddit.entity.User;
import com.github.jreddit.retrieval.Subreddits;
import com.github.jreddit.retrieval.params.SubredditsView;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;
import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.builder.Command;
import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.objects.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;



import static fr.cringebot.cringe.event.ReactionEvent.*;
import static fr.cringebot.cringe.objects.StringExtenders.*;


public class BotListener implements EventListener {

    private final CommandMap commandMap;
    private final BotDiscord bot;


    public BotListener(CommandMap cmd, BotDiscord bot) {
        this.commandMap = cmd;
        this.bot = bot;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event){
        System.out.println(event.getClass().getSimpleName());
        if (event instanceof ReadyEvent) onEnable((ReadyEvent)event);
        if (event instanceof MessageReceivedEvent) {
            try {
                onMessage((MessageReceivedEvent)event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(event instanceof GuildMemberJoinEvent) onGuildMemberJoin((GuildMemberJoinEvent) event);
        else if(event instanceof GuildMemberRemoveEvent) onGuildMemberLeave((GuildMemberRemoveEvent) event);
        else if(event instanceof MessageReactionAddEvent) onAddReact((MessageReactionAddEvent) event);
    }

    private void onEnable(ReadyEvent event){

        Activity act = new activity(", si tu lis ça tu es cringe", null ,Activity.ActivityType.LISTENING);
        bot.getJda().getPresence().setActivity(act);
    }

    private void onMessage(MessageReceivedEvent event) throws IOException {
        if(event.getAuthor().equals(event.getJDA().getSelfUser())) return;

        Message msg = event.getMessage();

        if(msg.getContentRaw().startsWith(CommandMap.getTag())){
            commandMap.commandUser(msg.getContentRaw().replaceFirst(CommandMap.getTag(), ""), event.getMessage());
            return;
        }



        String[] args = msg.getContentRaw().split(" ");


        if (args[0].equalsIgnoreCase("f")) {
            pressf(msg);
        }

        if (msg.getContentRaw().equalsIgnoreCase("ping"))
        {
            long time = System.currentTimeMillis();
            msg.getChannel().sendMessage("pong").complete().editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
        }

        if (startWithIgnoreCase(msg.getContentRaw(),"AAA") && containsIgnoreCase(msg.getContentRaw(), "AHH")){
            rage(msg);
        }

        if (containsIgnoreCase(msg.getContentRaw(), "nice")) {
            if (!UtilFunction.isAnyLink(msg)) {
                try {
                    randomcity(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String[] f = msg.getContentRaw().replace("?","").replace(".","").split(" ");
        if (containsIgnoreCase(f[f.length - 1], "quoi" )){
            feur(msg);
        }

        if (containsIgnoreCase(msg.getContentRaw(), "putain")){
            putain(msg);
        }

        if (containsIgnoreCase(msg.getContentRaw(), "hmm")){
            for (String split : msg.getContentRaw().split(" "))
                if (StringExtenders.startWithIgnoreCase(split,"hmm"))
                    hmm(msg, split);
        }

        if (containsIgnoreCase(msg.getContentRaw(),"cringe")){
            if (!UtilFunction.isAnyLink(msg))
                msg.getTextChannel().sendMessage("https://tenor.com/view/oh-no-cringe-cringe-oh-no-kimo-kimmo-gif-23168319").queue();
        }

        if (msg.getChannel().getId().equals("461606547064356864") && UtilFunction.isAnyLink(msg))
        {
            if (UtilFunction.isImage(msg.getContentRaw()))
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedImage im = imgExtenders.getImage(new URL(msg.getContentRaw()));
                ImageIO.write(im, "png", baos);
                Message temp = msg.getTextChannel().sendFile(baos.toByteArray(), msg.getContentRaw().split("/")[msg.getContentRaw().split("/").length - 1]).complete();
                msg.delete().queue();
                msg = temp;

            }
            msg.addReaction(msg.getGuild().getEmoteById(Emote.rirederoite)).queue();
            msg.addReaction(msg.getGuild().getEmoteById(Emote.anto)).queue();
            msg.addReaction(msg.getGuild().getEmoteById(Emote.porte)).queue();
        }

    }

    private void onGuildMemberJoin(GuildMemberJoinEvent event){
        event.getGuild().getTextChannelById("687244482739044370").sendMessage(event.getUser().getAsMention()+" a rejoint la Guild.").queue();
    }

    private void onGuildMemberLeave(GuildMemberRemoveEvent event){
        event.getGuild().getTextChannelById("687244482739044370").sendMessage(event.getUser().getAsMention()+" a quitté la Guild.").queue();
    }

    private void onAddReact(MessageReactionAddEvent event){
        if (event.getChannel().getId().equals("461606547064356864"))
        {
            memesEvent.addReaction(event.getChannel().retrieveMessageById(event.getMessageId()).complete(),event.getReaction());
        }
    }
}
