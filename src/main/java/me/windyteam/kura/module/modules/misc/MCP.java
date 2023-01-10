package me.windyteam.kura.module.modules.misc;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.utils.inventory.InventoryUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

@Module.Info(name="MCP", category=Category.MISC)
public class MCP
        extends Module {
    public int oldSlot = -1;

    @Override
    public void onEnable() {
        if (MCP.fullNullCheck()) {
            return;
        }
        this.oldSlot = MCP.mc.player.inventory.currentItem;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (MCP.fullNullCheck() || MCP.mc.currentScreen instanceof GuiContainer) {
            return;
        }
        this.oldSlot = MCP.mc.player.inventory.currentItem;
        if (Mouse.isButtonDown((int)2)) {
            RayTraceResult var2 = MCP.mc.objectMouseOver;
            if (var2.typeOfHit != RayTraceResult.Type.ENTITY && var2.typeOfHit != RayTraceResult.Type.BLOCK) {
                int p = InventoryUtils.findHotbarItem(ItemEnderPearl.class);
                if (p == -1) {
                    return;
                }
                InventoryUtils.switchToHotbarSlot(p, false);
                try {
                    MCP.mc.playerController.processRightClick((EntityPlayer)MCP.mc.player, (World)MCP.mc.world, MCP.mc.player.getHeldItemOffhand().getItem() == Items.ENDER_PEARL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                InventoryUtils.switchToHotbarSlot(this.oldSlot, false);
            }
        }
    }
}

