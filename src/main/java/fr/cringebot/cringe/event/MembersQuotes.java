package fr.cringebot.cringe.event;

import fr.cringebot.cringe.objects.MessageReact;
import fr.cringebot.cringe.objects.imgExtenders;
import net.dv8tion.jda.api.entities.Message;

import java.io.IOException;
import java.util.Random;

public class MembersQuotes {
    public static boolean MemberReact(Message msg) throws IOException {
        if (msg.getContentRaw().equalsIgnoreCase("max"))
            Max(msg);
        else if (msg.getContentRaw().equalsIgnoreCase("roro"))
            Roro(msg);
        else if (msg.getContentRaw().equalsIgnoreCase("logan"))
            Logan(msg);
        else if (msg.getContentRaw().equalsIgnoreCase("guigui"))
            Guigui(msg);
        else if (msg.getContentRaw().equalsIgnoreCase("enki"))
            Enki(msg);
        else if (msg.getContentRaw().equalsIgnoreCase("oscar"))
            Oscar(msg);
        else if (msg.getContentRaw().equalsIgnoreCase("Antonin"))
            Anto(msg);
        else if (msg.getContentRaw().equalsIgnoreCase("jonathan")) {
            Jojo(msg);
        }
        else return false;
        return true;
    }

    static void Jojo(Message msg) throws IOException {
        int r = new Random().nextInt(8);
        if (r == 0)
            msg.getChannel().sendMessage("Obsédé de saint pierre d'allevard").queue();
        if (r == 1)
            msg.getChannel().sendMessage("Qu'est ce qui est jaune et qui attends ?").queue();
        if (r == 2)
            msg.getChannel().sendMessage("De ce qu'on m'a dit il a un humour fin et raffinés et aussi un peu maigre et musculeux\n\nhmmmmm je crois que j'ai menti").queue();
        if (r == 3)
            msg.getChannel().sendFile(imgExtenders.getFile("jojo.png")).queue();
        if (r == 4)
            msg.getChannel().sendMessage("Un petit fortnite ?").queue();
        if (r == 5)
            msg.getChannel().sendMessage("https://tenor.com/view/chika-chika-dance-anime-anime-dance-dance-gif-13973731").queue();
        if (r == 6)
            msg.getChannel().sendMessage("Top 1 du B2K sur doodle jump").queue();
        if (r == 7)
            msg.getChannel().sendMessage("créateur de la playlist qui tue sa mère").queue();
    }

    static void Anto(Message msg) {
        int r = new Random().nextInt(8);
        if (r == 0)
            msg.getChannel().sendMessage("Male alpha").queue();
        if (r == 1)
            msg.getChannel().sendMessage("**ahou ahou**").queue();
        if (r == 2)
            msg.getChannel().sendMessage("de ce qu'on m'a dit c'est qu'il est toujours plus fort que les autres car il est a fond\n*Un jour il se fera dépasser mais pas par enki*").queue();
        if (r == 3)
            msg.getChannel().sendMessage("Un homme musculeux !").queue();
        if (r == 4)
            msg.getChannel().sendMessage("Son deuxième prénom est discrétion !").queue();
        if (r == 5)
            msg.getChannel().sendMessage("Le PussySlayer par excellence !").queue();
        if (r == 6)
            msg.getChannel().sendMessage("***Errape.mp3***").queue();
        if (r == 7)
            msg.getChannel().sendMessage("https://www.youtube.com/watch?v=DeumyOzKqgI").queue();
    }

    static void Oscar(Message msg) {
        int r = new Random().nextInt(5);
        if (r == 0)
            msg.getChannel().sendMessage("TRACTEUR VROUM VROUM !!!!!").queue();
        if (r == 1)
            msg.getChannel().sendMessage("vitesse moyenne en montagne : 250km/h").queue();
        if (r == 2)
            msg.getChannel().sendMessage("aparemment le porc ça le connait").queue();
        if (r == 3)
            msg.getChannel().sendMessage("hmmm, de ce qu'on m'a dis il aime bien boire, et il vole des panneaux").queue();
        if (r == 4)
            msg.getChannel().sendMessage("aparemment il sait qui gagne entre un tracteur et un mur").queue();
    }

