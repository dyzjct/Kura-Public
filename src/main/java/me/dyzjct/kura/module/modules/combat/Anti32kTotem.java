package me.dyzjct.kura.module.modules.combat;

import me.dyzjct.kura.event.events.entity.MotionUpdateEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.modules.xddd.SmartOffHand;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.DoubleSetting;
import me.dyzjct.kura.setting.IntegerSetting;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "Anti32kTotem", description = "null", category = Category.COMBAT)
public class Anti32kTotem extends Module {
    private final BooleanSetting AutoSwitch = bsetting("AutoSwitch",true);
    private final DoubleSetting Health = dsetting("SwitchHealth",15.0,0.0,36.0);
    @Override
    public String getHudInfo() {
        return "[" + getItems(Items.TOTEM_OF_UNDYING) + "]";
    }

    public static int getItems(Item i) {
        return mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == i).mapToInt(ItemStack::getCount).sum() + mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == i).mapToInt(ItemStack::getCount).sum();
    }

    @Override
    public void onUpdate() {
        if (mc.currentScreen instanceof GuiHopper || fullNullCheck()) {
            return;
        }
        if (mc.player.inventory.getStackInSlot(0).getItem() == Items.TOTEM_OF_UNDYING) {
            return;
        }
        for (int i = 9; i < 35; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.SWAP, mc.player);
                break;
            }
        }
    }

    @SubscribeEvent
    public void onTick(MotionUpdateEvent event){
        if (fullNullCheck()){
            return;
        }
        if (HealthCheck()&&AutoSwitch.getValue()&&mc.player.inventory.getStackInSlot(0).getItem() == Items.TOTEM_OF_UNDYING){
            InventoryUtil.switchToHotbarSlot(0,false);
        }
    }
    public Boolean HealthCheck(){
        boolean lowHealth = (double)(mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= (Double)this.Health.getValue();
        return lowHealth;
    }
}
