package me.dyzjct.kura.gui.xg42guistart;

import me.dyzjct.kura.gui.xg42guistart.component.DisplayMainMenu;
import me.dyzjct.kura.gui.xg42guistart.component.FeatMelonGui;
import me.dyzjct.kura.gui.xg42guistart.component.ProtectByEskidGUI;
import me.dyzjct.kura.utils.Timer;
import me.dyzjct.kura.utils.gl.RenderUtils;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.concurrent.LinkedBlockingQueue;

public class XG42GuiStart
extends GuiScreen {
    private final Timer time = new Timer();
    private final Timer timer = new Timer();
    private static final LinkedBlockingQueue<Lcomponent> pendingNotifications = new LinkedBlockingQueue<>();
    private static Lcomponent currentNotification = null;

    public XG42GuiStart() {
        pendingNotifications.add(new ProtectByEskidGUI(5));
        pendingNotifications.add(new FeatMelonGui(5));
        pendingNotifications.add(new DisplayMainMenu(1));
        this.time.reset();
    }

    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        RenderUtils.drawRect(0.0, 0.0, this.width, this.height, Color.black);
        if (this.time.passed(1300L)) {
            if (currentNotification != null && !currentNotification.isShown()) {
                timer.reset();
                currentNotification = null;
            }
            if (currentNotification == null && !pendingNotifications.isEmpty()) {
                if (timer.passed(150)){
                    currentNotification = pendingNotifications.poll();
                    currentNotification.show();
                }
            }
            if (currentNotification != null) {
                currentNotification.render(this.width, this.height);
            }
        }
        if (currentNotification == null) {
            RenderUtils.drawRect(0.0, 0.0, this.width, this.height, Color.black);
        }
    }
}

