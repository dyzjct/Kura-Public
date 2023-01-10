package me.windyteam.kura.module.modules.combat;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

@Module.Info(name = "Pull32k", category = Category.COMBAT, description = "Pulls 32ks out of hoppers automatically")
public class Pull32k extends Module {
    public Setting<Server> server = msetting("ServerType", Server.Normal);
    boolean foundsword = false;

    @Override
    public void onUpdate() {
        boolean foundair = false;
        int enchantedSwordIndex = -1;
        if (server.getValue() == Server.Normal) {
            for (int i = 0; i < 9; i++) {
                ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) >= 25) {
                    enchantedSwordIndex = i;
                    foundsword = true;
                }
                if (!foundsword) {
                    enchantedSwordIndex = -1;
                    foundsword = false;
                }
            }
            if (enchantedSwordIndex != -1) {
                if (mc.player.inventory.currentItem != enchantedSwordIndex) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(enchantedSwordIndex));
                    mc.player.inventory.currentItem = enchantedSwordIndex;
                    mc.playerController.updateController();
                }
            }
            if (enchantedSwordIndex == -1 && mc.player.openContainer instanceof ContainerHopper && mc.player.openContainer.inventorySlots != null && !mc.player.openContainer.inventorySlots.isEmpty()) {
                for (int i = 0; i < 5; i++) {
                    if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i)) >= 20) {
                        enchantedSwordIndex = i;
                        break;
                    }
                }

                if (enchantedSwordIndex == -1) {
                    return;
                }
                for (int i = 0; i < 9; i++) {
                    ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
                    if (itemStack.getItem() instanceof ItemAir) {
                        if (mc.player.inventory.currentItem != i) {
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(i));
                            mc.player.inventory.currentItem = i;
                            mc.playerController.updateController();
                        }
                        foundair = true;
                        break;
                    }
                }
                if (foundair || checkStuff()) {
                    mc.playerController.windowClick(mc.player.openContainer.windowId, enchantedSwordIndex, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                }
            }
        } else if (server.getValue() == Server.Xin) {
            if (!(mc.player.openContainer instanceof ContainerHopper)) {
                return;
            }
            for (int i = 0; i < 9; i++) {
                ItemStack itemStack = mc.player.inventory.mainInventory.get(i);

                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) >= 5) {
                    enchantedSwordIndex = i;
                    foundsword = true;
                }
                if (!foundsword) {
                    enchantedSwordIndex = -1;
                    foundsword = false;
                }
            }
            if (enchantedSwordIndex != -1) {
                if (mc.player.inventory.currentItem != enchantedSwordIndex) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(enchantedSwordIndex));
                    mc.player.inventory.currentItem = enchantedSwordIndex;
                    mc.playerController.updateController();
                }
            }
            if (enchantedSwordIndex == -1 && mc.player.openContainer instanceof ContainerHopper && mc.player.openContainer.inventorySlots != null && !mc.player.openContainer.inventorySlots.isEmpty()) {
                for (int i = 0; i < 5; i++) {
                    if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i)) >= 5) {
                        enchantedSwordIndex = i;
                        break;
                    }
                }

                if (enchantedSwordIndex == -1) {
                    return;
                }
                for (int i = 0; i < 9; i++) {
                    ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
                    if (itemStack.getItem() instanceof ItemAir) {
                        if (mc.player.inventory.currentItem != i) {
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(i));
                            mc.player.inventory.currentItem = i;
                            mc.playerController.updateController();
                        }
                        foundair = true;
                        break;
                    }
                }
                if (foundair || checkStuff2()) {
                    mc.playerController.windowClick(mc.player.openContainer.windowId, enchantedSwordIndex, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                }
            }
        }
    }

    public boolean checkStuff() {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, mc.player.inventory.getCurrentItem()) == (short) 5;
    }

    public boolean checkStuff2() {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, mc.player.inventory.getCurrentItem()) == (short) 6;
    }

    public enum Server {
        Xin,
        Normal
    }
}
