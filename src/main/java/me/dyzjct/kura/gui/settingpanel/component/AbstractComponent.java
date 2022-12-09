package me.dyzjct.kura.gui.settingpanel.component;

public abstract class AbstractComponent {
    protected int x;
    protected int y;
    private int width;
    private int height;
    private boolean sizeChanged;

    public abstract void render();

    public int getEventPriority() {
        return 0;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        if (this.x != x) {
            this.setSizeChanged(true);
        }
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        if (this.y != y) {
            this.setSizeChanged(true);
        }
        this.y = y;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        if (this.width != width) {
            this.setSizeChanged(true);
        }
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        if (this.height != height) {
            this.setSizeChanged(true);
        }
        this.height = height;
    }

    public boolean isSizeChanged() {
        return this.sizeChanged;
    }

    public void setSizeChanged(boolean sizeChanged) {
        this.sizeChanged = sizeChanged;
    }

    public boolean keyPressed(int key, char c) {
        return false;
    }

    public boolean mouseReleased(int button, int x, int y, boolean offscreen) {
        return false;
    }

    public boolean mouseMove(int x, int y, boolean offscreen) {
        return false;
    }

    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        return false;
    }

    public boolean mouseWheel(int change) {
        return false;
    }
}

