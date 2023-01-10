package me.windyteam.kura.module.modules.chat;

import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "ChatSuffix", category = Category.CHAT, description = "Custom Chat Suffix")
public class ChatSuffix extends Module {

    public static String CHAT_SUFFIX = "⇜ ᴋᴜʀᴀ";
    public Setting<Boolean> commands = bsetting("Command", false);

    @SubscribeEvent
    public void NMSL(PacketEvents.Send event) {
        if (event.getStage() == 0) {
            if (event.getPacket() instanceof CPacketChatMessage) {
                String s = ((CPacketChatMessage) event.getPacket()).getMessage();
                if (s.startsWith("/") && !commands.getValue()) return;
                s += CHAT_SUFFIX;
                if (s.length() >= 256) s = s.substring(0, 256);
                ((CPacketChatMessage) event.getPacket()).message = s;
            }
        }
    }

}

