package me.windyteam.kura.module.modules.misc

import me.windyteam.kura.event.events.PlayerDamageBlockEvent
import me.windyteam.kura.event.events.player.PacketEvent
import me.windyteam.kura.event.events.render.RenderEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.BooleanSetting
import me.windyteam.kura.setting.FloatSetting
import me.windyteam.kura.setting.Setting
import me.windyteam.kura.utils.block.BlockUtil2
import me.windyteam.kura.utils.Timer
import me.windyteam.kura.utils.inventory.InventoryUtil
import me.windyteam.kura.utils.render.RenderUtil
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Mouse
import java.awt.Color

@Module.Info(name = "InstantMine", category = Category.MISC)
class InstantMine : Module() {
    private val breakSuccess = Timer()
    private val creatoveMode = bsetting("CreativeMode", true)
    private val ghosthand: Setting<Boolean> = bsetting("GhostHand", true).b(creatoveMode)
    private val render = bsetting("Fill", true)
    private val falpha = isetting("FillAlpha", 30, 0, 255).b(render)
    private val render2 = bsetting("Box", true)
    private val balpha = isetting("BoxAlpha", 100, 0, 255).b(render2)
    @JvmField
    var db: BooleanSetting = bsetting("Silent Double", true)
    val health: FloatSetting = fsetting("Health", 18.0f, 0.0f, 35.9f).b(db)
    private val red = isetting("Red", 255, 0, 255)
    private val green = isetting("Green", 255, 0, 255)
    private val blue = isetting("Blue", 255, 0, 255)
    private val alpha = isetting("BoxAlpha", 150, 0, 255)
    private val alpha2 = isetting("FillAlpha", 70, 0, 255)
    private val godBlocks =
        listOf(Blocks.AIR, Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.WATER, Blocks.BEDROCK)
    private var cancelStart = false
    private var empty = false
    private var facing: EnumFacing? = null
    private var slotMain2 = 0
    private var swithc2 = 0
    private var manxi = 0.0
    private var manxi2 = 0.0
    private val imerS = Timer()
    private val imerS2 = Timer()
    var times = 0L
    override fun onEnable() {
        INSTANCE = this
    }

