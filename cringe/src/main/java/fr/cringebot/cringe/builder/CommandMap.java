package fr.cringebot.cringe.builder;

import fr.cringebot.BotDiscord;
import fr.cringebot.cringe.builder.Command.ExecutorType;
import fr.cringebot.cringe.command.CommandDefault;
import fr.cringebot.cringe.command.CommandPokemon;
import fr.cringebot.cringe.command.HelpCommand;
import fr.cringebot.cringe.command.UtilCommand;
import fr.cringebot.music.MusicCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public final class CommandMap {

    private final static String tag = ">";

    private final BotDiscord botDiscord;


    private final Map<String, SimpleCommand> commands = new HashMap<>();


    public CommandMap(BotDiscord botDiscord) {
        this.botDiscord = botDiscord;
        registerCommands(new CommandDefault(botDiscord, this), new HelpCommand(this), new MusicCommand(), new UtilCommand(this.botDiscord), new CommandPokemon());
    }

    public static String getTag() {
        return tag;
    }

    public Collection<SimpleCommand> getCommands(){
        return commands.values();
    }

    public void registerCommands(Object...objects){
        for(Object object : objects) registerCommand(object);
    }

    public void registerCommand(Object object){
        for(Method method : object.getClass().getDeclaredMethods()){
            if(method.isAnnotationPresent(Command.class)){
                Command command = method.getAnnotation(Command.class);
                method.setAccessible(true);
                SimpleCommand simpleCommand = new SimpleCommand(command.name(), command.description(), command.type(), object, method, command.power());
                commands.put(command.name(), simpleCommand);
            }
        }
    }

    public void commandConsole(String command){
        Object[] object = getCommand(command);
        if(object[0] == null || ((SimpleCommand)object[0]).getExecutorType() == ExecutorType.USER){
            System.out.println("Commande inconnue.");
            return;
        }
        try{
            execute(((SimpleCommand)object[0]), command, (String[])object[1], null);
        }catch(Exception exception){
            System.out.println("La methode "+((SimpleCommand)object[0]).getMethod().getName()+" n'est pas correctement initialisé.");
        }
    }

    public boolean commandUser(String command, Message message){
        Object[] object = getCommand(command);
        if(object[0] == null || ((SimpleCommand)object[0]).getExecutorType() == ExecutorType.CONSOLE) return false;

        message.getGuild();

        try{
            execute(((SimpleCommand)object[0]), command.toLowerCase(),(String[])object[1], message);
        }catch(Exception exception){
            System.out.println("La methode "+((SimpleCommand)object[0]).getMethod().getName()+" n'est pas correctement initialisé.");
        }
        return true;
    }

    private Object[] getCommand(String command){
        String[] commandSplit = command.split(" ");
        String[] args = new String[commandSplit.length-1];
        for(int i = 1; i < commandSplit.length; i++) args[i-1] = commandSplit[i];
        SimpleCommand simpleCommand = commands.get(commandSplit[0]);
        return new Object[]{simpleCommand, args};
    }

    private void execute(SimpleCommand simpleCommand, String command, String[] args, Message message) throws Exception{
        Parameter[] parameters = simpleCommand.getMethod().getParameters();
        Object[] objects = new Object[parameters.length];
        for(int i = 0; i < parameters.length; i++){
            if(parameters[i].getType() == String[].class) objects[i] = args;
            else if(parameters[i].getType() == User.class) objects[i] = message == null ? null : message.getAuthor();
            else if(parameters[i].getType() == TextChannel.class) objects[i] = message == null ? null : message.getTextChannel();
            else if(parameters[i].getType() == PrivateChannel.class) objects[i] = message == null ? null : message.getPrivateChannel();
            else if(parameters[i].getType() == Guild.class) objects[i] = message == null ? null : message.getGuild();
            else if(parameters[i].getType() == String.class) objects[i] = command;
            else if(parameters[i].getType() == Message.class) objects[i] = message;
            else if(parameters[i].getType() == JDA.class) objects[i] = botDiscord.getJda();
            else if(parameters[i].getType() == MessageChannel.class) objects[i] = message == null ? null : message.getChannel();
        }
        simpleCommand.getMethod().invoke(simpleCommand.getObject(), objects);
    }
}