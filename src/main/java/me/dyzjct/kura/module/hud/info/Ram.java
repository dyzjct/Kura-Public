package me.dyzjct.kura.module.hud.info;

import me.dyzjct.kura.manager.GuiManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.HUDModule;
import me.dyzjct.kura.utils.math.InfoCalculator;
import me.dyzjct.kura.utils.mc.ChatUtil;

import java.awt.*;

@HUDModule.Info(name = "Ram", x = 160, y = 160, width = 100, height = 10,category = Category.HUD)
public class Ram extends HUDModule {

    @Override
    public void onRender() {

        int fontColor = new Color(GuiManager.getINSTANCE().getRed() / 255f, GuiManager.getINSTANCE().getGreen() / 255f, GuiManager.getINSTANCE().getBlue() / 255f, 1F).getRGB();

        String Final = "Ram Usage " + ChatUtil.SECTIONSIGN + "f" + InfoCalculator.memory();
        fontRenderer.drawString(Final, this.x + 2, this.y + 4, fontColor);

        this.width = fontRenderer.getStringWidth(Final) + 4;

    }

}
