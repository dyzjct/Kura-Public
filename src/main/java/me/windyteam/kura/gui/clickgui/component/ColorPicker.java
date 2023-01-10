package me.windyteam.kura.gui.clickgui.component;

import me.windyteam.kura.gui.clickgui.Panel;
import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.setting.ColorSetting;
import me.windyteam.kura.utils.color.ColorUtils;
import me.windyteam.kura.utils.font.CFontRenderer;
import me.windyteam.kura.utils.render.RenderUtils;
import java.awt.Color;

import me.windyteam.kura.manager.GuiManager;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;

public class ColorPicker
extends SettingButton<Color> {
    private final double radius;
    private double selectedX;
    private double selectedY;
    private float brightness = 1.0f;
    private int colorPanelX;
    private int colorPanelY;
    private boolean extend = false;
    private final int headerHeight;
    private final int extendHeight;

    public ColorPicker(ColorSetting colorSetting, Panel father, int width, int NormalHeight, int ExtendHeight) {
        this.setValue(colorSetting);
        this.father = father;
        this.width = width;
        this.height = NormalHeight;
        this.radius = (double)width / 2.0 / 5.0 * 2.0;
        this.headerHeight = NormalHeight;
        this.extendHeight = ExtendHeight;
        this.colorPanelX = (int)((double)this.x + this.radius + 15.0);
        this.colorPanelY = this.y + this.headerHeight + this.extendHeight / 2;
        this.setColor((Color)this.getValue().getValue());
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        CFontRenderer font = GuiManager.getINSTANCE().getFont();
        this.setColor((Color)this.getValue().getValue());
        Gui.drawRect((int)this.x, (int)this.y, (int)(this.x + this.width), (int)(this.y + this.height), (int)-2063597568);
        int colorGM = GuiManager.getINSTANCE().isRainbow() ? GuiManager.getINSTANCE().getRainbowColorAdd((long)this.add) : GuiManager.getINSTANCE().getRGB();
        font.drawString(this.getValue().getName(), this.x + 3, (int)((float)(this.y + this.headerHeight / 2) - (float)font.getHeight() / 2.0f) + 2, ColorUtils.getHoovered(colorGM, this.isHovered(this.x, this.y, this.width, this.headerHeight, mouseX, mouseY)));
        RenderUtils.drawRect(this.x + this.width - (this.headerHeight + 3), this.y + (this.headerHeight - (this.headerHeight - 2)), this.headerHeight - 4, this.headerHeight - 4, this.getColor());
        if (this.extend) {
            if (Mouse.isButtonDown((int)0) && this.isPointInColor(this.colorPanelX, this.colorPanelY, this.radius, mouseX, mouseY)) {
                this.selectedX = mouseX - this.colorPanelX;
                this.selectedY = mouseY - this.colorPanelY;
            } else if (Mouse.isButtonDown((int)0) && this.isHovered(this.x + this.width / 16 * 7 + 5, this.y + 2, this.width / 6, this.height - 4, mouseX, mouseY)) {
                float az = this.y + this.headerHeight + 2;
                float za = this.extendHeight - 4;
                this.setBrightness(Math.max(Math.min(1.0f - ((float)mouseY - az) / za, 1.0f), 0.0f));
            }
            if (((Color)this.getValue().getValue()).getRGB() != this.getColor().getRGB()) {
                this.getValue().setValue(this.getColor());
            }
            this.height = this.headerHeight + this.extendHeight;
            this.colorPanelX = (int)((double)this.x + this.radius + 3.0);
            this.colorPanelY = this.y + this.headerHeight + this.extendHeight / 2;
            Color color = (Color)this.getValue().getValue();
            RenderUtils.drawColoredCircle(this.colorPanelX, this.colorPanelY, this.radius, 1.0f, this.brightness);
            RenderUtils.drawCircle((double)this.colorPanelX + this.selectedX, (double)this.colorPanelY + this.selectedY, 3.0, new Color(-15592942));
            RenderUtils.drawCircle((double)this.colorPanelX + this.selectedX, (double)this.colorPanelY + this.selectedY, 3.0, new Color(-1));
            RenderUtils.drawGradientRect((double)this.x + this.radius + this.radius + 5.0 + 6.0, this.y + this.headerHeight + 2, (float)this.width / 8.0f, this.extendHeight - 4, RenderUtils.GradientDirection.DownToUp, ColorUtils.brightness(this.getColor(), 1.0f), ColorUtils.brightness(this.getColor(), 0.0f));
            double AX = (double)this.x + this.radius + this.radius + 5.0;
            double AY = (float)(this.y + this.headerHeight + 2) + ((float)(this.extendHeight - 4) - (float)(this.extendHeight - 4) * this.brightness);
            RenderUtils.drawTriangle(AX, AY + 3.0, AX + 3.0, AY, AX, AY - 3.0, color);
            font.drawString("R: " + color.getRed(), (float)this.x + (float)this.width / 16.0f * 9.0f + 10.0f, this.y + this.headerHeight + font.getHeight() + 5, -1);
            font.drawString("G: " + color.getGreen(), (float)this.x + (float)this.width / 16.0f * 9.0f + 10.0f, this.y + this.headerHeight + (font.getHeight() + 5) * 2, -1);
            font.drawString("B: " + color.getBlue(), (float)this.x + (float)this.width / 16.0f * 9.0f + 10.0f, this.y + this.headerHeight + (font.getHeight() + 5) * 3, -1);
        } else {
            this.height = this.headerHeight;
        }
    }

    private float getNormalized() {
        return (float)((-Math.toDegrees(Math.atan2(this.selectedY, this.selectedX)) + 450.0) % 360.0) / 360.0f;
    }

    private Color getColor() {
        return new Color(Color.HSBtoRGB(this.getNormalized(), (float)(Math.hypot(this.selectedX, this.selectedY) / this.radius), this.brightness));
    }

    private void setColor(Color selectedColor) {
        float[] hsb = Color.RGBtoHSB(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue(), null);
        this.selectedX = (double)hsb[1] * this.radius * (Math.sin(Math.toRadians(hsb[0] * 360.0f)) / Math.sin(Math.toRadians(90.0)));
        this.selectedY = (double)hsb[1] * this.radius * (Math.sin(Math.toRadians(90.0f - hsb[0] * 360.0f)) / Math.sin(Math.toRadians(90.0)));
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
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovered(this.x, this.y, this.width, this.headerHeight, mouseX, mouseY)) {
            this.extend = !this.extend;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
    }
}

