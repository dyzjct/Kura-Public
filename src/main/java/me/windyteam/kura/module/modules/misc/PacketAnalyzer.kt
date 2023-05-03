package me.windyteam.kura.module.modules.misc

import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.manager.FileManager
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import net.minecraft.network.Packet
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.nio.charset.StandardCharsets

@Module.Info(name = "PacketAnalyzer", category = Category.MISC)
class PacketAnalyzer : Module() {
    private val packetSendFile = File(FileManager.PACKETSEND)
    private val packetReceiveFile = File(FileManager.PACKETRECEIVE)
    override fun onEnable() {
        if (fullNullCheck()) {
            return
        }
        runCatching {
            if (!packetSendFile.exists()) {
                packetSendFile.parentFile.mkdirs()
                packetSendFile.createNewFile()
            }
            if (!packetReceiveFile.exists()) {
                packetReceiveFile.parentFile.mkdirs()
                packetReceiveFile.createNewFile()
            }
        }
    }

    @SubscribeEvent
    fun onPacketSend(event: PacketEvents.Send) {
        if (fullNullCheck()) {
            return
        }
        runCatching {
            if (packetSendFile.exists()) {
                writeFile(packetSendFile, event.getPacket<Packet<*>>().toString())
            }
        }
    }

    @SubscribeEvent
    fun onPacketSend(event: PacketEvents.Receive) {
        if (fullNullCheck()) {
            return
        }
        runCatching {
            if (packetReceiveFile.exists()) {
                writeFile(packetReceiveFile, event.getPacket<Packet<*>>().toString())
            }
        }
    }

    companion object {
        fun writeFile(file: File?, msg: String?) {
            runCatching {
                if (file != null) {
                    val saveJSon = PrintWriter(OutputStreamWriter(FileOutputStream(file, true), StandardCharsets.UTF_8))
                    saveJSon.println(msg)
                    saveJSon.close()
                }
            }
        }
    }
}
