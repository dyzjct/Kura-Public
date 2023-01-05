package me.dyzjct.kura.utils.fn;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import me.dyzjct.kura.utils.mc.ChatUtil;
import net.minecraft.client.Minecraft;

@Metadata(mv = {1, 6, 0}, k = 1, xi = 48, d1 = {"\000 \n\002\030\002\n\002\020\000\n\002\b\002\n\002\030\002\n\002\b\006\n\002\020\002\n\000\n\002\020\b\n\000\030\0002\0020\001B\005¢\006\002\020\002J\016\020\n\032\0020\0132\006\020\f\032\0020\rR\"\020\003\032\n \005*\004\030\0010\0040\004X\016¢\006\016\n\000\032\004\b\006\020\007\"\004\b\b\020\t¨\006\016"}, d2 = {"Lcnm/supermic/rainynight/util/PlayerSpoofUtil;", "", "()V", "mc", "Lnet/minecraft/client/Minecraft;", "kotlin.jvm.PlatformType", "getMc", "()Lnet/minecraft/client/Minecraft;", "setMc", "(Lnet/minecraft/client/Minecraft;)V", "spoofHotBar", "", "slot", "", "Finally"})
public final class PlayerSpoofUtil {
  private Minecraft mc = Minecraft.getMinecraft();
  
  public final Minecraft getMc() {
    return this.mc;
  }

  public final void spoofHotBar(int slot) {
    if (this.mc.player.inventory.currentItem == slot || slot < 0)
      return; 
    try {
      if (this.mc.isCallingFromMinecraftThread()) {
        Intrinsics.checkNotNullExpressionValue(null, "mc.playerController");

          (Minecraft.getMinecraft()).player.inventory.currentItem = slot;
          (Minecraft.getMinecraft()).playerController.updateController();
          Unit unit = Unit.INSTANCE;
        } 
      } catch (Exception e) {
      ChatUtil.sendMessage(Intrinsics.stringPlus("Error: ", e));
    } 
  }
}
