package me.dyzjct.kura.module.hud.huds;

import me.dyzjct.kura.manager.GuiManager;
import me.dyzjct.kura.module.HUDModule;
import me.dyzjct.kura.module.HUDModule.Info;
import me.dyzjct.kura.module.IModule;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.ColorSetting;
import me.dyzjct.kura.setting.DoubleSetting;
import me.dyzjct.kura.setting.IntegerSetting;
import me.dyzjct.kura.setting.ModeSetting;
import me.dyzjct.kura.utils.color.ColorUtil;
import me.dyzjct.kura.utils.font.CFont;
import me.dyzjct.kura.utils.font.CFontRenderer;
import me.dyzjct.kura.utils.gl.MelonTessellator;
import me.dyzjct.kura.utils.hud.AnimationUtil;
import java.awt.Color;
import java.util.Comparator;

import me.dyzjct.kura.utils.mc.ChatUtil;
import net.minecraft.client.gui.ScaledResolution;

@Info(name = "ArrayList", x = 50, y = 50, width = 100, height = 100)
public class ShowArrayList extends HUDModule {
    public static CFontRenderer fonts = new CFontRenderer(new CFont.CustomFont("/assets/fonts/Montserrat.ttf", 20.0F, 0), true, false);

    public static ShowArrayList INSTANCE = new ShowArrayList();

    public int count = 0;

    public BooleanSetting customFont = bsetting("CustomFont", true);

    public BooleanSetting drawBG = bsetting("BlurBackground", true);

    public BooleanSetting anim = bsetting("StressAnimation", false);

    public DoubleSetting animationSpeed = dsetting("AnimationSpeed", 3.5D, 0.0D, 5.0D);

    public BooleanSetting sideLine = bsetting("SideLine", false);

    public IntegerSetting sideLineWidth = isetting("SideWidth", -1, -1, 5).b(this.sideLine);

    public ColorSetting sideColor = csetting("SideLineColor", new Color(231, 13, 103)).b(this.sideLine);

    public IntegerSetting sideAlpha = isetting("SideAlpha", 80, 1, 255).b(this.sideLine);

    ModeSetting<Mode> mode = msetting("Mode", Mode.Rainbow);

    public ColorSetting color = csetting("Color", new Color(210, 100, 165)).m(this.mode, Mode.Custom);

    private String getArrayList(IModule module) {
        return module.getName() + ((module.getHudInfo() == null || module
                .getHudInfo().equals("")) ? "" : (" " + ChatUtil.SECTIONSIGN + "7" + ((module.getHudInfo().equals("") || module
                .getHudInfo() == null) ? "" : "[") + ChatUtil.SECTIONSIGN + "f" + module.getHudInfo() + '\u00a7' + "7" + (module.getHudInfo().equals("") ? "" : "]")));
    }

