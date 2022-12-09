package me.dyzjct.kura.event.events.player;

import me.dyzjct.kura.event.EventStage;
import net.minecraft.client.Minecraft;

public class UpdateWalkingPlayerEvent
        extends EventStage {
    public UpdateWalkingPlayerEvent(int stage) {
        super(stage);
    }
    protected float yaw;
    protected float pitch;

    public void setRotation(float yaw, float pitch) {
        if (Minecraft.getMinecraft().player != null) {
            Minecraft.getMinecraft().player.rotationYawHead = yaw;
            Minecraft.getMinecraft().player.renderYawOffset = yaw;
        }
        this.setYaw(yaw);
        this.setPitch(pitch);
    }
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}

