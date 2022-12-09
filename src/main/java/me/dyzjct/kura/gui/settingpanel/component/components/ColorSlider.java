package me.dyzjct.kura.gui.settingpanel.component.components;

import me.dyzjct.kura.gui.settingpanel.Window;
import me.dyzjct.kura.gui.settingpanel.component.AbstractComponent;
import me.dyzjct.kura.gui.settingpanel.utils.GLUtil;
import me.dyzjct.kura.setting.ColorSetting;
import me.dyzjct.kura.utils.color.ColorUtils;
import me.dyzjct.kura.utils.gl.RenderUtils;

import java.awt.*;

public class ColorSlider
        extends AbstractComponent {
    private static final int PREFERRED_WIDTH = 180;
    private static final int PREFERRED_HEIGHT = 22;
    private final double radius;
    private final int headerHeight;
    private final int extendHeight;
    private final ColorSetting containSetting;
    private double selectedX;
    private double selectedY;
    private float brightness = 1.0f;
    private int colorPanelX;
    private int colorPanelY;
    private boolean extend = false;
    private boolean hovered;
    private boolean colorPanelChanging = false;
    private boolean brightnessChanging = false;

    public ColorSlider(ColorSetting colorSetting) {
        this(colorSetting, 180, 22);
    }

    public ColorSlider(ColorSetting colorSetting, int width, int NormalHeight) {
        this.containSetting = colorSetting;
        this.setWidth(width);
        this.setHeight(NormalHeight);
        this.radius = (double) width / 2.0 / 5.0 * 2.0;
        this.headerHeight = NormalHeight;
        this.extendHeight = 90;
        this.colorPanelX = (int) ((double) this.x + this.radius + 15.0);
        this.colorPanelY = this.y + this.headerHeight + this.extendHeight / 2;
    }

    @Override
    public void render() {
        this.setColor(this.containSetting.getValue());
        RenderUtils.drawRoundedRectangle(this.x, this.y, this.getWidth(), this.getHeight(), 7f, this.hovered ? Window.TERTIARY_FOREGROUND : Window.SECONDARY_FOREGROUND);
        RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.getWidth(), this.getHeight(), 7f, 1.0f, this.hovered ? Window.TERTIARY_OUTLINE : Window.SECONDARY_OUTLINE);
        GLUtil.getFontRenderer().drawString(this.containSetting.getName(), this.x + 3, (int) ((float) (this.y + this.headerHeight / 2) - (float) GLUtil.getFontRenderer().getHeight() / 2.0f) + 2, Window.FONT.getRGB());
        RenderUtils.drawRect(this.x + this.getWidth() - (this.headerHeight + 5), this.y + (this.headerHeight - (this.headerHeight - 2)), this.headerHeight - 4, this.headerHeight - 4, this.containSetting.getValue());
        if (this.extend) {
            this.setHeight(this.headerHeight + this.extendHeight);
            this.colorPanelX = (int) ((double) this.x + this.radius + 3.0);
            this.colorPanelY = this.y + this.headerHeight + this.extendHeight / 2;
            Color color = this.containSetting.getValue();
            RenderUtils.drawColoredCircle(this.colorPanelX, this.colorPanelY, this.radius, 1.0f, this.brightness);
            RenderUtils.drawCircle((double) this.colorPanelX + this.selectedX, (double) this.colorPanelY + this.selectedY, 3.0, new Color(-15592942));
            RenderUtils.drawCircle((double) this.colorPanelX + this.selectedX, (double) this.colorPanelY + this.selectedY, 3.0, new Color(-1));
            RenderUtils.drawGradientRect((double) this.x + this.radius + this.radius + 5.0 + 6.0, this.y + this.headerHeight + 2, (float) this.getWidth() / 8.0f, this.extendHeight - 4, RenderUtils.GradientDirection.DownToUp, ColorUtils.brightness(this.getColor(), 1.0f), ColorUtils.brightness(this.getColor(), 0.0f));
            double AX = (double) this.x + this.radius + this.radius + 5.0;
            double AY = (float) (this.y + this.headerHeight + 2) + ((float) (this.extendHeight - 4) - (float) (this.extendHeight - 4) * this.brightness);
            RenderUtils.drawTriangle(AX, AY + 3.0, AX + 3.0, AY, AX, AY - 3.0, color);
            GLUtil.getFontRenderer().drawString("R: " + color.getRed(), (float) this.x + (float) this.getWidth() / 16.0f * 9.0f + 10.0f, this.y + this.headerHeight + GLUtil.getFontRenderer().getHeight() + 5, -1);
            GLUtil.getFontRenderer().drawString("G: " + color.getGreen(), (float) this.x + (float) this.getWidth() / 16.0f * 9.0f + 10.0f, this.y + this.headerHeight + (GLUtil.getFontRenderer().getHeight() + 5) * 2, -1);
            GLUtil.getFontRenderer().drawString("B: " + color.getBlue(), (float) this.x + (float) this.getWidth() / 16.0f * 9.0f + 10.0f, this.y + this.headerHeight + (GLUtil.getFontRenderer().getHeight() + 5) * 3, -1);
        } else {
            this.setHeight(this.headerHeight);
        }
    }

    private float getNormalized() {
        return (float) ((-Math.toDegrees(Math.atan2(this.selectedY, this.selectedX)) + 450.0) % 360.0) / 360.0f;
    }

    private Color getColor() {
        return new Color(Color.HSBtoRGB(this.getNormalized(), (float) (Math.hypot(this.selectedX, this.selectedY) / this.radius), this.brightness));
    }

    public void setColor(Color selectedColor) {
        float[] hsb = Color.RGBtoHSB(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue(), null);
        this.selectedX = (double) hsb[1] * this.radius * (Math.sin(Math.toRadians(hsb[0] * 360.0f)) / Math.sin(Math.toRadians(90.0)));
        this.selectedY = (double) hsb[1] * this.radius * (Math.sin(Math.toRadians(90.0f - hsb[0] * 360.0f)) / Math.sin(Math.toRadians(90.0)));
        this.setBrightness(hsb[2]);
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    protected boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= Math.min(x, x + width) && mouseX <= Math.max(x, x + width) && mouseY >= Math.min(y, y + height) && mouseY <= Math.max(y, y + height);
    }

    private boolean isPointInColor(double x, double y, double radius, double pX, double pY) {
        return (pX - x) * (pX - x) + (pY - y) * (pY - y) <= radius * radius;
    }

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        this.updateHovered(x, y, offscreen);
        this.updateValue(x, y);
        return this.brightnessChanging || this.colorPanelChanging;
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        this.hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + this.getWidth() && y <= this.y + this.getHeight();
    }

    private void updateValue(int Mx, int My) {
        if (this.extend) {
            if (this.colorPanelChanging && this.isPointInColor(this.colorPanelX, this.colorPanelY, this.radius, Mx, My)) {
                this.selectedX = Mx - this.colorPanelX;
                this.selectedY = My - this.colorPanelY;
            } else if (this.brightnessChanging && this.isHovered(this.x + this.getWidth() / 16 * 7 + 5, this.y + 2, this.getWidth() / 6, this.getHeight() - 4, Mx, My)) {
                float az = this.y + this.headerHeight + 2;
                float za = this.extendHeight - 4;
                this.setBrightness(Math.max(Math.min(1.0f - ((float) My - az) / za, 1.0f), 0.0f));
            }
        }
        if ((this.colorPanelChanging || this.brightnessChanging) && this.containSetting.getValue().getRGB() != this.getColor().getRGB()) {
            this.containSetting.setValue(this.getColor());
        }
    }

    @Override
    public boolean mousePressed(int button, int Mx, int My, boolean offscreen) {
        if (button == 0 && this.isHovered(this.x, this.y, this.getWidth(), this.headerHeight, Mx, My)) {
            this.extend = !this.extend;
            return true;
        }
        if (button == 0) {
            this.updateHovered(Mx, My, offscreen);
            if (this.extend) {
                if (this.isPointInColor(this.colorPanelX, this.colorPanelY, this.radius, Mx, My)) {
                    this.colorPanelChanging = true;
                    this.updateValue(Mx, My);
                    return true;
                }
                if (this.isHovered(this.x + this.getWidth() / 16 * 7 + 5, this.y + 2, this.getWidth() / 6, this.getHeight() - 4, Mx, My)) {
                    this.brightnessChanging = true;
                    this.updateValue(Mx, My);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(int button, int x, int y, boolean offscreen) {
        if (button == 0) {
            this.updateHovered(x, y, offscreen);
            if (this.colorPanelChanging) {
                this.colorPanelChanging = false;
                this.updateValue(x, y);
                return true;
            }
            if (this.brightnessChanging) {
                this.brightnessChanging = false;
                this.updateValue(x, y);
                return true;
            }
        }
        return false;
    }
}

