package me.windyteam.kura.command;

import me.windyteam.kura.Kura;
import me.windyteam.kura.command.commands.CreditsCommand;
import me.windyteam.kura.command.commands.HelpCommand;
import me.windyteam.kura.command.commands.comm.BindCommand;
import me.windyteam.kura.command.commands.comm.CommandsCommand;
import me.windyteam.kura.command.commands.comm.PrefixCommand;
import me.windyteam.kura.command.commands.gui.FixGuiCommand;
import me.windyteam.kura.command.commands.mc.*;
import me.windyteam.kura.command.commands.module.*;
import me.windyteam.kura.friend.FriendCommand;
import me.windyteam.kura.utils.mc.ChatUtil;

import java.util.ArrayList;
import java.util.LinkedList;

public class CommandManager {
    public static CommandManager INSTANCE;
    public static ArrayList<Command> commands = new ArrayList<>();

    public CommandManager() {
        INSTANCE = this;
        registerCommand(new BindCommand());
        registerCommand(new CommandsCommand());
        registerCommand(new PrefixCommand());
        registerCommand(new FixGuiCommand());
        //registerCommand(new IRC());
        //registerCommand(new Online());
        //registerCommand(new Tell());
        registerCommand(new EntityStatsCommand());
        registerCommand(new NBTCommand());
        registerCommand(new PeekCommand());
        registerCommand(new SayCommand());
        registerCommand(new SignBookCommand());
        registerCommand(new ConfigCommand());
        registerCommand(new DescriptionCommand());
        registerCommand(new DupeBookCommand());
        registerCommand(new EnabledCommand());
        registerCommand(new SetCommand());
        registerCommand(new SettingsCommand());
        registerCommand(new ToggleCommand());
        //registerCommand(new YoutubeDownload());
        registerCommand(new CreditsCommand());
        registerCommand(new HelpCommand());
        registerCommand(new FriendCommand());
        registerCommand(new FakePlayerCommand());
        registerCommand(new ClipCommand());
        Kura.logger.info("Commands initialised");
    }

    public static void registerCommand(Command command) {
        try {
            commands.add(command);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't initiate module " + command.getClass().getSimpleName() + "! Err: " + e.getClass().getSimpleName() + ", message: " + e.getMessage());
        }
    }

    public static String[] removeElement(String[] input, int indexToDelete) {
        LinkedList<String> result = new LinkedList<>();
        for (int i = 0; i < input.length; ++i) {
            if (i == indexToDelete) continue;
            result.add(input[i]);
        }
        return result.toArray(input);
    }

    public static String strip(String str) {
        if (str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring("\"".length(), str.length() - "\"".length());
        }
        return str;
    }

    public void callCommand(String command) {
        String[] parts = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        String label = parts[0].contains(" ") ? parts[0].substring(parts[0].indexOf(" ")).substring(1) : parts[0].substring(1);
        String[] args2 = CommandManager.removeElement(parts, 0);
        for (int i = 0; i < args2.length; ++i) {
            if (args2[i] == null) continue;
            args2[i] = CommandManager.strip(args2[i]);
        }
        for (Command c : commands) {
            if (c.getLabel().equalsIgnoreCase(label)) {
                c.call(parts);
                this.runAliases(c);
                return;
            }
            if (c.getAliases().stream().noneMatch(alias -> alias.equalsIgnoreCase(label))) continue;
            c.call(parts);
            return;
        }
        ChatUtil.NoSpam.sendWarnMessage("Unknown command. try '&f" + Command.getCommandPrefix() + "cmds' for a list of commands.");
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public void runAliases(Command command) {
        int amount = command.getAliases().size();
        if (amount > 0) {
            ChatUtil.NoSpam.sendWarnMessage("'" + command.getLabel() + "' has " + this.grammar1(amount) + "alias" + this.grammar2(amount));
            ChatUtil.NoSpam.sendWarnMessage(command.getAliases().toString());
        }
    }

    public String grammar1(int amount) {
        if (amount == 1) {
            return "an ";
        }
        return amount + " ";
    }

    public String grammar2(int amount) {
        if (amount == 1) {
            return "!";
        }
        return "es!";
    }
}

