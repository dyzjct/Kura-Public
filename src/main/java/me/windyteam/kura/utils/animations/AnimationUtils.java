package me.windyteam.kura.utils.animations;


import me.windyteam.kura.utils.Timer;

public class AnimationUtils {
    private static float defaultSpeed = 0.125F;
    private Timer timerUtil = new Timer();

    public AnimationUtils() {
    }

    public static float calculateCompensation(float target, float current, long delta, double speed) {
        float diff = current - target;
        if (delta < 1L) {
            delta = 1L;
        }

        if (delta > 1000L) {
            delta = 16L;
        }

        double xD;
        if ((double)diff > speed) {
            xD = speed * (double)delta / 16.0 < 0.5 ? 0.5 : speed * (double)delta / 16.0;
            if ((current = (float)((double)current - xD)) < target) {
                current = target;
            }
        } else if ((double)diff < -speed) {
            xD = speed * (double)delta / 16.0 < 0.5 ? 0.5 : speed * (double)delta / 16.0;
            if ((current = (float)((double)current + xD)) > target) {
                current = target;
            }
        } else {
            current = target;
        }

        return current;
    }

    public float mvoeUD(float current, float end, float minSpeed) {
        return this.moveUD(current, end, defaultSpeed, minSpeed);
    }

    public double animate(double target, double current, double speed) {
        if (this.timerUtil.passedMs(2L)) {
            boolean larger = target > current;
            if (speed < 0.0) {
                speed = 0.0;
            } else if (speed > 1.0) {
                speed = 1.0;
            }

            double dif = Math.max(target, current) - Math.min(target, current);
            double factor = dif * speed;
            if (factor < 0.1) {
                factor = 0.1;
            }

            current = larger ? current + factor : current - factor;
            this.timerUtil.reset();
        }

        return current;
    }

    public float animate(float target, float current, float speed) {
        if (this.timerUtil.passedMs(2L)) {
            boolean larger = target > current;
            if (speed < 0.0F) {
                speed = 0.0F;
            } else if ((double)speed > 1.0) {
                speed = 1.0F;
            }

            float dif = Math.max(target, current) - Math.min(target, current);
            float factor = dif * speed;
            if (factor < 0.1F) {
                factor = 0.1F;
            }

            current = larger ? current + factor : current - factor;
            this.timerUtil.reset();
        }

        return (double)Math.abs(current - target) < 0.2 ? target : current;
    }

    public float moveUD(float current, float end, float smoothSpeed, float minSpeed) {
        float movement = 0.0F;
        if (this.timerUtil.passedMs(20L)) {
            movement = (end - current) * smoothSpeed;
            if (movement > 0.0F) {
                movement = Math.max(minSpeed, movement);
                movement = Math.min(end - current, movement);
            } else if (movement < 0.0F) {
                movement = Math.min(-minSpeed, movement);
                movement = Math.max(end - current, movement);
            }

            this.timerUtil.reset();
        }

        return current + movement;
    }
}
