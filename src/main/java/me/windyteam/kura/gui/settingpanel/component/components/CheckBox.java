package me.windyteam.kura.gui.settingpanel.component.components;

import me.windyteam.kura.gui.settingpanel.Window;
import me.windyteam.kura.gui.settingpanel.component.AbstractComponent;
import me.windyteam.kura.gui.settingpanel.component.ValueChangeListener;
import me.windyteam.kura.gui.settingpanel.utils.GLUtil;
import me.windyteam.kura.gui.settingpanel.utils.UserValueChangeListener;
import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.utils.gl.RenderUtils;
import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.utils.gl.RenderUtils;

import java.awt.*;

public class CheckBox
        extends AbstractComponent {
    private static final int PREFERRED_HEIGHT = 22;
    private boolean selected;
    private String title;
    private final int preferredHeight;
    private boolean hovered;
    private ValueChangeListener<Boolean> listener;

    public CheckBox(String title, int preferredHeight) {
        this.preferredHeight = preferredHeight;
        this.setTitle(title);
    }

    public CheckBox(String title) {
        this(title, 22);
    }

    @Override
    public void render() {
        RenderUtils.drawRoundedRectangle(this.x, this.y, this.preferredHeight, this.preferredHeight, 7.0, this.hovered ? Window.SECONDARY_FOREGROUND : Window.TERTIARY_FOREGROUND);
        Color color2 = GuiManager.getINSTANCE().isRainbow() ? new Color(GuiManager.getINSTANCE().getRainbow()) : (this.hovered ? Window.TERTIARY_FOREGROUND : Window.SECONDARY_FOREGROUND);
        if (this.selected) {
            RenderUtils.drawRoundedRectangle(this.x + 3, this.y + 3, this.preferredHeight - 6, this.preferredHeight - 6, 3.0, color2);
        }
        Color colorHovered = GuiManager.getINSTANCE().isRainbow() ? new Color(GuiManager.getINSTANCE().getRainbow()) : (this.hovered ? Window.TERTIARY_OUTLINE : Window.SECONDARY_OUTLINE);
        RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.preferredHeight, this.preferredHeight, 7f, 1.0f, colorHovered);
        GLUtil.getFontRenderer().drawString(this.title, (float) (this.x + this.preferredHeight) + (float) this.preferredHeight / 2.0f, (float) this.y + (float) this.preferredHeight / 2.0f - (float) GLUtil.getFontRenderer().getHeight() / 2.0f, Window.FONT.getRGB());
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
            if (this.hovered) {
                boolean newVal = !this.selected;
                boolean change = true;
                if (this.listener != null) {
                    change = this.listener.onValueChange(newVal);
                    UserValueChangeListener.ValueChange();
                }
                if (change) {
                    this.selected = newVal;
                }
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
        this.setWidth(GLUtil.getFontRenderer().getStringWidth(title) + this.preferredHeight + this.preferredHeight / 4);
        this.setHeight(this.preferredHeight);
    }

    public void setListener(ValueChangeListener<Boolean> listener) {
        this.listener = listener;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

