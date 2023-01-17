package me.windyteam.kura.module.modules.movement

import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.event.events.entity.MoveEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.ModuleManager
import me.windyteam.kura.module.modules.combat.HoleSnap
import me.windyteam.kura.module.modules.player.Timer
import me.windyteam.kura.utils.TimerUtils
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.mc.ChatUtil
import me.windyteam.kura.utils.player.MovementUtil
import net.minecraft.init.MobEffects
import net.minecraft.network.Packet
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.network.play.server.SPacketExplosion
import net.minecraft.network.play.server.SPacketPlayerPosLook
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

@Module.Info(name = "Speed", category = Category.MOVEMENT)
class Speed : Module() {
    //    public DoubleSetting boostVal = dsetting("BoostValue", 0.5, 0.1, 2).m(mode, Mode.BYPASS).m(boostMode, BoostMode.Normal);
    //    public IntegerSetting boostRange = isetting("BoostRange", 4, 1, 8).m(mode, Mode.BYPASS).m(boostMode, BoostMode.Normal);
    private var damageBoost = bsetting("DamageBoost", true)
    private var boostDelay = isetting("BoostDelay", 750, 1, 3000).b(damageBoost)
    private var longjump = bsetting("TryLongJump", false)
    private var lagCoolDown = dsetting("LagCoolDown", 2.2, 1.0, 8.0).b(longjump)
    private var jumpStage = isetting("JumpStage", 6, 1, 20).b(longjump)
    private var jumpSec = isetting("JumpCoolDown", 3, 1, 10).b(longjump)
    private var motionJump = bsetting("MotionJump", false).b(longjump)
    private var randomBoost = bsetting("RandomBoost", false)
    private var randomBoostDebug = bsetting("RandomBoostDebug", false).b(randomBoost)
    private var lavaBoost = bsetting("LavaBoost", true)
    private var potion = bsetting("Potion", true)
    private var speedInWater = bsetting("SpeedInWater", true)
    private var bbtt = bsetting("2b2t", false)
    private var strictBoost = bsetting("StrictBoost", false).b(bbtt).b(damageBoost)
    private var useTimer = bsetting("UseTimer", true)
    private var decimal = DecimalFormat()
    private var lagBackCoolDown = TimerUtils()
    private var boostTimer = TimerUtils()
    private var jumpDelay = TimerUtils()
    private var rdBoostTimer = TimerUtils()
    private var boostFactor = 6f
    private var detectionTime: Long = 0
    private var lagDetected = false
    private var inCoolDown = false
    private var checkCoolDown = false
    private var readyStage = 0
    private var warn = false
    private var boostSpeed = 0.0
    private var boostSpeed2 = 0.0
    private var boostRangeVec: Vec3d? = null
    private var lastDist = 0.0
    var stage = 1
    private var level = 1
    private var moveSpeed = 0.0
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPacketReceive(event: PacketEvents.Receive) {
        if (fullNullCheck()) {
            return
        }
        if (event.getPacket<Packet<*>>() is SPacketPlayerPosLook) {
            lastDist = 0.0
            moveSpeed = Math.min(baseMoveSpeed, baseMoveSpeed)
            stage = 2

            //RDBoost
            detectionTime = System.currentTimeMillis()
            lagDetected = true
            rdBoostTimer.reset()
            boostFactor = 8f

            //JumpBoost
            if (longjump.value) {
                readyStage = 0
                inCoolDown = true
                if (!checkCoolDown) {
                    lagBackCoolDown.reset()
                    checkCoolDown = true
                }
            }
        }
        if (event.getPacket<Packet<*>>() is SPacketExplosion) {
            //boostSpeed = Math.hypot(((SPacketExplosion) event.getPacket()).motionX, ((SPacketExplosion) event.getPacket()).motionZ);
            boostRangeVec = Vec3d(
                (event.getPacket<Packet<*>>() as SPacketExplosion).posX,
                (event.getPacket<Packet<*>>() as SPacketExplosion).posY,
                (event.getPacket<Packet<*>>() as SPacketExplosion).posZ
            )
        }
        if (event.getPacket<Packet<*>>() is SPacketEntityVelocity) {
            if ((event.getPacket<Packet<*>>() as SPacketEntityVelocity).getEntityID() == mc.player.getEntityId()) {
                if (longjump.value) {
                    readyStage++
                }
                boostSpeed = Math.hypot(
                    ((event.getPacket<Packet<*>>() as SPacketEntityVelocity).motionX / 8000f).toDouble(),
                    ((event.getPacket<Packet<*>>() as SPacketEntityVelocity).motionZ / 8000f).toDouble()
                )
                boostSpeed2 = boostSpeed
            }
        }
    }

