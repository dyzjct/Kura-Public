package me.dyzjct.kura.module.hud.huds;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dyzjct.kura.manager.FriendManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.Module.Info;
import me.dyzjct.kura.setting.BooleanSetting;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Info(name = "ChatNotifier", category = Category.CHAT)
public class ChatNotifier extends Module {
    public BooleanSetting friend = bsetting("Friend", true);

    public BooleanSetting playSound = bsetting("PlaySound", true);

    @SubscribeEvent
    public void onReceiveChat(ClientChatReceivedEvent event) {
        if (fullNullCheck())
            return;
        if (((Boolean)this.friend.getValue()).booleanValue() &&
                FriendManager.getFriendStringList() != null)
            FriendManager.getFriendStringList().forEach(p -> {
                if (p != null && event.getMessage().getUnformattedText().contains(p)) {
                    event.setMessage((ITextComponent)new TextComponentString(event.getMessage().getUnformattedText().replace(p, ChatFormatting.AQUA + "" + ChatFormatting.BOLD + p + ChatFormatting.WHITE)));
                    if (((Boolean)this.playSound.getValue()).booleanValue())
                        mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.1F, 0.5F);
                }
            });
        event.setMessage((ITextComponent)new TextComponentString(event.getMessage().getUnformattedText().replace(mc.player.getName(), ChatFormatting.RED + "" + ChatFormatting.BOLD + mc.player.getName() + ChatFormatting.WHITE)));
        if (event.getMessage().getUnformattedText().contains(mc.player.getName()) && !event.getMessage().getUnformattedText().contains("<" + mc.player.getName() + ">") && (
                (Boolean)this.playSound.getValue()).booleanValue())
            mc.player.playSound(SoundEvents.BLOCK_NOTE_BASS, 0.1F, 0.5F);
    }
}
