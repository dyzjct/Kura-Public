package me.dyzjct.kura.gui.settingpanel.component.components;

import me.dyzjct.kura.gui.settingpanel.Window;
import me.dyzjct.kura.gui.settingpanel.component.AbstractComponent;
import me.dyzjct.kura.gui.settingpanel.component.ValueChangeListener;
import me.dyzjct.kura.gui.settingpanel.utils.GLUtil;
import me.dyzjct.kura.gui.settingpanel.utils.Utils;
import me.dyzjct.kura.manager.GuiManager;
import me.dyzjct.kura.utils.gl.RenderUtils;

import java.util.Locale;
import java.util.function.Function;

public class Slider
        extends AbstractComponent {
    private static final int PREFERRED_WIDTH = 180;
    private static final int PREFERRED_HEIGHT = 24;
    private final int preferredWidth;
    private final int preferredHeight;
    private boolean hovered;
    private double value;
    private final double minValue;
    private final double maxValue;
    private final NumberType numberType;
    private ValueChangeListener<Number> listener;
    private boolean changing = false;

    public Slider(double value, double minValue, double maxValue, NumberType numberType, int preferredWidth, int preferredHeight) {
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.numberType = numberType;
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
        this.setWidth(preferredWidth);
        this.setHeight(preferredHeight);
    }

    public Slider(double value, double minValue, double maxValue, NumberType numberType) {
        this(value, minValue, maxValue, numberType, 180, 24);
    }

    @Override
    public void render() {
        if (GuiManager.getINSTANCE().isRainbow()) {
            RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.getWidth(), this.getHeight(), 4f, 1.0f, RenderUtils.GradientDirection.LeftToRight, GuiManager.getINSTANCE().getRainbowColor(), GuiManager.getINSTANCE().getAddRainbowColor());
        } else {
            RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.getWidth(), this.getHeight(), 4f, 1.0f, this.hovered || this.changing ? Window.SECONDARY_OUTLINE : Window.TERTIARY_OUTLINE);
        }
        int sliderWidth = 4;
        double sliderPos = (this.value - this.minValue) / (this.maxValue - this.minValue) * (double) (this.getWidth() - sliderWidth);
        if (GuiManager.getINSTANCE().isRainbow()) {
            RenderUtils.drawRoundedRectangle((double) this.x + sliderPos, this.y + 2, sliderWidth, this.getHeight() - 3, 2.0, GuiManager.getINSTANCE().getRainbowColor());
        } else {
            RenderUtils.drawRoundedRectangle((double) this.x + sliderPos, this.y + 2, sliderWidth, this.getHeight() - 3, 2.0, this.hovered || this.changing ? Window.TERTIARY_FOREGROUND : Window.SECONDARY_FOREGROUND);
        }
        String text = this.numberType.getFormatter().apply(this.value);
        GLUtil.getFontRenderer().drawString(text, (float) this.x + (float) this.getWidth() / 2.0f - (float) GLUtil.getFontRenderer().getStringWidth(text) / 2.0f, (float) this.y + (float) this.getHeight() / 2.0f - (float) GLUtil.getFontRenderer().getHeight() / 4.0f, Window.FONT.getRGB());
    }

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        this.updateHovered(x, y, offscreen);
        this.updateValue(x, y);
        return this.changing;
    }

    private void updateValue(int x, int y) {
        if (this.changing) {
            double oldValue = this.value;
            double newValue = Math.max(Math.min((double) (x - this.x) / (double) this.getWidth() * (this.maxValue - this.minValue) + this.minValue, this.maxValue), this.minValue);
            boolean change = true;
            if (oldValue != newValue && this.listener != null) {
                change = this.listener.onValueChange(newValue);
            }
            if (change) {
                this.value = newValue;
            }
        }
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        this.hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + this.getWidth() && y <= this.y + this.getHeight();
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        if (button == 0) {
            this.updateHovered(x, y, offscreen);
            if (this.hovered) {
                this.changing = true;
                this.updateValue(x, y);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(int button, int x, int y, boolean offscreen) {
        if (button == 0) {
            this.updateHovered(x, y, offscreen);
            if (this.changing) {
                this.changing = false;
                this.updateValue(x, y);
                return true;
            }
        }
        return false;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setListener(ValueChangeListener<Number> listener) {
        this.listener = listener;
    }

    public enum NumberType {
        PERCENT(number -> String.format(Locale.ENGLISH, "%.1f%%", Float.valueOf(number.floatValue()))),
        TIME(number -> Utils.formatTime(number.longValue())),
        DECIMAL(number -> String.format(Locale.ENGLISH, "%.4f", Float.valueOf(number.floatValue()))),
        INTEGER(number -> Long.toString(number.longValue()));

        private final Function<Number, String> formatter;

        NumberType(Function<Number, String> formatter) {
            this.formatter = formatter;
        }

        public Function<Number, String> getFormatter() {
            return this.formatter;
        }
    }
}

