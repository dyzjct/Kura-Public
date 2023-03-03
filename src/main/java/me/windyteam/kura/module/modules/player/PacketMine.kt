package me.windyteam.kura.module.modules.player

import me.windyteam.kura.event.events.block.BlockEvent
import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.event.events.render.RenderEvent
import me.windyteam.kura.manager.RotationManager
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.utils.TimerUtils
import me.windyteam.kura.utils.animations.BlockEasingRender
import me.windyteam.kura.utils.block.BlockInteractionHelper
import me.windyteam.kura.utils.block.BlockUtil
import me.windyteam.kura.utils.gl.MelonTessellator.drawBBBox
import me.windyteam.kura.utils.inventory.InventoryUtil
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent
import java.awt.Color

@Module.Info(name = "PacketMine", category = Category.PLAYER, description = "Better Mining")
object PacketMine : Module() {
    private var blockRenderSmooth = BlockEasingRender(BlockPos(0, 0, 0), 0.0f, 2000.0f)
    private var timerUtils = TimerUtils()
    private var strictTimer = TimerUtils()
    private var renderTimerUtils = TimerUtils()
    private var packet = bsetting("PacketOnly", false)
    private var swap = bsetting("SwapMine", true)
    private var swing = settings("Swing", false)
    private var rotate = bsetting("Rotate", false)
    private var render = bsetting("Render", true)
    private var alpha = isetting("Alpha", 30, 0, 255).b(render)
    private var range = isetting("Range", 6, 0, 10)
    private var strict = bsetting("Strict",true)
    private var superStrict = bsetting("SuperStrict",false).b(strict)
    private var currentBlockState: IBlockState? = null


    var facing: EnumFacing? = null


    @SubscribeEvent
    fun onDisconnect(event: ClientDisconnectionFromServerEvent?) {
        currentPos = null
        facing = null
        renderTimerUtils.reset()
    }

    @SubscribeEvent
    fun onBlockEvent(event: BlockEvent) {
        if (fullNullCheck() || InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE) == -1) {
            return
        }
        val oldSlot = mc.player.inventory.currentItem
        val picSlot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE)
        runCatching {
            if (currentPos != null) {
                if (!BlockUtil.canBreak(currentPos, false)) {
                    currentPos = null
                    return
                }
                if (currentPos!!.getX() == event.getPos().getX() && currentPos!!.getY() == event.getPos()
                        .getY() && currentPos!!.getZ() == event.getPos().getZ()
                ) {
                    return
                }
                if (currentPos == event.pos) {
                    if (strict.value) {
                        if (swap.value) {
                            mc.player.connection.sendPacket(
                                CPacketPlayerDigging(
                                    CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, currentPos!!, facing!!
                                )
                            )
                            mc.player.inventory.currentItem = picSlot
                            mc.player.connection.sendPacket(
                                CPacketPlayerDigging(
                                    CPacketPlayerDigging.Action.START_DESTROY_BLOCK, currentPos!!, facing!!
                                )
                            )
                            mc.player.inventory.currentItem = oldSlot
                        } else {
                            mc.player.connection.sendPacket(
                                CPacketPlayerDigging(
                                    CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, currentPos!!, facing!!
                                )
                            )
                            mc.player.connection.sendPacket(
                                CPacketPlayerDigging(
                                    CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos!!, facing!!
                                )
                            )
                        }
                    }
                }
            }
            currentPos = event.getPos()
            facing = event.getFacing()
            currentBlockState = mc.world.getBlockState(currentPos!!)
            renderTimerUtils.reset()
            timerUtils.reset()
            if (mc.connection != null && BlockUtil.canBreak(currentPos, false)) {
                blockRenderSmooth.updatePos(currentPos!!)
                blockRenderSmooth.reset()
                mc.player.swingArm(EnumHand.MAIN_HAND)
                mc.player.connection.sendPacket(
                    CPacketPlayerDigging(
                        CPacketPlayerDigging.Action.START_DESTROY_BLOCK, currentPos!!, facing!!
                    )
                )
            }
            event.isCanceled = true
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onTick(event: MotionUpdateEvent.Tick) {
        if (fullNullCheck() || InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE) == -1) return
        currentPos?.let {
            if (mc.world.getBlockState(it).block == Blocks.BEDROCK || mc.world.getBlockState(it).block == Blocks.AIR) {
                if (superStrict.value){
                    strictTimer.reset()
                    renderTimerUtils.reset()
                }
                return
            }

            if (strict.value) {
                if (!strictTimer.passedMs(2000L) && mc.world.getBlockState(it).block == Blocks.OBSIDIAN){
                    return
                } else if (!strictTimer.passedMs(1800) && mc.world.getBlockState(it).block == Blocks.ENDER_CHEST){
                    return
                } else if (!strictTimer.passedMs(400L)){
                    return
                }
            }

            if (mc.world.getBlockState(it).block == Blocks.AIR) return
            if (mc.player.getDistanceSq(it) > range.value * range.value) return
            val oldSlot = mc.player.inventory.currentItem
            switchToSlot(InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE))

            mc.player.connection.sendPacket(
                CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, it, facing!!
                )
            )

            if (strict.value){
                mc.player.connection.sendPacket(
                    CPacketPlayerDigging(
                        CPacketPlayerDigging.Action.START_DESTROY_BLOCK, it, facing!!
                    )
                )
            }

            switchToSlot(oldSlot)
            if (swing.value) mc.player.swingArm(EnumHand.MAIN_HAND)
            if (rotate.value) {
                event.setRotation(
                    BlockInteractionHelper.getLegitRotations(it.add(0.5,0.5,0.5))[0],
                    BlockInteractionHelper.getLegitRotations(it.add(0.5,0.5,0.5))[1]
                )
            }
            strictTimer.reset()
            renderTimerUtils.reset()
        }
    }

    override fun onWorldRender(event: RenderEvent) {
        if (fullNullCheck()) {
            return
        }
        val color = Color(0, 255, 0)
        val color2 = Color(255, 0, 0)
        if (render.value as Boolean && currentPos != null && BlockUtil.canBreak(currentPos, true)) {
            blockRenderSmooth.begin()
            drawBBBox(
                blockRenderSmooth.getFullUpdate(),
                if (renderTimerUtils.passed(1800)) color else color2,
                (alpha.value as Int),
                3.0f,
                true
            )
        } else if (currentPos == null) {
            blockRenderSmooth.end()
        }
    }

    override fun onDisable() {
        if (fullNullCheck()) {
            return
        }
        currentPos = null
        facing = null
        RotationManager.resetRotation()
    }

    override fun onEnable() {
        if (fullNullCheck()) {
            return
        }

        currentPos = null
        facing = null
        blockRenderSmooth.reset()
    }

    override fun getHudInfo(): String {
        return if (packet.value) "Packet" else "Instant"
    }

    private fun switchToSlot(slot: Int) {
        mc.player.connection.sendPacket(CPacketHeldItemChange(slot))
        mc.player.inventory.currentItem = slot
        mc.playerController.updateController()
    }

    @JvmField
    var currentPos: BlockPos? = null

}