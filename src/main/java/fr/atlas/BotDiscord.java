package fr.atlas;


import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Region;
import fr.atlas.builder.CommandMap;
import fr.atlas.event.BotListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.Scanner;

public class BotDiscord implements Runnable{

    public final static String SecondaryRoleId = "502575765179858945";
    public final static String FarmingSalonId = "981183442509238302";
    public final static String AFKSalonId = "979859652848283748";
    public final static String AnnounceSalonId = "947564791759777792";
    public static final Boolean isMaintenance = false;
    private final JDA jda;
    private final CommandMap commandMap = new CommandMap(this);
    private final Scanner scanner = new Scanner(System.in);
    public static String token;

    public static final String activity = " avec le monde";

    private boolean running;

    public BotDiscord(String token) throws LoginException, IllegalArgumentException, RateLimitedException {
        Orianna.setRiotAPIKey("RGAPI-2e977103-c601-4803-9f93-98e058b48168");
        Orianna.setDefaultRegion(Region.EUROPE_WEST);
        Orianna.setDefaultLocale("fr_FR");
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

    public static void main(String args[]) {
        token = args[1];
        try {
            BotDiscord botDiscord = new BotDiscord(args[0]);
            new Thread(botDiscord, "bot").start();
        } catch (LoginException | IllegalArgumentException | RateLimitedException e) {
            e.printStackTrace();
        }
    }
}