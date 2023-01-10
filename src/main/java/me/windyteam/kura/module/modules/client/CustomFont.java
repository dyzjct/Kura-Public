package me.windyteam.kura.module.modules.client;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.ModeSetting;
import me.windyteam.kura.utils.font.CFontRenderer;
import me.windyteam.kura.utils.font.FontUtils;

@Module.Info(name = "CustomFont", category = Category.CLIENT, description = "Custom Font That We can change")
public class CustomFont extends Module {
    public static CustomFont INSTANCE;
    public ModeSetting<?> CGfont = this.msetting("CG Font", FontRender.ROBOTO);
    public ModeSetting<?> SetPanFont = this.msetting("SetPanFont", FontRender.MONTSERRAT);
    public ModeSetting<?> HUDfont = this.msetting("HUD Font", FontRender.CALIBRI);
    public ModeSetting<?> IDKfont = this.msetting("IDK Font", FontRender.CALIBRI);
//    public BooleanSetting overrideMinecraft = bsetting("overrideMinecraft",false);
    public static CFontRenderer getCGFont() {
        return INSTANCE != null ? certifiedFont((FontRender) INSTANCE.CGfont.getValue()) : FontUtils.Calibri;
    }

    public static CFontRenderer getSetPanFontFont() {
        return INSTANCE != null ? certifiedFont((FontRender) INSTANCE.SetPanFont.getValue()) : FontUtils.Calibri;
    }

    public static CFontRenderer getHUDFont() {
        return INSTANCE != null ? certifiedFont((FontRender) INSTANCE.HUDfont.getValue()) : FontUtils.Calibri;
    }

    public static CFontRenderer getIDKFont() {
        return INSTANCE != null ? certifiedFont((FontRender) INSTANCE.IDKfont.getValue()) : FontUtils.Calibri;
    }

    public static CFontRenderer certifiedFont(FontRender font) {
        switch (font) {
            case CALIBRI: {
                return FontUtils.Calibri;
            }
            case COMFORTAA: {
                return FontUtils.Comfortaa;
            }
            case FOUGHTKNIGHT: {
                return FontUtils.FoughtKnight;
            }
            case GOLDMAN: {
                return FontUtils.Goldman;
            }
            case LEMONMILK: {
                return FontUtils.LemonMilk;
            }
            case MODERNSPACE: {
                return FontUtils.ModernSpace;
            }
            case MONTSERRAT: {
                return FontUtils.Montserrat;
            }
            case NINJAGO: {
                return FontUtils.Ninjago;
            }
            case ROBOTO: {
                return FontUtils.Roboto;
            }
        }
        return FontUtils.Goldman;
    }

    @Override
    public void onInit() {
        INSTANCE = this;
    }

    public enum FontRender {
        CALIBRI,
        COMFORTAA,
        FOUGHTKNIGHT,
        GOLDMAN,
        LEMONMILK,
        MODERNSPACE,
        MONTSERRAT,
        NINJAGO,
        ROBOTO
    }
}
