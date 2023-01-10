package me.windyteam.kura.utils.render.gui;

import me.windyteam.kura.utils.render.FadeUtil;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class ScalaCalc {
    public final FadeUtil FadeUtil = new FadeUtil(700L);
    public double percent;
    public FadeUtil.FadeMode fadeMode = me.windyteam.kura.utils.render.FadeUtil.FadeMode.FADE_ONE;

    public ScalaCalc() {
        this.reset();
    }

    public void drawA(ScaledResolution sr) {
        GL11.glPushMatrix();
        this.percent = this.FadeUtil.getFade(this.fadeMode);
        GL11.glTranslated((double)((double)sr.getScaledWidth() / 2.0), (double)((double)sr.getScaledHeight() / 2.0), (double)0.0);
        GL11.glScaled((double)this.percent, (double)this.percent, (double)0.0);
        GL11.glTranslated((double)((double)(-sr.getScaledWidth()) / 2.0), (double)((double)(-sr.getScaledHeight()) / 2.0), (double)0.0);
    }

    public void drawB(ScaledResolution sr) {
        GL11.glTranslated((double)((double)sr.getScaledWidth() / 2.0), (double)((double)sr.getScaledHeight() / 2.0), (double)0.0);
        GL11.glScaled((double)(1.0 / this.percent), (double)(1.0 / this.percent), (double)0.0);
        GL11.glTranslated((double)((double)(-sr.getScaledWidth()) / 2.0), (double)((double)(-sr.getScaledHeight()) / 2.0), (double)0.0);
        GL11.glTranslated((double)((double)sr.getScaledWidth() / 2.0), (double)((double)sr.getScaledHeight() / 2.0), (double)0.0);
        GL11.glScaled((double)this.percent, (double)this.percent, (double)0.0);
        GL11.glTranslated((double)((double)(-sr.getScaledWidth()) / 2.0), (double)((double)(-sr.getScaledHeight()) / 2.0), (double)0.0);
        GL11.glPopMatrix();
    }

    public void reset() {
        this.FadeUtil.reset();
    }

    public ScalaCalc setAnimationTime(long ms) {
        this.FadeUtil.setLength(ms);
        return this;
    }

    public ScalaCalc setFadeMode(FadeUtil.FadeMode fadeMode) {
        this.fadeMode = fadeMode;
        return this;
    }
}

