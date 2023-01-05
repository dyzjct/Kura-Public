package me.dyzjct.kura.module.modules.xddd

import me.dyzjct.kura.utils.fuck.animations.fastFloor
import it.unimi.dsi.fastutil.longs.Long2LongMaps
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.longs.LongSets
import me.dyzjct.kura.event.events.client.PacketEvents
import me.dyzjct.kura.event.events.entity.MotionUpdateEvent
import me.dyzjct.kura.manager.HotbarManager
import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.utils.block.BlockInteractionHelper
import me.dyzjct.kura.utils.entity.EntityUtil
import me.dyzjct.kura.utils.inventory.HotbarSlot
import me.dyzjct.kura.utils.inventory.InventoryUtil
import me.dyzjct.kura.utils.mc.ChatUtil
import kura.events.TickEvent
import kura.utils.*
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.network.play.server.SPacketBlockChange
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.hypot

@Module.Info(name = "SurroundRewrite", category = Category.XDDD, description = "Continually places obsidian around your feet")
class SurroundRewrite : Module() {
    private var placeDelay = isetting("PlaceDelay", 50, 0, 1000)
    private var multiPlace = isetting("MultiPlace", 2, 1, 5)
    private var strictDirection = bsetting("StrictDirection", false)
    private var autoCenter = bsetting("AutoCenter", true)
    private var rotation = bsetting("Rotation", false)
    private val placing = EnumMap<SurroundOffset, List<PlaceInfo>>().synchronized()
    private val placingSet = LongOpenHashSet()
    private val pendingPlacing = Long2LongMaps.synchronize(Long2LongOpenHashMap()).apply { defaultReturnValue(-1L) }
    private val placed = LongSets.synchronize(LongOpenHashSet())
    private val toggleTimer = TickTimer(TimeUnit.TICKS)
    private var placeTimer = TickTimer()
    private var holePos: BlockPos? = null
    private var enableTicks = 0

    override fun onDisable() {
        placeTimer.reset(-114514L)
        toggleTimer.reset()

        placing.clear()
        placingSet.clear()
        pendingPlacing.clear()
        placed.clear()

        holePos = null
        enableTicks = 0
    }

    override fun onEnable() {
        if (fullNullCheck()){
            return
        }
        if (autoCenter.value) {
            EntityUtil.autoCenter()
        }
    }

