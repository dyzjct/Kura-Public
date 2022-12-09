package me.dyzjct.kura.command.commands.module;

import me.dyzjct.kura.command.Command;
import me.dyzjct.kura.command.syntax.ChunkBuilder;
import me.dyzjct.kura.module.IModule;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.utils.mc.ChatUtil;

public class DescriptionCommand
extends Command {
    public DescriptionCommand() {
        super("description", new ChunkBuilder().append("module").build(), "tooltip");
        this.setDescription("Prints a module's description into the chat");
    }

    @Override
    public void call(String[] args2) {
        for (String s : args2) {
            if (s == null) continue;
            IModule module = ModuleManager.getModuleByName(s);
            if (module != null){
                ChatUtil.sendMessage(module.getName() + "Description: &7" + module.description);
            }
        }
    }
}

