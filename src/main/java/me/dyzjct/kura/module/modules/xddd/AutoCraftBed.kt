package me.dyzjct.kura.module.modules.xddd

import me.dyzjct.kura.event.events.entity.MotionUpdateEvent.Tick
import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.utils.NTMiku.TimerUtils
import me.dyzjct.kura.utils.inventory.InventoryUtil
import net.minecraft.block.Block
import net.minecraft.block.BlockPlanks
import net.minecraft.block.material.Material
import net.minecraft.client.gui.inventory.GuiCrafting
import net.minecraft.inventory.ClickType
import net.minecraft.item.ItemBlock
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.network.play.client.CPacketPlaceRecipe
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.concurrent.CopyOnWriteArrayList


@Module.Info(name = "AutoCraftBed", category = Category.XDDD)
class AutoCraftBed : Module() {
    private var craftMode = msetting("CraftMode", Mode.Smart)
    private var craftDelay = isetting("CraftDelay", 5, 0, 1000)
    private var slotList: CopyOnWriteArrayList<Int> = CopyOnWriteArrayList()
    private var craftTimer: TimerUtils? = TimerUtils()
    private var shouldCraft = false

    override fun getHudInfo(): String {
        return slotList.size.toString()
    }

    @SubscribeEvent
    fun onTick(event: Tick?) {
        if (fullNullCheck()) {
            return
        }
        val woolSlot: Int = findInventoryWool()
        val woodSlot: Int = InventoryUtil.findInInventory(
            { s -> s.getItem() is ItemBlock && (s.getItem() as ItemBlock).block is BlockPlanks },
            true
        )
        if (event!!.stage == 0) {
            for (slot in 0..45) {
                if (mc.player.inventory.getStackInSlot(slot).item != null) {
                    slotList.remove(slot)
                } else {
                    if (!slotList.contains(slot) && slotList.size < slot) {
                        slotList.add(slot)
                    }
                }
            }
            if (mc.currentScreen is GuiCrafting) {
                if (woolSlot == -1 || woodSlot == -1 || woolSlot == -2 || woodSlot == -2) {
                    //mc.displayGuiScreen(null)
                    //mc.currentScreen = null
                    shouldCraft = false
                    return
                }
                if (craftTimer!!.tickAndReset(craftDelay.value)) {
                    CraftingManager.getRecipe(
                        ResourceLocation("white_bed")
                    )?.let {
                        CPacketPlaceRecipe(
                            mc.player.openContainer.windowId, it, true
                        )
                    }?.let {
                        mc.player.connection.sendPacket(
                            it
                        )
                    }
                    slotList.forEach {
                        when (craftMode.value) {
                            Mode.Semi -> {
                                if (it != woodSlot && it != woolSlot) {
                                    mc.playerController.windowClick(
                                        mc.player.openContainer.windowId,
                                        0,
                                        it,
                                        ClickType.QUICK_MOVE,
                                        mc.player
                                    )
                                    mc.playerController.updateController()
                                    slotList.remove(it)
                                }
                            }

                            Mode.Auto -> {
                                mc.playerController.windowClick(
                                    mc.player.openContainer.windowId,
                                    0,
                                    0,
                                    ClickType.QUICK_MOVE,
                                    mc.player
                                )
                                mc.playerController.updateController()
                                slotList.remove(it)
                            }

                            Mode.Smart -> {
                                if (it != woodSlot && it != woolSlot) {
                                    mc.playerController.windowClick(
                                        mc.player.openContainer.windowId,
                                        0,
                                        0,
                                        ClickType.QUICK_MOVE,
                                        mc.player
                                    )
                                    mc.playerController.updateController()
                                    slotList.remove(it)
                                }
                            }
                        }
                        //mc.displayGuiScreen(null)
                    }
                }
            }
        }
    }

    private fun findInventoryWool(): Int {
        return InventoryUtil.findInInventory({ s ->
            if (s.getItem() is ItemBlock) {
                val block: Block = (s.getItem() as ItemBlock).block
                return@findInInventory block.defaultState.material === Material.CLOTH
            }
            false
        }, true)
    }

    enum class Mode {
        Auto, Semi, Smart
    }
}