    @SubscribeEvent
    fun Packet(event: PacketEvents.Receive) {
        if (fullNullCheck()){
            return
        }
        if (event.packet is SPacketBlockChange) {
            ChatUtil.sendMessage("HouYuePing")
            if (!(event.packet as SPacketBlockChange).blockState.isReplaceable) {
                val long = (event.packet as SPacketBlockChange).blockPosition.toLong()
                if (placingSet.contains(long)) {
                    pendingPlacing.remove(long)
                    placed.add(long)
                }
            } else {
                ChatUtil.sendMessage("HeMingZhu")
                val pos = (event.packet as SPacketBlockChange).blockPosition
                val relative = pos.subtract(Wrapper.player!!.betterPosition)
                if (SurroundOffset.values().any { it.offset == relative } && checkColliding(pos)) {
                    getNeighbor(pos)?.let { placeInfo ->
                        if (checkRotation(placeInfo)) {
                            placingSet.add(placeInfo.placedPos.toLong())
                            placeBlock(placeInfo)
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun PlayerMotion(event: MotionUpdateEvent) {
        if (rotation.value) {
            placing.runSynchronized {
                for (list in values) {
                    for (placeInfo in list) {
                        val long = placeInfo.placedPos.toLong()
                        if (placed.contains(long)) {
                            continue
                        }
                        event.setRotation(
                            BlockInteractionHelper.getLegitRotations(placeInfo.hitVec)[0],
                            BlockInteractionHelper.getLegitRotations(placeInfo.hitVec)[1]
                        )
                        return
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun Tick(event: TickEvent.Pre) {
        enableTicks++
    }

    inline val Entity.realSpeed get() = hypot(posX - prevPosX, posZ - prevPosZ)

    @SubscribeEvent
    fun RunGameLoop(event: MotionUpdateEvent.FastTick) {
        if (fullNullCheck()) {
            return
        }
        if (!Wrapper.player!!.onGround) {
            if (isEnabled) disable()
            return
        }

        var playerPos = Wrapper.player!!.betterPosition

        if (Wrapper.world!!.getBlockState(playerPos.down()).getCollisionBoundingBox(mc.world, playerPos) == null) {
            playerPos = mc.world.getGroundPos(mc.player).up()
        }

        if (holePos == null) {
            holePos = playerPos
        }

        updatePlacingMap(playerPos)

        if (placing.isNotEmpty() && placeTimer.tickAndReset(placeDelay.value)) {
            runPlacing()
            ChatUtil.sendMessage("YuJingTaang")
        }
    }

    private fun updatePlacingMap(playerPos: BlockPos) {
        pendingPlacing.runSynchronized {
            keys.removeIf {
                if (!mc.world.getBlockState(BlockPos.fromLong(it)).isReplaceable) {
                    placed.add(it)
                } else {
                    false
                }
            }
        }

        if (placing.isEmpty() && (pendingPlacing.isEmpty() || pendingPlacing.runSynchronized { values.all { System.currentTimeMillis() > it } })) {
            placing.clear()
            placed.clear()
        }

        for (surroundOffset in SurroundOffset.values()) {
            val offsetPos = playerPos.add(surroundOffset.offset)
            if (!mc.world.getBlockState(offsetPos).isReplaceable) continue

            getNeighborSequence(offsetPos, 2, 5.0f, strictDirection.value, false)?.let { list ->
                placing[surroundOffset] = list
                list.forEach {
                    placingSet.add(it.placedPos.toLong())
                }
            }
        }
    }

    private fun runPlacing() {
        var placeCount = 0

        placing.runSynchronized {
            val iterator = placing.values.iterator()
            while (iterator.hasNext()) {
                val list = iterator.next()
                var allPlaced = true
                var breakCrystal = false
                ChatUtil.sendMessage("RZY")

                loop@ for (placeInfo in list) {
                    val long = placeInfo.placedPos.toLong()
                    if (placed.contains(long)) continue
                    allPlaced = false
                    ChatUtil.sendMessage("LiuMengLong")

                    if (System.currentTimeMillis() <= pendingPlacing[long]) continue
                    if (!checkRotation(placeInfo)) continue

                    for (entity in mc.world.loadedEntityList) {
                        if (breakCrystal && entity is EntityEnderCrystal) continue
                        if (!entity.preventEntitySpawning) continue
                        if (!entity.isEntityAlive) continue
                        if (!entity.entityBoundingBox.intersects(AxisAlignedBB(placeInfo.placedPos))) continue
                        if (entity !is EntityEnderCrystal) continue@loop

                        mc.connection!!.sendPacket(CPacketUseEntity(entity))
                        mc.connection!!.sendPacket(CPacketAnimation(EnumHand.MAIN_HAND))
                        breakCrystal = true
                    }

                    placeBlock(placeInfo)
                    placeCount++
                    if (placeCount >= multiPlace.value) return
                }

                if (allPlaced) iterator.remove()
            }
        }
    }

    private fun getNeighbor(pos: BlockPos): PlaceInfo? {
        for (side in EnumFacing.values()) {
            val offsetPos = pos.offset(side)
            val oppositeSide = side.getOpposite()

            if (strictDirection.value && !getVisibleSides(offsetPos, true).contains(oppositeSide)) continue
            if (mc.world.getBlockState(offsetPos).isReplaceable) continue

            val hitVec = getHitVec(offsetPos, oppositeSide)
            val hitVecOffset = getHitVecOffset(oppositeSide)

            return PlaceInfo(offsetPos, oppositeSide, 0.0, hitVecOffset, hitVec, pos)
        }

        return null
    }

    private fun checkColliding(pos: BlockPos): Boolean {
        val box = AxisAlignedBB(pos)

        return mc.world.loadedEntityList.none {
            it.isEntityAlive && it.preventEntitySpawning && it.entityBoundingBox.intersects(box)
        }
    }

    private fun placeBlock(placeInfo: PlaceInfo) {
        if (fullNullCheck()){
            return
        }
        ChatUtil.sendMessage("Placed")
        val slot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN)
        val oldSlot = mc.player.inventory.currentItem

        if (slot < 0) {
            return
        }

        val sneak = !Wrapper.player!!.isSneaking
        if (sneak) mc.connection!!.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING))
        HotbarManager.spoofHotbar(slot)
        mc.connection!!.sendPacket(placeInfo.toPlacePacket(EnumHand.MAIN_HAND))
        HotbarManager.spoofHotbar(oldSlot)
        mc.connection!!.sendPacket(placeInfo.toPlacePacket(EnumHand.MAIN_HAND))
        mc.connection!!.sendPacket(CPacketAnimation(EnumHand.MAIN_HAND))
        if (sneak) mc.connection!!.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING))

        val blockState = Blocks.OBSIDIAN.getStateForPlacement(
            world,
            placeInfo.pos,
            placeInfo.side,
            placeInfo.hitVecOffset.x,
            placeInfo.hitVecOffset.y,
            placeInfo.hitVecOffset.z,
            0,
            player,
            EnumHand.MAIN_HAND
        )
        val soundType = blockState.block.getSoundType(blockState, world, placeInfo.pos, player)
        world.playSound(
            player,
            placeInfo.pos,
            soundType.placeSound,
            SoundCategory.BLOCKS,
            (soundType.getVolume() + 1.0f) / 2.0f,
            soundType.getPitch() * 0.8f
        )

        pendingPlacing[placeInfo.placedPos.toLong()] = System.currentTimeMillis() + 50L
    }


    private fun checkRotation(placeInfo: PlaceInfo): Boolean {
        var eyeHeight = mc.player.getEyeHeight()
        if (!Wrapper.player!!.isSneaking) eyeHeight -= 0.08f
        return !rotation.value || AxisAlignedBB(placeInfo.pos).isInSight(
            mc.player.positionVector.add(
                0.0,
                eyeHeight.toDouble(),
                0.0
            )
        ) != null
    }

    private enum class SurroundOffset(val offset: BlockPos) {
        DOWN(BlockPos(0, -1, 0)),
        NORTH(BlockPos(0, 0, -1)),
        EAST(BlockPos(1, 0, 0)),
        SOUTH(BlockPos(0, 0, 1)),
        WEST(BlockPos(-1, 0, 0))
    }

    inline val EntityPlayer.hotbarSlots: List<HotbarSlot>
        get() = ArrayList<HotbarSlot>().apply {
            for (slot in 36..44) {
                add(HotbarSlot(inventoryContainer.inventorySlots[slot]))
            }
        }

    @OptIn(ExperimentalContracts::class)
    inline fun <T : Any, R> T.runSynchronized(block: T.() -> R): R {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }

        return synchronized(this@runSynchronized) {
            block.invoke(this@runSynchronized)
        }
    }

    val Entity.betterPosition get() = BlockPos(this.posX.fastFloor(), (this.posY + 0.25).fastFloor(), this.posZ.fastFloor())

}