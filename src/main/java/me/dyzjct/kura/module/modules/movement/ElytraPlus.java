package me.dyzjct.kura.module.modules.movement;

import me.dyzjct.kura.module.*;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.*;
import me.dyzjct.kura.event.events.client.*;
import me.dyzjct.kura.utils.NTMiku.TimerUtils;
import me.dyzjct.kura.utils.math.MathUtil;
import me.dyzjct.kura.utils.mc.ChatUtil;
import java.util.*;
import net.minecraft.network.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.dyzjct.kura.event.events.entity.*;
import net.minecraft.init.*;
import net.minecraft.network.play.client.*;
import net.minecraft.entity.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import me.dyzjct.kura.manager.*;
import net.minecraft.client.entity.*;
@Module.Info(name = "ElytraFly", description = "Allows you to fly with elytra", category = Category.MOVEMENT)
public class ElytraPlus extends Module
{
    public final Setting<?> mode;
    public final Setting<Float> speed;
    public final Setting<Float> DownSpeed;
    public final Setting<Float> GlideSpeed;
    public final Setting<Float> UpSpeed;
    public final Setting<Boolean> Accelerate;
    public final Setting<Integer> vAccelerationTimer;
    public final Setting<Float> RotationPitch;
    public final Setting<Boolean> CancelInWater;
    public final Setting<Integer> CancelAtHeight;
    public final Setting<Boolean> InstantFly;
    public final Setting<Boolean> onEnableEquipElytra;
    public final Setting<Boolean> PitchSpoof;
    private final TimerUtils AccelerationTimer;
    private final TimerUtils AccelerationResetTimer;
    private final TimerUtils InstantFlyTimer;
    private boolean SendMessage;
    private int ElytraSlot;

    public ElytraPlus() {
        this.mode = (Setting<?>)this.msetting("Mode", (Enum)Mode.Superior);
        this.speed = this.fsetting("Speed", 18.0f, 0.0f, 50.0f);
        this.DownSpeed = this.fsetting("DownSpeed", 1.8f, 0.0f, 10.0f);
        this.GlideSpeed = this.fsetting("GlideSpeed", 1.0E-4f, 0.0f, 10.0f);
        this.UpSpeed = this.fsetting("UpSpeed", 5.0f, 0.0f, 10.0f);
        this.Accelerate = this.bsetting("Accelerate", true);
        this.vAccelerationTimer = this.isetting("AccTime", 1000, 0, 10000);
        this.RotationPitch = this.fsetting("RotationPitch", 45.0f, 0.0f, 90.0f);
        this.CancelInWater = this.bsetting("CancelInWater", true);
        this.CancelAtHeight = this.isetting("CancelHeight", 0, 0, 10);
        this.InstantFly = this.bsetting("FastBoost", true);
        this.onEnableEquipElytra = this.bsetting("AutoEnableWhileElytra", false);
        this.PitchSpoof = this.bsetting("PitchSpoof", false);
        this.AccelerationTimer = new TimerUtils();
        this.AccelerationResetTimer = new TimerUtils();
        this.InstantFlyTimer = new TimerUtils();
        this.SendMessage = false;
        this.ElytraSlot = -1;
    }

