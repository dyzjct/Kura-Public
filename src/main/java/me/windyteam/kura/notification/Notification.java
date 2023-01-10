package me.windyteam.kura.notification;

import me.windyteam.kura.utils.Rainbow;
import me.windyteam.kura.utils.gl.RenderUtils;
import me.windyteam.kura.utils.font.CFontRenderer;
import me.windyteam.kura.utils.Rainbow;
import me.windyteam.kura.utils.font.CFontRenderer;
import me.windyteam.kura.utils.gl.RenderUtils;

import java.awt.Color;

public abstract class Notification {
    protected final NotificationType type;
    protected final String title;
    protected final String message;
    protected long start;
    protected final long fadedIn;
    protected final long fadeOut;
    protected final long end;
    protected CFontRenderer font = RenderUtils.getFontRender();

    public Notification(NotificationType type, String title, String message, int length) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.fadedIn = 100L * (long)length;
        this.fadeOut = this.fadedIn + 150L * (long)length;
        this.end = this.fadeOut + this.fadedIn;
    }

    public void show() {
        this.start = System.currentTimeMillis();
    }

    public boolean isShown() {
        return this.getTime() <= this.end;
    }

    protected long getTime() {
        return System.currentTimeMillis() - this.start;
    }

    protected int getOffset(double maxWidth) {
        if (this.getTime() < this.fadedIn) {
            return (int)(Math.tanh((double)this.getTime() / (double)this.fadedIn * 3.0) * maxWidth);
        }
        if (this.getTime() > this.fadeOut) {
            return (int)(Math.tanh(3.0 - (double)(this.getTime() - this.fadeOut) / (double)(this.end - this.fadeOut) * 3.0) * maxWidth);
        }
        return (int)maxWidth;
    }

    protected Color getDefaultTypeColor() {
        if (this.type == NotificationType.INFO) {
            return Color.BLUE;
        }
        if (this.type == NotificationType.WARNING) {
            return new Color(218, 165, 32);
        }
        if (this.type == NotificationType.RAINBOW) {
            return Rainbow.getRainbowColor(7.0f, 0.75f, 1.0f);
        }
        return Color.RED;
    }

    public abstract void render(int var1, int var2);
}

