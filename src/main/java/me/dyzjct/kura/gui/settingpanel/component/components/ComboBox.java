package me.dyzjct.kura.gui.settingpanel.component.components;

import me.dyzjct.kura.gui.settingpanel.Window;
import me.dyzjct.kura.gui.settingpanel.component.AbstractComponent;
import me.dyzjct.kura.gui.settingpanel.component.ValueChangeListener;
import me.dyzjct.kura.gui.settingpanel.utils.GLUtil;
import me.dyzjct.kura.gui.settingpanel.utils.UserValueChangeListener;
import me.dyzjct.kura.manager.GuiManager;
import me.dyzjct.kura.setting.ModeSetting;
import me.dyzjct.kura.utils.gl.RenderUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ComboBox
        extends AbstractComponent {
    private static final int PREFERRED_WIDTH = 180;
    private static final int PREFERRED_HEIGHT = 22;
    private final int preferredWidth;
    private final int preferredHeight;
    private boolean hovered;
    private boolean hoveredExtended;
    private ValueChangeListener<Integer> listener;
    private final String[] values;
    private int selectedIndex;
    private boolean opened;
    private int mouseX;
    private int mouseY;

    public ComboBox(int preferredWidth, int preferredHeight, String[] values2, int selectedIndex) {
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
        this.values = values2;
        this.selectedIndex = selectedIndex;
        this.setWidth(preferredWidth);
        this.updateHeight();
    }

    public ComboBox(String[] values2, int selectedIndex) {
        this(180, 22, values2, selectedIndex);
    }

    private void updateHeight() {
        if (this.opened) {
            this.setHeight(this.preferredHeight * this.values.length + 4);
        } else {
            this.setHeight(this.preferredHeight);
        }
    }

    @Override
    public void render() {
        double y1;
        double x1;
        double y2;
        double x2;
        double y3;
        double x3;
        this.updateHeight();
        RenderUtils.drawRoundedRectangle(this.x, this.y, this.getWidth(), this.getHeight(), 7f, Window.TERTIARY_FOREGROUND);
        if (this.hovered) {
            RenderUtils.drawRoundedRectangle(this.x, this.y, this.getWidth(), this.preferredHeight, 7f, Window.SECONDARY_FOREGROUND);
        } else if (this.hoveredExtended) {
            int offset = this.preferredHeight + 4;
            for (int i = 0; i < this.values.length; ++i) {
                if (i == this.selectedIndex) continue;
                int height = this.preferredHeight;
                if ((this.selectedIndex != 0 ? i == 0 : i == 1) || (this.selectedIndex == this.values.length - 1 ? i == this.values.length - 2 : i == this.values.length - 1)) {
                    ++height;
                }
                if (this.mouseY >= this.getY() + offset && this.mouseY <= this.getY() + offset + this.preferredHeight) {
                    RenderUtils.drawRoundedRectangle(this.x, this.y + offset, this.getWidth(), this.preferredHeight, 7f, new Color(70, 70, 70));
                    break;
                }
                offset += height;
            }
        }
        RenderUtils.drawRoundedRectangle(this.x + this.getWidth() - this.preferredHeight, this.y, this.preferredHeight, this.getHeight(), 7f, this.hovered || this.opened ? Window.TERTIARY_FOREGROUND : Window.SECONDARY_FOREGROUND);
        GL11.glPushMatrix();
        if (this.opened) {
            x3 = (double) (this.x + this.getWidth() - this.preferredHeight) + (double) this.preferredHeight / 4.0;
            y3 = (double) this.y + (double) this.preferredHeight * 3.0 / 4.0;
            x2 = (double) (this.x + this.getWidth() - this.preferredHeight) + (double) this.preferredHeight / 2.0;
            y2 = (double) this.y + (double) this.preferredHeight / 4.0;
            x1 = (double) (this.x + this.getWidth() - this.preferredHeight) + (double) this.preferredHeight * 3.0 / 4.0;
            y1 = (double) this.y + (double) this.preferredHeight * 3.0 / 4.0;
        } else {
            x1 = (double) (this.x + this.getWidth() - this.preferredHeight) + (double) this.preferredHeight / 4.0;
            y1 = (double) this.y + (double) this.preferredHeight / 4.0;
            x2 = (double) (this.x + this.getWidth() - this.preferredHeight) + (double) this.preferredHeight / 2.0;
            y2 = (double) this.y + (double) this.preferredHeight * 3.0 / 4.0;
            x3 = (double) (this.x + this.getWidth() - this.preferredHeight) + (double) this.preferredHeight * 3.0 / 4.0;
            y3 = (double) this.y + (double) this.preferredHeight / 4.0;
        }
        if (GuiManager.getINSTANCE().isRainbow()) {
            GLUtil.drawTriangle(x1, y1, x2, y2, x3, y3, GuiManager.getINSTANCE().getRainbowColor());
        } else {
            GLUtil.drawTriangle(x1, y1, x2, y2, x3, y3, Window.FOREGROUND);
        }
        GL11.glPopMatrix();
        String text = this.values[this.selectedIndex];
        GLUtil.getFontRenderer().drawString(text, this.x + 4, (float) this.y + (float) this.preferredHeight / 2.0f - (float) GLUtil.getFontRenderer().getHeight() / 2.0f, Window.FONT.getRGB());
        if (this.opened) {
            int offset = this.preferredHeight + 8;
            for (int i = 0; i < this.values.length; ++i) {
                if (i == this.selectedIndex) continue;
                GLUtil.getFontRenderer().drawString(this.values[i], this.x + 4, (float) (this.y + offset) + (float) GLUtil.getFontRenderer().getHeight() / 2.0f, Window.FONT.getRGB());
                offset += this.preferredHeight;
            }
        }
        if (GuiManager.getINSTANCE().isRainbow()) {
            RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.getWidth(), this.getHeight(), 7f, 1.0f, RenderUtils.GradientDirection.LeftToRight, GuiManager.getINSTANCE().getRainbowColor(), GuiManager.getINSTANCE().getAddRainbowColor());
        } else {
            RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.getWidth(), this.getHeight(), 7f, 1.0f, this.hovered && !this.opened ? Window.TERTIARY_OUTLINE : Window.SECONDARY_OUTLINE);
        }
    }

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        this.updateHovered(x, y, offscreen);
        return false;
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        this.hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + this.getWidth() && y <= this.y + this.preferredHeight;
        this.hoveredExtended = !offscreen && x >= this.x && y >= this.y && x <= this.x + this.getWidth() && y <= this.y + this.getHeight();
        this.mouseX = x;
        this.mouseY = y;
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        this.updateHovered(x, y, offscreen);
        if (button != 0) {
            return false;
        }
        if (this.hovered) {
            this.setOpened(!this.opened);
            this.updateHeight();
            return true;
        }
        if (this.hoveredExtended && this.opened) {
            int offset = this.y + this.preferredHeight + 4;
            for (int i = 0; i < this.values.length; ++i) {
                if (i == this.selectedIndex) continue;
                if (y >= offset && y <= offset + this.preferredHeight) {
                    this.setSelectedChecked(i);
                    this.setOpened(false);
                    break;
                }
                offset += this.preferredHeight;
            }
            this.updateHovered(x, y, offscreen);
            return true;
        }
        return false;
    }

    private void setSelectedChecked(int i) {
        boolean change = true;
        if (this.listener != null) {
            change = this.listener.onValueChange(i);
            UserValueChangeListener.ValueChange();
            this.listener.onValueChange(i);
        }
        if (change) {
            this.selectedIndex = i;
        }
    }
    public void setListener(ValueChangeListener<Integer> listener) {
        this.listener = listener;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
        this.updateHeight();
    }
}

