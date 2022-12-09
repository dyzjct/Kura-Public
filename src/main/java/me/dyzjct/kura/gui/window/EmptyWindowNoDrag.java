package me.dyzjct.kura.gui.window;

import me.dyzjct.kura.gui.settingpanel.component.components.Pane;

public class EmptyWindowNoDrag {
    private int y;
    private int x;
    private int width;
    private int height;
    private Pane contentPane;

    public EmptyWindowNoDrag(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render() {
        if (this.contentPane != null) {
            if (this.contentPane.isSizeChanged()) {
                this.contentPane.setSizeChanged(false);
            }
            this.contentPane.setX(this.x);
            this.contentPane.setY(this.y);
            this.contentPane.setWidth(this.width);
            this.contentPane.setHeight(this.height);
            this.contentPane.render();
        }
    }

    public void mousePressed(int button, int x, int y) {
        if (this.contentPane != null) {
            this.contentPane.mousePressed(button, x, y, false);
        }
    }

    public void mouseReleased(int button, int x, int y) {
        if (this.contentPane != null) {
            this.contentPane.mouseReleased(button, x, y, false);
        }
    }

    public void mouseMoved(int x, int y) {
        if (this.contentPane != null) {
            this.contentPane.mouseMove(x, y, false);
        }
    }

    public void setContentPane(Pane contentPane) {
        this.contentPane = contentPane;
    }

    public Pane getContentPane() {
        return contentPane;
    }

    public void keyPressed(int key, char c) {
        if (this.contentPane != null) {
            this.contentPane.keyPressed(key, c);
        }
    }

    public void mouseWheel(int change) {
        if (this.contentPane != null) {
            this.contentPane.mouseWheel(change);
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

