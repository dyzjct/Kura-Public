package me.windyteam.kura.module.modules.movement

import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.event.events.entity.MoveEvent
import me.windyteam.kura.event.events.entity.PushEvent
import me.windyteam.kura.event.events.render.InsideBlockRenderEvent
import me.windyteam.kura.event.events.render.RenderOverlayEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.ModuleManager
import me.windyteam.kura.setting.Setting
import me.windyteam.kura.utils.MathUtil
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.math.RandomUtil
import me.windyteam.kura.utils.mc.ChatUtil
import net.minecraft.client.gui.GuiDownloadTerrain
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketConfirmTeleport
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation
import net.minecraft.network.play.server.SPacketPlayerPosLook
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Module.Info(name = "PacketFlyRewrite", category = Category.MOVEMENT)
class PacketFlyRewrite : Module() {
    private val posLooks: MutableMap<Int, TimeVec> = ConcurrentHashMap()
    private var teleportID = 0
    private var packets = ArrayList<CPacketPlayer>()
    private var mode = msetting("Mode", Modes.Factor)
    private var phase = msetting("PhaseMode", PhaseMode.Full)
    private var type = msetting("Type", Types.LimitJitter)
    private var TRStep: Setting<Boolean> = bsetting("ToggleRStep", false)
    private var betterResponse = bsetting("BetterResponse", false)
    private var confirmtp = bsetting("ConfirmTeleport", true)
    private var AntiKick: Setting<Boolean> = bsetting("AntiKick", true)
    private var lessReduction = bsetting("LessReduction", false)
    private var Reduction: Setting<Double> = dsetting("Reduction", 1.0, 0.1, 3.0).m(mode, Modes.Fast)
    private var FactorValue = fsetting("Factor", 1f, 1f, 5f)
    private var XZSpeed = fsetting("XZSpeed", 1f, 0f, 5f).m2(mode, Modes.Setback).m2(mode, Modes.Fast)
    private var YSpeedValue = fsetting("YSpeed", 1f, 0f, 3f).m2(mode, Modes.Setback).m2(mode, Modes.Fast)
    private var valueBounded = isetting("BoundedVal", 2, 1, 255)
    private var firstStart = false
    private var otherids = 0
    protected var lastFactor = 0f
    protected fun clearValues() {
        lastFactor = 1.0f
        otherids = 0
        teleportID = 0
        packets.clear()
        posLooks!!.clear()
    }

    override fun onEnable() {
        if (fullNullCheck()) {
            return
        }
        if (TRStep.value) {
            ModuleManager.getModuleByClass(ReverseStep::class.java).disable()
        }
        if (mc.isSingleplayer) {
            ChatUtil.sendMessage(TextFormatting.RED.toString() + "Can't enable PacketFly in SinglePlayer!")
            disable()
        }
        firstStart = true
        clearValues()
    }

    override fun onDisable() {
        if (fullNullCheck()) {
            return
        }
        if (TRStep.value) {
            ModuleManager.getModuleByClass(ReverseStep::class.java).enable()
        }
        otherids = 0
        packets.clear()
    }

    override fun getHudInfo(): String {
        return "" + TextFormatting.AQUA + mode.value
    }

    @SubscribeEvent
    fun onClientDisconnect(event: ClientDisconnectionFromServerEvent?) {
        disable()
    }

    @SubscribeEvent
    fun onPacketReceive(event: PacketEvents.Receive) {
        if (fullNullCheck()) {
            return
        }
        if (event.getPacket<Packet<*>>() is SPacketPlayerPosLook) {
            val packet = event.getPacket<SPacketPlayerPosLook>()
            if (mc.player.isEntityAlive && mode.value !== Modes.Setback && mc.world.isBlockLoaded(
                    BlockPos(mc.player),
                    false
                ) && mc.currentScreen !is GuiDownloadTerrain
            ) {
                val vec = posLooks!!.remove(packet.getTeleportId())
                if (vec != null && vec.x == packet.getX() && vec.y == packet.getY() && vec.z == packet.getZ()) {
                    event.isCanceled = true
                    return
                }
                teleportID = packet.getTeleportId()
                if (betterResponse.value) {
                    doBetterResponse(event.getPacket())
                }
            }
        }
    }

    private fun doBetterResponse(event: SPacketPlayerPosLook) {
        var x = event.x
        var z = event.z
        var yaw = event.yaw
        var pitch = event.pitch
        if (event.flags.contains(SPacketPlayerPosLook.EnumFlags.X)) {
            x += mc.player.posX
        }
        if (event.flags.contains(SPacketPlayerPosLook.EnumFlags.Z)) {
            z += mc.player.posZ
        }
        if (event.flags.contains(SPacketPlayerPosLook.EnumFlags.X_ROT)) {
            pitch += mc.player.rotationPitch
        }
        if (event.flags.contains(SPacketPlayerPosLook.EnumFlags.Y_ROT)) {
            yaw += mc.player.rotationYaw
        }
        mc.player.connection.sendPacket(PositionRotation(x, mc.player.entityBoundingBox.minY, z, yaw, pitch, false))
    }

