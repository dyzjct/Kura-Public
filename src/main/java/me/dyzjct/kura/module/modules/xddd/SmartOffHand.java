package me.dyzjct.kura.module.modules.xddd;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.modules.combat.AutoTotem;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.DoubleSetting;
import me.dyzjct.kura.setting.ModeSetting;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.NTMiku.TimerUtils;
import me.dyzjct.kura.utils.entity.CrystalUtil;
import me.dyzjct.kura.utils.entity.HoleUtil;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.lwjgl.input.Mouse;

@Module.Info(name="SmartOffHand", category=Category.XDDD, description="StupidOffHand")
public class SmartOffHand
        extends Module {
    public static TimerUtils timerUtils = new TimerUtils();
    public int totems;
    public int count;
    ModeSetting<Mode> mode = this.msetting("Mode", Mode.Crystal);
    Setting<Integer> delay = this.isetting("Delay", 0, 0, 1000);
    BooleanSetting totem = this.bsetting("SwitchTotem", true);
    DoubleSetting sbHealth = this.dsetting("Health", 11.0, 0.0, 36.0);
    BooleanSetting autoSwitch = this.bsetting("SwitchGap", true);
    ModeSetting<SMode> switchMode = this.msetting("GapWhen", SMode.RClick).b(this.autoSwitch);
    BooleanSetting elytra = this.bsetting("CheckElytra", true);
    BooleanSetting holeCheck = this.bsetting("CheckHole", false);
    DoubleSetting holeSwitch = this.dsetting("HoleHealth", 8.0, 0.0, 36.0).b(this.holeCheck);
    BooleanSetting crystalCalculate = this.bsetting("CalculateDmg", true);
    DoubleSetting maxSelfDmg = this.dsetting("MaxSelfDmg", 26.0, 0.0, 36.0).b(this.crystalCalculate);

    @Override
    public void onUpdate() {
        boolean shouldSwitch;
        Item sOffhandItem;
        if (SmartOffHand.fullNullCheck()) {
            return;
        }
        if (!AutoTotem.INSTANCE.soft.getValue().booleanValue()) {
            AutoTotem.INSTANCE.soft.setValue(true);
        }
        int crystals = SmartOffHand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();
        if (SmartOffHand.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            crystals += SmartOffHand.mc.player.getHeldItemOffhand().getCount();
        }
        int gapple = SmartOffHand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();
        if (SmartOffHand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) {
            gapple += SmartOffHand.mc.player.getHeldItemOffhand().getCount();
        }
        this.totems = SmartOffHand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (SmartOffHand.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            ++this.totems;
        }
        Item item = null;
        if (!SmartOffHand.mc.player.getHeldItemOffhand().isEmpty()) {
            item = SmartOffHand.mc.player.getHeldItemOffhand().getItem();
        }
        this.count = item != null ? (item.equals(Items.END_CRYSTAL) ? crystals : (item.equals(Items.TOTEM_OF_UNDYING) ? this.totems : gapple)) : 0;
        Item handItem = SmartOffHand.mc.player.getHeldItemMainhand().getItem();
        Item offhandItem = ((Mode)((Object)this.mode.getValue())).equals((Object)Mode.Crystal) ? Items.END_CRYSTAL : Items.GOLDEN_APPLE;
        Item item2 = sOffhandItem = ((Mode)((Object)this.mode.getValue())).equals((Object)Mode.Crystal) ? Items.GOLDEN_APPLE : Items.END_CRYSTAL;
        if (((SMode)((Object)this.switchMode.getValue())).equals((Object)SMode.Sword)) {
            shouldSwitch = SmartOffHand.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && Mouse.isButtonDown((int)1) && (Boolean)this.autoSwitch.getValue() != false;
        } else {
            boolean bl = shouldSwitch = Mouse.isButtonDown((int)1) && (Boolean)this.autoSwitch.getValue() != false && !(handItem instanceof ItemFood) && !(handItem instanceof ItemExpBottle) && !(handItem instanceof ItemBlock);
        }
        if (this.shouldTotem() && this.getItemSlot(Items.TOTEM_OF_UNDYING) != -1) {
            this.switch_Totem();
        } else if (shouldSwitch && this.getItemSlot(sOffhandItem) != -1) {
            if (!SmartOffHand.mc.player.getHeldItemOffhand().getItem().equals(sOffhandItem)) {
                int slot = this.getItemSlot(sOffhandItem) < 9 ? this.getItemSlot(sOffhandItem) + 36 : this.getItemSlot(sOffhandItem);
                this.switchTo(slot);
            }
        } else if (this.getItemSlot(offhandItem) != -1) {
            int slot;
            int n = slot = this.getItemSlot(offhandItem) < 9 ? this.getItemSlot(offhandItem) + 36 : this.getItemSlot(offhandItem);
            if (!SmartOffHand.mc.player.getHeldItemOffhand().getItem().equals(offhandItem)) {
                this.switchTo(slot);
            }
        } else {
            this.switch_Totem();
        }
    }

    public boolean shouldTotem() {
        if (((Boolean)this.totem.getValue()).booleanValue()) {
            return this.checkHealth() || SmartOffHand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA && (Boolean)this.elytra.getValue() != false || SmartOffHand.mc.player.fallDistance >= 5.0f || HoleUtil.isPlayerInHole() && (Boolean)this.holeCheck.getValue() != false && (double)(SmartOffHand.mc.player.getHealth() + SmartOffHand.mc.player.getAbsorptionAmount()) <= (Double)this.holeSwitch.getValue() || (Boolean)this.crystalCalculate.getValue() != false && this.calcHealth();
        }
        return false;
    }

    public boolean calcHealth() {
        double maxDmg = 0.5;
        for (final Entity entity : new ArrayList<Entity>(SmartOffHand.mc.world.loadedEntityList)) {
            if (!(entity instanceof EntityEnderCrystal)) {
                continue;
            }
            if (SmartOffHand.mc.player.getDistance(entity) > 12.0f) {
                continue;
            }
            final double d = CrystalUtil.calculateDamage(entity.posX, entity.posY, entity.posZ, (Entity)SmartOffHand.mc.player);
            if (d <= maxDmg) {
                continue;
            }
            maxDmg = d;
        }
        return maxDmg - 0.5 > SmartOffHand.mc.player.getHealth() + SmartOffHand.mc.player.getAbsorptionAmount() || maxDmg > this.maxSelfDmg.getValue();
    }

    public boolean checkHealth() {
        boolean lowHealth = (double)(SmartOffHand.mc.player.getHealth() + SmartOffHand.mc.player.getAbsorptionAmount()) <= (Double)this.sbHealth.getValue();
        boolean notInHoleAndLowHealth = lowHealth && !HoleUtil.isPlayerInHole();
        return (Boolean)this.holeCheck.getValue() != false ? notInHoleAndLowHealth : lowHealth;
    }

    public void switch_Totem() {
        if (this.totems != 0 && !SmartOffHand.mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING)) {
            int slot = this.getItemSlot(Items.TOTEM_OF_UNDYING) < 9 ? this.getItemSlot(Items.TOTEM_OF_UNDYING) + 36 : this.getItemSlot(Items.TOTEM_OF_UNDYING);
            this.switchTo(slot);
        }
    }

    public void switchTo(int slot) {
        try {
            if (timerUtils.passed(this.delay.getValue())) {
                SmartOffHand.mc.playerController.windowClick(SmartOffHand.mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, (EntityPlayer)SmartOffHand.mc.player);
                SmartOffHand.mc.playerController.windowClick(SmartOffHand.mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, (EntityPlayer)SmartOffHand.mc.player);
                SmartOffHand.mc.playerController.windowClick(SmartOffHand.mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, (EntityPlayer)SmartOffHand.mc.player);
                timerUtils.reset();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public int getItemSlot(Item input) {
        int itemSlot = -1;
        for (int i = 45; i > 0; --i) {
            if (SmartOffHand.mc.player.inventory.getStackInSlot(i).getItem() != input) continue;
            itemSlot = i;
            break;
        }
        return itemSlot;
    }

    @Override
    public String getHudInfo() {
        if (SmartOffHand.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            return "Totem";
        }
        if (SmartOffHand.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            return "Crystal";
        }
        if (SmartOffHand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) {
            return "Gapple";
        }
        if (SmartOffHand.mc.player.getHeldItemOffhand().getItem() == Items.BED) {
            return "Bed";
        }
        return "None";
    }

    public static enum SMode {
        RClick,
        Sword;

    }

    public static enum Mode {
        Crystal,
        Gap;

    }
}

