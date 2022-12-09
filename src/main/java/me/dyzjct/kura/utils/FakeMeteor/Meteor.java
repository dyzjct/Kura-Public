package me.dyzjct.kura.utils.FakeMeteor;

import me.dyzjct.kura.utils.NTMiku.TimerUtils;
import me.dyzjct.kura.utils.Rainbow;
import me.dyzjct.kura.utils.render.FadeUtil;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.Random;

public class Meteor {
    public Vector2f pos;
    public Vector2f pos2;
    public static Random random = new Random();
    public float LineWidth;
    public float LineLength;
    public float alpha;
    public long speedMS;
    private final TimerUtils timerUtils = new TimerUtils();
    public Color randomColor;
    private final FadeUtil fadeUtil = new FadeUtil(170L);

    public Meteor(long speedMS, float x, float y, float LineWidth, float LineLength) {
        this.speedMS = speedMS;
        this.pos = new Vector2f(x, y);
        this.pos2 = new Vector2f(x, y);
        this.LineWidth = LineWidth;
        this.LineLength = LineLength;
        this.randomColor = new Color(Color.HSBtoRGB(random.nextInt(360), 0.4f, 1.0f));
        this.fadeUtil.setLength(speedMS / 5L * 2L);
    }

    public static Meteor generateMeteor() {
        long speedMS = 3000 + random.nextInt(1200);
        float x = random.nextInt(Display.getWidth());
        float y = random.nextInt(Display.getHeight());
        float lineLength = 50 + random.nextInt(300);
        float lineWidth = (float)(Math.random() * 2.0) + 1.0f;
        return new Meteor(speedMS, x, y, lineWidth, lineLength);
    }

    public float getAlpha() {
        return this.alpha;
    }

    public float getLineWidth() {
        return this.LineWidth;
    }

    public float getX() {
        return this.pos.x;
    }

    public float getY() {
        return this.pos.y;
    }

    public float getX2() {
        return this.pos2.x;
    }

    public float getY2() {
        return this.pos2.y;
    }

    public void setLineWidth(float f) {
        this.LineWidth = f;
    }

    public void tick() {
        double speedMoves;
        if (this.timerUtils.passed(this.speedMS)) {
            this.timerUtils.reset();
            this.pos.x = random.nextInt(Display.getWidth());
            this.pos.y = random.nextInt(Display.getHeight());
            this.LineLength = 70 + random.nextInt(300);
            this.randomColor = new Color(Rainbow.getRainbow(random.nextInt(360), 0.4f, 1.0f));
            this.fadeUtil.reset();
            this.alpha = 0.0f;
        }
        if (this.timerUtils.passed((speedMoves = (double)(this.speedMS / 5L)) * 3.0)) {
            this.pos.x = (float)((double)this.pos2.x + (double)this.LineLength * this.fadeUtil.getFade(FadeUtil.FadeMode.FADE_OUT));
            this.pos.y = (float)((double)this.pos2.y - (double)this.LineLength * this.fadeUtil.getFade(FadeUtil.FadeMode.FADE_OUT));
        } else if (this.timerUtils.passed(speedMoves * 2.0)) {
            this.fadeUtil.reset();
        } else {
            this.pos2.x = (float)((double)this.pos.x - (double)this.LineLength * this.fadeUtil.getFade(FadeUtil.FadeMode.FADE_IN));
            this.pos2.y = (float)((double)this.pos.y + (double)this.LineLength * this.fadeUtil.getFade(FadeUtil.FadeMode.FADE_IN));
        }
        if (this.alpha < 255.0f) {
            this.alpha += 15.0f;
        }
    }
}

