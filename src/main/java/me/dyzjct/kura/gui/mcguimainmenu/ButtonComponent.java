package me.dyzjct.kura.gui.mcguimainmenu;

import me.dyzjct.kura.utils.font.CFontRenderer;
import me.dyzjct.kura.utils.gl.RenderUtils;

import java.awt.*;

public abstract class ButtonComponent {
    public float width;
    public float height;
    public float x;
    public float y;
    public CFontRenderer font = RenderUtils.getFontRender();
    protected String title;
    protected boolean hovered = false;
    protected Point pressPoint;
    protected int pointAlpha = 0;
    private onClickListener listener;

    public ButtonComponent(String title, float x, float y, float width, float height) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    public void render() {
    }

    public void mouseMove(int mouseX, int mouseY) {
        this.hovered = this.isHovered(mouseX, mouseY);
    }

    public void mouseclick(int mouseX, int mouseY, int button) {
        if (button == 0 && this.isHovered(mouseX, mouseY)) {
            if (this.listener != null) {
                this.listener.onClickListener();
            }
            this.pressPoint = new Point(mouseX, mouseY);
            this.pointAlpha = 255;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ButtonComponent setOnClickListener(onClickListener listener) {
        this.listener = listener;
        return this;
    }

    protected boolean isHovered(int mouseX, int mouseY) {
        return (float) mouseX >= Math.min(this.x, this.x + this.width) && (float) mouseX <= Math.max(this.x, this.x + this.width) && (float) mouseY >= Math.min(this.y, this.y + this.height) && (float) mouseY <= Math.max(this.y, this.y + this.height);
    }

    protected interface onClickListener {
        void onClickListener();
    }
}

