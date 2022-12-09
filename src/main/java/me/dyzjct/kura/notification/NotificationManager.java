package me.dyzjct.kura.notification;

import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.client.Minecraft;

public class NotificationManager {
    public static final LinkedBlockingQueue<Notification> pendingNotifications = new LinkedBlockingQueue<>();
    private static Notification currentNotification = null;

    public static void show(Notification notification) {
        pendingNotifications.add(notification);
    }

    public static void update() {
        if (currentNotification != null && !currentNotification.isShown()) {
            currentNotification = null;
        }
        if (currentNotification == null && !pendingNotifications.isEmpty()) {
            currentNotification = pendingNotifications.poll();
            currentNotification.show();
        }
    }

    public static void render() {
        try {
            int divider = Minecraft.getMinecraft().gameSettings.guiScale;
            int Dwidth = Minecraft.getMinecraft().displayWidth / divider;
            int Dheight = Minecraft.getMinecraft().displayHeight / divider;
            NotificationManager.update();
            if (currentNotification != null) {
                currentNotification.render(Dwidth, Dheight);
            }
        }catch (Exception ignored){}
    }

}

