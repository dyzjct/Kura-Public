package me.windyteam.kura.gui.settingpanel.component.components;

import me.windyteam.kura.gui.settingpanel.Window;
import me.windyteam.kura.gui.settingpanel.component.AbstractComponent;
import me.windyteam.kura.gui.settingpanel.component.ActionEventListener;
import me.windyteam.kura.gui.settingpanel.utils.GLUtil;
import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.utils.gl.RenderUtils;
import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.utils.gl.RenderUtils;

public class Button
extends AbstractComponent {
    private static final int PREFERRED_WIDTH = 180;
    private static final int PREFERRED_HEIGHT = 22;
    private String title;
    private int preferredWidth;
    private int preferredHeight;
    private boolean hovered;
    private ActionEventListener listener;

    public Button(String title, int preferredWidth, int preferredHeight) {
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
        this.setWidth(preferredWidth);
        this.setHeight(preferredHeight);
        this.setTitle(title);
    }

    public Button(String title) {
        this(title, 180, 22);
    }

    @Override
    public void render() {
        RenderUtils.drawRoundedRectangle(this.x, this.y, this.getWidth(), this.getHeight(), 7.0, this.hovered ? Window.SECONDARY_FOREGROUND : Window.TERTIARY_FOREGROUND);
        if (GuiManager.getINSTANCE().isRainbow()) {
            RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.getWidth(), this.getHeight(), 7f, 1.0f, RenderUtils.GradientDirection.LeftToRight, GuiManager.getINSTANCE().getRainbowColor(), GuiManager.getINSTANCE().getAddRainbowColor());
            GLUtil.getFontRenderer().drawString(this.title, (float)this.x + (float)this.getWidth() / 2.0f - (float)GLUtil.getFontRenderer().getStringWidth(this.title) / 2.0f, (float)this.y + (float)this.getHeight() / 2.0f - (float)GLUtil.getFontRenderer().getHeight() / 2.0f, Window.FONT.getRGB());
        } else {
            RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.getWidth(), this.getHeight(), 7f, 1.0f, Window.SECONDARY_OUTLINE);
            GLUtil.getFontRenderer().drawString(this.title, (float)this.x + (float)this.getWidth() / 2.0f - (float)GLUtil.getFontRenderer().getStringWidth(this.title) / 2.0f, (float)this.y + (float)this.getHeight() / 2.0f - (float)GLUtil.getFontRenderer().getHeight() / 2.0f, Window.FONT.getRGB());
        }
    }

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        this.updateHovered(x, y, offscreen);
        return false;
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        this.hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + this.getWidth() && y <= this.y + this.getHeight();
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        if (button == 0) {
            this.updateHovered(x, y, offscreen);
            if (this.hovered && this.listener != null) {
                this.listener.onActionEvent();
                return true;
            }
        }
        return false;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.setWidth(Math.max(GLUtil.getFontRenderer().getStringWidth(title), this.preferredWidth));
        this.setHeight(Math.max(GLUtil.getFontRenderer().getHeight() * 5 / 4, this.preferredHeight));
    }

    public ActionEventListener getOnClickListener() {
        return this.listener;
    }

    public void setOnClickListener(ActionEventListener listener) {
        this.listener = listener;
    }
}

