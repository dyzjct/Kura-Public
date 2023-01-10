package me.windyteam.kura.utils.math;

import net.minecraft.util.math.Vec3d;

public final class Vec2d {
    private double x;
    private double y;

    public Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2d() {
    }

    public Vec2d(Vec3d vec3d) {
        this(vec3d.x, vec3d.y);
    }

    public Vec2d(Vec2d vec2d) {
        this(vec2d.x, vec2d.y);
    }

    public final Vec2d toRadians() {
        return new Vec2d(this.x / 180.0 * Math.PI, this.y / 180.0 * Math.PI);
    }

    public final double length() {
        double d = this.lengthSquared();
        boolean bl = false;
        return Math.sqrt(d);
    }

    public final double lengthSquared() {
        double d = this.x;
        int n = 2;
        boolean bl = false;
        double d2 = Math.pow(d, n);
        d = this.y;
        n = 2;
        double d3 = d2;
        bl = false;
        double d4 = Math.pow(d, n);
        return d3 + d4;
    }

    public final Vec2d divide(Vec3d vec3d) {
        return this.divide(vec3d.x, vec3d.y);
    }

    public final Vec2d divide(Vec2d vec2d) {
        return this.divide(vec2d.x, vec2d.y);
    }

    public final Vec2d divide(double divider) {
        return this.divide(divider, divider);
    }

    public final Vec2d divide(double x, double y) {
        return new Vec2d(this.x / x, this.y / y);
    }

    public final Vec2d multiply(Vec3d vec3d) {
        return this.multiply(vec3d.x, vec3d.y);
    }

    public final Vec2d multiply(Vec2d vec2d) {
        return this.multiply(vec2d.x, vec2d.y);
    }

    public final Vec2d multiply(double mulitplier) {
        return this.multiply(mulitplier, mulitplier);
    }

    public final Vec2d multiply(double x, double y) {
        return new Vec2d(this.x * x, this.y * y);
    }

    public final Vec2d subtract(Vec3d vec3d) {
        return this.subtract(vec3d.x, vec3d.y);
    }

    public final Vec2d subtract(Vec2d vec2d) {
        return this.subtract(vec2d.x, vec2d.y);
    }

    public final Vec2d subtract(double sub) {
        return this.subtract(sub, sub);
    }

    public final Vec2d subtract(double x, double y) {
        return this.add(-x, -y);
    }

    public final Vec2d add(Vec3d vec3d) {
        return this.add(vec3d.x, vec3d.y);
    }

    public final Vec2d add(Vec2d vec2d) {
        return this.add(vec2d.x, vec2d.y);
    }

    public final Vec2d add(double add) {
        return this.add(add, add);
    }

    public final Vec2d add(double x, double y) {
        return new Vec2d(this.x + x, this.y + y);
    }

    public String toString() {
        return "Vec2d[" + this.x + ", " + this.y + ']';
    }

    public final double getX() {
        return this.x;
    }

    public final void setX(double d) {
        this.x = d;
    }

    public final double getY() {
        return this.y;
    }

    public final void setY(double d) {
        this.y = d;
    }
}

