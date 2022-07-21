package fr.cringebot.cringe.slashInteraction;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class slashCommand {
    public static void load(JDA jda)
    {
        jda.updateCommands().queue();

        jda.upsertCommand("play", "jouer de la musique")
                .addOption(OptionType.STRING, "rechercher", "indiquez la musique, url ou recherche", true)
                .queue();

        jda.upsertCommand("volume", "monter ou baisser le nom du bot")
                .addOption(OptionType.INTEGER, "niveau", "niveau du son a définir (entre 0 et 300)")
                .queue();

        jda.upsertCommand("np", "musique à jouer MAINTENANT (les actuelles passe après)")
                .addOption(OptionType.STRING, "rechercher", "indiquez la musique, url ou recherche", true)
                .queue();

        jda.upsertCommand("random", "rendre aléatoire les prochaines musique")
                .addOption(OptionType.STRING, "PLAYLIST", "une playlist qui sera totalement aléatoire")
                .queue();

        jda.upsertCommand("skip", "passer à la musique suivante")
                .queue();

        jda.upsertCommand("loop", "looper la musique actuelle")
                .queue();

        jda.upsertCommand("playmerde", "playlist qui tue sa mère")
                .queue();

        jda.upsertCommand("gift", "des cadeaux")
                .addOption(OptionType.STRING, "CODE", "indique le code",true)
                .queue();

        jda.upsertCommand("cki", "mini jeu, retrouve à qui ça fait référence")
                .addOption(OptionType.STRING,"de quel type ?","vous voulez le jeu de quel type")
                .queue();

        jda.upsertCommand("shop", "ouvre le shopping")
                .queue();

        jda.upsertCommand("role", "permet de creer un role")
                .addOption(OptionType.STRING, "NOM", "le nom du nouveau role", true)
                .addOption(OptionType.STRING, "EMOTE", "l'emote de représentation (Attention emote de base seulement)", true)
                .queue();

        jda.upsertCommand("poll", "faire un poll")
                .addOption(OptionType.STRING, "NOM/TITRE", "le titre du poll", true)
                .addOption(OptionType.STRING, "ARG01", "premier argument", true)
                .addOption(OptionType.STRING, "ARG02", "second argument", true)
                .addOption(OptionType.STRING, "ARG03", "troisième argument")
                .addOption(OptionType.STRING, "ARG04", "quatrième argument")
                .queue();

        jda.upsertCommand("top", "savoir le leaderboard d'une ou des escouades")
                .addOption(OptionType.STRING, "NOM", "nom de l'escouade a observer", false)
                .queue();

        jda.upsertCommand("waifu", "capturer une waifu (coming soon)")
                .queue();

        jda.upsertCommand("harem", "savoir le harem de quelqu'un")
                .addOption(OptionType.USER, "NOM", "le harem que tu veux savoir")
                .queue();

        jda.upsertCommand("info", "tout savoir sur une personne ou vous meme")
                .addOption(OptionType.USER, "NOM", "la personne que tu veux savoir")
                .queue();

        jda.upsertCommand("pokemon", "attraper un pokemon")
                .queue();

        jda.upsertCommand("clear", "nettoyer le channel")
                .addOption(OptionType.INTEGER, "NBR", "le nombre de message a delete", true)
                .queue();

        jda.upsertCommand("meteo", "savoir la météo")
                .addOption(OptionType.STRING, "ville", "savoir la météo dans une ville", true)
                .queue();

        jda.upsertCommand("pokedex", "description d'un pokemon")
                .addOption(OptionType.STRING, "NOM", "nom du pokémon", true)
                .queue();

        jda.upsertCommand("help", "savoir les commandes")
                .queue();
    }
}
