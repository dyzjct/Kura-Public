package me.windyteam.kura.module.modules.combat

import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.Setting
import me.windyteam.kura.utils.mc.EntityUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.init.Items
import net.minecraft.inventory.ClickType
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.CPacketPlayer
import java.util.concurrent.atomic.AtomicInteger

@Module.Info(name = "AutoTotem", description = "null", category = Category.COMBAT)
object AutoTotem : Module() {
    var soft: Setting<Boolean> = bsetting("Soft", false)
    private var strict: Setting<Boolean> = bsetting("Strict", false)
    private var numOfTotems = 0
    private var preferredTotemSlot = 0
    override fun onUpdate() {
        if (mc.player == null) {
            return
        }
        if (!findTotems()) {
            return
        }
        if (mc.currentScreen is GuiContainer && mc.currentScreen !is GuiInventory) {
            return
        }
        if (mc.currentScreen is GuiInventory) {
            return
        }
        if (soft.value) {
            if (mc.player.heldItemOffhand.getItem() == Items.AIR) {
                if (strict.value){
                    Module.mc.player.connection.sendPacket(
                        CPacketPlayer.Position(
                            Module.mc.player.posX,
                            Module.mc.player.posY - 10,
                            Module.mc.player.posZ,
                            false
                        )
                    )
                }
                if (strict.value && EntityUtil.isMoving()) {
                    mc.player.setVelocity(0.0, 0.0, 0.0)
                }
                mc.playerController.windowClick(0, preferredTotemSlot, 0, ClickType.PICKUP, mc.player)
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player)
                mc.playerController.updateController()
                if (strict.value){
                    Module.mc.player.connection.sendPacket(
                        CPacketPlayer.Position(
                            Module.mc.player.posX,
                            Module.mc.player.posY + 0,
                            Module.mc.player.posZ,
                            false
                        )
                    )
                }
            }
        } else {
            if (mc.player.heldItemOffhand.getItem() != Items.TOTEM_OF_UNDYING) {
                val offhandEmptyPreSwitch = mc.player.heldItemOffhand.getItem() == Items.AIR
                if (strict.value){
                    Module.mc.player.connection.sendPacket(
                        CPacketPlayer.Position(
                            Module.mc.player.posX,
                            Module.mc.player.posY - 10,
                            Module.mc.player.posZ,
                            false
                        )
                    )
                }
                if (strict.value && EntityUtil.isMoving()) {
                    mc.player.setVelocity(0.0, 0.0, 0.0)
                }
                mc.playerController.windowClick(0, preferredTotemSlot, 0, ClickType.PICKUP, mc.player)
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player)
                if (!offhandEmptyPreSwitch) {
                    mc.playerController.windowClick(0, preferredTotemSlot, 0, ClickType.PICKUP, mc.player)
                }
                mc.playerController.updateController()
                if (strict.value){
                    Module.mc.player.connection.sendPacket(
                        CPacketPlayer.Position(
                            Module.mc.player.posX,
                            Module.mc.player.posY + 0,
                            Module.mc.player.posZ,
                            false
                        )
                    )
                }
            }
        }
    }

    private fun findTotems(): Boolean {
        numOfTotems = 0
        val preferredTotemSlotStackSize = AtomicInteger()
        preferredTotemSlotStackSize.set(Int.MIN_VALUE)
        inventoryAndHotbarSlots.forEach { (slotKey: Int, slotValue: ItemStack) ->
            var numOfTotemsInStack = 0
            if (slotValue.getItem() == Items.TOTEM_OF_UNDYING) {
                numOfTotemsInStack = slotValue.count
                if (preferredTotemSlotStackSize.get() < numOfTotemsInStack) {
                    preferredTotemSlotStackSize.set(numOfTotemsInStack)
                    preferredTotemSlot = slotKey
                }
            }
            numOfTotems += numOfTotemsInStack
        }
        if (mc.player.heldItemOffhand.getItem() == Items.TOTEM_OF_UNDYING) {
            numOfTotems += mc.player.heldItemOffhand.count
        }
        return numOfTotems != 0
    }

    override fun getHudInfo(): String {
        return "[$numOfTotems]"
    }

    private var mc = Minecraft.getMinecraft()
    private val inventoryAndHotbarSlots: Map<Int, ItemStack>
        get() = getInventorySlots(9)

    private fun getInventorySlots(current: Int): Map<Int, ItemStack> {
        var current = current
        val fullInventorySlots: MutableMap<Int, ItemStack> = HashMap()
        while (current <= 44) {
            fullInventorySlots[current] = mc.player.inventoryContainer.inventory[current]
            current++
        }
        return fullInventorySlots
    }
}