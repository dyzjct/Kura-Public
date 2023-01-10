package me.windyteam.kura.module.modules.combat

import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.utils.inventory.InventoryUtil
import net.minecraft.client.gui.GuiHopper
import net.minecraft.init.Items
import net.minecraft.inventory.ClickType
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "Anti32kTotem", description = "null", category = Category.COMBAT)
class Anti32kTotem : Module() {
    private val AutoSwitch = bsetting("AutoSwitch", true)
    private val Health = dsetting("SwitchHealth", 15.0, 0.0, 36.0)
    override fun getHudInfo(): String {
        return "[" + getItems(Items.TOTEM_OF_UNDYING) + "]"
    }

    override fun onUpdate() {
        if (mc.currentScreen is GuiHopper || fullNullCheck()) {
            return
        }
        if (mc.player.inventory.getStackInSlot(0).getItem() === Items.TOTEM_OF_UNDYING) {
            return
        }
        for (i in 9..34) {
            if (mc.player.inventory.getStackInSlot(i).getItem() === Items.TOTEM_OF_UNDYING) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.SWAP, mc.player)
                break
            }
        }
    }

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent?) {
        if (fullNullCheck()) {
            return
        }
        if (HealthCheck() && AutoSwitch.value && mc.player.inventory.getStackInSlot(0)
                .getItem() === Items.TOTEM_OF_UNDYING
        ) {
            InventoryUtil.switchToHotbarSlot(0, false)
        }
    }

    fun HealthCheck(): Boolean {
        return (mc.player.health + mc.player.absorptionAmount).toDouble() <= Health.value as Double
    }

    companion object {
        fun getItems(i: Item): Int {
            return mc.player.inventory.mainInventory.stream()
                .filter { itemStack: ItemStack -> itemStack.getItem() === i }
                .mapToInt { obj: ItemStack -> obj.count }
                .sum() + mc.player.inventory.offHandInventory.stream()
                .filter { itemStack: ItemStack -> itemStack.getItem() === i }
                .mapToInt { obj: ItemStack -> obj.count }.sum()
        }
    }
}