    @SubscribeEvent
    public void PacketEvents(final PacketEvents.Send event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof CPacketPlayer && this.PitchSpoof.getValue()) {
            if (!ElytraPlus.mc.player.isElytraFlying()) {
                return;
            }
            if (event.getPacket() instanceof CPacketPlayer.PositionRotation && this.PitchSpoof.getValue()) {
                final CPacketPlayer.PositionRotation rotation = (CPacketPlayer.PositionRotation)event.getPacket();
                Objects.requireNonNull(ElytraPlus.mc.getConnection()).sendPacket((Packet)new CPacketPlayer.Position(rotation.x, rotation.y, rotation.z, rotation.onGround));
                event.setCanceled(true);
            }
            else if (event.getPacket() instanceof CPacketPlayer.Rotation && this.PitchSpoof.getValue()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void Travel(final EventPlayerTravel event) {
        if (fullNullCheck()) {
            return;
        }
        if (ElytraPlus.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA) {
            return;
        }
        if (!ElytraPlus.mc.player.isElytraFlying()) {
            if (!ElytraPlus.mc.player.onGround && this.InstantFly.getValue()) {
                if (!this.InstantFlyTimer.passed(500)) {
                    return;
                }
                this.InstantFlyTimer.reset();
                ElytraPlus.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)ElytraPlus.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            }
            return;
        }
        if (this.mode.getValue().equals(Mode.Packet)) {
            this.HandleNormalModeElytra(event);
        }
        else if (this.mode.getValue().equals(Mode.Superior)) {
            this.HandleImmediateModeElytra(event);
        }
    }

    public String getHudInfo() {
        return this.mode.getValue().toString();
    }

    public void onEnable() {
//        mc.player.setFlag(7, true);
        this.ElytraSlot = -1;
        if (this.onEnableEquipElytra.getValue() && ElytraPlus.mc.player != null && ElytraPlus.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA) {
            for (int i = 0; i < 44; ++i) {
                final ItemStack stacktemp = ElytraPlus.mc.player.inventory.getStackInSlot(i);
                if (!stacktemp.isEmpty() && stacktemp.getItem() == Items.ELYTRA) {
                    this.ElytraSlot = i;
                    break;
                }
            }
            if (this.ElytraSlot != -1) {
                final boolean l_HasArmorAtChest = ElytraPlus.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.AIR;
                ElytraPlus.mc.playerController.windowClick(ElytraPlus.mc.player.inventoryContainer.windowId, this.ElytraSlot, 0, ClickType.PICKUP, (EntityPlayer)ElytraPlus.mc.player);
                ElytraPlus.mc.playerController.windowClick(ElytraPlus.mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, (EntityPlayer)ElytraPlus.mc.player);
                if (l_HasArmorAtChest) {
                    ElytraPlus.mc.playerController.windowClick(ElytraPlus.mc.player.inventoryContainer.windowId, this.ElytraSlot, 0, ClickType.PICKUP, (EntityPlayer)ElytraPlus.mc.player);
                }
            }
        }
    }

    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
//        mc.player.setFlag(3, true);
        if (this.ElytraSlot != -1) {
            final boolean l_HasItem = !ElytraPlus.mc.player.inventory.getStackInSlot(this.ElytraSlot).isEmpty() || ElytraPlus.mc.player.inventory.getStackInSlot(this.ElytraSlot).getItem() != Items.AIR;
            ElytraPlus.mc.playerController.windowClick(ElytraPlus.mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, (EntityPlayer)ElytraPlus.mc.player);
            ElytraPlus.mc.playerController.windowClick(ElytraPlus.mc.player.inventoryContainer.windowId, this.ElytraSlot, 0, ClickType.PICKUP, (EntityPlayer)ElytraPlus.mc.player);
            if (l_HasItem) {
                ElytraPlus.mc.playerController.windowClick(ElytraPlus.mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, (EntityPlayer)ElytraPlus.mc.player);
            }
        }
    }

    public void HandleNormalModeElytra(final EventPlayerTravel p_Travel) {
        final double l_YHeight = ElytraPlus.mc.player.posY;
        if (l_YHeight <= this.CancelAtHeight.getValue()) {
            if (!this.SendMessage) {
                ChatUtil.NoSpam.sendMessage("WARNING, you must scaffold up or use fireworks, as YHeight <= CancelAtHeight!");
                this.SendMessage = true;
            }
            return;
        }
        final boolean isMoveKeyDown = ElytraPlus.mc.gameSettings.keyBindForward.isKeyDown() || ElytraPlus.mc.gameSettings.keyBindLeft.isKeyDown() || ElytraPlus.mc.gameSettings.keyBindRight.isKeyDown() || ElytraPlus.mc.gameSettings.keyBindBack.isKeyDown();
        final boolean l_CancelInWater = !ElytraPlus.mc.player.isInWater() && !ElytraPlus.mc.player.isInLava() && this.CancelInWater.getValue();
        if (!isMoveKeyDown) {
            this.AccelerationTimer.resetTimeSkipTo(-this.vAccelerationTimer.getValue());
        }
        else if (ElytraPlus.mc.player.rotationPitch <= this.RotationPitch.getValue() && l_CancelInWater) {
            if (this.Accelerate.getValue() && this.AccelerationTimer.passed(this.vAccelerationTimer.getValue())) {
                this.Accelerate();
            }
            return;
        }
        p_Travel.setCanceled(true);
        this.Accelerate();
    }