    val baseMoveSpeed: Double
        get() {
            var n = 0.2873
            if (mc.player.isPotionActive(MobEffects.SPEED) && potion.value) {
                n *= 1.0 + 0.2 * (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED))!!.amplifier + 1)
            }
            return n
        }

    @SubscribeEvent
    fun onMotion(event: MotionUpdateEvent.Tick) {
        if (fullNullCheck()) {
            return
        }
        if (ModuleManager.getModuleByClass(HoleSnap::class.java).isEnabled) {
            return
        }
        try {
            if (lagBackCoolDown.passed(decimal.format(lagCoolDown.value * 1000).toDouble())) {
                checkCoolDown = false
                inCoolDown = false
                lagBackCoolDown.reset()
            }
            if (System.currentTimeMillis() - detectionTime > 3182) {
                lagDetected = false
            }
            if (useTimer.value) {
                mc.timer.tickLength =
                    if (ModuleManager.getModuleByClass(Timer::class.java).isEnabled) 50f / mc.timer.tickLength else 45.955883f
            }
            if (event.stage == 1) {
                lastDist =
                    Math.sqrt((mc.player.posX - mc.player.prevPosX) * (mc.player.posX - mc.player.prevPosX) + (mc.player.posZ - mc.player.prevPosZ) * (mc.player.posZ - mc.player.prevPosZ))
            }
        } catch (ignored: NumberFormatException) {
        }
    }

    override fun onEnable() {
        if (mc.player == null) {
            disable()
            return
        }
        boostSpeed = 0.0
        boostSpeed2 = 0.0
        jumpDelay.reset()
        lagBackCoolDown.reset()
        boostTimer.reset()
        readyStage = 0
        warn = false
        moveSpeed = baseMoveSpeed
        if (ModuleManager.getModuleByName("LongJump").isEnabled) {
            ModuleManager.getModuleByName("LongJump").disable()
        }
        decimal.applyPattern("0.0")
    }

    @SubscribeEvent
    fun onMove(event: MoveEvent) {
        if (fullNullCheck()) {
            return
        }
        if (!speedInWater.value) {
            if (shouldReturn()) {
                return
            }
        }
        //        mc.player.stepHeight = ModuleManager.getModuleByClass(Step.class).isEnabled() ? Step.INSTANCE.height.getValue().floatValue() : 0.0f;
        if (!speedInWater.value) {
            if (shouldReturn()) {
                return
            }
        }
        if (mc.player.onGround) {
            level = 2
        }
        //        if (this.step.getValue()) {
//            mc.player.stepHeight = ModuleManager.getModuleByClass(Step.class).isEnabled() ? Step.INSTANCE.height.getValue().floatValue() : 0.6f;
//        }
        if (round(mc.player.posY - mc.player.posY.toInt(), 3) == round(0.138, 3)) {
            mc.player.motionY -= 0.07
            event.Y -= 0.08316090325960147
            mc.player.posY -= 0.08316090325960147
        }
        if (level != 1 || mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f) {
            if (level == 2) {
                level = 3
                if (EntityUtil.isMoving()) {
                    if (!mc.player.isInLava && mc.player.onGround) {
                        mc.player.motionY = 0.4
                        event.y = 0.4
                    }
                    moveSpeed *= if (bbtt.value) {
                        1.433
                    } else {
                        if (!mc.player.isSneaking) {
                            1.7103
                        } else {
                            1.433
                        }
                    }
                    //this.moveSpeed *= bbtt.getValue() ? 1.38 : 1.7103;
                }
            } else if (level == 3) {
                level = 4
                moveSpeed = lastDist - 0.6553 * (lastDist - baseMoveSpeed + 0.04)
            } else {
                if (mc.player.onGround && (mc.world.getCollisionBoxes(
                        mc.player,
                        mc.player.boundingBox.offset(0.0, mc.player.motionY, 0.0)
                    ).size > 0 || mc.player.collidedVertically)
                ) {
                    level = 1
                }
                moveSpeed = lastDist - lastDist / 201.0
            }
        } else {
            level = 2
            moveSpeed = 1.418 * baseMoveSpeed
        }
        if (damageBoost.value && EntityUtil.isMoving()) {
            if (boostSpeed2 != 0.0) {
                if (boostTimer.passed(boostDelay.value)) {
                    moveSpeed = if (bbtt.value) {
                        if (strictBoost.value) {
                            Math.max((moveSpeed + 1 / 10f) / 1.5f, baseMoveSpeed)
                        } else {
                            boostSpeed2
                        }
                    } else {
                        boostSpeed2 //Math.max(moveSpeed + Math.abs(moveSpeed - boostSpeed2), getBaseMoveSpeed());
                    }
                    boostTimer.reset()
                }
                boostSpeed2 = 0.0
            }
        }
        if (randomBoost.value && rdBoostTimer.passed(3500) && !lagDetected && EntityUtil.isMoving() && mc.player.onGround) {
            moveSpeed += moveSpeed / boostFactor
            if (randomBoostDebug.value) {
                if (boostFactor > 6f) {
                    ChatUtil.NoSpam.sendMessage("RDBoost Speed Strict")
                } else {
                    ChatUtil.NoSpam.sendMessage("RDBoost Speed")
                }
            }
            boostFactor = 6f
            rdBoostTimer.reset()
        }
        if (longjump.value && readyStage > 0 && readyStage >= jumpStage.value && !inCoolDown && jumpDelay.passed(jumpSec.value / 1000f) && EntityUtil.isMoving()) {
            if (!motionJump.value) {
                moveSpeed = moveSpeed * (jumpStage.value / 10f)
            } else {
                val v = Math.abs(moveSpeed - boostSpeed)
                //ChatUtil.sendMessage(v + "");
                MovementUtil.motionJump()
                mc.player.motionY *= 1.02
                mc.player.motionY *= 1.13
                mc.player.motionY *= 1.27
                //mc.player.motionY *= 1.301;
                moveSpeed = moveSpeed + v
                //moveSpeed = boostSpeed;
            }
            ChatUtil.sendMessage("Boost")
            jumpDelay.reset()
            readyStage = 0
        }
        moveSpeed = Math.max(moveSpeed, baseMoveSpeed)
        //        mc.player.stepHeight = ModuleManager.getModuleByClass(Step.class).isEnabled() ? Step.INSTANCE.height.getValue().floatValue() : 0.6f;
        if (!mc.player.isInLava) {
            event.setSpeed(moveSpeed)
        } else if (lavaBoost.value && mc.player.isInLava) {
            event.x = event.x * 3.1
            event.z = event.z * 3.1
            if (mc.gameSettings.keyBindJump.isKeyDown) {
                event.y = event.y * 3
            }
        }
        if (mc.player.movementInput.moveForward == 0.0f && mc.player.movementInput.moveStrafe == 0.0f) {
            event.X = 0.0
            event.Z = 0.0
        }
    }

    fun shouldReturn(): Boolean {
        return mc.player.isInLava || mc.player.isInWater || isDisabled || mc.player.isInWeb
    }

    override fun onDisable() {
        moveSpeed = 0.0
        stage = 2
        if (mc.player != null) {
            mc.player.stepHeight = 0.6f
            mc.timer.tickLength = 50.0f
        }
    }

    companion object {
        var INSTANCE = Speed()
        fun round(n: Double, n2: Int): Double {
            require(n2 >= 0)
            return BigDecimal(n).setScale(n2, RoundingMode.HALF_UP).toDouble()
        }
    }
}