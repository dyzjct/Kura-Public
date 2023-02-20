package me.windyteam.kura.module.modules.player

import me.windyteam.kura.event.events.block.BlockEvent
import me.windyteam.kura.event.events.player.UpdateWalkingPlayerEvent
import me.windyteam.kura.event.events.render.RenderEvent
import me.windyteam.kura.manager.RotationManager
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.ModuleManager
import me.windyteam.kura.module.modules.combat.CevBreaker
import me.windyteam.kura.utils.TimerUtils
import me.windyteam.kura.utils.animations.BlockEasingRender
import me.windyteam.kura.utils.block.BlockInteractionHelper
import me.windyteam.kura.utils.block.BlockUtil
import me.windyteam.kura.utils.gl.MelonTessellator.drawBBBox
import me.windyteam.kura.utils.inventory.InventoryUtil
import net.minecraft.block.state.IBlockState
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.init.Blocks
import net.minecraft.init.Enchantments
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent
import java.awt.Color

@Module.Info(name = "PacketMine", category = Category.PLAYER, description = "Better Mining")
object PacketMine : Module() {
    private var blockRenderSmooth = BlockEasingRender(BlockPos(0, 0, 0), 0.0f, 2000.0f)
    private var timerUtils = TimerUtils()
    private var renderTimerUtils = TimerUtils()
    private var packet = bsetting("PacketOnly", false)
    private var spoofSwing = bsetting("SpoofSwing", false)
    private var startSwingTime = isetting("StartSwingTime", 1350, 0, 2000)
    private var swap = bsetting("SwapMine", true)
    private var resendClick = settings("ResendClick", false)
    private var rotate = bsetting("Rotate", false)
    private var render = bsetting("Render", true)
    private var alpha = isetting("Alpha", 30, 0, 255).b(render)
    private var currentBlockState: IBlockState? = null


    var facing: EnumFacing? = null
    var oldSlot = 0
    private var picSlot = 0
    private fun equipBestTool(blockState: IBlockState?) {
        var bestSlot = -1
        var max = 0.0
        for (i in 0..8) {
            var eff: Int
            val stack = mc.player.inventory.getStackInSlot(i)
            if (stack.isEmpty) continue
            var speed = stack.getDestroySpeed(blockState!!)
            if ((speed + if (EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack)
                        .also { eff = it } > 0
                ) eff.toDouble() + 1.0 else 0.0).toFloat().also { speed = it } <= max
            ) continue
            max = speed.toDouble()
            bestSlot = i
        }
        if (bestSlot != -1) {
            InventoryUtil.switchToHotbarSlot(bestSlot, false)
        }
    }

    @SubscribeEvent
    fun onDisconnect(event: ClientDisconnectionFromServerEvent?) {
        currentPos = null
        facing = null
        timerUtils.reset()
        renderTimerUtils.reset()
    }

    @SubscribeEvent
    fun onBlockEvent(event: BlockEvent) {
        if (fullNullCheck()) {
            return
        }
        oldSlot = mc.player.inventory.currentItem
        picSlot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE)
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
                    if (resendClick.value) {
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
            timerUtils.reset()
            renderTimerUtils.reset()
            if (mc.connection != null && BlockUtil.canBreak(currentPos, false)) {
                blockRenderSmooth.updatePos(currentPos!!)
                blockRenderSmooth.reset()
                mc.player.swingArm(EnumHand.MAIN_HAND)
                mc.player.connection.sendPacket(
                    CPacketPlayerDigging(
                        CPacketPlayerDigging.Action.START_DESTROY_BLOCK, currentPos!!, facing!!
                    )
                )
                mc.player.connection.sendPacket(
                    CPacketPlayerDigging(
                        CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos!!, facing!!
                    )
                )
            }
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun onUpdate(event: UpdateWalkingPlayerEvent) {
        if (fullNullCheck()) {
            return
        }
        oldSlot = mc.player.inventory.currentItem
        picSlot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE)
        runCatching {
            if (ModuleManager.getModuleByClass(CevBreaker::class.java).isEnabled) {
                currentPos = null
                return
            }

            if (BlockUtil.canBreak(currentPos, false)) {
                if (swap.value && currentBlockState!!.block != Blocks.SNOW_LAYER) {
                    equipBestTool(currentBlockState)
                } else if (swap.value && currentBlockState!!.block == Blocks.SNOW_LAYER) {
                    InventoryUtil.switchToHotbarSlot(picSlot, false)
                }

                if (!(packet.value) && timerUtils.passed(startSwingTime.value)) {
                    mc.player.connection.sendPacket(
                        CPacketPlayerDigging(
                            CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos!!, facing!!
                        )
                    )
                }

                if (swap.value) {
                    InventoryUtil.switchToHotbarSlot(oldSlot, false)
                }


                if (spoofSwing.value && timerUtils.passed((startSwingTime.value))) {
                    mc.player.connection.sendPacket(CPacketAnimation(EnumHand.MAIN_HAND))
                }
//                val side = BlockUtil.getFirstFacing(currentPos)
//                val neighbour: BlockPos = currentPos!!.offset(side)
//                val opposite = side.getOpposite()
//                val hitVec = Vec3d(neighbour as Vec3i).add(0.5, 0.5, 0.5)
//                    .add(Vec3d(opposite.getDirectionVec()).scale(0.5))
//                if (rotate.value) {
//                    RotationUtil.faceVector(hitVec, true)
//                }
                if (rotate.value && timerUtils.passed(1800.0f)) {
                    event.setRotation(
                        BlockInteractionHelper.getLegitRotations(Vec3d(currentPos!!).add(0.5, 0.0, 0.5))[0],
                        BlockInteractionHelper.getLegitRotations(
                            Vec3d(currentPos!!)
                        )[1]
                    )
                }
            }
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


    @JvmField
    var currentPos: BlockPos? = null

}