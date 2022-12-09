package me.dyzjct.kura.module.modules.movement;

import me.dyzjct.kura.event.events.client.PacketEvents;
import me.dyzjct.kura.event.events.entity.JesusEvent;
import me.dyzjct.kura.event.events.entity.MotionUpdateEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.setting.ModeSetting;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.entity.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "Jesus", description = "Allows you to walk on water", category = Category.MOVEMENT)
public class Jesus extends Module {
    public static AxisAlignedBB offset;
    private static Jesus INSTANCE;

    static {
        offset = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.9999, 1.0);
        INSTANCE = new Jesus();
    }

    public ModeSetting<?> mode = msetting("Mode", Mode.BOUNCE);
    public ModeSetting<?> eventMode = msetting("JumpMode", EventMode.ALL);
    public Setting<Boolean> cancelVehicle = bsetting("NoVehicle", true);
    public Setting<Boolean> fall = bsetting("NoFall", false);
    private boolean grounded;

    public Jesus() {
        this.grounded = false;
        INSTANCE = this;
    }

    public static Jesus getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Jesus();
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public void updateWalkingPlayer(final MotionUpdateEvent.Tick event) {
        if (fullNullCheck() || ModuleManager.getModuleByName("Freecam").isEnabled()) {
            return;
        }
        if (event.getStage() == 0 && (this.mode.getValue() == Mode.BOUNCE || this.mode.getValue() == Mode.VANILLA || this.mode.getValue() == Mode.NORMAL) && !mc.player.isSneaking() && !mc.player.noClip && !mc.gameSettings.keyBindJump.isKeyDown() && EntityUtil.isInLiquid()) {
            mc.player.motionY = 0.10000000149011612;
        }
        if (event.getStage() == 0 && this.mode.getValue() == Mode.TRAMPOLINE && (this.eventMode.getValue() == EventMode.ALL || this.eventMode.getValue() == EventMode.PRE)) {
            this.doTrampoline();
        } else if (event.getStage() == 1 && this.mode.getValue() == Mode.TRAMPOLINE && (this.eventMode.getValue() == EventMode.ALL || this.eventMode.getValue() == EventMode.POST)) {
            this.doTrampoline();
        }
    }

    @SubscribeEvent
    public void sendPacket(final PacketEvents.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && ModuleManager.getModuleByName("Freecam").isDisabled() && (this.mode.getValue() == Mode.BOUNCE || this.mode.getValue() == Mode.NORMAL) && mc.player.getRidingEntity() == null && !mc.gameSettings.keyBindJump.isKeyDown()) {
            final CPacketPlayer packet = event.getPacket();
            if (!EntityUtil.isInLiquid() && EntityUtil.isOnLiquid(0.05000000074505806) && EntityUtil.checkCollide() && mc.player.ticksExisted % 3 == 0) {
                packet.y -= 0.05000000074505806;
            }
        }
    }

    @SubscribeEvent
    public void onLiquidCollision(final JesusEvent event) {
        if (fullNullCheck() || ModuleManager.getModuleByName("Freecam").isEnabled()) {
            return;
        }
        if (event.getStage() == 0 && (this.mode.getValue() == Mode.BOUNCE || this.mode.getValue() == Mode.VANILLA || this.mode.getValue() == Mode.NORMAL) && mc.world != null && mc.player != null && EntityUtil.checkCollide() && mc.player.motionY < 0.10000000149011612 && event.getPos().getY() < mc.player.posY - 0.05000000074505806) {
            if (mc.player.getRidingEntity() != null) {
                event.setBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.949999988079071, 1.0));
            } else {
                event.setBoundingBox(Block.FULL_BLOCK_AABB);
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPacketReceived(final PacketEvents.Receive event) {
        if (this.cancelVehicle.getValue() && event.getPacket() instanceof SPacketMoveVehicle) {
            event.setCanceled(true);
        }
    }

    private void doTrampoline() {
        if (mc.player.isSneaking()) {
            return;
        }
        if (EntityUtil.isAboveLiquid(mc.player) && !mc.player.isSneaking() && !mc.gameSettings.keyBindJump.pressed) {
            mc.player.motionY = 0.1;
            return;
        }
        if (mc.player.onGround || mc.player.isOnLadder()) {
            this.grounded = false;
        }
        if (mc.player.motionY > 0.0) {
            if (mc.player.motionY < 0.03 && this.grounded) {
                final EntityPlayerSP player = mc.player;
                player.motionY += 0.06713;
            } else if (mc.player.motionY <= 0.05 && this.grounded) {
                final EntityPlayerSP player2 = mc.player;
                player2.motionY *= 1.20000000999;
                final EntityPlayerSP player3 = mc.player;
                player3.motionY += 0.06;
            } else if (mc.player.motionY <= 0.08 && this.grounded) {
                final EntityPlayerSP player4 = mc.player;
                player4.motionY *= 1.20000003;
                final EntityPlayerSP player5 = mc.player;
                player5.motionY += 0.055;
            } else if (mc.player.motionY <= 0.112 && this.grounded) {
                final EntityPlayerSP player6 = mc.player;
                player6.motionY += 0.0535;
            } else if (this.grounded) {
                final EntityPlayerSP player7 = mc.player;
                player7.motionY *= 1.000000000002;
                final EntityPlayerSP player8 = mc.player;
                player8.motionY += 0.0517;
            }
        }
        if (this.grounded && mc.player.motionY < 0.0 && mc.player.motionY > -0.3) {
            final EntityPlayerSP player9 = mc.player;
            player9.motionY += 0.045835;
        }
        if (!this.fall.getValue()) {
            mc.player.fallDistance = 0.0f;
        }
        if (!EntityUtil.checkForLiquid(mc.player, true)) {
            return;
        }
        if (EntityUtil.checkForLiquid(mc.player, true)) {
            mc.player.motionY = 0.5;
        }
        this.grounded = true;
    }

    public enum EventMode {
        PRE,
        POST,
        ALL
    }

    public enum Mode {
        TRAMPOLINE,
        BOUNCE,
        VANILLA,
        NORMAL
    }
}

