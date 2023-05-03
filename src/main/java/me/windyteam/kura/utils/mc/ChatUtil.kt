package me.windyteam.kura.utils.mc

import com.mojang.realmsclient.gui.ChatFormatting
import me.windyteam.kura.Kura
import me.windyteam.kura.Kura.Companion.fontRenderer
import me.windyteam.kura.gui.Notification
import me.windyteam.kura.module.modules.client.Colors.chatColorMode
import me.windyteam.kura.utils.Utils
import me.windyteam.kura.utils.Wrapper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.launchwrapper.LogWrapper
import net.minecraft.network.play.client.CPacketChatMessage
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

object ChatUtil {
    const val DeleteID = 94423
    var notifications = ArrayList<Notification>()
    @JvmField
    var SECTIONSIGN = '§'
    var BLACK = SECTIONSIGN.toString() + "0"
    var DARK_BLUE = SECTIONSIGN.toString() + "1"
    var DARK_GREEN = SECTIONSIGN.toString() + "2"
    var DARK_AQUA = SECTIONSIGN.toString() + "3"
    var DARK_RED = SECTIONSIGN.toString() + "4"
    var DARK_PURPLE = SECTIONSIGN.toString() + "5"
    var GOLD = SECTIONSIGN.toString() + "6"
    var GRAY = SECTIONSIGN.toString() + "7"
    var DARK_GRAY = SECTIONSIGN.toString() + "8"
    var BLUE = SECTIONSIGN.toString() + "9"
    var GREEN = SECTIONSIGN.toString() + "a"
    var AQUA = SECTIONSIGN.toString() + "b"
    var RED = SECTIONSIGN.toString() + "c"
    var LIGHT_PURPLE = SECTIONSIGN.toString() + "d"
    var YELLOW = SECTIONSIGN.toString() + "e"
    var WHITE = SECTIONSIGN.toString() + "f"
    var OBFUSCATED = SECTIONSIGN.toString() + "k"
    var BOLD = SECTIONSIGN.toString() + "l"
    var STRIKE_THROUGH = SECTIONSIGN.toString() + "m"
    var UNDER_LINE = SECTIONSIGN.toString() + "n"
    var ITALIC = SECTIONSIGN.toString() + "o"
    var RESET = SECTIONSIGN.toString() + "r"
    var colorMSG = SECTIONSIGN.toString() + "r"
    var colorKANJI = SECTIONSIGN.toString() + "b"
    var colorWarn = SECTIONSIGN.toString() + "6" + SECTIONSIGN + "l"
    var colorError = SECTIONSIGN.toString() + "4" + SECTIONSIGN + "l"
    var colorBracket = SECTIONSIGN.toString() + "7"
    fun clear() {
        notifications.clear()
    }

    @JvmStatic
    fun sendClientMessage(message: String?, type: Notification.Type?) {
        if (notifications.size > 8) {
            notifications.removeAt(0)
        }
        notifications.add(Notification(message, type))
    }

    fun drawNotifications() {
        try {
            val res = ScaledResolution(Minecraft.getMinecraft())
            var startY = res.scaledHeight - 25.0000
            val lastY = startY
            for (i in notifications.indices) {
                val not = notifications[i]
                if (not.shouldDelete()) {
                    notifications.remove(not)
                    var cao = 0.0000
                    while (cao > not.width) {
                        not.animationX = cao - not.width
                        cao--
                    }
                    startY += not.getHeight() + 3.0000
                }
                not.draw(startY, lastY)
                var cao = 0.0000
                while (cao < not.width) {
                    not.animationX = cao + not.width
                    cao++
                }
                startY -= not.getHeight() + 2.0000
            }
        } catch (ignored: Throwable) {
        }
    }

    @JvmStatic
    fun getStringWidth(text: String): Int {
        return fontRenderer!!.getStringWidth(
            text
        )
    }

    fun bracketBuilder(kanji: String): String {
        return RESET + colorBracket + "[" + RESET + kanji + colorBracket + "] " + RESET
    }

    fun printRawMessage(message: String?) {
        if (Utils.nullCheck()) return
        ChatMessage(message)
    }

    fun printMessage(message: String) {
        printRawMessage("[" + chatColorMode() + Kura.KANJI + ChatFormatting.RESET + "] " + RESET + colorMSG + message)
    }

    fun printWarnMessage(message: String) {
        printRawMessage(bracketBuilder(colorKANJI + Kura.KANJI) + bracketBuilder(colorWarn + "WARN") + RESET + colorMSG + message)
    }

