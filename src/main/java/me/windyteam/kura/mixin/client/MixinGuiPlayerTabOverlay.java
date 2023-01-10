package me.windyteam.kura.mixin.client;

import me.windyteam.kura.module.modules.misc.ExtraTab;
import me.windyteam.kura.module.modules.render.TabFriends;
import me.windyteam.kura.module.modules.misc.ExtraTab;
import me.windyteam.kura.module.modules.render.TabFriends;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Created by 086 on 8/04/2018.
 */
@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay {

    @Redirect(method = {"renderPlayerlist"}, at = @At(value = "INVOKE", target = "Ljava/util/List;subList(II)Ljava/util/List;", remap = false))
    public List<NetworkPlayerInfo> subListHook(final List<NetworkPlayerInfo> list, final int fromIndex, final int toIndex) {
        return list.subList(fromIndex, ExtraTab.getINSTANCE().isEnabled() ? Math.min(ExtraTab.getINSTANCE().size.getValue(), list.size()) : toIndex);
    }

    @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
    public void getPlayerName(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable<String> returnable) {
        if (TabFriends.INSTANCE.isEnabled()) {
            returnable.cancel();
            returnable.setReturnValue(TabFriends.getPlayerName(networkPlayerInfoIn));
        }
    }

}
