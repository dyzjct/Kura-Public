package me.windyteam.kura.module.modules.combat

import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.friend.FriendManager
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.utils.block.BlockUtils
import me.windyteam.kura.utils.entity.CrystalUtil
import me.windyteam.kura.utils.getTarget
import me.windyteam.kura.utils.inventory.InventoryUtil
import me.windyteam.kura.utils.mc.ChatUtil
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.MoverType
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "AutoTopCev", category = Category.COMBAT)
class AutoTopCev : Module() {
    private val range = dsetting("Range", 4.9, 0.0, 6.0)
    private var currentEntity: Entity? = null
    private var flag = false
    private var progress = 0
    private var sleep = 0
    private var civCounter = 0
    private var breakFlag = false
    private var target:EntityPlayer? = null

    private fun findItem(item: Item): Int {
        return if (item === Items.END_CRYSTAL && mc.player.heldItemOffhand.getItem() === Items.END_CRYSTAL) {
            999
        } else {
            for (i in 0..8) {
                if (mc.player.inventory.getStackInSlot(i).getItem() === item) {
                    return i
                }
            }
            -1
        }
    }

    override fun onEnable() {
        if (fullNullCheck()) return
        findTarget()
        progress = 0
        breakFlag = false
        flag = false
        civCounter = 0
        sleep = 0
        super.onEnable()
    }

    @SubscribeEvent
    fun onTick(event:MotionUpdateEvent.Tick) {
        if (fullNullCheck()) return
        val n = findItem(Items.DIAMOND_PICKAXE)
        val n2 = findItem(Items.END_CRYSTAL)
        val n3 = findMaterials(Blocks.OBSIDIAN)
        if (n3 == -1) return
        val objectArray = arrayOf(BlockPos(0, 0, 1), BlockPos(0, 1, 1), BlockPos(0, 2, 1), BlockPos(0, 2, 0))
        val n4 = InventoryUtil.getSlot()
        if (mc.player == null || mc.world == null) return
        target = getTarget(range.value)
        if (target == null) return
        val playerPos = BlockPos(target!!.posX,target!!.posY,target!!.posZ)
        var a = 2
        if (getBlock(playerPos.add(0,3,0)).block == Blocks.AIR && getBlock(playerPos.add(0,2,0)).block == Blocks.AIR){
            a = 2
        } else if (getBlock(playerPos.add(0,3,0)).block != Blocks.AIR){
            a = 3
        }
        if (n != -1 && n2 != -1 && n3 != -1) {
            if (currentEntity == null || currentEntity!!.getDistance(mc.player).toDouble() > range.value as Double) {
                findTarget()
            }
            if (currentEntity != null) {
                if (currentEntity!!.isDead) {
                    disable()
                    return
                }
                val entity = currentEntity
                if (entity is EntityPlayer && !FriendManager.isFriend(entity.getName())) {
//                    if (n2 == -1) {
//                        mc.player.inventory.offHandInventory.get(0).getItem();
//                        Item.getItemById(426);
//                    }
                    if (sleep > 0) {
                        --sleep
                    } else {
                        entity.move(MoverType.SELF, 0.0, -2.0, 0.0)
                        when (progress) {
                            0 -> {
                                val blockPos = BlockPos(entity)
                                val var17 = objectArray.size
                                var var10 = 0
                                while (var10 < var17) {
                                    val blockPos2 = objectArray[var10]
                                    if (listOf(*objectArray)
                                            .indexOf(blockPos2) != -1 && civCounter < 1
                                    ) {
                                        flag = true
                                        InventoryUtil.setSlot(n3)
                                    } else {
                                        InventoryUtil.setSlot(n3)
                                    }
                                    val blockUtils = BlockUtils.isPlaceable(blockPos.add(blockPos2), 0.0, true)
                                    blockUtils?.doPlace(true)
                                    ++var10
                                }
                                InventoryUtil.setSlot(n2)
                                CrystalUtil.placeCrystal(BlockPos(entity.posX, entity.posY + a+1, entity.posZ))
                                ++progress
                            }

                            1 -> {
                                InventoryUtil.setSlot(n)
                                mc.playerController.onPlayerDamageBlock(BlockPos(entity).add(0, a, 0), EnumFacing.UP)
                                mc.connection!!
                                    .sendPacket(
                                        CPacketPlayerDigging(
                                            CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                                            BlockPos(entity).add(0, a, 0),
                                            EnumFacing.UP
                                        )
                                    )
                                if (mc.world.isAirBlock(BlockPos(entity).add(0, a, 0))) {
                                    val var13: Iterator<*> = mc.world.loadedEntityList.iterator()
                                    while (var13.hasNext()) {
                                        val entity2 = var13.next() as Entity
                                        if (entity.getDistance(entity2)
                                                .toDouble() <= range.value as Double && entity2 is EntityEnderCrystal
                                        ) {
                                            mc.playerController.attackEntity(mc.player, entity2)
                                        }
                                    }
                                    breakFlag = true
                                }
                                if (civCounter < 1) {
                                    mc.playerController.onPlayerDamageBlock(
                                        BlockPos(entity).add(0, a, 0),
                                        EnumFacing.UP
                                    )
                                    sleep += 30
                                }
                                ++progress
                            }

                            2 -> {
                                var n5 = 0
                                val var8: Iterator<*> = mc.world.loadedEntityList.iterator()
                                while (var8.hasNext()) {
                                    val entity3 = var8.next() as Entity
                                    if (entity.getDistance(entity3)
                                            .toDouble() <= range.value as Double && entity3 is EntityEnderCrystal
                                    ) {
                                        mc.playerController.attackEntity(mc.player, entity3)
                                        ++n5
                                    }
                                }
                                if (n5 == 0 || flag) {
                                    ++progress
                                }
                            }

                            3 -> {
                                BlockUtils.doPlace(
                                    BlockUtils.isPlaceable(
                                        BlockPos(
                                            entity.posX,
                                            entity.posY + 2.0,
                                            entity.posZ
                                        ), 0.0, true
                                    ), true
                                )
                                InventoryUtil.setSlot(n3)
                                progress = 0
                                ++civCounter
                            }
                        }
                    }
                    InventoryUtil.setSlot(n4)
                    return
                }
                InventoryUtil.setSlot(n4)
            }
        } else {
            ChatUtil.sendMessage("Pix or Crystal or Obsidian No Material")
            disable()
        }
    }

    private fun findMaterials(block: Block): Int {
        for (i in 0..8) {
            if (mc.player.inventory.getStackInSlot(i).getItem() is ItemBlock && (mc.player.inventory.getStackInSlot(i)
                    .getItem() as ItemBlock).block === block
            ) {
                return i
            }
        }
        return -1
    }

    private fun findTarget() {
        try {
            currentEntity = mc.world.loadedEntityList.stream().filter { entity: Entity ->
                entity !== mc.player && entity is EntityLivingBase && entity.getDistance(
                    mc.player
                ).toDouble() < range.value && !FriendManager.isFriend(entity.getName())
            }
                .findFirst().orElse(null)
        } catch (e:Exception){
//            java.lang.HeMingZhuException
        }

    }

    private fun getBlock(block: BlockPos): IBlockState {
        return mc.world.getBlockState(block)
    }
}