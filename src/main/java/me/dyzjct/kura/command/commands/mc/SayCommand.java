package me.dyzjct.kura.command.commands.mc;

import me.dyzjct.kura.command.Command;
import me.dyzjct.kura.command.syntax.ChunkBuilder;
import me.dyzjct.kura.utils.mc.ChatUtil;

public class SayCommand
extends Command {
    public SayCommand() {
        super("say", new ChunkBuilder().append("message").build());
        this.setDescription("Allows you to send any message, even with a prefix in it");
    }

    @Override
    public void call(String[] args2) {
        StringBuilder message = new StringBuilder();
        for (String arg : args2) {
            if (arg == null) continue;
            message.append(" ").append(arg);
        }
        ChatUtil.sendServerMessage(message.toString());
    }
}

