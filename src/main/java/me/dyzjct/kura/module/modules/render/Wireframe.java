package me.dyzjct.kura.module.modules.render;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.ModeSetting;
import me.dyzjct.kura.setting.Setting;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "WireFrame", category = Category.RENDER)
public class Wireframe extends Module {
    private static Wireframe INSTANCE = new Wireframe();

    public ModeSetting<Page> p = msetting("Page", Page.ONE);
    public Setting<RenderMode> mode = msetting("Mode", RenderMode.WIREFRAME).m(p, Page.ONE);
    public Setting<RenderMode> cMode = msetting("CMode", RenderMode.SOLID).m(p, Page.ONE);
    public Setting<Boolean> players = bsetting("Players", true).m(p, Page.ONE);
    public Setting<Boolean> playerModel = bsetting("PlayerModels", true).m(p, Page.ONE);
    public Setting<Boolean> crystals = bsetting("Crystals", true).m(p, Page.ONE);
    public Setting<Boolean> crystalModel = bsetting("CrystalModels", true).m(p, Page.ONE);
    public Setting<Float> alpha = fsetting("Alpha", 87, 0.1f, 255).m(p, Page.TWO);
    public Setting<Float> cAlpha = fsetting("CAlpha", 87, 0.1f, 255).m(p, Page.TWO);
    public Setting<Float> lineWidth = fsetting("Width", 1, 0.1f, 5).m(p, Page.TWO);
    public Setting<Float> crystalLineWidth = fsetting("CWidth", 1, 0.1f, 5).m(p, Page.TWO);
    public Setting<Boolean> rainbow = bsetting("Rainbow", true).m(p, Page.THREE);
    public Setting<Integer> rainbowHue = isetting("RainbowDelay", 240, 0, 600).m(p, Page.THREE);
    public Setting<Float> rainbowSaturation = fsetting("Saturation", 150, 1, 255).m(p, Page.THREE);
    public Setting<Float> rainbowBrightness = fsetting("Brightness", 150, 1, 255).m(p, Page.THREE);
    public Setting<Integer> red = isetting("Red", 255, 1, 255).m(p, Page.THREE);
    public Setting<Integer> green = isetting("Green", 192, 1, 255).m(p, Page.THREE);
    public Setting<Integer> blue = isetting("Blue", 203, 1, 255).m(p, Page.THREE);

    @SubscribeEvent
    public void RenderPlayer(RenderPlayerEvent.Pre event) {
        event.getEntityPlayer().hurtTime = 0;
    }

    public Wireframe() {
        this.setInstance();
    }

    public static Wireframe getInstance() {
        if (Wireframe.INSTANCE == null) {
            Wireframe.INSTANCE = new Wireframe();
        }
        return Wireframe.INSTANCE;
    }

    public static Wireframe getINSTANCE() {
        if (Wireframe.INSTANCE == null) {
            Wireframe.INSTANCE = new Wireframe();
        }
        return Wireframe.INSTANCE;
    }

    private void setInstance() {
        Wireframe.INSTANCE = this;
    }

    private enum Page {
        ONE, TWO, THREE
    }

    public enum RenderMode {
        SOLID,
        WIREFRAME
    }
}
