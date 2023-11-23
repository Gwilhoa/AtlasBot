package fr.atlas.listeners;

import fr.atlas.objects.imgExtenders;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.IOException;
import java.util.Random;

public class MembersQuotes {
	
    static String[] roro =
			{
					"Shadow in the night",
					"Un jour il dira a Momo d'arreter de pomper la co\nUn jour...",
					"Super koala de destruction massif",
					"D'après ce qu'on m'a dit cette personne est sacrée\ndonc j'en déduis qu'il est parlementaire",
					"",
					"https://tenor.com/view/chika-chika-dance-anime-anime-dance-dance-gif-13973731",
					"blblblbl"
			};


	static String[] noke =
			{
					"Ce qui est sur c'est qu'il est direct",
					"Il est un peu énervé sur les bords",
					"Je bois pas pour me bourrer la gueule\nJuste pour etre assez con",
					"Friendzoned by momo",
					"Ne jamais le laisser seul, sinon il va se battre"
			};

	static String[] virgile =
			{
					"OTP Swain askip il a déja avoué que c'etait broken",
					"Direction le mordor",
					"La vodka c'est pas pour lui",
					"Il se pourrait que parfois il touche de l'herbe\ncelle de la faille de l'invocateur",
					"Il veut faire des croisades contre les roux (sans rancunes oscar)",
					"Roi des galipettes",
					"Non, on nage pas sous les tables,\nc'est pas fait pour ça",
					"",
					"Un vrai Homme, askip il a un couteau dans ses couilles",
					"***YAAAEEEEGERR***",
					"Son nom est swain"
			};
	static String[] jhuna =
			{
					"En recherche de sugar daddy",
					"uwu",
					"Jure il est bizarre",
					"https://images-ext-1.discordapp.net/external/CgwlejaODq9TVO3rXKvmeT-ALd448sPrM8pZXaoGX7I/%3Fsize%3D96%26quality%3Dlossless/https/cdn.discordapp.com/emojis/588193027030253581.gif"
			};

    static String[] max =
	{
					"La personne la plus calme et la plus sereine que je connaisse \n\n*ou pas*",
					"Le chevalier ou le roi du sel en personne\nRien que ça",
					"Composé à 95% de sel",
					"Un jour il sera calme, un jour...",
					"ComputerSlayer"
	};
    
    static String[] logan =
			{
					"Mais, mais qu'est ce que c'est ?\nun Avion ? non\nun Bastion ?, non\nune Porte ?\nnon c'est juste Logan\nC'est la même chose",
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
				"C'est rien, c'est la rue",
				"42 main",
				"Une soudaine envie de dev"
	};

	static String[] smog =
			{
					"Random de qualité",
					"Raleur de qualité",
					"Si ça tenait qu'à lui une personne disparaitrais",
					"Gilet jaune de qualité",
					"Antonin en plus gros, moche et avec de la barbe",
					"sheeeeesh"

			};

    static String[] enki =
	{
		"https://www.alcool-info-service.fr",
		"https://mediapi.org/support",
		"https://www.caf.fr/",
		"Enki, c'est mon hébergeur, il a chopé 2147483647 fois le covid\nil est parfois un peu con dans ses décisions",
		"il a pas une carte fidelité burger king ?",
		"je pense qu'il a déjà fait 4 fois le tour du monde en voiture",
			"XILAM POWER"
	};
    
    static String[] oscar =
	{
		"TRACTEUR VROUM VROUM !!!!!",
		"vitesse moyenne en montagne : 250km/h",
		"apparemment le porc ça le connait",
		"hmmm, de ce qu'on m'a dis il aime bien boire, et il vole des panneaux",
		"apparemment il sait qui gagne entre un tracteur et un mur"
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
		"https://www.youtube.com/watch?v=DeumyOzKqgI",
			"Qui s'en fout ?",
			"il sait terraformer IRL"
	};
    
    static String[] jojo =
			{
					"Obsédé de saint pierre d'allevard",
					"Qu'est ce qui est jaune et qui attends ?",
					"De ce qu'on m'a dit il a un humour fin et raffinés et aussi un peu maigre et musculeux\n\nhmmmmm je crois que j'ai menti",
					"",
					"Un petit fortnite ?",
					"Top 1 du B2K sur doodle jump",
					"Créateur de la playlist qui tue sa mère",
					"god of chicken"
			};

	static String[] jules =
			{
					"207 édition 64\t™",
					"",
					"",
					"des montages de qualités, ça c'est sur",
					"qu'est ce que poutine fait au collet d'allevard ?",
					"bourré en 30 min TopCommand chrono"
			};

	static String[] yann =
			{
					"Premier conseiller connu",
					"Prend un grand plaisir a faire chier guigui",
					"un homme bon a marier"
			};
    public static boolean MemberReact(Message msg) throws IOException {
        if (msg.getContentRaw().contains("max"))
            quoteDefault(msg, max);
        else if (msg.getContentRaw().contains("logan"))
        	quoteDefault(msg, logan);
        else if (msg.getContentRaw().contains("guigui"))
        	quoteDefault(msg, guigui);
        else if (msg.getContentRaw().contains("enki"))
        	quoteDefault(msg, enki);
        else if (msg.getContentRaw().contains("oscar"))
        	quoteDefault(msg, oscar);
        else if (msg.getContentRaw().contains("antonin"))
        	quoteDefault(msg, anto);
        else if (msg.getContentRaw().contains("jonathan"))
            Jojo(msg);
        else if (msg.getContentRaw().contains("roro"))
			Roro(msg);
		else if (msg.getContentRaw().contains("yann"))
			quoteDefault(msg, yann);
		else if (msg.getContentRaw().contains("jules"))
			Jules(msg);
		else if (msg.getContentRaw().contains("noke"))
			quoteDefault(msg, noke);
		else if (msg.getContentRaw().contains("jhuna"))
			quoteDefault(msg, jhuna);
		else if (msg.getContentRaw().contains("virgile"))
			Virgile(msg);
		else if (msg.getContentRaw().contains("smog") || msg.getContentRaw().contains("aurel")){
			quoteDefault(msg,smog);
		}
		else return false;
        return true;
    }
    
    

	static void Virgile(Message msg) throws IOException {
		int r = new Random().nextInt(virgile.length);
		if (r == 7) {
			msg.getChannel().sendMessage("il a juste fait 2147483647 game,\nen vrai il en a peut etre fait plus mais j'arrive plus a compter").queue();
		} else {
			msg.getChannel().sendMessage(virgile[r]).queue();
		}
	}

    static void Jojo(Message msg) throws IOException {

        int r = new Random().nextInt(jojo.length);
		if (r == 3) {
			msg.getChannel().sendFiles(FileUpload.fromData(imgExtenders.getFile("jojo.png"))).queue();
		} else {
			msg.getChannel().sendMessage(jojo[r]).queue();
		}
    }
	static void Jules(Message msg) throws IOException {
		int r = new Random().nextInt(jules.length);
		if (r == 1) {
			msg.getChannel().sendFiles(FileUpload.fromData(imgExtenders.getFile("jules_1.jpg"))).queue();
		} if (r == 2){
			msg.getChannel().sendFiles(FileUpload.fromData(imgExtenders.getFile("jules_2.jpeg"))).queue();
		}
		else {
			msg.getChannel().sendMessage(jules[r]).queue();
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
