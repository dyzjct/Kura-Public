package me.dyzjct.kura.gui;

import me.dyzjct.kura.utils.Timer;
import me.dyzjct.kura.utils.gl.XG42Tessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class Notification {

    public static String ICON_NOTIFY_INFO = "\u2139";
    public static String ICON_NOTIFY_SUCCESS = "\u2713";
    public static String ICON_NOTIFY_WARN = "\u26A0";
    public static String ICON_NOTIFY_ERROR = "\u26A0";
    public static String ICON_NOTIFY_DISABLED = "\u2717";
    public Timer timer = new Timer();
    public Type t;
    public long stayTime;
    public String message;
    public double lastY, posY, width, height, animationX;
    public int color;

    public Notification(String message, Type type) {
        this.message = message;
        timer.reset();
        width = Minecraft.getMinecraft().fontRenderer.getStringWidth(message) + 35;
        height = 20;
        animationX = width;
        stayTime = 1000;
        posY = -1;
        t = type;
        if (type.equals(Type.INFO)) {
            color = -14342875;
        } else if (type.equals(Type.ERROR)) {
            color = new Color(36, 36, 36).getRGB();
        } else if (type.equals(Type.SUCCESS)) {
            color = new Color(36, 36, 36).getRGB();
        } else if (type.equals(Type.DISABLE)) {
            color = new Color(36, 36, 36).getRGB();
        } else if (type.equals(Type.WARNING)) {
            color = -14342875;
        }
    }

    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = 0.003921569f * c.getRed();
        float g = 0.003921569f * c.getGreen();
        float b = 0.003921569f * c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }

    public void draw(double getY, double lastY) {
        width = Minecraft.getMinecraft().fontRenderer.getStringWidth(message) + 25;
        height = 22;
        this.lastY = lastY;
        animationX = this.getAnimationState(animationX, isFinished() ? width : 0, 450);
        if (posY == -1) {
            posY = getY;
        } else {
            posY = this.getAnimationState(posY, getY, 350);
        }
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        int x1 = (int) (res.getScaledWidth() - width + animationX / 2f), x2 = (int) (res.getScaledWidth() + animationX / 2f), y1 = (int) posY - 22, y2 = (int) (y1 + height);

        XG42Tessellator.drawRect(x1, y1, x2, y2, reAlpha(color, 0.85f));
        XG42Tessellator.drawRect(x1, y2 - 1, x1 + Math.min((x2 - x1) * (System.currentTimeMillis() - timer.getTime()) / stayTime, x2 - x1), y2, reAlpha(-1, 0.85f));

        switch (t) {
            case ERROR:
                Minecraft.getMinecraft().fontRenderer.drawString(ICON_NOTIFY_ERROR, x1 + 5, y1 + 7, -65794);
                break;
            case INFO:
                Minecraft.getMinecraft().fontRenderer.drawString(ICON_NOTIFY_INFO, x1 + 5, y1 + 7, -65794);
                break;
            case SUCCESS:
                Minecraft.getMinecraft().fontRenderer.drawString(ICON_NOTIFY_SUCCESS, x1 + 5, y1 + 7, -65794);
                break;
            case WARNING:
                Minecraft.getMinecraft().fontRenderer.drawString(ICON_NOTIFY_WARN, x1 + 5, y1 + 7, -65794);
                break;
            case DISABLE:
                Minecraft.getMinecraft().fontRenderer.drawString(ICON_NOTIFY_DISABLED, x1 + 5, y1 + 7, -65794);
                break;

        }

        y1 += 1;
        if (message.contains(" Enabled")) {
            Minecraft.getMinecraft().fontRenderer.drawString(message, (x1 + 19), (int) (y1 + height / 4F), -1);
            Minecraft.getMinecraft().fontRenderer.drawString(" Enabled", (x1 + 20 + Minecraft.getMinecraft().fontRenderer.getStringWidth(message)), (int) (y1 + height / 4F), -9868951);
        } else if (message.contains(" Disabled")) {
            Minecraft.getMinecraft().fontRenderer.drawString(message, (x1 + 19), (int) (y1 + height / 4F), -1);
            Minecraft.getMinecraft().fontRenderer.drawString(" Disabled", (x1 + 20 + Minecraft.getMinecraft().fontRenderer.getStringWidth(message)), (int) (y1 + height / 4F), -9868951);
        } else {
            Minecraft.getMinecraft().fontRenderer.drawString(message, (x1 + 20), (int) (y1 + height / 4F), -1);
        }

    }

    public boolean shouldDelete() {
        return isFinished() && animationX >= width;
    }

    public boolean isFinished() {
        return timer.passed(stayTime) && posY == lastY;
    }

    public double getHeight() {
        return height;
    }

    public double getAnimationState(double animation, double finalState, double speed) {
        float add = (float) (Minecraft.getMinecraft().timer.renderPartialTicks * speed * speed);
        if (animation < finalState) {
            if (animation + add < finalState)
                animation += add;
            else
                animation = finalState;
        } else {
            if (animation - add > finalState)
                animation -= add;
            else
                animation = finalState;
        }
        return animation;
    }

    public enum Type {
        SUCCESS, INFO, WARNING, ERROR, DISABLE
    }
}