    fun printErrorMessage(message: String) {
        printRawMessage(bracketBuilder(colorKANJI + Kura.KANJI) + bracketBuilder(colorError + "ERROR") + RESET + colorMSG + message)
    }

    fun sendRawMessage(message: String?) {
        printRawMessage(message)
    }

    @JvmStatic
    fun sendMessage(message: String) {
        printMessage(message)
    }

    @JvmStatic
    fun sendDisablerDebugMessage(message: String) {
        sendMessage("[DISABLER] $message")
    }

    @JvmStatic
    fun sendMessage(message: Array<String?>) {
        for (msg in message) {
            if (msg != null) {
                sendMessage(msg)
            }
        }
    }

    @JvmStatic
    fun sendErrorMessage(message: String) {
        printErrorMessage(message)
    }

    fun sendWarnMessage(message: String) {
        printWarnMessage(message)
    }

    @JvmStatic
    fun sendServerMessage(message: String) {
        if (Minecraft.getMinecraft().player != null) {
            Wrapper.getPlayer().connection.sendPacket(CPacketChatMessage(message))
        } else {
            LogWrapper.warning("Could not send server message: \"$message\"")
        }
    }

    @SideOnly(Side.CLIENT)
    fun sendSpamlessMessage(message: String?) {
        if (Utils.nullCheck()) return
        val chat = Wrapper.mc.ingameGUI.chatGUI
        chat.printChatMessageWithOptionalDeletion(TextComponentString(message), DeleteID)
    }

    @SideOnly(Side.CLIENT)
    fun sendSpamlessMessage(messageID: Int, message: String?) {
        if (Utils.nullCheck()) return
        val chat = Wrapper.mc.ingameGUI.chatGUI
        chat.printChatMessageWithOptionalDeletion(TextComponentString(message), messageID)
    }

    fun ChatMessage(message: String?) {
        Wrapper.mc.ingameGUI.chatGUI.printChatMessage(TextComponentString(message))
    }

    fun sendNoSpamErrorMessage(message: String) {
        sendNoSpamRawChatMessage(SECTIONSIGN.toString() + "7[" + SECTIONSIGN + "4" + SECTIONSIGN + "lERROR" + SECTIONSIGN + "7] " + SECTIONSIGN + "r" + message)
    }

    fun sendNoSpamErrorMessage(message: String, messageID: Int) {
        sendNoSpamRawChatMessage(
            SECTIONSIGN.toString() + "7[" + SECTIONSIGN + "4" + SECTIONSIGN + "lERROR" + SECTIONSIGN + "7] " + SECTIONSIGN + "r" + message,
            messageID
        )
    }

    fun sendNoSpamRawChatMessage(message: String?) {
        sendSpamlessMessage(message)
    }

    fun sendNoSpamRawChatMessage(message: String?, messageID: Int) {
        sendSpamlessMessage(messageID, message)
    }

    object NoSpam {
        fun sendMessage(message: String, messageID: Int) {
            sendRawChatMessage(bracketBuilder(colorKANJI + Kura.KANJI) + RESET + colorMSG + message, messageID)
        }

        @JvmStatic
        fun sendMessage(message: String) {
            sendRawChatMessage("[" + chatColorMode() + Kura.KANJI + ChatFormatting.RESET + "] " + RESET + colorMSG + message)
        }

        fun sendMessage(messages: Array<String?>) {
            sendMessage("")
            for (s in messages) sendRawChatMessage(s)
        }

        @JvmStatic
        fun sendErrorMessage(message: String) {
            sendRawChatMessage(bracketBuilder(colorKANJI + Kura.KANJI) + bracketBuilder(colorError + "ERROR") + RESET + colorMSG + message)
        }

        fun sendErrorMessage(message: String, messageID: Int) {
            sendRawChatMessage(
                bracketBuilder(colorKANJI + Kura.KANJI) + bracketBuilder(colorError + "ERROR") + RESET + colorMSG + message,
                messageID
            )
        }

        @JvmStatic
        fun sendWarnMessage(message: String) {
            sendRawChatMessage(bracketBuilder(colorKANJI + Kura.KANJI) + bracketBuilder(colorWarn + "WARN") + RESET + colorMSG + message)
        }

        fun sendWarnMessage(message: String, messageID: Int) {
            sendRawChatMessage(
                bracketBuilder(colorKANJI + Kura.KANJI) + bracketBuilder(colorWarn + "WARN") + RESET + colorMSG + message,
                messageID
            )
        }

        fun sendRawChatMessage(message: String?) {
            sendSpamlessMessage(message)
        }

        fun sendRawChatMessage(message: String?, messageID: Int) {
            sendSpamlessMessage(messageID, message)
        }
    }
}
