package fr.cringebot.cringe.command;

import fr.cringebot.cringe.builder.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.champion.dto.ChampionInfo;
import net.rithms.riot.api.endpoints.static_data.constant.ChampionTags;
import net.rithms.riot.api.endpoints.static_data.constant.Locale;
import net.rithms.riot.api.endpoints.static_data.dto.Champion;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import java.util.List;

public class CommandLol {
    @Command(name = "getrotate", type = Command.ExecutorType.USER, description = "récupère les donnée d'un summoner")
    private void getstats(Message msg) throws RiotApiException {
        ApiConfig config = new ApiConfig().setKey("RGAPI-2e977103-c601-4803-9f93-98e058b48168");
        RiotApi api = new RiotApi(config);

    }
}