    override fun onUpdate() {
        if (fullNullCheck()) {
            return
        }
        posLooks?.entries?.removeIf { (_, value): Map.Entry<Int, TimeVec> ->
            System.currentTimeMillis() - value.time > TimeUnit.SECONDS.toMillis(
                30L
            )
        }
    }

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent.Tick) {
        if (fullNullCheck() || event.stage != 0) {
            return
        }
        mc.player.setVelocity(0.0, 0.0, 0.0)
        if (mode.value !== Modes.Setback && teleportID == 0 && lessReduction.value) {
            sendTP(mc.player.positionVector)
            if (resetTicks(6)) {
                sendPackets(0.0, 0.0, 0.0)
            }
            return
        }
        if (TRStep.value) {
            if (ModuleManager.getModuleByClass(ReverseStep::class.java).isEnabled) {
                return
            }
        }
        val isPhasing = checkHitBoxes()
        var ySpeed: Double
        ySpeed =
            if (mc.player.movementInput.jump && (isPhasing || !EntityUtil.isMoving())) {
                if (AntiKick.value && !isPhasing) {
                    if (resetTicks(if (mode.value === Modes.Setback) 10 else 20)) -0.032 else 0.062
                } else {
                    0.062
                }
            } else if (mc.player.movementInput.sneak) {
                -0.062
            } else {
                if (!isPhasing) (if (resetTicks(4)) (if (AntiKick.value) -0.04 else 0.0) else 0.0) else 0.0
            }
        if (phase.value == PhaseMode.Full && isPhasing && EntityUtil.isMoving() && ySpeed != 0.0) {
            ySpeed /= 2.5
        }
        if (mode.value === Modes.Increment) {
            if (lastFactor >= FactorValue.value) {
                lastFactor = 1.0f
            } else if (++lastFactor > FactorValue.value) {
                lastFactor = FactorValue.value
            }
        } else {
            lastFactor = FactorValue.value
        }
        val dirSpeed = MathUtil.directionSpeed(if (phase.value == PhaseMode.Full && isPhasing) 0.031 else 0.26)
        var i = 1
        while (i <= if (mode.value === Modes.Factor || mode.value === Modes.Increment) lastFactor.toInt() else 1) {
            mc.player.motionX = dirSpeed[0] * 1f * i * XZSpeed.value
            mc.player.motionY = ySpeed * 1f * i * YSpeedValue.value
            mc.player.motionZ = dirSpeed[1] * 1f * i * XZSpeed.value
            sendPackets(mc.player.motionX, mc.player.motionY, mc.player.motionZ)
            i++
        }
        //Fast Mode
        var FastSpeed =
            if (mc.player.movementInput.jump && (checkHitBoxes() || !EntityUtil.isMoving())) (if (AntiKick.value && !checkHitBoxes()) (if (resetTicks(
                    10
                )
            ) -0.032 else 0.062) else if (resetTicks(20)) -0.032 else 0.062) else if (mc.player.movementInput.sneak) -0.062 else if (!checkHitBoxes()) (if (resetTicks(
                    4
                )
            ) (if (AntiKick.value) -0.04 else 0.0) else 0.0) else 0.0
        if (checkHitBoxes() && EntityUtil.isMoving() && FastSpeed != 0.0) {
            FastSpeed /= Reduction.value
        }
        if (mode.value == Modes.Fast) {
            sendPackets(dirSpeed[0], FastSpeed, dirSpeed[1])
        }
    }

    @SubscribeEvent
    fun onMove(event: MoveEvent) {
        if (fullNullCheck()) {
            return
        }
        if (firstStart) {
            event.isCanceled = true
            firstStart = false
        } else {
            event.isCanceled = false
        }
        if (teleportID != 0 || mode.value == Modes.Setback) {
            event.x = mc.player.motionX
            event.y = mc.player.motionY
            event.z = mc.player.motionZ
            if (checkHitBoxes() || phase.value == PhaseMode.Semi) {
                mc.player.noClip = true
            }
        }
    }

    @SubscribeEvent
    fun onPacketSend(event: PacketEvents.Send) {
        if (event.getPacket<Packet<*>>() is CPacketPlayer && !packets.remove(event.getPacket<Packet<*>>() as CPacketPlayer)) {
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun onPush(event: PushEvent) {
        event.isCanceled = true
    }

    @SubscribeEvent
    fun onRenderOverlay(event: RenderOverlayEvent) {
        event.isCanceled = true
    }

    @SubscribeEvent
    fun onRender(event: InsideBlockRenderEvent) {
        event.isCanceled = true
    }

    fun checkHitBoxes(): Boolean {
        return !mc.world.getCollisionBoxes(mc.player, mc.player.entityBoundingBox).isEmpty()
    }

    fun resetTicks(ticks: Int): Boolean {
        if (++otherids >= ticks) {
            otherids = 0
            return true
        }
        return false
    }

    fun sendPackets(x: Double, y: Double, z: Double) {
        val vec = Vec3d(x, y, z)
        val position = mc.player.positionVector.add(vec)
        val outOfBoundsVec = CAONIMA(position)
        packetSender(CPacketPlayer.Position(position.x, position.y, position.z, true))
        packetSender(
            PositionRotation(
                outOfBoundsVec.x,
                outOfBoundsVec.y,
                outOfBoundsVec.z,
                mc.player.rotationYaw,
                mc.player.rotationPitch,
                true
            )
        )
        mc.player.setPosition(position.x, position.y, position.z)
        sendTP(position)
    }

    fun sendTP(position: Vec3d) {
        if (confirmtp.value && teleportID != 0) {
            val id = ++teleportID
            mc.player.connection.sendPacket(CPacketConfirmTeleport(id))
            posLooks!![id] = TimeVec(position)
        }
    }

    fun CAONIMA(position: Vec3d): Vec3d {
        //左右极限 = 6
        //上下极限 =-150 || =300
        //尝试不非法发包
        var spoofX = position.x
        var spoofY = position.y
        var spoofZ = position.z
        when (type.value) {
            Types.Up -> {
                spoofY += 1337.0
            }

            Types.Down -> {
                spoofY -= 1337.0
            }

            Types.DownStrict -> {
                spoofY -= 256.0
            }

            Types.Bounded -> {
                spoofY += (if (spoofY < 127.5) 255 else 0) - position.y
            }

            Types.Conceal -> {
                spoofX += RandomUtil.nextInt(-100000, 100000).toDouble()
                spoofY += 2.0
                spoofZ += RandomUtil.nextInt(-100000, 100000).toDouble()
            }

            Types.Limit -> {
                spoofX += RandomUtil.nextDouble(-50.0, 50.0)
                spoofY += if (RandomUtil.getRandom().nextBoolean()) RandomUtil.nextDouble(
                    -80.0,
                    -50.0
                ) else RandomUtil.nextDouble(50.0, 80.0)
                spoofZ += RandomUtil.nextDouble(-50.0, 50.0)
            }

            Types.LimitJitter -> {
                spoofX += RandomUtil.nextDouble(-10.0, 10.0)
                spoofY += if (RandomUtil.getRandom().nextBoolean()) RandomUtil.nextDouble(
                    -100.0,
                    -80.0
                ) else RandomUtil.nextDouble(80.0, 100.0)
                spoofZ += RandomUtil.nextDouble(-10.0, 10.0)
            }

            Types.Preserve -> {
                spoofX += RandomUtil.getRandom().nextInt(100000).toDouble()
                spoofZ += RandomUtil.getRandom().nextInt(100000).toDouble()
            }

            Types.LimitPreserve -> {
                spoofX += RandomUtil.nextDouble(45.0, 85.0)
                spoofY += if (RandomUtil.getRandom().nextBoolean()) RandomUtil.nextDouble(
                    -95.0,
                    -40.0
                ) else RandomUtil.nextDouble(40.0, 95.0)
                spoofZ += RandomUtil.nextDouble(-85.0, -45.0)
            }

            Types.Xin -> {

                //spoofY += 10;
                spoofX += valueBounded.value.toDouble()
                spoofY += 0.0
                spoofZ += valueBounded.value.toDouble()
            }

            Types.OrgStrict -> {
                spoofX -= RandomUtil.nextInt(-10, 10).toDouble()
                spoofY -= RandomUtil.nextInt(-2, 2).toDouble()
                spoofZ += RandomUtil.nextInt(-10, 10).toDouble()
            }
        }
        return Vec3d(spoofX, spoofY, spoofZ)
    }

    fun packetSender(packet: CPacketPlayer) {
        packets.add(packet)
        mc.player.connection.sendPacket(packet)
    }

    enum class Types {
        Up,
        Down,
        DownStrict,
        Bounded,
        Conceal,
        Limit,
        LimitJitter,
        Preserve,
        LimitPreserve,
        Xin,
        OrgStrict
    }

    enum class PhaseMode {
        Off,
        Semi,
        Full
    }

    enum class Modes {
        Factor,
        Setback,
        Fast,
        Increment
    }

    class TimeVec(xIn: Double, yIn: Double, zIn: Double, val time: Long) : Vec3d(xIn, yIn, zIn) {

        constructor(vec3d: Vec3d) : this(vec3d.x, vec3d.y, vec3d.z, System.currentTimeMillis())
    }

    companion object {
        var INSTANCE = PacketFlyRewrite()
    }
}