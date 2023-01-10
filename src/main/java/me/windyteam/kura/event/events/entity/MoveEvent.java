package me.windyteam.kura.event.events.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.MoverType;
import me.windyteam.kura.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class MoveEvent extends EventStage {
    public MoverType type;
    public double X;
    public double Y;
    public double Z;
    
    public MoveEvent(final int stage, final MoverType type, final double x, final double y, final double z) {
        super(stage);
        this.type = type;
        this.X = x;
        this.Y = y;
        this.Z = z;
    }
    
    public MoverType getType() {
        return this.type;
    }
    
    public void setType(final MoverType type) {
        this.type = type;
    }
    
    public double getX() {
        return this.X;
    }
    
    public double getY() {
        return this.Y;
    }
    
    public double getZ() {
        return this.Z;
    }
    
    public void setX(final double x) {
        this.X = x;
    }
    
    public void setY(final double y) {
        this.Y = y;
    }
    
    public void setZ(final double z) {
        this.Z = z;
    }

    public void setSpeed(double speed) {
        float yaw = (Minecraft.getMinecraft()).player.rotationYaw;
        double forward = (Minecraft.getMinecraft()).player.movementInput.moveForward;
        double strafe = (Minecraft.getMinecraft()).player.movementInput.moveStrafe;
        if (forward == 0.0D && strafe == 0.0D) {
            setX(0.0D);
            setZ(0.0D);
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += ((forward > 0.0D) ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += ((forward > 0.0D) ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1.0D;
                } else {
                    forward = -1.0D;
                }
            }
            double cos = Math.cos(Math.toRadians((yaw + 90.0F)));
            double sin = Math.sin(Math.toRadians((yaw + 90.0F)));
            setX(forward * speed * cos + strafe * speed * sin);
            setZ(forward * speed * sin - strafe * speed * cos);
        }
    }
}
