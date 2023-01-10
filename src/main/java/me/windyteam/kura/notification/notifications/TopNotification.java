package me.windyteam.kura.notification.notifications;

import me.windyteam.kura.notification.Notification;
import me.windyteam.kura.notification.NotificationType;
import me.windyteam.kura.utils.color.ColorUtils;
import me.windyteam.kura.utils.gl.RenderUtils;
import me.windyteam.kura.utils.color.ColorUtils;
import me.windyteam.kura.utils.gl.RenderUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class TopNotification extends Notification {
    public Map<Integer, Integer> offsetDirLeft = new HashMap<>();
    public Map<Integer, Integer> offsetDirRight = new HashMap<>();
    public int arrAmount = 0;
    public boolean shouldAdd = true;
    public int delaydir = 0;

    public TopNotification(NotificationType type, String title, String message, int length) {
        super(type, title, message, length);
    }

    @Override
    public void render(int RealDisplayWidth, int RealDisplayHeight) {
        ++this.delaydir;
        int height = this.font.getHeight() * 4;
        int width = RealDisplayWidth / 4;
        int offset = this.getOffset(width);
        Color color = this.type == NotificationType.INFO ? Color.BLACK : this.getDefaultTypeColor();
        boolean shouldEffect = offset >= width - 5;
        int cx = RealDisplayWidth / 2;
        int cy = RealDisplayHeight / 8;
        int x = cx - offset;
        int dWidth = offset * 2;
        if (shouldEffect) {
            int alpha;
            int value;
            if (this.shouldAdd) {
                this.offsetDirLeft.put(this.arrAmount, -16 - 10 * this.arrAmount);
                this.offsetDirRight.put(this.arrAmount, -16 - 10 * this.arrAmount);
                ++this.arrAmount;
                if (this.arrAmount >= 3) {
                    this.arrAmount = 0;
                    this.shouldAdd = false;
                }
            }
            GL11.glLineWidth(2.0f);
            for (Map.Entry<Integer, Integer> offsetdir : this.offsetDirLeft.entrySet()) {
                value = offsetdir.getValue();
                alpha = ColorUtils.calculateAlphaChangeColor(255, 10, 50, value);
                RenderUtils.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                this.drawC(x - value, cy, height, height);
            }
            for (Map.Entry<Integer, Integer> offsetdir : this.offsetDirRight.entrySet()) {
                value = offsetdir.getValue();
                alpha = ColorUtils.calculateAlphaChangeColor(255, 10, 50, value);
                RenderUtils.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                this.drawBC(x + dWidth + value, cy, height, height);
            }
        }
        int delay = 3;
        if (this.delaydir >= delay) {
            this.delaydir = 0;
            for (Map.Entry<Integer, Integer> offsetdir : this.offsetDirLeft.entrySet()) {
                if (offsetdir.getValue() >= 50) {
                    offsetdir.setValue(-16);
                    continue;
                }
                offsetdir.setValue(offsetdir.getValue() + 1);
            }
            for (Map.Entry<Integer, Integer> offsetdir : this.offsetDirRight.entrySet()) {
                if (offsetdir.getValue() >= 50) {
                    offsetdir.setValue(-16);
                    continue;
                }
                offsetdir.setValue(offsetdir.getValue() + 1);
            }
        }
        RenderUtils.drawRect(x, cy, dWidth, height, color);
        RenderUtils.drawTriangle(x, cy, x - 15 - 1, cy + height / 2.0, x, cy + height, color);
        RenderUtils.drawTriangle(x + dWidth, cy + height, x + 15 + dWidth, cy + height / 2.0, x + dWidth, cy, color);
        int fx = x + dWidth / 2;
        int alpha = ColorUtils.calculateAlphaChangeColor(10, 255, width, offset);
        this.font.drawString(this.title, fx - this.font.getStringWidth(this.title) / 2f, cy + 3, new Color(255, 255, 255, alpha).getRGB());
        this.font.drawString(this.message, fx - this.font.getStringWidth(this.message) / 2f, cy + this.font.getHeight() + 8, new Color(255, 255, 255, alpha).getRGB());
        if (!this.shouldAdd && !shouldEffect) {
            this.offsetDirLeft.remove(this.arrAmount);
            this.offsetDirRight.remove(this.arrAmount);
            ++this.arrAmount;
            if (this.arrAmount >= 3) {
                this.arrAmount = 0;
                this.shouldAdd = true;
            }
        }
    }

    public void drawBC(int cx, int cy, int height, int margin) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBegin(3);
        GL11.glVertex2d(cx, cy);
        GL11.glVertex2d(cx + margin, cy + height / 2.0);
        GL11.glVertex2d(cx, cy + height);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    public void drawC(int cx, int cy, int height, int margin) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBegin(3);
        GL11.glVertex2d(cx, cy);
        GL11.glVertex2d((cx - margin), (cy + height / 2.0));
        GL11.glVertex2d(cx, (cy + height));
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }
}

