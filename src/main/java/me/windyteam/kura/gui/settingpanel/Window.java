package me.windyteam.kura.gui.settingpanel;

import me.windyteam.kura.gui.settingpanel.component.components.Pane;
import me.windyteam.kura.gui.settingpanel.utils.GLUtil;
import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.module.modules.client.SettingPanelColor;
import me.windyteam.kura.utils.Rainbow;
import me.windyteam.kura.utils.font.RFontRenderer;
import me.windyteam.kura.utils.gl.RenderUtils;

import java.awt.*;

public class Window {
    public static Color ENABLE = new Color(40, 40, 40);
    public static Color FONT = new Color(255, 255, 255);
    public static Color SECONDARY_FOREGROUND = new Color(30, 30, 30);
    public static Color TERTIARY_FOREGROUND = new Color(20, 20, 20);
    public static Color SECONDARY_OUTLINE = new Color(10, 10, 10);
    public static Color TERTIARY_OUTLINE = new Color(15, 15, 15);
    public static Color BACKGROUND = new Color(20, 20, 20);
    public static Color FOREGROUND = Color.WHITE;
    private final String title;
    private final RFontRenderer RainbowFont = new RFontRenderer(new Font("Consoles", 0, 18), true, false);
    private int y;
    private int x;
    private int width;
    private int height;
    private int headerHeight;
    private boolean beingDragged;
    private int dragX;
    private int dragY;
    private Pane contentPane;
    private Pane SpoilerPane;

    public Window(String title, int x, int y, int width, int height) {
        this.title = title;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    private void updateColor() {
        ENABLE = SettingPanelColor.INSTANCE.Enable.getValue();
        FONT = SettingPanelColor.INSTANCE.Font.getValue();
        SECONDARY_FOREGROUND = SettingPanelColor.INSTANCE.Secondary_Foreground.getValue();
        SECONDARY_OUTLINE = SettingPanelColor.INSTANCE.Secondary_Outline.getValue();
        TERTIARY_FOREGROUND = SettingPanelColor.INSTANCE.Tertiary_Foreground.getValue();
        TERTIARY_OUTLINE = SettingPanelColor.INSTANCE.Tertiary_Outline.getValue();
        BACKGROUND = SettingPanelColor.INSTANCE.Background.getValue();
        FOREGROUND = SettingPanelColor.INSTANCE.Foreground.getValue();
    }

    public void render() {
        int fontHeight = GLUtil.getFontRenderer().getHeight();
        int headerFontOffset = fontHeight / 2;
        this.updateColor();
        GuiManager manager = GuiManager.getINSTANCE();
        this.headerHeight = headerFontOffset * 2 + fontHeight;
        RenderUtils.drawRoundedRectangle(this.x, this.y, this.width, this.height, 15.0, BACKGROUND);
        if (GuiManager.getINSTANCE().isRainbow()) {
            RenderUtils.drawHalfRoundedRectangle(this.x, this.y, this.width, this.headerHeight, 15.0, RenderUtils.HalfRoundedDirection.Top, Rainbow.getRainbowColor(manager.getColorINSTANCE().rainbowSpeed.getValue(), 0.6f, 0.8f));
            this.RainbowFont.drawStringWithShadow(this.title, (float) this.x + (float) this.width / 2.0f - (float) this.RainbowFont.getStringWidth(this.title) / 2.0f, this.y + headerFontOffset, manager.getColorINSTANCE().rainbowSpeed.getValue(), manager.getColorINSTANCE().rainbowSaturation.getValue(), manager.getColorINSTANCE().rainbowBrightness.getValue(), -20L, 255);
        } else {
            RenderUtils.drawHalfRoundedRectangle(this.x, this.y, this.width, this.headerHeight, 15.0, RenderUtils.HalfRoundedDirection.Top, SECONDARY_FOREGROUND);
            RenderUtils.getFontRender().drawStringWithShadow(this.title, (float) this.x + (float) this.width / 2.0f - (float) RenderUtils.getFontRender().getStringWidth(this.title) / 2.0f, this.y + headerFontOffset, -1);
        }
        if (this.contentPane != null) {
            if (this.contentPane.isSizeChanged()) {
                this.contentPane.setSizeChanged(false);
            }
            this.contentPane.setX(this.x);
            this.contentPane.setY(this.y + this.headerHeight + 15);
            this.contentPane.setWidth(125);
            this.contentPane.setHeight(this.height - this.headerHeight);
            this.contentPane.render();
        }
        if (this.SpoilerPane != null) {
            if (this.SpoilerPane.isSizeChanged()) {
                this.SpoilerPane.setSizeChanged(false);
            }
            this.SpoilerPane.setX(this.x + 130);
            this.SpoilerPane.setY(this.y + this.headerHeight);
            this.SpoilerPane.setWidth(this.width - 130);
            this.SpoilerPane.setHeight(this.height - this.headerHeight);
            this.SpoilerPane.render();
        }
    }

    public void mousePressed(int button, int x, int y) {
        if (this.contentPane != null) {
            this.contentPane.mousePressed(button, x, y, false);
        }
        if (this.SpoilerPane != null) {
            this.SpoilerPane.mousePressed(button, x, y, false);
        }
        if (button == 0 && x >= this.x && y >= this.y && x <= this.x + this.width && y <= this.y + this.headerHeight) {
            this.beingDragged = true;
            this.dragX = this.x - x;
            this.dragY = this.y - y;
        }
    }

    private void drag(int mouseX, int mouseY) {
        if (this.beingDragged) {
            this.x = mouseX + this.dragX;
            this.y = mouseY + this.dragY;
        }
    }

    public void mouseReleased(int button, int x, int y) {
        if (this.contentPane != null) {
            this.contentPane.mouseReleased(button, x, y, false);
        }
        if (this.SpoilerPane != null) {
            this.SpoilerPane.mouseReleased(button, x, y, false);
        }
        if (button == 0) {
            this.beingDragged = false;
        }
    }

    public void mouseMoved(int x, int y) {
        if (this.contentPane != null) {
            this.contentPane.mouseMove(x, y, false);
        }
        if (this.SpoilerPane != null) {
            this.SpoilerPane.mouseMove(x, y, false);
        }
        this.drag(x, y);
    }

    public void setContentPane(Pane contentPane) {
        this.contentPane = contentPane;
    }

    public void setSpoilerPane(Pane spoilerPane) {
        this.SpoilerPane = spoilerPane;
    }

    public void keyPressed(int key, char c) {
        if (this.contentPane != null) {
            this.contentPane.keyPressed(key, c);
        }
        if (this.SpoilerPane != null) {
            this.SpoilerPane.keyPressed(key, c);
        }
    }

    public void mouseWheel(int change) {
        if (this.contentPane != null) {
            this.contentPane.mouseWheel(change);
        }
        if (this.SpoilerPane != null) {
            this.SpoilerPane.mouseWheel(change);
        }
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}