    public void HandleImmediateModeElytra(final EventPlayerTravel p_Travel) {
        p_Travel.setCanceled(true);
        final boolean moveForward = ElytraPlus.mc.gameSettings.keyBindForward.isKeyDown();
        final boolean moveBackward = ElytraPlus.mc.gameSettings.keyBindBack.isKeyDown();
        final boolean moveLeft = ElytraPlus.mc.gameSettings.keyBindLeft.isKeyDown();
        final boolean moveRight = ElytraPlus.mc.gameSettings.keyBindRight.isKeyDown();
        final boolean moveUp = ElytraPlus.mc.gameSettings.keyBindJump.isKeyDown();
        final boolean moveDown = ElytraPlus.mc.gameSettings.keyBindSneak.isKeyDown();
        final float moveForwardFactor = moveForward ? 1.0f : ((float)(moveBackward ? -1 : 0));
        float yawDeg = ElytraPlus.mc.player.rotationYaw;
        if (moveLeft && (moveForward || moveBackward)) {
            yawDeg -= 40.0f * moveForwardFactor;
        }
        else if (moveRight && (moveForward || moveBackward)) {
            yawDeg += 40.0f * moveForwardFactor;
        }
        else if (moveLeft) {
            yawDeg -= 90.0f;
        }
        else if (moveRight) {
            yawDeg += 90.0f;
        }
        if (moveBackward) {
            yawDeg -= 180.0f;
        }
        final float yaw = (float)Math.toRadians(yawDeg);
        final double motionAmount = Math.sqrt(ElytraPlus.mc.player.motionX * ElytraPlus.mc.player.motionX + ElytraPlus.mc.player.motionZ * ElytraPlus.mc.player.motionZ);
        if (moveUp || moveForward || moveBackward || moveLeft || moveRight) {
            if (moveUp && motionAmount > 1.0) {
                if (ElytraPlus.mc.player.motionX == 0.0 && ElytraPlus.mc.player.motionZ == 0.0) {
                    ElytraPlus.mc.player.motionY = this.UpSpeed.getValue();
                }
                else {
                    final double calcMotionDiff = motionAmount * 0.008;
                    final EntityPlayerSP player = ElytraPlus.mc.player;
                    player.motionY += calcMotionDiff * 3.2;
                    final EntityPlayerSP player2 = ElytraPlus.mc.player;
                    player2.motionX -= -MathHelper.sin(yaw) * calcMotionDiff;
                    final EntityPlayerSP player3 = ElytraPlus.mc.player;
                    player3.motionZ -= MathHelper.cos(yaw) * calcMotionDiff;
                    final EntityPlayerSP player4 = ElytraPlus.mc.player;
                    player4.motionX *= 0.9900000095367432;
                    final EntityPlayerSP player5 = ElytraPlus.mc.player;
                    player5.motionY *= 0.9800000190734863;
                    final EntityPlayerSP player6 = ElytraPlus.mc.player;
                    player6.motionZ *= 0.9900000095367432;
                }
            }
            else {
                ElytraPlus.mc.player.motionX = -MathHelper.sin(yaw) * (double)(this.speed.getValue() / 10.0f);
                ElytraPlus.mc.player.motionY = -this.GlideSpeed.getValue();
                ElytraPlus.mc.player.motionZ = MathHelper.cos(yaw) * (double)(this.speed.getValue() / 10.0f);
                RotationManager.addRotations(yaw, ElytraPlus.mc.player.rotationPitch);
            }
        }
        else {
            ElytraPlus.mc.player.motionX = 0.0;
            ElytraPlus.mc.player.motionY = 0.0;
            ElytraPlus.mc.player.motionZ = 0.0;
        }
        if (moveDown) {
            ElytraPlus.mc.player.motionY = -this.DownSpeed.getValue();
        }
    }

    public void Accelerate() {
        if (this.AccelerationResetTimer.passed(this.vAccelerationTimer.getValue())) {
            this.AccelerationResetTimer.reset();
            this.AccelerationTimer.reset();
            this.SendMessage = false;
        }
        final float speedacc = this.speed.getValue() / 10.0f;
        final double[] dir = MathUtil.directionSpeed(speedacc);
        ElytraPlus.mc.player.motionY = -this.GlideSpeed.getValue();
        if (ElytraPlus.mc.player.movementInput.moveStrafe != 0.0f || ElytraPlus.mc.player.movementInput.moveForward != 0.0f) {
            ElytraPlus.mc.player.motionX = dir[0];
            ElytraPlus.mc.player.motionZ = dir[1];
            final EntityPlayerSP player = ElytraPlus.mc.player;
            player.motionX -= ElytraPlus.mc.player.motionX * (Math.abs(ElytraPlus.mc.player.rotationPitch) + 90.0f) / 90.0 - ElytraPlus.mc.player.motionX;
            final EntityPlayerSP player2 = ElytraPlus.mc.player;
            player2.motionZ -= ElytraPlus.mc.player.motionZ * (Math.abs(ElytraPlus.mc.player.rotationPitch) + 90.0f) / 90.0 - ElytraPlus.mc.player.motionZ;
        }
        else {
            ElytraPlus.mc.player.motionX = 0.0;
            ElytraPlus.mc.player.motionZ = 0.0;
        }
        if (ElytraPlus.mc.gameSettings.keyBindSneak.isKeyDown()) {
            ElytraPlus.mc.player.motionY = -this.DownSpeed.getValue();
        }
    }

    private enum Mode
    {
        Superior,
        Packet;
    }
}