    static void Enki(Message msg) {
        int r = new Random().nextInt(6);
        if (r == 0)
            msg.getChannel().sendMessage("https://www.alcool-info-service.fr").queue();
        if (r == 1)
            msg.getChannel().sendMessage("https://www.pole-emploi.fr").queue();
        if (r == 2)
            msg.getChannel().sendMessage("https://www.caf.fr/").queue();
        if (r == 3)
            msg.getChannel().sendMessage("Enki, c'est mon hébergeur, il a chopé 2147483647 fois le covid\nil est parfois un peu con dans ses décisions").queue();
        if (r == 4)
            msg.getChannel().sendMessage("il a pas une carte fidelité burger king ?").queue();
        if (r == 5)
            msg.getChannel().sendMessage("je pense qu'il a déjà fait 4 fois le tour du monde en voiture").queue();
    }

    static void Guigui(Message msg) {
        int r = new Random().nextInt(8);
        if (r == 0)
            msg.getChannel().sendMessage("C'est quoi une vache ?").queue();
        if (r == 1)
            msg.getChannel().sendMessage("Roro :heart:").queue();
        if (r == 2)
            msg.getChannel().sendMessage("Créateur de la méta Diablo backlane").queue();
        if (r == 3)
            msg.getChannel().sendMessage("Mon magnifique créateur, il est noir et PD,\nil a vraiment rien pour lui").queue();
        if (r == 4)
            msg.getChannel().sendMessage("ChairSlayer").queue();
        if (r == 5)
            msg.getChannel().sendMessage("ah ouuuuaiis \nça se passe comme ça dans le Nexus").queue();
        if (r == 6)
            msg.getChannel().sendMessage("mamaaaaaaaaaaa").queue();
        if (r == 7)
            msg.getChannel().sendMessage("C'est rien, c'est la rue").queue();
    }

    static void Roro(Message msg) {
        int r = new Random().nextInt(5);
        if (r == 0)
            msg.getChannel().sendMessage("Shadow in the night").queue();
        if (r == 1)
            msg.getChannel().sendMessage("Un jour il dira a Momo d'arreter de pomper la co\nUn jour...").queue();
        if (r == 2)
            msg.getChannel().sendMessage("Super koala de destruction massif").queue();
        if (r == 3)
            msg.getChannel().sendMessage("D'après ce qu'on m'a dit cette peronne est sacrée\ndonc j'en déduis qu'il est parlementaire").queue();
        if (r == 4)
            msg.getChannel().sendMessage("Salut c'est toi, et moi\n moi c'est CringeBot et toi, c'est " + msg.getMember().getAsMention()).queue();
    }

    static void Max(Message msg) {
        int r = new Random().nextInt(5);
        if (r == 0)
            msg.getChannel().sendMessage("La personne la plus calme et la plus sereine que je connaisse \n\n*ou pas*").queue();
        if (r == 1)
            msg.getChannel().sendMessage("Le chevalier ou le roi du sel en personne\nRien que ça").queue();
        if (r == 2)
            msg.getChannel().sendMessage("Composé à 95% de sel").queue();
        if (r == 3)
            msg.getChannel().sendMessage("Un jour il sera calme, un jour...").queue();
        if (r == 4)
            msg.getChannel().sendMessage("ComputerSlayer").queue();
    }

    static void Logan(Message msg){
        int r = new Random().nextInt(4);
        if (r == 0)
            msg.getChannel().sendMessage("Mais, mais qu'est ce que c'est ?\nun Avion ? non\nun Bastion ?, non\nune Porte ?\nnon c'est juste Logan\nC'est la même chose").queue();
        if (r == 1)
            msg.getChannel().sendMessage("Grogan en action").queue();
        if (r == 2)
            msg.getChannel().sendMessage("Créateur de la porte humaine").queue();
        if (r == 3)
            msg.getChannel().sendMessage("Putain frérot arrete de post des mêmes j'en peux plus\nJe suis pas venu ici pour souffrir, ok ?").queue();
    }
}