    public void onRender() {
        this.count = 0;
        int screenWidth = (new ScaledResolution(mc)).getScaledWidth();
        ModuleManager.getModules().stream().filter(IModule::isEnabled).sorted(Comparator.comparing(module -> Integer.valueOf(((Boolean)this.customFont.getValue()).booleanValue() ? (fonts.getStringWidth(getArrayList(module)) * -1) : (mc.fontRenderer.getStringWidth(getArrayList(module)) * -1)))).forEach(module -> {
            if (((Module)module).isShownOnArray()) {
                int screenWidthScaled = (new ScaledResolution(mc)).getScaledWidth();
                float modWidth = ((Boolean)this.customFont.getValue()).booleanValue() ? fonts.getStringWidth(getArrayList(module)) : mc.fontRenderer.getStringWidth(getArrayList(module));
                String modText = getArrayList(module);
                if (module.remainingAnimation < modWidth && module.isEnabled())
                    module.remainingAnimation = AnimationUtil.moveTowards(module.remainingAnimation, modWidth + 1.0F, (float)(0.009999999776482582D + ((Double)this.animationSpeed.getValue()).doubleValue() / 30.0D), 0.1F, ((Boolean)this.anim.getValue()).booleanValue());
                if (module.remainingAnimation > modWidth && !module.isEnabled()) {
                    module.remainingAnimation = -AnimationUtil.moveTowards(module.remainingAnimation, modWidth + 1.0F, (float)(0.009999999776482582D + ((Double)this.animationSpeed.getValue()).doubleValue() / 30.0D), 0.1F, ((Boolean)this.anim.getValue()).booleanValue());
                } else if (module.remainingAnimation <= modWidth && !module.isEnabled()) {
                    module.remainingAnimation = -1.0F;
                }
                if (module.remainingAnimation > modWidth && module.isEnabled())
                    module.remainingAnimation = modWidth;
                if (this.x < screenWidthScaled / 2) {
                    if (((Boolean)this.drawBG.getValue()).booleanValue())
                        MelonTessellator.drawRect((int)((this.x - 1) - modWidth + module.remainingAnimation), (this.y + 10 * this.count), (int)((this.x - 2) + module.remainingAnimation), (this.y + 10 * this.count + 10), (new Color(0, 0, 0, 70)).getRGB());
                    if (((Boolean)this.customFont.getValue()).booleanValue()) {
                        fonts.drawString(modText, (int)((this.x - 2) - modWidth + module.remainingAnimation), (this.y + 10 * this.count), generateColor());
                    } else {
                        mc.fontRenderer.drawStringWithShadow(modText, (int)((this.x - 2) - modWidth + module.remainingAnimation), (this.y + 10 * this.count), generateColor());
                    }
                } else {
                    if (((Boolean)this.drawBG.getValue()).booleanValue())
                        MelonTessellator.drawRect((int)(this.x - module.remainingAnimation - 2.0F), (this.y + 10 * this.count), (int)(this.x - module.remainingAnimation + modWidth), (this.y + 10 * this.count + 10), (new Color(0, 0, 0, 70)).getRGB());
                    if (((Boolean)this.sideLine.getValue()).booleanValue()) {
                        Color sColor = new Color(((Color)this.sideColor.getValue()).getRed(), ((Color)this.sideColor.getValue()).getGreen(), ((Color)this.sideColor.getValue()).getBlue(), ((Integer)this.sideAlpha.getValue()).intValue());
                        MelonTessellator.drawRect((int)(this.x - module.remainingAnimation - 2.0F), (this.y + 10 * this.count), (int)(this.x - module.remainingAnimation + ((Integer)this.sideLineWidth.getValue()).intValue()), (this.y + 10 * this.count + 10), sColor.getRGB());
                    }
                    if (((Boolean)this.customFont.getValue()).booleanValue()) {
                        fonts.drawString(modText, (int)(this.x - module.remainingAnimation), (this.y + 10 * this.count), generateColor());
                    } else {
                        mc.fontRenderer.drawStringWithShadow(modText, (int)(this.x - module.remainingAnimation), (this.y + 10 * this.count), generateColor());
                    }
                }
                this.count++;
            }
        });
        if (this.x < screenWidth / 2) {
            this.width = 75;
        } else {
            this.width = -75;
        }
        this.height = (fonts.getHeight() + 1) * this.count;
    }

    public int generateColor() {
        int fontColor = (new Color(GuiManager.getINSTANCE().getRed() / 255.0F, GuiManager.getINSTANCE().getGreen() / 255.0F, GuiManager.getINSTANCE().getBlue() / 255.0F, 1.0F)).getRGB();
        int custom = (new Color(((Color)this.color.getValue()).getRed(), ((Color)this.color.getValue()).getGreen(), ((Color)this.color.getValue()).getBlue())).getRGB();
        switch ((Mode)this.mode.getValue()) {
            case Rainbow:
                return ColorUtil.staticRainbow().getRGB();
            case GuiSync:
                return fontColor;
            case Custom:
                return custom;
        }
        return -1;
    }

    public enum Mode {
        Rainbow, GuiSync, Custom;
    }
}
