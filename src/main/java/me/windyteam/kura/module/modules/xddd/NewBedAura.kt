package me.windyteam.kura.module.modules.xddd

import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.event.events.entity.MotionUpdateEvent.FastTick
import me.windyteam.kura.event.events.entity.MotionUpdateEvent.Tick
import me.windyteam.kura.event.events.render.RenderEvent
import me.windyteam.kura.friend.FriendManager
import me.windyteam.kura.manager.HotbarManager.spoofHotbar
import me.windyteam.kura.manager.RotationManager
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.CrystalDamageCalculator.Companion.calcDamage
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.CrystalHelper.Companion.PredictionHandlerNew
import me.windyteam.kura.utils.other.MultiThreading
import me.windyteam.kura.utils.TimerUtils
import me.windyteam.kura.utils.animations.BlockEasingRender
import me.windyteam.kura.utils.mc.ChatUtil
import me.windyteam.kura.utils.entity.CrystalUtil.getSphere
import me.windyteam.kura.utils.gl.MelonTessellator
import net.minecraft.block.Block
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.ClickType
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.network.play.server.SPacketBlockChange
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockPos.MutableBlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color
import java.util.stream.Collectors
import kotlin.math.floor
import kotlin.math.pow

@Module.Info(name = "NewBedAura", category = Category.XDDD)
object NewBedAura : Module() {
    private var calcMode = msetting("CalcMode", Mode.Stable)
    private var range = isetting("Range", 5, 1, 8)
    private var distance = isetting("EnemyBedDist", 8, 1, 13)
    private var minDmg = isetting("MinDMG", 4, 0, 20)
    private var minDifference = isetting("MinDiff", 4, 0, 20)
    private var maxSelfDmg = isetting("MaxSelfDmg", 4, 0, 20)
    private var prediction = bsetting("Prediction", true)
    private var predictedTicks = isetting("PredictedTicks", 1, 0, 20).b(prediction)
    private var packetExplode = bsetting("PacketExplode", false)
    private var packetDebug = bsetting("PacketDebug", false)
    private var packetDelay = isetting("PacketDelay", 15, 0, 1000).b(packetExplode)
    private var placeDelay = isetting("PlaceDelay", 15, 0, 1000)
    private var clickDelay = isetting("ClickDelay", 15, 0, 1000)
    private var invClickDelay = isetting("InvClickDelay", 5, 0, 1000)
    private var fade = bsetting("FadeMove", false)
    private var color = csetting("Color", Color(255, 198, 206))
    private var alpha = isetting("Alpha", 120, 1, 255)
    private var blockRenderSmooth: BlockEasingRender? = BlockEasingRender(BlockPos(0, 0, 0), 450f, 350f)
    private var blockPos: BlockPos? = null
    private var bedExplodePos: BlockPos? = null
    private var direction: EnumFacing? = null
    private var oldSlot = 0
    private var renderEnt: EntityPlayer? = null
    private var packetTimer: TimerUtils = TimerUtils()
    private var placeTimer: TimerUtils = TimerUtils()
    private var clickTimer: TimerUtils = TimerUtils()
    private var inventoryTimer: TimerUtils = TimerUtils()
    private var yawOffset = 0f
    private var offhand = false

    enum class Mode {
        Stable,
        Fast
    }

