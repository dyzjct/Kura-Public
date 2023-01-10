package me.windyteam.kura.module.modules.movement;

import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "BoatFly", category = Category.MOVEMENT, description = "Weeeeeeee")
public class BoatFly extends Module {
    public static BoatFly INSTANCE;
    public Setting<Double> speed = dsetting("Speed", 5, 0.1, 100);
    public Setting<Double> VSpeed = dsetting("VerticalSpeed", 5, 0.1, 100);
    public Setting<Boolean> noKick = bsetting("NoKick", false);
    public Setting<Boolean> packet = bsetting("Packet", false);
    public Setting<Integer> packets = isetting("Packets", 3, 1, 5);
    public Setting<Double> interact = dsetting("Delay", 3, 1, 10);
    private int teleportID;

    public BoatFly() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck() || mc.player.getRidingEntity() == null) {
            return;
        }
        //mc.player.getRidingEntity();
        mc.player.getRidingEntity().setNoGravity(true);
        mc.player.getRidingEntity().motionY = 0.0;
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.getRidingEntity().onGround = false;
            mc.player.getRidingEntity().motionY = (this.VSpeed.getValue() / 10.0);
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.getRidingEntity().onGround = false;
            mc.player.getRidingEntity().motionY = (this.VSpeed.getValue() / -10.0);

        }
        double[] normalDir = this.directionSpeed(this.speed.getValue() / 2.0);
        if (mc.player.movementInput.moveStrafe != 0.0f || mc.player.movementInput.moveForward != 0.0f) {
            mc.player.getRidingEntity().motionX = normalDir[0];
            mc.player.getRidingEntity().motionZ = normalDir[1];
        } else {
            mc.player.getRidingEntity().motionX = 0.0;
            mc.player.getRidingEntity().motionZ = 0.0;
        }
        if (this.noKick.getValue()) {
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                if (mc.player.ticksExisted % 8 < 2) {
                    mc.player.getRidingEntity().motionY = -0.03999999910593033;
                }
            } else if (mc.player.ticksExisted % 8 < 4) {
                mc.player.getRidingEntity().motionY = -0.07999999821186066;
            }
        }
        this.handlePackets(mc.player.getRidingEntity().motionX, mc.player.getRidingEntity().motionY, mc.player.getRidingEntity().motionZ);
    }

    public void handlePackets(double x, double y, double z) {
        if (this.packet.getValue()) {
            Vec3d vec = new Vec3d(x, y, z);
            if (mc.player.getRidingEntity() == null) {
                return;
            }
            Vec3d position = mc.player.getRidingEntity().getPositionVector().add(vec);
            mc.player.getRidingEntity().setPosition(position.x, position.y, position.z);
            mc.player.connection.sendPacket(new CPacketVehicleMove(mc.player.getRidingEntity()));
            for (int i = 0; i < this.packets.getValue(); ++i) {
                mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportID++));
            }
        }
    }

    @SubscribeEvent
    public void onSendPacket(PacketEvents.Send event) {
        if (event.getPacket() instanceof CPacketVehicleMove && mc.player.isRiding() && BoatFly.mc.player.ticksExisted / this.interact.getValue() == 0) {
            mc.playerController.interactWithEntity(mc.player, mc.player.ridingEntity, EnumHand.OFF_HAND);
        }
        if ((event.getPacket() instanceof CPacketPlayer.Rotation || event.getPacket() instanceof CPacketInput) && mc.player.isRiding()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvents.Receive event) {
        if (event.getPacket() instanceof SPacketMoveVehicle && mc.player.isRiding()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.teleportID = ((SPacketPlayerPosLook) event.getPacket()).teleportId;
        }
    }

    private double[] directionSpeed(double speed) {
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        double posX = forward * speed * cos + side * speed * sin;
        double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }
}
