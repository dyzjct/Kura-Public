package me.dyzjct.kura.gui.xg42guistart;

public abstract class Lcomponent {
    protected long start;
    protected final long fadedIn;
    protected final long fadeOut;
    protected final long end;

    public Lcomponent(int length) {
        this.fadedIn = 200L * (long)length;
        this.fadeOut = this.fadedIn + 500L * (long)length;
        this.end = this.fadeOut + this.fadedIn;
    }

    public void show() {
        this.start = System.currentTimeMillis();
    }

    public boolean isShown() {
        return this.getTime() <= this.end;
    }

    protected long getTime() {
        return System.currentTimeMillis() - this.start;
    }

    protected int getAlpha() {
        if (this.getTime() < this.fadedIn) {
            return (int)(Math.tanh((double)this.getTime() / (double)this.fadedIn * 3.0) * 255.0);
        }
        if (this.getTime() > this.fadeOut) {
            return (int)(Math.tanh(3.0 - (double)(this.getTime() - this.fadeOut) / (double)(this.end - this.fadeOut) * 3.0) * 255.0);
        }
        return 255;
    }

    public abstract void render(int var1, int var2);
}

