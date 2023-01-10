package me.windyteam.kura.gui.window.components;

import me.windyteam.kura.gui.settingpanel.Window;
import me.windyteam.kura.gui.settingpanel.component.AbstractComponent;
import me.windyteam.kura.gui.settingpanel.component.ActionEventListener;
import me.windyteam.kura.gui.settingpanel.utils.GLUtil;
import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.utils.MathUtil;
import me.windyteam.kura.utils.Timer;
import me.windyteam.kura.utils.gl.RenderUtils;
import me.windyteam.kura.manager.GuiManager;
import net.minecraft.client.Minecraft;

public class ButtonAlt extends AbstractComponent {
    private final Timer timer = new Timer().reset();
    private final int preferredWidth;
    private final int preferredHeight;
    private String title;
    private boolean hovered;
    private ActionEventListener listener;

    public ButtonAlt(String title, int preferredWidth, int preferredHeight) {
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
        this.setWidth(preferredWidth);
        this.setHeight(preferredHeight);
        this.setTitle(title);
        this.timer.reset();
    }

    public void reset() {
        this.timer.reset();
    }

    @Override
    public void render() {
        double offset = !this.timer.passed(700L) ? MathUtil.calculateDoubleChange(60 + this.getWidth(), 0, 700, (int) this.timer.getPassedTimeMs()) : 0.0;
        RenderUtils.drawRoundedRectangle((-offset) + this.x, this.y, this.getWidth(), this.getHeight(), 7.0, this.hovered ? Window.SECONDARY_FOREGROUND : Window.TERTIARY_FOREGROUND);
        RenderUtils.drawRoundedRectangleOutline((float) ((-offset) + this.x), this.y, this.getWidth(), this.getHeight(), 7.0f, 1.0f, RenderUtils.GradientDirection.LeftToRight, GuiManager.getINSTANCE().getRainbowColor(), GuiManager.getINSTANCE().getAddRainbowColor());
        Minecraft.getMinecraft().fontRenderer.drawString(this.title, (int) ((-offset) + this.x + this.getWidth() / 2.0f - GLUtil.getFontRenderer().getStringWidth(this.title) / 2.0f), (int) (this.y + this.getHeight() / 2.0f - GLUtil.getFontRenderer().getHeight() / 2.0), Window.FONT.getRGB());
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
