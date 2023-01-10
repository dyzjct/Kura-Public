package me.windyteam.kura.notification.notifications;

import me.windyteam.kura.notification.Notification;
import me.windyteam.kura.notification.NotificationType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class BottomRightNotification
        extends Notification {
    public BottomRightNotification(NotificationType type, String title, String message, int length) {
        super(type, title, message, length);
    }

    @Override
    public void render(int RealDisplayWidth, int RealDisplayHeight) {
        Color color1;
        int width = 120;
        int height = 30;
        int offset = this.getOffset(width);
        Color color = new Color(0, 0, 0, 220);
        if (this.type == NotificationType.INFO) {
            color1 = new Color(0, 26, 169);
        } else if (this.type == NotificationType.WARNING) {
            color1 = new Color(204, 193, 0);
        } else {
            color1 = new Color(204, 0, 18);
            int i = Math.max(0, Math.min(255, (int) (Math.sin((double) this.getTime() / 100.0) * 255.0 / 2.0 + 127.5)));
            color = new Color(i, 0, 0, 220);
        }
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        Gui.drawRect(RealDisplayWidth - offset, RealDisplayHeight - 5 - height, RealDisplayWidth, RealDisplayHeight - 5, color.getRGB());
        Gui.drawRect(RealDisplayWidth - offset, RealDisplayHeight - 5 - height, RealDisplayWidth - offset + 4, RealDisplayHeight - 5, color1.getRGB());
        fontRenderer.drawString(this.title, RealDisplayWidth - offset + 8, RealDisplayHeight - 2 - height, -1);
        fontRenderer.drawString(this.message, RealDisplayWidth - offset + 8, RealDisplayHeight - 15, -1);
    }
}

