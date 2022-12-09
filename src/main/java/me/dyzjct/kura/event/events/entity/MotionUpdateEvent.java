package me.dyzjct.kura.event.events.entity;

import me.dyzjct.kura.event.EventStage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class MotionUpdateEvent extends EventStage {
    public static MotionUpdateEvent INSTANCE;
    protected float yaw;
    protected float pitch;
    protected double x;
    protected double y;
    protected double z;
    protected boolean onGround;

    public MotionUpdateEvent(int stage, double posX, double posY, double posZ, float y, float p, boolean pOnGround) {
        super(stage);
        this.x = posX;
        this.y = posY;
        this.z = posZ;
        this.yaw = y;
        this.pitch = p;
        this.onGround = pOnGround;
        INSTANCE = this;
    }

    public MotionUpdateEvent(int stage, MotionUpdateEvent event) {
        this(stage, event.x, event.y, event.z, event.yaw, event.pitch, event.onGround);
    }

    public void setRotation(float yaw, float pitch) {
        if (Minecraft.getMinecraft().player != null) {
            Minecraft.getMinecraft().player.rotationYawHead = yaw;
            Minecraft.getMinecraft().player.renderYawOffset = yaw;
        }
        setYaw(yaw);
        setPitch(pitch);
    }

    public void setPostion(double x, double y, double z, boolean onGround) {
        setX(x);
        setY(y);
        setZ(z);
        setOnGround(onGround);
    }

    public void setPostion(BlockPos pos, boolean onGround) {
        setX(pos.getX());
        setY(pos.getY());
        setZ(pos.getZ());
        setOnGround(onGround);
    }

    public void setPostion(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        setX(x);
        setY(y);
        setZ(z);
        setYaw(yaw);
        setPitch(pitch);
        setOnGround(onGround);
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = (float) yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = (float) pitch;
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

    //Class Tick
    @Cancelable
    public static class Tick extends MotionUpdateEvent {
        public static Tick INSTANCETick;

        public Tick(int stage, MotionUpdateEvent event) {
            super(stage, event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch(), event.isOnGround());
            INSTANCETick = this;
        }
    }

    //Class FastTick
    @Cancelable
    public static class FastTick extends Event {
        public EntityPlayerSP player;
        public PlayerControllerMP playerController;
        public NetHandlerPlayClient connection;
        public WorldClient world;

        public FastTick(EntityPlayerSP player, PlayerControllerMP playerController, NetHandlerPlayClient connection, WorldClient world) {
            this.player = player;
            this.playerController = playerController;
            this.connection = connection;
            this.world = world;
        }
        public FastTick(EntityPlayerSP player, PlayerControllerMP playerController , WorldClient world) {
            this.player = player;
            this.playerController = playerController;
            this.world = world;
        }
    }

    //Class RenderTick
    @Cancelable
    public static class RenderTick extends Event {
        public static RenderTick INSTANCEFast;

        public RenderTick() {
            INSTANCEFast = this;
        }
    }

    //Class StartTick
    @Cancelable
    public static class StartTick extends Event {
        public static StartTick INSTANCEFast;

        public StartTick() {
            INSTANCEFast = this;
        }
    }

    //Class EndTick
    @Cancelable
    public static class EndTick extends Event {
        public static EndTick INSTANCEFast;

        public EndTick() {
            INSTANCEFast = this;
        }
    }
}

