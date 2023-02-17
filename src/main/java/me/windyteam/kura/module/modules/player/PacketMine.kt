package me.windyteam.kura.module.modules.player

import me.windyteam.kura.event.events.PlayerDamageBlockEvent
import me.windyteam.kura.event.events.block.BlockEvent
import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.event.events.player.PacketEvent
import me.windyteam.kura.event.events.render.RenderEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.Module.Info
import me.windyteam.kura.module.modules.misc.InstantMine
import me.windyteam.kura.utils.Timer
import me.windyteam.kura.utils.block.BlockUtil
import me.windyteam.kura.utils.gl.MelonTessellator
import me.windyteam.kura.utils.inventory.InventoryUtil
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color
import kotlin.math.abs

@Info(
    name = "PacketMine", category = Category.PLAYER, description = "Packet Mine"
)
object PacketMine : Module() {

    private val ghostHand = bsetting("GhostHand", true)
    private val red = isetting("Red", 255, 0, 255)
    private val green = isetting("Green", 255, 0, 255)
    private val blue = isetting("Blue", 255, 0, 255)
    private val alpha = isetting("Alpha", 150, 0, 255)
    private var lineWidth = fsetting("LineWidth", 2f, 1f, 3f)
    private var resendClick = bsetting("ResendClick",false)
    private var strict = bsetting("Strict",true)
    private var onlyPacket = bsetting("OnlyPacket",false)


    var breakPos: BlockPos? = null
    var facing: EnumFacing? = null
    val time = Timer()
    var cancel = false

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent.Tick) {
        if (fullNullCheck() || breakPos == null || strict.value) return
        if (!cancel) {
            return
        }
        if (getBlock(breakPos!!)!!.block != Blocks.AIR) {
            val oldSlot = mc.player.inventory.currentItem
            if (ghostHand.value) mc.player.inventory.currentItem = InventoryUtil.getItemHotbar(Items.DIAMOND_SWORD)
            mc.player.connection.sendPacket(
                CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, breakPos!!, facing!!
                ) as Packet<*>
            )
            mc.player.connection.sendPacket(
                CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos!!, facing!!
                ) as Packet<*>
            )
            if (ghostHand.value) mc.player.inventory.currentItem = oldSlot
        }
    }

    @SubscribeEvent
    fun onPacketSend(event: PacketEvent.Send) {
        if (fullNullCheck()) {
            return
        }
        if (mc.player.isCreative) {
            return
        }
        if (event.getPacket<Packet<*>>() !is CPacketPlayerDigging) {
            return
        }
        val packet = event.getPacket<Packet<*>>() as CPacketPlayerDigging
        if (packet.action != CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
            return
        }
        event.isCanceled = cancel
    }

    @SubscribeEvent
    fun onBlockEvent(event: BlockEvent) {
        if (fullNullCheck()) {
            return
        }
        if (mc.player.isCreative) {
            return
        }
        if (event.pos != Blocks.AIR && event.pos != Blocks.BEDROCK) {
            breakPos = event.pos
        }
        cancel = false
        if (breakPos != null) {
            if (!BlockUtil.canBreak(breakPos, false)) {
                breakPos = null
                return
            }
            if (breakPos == event.pos) {
                if (!onlyPacket.value) {
                    return
                } else {
                    if (resendClick.value) {
//                        if (cancelNonSafe.value && multiplier < 1f) {
//                            return
//                        }
                        if (ghostHand.value) {
                            mc.player.connection.sendPacket(
                                CPacketPlayerDigging(
                                    CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, breakPos!!, facing!!
                                ) as Packet<*>
                            )
                            val oldSlot = mc.player.inventory.currentItem
                            mc.player.inventory.currentItem = InventoryUtil.getItemHotbar(Items.DIAMOND_SWORD)
                            mc.player.connection.sendPacket(
                                CPacketPlayerDigging(
                                    CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos!!, facing!!
                                ) as Packet<*>
                            )
                            mc.player.inventory.currentItem = oldSlot
                            mc.playerController.updateController()
                        } else {
                            mc.player.connection.sendPacket(
                                CPacketPlayerDigging(
                                    CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, breakPos!!, facing!!
                                ) as Packet<*>
                            )
                            mc.player.connection.sendPacket(
                                CPacketPlayerDigging(
                                    CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos!!, facing!!
                                ) as Packet<*>
                            )
                        }
                    }
                }
            }
            cancel = true
        }

    }

    private fun getBlock(block: BlockPos): IBlockState? {
        return mc.world.getBlockState(block)
    }

    override fun onWorldRender(event: RenderEvent) {
        if (breakPos == null) return
        MelonTessellator.boxESP(
            breakPos!!,
            Color(red.value, green.value, blue.value),
            alpha.value,
            lineWidth.value,
            0f,
            1
        )
    }

}