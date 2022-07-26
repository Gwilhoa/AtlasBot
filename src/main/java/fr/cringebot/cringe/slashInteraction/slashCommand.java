package fr.cringebot.cringe.slashInteraction;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class slashCommand {
    public static void load(JDA jda) throws InterruptedException {
        /**
        jda.updateCommands().queue();

        jda.upsertCommand("play", "jouer de la musique")
                .addOption(OptionType.STRING, "rechercher", "indiquez la musique, url ou recherche", true)
                .queue();

        jda.upsertCommand("volume", "monter ou baisser le nom du bot")
                .addOption(OptionType.INTEGER, "niveau", "niveau du son a définir (entre 0 et 300)")
                .queue();

        jda.upsertCommand("np", "musique à jouer maintenant (les actuelles passe après)")
                .addOption(OptionType.STRING, "rechercher", "indiquez la musique, url ou recherche", true)
                .queue();

        jda.upsertCommand("random", "rendre aléatoire les prochaines musique")
                .addOption(OptionType.STRING, "playlist", "une playlist qui sera totalement aléatoire")
                .queue();

        jda.upsertCommand("skip", "passer à la musique suivante")
                .queue();

        jda.upsertCommand("loop", "looper la musique actuelle")
                .queue();

        jda.upsertCommand("playmerde", "playlist qui tue sa mère")
                .queue();

        jda.upsertCommand("gift", "des cadeaux")
                .addOption(OptionType.STRING, "code", "indique le code", true)
                .queue();

        jda.upsertCommand("cki", "mini jeu, retrouve à qui ça fait référence")
                .addOption(OptionType.STRING, "type", "vous voulez le jeu de quel type", true)
                .queue();

        jda.upsertCommand("shop", "ouvre le shopping")
                .queue();

        jda.upsertCommand("role", "permet de creer un role")
                .addOption(OptionType.STRING, "nom", "le nom du nouveau role", true)
                .addOption(OptionType.STRING, "emote", "l'emote de représentation (Attention emote de base seulement)", true)
                .queue();

        jda.upsertCommand("poll", "faire un poll")
                .addOption(OptionType.STRING, "nom", "le titre du poll", true)
                .addOption(OptionType.STRING, "arg01", "premier argument", true)
                .addOption(OptionType.STRING, "arg02", "second argument", true)
                .addOption(OptionType.STRING, "arg03", "troisième argument")
                .addOption(OptionType.STRING, "arg04", "quatrième argument")
                .queue();

        jda.upsertCommand("top", "savoir le leaderboard d'une ou des escouades")
                .addOption(OptionType.STRING, "squadname", "nom de l'escouade a observer", false, true)
                .queue();

        jda.upsertCommand("waifu", "capturer une waifu (coming soon)")
                .queue();

        jda.upsertCommand("harem", "savoir le harem de quelqu'un")
                .addOption(OptionType.USER, "nom", "le harem que tu veux savoir")
                .queue();

        jda.upsertCommand("info", "tout savoir sur une personne ou vous meme")
                .addOption(OptionType.USER, "nom", "la personne que tu veux savoir")
                .queue();

        jda.upsertCommand("pokemon", "attraper un pokemon")
                .queue();

        jda.upsertCommand("clear", "nettoyer le channel")
                .addOption(OptionType.INTEGER, "nombre", "le nombre de message a delete", true)
                .queue();

        jda.upsertCommand("meteo", "savoir la météo")
                .addOption(OptionType.STRING, "ville", "savoir la météo dans une ville", true)
                .queue();

        jda.upsertCommand("pokedex", "description d'un pokemon")
                .addOption(OptionType.STRING, "nom", "nom du pokémon", true)
                .queue();

        jda.upsertCommand("help", "savoir les commandes")
                .queue();
         **/
        jda.upsertCommand("waifu info", "savoir les commandes")
                .queue();
    }

}
