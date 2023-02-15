package me.windyteam.kura.module.modules.combat

import kura.utils.Wrapper
import kura.utils.isReplaceable
import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.utils.block.BlockUtil2
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.inventory.InventoryUtil
import me.windyteam.kura.utils.math.RotationUtil
import me.windyteam.kura.utils.player.Timer
import me.windyteam.kura.utils.player.getTarget
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.stream.Collectors

/**
 * created by chunfeng666 on 2022-10-05
 * update by dyzjct on 2023-2-15
 */

@Module.Info(name = "HoleKickerNew", category = Category.COMBAT)
object HoleKickerRewrite : Module() {
    private val range = settings("Range", 5, 1, 16)
    private val delay = settings("Delay", 100, 0, 500)
    private val breakCrystal = settings("BreakCrystal", false)
    private val packetPlace = settings("PacketPlace", false)
    private val autoToggle = settings("AutoToggle", true)
    private val autoPush = settings("Push", false)
    private val rotate = settings("Rotate", false)
    private var pistonList = mutableListOf<BlockPos>()
    private var pistonList2 = mutableListOf<BlockPos>()
    private var breakList = mutableListOf<BlockPos>()
    private var timer = Timer()

    var target: EntityPlayer? = null
    private var isSneaking = false
    override fun onEnable() {
        if (fullNullCheck()) return
        if (InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK) == -1 || InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON) == -1 && InventoryUtil.findHotbarBlock(
                Blocks.PISTON
            ) == -1
        ) {
            if (autoToggle.value) {
                disable()
            }
            return
        }
        EntityUtil.getRoundedBlockPos(mc.player as Entity)
        loadPistonList()
        loadPistonList2()
        loadBreakList()
    }

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent) {
        if (fullNullCheck()) return
        runCatching {
            if (InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK) == -1 || InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON) == -1 && InventoryUtil.findHotbarBlock(
                    Blocks.PISTON
                ) == -1
            ) {
                if (autoToggle.value) {
                    disable()
                }
                return
            }
            EntityUtil.getRoundedBlockPos(mc.player as Entity)
            target = getTarget(range.value)
            if (target == null) {
                if (autoToggle.value) {
                    disable()
                }
                return
            }
            if (breakCrystal.value) {
                breakCrystal()
            }
            doPistonTrap()
        }
    }

    override fun onDisable() {
        isSneaking = EntityUtil.stopSneaking(isSneaking)
        pistonList.clear()
    }

    private fun doPistonTrap() {
        if (fullNullCheck()) {
            return
        }
        target = getTarget(range.value)
        if (target == null) {
            if (autoToggle.value) {
                disable()
            }
            return
        }
        val playerPos = BlockPos(target!!.posX, target!!.posY, target!!.posZ)
        val b = mc.player.inventory.currentItem

        var doPiston = false
        var doRedStone = false
        var doRedStone1 = false
        for (i in 0..4) {
            if (getBlock(BlockPos(playerPos.add(0, 3, 0)))!!.block != Blocks.AIR) continue
            if (!mc.world.isPlaceable(BlockPos(playerPos.add(pistonList2[i]))) || !mc.world.isPlaceable(
                    BlockPos(
                        playerPos.add(
                            pistonList2[i].x, 2, pistonList2[i].z
                        )
                    )
                )
            ) continue
            if (!mc.world.isPlaceable(
                    BlockPos(
                        playerPos.add(
                            pistonList[i].x, 2, pistonList[i].z
                        )
                    )
                ) && getBlock(
                    BlockPos(
                        playerPos.add(
                            pistonList[i].x, 2, pistonList[i].z
                        )
                    )
                )!!.block != Blocks.REDSTONE_BLOCK
            ) {
                if (autoToggle.value) disable()
                continue
            }
            if (!mc.world.isPlaceable(
                    BlockPos(
                        playerPos.add(
                            pistonList[i].x, 3, pistonList[i].z
                        )
                    )
                ) && getBlock(
                    BlockPos(
                        playerPos.add(
                            pistonList[i].x, 3, pistonList[i].z
                        )
                    )
                )!!.block != Blocks.REDSTONE_BLOCK
            ) {
                if (autoToggle.value) disable()
                continue
            }
            if (!mc.world.isPlaceable(
                    BlockPos(
                        playerPos.add(
                            pistonList[i].x, 1, pistonList[i].z
                        )
                    )
                ) && getBlock(
                    BlockPos(
                        playerPos.add(
                            pistonList[i].x, 1, pistonList[i].z
                        )
                    )
                )!!.block != Blocks.STICKY_PISTON || !mc.world.isPlaceable(
                    BlockPos(
                        playerPos.add(
                            pistonList[i].x, 1, pistonList[i].z
                        )
                    )
                ) && getBlock(BlockPos(playerPos.add(pistonList[i].x, 1, pistonList[i].z)))!!.block != Blocks.PISTON
            ) continue
            if (mc.world.isPlaceable(BlockPos(playerPos.add(pistonList[i].x, 0, pistonList[i].z))) && timer.passedMs(
                    delay.value.toLong()
                )
            ) {
                switchToSlot(InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK))
                placeBlock(BlockPos(playerPos.add(pistonList[i].x, 0, pistonList[i].z)))
                doRedStone = true
            }
            val pistonSide = BlockUtil2.getFirstFacing(playerPos.add(pistonList[i]))
            val pistonNeighbour: BlockPos = playerPos.add(pistonList[i]).offset(pistonSide)
            val pistonOpposite = pistonSide.getOpposite()
            val pistonHitVec = Vec3d(pistonNeighbour as Vec3i).add(0.5, 0.5, 0.5)
                .add(Vec3d(pistonOpposite.getDirectionVec()).scale(0.5))
            if (rotate.value) {
                RotationUtil.faceVector(pistonHitVec, true)
            }
            switchToSlot(b)
            if (InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON) != -1) {
                switchToSlot(InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON))
            } else if (InventoryUtil.findHotbarBlock(Blocks.PISTON) != -1) {
                switchToSlot(InventoryUtil.findHotbarBlock(Blocks.PISTON))
            }
            if (timer.passedMs(delay.value.toLong()) && mc.world.isPlaceable(playerPos.add(pistonList[i])) && mc.player.inventory.currentItem == InventoryUtil.findHotbarBlock(
                    Blocks.PISTON
                ) || mc.player.inventory.currentItem == InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            ) {
                when (pistonList[i]) {
                    pistonList[0] -> {
                        mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, 0f, true))
                    }

                    pistonList[1] -> {
                        mc.player.connection.sendPacket(CPacketPlayer.Rotation(90.0f, 0f, true))
                    }

                    pistonList[2] -> {
                        mc.player.connection.sendPacket(CPacketPlayer.Rotation(0.0f, 0f, true))
                    }

                    pistonList[3] -> {
                        mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, 0f, true))
                    }
                }
                placeBlock(playerPos.add(pistonList[i]))
                doPiston = true
            }
            if (timer.passedMs(delay.value.toLong()) && mc.world.isPlaceable(
                    playerPos.add(
                        pistonList[i].x, 2, pistonList[i].z
                    )
                ) && !doRedStone
            ) {
                switchToSlot(InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK))
                placeBlock(playerPos.add(pistonList[i].x, 2, pistonList[i].z))
                switchToSlot(b)
                doRedStone1 = true
            }
            if (doPiston && doRedStone || doRedStone1) {
                if (autoPush.value) {
                    breakRedStone()
                }
                if (autoToggle.value) {
                    disable()
                }
                break
            }
        }
        switchToSlot(b)
        mc.player.inventory.currentItem = b
        mc.playerController.updateController()
    }

    private fun breakRedStone() {
        if (fullNullCheck()) {
            return
        }
        target = getTarget(range.value)
        if (target == null) {
            return
        }
        val breakPos = BlockPos(target!!.posX, target!!.posY, target!!.posZ)
        for (i in breakList) {
            if (getBlock(breakPos.add(i))!!.block == Blocks.REDSTONE_BLOCK) {
                mc.playerController.onPlayerDamageBlock(
                    breakPos.add(i), BlockUtil2.getRayTraceFacing(breakPos.add(i))
                )
            }
        }
    }

    private fun loadBreakList() {
        if (fullNullCheck()) return
        breakList.add(BlockPos(1, 2, 0))
        breakList.add(BlockPos(-1, 2, 0))
        breakList.add(BlockPos(0, 2, 1))
        breakList.add(BlockPos(0, 2, -1))
        breakList.add(BlockPos(1, 0, 0))
        breakList.add(BlockPos(-1, 0, 0))
        breakList.add(BlockPos(0, 0, 1))
        breakList.add(BlockPos(0, 0, -1))
    }

    private fun loadPistonList() {
        pistonList.add(BlockPos(1, 1, 0))
        pistonList.add(BlockPos(-1, 1, 0))
        pistonList.add(BlockPos(0, 1, 1))
        pistonList.add(BlockPos(0, 1, -1))
    }

    private fun loadPistonList2() {
        pistonList2.add(BlockPos(-1, 1, 0))
        pistonList2.add(BlockPos(1, 1, 0))
        pistonList2.add(BlockPos(0, 1, -1))
        pistonList2.add(BlockPos(0, 1, 1))
    }

    private fun placeBlock(pos: BlockPos) {
        BlockUtil2.placeBlock(pos, EnumHand.MAIN_HAND, false, packetPlace.value, false)
    }

    private fun getBlock(block: BlockPos): IBlockState? {
        return mc.world.getBlockState(block)
    }

    private fun switchToSlot(slot: Int) {
        mc.player.connection.sendPacket(CPacketHeldItemChange(slot) as Packet<*>)
        mc.player.inventory.currentItem = slot
        mc.playerController.updateController()
    }

    fun World.isPlaceable(pos: BlockPos, ignoreSelfCollide: Boolean = false) =
        this.getBlockState(pos).isReplaceable && this.checkNoEntityCollision(
            AxisAlignedBB(pos), if (ignoreSelfCollide) Wrapper.player else null
        )

    private fun breakCrystal() {
        for (crystal in mc.world.loadedEntityList.stream().filter { e: Entity -> e is EntityEnderCrystal && !e.isDead }
            .sorted(Comparator.comparing { e: Entity -> java.lang.Float.valueOf(mc.player.getDistance(e)) }).collect(
                Collectors.toList()
            )) {
            if (crystal !is EntityEnderCrystal || mc.player.getDistance(crystal) > 4.0f) continue
            mc.player.connection.sendPacket(CPacketUseEntity(crystal) as Packet<*>)
            mc.player.connection.sendPacket(CPacketAnimation(EnumHand.OFF_HAND) as Packet<*>)
        }
    }
}