    override fun onUpdate() {
        if (fullNullCheck()) return
        val slotMain: Int
        if (mc.player.isCreative) {
            return
        }
        slotMain2 = mc.player.inventory.currentItem
        if (ticked in 0..86) {
            ++ticked
        }
        if (breakPos2 == null) {
            manxi2 = 0.0
        }
        if (breakPos2 != null && (ticked >= 65 || ticked >= 20 && mc.world.getBlockState(breakPos!!).block === Blocks.ENDER_CHEST)) {
            if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() === Items.GOLDEN_APPLE || mc.player.getHeldItem(
                    EnumHand.MAIN_HAND
                ).getItem() === Items.CHORUS_FRUIT
            ) {
                if (!Mouse.isButtonDown(1)) {
                    if (mc.player.health + mc.player.getAbsorptionAmount() >= health.value) {
                        if (InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE) != -1 && db.value) {
                            mc.player.connection.sendPacket(CPacketHeldItemChange(InventoryUtil.getItemHotbars(Items.DIAMOND_PICKAXE)) as Packet<*>)
                            swithc2 = 1
                            ++ticked
                        }
                    } else if (swithc2 == 1) {
                        mc.player.connection.sendPacket(CPacketHeldItemChange(slotMain2) as Packet<*>)
                        swithc2 = 0
                    }
                } else if (swithc2 == 1) {
                    mc.player.connection.sendPacket(CPacketHeldItemChange(slotMain2) as Packet<*>)
                    swithc2 = 0
                }
            } else if (mc.player.health + mc.player.getAbsorptionAmount() >= health.value) {
                if (InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE) != -1 && db.value) {
                    mc.player.connection.sendPacket(CPacketHeldItemChange(InventoryUtil.getItemHotbars(Items.DIAMOND_PICKAXE)) as Packet<*>)
                    swithc2 = 1
                    ++ticked
                }
            } else if (swithc2 == 1) {
                mc.player.connection.sendPacket(CPacketHeldItemChange(slotMain2) as Packet<*>)
                swithc2 = 0
            }
        }
        if (breakPos2 != null && mc.world.getBlockState(breakPos2!!).block === Blocks.AIR) {
            if (swithc2 == 1) {
                mc.player.connection.sendPacket(CPacketHeldItemChange(slotMain2) as Packet<*>)
                swithc2 = 0
            }
            breakPos2 = null
            manxi2 = 0.0
            ticked = 0
        }
        if (ticked == 0) {
            manxi2 = 0.0
            breakPos2 = null
        }
        if (ticked >= 140) {
            if (swithc2 == 1) {
                mc.player.connection.sendPacket(CPacketHeldItemChange(slotMain2) as Packet<*>)
                swithc2 = 0
            }
            manxi2 = 0.0
            breakPos2 = null
            ticked = 0
        }
        if (breakPos != null && mc.world.getBlockState(breakPos!!).block === Blocks.AIR && breakPos2 == null) {
            ticked = 0
        }
        if (fullNullCheck()) {
            return
        }
        if (!creatoveMode.value) {
            return
        }
        if (!cancelStart) {
            return
        }
        if (godBlocks.contains(mc.world.getBlockState(breakPos!!).block)) {
            return
        }
        if (mc.world.getBlockState(breakPos!!).block !== Blocks.WEB) {
            if (ghosthand.value && InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE) != -1 && InventoryUtil.getItemHotbars(
                    Items.DIAMOND_PICKAXE
                ) != -1
            ) {
                slotMain = mc.player.inventory.currentItem
                if (mc.world.getBlockState(breakPos!!).block === Blocks.OBSIDIAN) {
                    if (!breakSuccess.passedMs(1234L)) {
                        return
                    }
                    mc.player.inventory.currentItem = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE)
                    mc.playerController.updateController()
                    mc.player.connection.sendPacket(
                        CPacketPlayerDigging(
                            CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos!!, facing!!
                        ) as Packet<*>
                    )
                    mc.player.inventory.currentItem = slotMain
                    mc.playerController.updateController()
                    return
                }
                mc.player.inventory.currentItem = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE)
                mc.playerController.updateController()
                mc.player.connection.sendPacket(
                    CPacketPlayerDigging(
                        CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos!!, facing!!
                    ) as Packet<*>
                )
                mc.player.inventory.currentItem = slotMain
                mc.playerController.updateController()
                return
            }
        } else if (ghosthand.value && InventoryUtil.getItemHotbar(Items.DIAMOND_SWORD) != -1 && InventoryUtil.getItemHotbars(
                Items.DIAMOND_SWORD
            ) != -1
        ) {
            slotMain = mc.player.inventory.currentItem
            mc.player.inventory.currentItem = InventoryUtil.getItemHotbar(Items.DIAMOND_SWORD)
            mc.playerController.updateController()
            mc.player.connection.sendPacket(
                CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos!!, facing!!
                ) as Packet<*>
            )
            mc.player.inventory.currentItem = slotMain
            mc.playerController.updateController()
            return
        }
        mc.player.connection.sendPacket(
            CPacketPlayerDigging(
                CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos!!, facing!!
            ) as Packet<*>
        )
    }

    override fun onWorldRender(event: RenderEvent) {
        if (!mc.player.isCreative) {
            var axisAlignedBB1: AxisAlignedBB
            var progressValZ: Double
            var progressValY: Double
            var progressValX: Double
            var centerZ: Double
            var centerY: Double
            var centerX: Double
            var axisAlignedBB: AxisAlignedBB
            if (breakPos2 != null) {
                axisAlignedBB =
                    mc.world.getBlockState(breakPos2!!).getSelectedBoundingBox(mc.world as World, breakPos2!!)
                centerX = axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) / 2.0
                centerY = axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) / 2.0
                centerZ = axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) / 2.0
                progressValX = instance!!.manxi2 * ((axisAlignedBB.maxX - centerX) / 10.0)
                progressValY = instance!!.manxi2 * ((axisAlignedBB.maxY - centerY) / 10.0)
                progressValZ = instance!!.manxi2 * ((axisAlignedBB.maxZ - centerZ) / 10.0)
                axisAlignedBB1 = AxisAlignedBB(
                    centerX - progressValX,
                    centerY - progressValY,
                    centerZ - progressValZ,
                    centerX + progressValX,
                    centerY + progressValY,
                    centerZ + progressValZ
                )
                if (breakPos != null) {
                    if (breakPos2 != breakPos as Any?) {
                        RenderUtil.drawBBBox(
                            axisAlignedBB1, Color(red.value, green.value, blue.value, alpha.value), alpha.value
                        )
                        RenderUtil.drawBBFill(
                            axisAlignedBB1, Color(red.value, green.value, blue.value, alpha2.value), alpha2.value
                        )
                    }
                } else {
                    RenderUtil.drawBBBox(
                        axisAlignedBB1, Color(red.value, green.value, blue.value, alpha.value), alpha.value
                    )
                    RenderUtil.drawBBFill(
                        axisAlignedBB1, Color(red.value, green.value, blue.value, alpha2.value), alpha2.value
                    )
                }
            }
            if (creatoveMode.value && cancelStart) {
                if (godBlocks.contains(mc.world.getBlockState(breakPos!!).block)) {
                    empty = true
                }
                if (imerS.passedMs(15L)) {
                    if (manxi <= 10.0) {
                        manxi += 0.11
                    }
                    imerS.reset()
                }
                if (imerS2.passedMs(22L)) {
                    if (manxi2 in 0.0..10.0) {
                        manxi2 += 0.11
                    }
                    imerS2.reset()
                }
                axisAlignedBB = mc.world.getBlockState(breakPos!!).getSelectedBoundingBox(mc.world as World, breakPos!!)
                centerX = axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) / 2.0
                centerY = axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) / 2.0
                centerZ = axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) / 2.0
                progressValX = manxi * ((axisAlignedBB.maxX - centerX) / 10.0)
                progressValY = manxi * ((axisAlignedBB.maxY - centerY) / 10.0)
                progressValZ = manxi * ((axisAlignedBB.maxZ - centerZ) / 10.0)
                axisAlignedBB1 = AxisAlignedBB(
                    centerX - progressValX,
                    centerY - progressValY,
                    centerZ - progressValZ,
                    centerX + progressValX,
                    centerY + progressValY,
                    centerZ + progressValZ
                )
                if (render.value) {
                    RenderUtil.drawBBFill(
                        axisAlignedBB1, Color(if (empty) 0 else 255, if (empty) 255 else 0, 0, 255), falpha.value
                    )
                }
                if (render2.value) {
                    RenderUtil.drawBBBox(
                        axisAlignedBB1, Color(if (empty) 0 else 255, if (empty) 255 else 0, 0, 255), balpha.value
                    )
                }
            }
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
        event.isCanceled = cancelStart
    }

    @SubscribeEvent
    fun onBlockEvent(event: PlayerDamageBlockEvent) {
        if (fullNullCheck()) {
            return
        }
        if (mc.player.isCreative) {
            return
        }
        if (!BlockUtil2.canBreak(event.pos)) {
            return
        }
        if (breakPos != null && breakPos!!.getX() == event.pos.getX() && breakPos!!.getY() == event.pos.getY() && breakPos!!.getZ() == event.pos.getZ()) {
            return
        }
        if (ticked == 0) {
            ticked = 1
        }
        if (manxi2 == 0.0) {
            manxi2 = 0.11
        }
        if (breakPos != null && breakPos2 == null && mc.world.getBlockState(breakPos!!).block !== Blocks.AIR) {
            breakPos2 = breakPos
        }
        if (breakPos == null && breakPos2 == null) {
            breakPos2 = event.pos
        }
        manxi = 0.0
        empty = false
        cancelStart = false
        breakPos = event.pos
        breakSuccess.reset()
        facing = event.facing
        if (breakPos == null) {
            return
        }
        mc.player.swingArm(EnumHand.MAIN_HAND)
        mc.player.connection.sendPacket(
            CPacketPlayerDigging(
                CPacketPlayerDigging.Action.START_DESTROY_BLOCK, breakPos!!, facing!!
            ) as Packet<*>
        )
        cancelStart = true
        mc.player.connection.sendPacket(
            CPacketPlayerDigging(
                CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos!!, facing!!
            ) as Packet<*>
        )
        event.isCanceled = true
    }

    companion object {
        private var INSTANCE: InstantMine? = InstantMine()

        @JvmField
        var breakPos: BlockPos? = null

        @JvmField
        var breakPos2: BlockPos? = null
        var ticked = 0

        @JvmStatic
        val instance: InstantMine?
            get() {
                if (INSTANCE != null) {
                    return INSTANCE
                }
                INSTANCE = InstantMine()
                return INSTANCE
            }

        init {
            ticked = 0
        }
    }
}