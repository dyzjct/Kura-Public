package me.windyteam.kura.command.commands.module;

import me.windyteam.kura.command.Command;
import me.windyteam.kura.command.syntax.ChunkBuilder;
import me.windyteam.kura.command.syntax.parsers.ModuleParser;
import me.windyteam.kura.module.IModule;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.module.IModule;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.utils.mc.ChatUtil;

public class ToggleCommand
extends Command {
    public ToggleCommand() {
        super("toggle", new ChunkBuilder().append("module", true, new ModuleParser()).build(), "t");
        this.setDescription("Quickly toggle a module on and off");
    }

    @Override
    public void call(String[] args2) {
        if (args2.length == 0) {
            ChatUtil.NoSpam.sendWarnMessage("Please specify a module!");
            return;
        }
        IModule m = ModuleManager.getModuleByName(args2[0]);
        if (m == null) {
            ChatUtil.NoSpam.sendWarnMessage("Unknown module '" + args2[0] + "'");
            return;
        }
        m.toggle();
        ChatUtil.NoSpam.sendWarnMessage(m.getName() + (m.isEnabled() ? " §aEnabled" : " §cDisabled"));
    }
}

