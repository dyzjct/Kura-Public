package me.dyzjct.kura.utils.particle.utils;

public class MathUtil {
    public static double distance(double x, double y, double x1, double y1) {
        return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
    }
}

