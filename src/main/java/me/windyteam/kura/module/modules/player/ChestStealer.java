package me.windyteam.kura.module.modules.player;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.setting.IntegerSetting;
import me.windyteam.kura.setting.ModeSetting;
import me.windyteam.kura.utils.Timer;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

@Module.Info(name = "ChestStealer", category = Category.PLAYER)
public class ChestStealer extends Module {
    private final me.windyteam.kura.utils.Timer Timer = new Timer();
    public ModeSetting<?> mode = msetting("Mode", Mode.Steal);
    public BooleanSetting Entity = bsetting("EntityChest", true);
    public IntegerSetting delay = isetting("Delay", 3, 0, 40);

    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        if (this.Timer.passed(delay.getValue() * 100)) {
            if (mc.currentScreen instanceof GuiChest || mc.currentScreen instanceof GuiShulkerBox) {
                GuiChest guiChest = (GuiChest) mc.currentScreen;
                Quickhandle(guiChest, guiChest.lowerChestInventory.getSizeInventory());
            } else if (mc.currentScreen instanceof GuiScreenHorseInventory && this.Entity.getValue()) {
                GuiScreenHorseInventory horseInventory = (GuiScreenHorseInventory) mc.currentScreen;
                Quickhandle(horseInventory, horseInventory.horseInventory.getSizeInventory());
            }
            Timer.reset();
        }
    }

    private void Quickhandle(GuiContainer guiContainer, int size) {
        for (int i = 0; i < size; i++) {
            ItemStack stack = guiContainer.inventorySlots.getInventory().get(i);
            if (StealorDrop(guiContainer.inventorySlots.windowId, i, stack))
                break;
        }
    }

    private boolean StealorDrop(int windowId, int i, ItemStack stack) {
        if (stack.isEmpty())
            return false;
        if (mode.getValue() == Mode.Steal) {
            mc.playerController.windowClick(windowId, i, 0, ClickType.QUICK_MOVE, mc.player);
            return true;
        }
        if (mode.getValue() == Mode.Swap) {
            for (int a = 0; a < 36; ++a) {
                if (mc.player.inventory.getStackInSlot(a).getItem() == Items.AIR) {
                    mc.playerController.windowClick(windowId, i, a, ClickType.SWAP, mc.player);
                }
            }
            return true;
        }
        if (mode.getValue() == Mode.Drop) {
            mc.playerController.windowClick(windowId, i, 1, ClickType.THROW, mc.player);
            return true;
        }
        return false;
    }

    public enum Mode {
        Steal,
        Store,
        Swap,
        Drop
    }
}