    @SubscribeEvent
    fun onPacket(it: PacketEvents.Receive) {
        if (fullNullCheck()) {
            return
        }
        if (it.getPacket<Packet<*>>() is SPacketBlockChange) {
            val packet: SPacketBlockChange = it.getPacket()
            if (packetExplode.value) {
                if (packetTimer.tickAndReset(packetDelay.value)) {
                    if (bedExplodePos != null) {
                        if (packet.blockPosition.equals(bedExplodePos)) {
                            if (packetDebug.value) {
                                ChatUtil.sendMessage("1")
                            }
                            mc!!.connection!!.sendPacket(
                                CPacketPlayerTryUseItemOnBlock(
                                    bedExplodePos!!,
                                    EnumFacing.UP,
                                    if (offhand) EnumHand.OFF_HAND else EnumHand.MAIN_HAND,
                                    0.0f,
                                    0.0f,
                                    0.0f
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onCalc() {
        MultiThreading.runAsync {
            val entities = mc.world.playerEntities.stream()
                .filter { e: EntityPlayer -> !FriendManager.isFriend(e.name) && e !== mc.player && e.health > 0.0f && !e.isDead }
                .collect(
                    Collectors.toCollection { ArrayList() }
                )
            if (entities == null) {
                blockPos = null
                return@runAsync
            }
            val sphereBlocks = getSphere(
                mc.player.position, range.value.toDouble(), range.value.toDouble(),
                false,
                true,
                0
            )
            val bedPos = canPlaceBed(entities, sphereBlocks)
            var d = minDmg.value.toDouble()
            for (entity in entities) {
                if (entity == null) {
                    blockPos = null
                }
                for (pos in bedPos) {
                    if (entity!!.getDistanceSq(pos.blockPos) > distance.value.toDouble().pow(2.0)) continue
                    for (i in pos.canPlaceDirection.indices) {
                        val boost2 = pos.blockPos.add(0, 1, 0).offset(pos.canPlaceDirection[i])
                        val predictTarget = if (prediction.value) PredictionHandlerNew(
                            entity,
                            predictedTicks.value
                        ) else Vec3d(0.0, 0.0, 0.0)
                        val d2 = calcDamage(
                            entity,
                            entity.positionVector.add(predictTarget),
                            entity.entityBoundingBox,
                            boost2.x.toDouble() + 0.5,
                            boost2.y.toDouble() + 0.5,
                            boost2.z.toDouble() + 0.5,
                            MutableBlockPos()
                        ).toDouble()
                        if (d2 < pos.selfDamage[i] && d2 <= (entity.health + entity.absorptionAmount).toDouble() || d2 < d || d2 - pos.selfDamage[i] < minDifference.value.toDouble()) continue
                        if (d2 < minDmg.value) continue
                        d = d2
                        blockPos = pos.blockPos
                        direction = pos.canPlaceDirection[i]
                        renderEnt = entity
                    }
                }
            }
            if (d == minDmg.value.toDouble() || renderEnt == null) {
                blockPos = null
                return@runAsync
            }
        }
    }

    @SubscribeEvent
    fun onTick(event: Tick?) {
        if (fullNullCheck()) {
            return
        }
        if (calcMode.value!!.equals(Mode.Stable)) {
            onCalc()
        }
    }

    @SubscribeEvent
    fun onTick(event: FastTick?) {
        if (fullNullCheck()) {
            return
        }
        if (calcMode.value!!.equals(Mode.Fast)) {
            onCalc()
        }
        oldSlot = mc.player.inventory.currentItem
        offhand = mc.player.heldItemOffhand.getItem() === Items.BED
        var bedSlot = if (mc.player.heldItemMainhand.getItem() === Items.BED) mc.player.inventory.currentItem else -1
        if (bedSlot == -1) {
            for (l in 0..8) {
                if (mc.player.inventory.getStackInSlot(l).getItem() !== Items.BED) continue
                bedSlot = l
                break
            }
        }
        if (bedSlot == -1 && !offhand) {
            if (mc.currentScreen !is GuiContainer) {
                for (i in 9..34) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() !== Items.BED) continue
                    if (inventoryTimer.tickAndReset(invClickDelay.value)) {
                        mc.playerController.windowClick(
                            mc.player.inventoryContainer.windowId,
                            i,
                            0,
                            ClickType.QUICK_MOVE,
                            mc.player
                        )
                    }
                    break
                }
            }
            bedSlot =
                if (mc.player.heldItemMainhand.getItem() === Items.BED) mc.player.inventory.currentItem else -1
            if (bedSlot == -1) {
                for (l in 0..8) {
                    if (mc.player.inventory.getStackInSlot(l).getItem() !== Items.BED) continue
                    bedSlot = l
                    break
                }
            }
            if (bedSlot == -1 && !offhand) {
                return
            }
        }
        if (blockPos != null) {
            bedExplodePos = blockPos
            if (direction != null) {
                when (direction) {
                    EnumFacing.EAST -> {
                        RotationManager.addRotations(-91.0f, mc.player.rotationPitch)
                        yawOffset = -91f
                        //event.setRotation(-91.0f, mc.player.rotationPitch)
                    }

                    EnumFacing.NORTH -> {
                        RotationManager.addRotations(179.0f, mc.player.rotationPitch)
                        yawOffset = 179f
                        //event.setRotation(179.0f, mc.player.rotationPitch)
                    }

                    EnumFacing.WEST -> {
                        RotationManager.addRotations(89.0f, mc.player.rotationPitch)
                        yawOffset = 89f
                        //event.setRotation(89.0f, mc.player.rotationPitch)
                    }

                    else -> {
                        RotationManager.addRotations(-1.0f, mc.player.rotationPitch)
                        yawOffset = -1f
                        //event.setRotation(-1.0f, mc.player.rotationPitch)
                    }
                }
            }
            val vec =
                blockPos?.let {
                    Vec3d(it).add(0.5, 0.5, 0.5).add(Vec3d(EnumFacing.DOWN.getDirectionVec()).scale(0.5))
                }
            val f = (vec!!.x - blockPos!!.getX().toDouble()).toFloat()
            val f1 = (vec.y - blockPos!!.getY().toDouble()).toFloat()
            val f2 = (vec.z - blockPos!!.getZ().toDouble()).toFloat()
            var sneak = false
            if (mc.player.isSneaking) {
                sneak = true
                mc.player.connection.sendPacket(
                    CPacketEntityAction(
                        mc.player,
                        CPacketEntityAction.Action.STOP_SNEAKING
                    )
                )
            }
            if (direction?.let { blockPos!!.up().offset(it) }
                    ?.let { mc.world.getBlockState(it).block } === Blocks.BED) {
                mc.player.connection.sendPacket(CPacketAnimation(if (offhand) EnumHand.OFF_HAND else EnumHand.MAIN_HAND))
                direction?.let { blockPos!!.up().offset(it) }?.let {
                    CPacketPlayerTryUseItemOnBlock(
                        it,
                        EnumFacing.UP,
                        if (offhand) EnumHand.OFF_HAND else EnumHand.MAIN_HAND,
                        0.0f,
                        0.0f,
                        0.0f
                    )
                }?.let {
                    if (clickTimer.tickAndReset(clickDelay.value)) {
                        mc!!.connection!!.sendPacket(
                            it
                        )
                    }
                }
            }
            spoofHotbar(bedSlot, event)
            mc.player.swingArm(if (offhand) EnumHand.OFF_HAND else EnumHand.MAIN_HAND)
            try {
                if (placeTimer.tickAndReset(placeDelay.value)) {
                    blockPos?.let {
                        CPacketPlayerTryUseItemOnBlock(
                            it,
                            EnumFacing.UP,
                            if (offhand) EnumHand.OFF_HAND else EnumHand.MAIN_HAND,
                            f,
                            f1,
                            f2
                        )
                    }?.let {
                        mc.connection!!.sendPacket(
                            it
                        )
                    }
                    mc.connection!!.sendPacket(CPacketAnimation(if (offhand) EnumHand.OFF_HAND else EnumHand.MAIN_HAND))
                    mc.connection!!.sendPacket(
                        CPacketPlayerTryUseItemOnBlock(
                            blockPos!!.up(),
                            EnumFacing.UP,
                            if (offhand) EnumHand.OFF_HAND else EnumHand.MAIN_HAND,
                            0.0f,
                            0.0f,
                            0.0f
                        )
                    )
                }
                spoofHotbar(oldSlot, event)
            } catch (_: Exception) {
            }
            if (sneak) {
                mc.player.connection.sendPacket(
                    CPacketEntityAction(
                        mc.player,
                        CPacketEntityAction.Action.START_SNEAKING
                    )
                )
            }
        }
    }

    override fun onEnable() {
        if (fullNullCheck()) {
            return
        }
        blockPos = null
        placeTimer.reset()
        packetTimer.reset()
        clickTimer.reset()
        inventoryTimer.reset()
    }

    private fun canPlaceBed(entityPlayerList: List<EntityPlayer>, blockPosList: List<BlockPos>): List<BedSaver> {
        val bedSaverList = ArrayList<BedSaver>()
        val list = ArrayList<EnumFacing>()
        val damage = ArrayList<Double>()
        for (pos in blockPosList) {
            var x = false
            for (entityPlayer in entityPlayerList) {
                if (entityPlayer.getDistanceSq(pos) > distance.value.toDouble().pow(2.0)) continue
                x = true
                break
            }
            if (!x) continue
            for (facing in EnumFacing.HORIZONTALS) {
                var selfDmg = 0.0
                val side = pos.offset(facing)
                val boost = pos.add(0, 1, 0)
                val boost2 = pos.add(0, 1, 0).offset(facing)
                val boostBlock = mc.world.getBlockState(boost).block
                val boostBlock2 = mc.world.getBlockState(boost2).block
                if (boostBlock !== Blocks.AIR && boostBlock !== Blocks.BED || boostBlock2 !== Blocks.AIR && boostBlock2 !== Blocks.BED || !mc.world.getBlockState(
                        side
                    ).material.isOpaque || !mc.world.getBlockState(side).isFullCube || !mc.world.getBlockState(pos).material.isOpaque || !mc.world.getBlockState(
                        pos
                    ).isFullCube || calcDamage(
                        mc.player,
                        mc.player.positionVector,
                        mc.player.entityBoundingBox,
                        boost2.x.toDouble() + 0.5,
                        boost2.y.toDouble() + 0.5,
                        boost2.z.toDouble() + 0.5,
                        MutableBlockPos()
                    ).also {
                        selfDmg = it.toDouble()
                    } > maxSelfDmg.value.toDouble() || selfDmg >= (mc.player.health + mc.player.absorptionAmount + 2.0f).toDouble()
                ) continue
                list.add(facing)
                damage.add(selfDmg)
            }
            if (list.isEmpty()) continue
            bedSaverList.add(BedSaver(pos, list, damage))
            list.clear()
            damage.clear()
        }
        return bedSaverList
    }

    override fun onWorldRender(event: RenderEvent) {
        if (direction == null || blockPos == null) {
            return
        }
        val render = direction?.let { blockPos!!.up().offset(it) }
        val renderPos = AxisAlignedBB(
            render!!.x.toDouble() - 0.5,
            render.y.toDouble(),
            render.z.toDouble() - 1,
            (render.x + 0.5),
            render.y.toDouble() + 0.5625,
            (render.z + 1).toDouble()
        )
        blockRenderSmooth!!.updatePos(BlockPos(renderPos.center.x, renderPos.center.y - 0.25f, renderPos.center.z))
        blockRenderSmooth!!.begin()
        MelonTessellator.drawBBBox(
            if (fade.value) blockRenderSmooth!!.getFullUpdate() else renderPos,
            color.value,
            alpha.value,
            2f,
            true
        )
        //MelonTessellator.prepare(GL11.GL_QUADS)
        //MelonTessellator.drawFullBox(BlockPos(xPos, yPos, zPos), 2f, c.rgb)
        //MelonTessellator.drawFullBox(renderPos.center.add(0, (0.5625).toInt(), 0), 2f, c.rgb)
        //MelonTessellator.release()
        GlStateManager.pushMatrix()
        try {
            if (!fade.value) {
                MelonTessellator.glBillboardDistanceScaled(
                    renderPos.center.x.toFloat() + 0.5f,
                    renderPos.center.y.toFloat() + 0.5f,
                    renderPos.center.z.toFloat() + 0.5f,
                    mc.player,
                    1.0f
                )
            } else {
                MelonTessellator.glBillboardDistanceScaled(
                    blockRenderSmooth!!.getFullUpdate().center.x.toFloat() + 0.5f,
                    blockRenderSmooth!!.getFullUpdate().center.y.toFloat() + 0.5f,
                    blockRenderSmooth!!.getFullUpdate().center.z.toFloat() + 0.5f,
                    mc.player,
                    1.0f
                )
            }
            val damage = if (fade.value) {
                calcDamage(
                    renderEnt!!,
                    renderEnt!!.positionVector,
                    renderEnt!!.entityBoundingBox,
                    renderPos.center.x + 0.5,
                    renderPos.center.y + 0.5,
                    renderPos.center.z + 0.5,
                    MutableBlockPos()
                )
            } else {
                calcDamage(
                    renderEnt!!,
                    renderEnt!!.positionVector,
                    renderEnt!!.entityBoundingBox,
                    blockRenderSmooth!!.getFullUpdate().center.x + 0.5,
                    blockRenderSmooth!!.getFullUpdate().center.y + 0.5,
                    blockRenderSmooth!!.getFullUpdate().center.z + 0.5,
                    MutableBlockPos()
                )
            }
            val damage2 = if (fade.value) {
                calcDamage(
                    mc.player,
                    mc.player.positionVector,
                    mc.player.entityBoundingBox,
                    renderPos.center.x + 0.5,
                    renderPos.center.y + 0.5,
                    renderPos.center.z + 0.5,
                    MutableBlockPos()
                )
            } else {
                calcDamage(
                    mc.player,
                    mc.player.positionVector,
                    mc.player.entityBoundingBox,
                    blockRenderSmooth!!.getFullUpdate().center.x + 0.5,
                    blockRenderSmooth!!.getFullUpdate().center.y + 0.5,
                    blockRenderSmooth!!.getFullUpdate().center.z + 0.5,
                    MutableBlockPos()
                )
            }
            val damageText =
                (if (floor(damage.toDouble()) == damage.toDouble()) Integer.valueOf(damage.toInt()) else String.format(
                    "%.1f",
                    damage
                )).toString() + ""
            val damageText2 =
                (if (floor(damage2.toDouble()) == damage2.toDouble()) Integer.valueOf(damage2.toInt()) else String.format(
                    "%.1f",
                    damage2
                )).toString() + ""
            GlStateManager.disableDepth()
            GlStateManager.translate(
                -(fontRenderer.getStringWidth("$damageText/$damageText2").toDouble() / 2.0),
                0.0,
                0.0
            )
            fontRenderer.drawStringWithShadow("\u00a7b$damageText/$damageText2", 0f, 10f, -5592406)
            GlStateManager.enableDepth()
        } catch (exception: Exception) {
            // empty catch block
        }
        GlStateManager.popMatrix()
    }

    class BedSaver(var blockPos: BlockPos, canPlaceDirection: List<EnumFacing>?, selfDamage: List<Double>?) {
        var canPlaceDirection: List<EnumFacing>
        var selfDamage: List<Double>

        init {
            this.canPlaceDirection = canPlaceDirection?.let { ArrayList(it) }!!
            this.selfDamage = selfDamage?.let { ArrayList(it) }!!
        }
    }

    val blocks: List<Block>
        get() = listOf(
            Blocks.OBSIDIAN,
            Blocks.BEDROCK,
            Blocks.COMMAND_BLOCK,
            Blocks.BARRIER,
            Blocks.ENCHANTING_TABLE,
            Blocks.ENDER_CHEST,
            Blocks.END_PORTAL_FRAME,
            Blocks.BEACON,
            Blocks.ANVIL
        )
}