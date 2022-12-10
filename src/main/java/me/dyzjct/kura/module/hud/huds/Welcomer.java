package me.dyzjct.kura.module.hud.huds;

import me.dyzjct.kura.manager.GuiManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.HUDModule;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.utils.font.CFont;
import me.dyzjct.kura.utils.font.RFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

@HUDModule.Info(name = "Welcomer", x = 20, y = 20)
public class Welcomer extends HUDModule {
    public CopyOnWriteArrayList<RFontRenderer> fonts = new CopyOnWriteArrayList<>();
    RFontRenderer font = new RFontRenderer(new CFont.CustomFont("/assets/fonts/font.ttf", 47.0f, 0), true, false);
    @Override
    public void onRender() {
        if (!fonts.contains(font)) {
            fonts.add(font);
        }
        int fontColor = new Color(GuiManager.getINSTANCE().getRed() / 255f, GuiManager.getINSTANCE().getGreen() / 255f, GuiManager.getINSTANCE().getBlue() / 255f, 1F).getRGB();
        String Final = "Welcome " + Minecraft.getMinecraft().player.getName() + "!Have a nice day :)";
        fontRenderer.drawString(Final, this.x + 2, this.y + 4, fontColor);
        this.width = fontRenderer.getStringWidth(Final) + 4;
    }
};

