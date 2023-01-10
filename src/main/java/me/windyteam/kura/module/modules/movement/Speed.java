package me.windyteam.kura.module.modules.movement;

import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.event.events.entity.MotionUpdateEvent;
import me.windyteam.kura.event.events.entity.MoveEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.module.modules.combat.HoleSnap;
import me.windyteam.kura.module.modules.player.Timer;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.setting.DoubleSetting;
import me.windyteam.kura.setting.IntegerSetting;
import me.windyteam.kura.utils.TimerUtils;
import me.windyteam.kura.utils.entity.EntityUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.utils.player.MovementUtil;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

@Module.Info(name = "Speed", category = Category.MOVEMENT)
public class Speed extends Module {
    public static Speed INSTANCE = new Speed();
    //public DoubleSetting boostVal = dsetting("BoostValue", 0.5, 0.1, 2).m(mode, Mode.BYPASS).m(boostMode, BoostMode.Normal);
    //public IntegerSetting boostRange = isetting("BoostRange", 4, 1, 8).m(mode, Mode.BYPASS).m(boostMode, BoostMode.Normal);
    public BooleanSetting damageBoost = bsetting("DamageBoost", true);
    public IntegerSetting boostDelay = isetting("BoostDelay", 750, 1, 3000).b(damageBoost);
    public BooleanSetting longjump = bsetting("TryLongJump", false);
    public DoubleSetting lagCoolDown = dsetting("LagCoolDown", 2.2, 1, 8).b(longjump);
    public IntegerSetting jumpStage = isetting("JumpStage", 6, 1, 20).b(longjump);
    public IntegerSetting jumpSec = isetting("JumpCoolDown", 3, 1, 10).b(longjump);
    public BooleanSetting motionJump = bsetting("MotionJump", false).b(longjump);
    public BooleanSetting randomBoost = bsetting("RandomBoost", false);
    public BooleanSetting randomBoostDebug = bsetting("RandomBoostDebug", false).b(randomBoost);
    public BooleanSetting lavaBoost = bsetting("LavaBoost", true);
    public BooleanSetting potion = bsetting("Potion", true);
//    public BooleanSetting step = bsetting("SetStep", true);
    public BooleanSetting SpeedInWater = bsetting("SpeedInWater", true);
    public BooleanSetting bbtt = bsetting("2b2t", false);
    public BooleanSetting strictBoost = bsetting("StrictBoost", false).b(bbtt).b(damageBoost);
    public BooleanSetting useTimer = bsetting("UseTimer", true);
    public DecimalFormat decimal = new DecimalFormat();
    public TimerUtils lagBackCoolDown = new TimerUtils();
    public TimerUtils boostTimer = new TimerUtils();
    public TimerUtils jumpDelay = new TimerUtils();
    public TimerUtils rdBoostTimer = new TimerUtils();
    public float boostFactor = 6f;
    public long detectionTime;
    public boolean lagDetected;
    public boolean inCoolDown = false;
    public boolean checkCoolDown = false;
    public int readyStage = 0;
    public boolean warn = false;
    public double boostSpeed;
    public double boostSpeed2;
    public Vec3d boostRangeVec;
    public double lastDist;
    public int stage = 1;
    public int level = 1;
    public double moveSpeed;

