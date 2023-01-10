package me.windyteam.kura.module.hud.info;

import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.HUDModule;
import me.windyteam.kura.utils.math.InfoCalculator;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.utils.mc.ChatUtil;

import java.awt.*;

/**
 * Created by B_312 on 01/03/21
 */
@HUDModule.Info(name = "Speed", x = 140, y = 160, width = 100, height = 10,category = Category.HUD)
public class SpeedHud extends HUDModule {

    @Override
    public void onRender() {

        int fontColor = new Color(GuiManager.getINSTANCE().getRed() / 255f, GuiManager.getINSTANCE().getGreen() / 255f, GuiManager.getINSTANCE().getBlue() / 255f, 1F).getRGB();

        String Final = "Speed " + ChatUtil.SECTIONSIGN + "f" + InfoCalculator.speed(true,mc) + " km/h";

        fontRenderer.drawString(Final, this.x + 2, this.y + 4, fontColor);

        this.width = fontRenderer.getStringWidth(Final) + 4;

    }

}
