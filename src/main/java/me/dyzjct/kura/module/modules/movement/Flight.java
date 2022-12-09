package me.dyzjct.kura.module.modules.movement;

import me.dyzjct.kura.event.events.client.PacketEvents;
import me.dyzjct.kura.gui.Notification;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.FloatSetting;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.Timer;
import me.dyzjct.kura.utils.entity.EntityUtil;
import me.dyzjct.kura.utils.math.RandomUtil;
import me.dyzjct.kura.utils.mc.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by 086 on 25/08/2017.
 */
@Module.Info(category = Category.MOVEMENT, description = "Makes the player fly", name = "Flight")
public class Flight extends Module {
    public static Flight INSTANCE = new Flight();
    public Setting<FlightMode> mode = msetting("Mode", FlightMode.CREATIVE);
    public Setting<Float> speed = fsetting("Speed", 2, 0.1f, 10);
    public FloatSetting motion = fsetting("Motion", 0, 0, 1);
    public BooleanSetting DamageBoost = bsetting("DamageBoost", false);
    public Timer timer = new Timer();
    public Timer delay = new Timer();
    public boolean warn = false;
    public float launchY;

    public void damagePlayer(double d) {
        if (d < 1)
            d = 1;
        if (d > MathHelper.floor(mc.player.getMaxHealth()))
            d = MathHelper.floor(mc.player.getMaxHealth());

        double offset = 0.0625;
        if (mc.player != null && mc.getConnection() != null && mc.player.onGround) {
            for (int i = 0; i <= ((3 + d) / offset); i++) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,
                        mc.player.posY + offset, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,
                        mc.player.posY, mc.player.posZ, (i == ((3 + d) / offset))));
            }
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvents.Receive event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            if (!warn) {
                ChatUtil.sendClientMessage("(LagBackCheck) Fly Disabled", Notification.Type.ERROR);
                warn = true;
            }
            toggle();
        }
    }

    @Override
    public void onEnable() {
        if (mc.player == null) return;
        warn = false;
        delay.reset();
        launchY = (float) mc.player.posY;
        if (mode.getValue() == FlightMode.VANILLA) {
            mc.player.capabilities.isFlying = true;
            if (mc.player.capabilities.isCreativeMode) return;
            mc.player.capabilities.allowFlying = true;
        }
        if (mode.getValue().equals(FlightMode.OLDNCP)) {
            if (!mc.player.onGround) {
                return;
            }
            for (int i = 0; i < 3; i++) {
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.01, mc.player.posZ, false));
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            }
            mc.player.jump();
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        if (mode.getValue().equals(FlightMode.NEWNCP)) {
            if (!mc.player.onGround) {
                return;
            }
            for (int i = 0; i < 65; i++) {
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.049, mc.player.posZ, false));
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            }
            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1, mc.player.posZ, true));

            mc.player.motionX *= 0.1;
            mc.player.motionZ *= 0.1;
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        if (delay.passed(1500)) {
            warn = false;
            delay.reset();
        }
        if (DamageBoost.getValue()) {
            if (timer.passed(500)) {
                damagePlayer(RandomUtil.nextInt(1, 3));
                timer.reset();
            }
        }
        switch (mode.getValue()) {
            case STATIC:
                mc.player.capabilities.isFlying = false;
                mc.player.motionX = 0;
                mc.player.motionY = 0;
                mc.player.motionZ = 0;
                mc.player.jumpMovementFactor = speed.getValue();

                if (mc.gameSettings.keyBindJump.isKeyDown())
                    mc.player.motionY += speed.getValue();
                if (mc.gameSettings.keyBindSneak.isKeyDown())
                    mc.player.motionY -= speed.getValue();
                break;
            case VANILLA:
                mc.player.capabilities.setFlySpeed(speed.getValue() / 100f);
                mc.player.capabilities.isFlying = true;
                if (mc.player.capabilities.isCreativeMode) return;
                mc.player.capabilities.allowFlying = true;
                break;
            case PACKET:
                int angle;

                boolean forward = mc.gameSettings.keyBindForward.isKeyDown();
                boolean left = mc.gameSettings.keyBindLeft.isKeyDown();
                boolean right = mc.gameSettings.keyBindRight.isKeyDown();
                boolean back = mc.gameSettings.keyBindBack.isKeyDown();

                if (left && right) angle = forward ? 0 : back ? 180 : -1;
                else if (forward && back) angle = left ? -90 : (right ? 90 : -1);
                else {
                    angle = left ? -90 : (right ? 90 : 0);
                    if (forward) angle /= 2;
                    else if (back) angle = 180 - (angle / 2);
                }

                if (angle != -1 && (forward || left || right || back)) {
                    float yaw = mc.player.rotationYaw + angle;
                    mc.player.motionX = EntityUtil.getRelativeX(yaw) * 0.2f;
                    mc.player.motionZ = EntityUtil.getRelativeZ(yaw) * 0.2f;
                }

                mc.player.motionY = 0;
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, mc.player.posY + (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown() ? 0.0622 : 0) - (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown() ? 0.0622 : 0), mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, false));
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, mc.player.posY - 42069, mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
                break;
            case CREATIVE:
                mc.player.capabilities.isFlying = true;
                break;
            case OLDNCP:
                if (launchY > mc.player.posY) {
                    mc.player.motionY = -0.000000000000000000000000000000001;
                }

                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.player.motionY = -0.2;
                }

                if (mc.gameSettings.keyBindJump.isKeyDown() && mc.player.posY < launchY - 0.1) {
                    mc.player.motionY = 0.2;
                }
                EntityUtil.strafe();
                break;
            case NEWNCP:
                mc.player.motionY = -motion.getValue();
                if (mc.gameSettings.keyBindSneak.isKeyDown()) mc.player.motionY = -0.5;
                EntityUtil.strafe();
                break;
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvents.Send event) {
        if (fullNullCheck()) {
            return;
        }
        if (mode.getValue().equals(FlightMode.NEWNCP)) {
            if (event.getPacket() instanceof CPacketPlayer) {
                ((CPacketPlayer) event.getPacket()).onGround = true;
            }
        }
    }

    @Override
    public void onDisable() {
        switch (mode.getValue()) {
            case VANILLA:
                mc.player.capabilities.isFlying = false;
                mc.player.capabilities.setFlySpeed(0.05f);
                if (mc.player.capabilities.isCreativeMode) return;
                mc.player.capabilities.allowFlying = false;
                break;
            case CREATIVE:
                mc.player.capabilities.isFlying = false;
        }
    }

    public enum FlightMode {
        VANILLA, STATIC, PACKET, CREATIVE, OLDNCP, NEWNCP
    }

}
