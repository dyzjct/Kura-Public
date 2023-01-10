package me.windyteam.kura.utils.fn;

import net.minecraft.client.Minecraft;

public interface MC {
  public static final Minecraft mc = Minecraft.getMinecraft();
  
  default boolean nullCheck() {
    return (mc.player == null || mc.world == null);
  }
  
  default double posX() {
    if (!nullCheck())
      return mc.player.posX; 
    return 0.0D;
  }
  
  default double posY() {
    if (!nullCheck())
      return mc.player.posY; 
    return 0.0D;
  }
  
  default double posZ() {
    if (!nullCheck())
      return mc.player.posZ; 
    return 0.0D;
  }
  
  default float rotationYaw() {
    if (!nullCheck())
      return mc.player.rotationYaw; 
    return 0.0F;
  }
  
  default float rotationPitch() {
    if (!nullCheck())
      return mc.player.rotationPitch; 
    return 0.0F;
  }
}
