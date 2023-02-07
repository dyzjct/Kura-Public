package me.windyteam.kura.module.modules.chat

import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.Setting
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketChatMessage
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "ChatSuffix", category = Category.CHAT, description = "Custom Chat Suffix")
class ChatSuffix : Module() {
    private val suffix = msetting("Mode",Mode.Kura)
    var commands: Setting<Boolean> = bsetting("Command", false)
    @SubscribeEvent
    fun NMSL(event: PacketEvents.Send) {
        when (suffix.value){
            Mode.Kura -> CHAT_SUFFIX = " ⲕᴜʀꞅⲁ.ツ"
            Mode.Beta -> CHAT_SUFFIX = " ᴋᴜʀᴀ ʙᴇᴛᴀ"
            Mode.WuShuang -> CHAT_SUFFIX = " 吴爽Pro"
        }

        if (event.stage == 0) {
            if (event.getPacket<Packet<*>>() is CPacketChatMessage) {
                var s = (event.getPacket<Packet<*>>() as CPacketChatMessage).getMessage()
                if (s.startsWith("/") && !commands.value) return
                s += CHAT_SUFFIX
                if (s.length >= 256) s = s.substring(0, 256)
                (event.getPacket<Packet<*>>() as CPacketChatMessage).message = s
            }
        }
    }

    enum class Mode{
        Kura,Beta,WuShuang
    }

    companion object {
        var CHAT_SUFFIX:String? = null
    }
}