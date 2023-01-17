package me.windyteam.kura.module.modules.player

import com.mojang.realmsclient.gui.ChatFormatting
import me.windyteam.kura.friend.FriendManager
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.Setting
import me.windyteam.kura.utils.mc.ChatUtil
import me.windyteam.kura.utils.mc.EntityUtil
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

@Module.Info(name = "Warner", category = Category.PLAYER)
class Warner : Module() {
    private val armorThreshold: Setting<Int> = isetting("Armor%", 20, 1, 100)
    private val notifySelf: Setting<Boolean> = bsetting("Self", true)
    private val notification: Setting<Boolean> = bsetting("Friends", true)
    private val entityArmorArraylist: MutableMap<EntityPlayer, Int> = HashMap()
    override fun onUpdate() {
        for (player in mc.world.playerEntities) {
            if (player.isDead || !FriendManager.isFriend(player.name)) continue
            for (stack in player.inventory.armorInventory) {
                if (stack == ItemStack.EMPTY) continue
                val percent = EntityUtil.getDamagePercent(stack)
                if (percent <= armorThreshold.value && !entityArmorArraylist.containsKey(player)) {
                    if (player === mc.player && notifySelf.value) {
                        ChatUtil.sendMessage(ChatFormatting.RED.toString() + "Your " + getArmorPieceName(stack) + " low dura!")
                    }
                    if (FriendManager.isFriend(player.name) && notification.value && player !== mc.player) {
                        mc.player.sendChatMessage(
                            "/msg " + player.name + " Yo, " + player.name + ", ur " + getArmorPieceName(
                                stack
                            ) + " low dura!"
                        )
                    }
                    entityArmorArraylist[player] = player.inventory.armorInventory.indexOf(stack)
                }
                if (!entityArmorArraylist.containsKey(player) || entityArmorArraylist[player] != player.inventory.armorInventory.indexOf(
                        stack
                    ) || percent <= armorThreshold.value
                ) continue
                entityArmorArraylist.remove(player)
            }
            if (!entityArmorArraylist.containsKey(player) || player.inventory.armorInventory[entityArmorArraylist[player]!!] != ItemStack.EMPTY) continue
            entityArmorArraylist.remove(player)
        }
    }

    private fun getArmorPieceName(stack: ItemStack): String {
        if (stack.getItem() === Items.DIAMOND_HELMET || stack.getItem() === Items.GOLDEN_HELMET || stack.getItem() === Items.IRON_HELMET || stack.getItem() === Items.CHAINMAIL_HELMET || stack.getItem() === Items.LEATHER_HELMET) {
            return "helmet is"
        }
        if (stack.getItem() === Items.DIAMOND_CHESTPLATE || stack.getItem() === Items.GOLDEN_CHESTPLATE || stack.getItem() === Items.IRON_CHESTPLATE || stack.getItem() === Items.CHAINMAIL_CHESTPLATE || stack.getItem() === Items.LEATHER_CHESTPLATE) {
            return "chest is"
        }
        return if (stack.getItem() === Items.DIAMOND_LEGGINGS || stack.getItem() === Items.GOLDEN_LEGGINGS || stack.getItem() === Items.IRON_LEGGINGS || stack.getItem() === Items.CHAINMAIL_LEGGINGS || stack.getItem() === Items.LEATHER_LEGGINGS) {
            "leggings are"
        } else "boots are"
    }
}