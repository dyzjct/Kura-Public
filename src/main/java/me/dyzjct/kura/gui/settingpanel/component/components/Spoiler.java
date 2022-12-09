package me.dyzjct.kura.gui.settingpanel.component.components;

import me.dyzjct.kura.gui.settingpanel.Window;
import me.dyzjct.kura.gui.settingpanel.component.AbstractComponent;
import me.dyzjct.kura.gui.settingpanel.component.ActionEventListener;
import me.dyzjct.kura.gui.settingpanel.utils.GLUtil;
import me.dyzjct.kura.manager.GuiManager;
import me.dyzjct.kura.module.IModule;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.utils.gl.RenderUtils;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class Spoiler
        extends AbstractComponent {
    private static final int PREFERRED_HEIGHT = 28;
    public int preferredWidth = 614;
    public int preferredHeight;
    public int lastmousex = 0;
    public int lastmousey = 0;
    private String title;
    private boolean hovered;
    private ActionEventListener listener;
    private Pane contentPane;
    private boolean opened = false;

    public Spoiler(String title, int preferredWidth, int preferredHeight, Pane contentPane) {
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
        this.contentPane = contentPane;
        this.setTitle(title);
    }

    public Spoiler(String title, int preferredWidth, Pane contentPane) {
        this(title, preferredWidth, 28, contentPane);
    }

    public IModule getModule() {
        return ModuleManager.getModuleByName(this.getTitle());
    }

    @Override
    public void render() {
        if (this.getModule().isEnabled()) {
            RenderUtils.drawRoundedRectangle(this.x, this.y, this.getWidth(), this.preferredHeight, 7f, Window.ENABLE);
        } else if (this.hovered) {
            RenderUtils.drawRoundedRectangle(this.x, this.y, this.getWidth(), this.preferredHeight, 7f, Window.SECONDARY_FOREGROUND);
        }
        if (this.opened) {
            this.updateBounds();
            this.contentPane.setX(this.getX());
            this.contentPane.setY(this.getY() + this.preferredHeight);
            this.contentPane.render();
            if (GuiManager.getINSTANCE().isRainbow()) {
                RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.getWidth(), this.preferredHeight, 7f, 1.0f, RenderUtils.GradientDirection.LeftToRight, GuiManager.getINSTANCE().getRainbowColor(), GuiManager.getINSTANCE().getAddRainbowColor());
                RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.getWidth(), this.getHeight(), 7f, 1.0f, RenderUtils.GradientDirection.LeftToRight, GuiManager.getINSTANCE().getRainbowColor(), GuiManager.getINSTANCE().getAddRainbowColor());
            } else {
                RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.getWidth(), this.preferredHeight, 7f, 1.0f, Window.SECONDARY_OUTLINE);
                RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.getWidth(), this.getHeight(), 7f, 1.0f, Window.SECONDARY_OUTLINE);
            }
        } else if (GuiManager.getINSTANCE().isRainbow()) {
            RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.getWidth(), this.preferredHeight, 7f, 1.0f, RenderUtils.GradientDirection.LeftToRight, GuiManager.getINSTANCE().getRainbowColor(), GuiManager.getINSTANCE().getAddRainbowColor());
        } else {
            RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.getWidth(), this.preferredHeight, 7f, 1.0f, Window.SECONDARY_OUTLINE);
        }
        GLUtil.getFontRenderer().drawString(this.title, (float) this.x + (float) this.getWidth() / 2.0f - (float) GLUtil.getFontRenderer().getStringWidth(this.title) / 2.0f, (float) this.y + (float) this.preferredHeight / 2.0f - (float) GLUtil.getFontRenderer().getHeight() / 2.0f, Window.FONT.getRGB());
        if (this.hovered && !this.getModule().description.equals("")) {
            float widthD = GLUtil.getFontRenderer().getStringWidth(this.getModule().description) + 15;
            float heightD = GLUtil.getFontRenderer().getHeight() + 5;
            float xd = this.lastmousex + 7;
            float yd = (float) this.lastmousey - heightD / 2.0f;
            RenderUtils.drawRoundedRectangle(xd, yd, widthD, heightD, 2.0, new Color(Window.FOREGROUND.getRed(), Window.FOREGROUND.getGreen(), Window.FOREGROUND.getBlue(), 191));
            GLUtil.getFontRenderer().drawCenteredString(this.getModule().description, xd + widthD / 2.0f, yd + heightD / 2.0f, Window.FONT.getRGB());
        }
    }

    public Pane getContentPane() {
        return this.contentPane;
    }

    public void setContentPane(Pane newContentPane) {
        this.contentPane = newContentPane;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        this.lastmousey = y;
        this.lastmousex = x;
        this.updateHovered(x, y, offscreen);
        return this.opened && this.contentPane.mouseMove(x, y, offscreen);
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        this.hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + this.getWidth() && y <= this.y + this.preferredHeight;
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        if (button == 0) {
            if (x >= this.x && y >= this.y && x <= this.x + this.getWidth() && y <= this.y + this.preferredHeight) {
                this.updateHovered(x, y, offscreen);
                if (Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().world != null) {
                    ModuleManager.getModuleByName(this.getTitle()).toggle();
                }
                return false;
            }
        } else if (button == 1) {
            this.updateHovered(x, y, offscreen);
            if (this.hovered) {
                this.opened = !this.opened;
                this.contentPane.updateLayout();
                this.updateBounds();
                return true;
            }
        }
        return this.opened && this.contentPane.mousePressed(button, x, y, offscreen);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updateBounds();
    }

    private void updateBounds() {
        this.setWidth(Math.max(Math.max(GLUtil.getFontRenderer().getStringWidth(this.getTitle()), this.contentPane.getHeight()), this.preferredWidth));
        this.setHeight(Math.max(GLUtil.getFontRenderer().getHeight() * 5 / 4, this.preferredHeight) + (this.opened ? this.contentPane.getHeight() : 0));
    }

    public void setListener(ActionEventListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean mouseReleased(int button, int x, int y, boolean offscreen) {
        return this.opened && this.contentPane.mouseReleased(button, x, y, offscreen);
    }

    @Override
    public boolean mouseWheel(int change) {
        return this.opened && this.contentPane.mouseWheel(change);
    }

    @Override
    public boolean keyPressed(int key, char c) {
        return this.opened && this.contentPane.keyPressed(key, c);
    }
}

