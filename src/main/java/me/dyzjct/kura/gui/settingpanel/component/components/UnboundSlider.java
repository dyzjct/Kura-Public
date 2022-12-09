package me.dyzjct.kura.gui.settingpanel.component.components;

import me.dyzjct.kura.gui.settingpanel.Window;
import me.dyzjct.kura.gui.settingpanel.component.AbstractComponent;
import me.dyzjct.kura.gui.settingpanel.component.ValueChangeListener;
import me.dyzjct.kura.gui.settingpanel.utils.GLUtil;
import me.dyzjct.kura.manager.GuiManager;
import me.dyzjct.kura.utils.gl.RenderUtils;

public class UnboundSlider
        extends AbstractComponent {
    private static final int PREFERRED_WIDTH = 180;
    private static final int PREFERRED_HEIGHT = 24;
    public int sensitivity = 5;
    double value;
    int originX;
    double originValue;
    boolean integer;
    double max = Double.MAX_VALUE;
    double min = Double.MIN_VALUE;
    private boolean hovered;
    private ValueChangeListener<Number> listener;
    private boolean isDrag;

    public UnboundSlider(double value, boolean integer) {
        this.value = value;
        this.integer = integer;
        this.setHeight(24);
        this.setWidth(180);
    }

    @Override
    public void render() {
        if (GuiManager.getINSTANCE().isRainbow()) {
            RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.getWidth(), this.getHeight(), 7f, 1.0f, RenderUtils.GradientDirection.LeftToRight, GuiManager.getINSTANCE().getRainbowColor(), GuiManager.getINSTANCE().getAddRainbowColor());
        } else {
            RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.getWidth(), this.getHeight(), 7f, 1.0f, this.hovered ? Window.SECONDARY_OUTLINE : Window.SECONDARY_FOREGROUND);
        }
        GLUtil.drawRect(7, this.x, this.y, this.getWidth(), this.getHeight(), this.hovered ? Window.SECONDARY_FOREGROUND.getRGB() : Window.TERTIARY_FOREGROUND.getRGB());
        GLUtil.getFontRenderer().drawString(String.valueOf(this.value), (float) this.x + (float) this.getWidth() / 2.0f - (float) GLUtil.getFontRenderer().getStringWidth(String.valueOf(this.value)) / 2.0f, (float) this.y + (float) this.getHeight() / 2.0f - (float) GLUtil.getFontRenderer().getHeight() / 2.0f, Window.FOREGROUND.getRGB());
    }

    private void updateValue(int x, int y) {
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        this.originX = x;
        this.originValue = this.getValue();
        if (button == 0 && x >= this.x && y >= this.y && x <= this.x + this.getWidth() && y <= this.y + this.getHeight()) {
            this.isDrag = true;
            return true;
        }
        return false;
    }

    public void mouseDrag(int x, int y, boolean offscreen) {
        int diff = (this.originX - x) / this.sensitivity;
        double newValue = Math.floor((this.originValue - (double) diff * (this.originValue == 0.0 ? 1.0 : Math.abs(this.originValue) / 10.0)) * 10.0) / 10.0;
        boolean change = true;
        if (this.originValue != newValue && this.listener != null) {
            change = this.listener.onValueChange(newValue);
        }
        if (change) {
            this.value = newValue;
        }
    }

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        this.updateHovered(x, y, offscreen);
        if (this.isDrag) {
            this.mouseDrag(x, y, offscreen);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(int button, int x, int y, boolean offscreen) {
        if (button == 0 && this.isDrag) {
            this.isDrag = false;
            return true;
        }
        return false;
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        this.hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + this.getWidth() && y <= this.y + this.getHeight();
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setListener(ValueChangeListener<Number> listener) {
        this.listener = listener;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = this.integer ? Math.floor(value) : value;
    }
}

