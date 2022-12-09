package me.dyzjct.kura.module.modules.combat;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.mc.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Module.Info(name = "AutoTotem", description = "null", category = Category.COMBAT)
public class AutoTotem extends Module {
    public static AutoTotem INSTANCE = new AutoTotem();
    public static Minecraft mc = Minecraft.getMinecraft();
    public Setting<Boolean> soft = bsetting("Soft", false);
    public Setting<Boolean> strict = bsetting("Strict", false);
    public int numOfTotems;
    public int preferredTotemSlot;

    public static Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
        return getInventorySlots(9);
    }

    public static Map<Integer, ItemStack> getInventorySlots(int current) {
        Map<Integer, ItemStack> fullInventorySlots = new HashMap<>();
        while (current <= 44) {
            fullInventorySlots.put(current, mc.player.inventoryContainer.getInventory().get(current));
            current++;
        }
        return fullInventorySlots;
    }

    @Override
    public void onUpdate() {

        if (mc.player == null) {
            return;
        }
        if (!findTotems()) {
            return;
        }
        if (mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        if (mc.currentScreen instanceof GuiInventory) {
            return;
        }

        if (soft.getValue()) {
            if (mc.player.getHeldItemOffhand().getItem().equals(Items.AIR)) {
                if (strict.getValue() && EntityUtil.isMoving()) {
                    mc.player.setVelocity(0, 0, 0);
                }
                mc.playerController.windowClick(0, preferredTotemSlot, 0, ClickType.PICKUP, mc.player);
                if (strict.getValue() && EntityUtil.isMoving()) {
                    mc.player.setVelocity(0, 0, 0);
                    return;
                }
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                if (strict.getValue() && EntityUtil.isMoving()) {
                    mc.player.setVelocity(0, 0, 0);
                    return;
                }
                mc.playerController.updateController();
            }

        } else {

            if (!mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING)) {
                boolean offhandEmptyPreSwitch = mc.player.getHeldItemOffhand().getItem().equals(Items.AIR);
                if (strict.getValue() && EntityUtil.isMoving()) {
                    mc.player.setVelocity(0, 0, 0);
                }
                mc.playerController.windowClick(0, preferredTotemSlot, 0, ClickType.PICKUP, mc.player);
                if (strict.getValue() && EntityUtil.isMoving()) {
                    mc.player.setVelocity(0, 0, 0);
                    return;
                }
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                if (strict.getValue() && EntityUtil.isMoving()) {
                    mc.player.setVelocity(0, 0, 0);
                    return;
                }
                if (!offhandEmptyPreSwitch) {
                    mc.playerController.windowClick(0, preferredTotemSlot, 0, ClickType.PICKUP, mc.player);
                }
                mc.playerController.updateController();

            }

        }

    }

    public boolean findTotems() {
        this.numOfTotems = 0;
        AtomicInteger preferredTotemSlotStackSize = new AtomicInteger();
        preferredTotemSlotStackSize.set(Integer.MIN_VALUE);
        getInventoryAndHotbarSlots().forEach((slotKey, slotValue) -> {
            int numOfTotemsInStack = 0;
            if (slotValue.getItem().equals(Items.TOTEM_OF_UNDYING)) {
                numOfTotemsInStack = slotValue.getCount();
                if (preferredTotemSlotStackSize.get() < numOfTotemsInStack) {
                    preferredTotemSlotStackSize.set(numOfTotemsInStack);
                    preferredTotemSlot = slotKey;
                }
            }

            this.numOfTotems = this.numOfTotems + numOfTotemsInStack;

        });
        if (mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING)) {
            this.numOfTotems = this.numOfTotems + mc.player.getHeldItemOffhand().getCount();
        }
        return this.numOfTotems != 0;
    }

    @Override
    public String getHudInfo() {
        return "[" + this.numOfTotems + "]";
    }
}
