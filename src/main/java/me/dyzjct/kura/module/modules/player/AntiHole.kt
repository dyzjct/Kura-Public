package me.dyzjct.kura.module.modules.player

import com.mojang.realmsclient.gui.ChatFormatting
import me.dyzjct.kura.event.events.entity.MotionUpdateEvent
import me.dyzjct.kura.event.events.player.UpdateWalkingPlayerEvent
import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.setting.BooleanSetting
import me.dyzjct.kura.utils.MathUtil
import me.dyzjct.kura.utils.NTMiku.BlockUtilss
import me.dyzjct.kura.utils.entity.EntityUtil
import me.dyzjct.kura.utils.inventory.InventoryUtil
import me.dyzjct.kura.utils.mc.ChatUtil
import me.dyzjct.kura.utils.NTMiku.Timerss
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.block.BlockFire
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemPickaxe
import net.minecraft.item.ItemStack
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "AntiHole", category = Category.PLAYER)
class AntiHole : Module() {
    private val distance = isetting("distance", 2, 0, 6)
    private val range = isetting("range", 2, 0, 6)
    private val REDSTONE_BLOCK = bsetting("REDSTONE_BLOCK", false)
    private val packet = bsetting("packet", false)
    private val delay = isetting("delay", 2, 0, 6)
    private val blocksPerTick = isetting("blocksPerTick", 12, 1, 20)
    private val autoSwitch = bsetting("autoSwitch", false)
    private val rotate = bsetting("rotate", false)
    private val toggle = bsetting("toggle", false)
    private val timer = Timerss()
    var target: EntityPlayer? = null
    private var filler = false
    private var didPlace = false
    private var isSneaking = false
    private var oldSlot = -1
    private var isMining = false
    private var placements = 0


    override fun onEnable() {
        if (fullNullCheck()) return
        EntityUtil.getRoundedBlockPos(mc.player as Entity)
    }

