package me.windyteam.kura.command.commands.gui;

import me.windyteam.kura.command.Command;
import me.windyteam.kura.command.syntax.ChunkBuilder;
import me.windyteam.kura.gui.clickgui.GUIRender;
import me.windyteam.kura.gui.clickgui.Panel;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.gui.clickgui.GUIRender;
import me.windyteam.kura.gui.clickgui.Panel;
import me.windyteam.kura.utils.mc.ChatUtil;

public class FixGuiCommand
extends Command {
    public FixGuiCommand() {
        super("fixgui", new ChunkBuilder().build());
        this.setDescription("Allows you to disable the automatic gui positioning");
    }

    @Override
    public void call(String[] args2) {
        int startX = 5;
        for (Panel panel : GUIRender.getINSTANCE().panels) {
            panel.y = 5;
            panel.x = startX;
            startX += 100;
        }
        ChatUtil.NoSpam.sendWarnMessage("[Gui] Fix Done.");
    }
}

