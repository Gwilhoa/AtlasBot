package fr.atlas.command;

import fr.atlas.BotDiscord;
import fr.atlas.Request.Achievement;
import fr.atlas.Request.Item;
import fr.atlas.Request.User;
import fr.atlas.Request.Squads;
import fr.atlas.builder.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.util.*;

import static fr.atlas.BotDiscord.setError;

public class Admin {
    private static boolean isArchived = false;

    @Command(name = "newsquad", description = "Ajoutes des coalitions", type = Command.ExecutorType.USER)
    private void addsquad(Message msg) {
        if (msg.getMember().getPermissions().contains(Permission.ADMINISTRATOR) && !msg.getMentions().getRoles().isEmpty()) {
            try {
                Squads.newSquads(msg.getMentions().getRoles().get(0).getName(), msg.getMentions().getRoles().get(0).getId(), msg.getMentions().getRoles().get(0).getColor());
            } catch (ConnectException e) {
                msg.getChannel().sendMessage("disconnected").queue();
            } catch (Exception e) {
                setError(e);
            }
        }
    }

    @Command(name = "addmember", description = "ajouter une personne manuellement a la base de donnée et lui donne une Escouade choisie", type = Command.ExecutorType.USER, permission = Permission.ADMINISTRATOR)
    private void addMember(Message msg)
    {
        msg.getMentions().getMembers().get(0).getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("vous avez été ajouté à l'escouade " + msg.getMentions().getRoles().get(0).getName()).queue());
        msg.getGuild().addRoleToMember(msg.getMentions().getMembers().get(0), msg.getMentions().getRoles().get(0)).queue();
        try {
            User.newMembers(msg.getMentions().getMembers().get(0), msg.getMentions().getRoles().get(0).getId());
        } catch (ConnectException e) {
            msg.getChannel().sendMessage("disconnected").queue();
        } catch (Exception e) {
            setError(e);
        }
        TextChannel tc = msg.getGuild().getTextChannelById("947564791759777792");
        tc.sendMessage(msg.getMentions().getMembers().get(0).getAsMention() + "a rejoint l'équipe "+ msg.getMentions().getRoles().get(0).getName()).queue();
    }

    @Command(name = "removepoints", description = "Enlève des point à une Escouade", type = Command.ExecutorType.USER)
    private void removepoints(Message msg)
    {
        if (msg.getMember().getPermissions().contains(Permission.ADMINISTRATOR))
        {
            try {
                Squads.addManualPoint(msg.getMentions().getRoles().get(0).getId(), (Integer.parseInt(msg.getContentRaw().split(" ")[1])) * -1);
            } catch (ConnectException e) {
            msg.getChannel().sendMessage("disconnected").queue();
            } catch (Exception e) {
                setError(e);
            }
        } else {
            msg.getChannel().sendMessage("tu n'as pas les droits").queue();
        }
    }

    @Command(name = "removeachievement", description = "permet de supprimer un achievement de la base de donnée", type = Command.ExecutorType.USER, permission = Permission.ADMINISTRATOR)
    private void removeAchievement(Message msg, MessageChannel channel, String[] args) {
        try {
            Achievement.removeAchievement(args[0]);
            channel.sendMessage("L'achievement " + args[0] + " a bien été supprimé.").queue();
        } catch (IOException e) {
            setError(e);
            channel.sendMessage("Une erreur est survenue lors de la suppression de l'achievement.").queue();
        }
    }

    @Command(name = "setactivity", type = Command.ExecutorType.USER, description = "permet de changer l'activité du bot", permission = Permission.ADMINISTRATOR)
    private void setactivity(Message msg) {
        String args[] = msg.getContentRaw().split(" ");
        if (args.length > 2) {
            switch (args[1]) {
                case "playing":
                    msg.getJDA().getPresence().setActivity(Activity.playing(args[2]));
                    break;
                case "listening":
                    msg.getJDA().getPresence().setActivity(Activity.listening(args[2]));
                    break;
                case "watching":
                    msg.getJDA().getPresence().setActivity(Activity.watching(args[2]));
                    break;
                case "streaming":
                    msg.getJDA().getPresence().setActivity(Activity.streaming(args[2], "https://www.twitch.tv/"));
                    break;
                default:
                    msg.getJDA().getPresence().setActivity(Activity.playing(args[2]));
                    break;
            }
        }
    }

    @Command(name = "slashupdate", description = "commande permettant de rafraichir les commandes '/'", type = Command.ExecutorType.USER)
    private void slashupdate(Message message) {
		/*
		message.getJDA().updateCommands().queue();
		message.getJDA().upsertCommand("top", "voir le soreboard des escouades")
				.addOption(OptionType.STRING, "squad", "voir les scoreboards des escouades", true, true)
				.queue();
		message.getJDA().upsertCommand("profil", "voir le profil d'un joueur")
				.addOption(OptionType.USER, "pseudo", "voir le profil d'un joueur", false, false)
				.queue();*/
    }

    @Command(name="archive", description = "permet d'archiver n'importe quelle salon", type = Command.ExecutorType.USER, permission = Permission.ADMINISTRATOR)
    private void archive(Message msg) throws IOException {
        ArrayList<String> messagesId = new ArrayList<>();
        File directory = new File("archive");
        if (directory.mkdir())
            System.out.println("[EVENT] directory created");
        else
            System.out.println("[DEBUG] directory already exist");
        File file = new File("archive/" + msg.getChannel().getName() + "_" + new Date(System.currentTimeMillis()).getDay() + "_" + new Date(System.currentTimeMillis()).getMonth() + "_" + new Date(System.currentTimeMillis()).getMonth()  + ".html");
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
        } catch (IOException error) {
            error.printStackTrace();
        }
        if (isArchived) {
            msg.getChannel().sendMessage("un archivage est déjà en cours").queue();
            return;
        }
        msg.getChannel().sendMessage("archive en cours").queue();
        isArchived = true;
        new Thread(() -> {
            try {
                FileWriter fileWriter = new FileWriter(file);
                boolean finish = true;
                Message pin = msg.getChannel().getHistoryFromBeginning(1).complete().getRetrievedHistory().get(0);
                String endid = msg.getId();
                while (finish) {
                    Thread.sleep(500);
                    fileWriter.write("<head>\n" +
                            "    <meta charset=\"utf-8\">\n<style>\n" +
                                    "        .avatar {\n" +
                                    "            width: 50px; /* Largeur de l'avatar */\n" +
                                    "            height: 50px; /* Hauteur de l'avatar */\n" +
                                    "            border-radius: 50%; /* Arrondi les coins pour créer une forme de cercle */\n" +
                                    "        }\n" +
                                    "        .message {\n" +
                                    "            background-color: #f2f2f2; /* Couleur de fond du message */\n" +
                                    "            padding: 10px; /* Espace interne du message */\n" +
                                    "            border: 1px solid #ccc; /* Bordure autour du message */\n" +
                                    "            border-radius: 5px; /* Coins arrondis */\n" +
                                    "        }\n" +
                                    "    </style>\n</head>\n"
                            );
                    ArrayList<Message> messages = new ArrayList<>(msg.getChannel().getHistoryAfter(pin, 100).complete().getRetrievedHistory());
                    Collections.reverse(messages);
                    for (Message message : messages) {
                        if (messagesId.contains(message.getId()))
                            continue;
                        StringBuilder sb = new StringBuilder();
                        sb.append("<img class=\"avatar\" src=\"");
                        sb.append(message.getAuthor().getAvatarUrl());
                        sb.append("\" alt=\"\">");
                        sb.append("<div class=\"message\">");
                        sb.append(message.getAuthor().getName());
                        sb.append(" (").append(message.getTimeCreated().getDayOfMonth()).append("/").append(message.getTimeCreated().getMonthValue()).append("/").append(message.getTimeCreated().getYear()).append(" à ").append(message.getTimeCreated().getHour()).append(":").append(message.getTimeCreated().getMinute()).append(") ");
                        sb.append(" : ");
                        sb.append(message.getContentRaw());
                        sb.append("\n");
                        for (Message.Attachment attachment : message.getAttachments()) {
                            sb.append("<img src=\"");
                            sb.append(attachment.getUrl());
                            sb.append("\" alt=\"\">");
                            sb.append("\n");
                        }
                        sb.append("\n</div>");
                        fileWriter.write(sb.toString());
                        messagesId.add(message.getId());
                        if (message.getId().equals(endid)) {
                            finish = false;
                            break;
                        }
                        pin = message;
                    }
                }
                fileWriter.close();
                msg.getChannel().sendMessage("archivage terminée").queue();
                isArchived = false;
            } catch (InterruptedException e) {
                setError(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    @Command(name = "reset", description = "permet de remettre a 0 certaines choses [PAS DEV]" ,type = Command.ExecutorType.USER)
    private void reset(Message msg) throws IOException {
        msg.getChannel().sendMessage("coming soon").queue();
    }


    @Command(name = "removesquad", description = "supprimer une Escouade existante [PAS DEV]", type = Command.ExecutorType.USER)
    private void RemoveSquad(Message msg) {
        if (msg.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
        }
    }


    @Command(name = "revokeachievement", description = "enlever un achievement à quelqu'un", type = Command.ExecutorType.USER, permission = Permission.ADMINISTRATOR)
    private void revokeAchievement(Message msg, String[] args) throws IOException {
        if (args.length == 2) {
            User.revokeAchievement(msg.getMentions().getMembers().get(0), args[1]);
            msg.getChannel().sendMessage("succès révoqué").queue();
        } else {
            msg.getChannel().sendMessage("Mauvais usage de la commande ! il faut mettre par exemple : >revokeachievement @user id").queue();
        }
    }

    @Command(name = "test", description = "permet de tester des fonctionnalités en cours de développement", type = Command.ExecutorType.USER, permission = Permission.ADMINISTRATOR)
    private void test(Message msg) throws IOException {
        List<User> users = User.getMembers();
        msg.getChannel().sendMessage(users.toString()).queue();
    }

    @Command(name = "giveachievement", description = "donner un achievement", type = Command.ExecutorType.USER, permission = Permission.ADMINISTRATOR)
    private void giveAchievement(String[] args, Message msg)
    {
        if (args.length == 2)
        {
            try {
                User.addAchievement(msg.getMentions().getMembers().get(0).getId(), args[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            msg.getChannel().sendMessage("mauvais usage de la commande").queue();
    }

    @Command(name = "createitem", description = "permet de créer un item", type = Command.ExecutorType.USER, permission = Permission.ADMINISTRATOR)
    private void createItem(Message msg) throws IOException {
        String[] args = msg.getContentRaw().split(";");
        if (args.length == 4) {
            int price = Integer.parseInt(args[3]);
            Item item = Item.createItem(args[1], args[2], price);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Item créé");
            embedBuilder.setDescription("**" + item.getName() + "**\n" + item.getDescription() + "\n" + item.getPrice() + " coins");
            msg.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        } else {
            msg.getChannel().sendMessage("mauvais usage de la commande").queue();
        }
    }

}
