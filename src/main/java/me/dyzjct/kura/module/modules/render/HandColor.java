package me.dyzjct.kura.module.modules.render;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;

import java.awt.*;

@Module.Info(name = "HandColor", description = "null", category = Category.RENDER)
public class HandColor extends Module{

    public static HandColor INSTANCE;
    public Setting<Boolean> colorSync = bsetting("ColorSync",false);
    public Setting<Boolean> rainbow = bsetting("Rainbow",true);
    public Setting<Integer> saturation = isetting("Saturation",50,0,100);
    public Setting<Integer> brightness = isetting("Brightness",50,0,100);
    public Setting<Integer> speed = isetting("Speed",50,0,100);
    public Setting<Integer> red = isetting("Red",255,1,255);
    public Setting<Integer> green = isetting("Green",192,1,255);
    public Setting<Integer> blue = isetting("Blue",203,1,255);
    public Setting<Integer> alpha = isetting("Alpha",90,1,255);
    public float hue;
    public HandColor(){
        HandColor.INSTANCE = this;
    }

    public void setInstance() {
        HandColor.INSTANCE = this;
    }

    public static HandColor getInstance() {
        if (HandColor.INSTANCE == null) {
            HandColor.INSTANCE = new HandColor();
        }
        return HandColor.INSTANCE;
    }

    public static HandColor getINSTANCE() {
        if (HandColor.INSTANCE == null) {
            HandColor.INSTANCE = new HandColor();
        }
        return HandColor.INSTANCE;
    }

    static {
        HandColor.INSTANCE = new HandColor();
    }

    public Color getCurrentColor() {
        final int colorSpeed = 101 - this.speed.getValue();
        this.hue = System.currentTimeMillis() % (360 * colorSpeed) / (360.0f * colorSpeed);
        if (this.rainbow.getValue()) {
            return Color.getHSBColor(this.hue, this.saturation.getValue() / 255.0f, this.brightness.getValue() / 255.0f);
        }
        return new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
    }
}
