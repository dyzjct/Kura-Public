package me.dyzjct.kura.utils.render;

public class FadeUtils {

    protected long start;
    protected long length;

    public FadeUtils(long ms) {
        reset();
        length = ms;
    }

    public void reset() {
        this.start = System.currentTimeMillis();
    }

    public boolean isEnd() {
        return this.getTime() >= this.length;
    }

    protected long getTime() {
        return System.currentTimeMillis() - this.start;
    }

    public void setLength(long length) {
        this.length = length;
    }

    private double getFadeOne() {
        return (double) getTime() / this.length;
    }

    public double getFadeInDefault() {
        if (isEnd()) {
            return 1.0;
        }
        return Math.tanh((double) this.getTime() / (double) this.length * 3.0);
    }

    public double getFadeOutDefault() {
        if (isEnd()) {
            return 0.0;
        }
        return 1.0 - Math.tanh((double) this.getTime() / (double) this.length * 3.0);
    }

    public double getEpsEzFadeIn() {
        if (isEnd()) {
            return 1.0;
        }
        return 1.0 - Math.sin(0.5 * Math.PI * this.getFadeOne()) * Math.sin(0.8 * Math.PI * this.getFadeOne());
    }

    public double getEpsEzFadeInGUI() {
        if (isEnd()) {
            return 1.0;
        }
        return Math.sin(this.getFadeOne());
    }

    public double getEpsEzFadeOut() {
        if (isEnd()) {
            return 0.0;
        }
        return Math.sin(0.5 * Math.PI * this.getFadeOne()) * Math.sin(0.8 * Math.PI * this.getFadeOne());
    }

    public double easeOutQuad() {
        if (isEnd()) {
            return 1.0;
        }
        return 1 - (1 - getFadeOne()) * (1 - getFadeOne());
    }

    public double easeInQuad() {
        if (isEnd()) {
            return 0.0;
        }
        return getFadeOne() * getFadeOne();
    }
}