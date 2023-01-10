package me.windyteam.kura.utils.player;

import java.text.DecimalFormat;
import net.minecraft.client.Minecraft;

public class MovementUtil {
    static Minecraft mc = Minecraft.getMinecraft();

    static DecimalFormat decimal = new DecimalFormat();

    public static Double getPosX() {
        decimal.applyPattern("0.00");
        return Double.valueOf(Double.parseDouble(decimal.format(mc.player.posX)));
    }

    public static Double getPosY() {
        decimal.applyPattern("0.00");
        return Double.valueOf(Double.parseDouble(decimal.format(mc.player.posY)));
    }

    public static Double getPosZ() {
        decimal.applyPattern("0.00");
        return Double.valueOf(Double.parseDouble(decimal.format(mc.player.posZ)));
    }

    public static Double getTotalPosMoved() {
        decimal.applyPattern("0.00");
        return Double.valueOf(Double.parseDouble(decimal.format(Math.sqrt(getPosX().doubleValue() * getPosX().doubleValue() + getPosZ().doubleValue() * getPosZ().doubleValue()))));
    }

    public static boolean onSpeed(double speed) {
        return (getTotalPosMoved().doubleValue() > speed);
    }

    public static void motionJump() {
        if (!mc.player.collidedVertically) {
            if (mc.player.motionY == -0.07190068807140403D) {
                mc.player.motionY *= 0.3499999940395355D;
            } else if (mc.player.motionY == -0.10306193759436909D) {
                mc.player.motionY *= 0.550000011920929D;
            } else if (mc.player.motionY == -0.13395038817442878D) {
                mc.player.motionY *= 0.6700000166893005D;
            } else if (mc.player.motionY == -0.16635183030382D) {
                mc.player.motionY *= 0.6899999976158142D;
            } else if (mc.player.motionY == -0.19088711097794803D) {
                mc.player.motionY *= 0.7099999785423279D;
            } else if (mc.player.motionY == -0.21121925191528862D) {
                mc.player.motionY *= 0.20000000298023224D;
            } else if (mc.player.motionY == -0.11979897632390576D) {
                mc.player.motionY *= 0.9300000071525574D;
            } else if (mc.player.motionY == -0.18758479151225355D) {
                mc.player.motionY *= 0.7200000286102295D;
            } else if (mc.player.motionY == -0.21075983825251726D) {
                mc.player.motionY *= 0.7599999904632568D;
            }
            if (mc.player.motionY < -0.2D && mc.player.motionY > -0.24D)
                mc.player.motionY *= 0.7D;
            if (mc.player.motionY < -0.25D && mc.player.motionY > -0.32D)
                mc.player.motionY *= 0.8D;
            if (mc.player.motionY < -0.35D && mc.player.motionY > -0.8D)
                mc.player.motionY *= 0.98D;
            if (mc.player.motionY < -0.8D && mc.player.motionY > -1.6D)
                mc.player.motionY *= 0.99D;
        }
    }
}
