package fr.cringebot.cringe.event;

import fr.cringebot.cringe.objects.MessageReact;
import fr.cringebot.cringe.objects.imgExtenders;
import net.dv8tion.jda.api.entities.Message;

import java.io.IOException;
import java.util.Random;

public class MembersQuotes {
	
    public static boolean MemberReact(Message msg) throws IOException {
        if (msg.getContentRaw().equalsIgnoreCase("max"))
            quoteDefault(msg, max);
        else if (msg.getContentRaw().equalsIgnoreCase("logan"))
        	quoteDefault(msg, logan);
        else if (msg.getContentRaw().equalsIgnoreCase("guigui"))
        	quoteDefault(msg, guigui);
        else if (msg.getContentRaw().equalsIgnoreCase("enki"))
        	quoteDefault(msg, enki);
        else if (msg.getContentRaw().equalsIgnoreCase("oscar"))
        	quoteDefault(msg, oscar);
        else if (msg.getContentRaw().equalsIgnoreCase("antonin"))
        	quoteDefault(msg, anto);
        else if (msg.getContentRaw().equalsIgnoreCase("jonathan"))
            Jojo(msg);
        else if (msg.getContentRaw().equalsIgnoreCase("roro"))
            Roro(msg);
	else if (msg.getContentRaw().equalsIgnoreCase("yann"))
            Yann(msg);
        else return false;
        return true;
    }
    

    
    static String[] max =
	{
		"Mais, mais qu'est ce que c'est ?\nun Avion ? non\nun Bastion ?, non\nune Porte ?\nnon c'est juste Logan\nC'est la même chose",
		"Le chevalier ou le roi du sel en personne\nRien que ça",
		"Composé à 95% de sel",
		"Un jour il sera calme, un jour...",
		"ComputerSlayer"
	};
    
    static String[] logan =
	{
		"La personne la plus calme et la plus sereine que je connaisse \n\n*ou pas*",
		"Grogan en action",
		"Créateur de la porte humaine",
		"Putain frérot arrete de post des mêmes j'en peux plus\nJe suis pas venu ici pour souffrir, ok ?"
	};
    
    static String[] guigui =
	{
		"C'est quoi une vache ?",
		"Roro :heart:",
		"Créateur de la méta Diablo backlane",
		"Mon magnifique créateur, il est noir et PD,\nil a vraiment rien pour lui",
		"ChairSlayer",
		"ah ouuuuaiis \nça se passe comme ça dans le Nexus",
		"mamaaaaaaaaaaa",
		"C'est rien, c'est la rue"
	};
    
    static String[] enki =
	{
		"https://www.alcool-info-service.fr",
		"https://www.pole-emploi.fr",
		"https://www.caf.fr/",
		"Enki, c'est mon hébergeur, il a chopé 2147483647 fois le covid\nil est parfois un peu con dans ses décisions",
		"il a pas une carte fidelité burger king ?",
		"je pense qu'il a déjà fait 4 fois le tour du monde en voiture"
	};
    
    static String[] oscar =
	{
		"TRACTEUR VROUM VROUM !!!!!",
		"vitesse moyenne en montagne : 250km/h",
		"aparemment le porc ça le connait",
		"hmmm, de ce qu'on m'a dis il aime bien boire, et il vole des panneaux",
		"aparemment il sait qui gagne entre un tracteur et un mur"
	};
    
    static String[] anto =
	{
		"Male alpha",
		"**ahou ahou**",
		"de ce qu'on m'a dit c'est qu'il est toujours plus fort que les autres car il est a fond\n*Un jour il se fera dépasser mais pas par enki*",
		"Un homme musculeux !",
		"Son deuxième prénom est discrétion !",
		"Le PussySlayer par excellence !",
		"***Errape.mp3***",
		"https://www.youtube.com/watch?v=DeumyOzKqgI"
	};
    
    static String[] jojo =
    {
    	"Obsédé de saint pierre d'allevard",
    	"Qu'est ce qui est jaune et qui attends ?",
    	"De ce qu'on m'a dit il a un humour fin et raffinés et aussi un peu maigre et musculeux\n\nhmmmmm je crois que j'ai menti",
    	"",
    	"Un petit fortnite ?",
    	"https://tenor.com/view/chika-chika-dance-anime-anime-dance-dance-gif-13973731",
    	"Top 1 du B2K sur doodle jump",
    	"créateur de la playlist qui tue sa mère"
    };

    static String[] roro =
	{
		"Shadow in the night",
		"Un jour il dira a Momo d'arreter de pomper la co\nUn jour...",
		"Super koala de destruction massif",
		"D'après ce qu'on m'a dit cette peronne est sacrée\ndonc j'en déduis qu'il est parlementaire",
		""
	};

    static String[] yann =
	{
		"Premier conseiller connues",
		"prend un grand plaisir a faire chier guigui",
		""
	};
    
    

    /*
     * Action spécifique pour Jonathan pour le choix 3.
     */
    static void Jojo(Message msg) throws IOException {
        int r = new Random().nextInt(jojo.length);
		if (r == 3) {
			msg.getChannel().sendFile(imgExtenders.getFile("jojo.png")).queue();
		} else {
			msg.getChannel().sendMessage(jojo[r]).queue();
		}
    }

    /*
     * Action spécifique pour Robin pour le choix 4.
     */
    static void Roro(Message msg) {
        int r = new Random().nextInt(roro.length);
		if (r == 4) {
			msg.getChannel().sendMessage("Salut c'est toi, et moi\n moi c'est CringeBot et toi, c'est " + msg.getMember().getAsMention()).queue();
		} else {
			msg.getChannel().sendMessage(roro[r]).queue();
		}
    }
    
    /*
     * Fonction par défaut qui se contente de choisir aléatoirement dans l'array donné et d'afficher
     * le message choisi en réponse du message "msg".
     */
    static void quoteDefault(Message msg, String[] lequel) {
        int r = new Random().nextInt(lequel.length);
        msg.getChannel().sendMessage(lequel[r]).queue();
    }
}
