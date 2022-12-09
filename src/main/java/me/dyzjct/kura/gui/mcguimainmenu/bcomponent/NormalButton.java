package me.dyzjct.kura.gui.mcguimainmenu.bcomponent;

import me.dyzjct.kura.gui.mcguimainmenu.ButtonComponent;
import me.dyzjct.kura.utils.gl.RenderUtils;
import java.awt.Color;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL41;

public class NormalButton
extends ButtonComponent {
    public NormalButton(String title, float x, float y, float width, float height) {
        super(title, x, y, width, height);
    }

    @Override
    public void render() {
        if (this.pointAlpha >= 0) {
            this.pointAlpha -= 15;
            if (this.pointAlpha <= 0) {
                this.pointAlpha = 0;
            }
        }
        GL41.glClearDepthf(1.0f);
        GL11.glClear(256);
        GL11.glColorMask(false, false, false, false);
        GL11.glDepthFunc(513);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        RenderUtils.drawRoundedRectangle(this.x, this.y, this.width, this.height, 3.0, Color.WHITE);
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(true);
        GL11.glDepthFunc(514);
        RenderUtils.drawRoundedRectangle(this.x, this.y, this.width, this.height, 3.0, RenderUtils.GradientDirection.LeftToRight, new Color(60, 90, 210), new Color(170, 120, 235));
        this.font.drawCenteredString(this.title, this.x + this.width / 2.0f, this.y + this.height / 2.0f, -1);
        if (this.pressPoint != null && this.pointAlpha != 0) {
            RenderUtils.setColor(new Color(255, 255, 255, this.pointAlpha));
            RenderUtils.drawCircle(this.pressPoint.x, this.pressPoint.y, 21.25f - (float)this.pointAlpha / 12.0f);
        }
        GL41.glClearDepthf(1.0f);
        GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glClear(1280);
        GL11.glDisable(2929);
        GL11.glDepthFunc(515);
        GL11.glDepthMask(false);
    }
}

