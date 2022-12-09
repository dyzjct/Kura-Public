package me.dyzjct.kura.utils.render;

public class FadeUtil {
    protected long start;
    protected long length;

    public FadeUtil(long ms) {
        this.length = ms;
        this.reset();
    }

    public void reset() {
        this.start = System.currentTimeMillis();
    }

    public boolean isEnd() {
        return this.getTime() >= this.length;
    }

    public FadeUtil end() {
        this.start = System.currentTimeMillis() - this.length;
        return this;
    }

    protected long getTime() {
        return System.currentTimeMillis() - this.start;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getLength() {
        return this.length;
    }

    private double getFadeOne() {
        return this.isEnd() ? 1.0 : (double)this.getTime() / (double)this.length;
    }

    public double getFade(FadeMode fadeMode) {
        return FadeUtil.getFade(fadeMode, this.getFadeOne());
    }

    public static double getFade(FadeMode fadeMode, double current) {
        switch (fadeMode) {
            case FADE_IN: {
                return FadeUtil.getFadeInDefault(current);
            }
            case FADE_OUT: {
                return FadeUtil.getFadeOutDefault(current);
            }
            case FADE_EPS_IN: {
                return FadeUtil.getEpsEzFadeIn(current);
            }
            case FADE_EPS_OUT: {
                return FadeUtil.getEpsEzFadeOut(current);
            }
            case FADE_EASE_IN_QUAD: {
                return FadeUtil.easeInQuad(current);
            }
            case FADE_EASE_OUT_QUAD: {
                return FadeUtil.easeOutQuad(current);
            }
        }
        return current;
    }

    public static double getFadeType(FadeType fadeType, boolean FadeIn, double current) {
        switch (fadeType) {
            case FADE_DEFAULT: {
                return FadeIn ? FadeUtil.getFadeInDefault(current) : FadeUtil.getFadeOutDefault(current);
            }
            case FADE_EPS: {
                return FadeIn ? FadeUtil.getEpsEzFadeIn(current) : FadeUtil.getEpsEzFadeOut(current);
            }
            case FADE_EASE_QUAD: {
                return FadeIn ? FadeUtil.easeInQuad(current) : FadeUtil.easeOutQuad(current);
            }
        }
        return FadeIn ? current : 1.0 - current;
    }

    private static double checkOne(double one) {
        return Math.max(0.0, Math.min(1.0, one));
    }

    public static double getFadeInDefault(double current) {
        return Math.tanh(FadeUtil.checkOne(current) * 3.0);
    }

    public static double getFadeOutDefault(double current) {
        return 1.0 - FadeUtil.getFadeInDefault(current);
    }

    public static double getEpsEzFadeIn(double current) {
        return 1.0 - FadeUtil.getEpsEzFadeOut(current);
    }

    public static double getEpsEzFadeOut(double current) {
        return Math.cos(1.5707963267948966 * FadeUtil.checkOne(current)) * Math.cos(2.5132741228718345 * FadeUtil.checkOne(current));
    }

    public static double easeOutQuad(double current) {
        return 1.0 - FadeUtil.easeInQuad(current);
    }

    public static double easeInQuad(double current) {
        return FadeUtil.checkOne(current) * FadeUtil.checkOne(current);
    }

    public static enum FadeMode {
        FADE_IN,
        FADE_OUT,
        FADE_ONE,
        FADE_EPS_IN,
        FADE_EPS_OUT,
        FADE_EASE_OUT_QUAD,
        FADE_EASE_IN_QUAD;

    }

    public static enum FadeType {
        FADE_DEFAULT,
        FADE_ONE,
        FADE_EPS,
        FADE_EASE_QUAD;

    }
}

