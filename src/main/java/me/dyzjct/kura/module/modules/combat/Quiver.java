package me.dyzjct.kura.module.modules.combat;

import me.dyzjct.kura.manager.RotationManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.PotionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Module.Info(name = "Quiver", category = Category.COMBAT)
public class Quiver extends Module {

    private final Setting<Integer> tickDelay = isetting("Delay", 3, 0, 8);

    public static List<Integer> getItemInventory(final Item item) {
        final List<Integer> ints = new ArrayList<>();
        for (int i = 9; i < 36; ++i) {
            if (item instanceof ItemBlock && ((ItemBlock) item).getBlock().equals(item)) {
                ints.add(i);
            }
        }
        if (ints.size() == 0) {
            ints.add(-1);
        }
        return ints;
    }

    public static int getItemHotbar(final Item input) {
        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory != null) {
                Item item = mc.player.inventory.getStackInSlot(i).getItem();
                if (Item.getIdFromItem(item) == Item.getIdFromItem(input)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        if (mc.player != null) {
            if (mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= this.tickDelay.getValue()) {
                float oao = -90f;
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.cameraYaw, oao, mc.player.onGround));
                mc.playerController.onStoppedUsingItem(mc.player);
            }
            final List<Integer> arrowSlots = getItemInventory(Items.TIPPED_ARROW);
            if (arrowSlots.get(0) == -1) {
                return;
            }
            int speedSlot = -1;
            int strengthSlot = -1;
            for (final Integer slot : arrowSlots) {
                if (Objects.requireNonNull(PotionUtils.getPotionFromItem(mc.player.inventory.getStackInSlot(slot)).getRegistryName()).getPath().contains("swiftness")) {
                    speedSlot = slot;
                } else {
                    if (!Objects.requireNonNull(PotionUtils.getPotionFromItem(mc.player.inventory.getStackInSlot(slot)).getRegistryName()).getPath().contains("strength")) {
                        continue;
                    }
                    strengthSlot = slot;
                }
            }
        }
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        RotationManager.resetRotation();
    }

    private int findBow() {
        return getItemHotbar(Items.BOW);
    }
}