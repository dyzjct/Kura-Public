package me.dyzjct.kura.command.commands.module;

import me.dyzjct.kura.command.Command;
import me.dyzjct.kura.module.IModule;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.utils.mc.ChatUtil;

public class FakePlayerCommand
extends Command {
    public FakePlayerCommand() {
        super("fp");
        this.setDescription("Quickly toggle a module on and off");
    }

    @Override
    public void call(String[] args2) {
        if (args2.length == 0) {
            ChatUtil.NoSpam.sendWarnMessage("Please specify a module!");
            return;
        }
        IModule m = ModuleManager.getModuleByName("FakePlayer");
        m.toggle();
        ChatUtil.NoSpam.sendWarnMessage(m.getName() + (m.isEnabled() ? ChatUtil.SECTIONSIGN + "a"+ " Enabled" : " §cDisabled"));
    }
}

