package me.dyzjct.kura.utils.color;

import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class Colors extends Color {

    public Colors(int r, int g, int b) {
        super(r, g, b);
    }

    public Colors(int r, int g, int b, int a) {
        super(r,g,b,a);
    }

    public void glColor() {
        GlStateManager.color(getRed()/255.0f,getGreen()/255.0f,getBlue()/255.0f,getAlpha()/255.0f);
    }
}
