package me.windyteam.kura.gui.settingpanel.component.components;

import me.windyteam.kura.gui.settingpanel.component.AbstractComponent;
import me.windyteam.kura.gui.settingpanel.layout.ILayoutManager;
import me.windyteam.kura.gui.settingpanel.utils.GLUtil;
import me.windyteam.kura.utils.gl.RenderUtils;
import java.awt.Color;

import me.windyteam.kura.utils.gl.RenderUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL41;

public class ScrollPane
extends Pane {
    private static final double SCROLL_AMOUNT = 0.25;
    private int scrollOffset = 0;
    private boolean hovered = false;
    private int realHeight;

    public ScrollPane(ILayoutManager layoutManager) {
        super(layoutManager);
    }

    @Override
    public void updateLayout() {
        this.updateLayout(this.getWidth(), Integer.MAX_VALUE, true);
    }

    @Override
    protected void updateLayout(int width, int height, boolean changeHeight) {
        super.updateLayout(width, height, false);
        this.realHeight = this.layout.getMaxHeight();
        this.validateOffset();
    }

    @Override
    public void render() {
        GL41.glClearDepthf(1.0f);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glColorMask(false, false, false, false);
        GL11.glDepthFunc(513);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);

        GLUtil.drawRect(7, this.x, this.y, this.getWidth(), this.getHeight(), Color.WHITE.getRGB());

        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(true);
        GL11.glDepthFunc(514);

        super.render();

        GL41.glClearDepthf(1.0f);
        GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glClear(1280);
        GL11.glDisable(2929);
        GL11.glDepthFunc(515);
        GL11.glDepthMask(false);

        int maxY = this.realHeight - this.getHeight();
        if (maxY > 0) {
            int sliderHeight = (int)((double)this.getHeight() / (double)this.realHeight * (double)this.getHeight());
            int sliderWidth = 3;
            RenderUtils.drawRoundedRectangle(this.x + this.getWidth() - sliderWidth, (double)this.y + (double)(this.getHeight() - sliderHeight) * ((double)this.scrollOffset / (double)maxY), sliderWidth, sliderHeight, 1.0, Color.white);
        }
    }

    @Override
    protected void updateComponentLocation() {
        for (AbstractComponent component : this.components) {
            int[] ints = this.componentLocations.get(component);
            if (ints == null) {
                this.updateLayout();
                this.updateComponentLocation();
                return;
            }
            component.setX(this.x + ints[0]);
            component.setY(this.y + ints[1] - this.scrollOffset);
        }
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        this.hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + this.getWidth() && y <= this.y + this.getHeight();
    }

    @Override
    public boolean mouseWheel(int change) {
        this.scrollOffset = (int)((double)this.scrollOffset - (double)change * 0.25);
        this.validateOffset();
        return super.mouseWheel(change);
    }

    private void validateOffset() {
        if (this.scrollOffset > this.realHeight - this.getHeight()) {
            this.scrollOffset = this.realHeight - this.getHeight();
        }
        if (this.scrollOffset < 0) {
            this.scrollOffset = 0;
        }
    }

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        this.updateHovered(x, y, offscreen);
        return super.mouseMove(x, y, offscreen || x < this.x || y < this.y || x > this.x + this.getWidth() || y > this.y + this.getHeight());
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        return super.mousePressed(button, x, y, offscreen || x < this.x || y < this.y || x > this.x + this.getWidth() || y > this.y + this.getHeight());
    }

    @Override
    public boolean mouseReleased(int button, int x, int y, boolean offscreen) {
        return super.mouseReleased(button, x, y, offscreen || x < this.x || y < this.y || x > this.x + this.getWidth() || y > this.y + this.getHeight());
    }

    @Override
    public void addComponent(AbstractComponent component) {
        super.addComponent(component);
    }
}