    fun onTick(event: MotionUpdateEvent.Tick?) {
        if (InventoryUtil.findHotbarBlock(Blocks.PISTON as Block) == -1 || InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK) == -1) {
            ChatUtil.sendMessage("<" + "gay" + "> " + ChatFormatting.RED + "Piston ? RedstoneBlock ?")
            disable()
            return
        }
        doPiston()
    }

    fun getDisplayInfo(): String? {
        return if (target != null) target!!.name else null
    }

    override fun onDisable() {
        isPlacing = false
        isSneaking = EntityUtil.stopSneaking(isSneaking)
    }

    private fun doPiston() {
        if (check()) return
        doPistonTrap()
        if (didPlace) timer.reset()
        if (toggle as Boolean);
    }

    private fun doPistonTrap() {
        val a = mc.player.rotationPitch
        val b = mc.player.inventory.currentItem
        val c = target!!.positionVector
        if (checkList(c, EntityUtil.getVarOffsets(0, 1, 0)) && checkList(
                c,
                EntityUtil.getVarOffsets(0, 2, 0)
            )
        ) if (checkList(c, EntityUtil.getVarOffsets(1, 1, 0)) && checkList(
                c,
                EntityUtil.getVarOffsets(-1, 1, 0)
            ) && checkList(c, EntityUtil.getVarOffsets(-1, 2, 0))
        ) {
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON as Block)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, a, true) as Packet<*>)
            place(c, EntityUtil.getVarOffsets(1, 1, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController()
            place(c, EntityUtil.getVarOffsets(1, 2, 0))
            checkCrystal(c, EntityUtil.getVarOffsets(0, 1, -1))
            checkCrystal(c, EntityUtil.getVarOffsets(0, 1, 1))
            checkCrystal(c, EntityUtil.getVarOffsets(1, 1, 0))
            checkCrystal(c, EntityUtil.getVarOffsets(-1, 1, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON as Block)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, a, true) as Packet<*>)
            place(c, EntityUtil.getVarOffsets(1, 1, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController()
            place(c, EntityUtil.getVarOffsets(1, 0, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON as Block)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, a, true) as Packet<*>)
            place(c, EntityUtil.getVarOffsets(1, 1, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController()
            place(c, EntityUtil.getVarOffsets(2, 1, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON as Block)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, a, true) as Packet<*>)
            place(c, EntityUtil.getVarOffsets(1, 1, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController()
            place(c, EntityUtil.getVarOffsets(1, 1, -1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON as Block)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, a, true) as Packet<*>)
            place(c, EntityUtil.getVarOffsets(1, 1, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController()
            place(c, EntityUtil.getVarOffsets(1, 1, 1))
        } else if (checkList(c, EntityUtil.getVarOffsets(0, 1, -1)) && checkList(
                c,
                EntityUtil.getVarOffsets(1, 1, 1)
            ) && checkList(c, EntityUtil.getVarOffsets(1, 2, 1))
        ) {
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON as Block)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, a, true) as Packet<*>)
            place(c, EntityUtil.getVarOffsets(0, 1, -1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController()
            place(c, EntityUtil.getVarOffsets(0, 2, -1))
            checkCrystal(c, EntityUtil.getVarOffsets(0, 1, -1))
            checkCrystal(c, EntityUtil.getVarOffsets(0, 1, 1))
            checkCrystal(c, EntityUtil.getVarOffsets(1, 1, 0))
            checkCrystal(c, EntityUtil.getVarOffsets(-1, 1, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON as Block)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, a, true) as Packet<*>)
            place(c, EntityUtil.getVarOffsets(0, 1, -1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController()
            place(c, EntityUtil.getVarOffsets(0, 2, -1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON as Block)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, a, true) as Packet<*>)
            place(c, EntityUtil.getVarOffsets(0, 1, -1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController()
            place(c, EntityUtil.getVarOffsets(0, 1, -2))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON as Block)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, a, true) as Packet<*>)
            place(c, EntityUtil.getVarOffsets(0, 1, -1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController()
            place(c, EntityUtil.getVarOffsets(1, 1, -1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON as Block)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, a, true) as Packet<*>)
            place(c, EntityUtil.getVarOffsets(0, 1, -1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController()
            place(c, EntityUtil.getVarOffsets(-1, 1, -1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON as Block)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, a, true) as Packet<*>)
            place(c, EntityUtil.getVarOffsets(0, 1, -1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController()
            place(c, EntityUtil.getVarOffsets(0, 0, -1))
        }
        mc.player.inventory.currentItem = b
        mc.playerController.updateController()
        filler = true
    }

    private fun checkList(pos: Vec3d, list: Array<Vec3d>): Boolean {
        for (vec3d in list) {
            val position = BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z)
            val block = mc.world.getBlockState(position).block
            if (block is BlockAir || block is BlockFire) return true
        }
        return false
    }


    private fun place(pos: Vec3d, list: Array<Vec3d>) {
        if (placements < (blocksPerTick as Int).toInt()) for (vec3d in list) {
            val position = BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z)
            val a = mc.player.inventory.currentItem
            isSneaking = BlockUtilss.placeBlock(
                position,
                EnumHand.MAIN_HAND,
                (rotate as Boolean),
                (packet as Boolean),
                true
            )
            mc.player.inventory.currentItem = a
            mc.playerController.updateController()
            didPlace = true
            placements++
        }
    }

    fun checkCrystal(pos: Vec3d?, list: Array<Vec3d>): Entity? {
        var crystal: Entity? = null
        for (vec3d in list) {
            val position = BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z)
            for (entity in mc.world.getEntitiesWithinAABB(
                Entity::class.java, AxisAlignedBB(position)
            )) {
                if (entity is EntityEnderCrystal &&
                    crystal == null
                ) crystal = entity
            }
        }
        return crystal
    }

    private fun check(): Boolean {
        isPlacing = false
        didPlace = false
        filler = false
        placements = 0
//        if (isOff) return true
        isSneaking = EntityUtil.stopSneaking(isSneaking)
        target = getTarget(range as Double)
        return target == null || !timer.passedMs(delay as Long)
    }

    private fun getTarget(range: Double): EntityPlayer? {
        var target: EntityPlayer? = null
        var distance = range
        for (player in mc.world.playerEntities) {
            if (EntityUtil.isntValid(player as Entity, range)) continue
            if (target == null) {
                target = player
                distance = mc.player.getDistanceSq(player as Entity)
                continue
            }
            if (mc.player.getDistanceSq(player as Entity) >= distance) continue
            target = player
            distance = mc.player.getDistanceSq(player as Entity)
        }
        return target
    }

    @SubscribeEvent
    fun onUpdateWalkingPlayerPre(event: UpdateWalkingPlayerEvent) {
        if (event.stage == 0) {
            if (REDSTONE_BLOCK as Boolean) {
                val blocklist = ArrayList<Block>()
                blocklist.add(Blocks.REDSTONE_BLOCK)
                breakBlocks(blocklist)
            }
        }
    }

    fun breakBlocks(blocks: List<Block>) {
        val pos = getNearestBlock(blocks)
        if (pos != null) {
            if (!isMining) {
                oldSlot = mc.player.inventory.currentItem
                isMining = true
            }
            if (canBreak(pos)) {
                if (autoSwitch as Boolean) {
                    var newSlot = -1
                    for (i in 0..8) {
                        val stack = mc.player.inventory.getStackInSlot(i)
                        if (stack == ItemStack.EMPTY || stack.getItem() !is ItemPickaxe) continue
                        newSlot = i
                        break
                    }
                    if (newSlot != -1) {
                        mc.player.inventory.currentItem = newSlot
                    }
                }
                mc.playerController.onPlayerDamageBlock(pos, mc.player.horizontalFacing)
                mc.player.swingArm(EnumHand.MAIN_HAND)
            }
        } else if (autoSwitch as Boolean && oldSlot != -1) {
            mc.player.inventory.currentItem = oldSlot
            oldSlot = -1
            isMining = false
        }
    }

    private fun getNearestBlock(blocks: List<Block>): BlockPos? {
        var maxDist = MathUtil.square(distance as Double)
        var ret: BlockPos? = null
        run {
            var x = maxDist
            while (x >= -maxDist) {
                run {
                    var y = maxDist
                    while (y >= -maxDist) {
                        var z = maxDist
                        while (z >= -maxDist) {
                            val pos = BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z)
                            val dist = mc.player.getDistanceSq(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
                            if (dist > maxDist || !blocks.contains(mc.world.getBlockState(pos).block) || !this.canBreak(
                                    pos
                                )
                            ) {
                                z -= 1.0
                                continue
                            }
                            maxDist = dist
                            ret = pos
                            z -= 1.0
                        }
                        y -= 1.0
                    }
                }
                x -= 1.0
            }
        }
        return ret
    }

    private fun canBreak(pos: BlockPos): Boolean {
        val blockState = mc.world.getBlockState(pos)
        val block = blockState.block
        return block.getBlockHardness(blockState, mc.world, pos) != -1.0f
    }

    companion object {
        var isPlacing = false
    }
}