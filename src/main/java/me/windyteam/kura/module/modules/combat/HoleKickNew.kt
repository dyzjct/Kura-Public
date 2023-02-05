package me.windyteam.kura.module.modules.combat

import com.mojang.realmsclient.gui.ChatFormatting
import me.windyteam.kura.concurrent.utils.Timer
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.Setting
import me.windyteam.kura.utils.MathUtil
import me.windyteam.kura.utils.block.BlockUtil
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.inventory.InventoryUtil
import me.windyteam.kura.utils.mc.ChatUtil
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.block.BlockFire
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

@Module.Info(name = "HoleKickerNew", category = Category.COMBAT)
class HoleKickNew : Module() {
    private val delay: Setting<Int> = isetting("Place Speed", 50, 0, 250)
    private val range: Setting<Int> = isetting("Range", 5, 1, 16)
    private val blocksPerPlace: Setting<Int> = isetting("placeTicks", 8, 1, 30)
    private val timer = Timer()
    var target: EntityPlayer? = null
    private var didPlace = false
    private var isSneaking = false
    private var placements = 0
    override fun onEnable() {
        if (InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK) == -1) {
            ChatUtil.sendMessage("<" + "HoleKicker" + "> " + ChatFormatting.RED + "Inventory Not Found REDSTONE_BLOCK!!")
            disable()
            return
        }
        if (InventoryUtil.findHotbarBlock(Blocks.PISTON) == -1) {
            ChatUtil.sendMessage("<" + "HoleKicker" + "> " + ChatFormatting.RED + "Inventory Not Found Piston!!")
            disable()
            return
        }
        if (fullNullCheck()) {
            return
        }
        EntityUtil.getRoundedBlockPos(mc.player)
    }

    fun onTick() {
        doPiston()
        toggle()
    }

    override fun getHudInfo(): String? {
        return if (target != null) {
            target!!.name
        } else null
    }

    override fun onDisable() {
        isPlacing = false
        isSneaking = EntityUtil.stopSneaking(isSneaking)
    }

    private fun doPiston() {
        if (this.check()) {
            return
        }
        doPistonTrap()
        if (didPlace) {
            timer.reset()
        }
    }

    private fun doPistonTrap() {
        val a = mc.player.rotationPitch
        val b = mc.player.inventory.currentItem
        val c = target!!.positionVector
        if (checkList(c, EntityUtil.getVarOffsets(0, 1, 0)) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))) {
            if (checkList(c, EntityUtil.getVarOffsets(1, 1, 0)) && checkList(
                    c, EntityUtil.getVarOffsets(-1, 1, 0)
                ) && checkList(c, EntityUtil.getVarOffsets(-1, 2, 0))
            ) {
                // x = "-955" y = "3" x = "647"
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON as Block)
                mc.playerController.updateController()
                mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, a, true) as Packet<*>)
                placeList(c, EntityUtil.getVarOffsets(1, 1, 0))
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
                mc.playerController.updateController()
                mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, a, true) as Packet<*>)
                placeList(c, EntityUtil.getVarOffsets(1, 2, 0))


                /*
    These girls are only "cute" for you because you missed out on teenage love,and now any girl that looks underdeveloped is more,
    attractive to you because you long for simpler times, wishing you could have spent long worryfree sunmmer days with you teen girlfriend.
    Our goal now is to try to write code and temporarily abandon the love of children and girls
    */
            } else if (checkList(c, EntityUtil.getVarOffsets(0, 1, -1)) && checkList(
                    c, EntityUtil.getVarOffsets(1, 1, 1)
                ) && checkList(c, EntityUtil.getVarOffsets(1, 2, 1))
            ) {
                // x = "-956" y = "3" x = "646"
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON as Block)
                mc.playerController.updateController()
                mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, a, true) as Packet<*>)
                placeList(c, EntityUtil.getVarOffsets(0, 1, -1))
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
                mc.playerController.updateController()
                mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, a, true) as Packet<*>)
                placeList(c, EntityUtil.getVarOffsets(0, 2, -1))
            }
        }

        /*
    These girls are only "cute" for you because you missed out on teenage love,and now any girl that looks underdeveloped is more,
    attractive to you because you long for simpler times, wishing you could have spent long worryfree sunmmer days with you teen girlfriend.
    Our goal now is to try to write code and temporarily abandon the love of children and girls
    */if (checkList(c, EntityUtil.getVarOffsets(1, 1, 0)) && checkList(
                c, EntityUtil.getVarOffsets(-1, 1, 0)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            // x = "-957" y = "3" x = "647"
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON as Block)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(90.0f, a, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(-1, 1, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(90.0f, a, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(-1, 2, 0))
        }


        /*
    These girls are only "cute" for you because you missed out on teenage love,and now any girl that looks underdeveloped is more,
    attractive to you because you long for simpler times, wishing you could have spent long worryfree sunmmer days with you teen girlfriend.
    Our goal now is to try to write code and temporarily abandon the love of children and girls
    */if (checkList(c, EntityUtil.getVarOffsets(1, 1, 0)) && checkList(
                c, EntityUtil.getVarOffsets(-1, 1, 0)
            ) && checkList(c, EntityUtil.getVarOffsets(-1, 2, 0))
        ) {
            // x = "-955" y = "1" x = "647"
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, a, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(1, 0, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON as Block)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, a, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(1, 1, 0))
        }

        /*
    These girls are only "cute" for you because you missed out on teenage love,and now any girl that looks underdeveloped is more,
    attractive to you because you long for simpler times, wishing you could have spent long worryfree sunmmer days with you teen girlfriend.
    Our goal now is to try to write code and temporarily abandon the love of children and girls
    */if (checkList(c, EntityUtil.getVarOffsets(1, 1, 0)) && checkList(
                c, EntityUtil.getVarOffsets(-1, 1, 0)
            ) && checkList(c, EntityUtil.getVarOffsets(-1, 2, 0))
        ) {
            // x = "-956" y = "1" x = "646"
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK as Block)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, a, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(0, 0, -1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, a, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(0, 1, -1))
        }

        /*
    These girls are only "cute" for you because you missed out on teenage love,and now any girl that looks underdeveloped is more,
    attractive to you because you long for simpler times, wishing you could have spent long worryfree sunmmer days with you teen girlfriend.
    Our goal now is to try to write code and temporarily abandon the love of children and girls
    */if (checkList(c, EntityUtil.getVarOffsets(1, 1, 0)) && checkList(
                c, EntityUtil.getVarOffsets(-1, 1, 0)
            ) && checkList(c, EntityUtil.getVarOffsets(-1, 2, 0))
        ) {
            // x = "-957" y = "1" x = "647"
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK as Block)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(90.0f, a, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(-1, 0, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(90.0f, a, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(-1, 1, 0))
        }
        mc.player.inventory.currentItem = b
        mc.playerController.updateController()
    }

    private fun placeList(pos: Vec3d, list: Array<Vec3d>) {
        for (vec3d in list) {
            val position = BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z)
            placeBlock(position)
        }
    }

    private fun checkList(pos: Vec3d, list: Array<Vec3d>): Boolean {
        for (vec3d in list) {
            val position = BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z)
            val block = mc.world.getBlockState(position).block
            if (block is BlockAir || block is BlockFire) {
                return true
            }
        }
        return false
    }

    private fun check(): Boolean {
        isPlacing = false
        didPlace = false
        placements = 0
        isSneaking = EntityUtil.stopSneaking(isSneaking)
        target = getTarget((range.value as Int).toDouble())
        return target == null || !timer.passedMs((delay.value as Int).toLong())
    }

    private fun getTarget(range: Double): EntityPlayer? {
        var target: EntityPlayer? = null
        var distance = range
        for (player in mc.world.playerEntities) {
            if (EntityUtil.isntValid(player as Entity, range)) {
                continue
            }
            if (target == null) {
                target = player
                distance = mc.player.getDistanceSq(player as Entity)
            } else {
                if (mc.player.getDistanceSq(player as Entity) >= distance) {
                    continue
                }
                target = player
                distance = mc.player.getDistanceSq(player as Entity)
            }
        }
        return target
    }

    private fun placeBlock(pos: BlockPos) {
        if (placements < blocksPerPlace.value as Int && mc.player.getDistanceSq(pos) <= MathUtil.square(6.0)) {
            isPlacing = true
            isSneaking = BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, false, false, true)
            didPlace = true
            ++placements
        }
    }

    companion object {
        var isPlacing = false

        init {
            isPlacing = false
        }
    }
}