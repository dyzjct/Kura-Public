package me.windyteam.kura.module.modules.movement;

import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.event.events.entity.MotionUpdateEvent;
import me.windyteam.kura.event.events.entity.MoveEvent;
import me.windyteam.kura.event.events.player.JumpEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.setting.ModeSetting;
import me.windyteam.kura.utils.TimerUtils;
import me.windyteam.kura.utils.entity.EntityUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

@Module.Info(name = "Strafe", category = Category.MOVEMENT)
public class Strafe extends Module {
    private final ModeSetting<?> Mode = msetting("Mode", mode.NORMAL);
    public BooleanSetting boost = bsetting("DamageBoost", false);
    public BooleanSetting randomBoost = bsetting("RandomBoost", false);
    public TimerUtils rdBoostTimer = new TimerUtils();
    public float boostFactor = 4f;
    public long detectionTime;
    public boolean lagDetected;
    public double boostSpeed;
    public int stage = 1;
    private double lastDist;
    private double moveSpeed;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketReceive(PacketEvents.Receive event) {
        if (fullNullCheck()) {
            return;
        }
        if (ModuleManager.getModuleByClass(ElytraPlus.class).isEnabled()){
            return;
        }
        if (event.getPacket() instanceof SPacketEntityVelocity) {
            if (((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId()) {
                if (ModuleManager.getModuleByClass(Speed.class).isDisabled()) {
                    boostSpeed = Math.hypot(((SPacketEntityVelocity) event.getPacket()).motionX / 8000f, ((SPacketEntityVelocity) event.getPacket()).motionZ / 8000f);
                }
            }
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            detectionTime = System.currentTimeMillis();
            lagDetected = true;
            rdBoostTimer.reset();
            boostFactor = 6f;
        }
    }

    @SubscribeEvent
    public void onPreMotion(MotionUpdateEvent.Tick event) {
        if (fullNullCheck()) {
            return;
        }
        if (ModuleManager.getModuleByClass(ElytraPlus.class).isEnabled()){
            return;
        }
        if (System.currentTimeMillis() - detectionTime > 3182) {
            lagDetected = false;
        }
        if (event.getStage() == 1) {
            this.lastDist = Math.sqrt((mc.player.posX - mc.player.prevPosX) * (mc.player.posX - mc.player.prevPosX) + (mc.player.posZ - mc.player.prevPosZ) * (mc.player.posZ - mc.player.prevPosZ));
        }
    }

    @SubscribeEvent
    public void onJump(JumpEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (ModuleManager.getModuleByClass(ElytraPlus.class).isEnabled()){
            return;
        }
        if (this.shouldReturn() && !mc.player.isInWater() && !mc.player.isInLava()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (ModuleManager.getModuleByClass(ElytraPlus.class).isEnabled()){
            return;
        }
        final double motionY = 0;
        if (this.shouldReturn() && !mc.player.isInWater() && !mc.player.isInLava()) {
            if (mc.player.onGround) {
                this.stage = 2;
            }
            switch (this.stage) {
                case 0: {
                    ++this.stage;
                    this.lastDist = 0.0;
                    break;
                }
                case 2: {
                    if (mc.player.onGround && mc.gameSettings.keyBindJump.isKeyDown()) {
                        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                            event.setY(mc.player.motionY = motionY);
                            this.moveSpeed *= (this.Mode.getValue().equals(mode.NORMAL) ? 1.7 : 2.149);
                        }
                    }
                    break;
                }
                case 3: {
                    this.moveSpeed = this.lastDist - (this.Mode.getValue().equals(mode.NORMAL) ? 0.6896 : 0.795) * (this.lastDist - this.getBaseMoveSpeed());
                    break;
                }
                default: {
                    if ((mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) && this.stage > 0) {
                        this.stage = ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) ? 1 : 0);
                    }
                    this.moveSpeed = this.lastDist - this.lastDist / 159.0;
                    break;
                }
            }
            if (boost.getValue() && boostSpeed != 0 && EntityUtil.isMoving()) {
                //moveSpeed = moveSpeed + Math.abs(moveSpeed - boostSpeed);
                moveSpeed = boostSpeed;
                boostSpeed = 0;
            }
            if (randomBoost.getValue() && rdBoostTimer.passed(3500) && !lagDetected && EntityUtil.isMoving() && mc.player.onGround) {
                moveSpeed += moveSpeed / boostFactor;
                ChatUtil.sendMessage("RDBoost");
                boostFactor = 4f;
                rdBoostTimer.reset();
            }
            if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.player.onGround) {
                this.moveSpeed = this.getBaseMoveSpeed();
            } else {
                this.moveSpeed = Math.max(this.moveSpeed, this.getBaseMoveSpeed());
            }
            if (mc.player.movementInput.moveForward == 0.0 && mc.player.movementInput.moveStrafe == 0.0) {
                event.setSpeed(0);
            } else if (mc.player.movementInput.moveForward != 0.0 && mc.player.movementInput.moveStrafe != 0.0) {
                mc.player.movementInput.moveForward *= Math.sin(0.7853981633974483);
                mc.player.movementInput.moveStrafe *= Math.cos(0.7853981633974483);
            }
            event.setX((mc.player.movementInput.moveForward * this.moveSpeed * -Math.sin(Math.toRadians(mc.player.rotationYaw)) + mc.player.movementInput.moveStrafe * this.moveSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw))) * (Mode.getValue().equals(mode.NORMAL) ? 0.993 : 0.99));
            event.setZ((mc.player.movementInput.moveForward * this.moveSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw)) - mc.player.movementInput.moveStrafe * this.moveSpeed * -Math.sin(Math.toRadians(mc.player.rotationYaw))) * (Mode.getValue().equals(mode.NORMAL) ? 0.993 : 0.99));
            ++this.stage;
        }
    }

    @Override
    public String getHudInfo() {
        return this.Mode.getValue().equals(mode.NORMAL) ? "Normal" : "Strict";
    }

    public double getBaseMoveSpeed() {
        double n = 0.2873;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            n *= 1.0 + 0.2 * (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier() + 1);
        }
        return n;
    }

    private boolean shouldReturn() {
        return !ModuleManager.getModuleByClass(Speed.class).isEnabled() && !ModuleManager.getModuleByClass(LongJump.class).isEnabled();
    }

    public enum mode {
        STRICT,
        NORMAL
    }
}