package me.dyzjct.kura.command.commands.comm;

import me.dyzjct.kura.Kura;
import me.dyzjct.kura.command.Command;
import me.dyzjct.kura.command.syntax.SyntaxChunk;
import me.dyzjct.kura.utils.mc.ChatUtil;

import java.util.Comparator;

public class CommandsCommand
        extends Command {
    public CommandsCommand() {
        super("commands", SyntaxChunk.EMPTY, "cmds");
        this.setDescription("Gives you this list of commands");
    }

    @Override
    public void call(String[] args2) {
        Kura.getInstance().getCommandManager().getCommands().stream().sorted(Comparator.comparing(Command::getLabel)).forEach(command -> ChatUtil.sendMessage("&f" + Command.getCommandPrefix() + command.getLabel() + "&r ~ &7" + command.getDescription()));
    }
}

