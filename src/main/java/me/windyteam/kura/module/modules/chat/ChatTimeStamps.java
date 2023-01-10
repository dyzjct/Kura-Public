package me.windyteam.kura.module.modules.chat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

@Module.Info(name = "ChatTimeStamps", category = Category.CHAT)
public class ChatTimeStamps extends Module {

    private final Setting<Boolean> deco = bsetting("Deco", true);

    @SubscribeEvent
    public void awa(ClientChatReceivedEvent event) {
        TextComponentString newTextComponentString = new TextComponentString(ChatFormatting.LIGHT_PURPLE + (deco.getValue() ? "<" : "") + new SimpleDateFormat("k:mm").format(new Date()) + (deco.getValue() ? ">" : "") + ChatFormatting.RESET + " ");
        newTextComponentString.appendSibling(event.getMessage());
        event.setMessage(newTextComponentString);
    }

}