    public static double round(double n, int n2) {
        if (n2 < 0) {
            throw new IllegalArgumentException();
        }
        return new BigDecimal(n).setScale(n2, RoundingMode.HALF_UP).doubleValue();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketReceive(PacketEvents.Receive event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            lastDist = 0.0;
            moveSpeed = Math.min(getBaseMoveSpeed(), getBaseMoveSpeed());
            stage = 2;

            //RDBoost
            detectionTime = System.currentTimeMillis();
            lagDetected = true;
            rdBoostTimer.reset();
            boostFactor = 8f;

            //JumpBoost
            if (longjump.getValue()) {
                readyStage = 0;
                inCoolDown = true;
                if (!checkCoolDown) {
                    lagBackCoolDown.reset();
                    checkCoolDown = true;
                }
            }
        }
        if (event.getPacket() instanceof SPacketExplosion) {
            //boostSpeed = Math.hypot(((SPacketExplosion) event.getPacket()).motionX, ((SPacketExplosion) event.getPacket()).motionZ);
            boostRangeVec = new Vec3d(((SPacketExplosion) event.getPacket()).posX, ((SPacketExplosion) event.getPacket()).posY, ((SPacketExplosion) event.getPacket()).posZ);
        }
        if (event.getPacket() instanceof SPacketEntityVelocity) {
            if (((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId()) {
                if (longjump.getValue()) {
                    readyStage++;
                }
                boostSpeed = Math.hypot(((SPacketEntityVelocity) event.getPacket()).motionX / 8000f, ((SPacketEntityVelocity) event.getPacket()).motionZ / 8000f);
                boostSpeed2 = boostSpeed;
            }
        }
    }

    public double getBaseMoveSpeed() {
        double n = 0.2873;
        if (mc.player.isPotionActive(MobEffects.SPEED) && potion.getValue()) {
            n *= 1.0 + 0.2 * (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier() + 1);
        }
        return n;
    }

    @SubscribeEvent
    public void onMotion(MotionUpdateEvent.Tick event) {
        if (fullNullCheck()) {
            return;
        }
        if (ModuleManager.getModuleByClass(HoleSnap.class).isEnabled()) {
            return;
        }
        try {
            if (lagBackCoolDown.passed(Double.parseDouble(decimal.format(lagCoolDown.getValue() * 1000)))) {
                checkCoolDown = false;
                inCoolDown = false;
                lagBackCoolDown.reset();
            }
            if (System.currentTimeMillis() - detectionTime > 3182) {
                lagDetected = false;
            }
            if (useTimer.getValue()) {
                mc.timer.tickLength = ModuleManager.getModuleByClass(Timer.class).isEnabled() ? 50f / mc.timer.tickLength : 45.955883f;
            }
            if (event.getStage() == 1) {
                lastDist = Math.sqrt((mc.player.posX - mc.player.prevPosX) * (mc.player.posX - mc.player.prevPosX) + (mc.player.posZ - mc.player.prevPosZ) * (mc.player.posZ - mc.player.prevPosZ));
            }
        } catch (NumberFormatException ignored) {
        }
    }

    @Override
    public void onEnable() {
        if (mc.player == null) {
            disable();
            return;
        }
        boostSpeed = 0;
        boostSpeed2 = 0;
        jumpDelay.reset();
        lagBackCoolDown.reset();
        boostTimer.reset();
        readyStage = 0;
        warn = false;
        moveSpeed = getBaseMoveSpeed();
        if (ModuleManager.getModuleByName("LongJump").isEnabled()) {
            ModuleManager.getModuleByName("LongJump").disable();
        }
        decimal.applyPattern("0.0");
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (!SpeedInWater.getValue()) {
            if (shouldReturn()) {
                return;
            }
        }
//        mc.player.stepHeight = ModuleManager.getModuleByClass(Step.class).isEnabled() ? Step.INSTANCE.height.getValue().floatValue() : 0.0f;

        if (!SpeedInWater.getValue()) {
            if (this.shouldReturn()) {
                return;
            }
        }
        if (mc.player.onGround) {
            this.level = 2;
        }
//        if (this.step.getValue()) {
//            mc.player.stepHeight = ModuleManager.getModuleByClass(Step.class).isEnabled() ? Step.INSTANCE.height.getValue().floatValue() : 0.6f;
//        }
        if (round(mc.player.posY - (int) mc.player.posY, 3) == round(0.138, 3)) {
            mc.player.motionY -= 0.07;
            event.Y -= 0.08316090325960147;
            mc.player.posY -= 0.08316090325960147;
        }
        if (this.level != 1 || (mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f)) {
            if (this.level == 2) {
                this.level = 3;
                if (EntityUtil.isMoving()) {
                    if (!mc.player.isInLava() && mc.player.onGround) {
                        mc.player.motionY = 0.4;
                        event.setY(0.4);
                    }
                    if (bbtt.getValue()) {
                        moveSpeed *= 1.433;
                    } else {
                        if (!mc.player.isSneaking()) {
                            moveSpeed *= 1.7103;
                        } else {
                            moveSpeed *= 1.433;
                        }
                    }
                    //this.moveSpeed *= bbtt.getValue() ? 1.38 : 1.7103;
                }
            } else if (this.level == 3) {
                this.level = 4;
                this.moveSpeed = this.lastDist - 0.6553 * (this.lastDist - this.getBaseMoveSpeed() + 0.04);
            } else {
                if (mc.player.onGround && (mc.world.getCollisionBoxes(mc.player, mc.player.boundingBox.offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically)) {
                    this.level = 1;
                }
                this.moveSpeed = this.lastDist - this.lastDist / 201.0;
            }
        } else {
            this.level = 2;
            this.moveSpeed = 1.418 * this.getBaseMoveSpeed();
        }
        if (damageBoost.getValue() && EntityUtil.isMoving()) {
            if (boostSpeed2 != 0) {
                if (boostTimer.passed(boostDelay.getValue())) {
                    if (bbtt.getValue()) {
                        if (strictBoost.getValue()) {
                            moveSpeed = Math.max((moveSpeed + (1 / 10f)) / 1.5f, getBaseMoveSpeed());
                        } else {
                            moveSpeed = boostSpeed2;
                        }
                    } else {
                        moveSpeed = boostSpeed2;//Math.max(moveSpeed + Math.abs(moveSpeed - boostSpeed2), getBaseMoveSpeed());
                    }
                    boostTimer.reset();
                }
                boostSpeed2 = 0;
            }
        }
        if (randomBoost.getValue() && rdBoostTimer.passed(3500) && !lagDetected && EntityUtil.isMoving() && mc.player.onGround) {
            moveSpeed += moveSpeed / boostFactor;
            if (randomBoostDebug.getValue()) {
                if (boostFactor > 6f) {
                    ChatUtil.NoSpam.sendMessage("RDBoost Speed Strict");
                } else {
                    ChatUtil.NoSpam.sendMessage("RDBoost Speed");
                }
            }
            boostFactor = 6f;
            rdBoostTimer.reset();
        }
        if (longjump.getValue() && readyStage > 0 && readyStage >= jumpStage.getValue() && !inCoolDown && jumpDelay.passed(jumpSec.getValue() / 1000f) && EntityUtil.isMoving()) {
            if (!motionJump.getValue()) {
                moveSpeed = moveSpeed * (jumpStage.getValue() / 10f);
            } else {
                double v = Math.abs(moveSpeed - boostSpeed);
                //ChatUtil.sendMessage(v + "");
                MovementUtil.motionJump();
                mc.player.motionY *= 1.02;
                mc.player.motionY *= 1.13;
                mc.player.motionY *= 1.27;
                //mc.player.motionY *= 1.301;
                moveSpeed = moveSpeed + v;
                //moveSpeed = boostSpeed;
            }
            ChatUtil.sendMessage("Boost");
            jumpDelay.reset();
            readyStage = 0;
        }
        this.moveSpeed = Math.max(this.moveSpeed, this.getBaseMoveSpeed());
//        mc.player.stepHeight = ModuleManager.getModuleByClass(Step.class).isEnabled() ? Step.INSTANCE.height.getValue().floatValue() : 0.6f;
        if (!mc.player.isInLava()) {
            event.setSpeed(moveSpeed);
        } else if (lavaBoost.getValue() && mc.player.isInLava()) {
            event.setX(event.getX() * 3.1);
            event.setZ(event.getZ() * 3.1);
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                event.setY(event.getY() * 3);
            }
        }
        if (mc.player.movementInput.moveForward == 0.0f && mc.player.movementInput.moveStrafe == 0.0f) {
            event.X = 0.0;
            event.Z = 0.0;
        }
    }

    public boolean shouldReturn() {
        return mc.player.isInLava() || mc.player.isInWater() || isDisabled() || mc.player.isInWeb;
    }

    @Override
    public void onDisable() {
        moveSpeed = 0.0;
        stage = 2;
        if (mc.player != null) {
            mc.player.stepHeight = 0.6f;
            mc.timer.tickLength = 50.0f;
        }
    }
}