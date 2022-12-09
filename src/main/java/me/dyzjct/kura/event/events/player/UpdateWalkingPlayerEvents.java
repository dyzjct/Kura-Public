package me.dyzjct.kura.event.events.player;

import me.dyzjct.kura.event.EventStage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class UpdateWalkingPlayerEvents
        extends EventStage {
    public static UpdateWalkingPlayerEvents INSTANCE;
    protected float yaw;
    protected float pitch;
    protected double x;
    protected double y;
    protected double z;
    protected boolean onGround;

    public UpdateWalkingPlayerEvents(int stage, double posX, double posY, double posZ, float y, float p, boolean pOnGround) {
        super(stage);
        INSTANCE = this;
        this.x = posX;
        this.y = posY;
        this.z = posZ;
        this.yaw = y;
        this.pitch = p;
        this.onGround = pOnGround;
    }

    public UpdateWalkingPlayerEvents(int stage, UpdateWalkingPlayerEvents event) {
        this(stage, event.x, event.y, event.z, event.yaw, event.pitch, event.onGround);
    }

    public void setRotation(float yaw, float pitch) {
        if (Minecraft.getMinecraft().player != null) {
            Minecraft.getMinecraft().player.rotationYawHead = yaw;
            Minecraft.getMinecraft().player.renderYawOffset = yaw;
        }
        this.setYaw(yaw);
        this.setPitch(pitch);
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = (float)yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = (float)pitch;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double posX) {
        this.x = posX;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double d) {
        this.y = d;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double posZ) {
        this.z = posZ;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void setOnGround(boolean b) {
        this.onGround = b;
    }
}

