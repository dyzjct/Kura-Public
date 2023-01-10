package me.windyteam.kura.gui.xg42guistart.component;

import me.windyteam.kura.gui.mcguimainmenu.XG42MainMenu;
import me.windyteam.kura.gui.xg42guistart.Lcomponent;
import me.windyteam.kura.gui.mcguimainmenu.XG42MainMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class DisplayMainMenu
extends Lcomponent {
    public DisplayMainMenu(int length) {
        super(length);
    }

    @Override
    public void render(int realDisplayWidth, int realDisplayHeight) {
        Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new XG42MainMenu());
    }
}

