package me.windyteam.kura.module.modules.misc

import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.utils.TimerUtils
import me.windyteam.kura.utils.inventory.InventoryUtil
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.init.Items
import net.minecraft.inventory.ClickType
import net.minecraft.item.Item
import net.minecraft.network.play.client.CPacketClickWindow
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.util.EnumHand
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Mouse
import java.util.concurrent.ConcurrentLinkedQueue

@Module.Info(name = "MCP", category = Category.MISC)
object MCP : Module() {
    private var invSwapDelay = isetting("InvSwapDelay", 10, 0, 1000)
    private var task = ConcurrentLinkedQueue<CPacketClickWindow>()
    private var delay = TimerUtils()

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent.FastTick) {
        if (mc.currentScreen is GuiContainer) return
        if (task.isNotEmpty() && delay.passed(invSwapDelay.value)) {
            mc.connection!!.sendPacket(task.poll())
            mc.playerController.updateController()
        }
        if (Mouse.isButtonDown(2)) {
            if (mc.objectMouseOver.typeOfHit != RayTraceResult.Type.ENTITY && mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK) {
                val slot = InventoryUtil.getItemHotbar(Items.ENDER_PEARL)
                var intSlot = -1
                if (slot == -1) {
                    for (i in 9..35) {
                        if (mc.player.inventory.getStackInSlot(i).getItem() != Items.ENDER_PEARL) continue
                        intSlot = i
                        break
                    }
                    if (intSlot == -1) return
                    mc.connection!!.sendPacket(
                        CPacketClickWindow(
                            mc.player.inventoryContainer.windowId,
                            intSlot,
                            mc.playerController.currentPlayerItem,
                            ClickType.SWAP,
                            mc.player.inventory.getStackInSlot(intSlot),
                            mc.player.openContainer.getNextTransactionID(mc.player.inventory)
                        )
                    )
                    mc.connection!!.sendPacket(CPacketPlayerTryUseItem(getItem(Items.ENDER_PEARL)))
                    task.add(
                        CPacketClickWindow(
                            mc.player.inventoryContainer.windowId,
                            intSlot,
                            mc.playerController.currentPlayerItem,
                            ClickType.SWAP,
                            mc.player.inventory.getStackInSlot(intSlot),
                            mc.player.openContainer.getNextTransactionID(mc.player.inventory)
                        )
                    )
                    delay.reset()
                } else {
                    val oldSlot = mc.player.inventory.currentItem
                    mc.player.inventory.currentItem = slot
                    mc.connection!!.sendPacket(CPacketPlayerTryUseItem(getItem(Items.ENDER_PEARL)))
                    mc.player.inventory.currentItem = oldSlot
                }
            }
        }
    }

    fun getItem(item: Item) =
        if (mc.player!!.heldItemOffhand.item === item) EnumHand.OFF_HAND else EnumHand.MAIN_HAND
}