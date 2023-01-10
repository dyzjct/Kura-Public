package me.windyteam.kura.module.modules.player;

import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.event.events.entity.MoveEvent;
import me.windyteam.kura.event.events.entity.PushEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.math.MathUtil;
import me.windyteam.kura.event.events.entity.MoveEvent;
import me.windyteam.kura.event.events.entity.PushEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.math.MathUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by 086 on 22/12/2017.
 */
@Module.Info(name = "Freecam", category = Category.PLAYER, description = "Leave your body and trascend into the realm of the gods")
public class Freecam extends Module {

    public static Freecam INSTANCE = new Freecam();
    public Setting<Boolean> CancelPackes = bsetting("CancelPackets", true);
    public Setting<Boolean> toggleRStep = bsetting("ToggleRStep", true);
    public Setting<Double> speed = dsetting("Speed", 1, 0.1, 10);
    public boolean firstStart = false;
    public double posX, posY, posZ;
    public float pitch, yaw;
    public EntityOtherPlayerMP clonedPlayer;
    public boolean isRidingEntity;
    public Entity ridingEntity;

    public Freecam() {
        this.setInstance();
    }

    public static Freecam getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Freecam();
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public void PacketSend(PacketEvents.Send event) {
        if (!CancelPackes.getValue())
            return;
        if (event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketInput) {
            event.setCanceled(true);
        }
    }

    public void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onPush(PushEvent event) {
        event.setCanceled(true);
    }

    @Override
    public void onEnable() {
        firstStart = true;
        if (this.toggleRStep.getValue()) {
            ModuleManager.getModuleByName("ReverseStep").disable();
        }
        if (mc.player != null) {
            isRidingEntity = mc.player.getRidingEntity() != null;

            if (mc.player.getRidingEntity() == null) {
                posX = mc.player.posX;
                posY = mc.player.posY;
                posZ = mc.player.posZ;
            } else {
                ridingEntity = mc.player.getRidingEntity();
                mc.player.dismountRidingEntity();
            }

            pitch = mc.player.rotationPitch;
            yaw = mc.player.rotationYaw;

            clonedPlayer = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
            clonedPlayer.setEntityBoundingBox(new AxisAlignedBB(0, 0, 0, 0, 0, 0));
            clonedPlayer.copyLocationAndAnglesFrom(mc.player);
            clonedPlayer.rotationYawHead = mc.player.rotationYawHead;
            mc.world.addEntityToWorld(-101, clonedPlayer);
            mc.player.capabilities.isFlying = true;
            mc.player.capabilities.setFlySpeed((float) (speed.getValue() / 100f));
            mc.player.noClip = true;
        }
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        if (this.toggleRStep.getValue()) {
            ModuleManager.getModuleByName("ReverseStep").enable();
        }
        mc.player.setPositionAndRotation(posX, posY, posZ, clonedPlayer.rotationYaw, clonedPlayer.rotationPitch);
        mc.world.removeEntityFromWorld(-101);
        clonedPlayer = null;
        posX = posY = posZ = 0.D;
        pitch = yaw = 0.f;
        mc.player.capabilities.isFlying = false;
        mc.player.capabilities.setFlySpeed(0.05f);
        mc.player.noClip = false;
        mc.player.motionX = mc.player.motionY = mc.player.motionZ = 0.f;
        if (isRidingEntity) {
            mc.player.startRiding(ridingEntity, true);
        }
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        if (toggleRStep.getValue()) {
            if (ModuleManager.getModuleByName("ReverseStep").isEnabled()) {
                return;
            }
        }
        mc.player.noClip = true;
        mc.player.setVelocity(0, 0, 0);
        final double[] dir = MathUtil.directionSpeed(this.speed.getValue());
        if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
        mc.player.setSprinting(false);
        clonedPlayer.prevRotationPitch = mc.player.prevRotationPitch;
        mc.player.prevRotationPitch = clonedPlayer.prevRotationPitch;

        clonedPlayer.rotationPitch = mc.player.rotationPitch;
        mc.player.rotationPitch = clonedPlayer.rotationPitch;

        clonedPlayer.rotationYaw = mc.player.rotationYaw;
        mc.player.rotationYaw = clonedPlayer.rotationYaw;

        clonedPlayer.renderYawOffset = mc.player.renderYawOffset;
        mc.player.renderYawOffset = clonedPlayer.renderYawOffset;

        clonedPlayer.rotationYawHead = mc.player.rotationYawHead;
        mc.player.rotationYawHead = clonedPlayer.rotationYawHead;

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY += this.speed.getValue();
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY -= this.speed.getValue();
        }
    }

    @SubscribeEvent
    public void move(MoveEvent event) {
        if (firstStart) {
            event.setX(0);
            event.setY(0);
            event.setZ(0);
            firstStart = false;
        }
        mc.player.noClip = true;
    }

    @SubscribeEvent
    public void onWorldEvent(EntityJoinWorldEvent event) {
        if (event.getEntity() == mc.player) {
            toggle();
        }
    }

}
