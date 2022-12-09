package me.dyzjct.kura.module.hud.huds;

import me.dyzjct.kura.module.HUDModule;
import me.dyzjct.kura.setting.FloatSetting;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.setting.StringSetting;
import me.dyzjct.kura.utils.font.CFont;
import me.dyzjct.kura.utils.font.RFontRenderer;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.CopyOnWriteArrayList;

@HUDModule.Info(name = "WaterMark", x = 20, y = 20)
public class WaterMark extends HUDModule {
    public CopyOnWriteArrayList<RFontRenderer> fonts = new CopyOnWriteArrayList<>();
    public StringSetting text = this.ssetting("ViewText", "Kura");
//    public ColorSetting color = csetting("Color", new Color(255,255,255));
    public Setting<Float> saturation = this.fsetting("Saturation",1.0f,0f,1.0f);
    public Setting<Float> brightness = this.fsetting("Brightness",1.0f,0f,1.0f);
    public Setting<Integer> alpha = isetting("Alpha",90,1,255);
    public FloatSetting speed = this.fsetting("RainbowSpeed", 1.0f, 0.0f, 1.0f);
    public FloatSetting Scala = this.fsetting("Scala", 1.0f, 0.0f, 3.0f);
    RFontRenderer font = new RFontRenderer(new CFont.CustomFont("/assets/fonts/font.ttf", 47.0f, 0), true, false);

    @Override
    public void onRender() {
        if (!fonts.contains(font)) {
            fonts.add(font);
        }
//        int fontColor = new Color(GuiManager.getINSTANCE().getRed() / 255f, GuiManager.getINSTANCE().getGreen() / 255f, GuiManager.getINSTANCE().getBlue() / 255f, 1F).getRGB();

        fonts.forEach(f -> {
            GL11.glPushMatrix();
            GL11.glTranslated(this.x, (float) this.y, 0);
            GL11.glScaled((double) this.Scala.getValue(), (double) this.Scala.getValue(), 0.0);
            f.drawString(this.text.getValue(), 0, 0, speed.getValue().floatValue(), saturation.getValue().floatValue(), this.brightness.getValue().floatValue(), 50, this.alpha.getValue(),false);
            GL11.glPopMatrix();
            this.width = (int) ((float) f.getStringWidth(this.text.getValue()) * this.Scala.getValue());
            this.height = (int) ((float) f.getHeight() * this.Scala.getValue());
        });
    }
    public enum Mode {
        Rainbow , Custom;
    }
}

