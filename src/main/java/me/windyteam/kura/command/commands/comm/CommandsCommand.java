package me.windyteam.kura.command.commands.comm;

import me.windyteam.kura.Kura;
import me.windyteam.kura.command.Command;
import me.windyteam.kura.command.syntax.SyntaxChunk;
import me.windyteam.kura.utils.mc.ChatUtil;

import java.util.Comparator;

public class CommandsCommand
        extends Command {
    public CommandsCommand() {
        super("commands", SyntaxChunk.EMPTY, "cmds");
        this.setDescription("Gives you this list of commands");
    }

    @Override
    public void call(String[] args2) {
        Kura.Companion.getInstance().commandManager.getCommands().stream().sorted(Comparator.comparing(Command::getLabel)).forEach(command -> ChatUtil.sendMessage("&f" + Command.getCommandPrefix() + command.getLabel() + "&r ~ &7" + command.getDescription()));
    }
}

