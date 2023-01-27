package me.windyteam.kura.module.modules.misc;

import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.module.Category;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import me.windyteam.kura.module.Module;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Module.Info(name = "PacketAnalyzer", category = Category.MISC)
public class PacketAnalyzer extends Module {
    private final File packetSendFile = new File("PacketSend.txt");
    private final File packetReceiveFile = new File("PacketReceive.txt");

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        try {
            if (!packetSendFile.exists()) {
                packetSendFile.getParentFile().mkdirs();
                packetSendFile.createNewFile();
            }
            if (!packetReceiveFile.exists()) {
                packetReceiveFile.getParentFile().mkdirs();
                packetReceiveFile.createNewFile();
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvents.Send event) {
        if (fullNullCheck()) {
            return;
        }
        if (packetSendFile.exists()) {
            writeFile(packetSendFile, event.getPacket().toString());
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvents.Receive event) {
        if (fullNullCheck()) {
            return;
        }
        if (packetReceiveFile.exists()) {
            writeFile(packetReceiveFile, event.getPacket().toString());
        }
    }

    public static void writeFile(File file, String msg) {
        try {
            if (file != null) {
                PrintWriter saveJSon = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8));
                saveJSon.println(msg);
                saveJSon.close();
            }
        } catch (Exception ignored) {
        }
    }
}
