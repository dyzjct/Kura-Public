package me.dyzjct.kura.module.modules.combat

import com.mojang.realmsclient.gui.ChatFormatting
import me.dyzjct.kura.manager.FriendManager
import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.setting.Setting
import me.dyzjct.kura.utils.mc.ChatUtil
import net.minecraft.entity.player.EntityPlayer
import java.awt.Image
import java.awt.Toolkit
import java.awt.TrayIcon

/**
 * Created on 26 October 2019 by hub
 * Updated 12 January 2020 by hub
 * Updated by polymer on 23/02/20
 */
@Module.Info(
    name = "VisualRange",
    description = "Shows players who enter and leave range in chat",
    category = Category.COMBAT
)
class VisualRange : Module() {
    private val leaving: Setting<Boolean> = bsetting("Leave", true)
    private var knownPlayers: MutableList<String>? = null
    override fun onUpdate() {
        if (mc.player == null) return
        val tickPlayerList: MutableList<String> = ArrayList()
        for (entity in mc.world.getLoadedEntityList()) {
            if (entity is EntityPlayer) tickPlayerList.add(entity.getName())
        }
        if (tickPlayerList.size > 0) {
            for (playerName in tickPlayerList) {
                if (playerName == mc.player.name) continue
                if (!knownPlayers!!.contains(playerName)) {
                    knownPlayers!!.add(playerName)
                    if (FriendManager.isFriend(playerName)) {
                        sendNotification(ChatFormatting.BLUE.toString() + playerName + ChatFormatting.RESET.toString() + " entered the Battlefield!")
                    } else {
                        sendNotification(ChatFormatting.RED.toString() + playerName + ChatFormatting.RESET.toString() + " entered the Battlefield!")
                    }
                    return
                }
            }
        }
        if (knownPlayers!!.size > 0) {
            for (playerName in knownPlayers!!) {
                if (!tickPlayerList.contains(playerName)) {
                    knownPlayers!!.remove(playerName)
                    if (leaving.value) {
                        if (FriendManager.isFriend(playerName)) {
                            sendNotification(ChatFormatting.GREEN.toString() + playerName + ChatFormatting.RESET.toString() + " left the Battlefield!")
                        } else {
                            sendNotification(ChatFormatting.RED.toString() + playerName + ChatFormatting.RESET.toString() + " left the Battlefield!")
                        }
                    }
                    return
                }
            }
        }
    }

    private fun sendNotification(s: String) {
        ChatUtil.sendMessage(s)
    }

    override fun onEnable() {
        knownPlayers = ArrayList()
    }
}