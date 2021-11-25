package fr.cringebot;


import fr.cringebot.cringe.builder.CommandMap;
import fr.cringebot.cringe.event.BotListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

/**
 * Hello world!
 *
 */
import java.util.Scanner;

import javax.security.auth.login.LoginException;



public class BotDiscord implements Runnable{

    private final JDA jda;
    private final CommandMap commandMap = new CommandMap(this);
    private final Scanner scanner = new Scanner(System.in);

    private boolean running;

    public BotDiscord(String token) throws LoginException, IllegalArgumentException, RateLimitedException {
        jda = JDABuilder.create(token, GatewayIntent.getIntents(GatewayIntent.DEFAULT | GatewayIntent.getRaw(GatewayIntent.GUILD_MEMBERS) | GatewayIntent.getRaw(GatewayIntent.GUILD_PRESENCES)))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .disableCache(CacheFlag.ACTIVITY)
                .setLargeThreshold(250)
                .addEventListeners(
                        new BotListener(commandMap, this)
                ).build();
        System.out.println("Bot connected.");
    }

    public JDA getJda() {
        return jda;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            if(scanner.hasNextLine()) commandMap.commandConsole(scanner.nextLine());
        }

        scanner.close();
        System.out.println("Bot stopped.");
        jda.shutdown();
        System.exit(0);
    }

    public static void main(String[] args) {
        try {
            BotDiscord botDiscord = new BotDiscord(args[0]);
            new Thread(botDiscord, "bot").start();
        } catch (LoginException | IllegalArgumentException | RateLimitedException e) {
            e.printStackTrace();
        }
    }
}