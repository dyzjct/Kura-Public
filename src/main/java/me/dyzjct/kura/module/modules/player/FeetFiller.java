package me.dyzjct.kura.module.modules.player;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.NTMiku.BlockUtil;
import me.dyzjct.kura.utils.entity.EntityUtil;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

@Module.Info(name = "FeetFiller",category = Category.PLAYER)
public class FeetFiller
        extends Module {
    public EntityPlayer target;
    private final Setting<Float> range = fsetting("Range", Float.valueOf(5.0f), Float.valueOf(1.0f), Float.valueOf(6.0f));
    private final Setting<Boolean> autoDisable = bsetting("AutoDisable", true);
    private final Setting<Boolean> chestplace = bsetting("Y Chest Place", true);
    private final Setting<Boolean> xzchestplace = bsetting("X|Z Chest Place", false);
    private final Setting<Boolean> negative = bsetting("-X|-Z Chest Place", false);
    

    @Override
    public void onUpdate() {
        if (FeetFiller.fullNullCheck()) {
            return;
        }
        this.target = this.getTarget(this.range.getValue().floatValue());
        if (this.target == null) {
            return;
        }
        BlockPos people = new BlockPos(this.target.posX, this.target.posY, this.target.posZ);
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int chestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (obbySlot == -1) {
            return;
        }
        int old = FeetFiller.mc.player.inventory.currentItem;
        if (this.getBlock(people.add(0, -1, 0)).getBlock() == Blocks.AIR && this.getBlock(people.add(0, -2, 0)).getBlock() != Blocks.AIR) {
            if (this.chestplace.getValue().booleanValue() && InventoryUtil.findHotbarBlock(BlockEnderChest.class) != -1) {
                this.switchToSlot(chestSlot);
            } else {
                this.switchToSlot(obbySlot);
            }
            BlockUtil.placeBlock(people.add(0, -1, 0), EnumHand.MAIN_HAND, false, true, false);
            this.switchToSlot(old);
        }
        if (this.getBlock(people.add(0, -1, 0)).getBlock() != Blocks.AIR && this.getBlock(people.add(1, -1, 0)).getBlock() == Blocks.AIR && this.getBlock(people.add(1, 0, 0)).getBlock() == Blocks.AIR) {
            if (this.negative.getValue().booleanValue() && InventoryUtil.findHotbarBlock(BlockEnderChest.class) != -1) {
                this.switchToSlot(chestSlot);
            } else {
                this.switchToSlot(obbySlot);
            }
            BlockUtil.placeBlock(people.add(1, -1, 0), EnumHand.MAIN_HAND, false, true, false);
            this.switchToSlot(old);
        } else if (this.getBlock(people.add(0, -1, 0)).getBlock() != Blocks.AIR && this.getBlock(people.add(-1, -1, 0)).getBlock() == Blocks.AIR && this.getBlock(people.add(-1, 0, 0)).getBlock() == Blocks.AIR) {
            if (this.xzchestplace.getValue().booleanValue() && InventoryUtil.findHotbarBlock(BlockEnderChest.class) != -1) {
                this.switchToSlot(chestSlot);
            } else {
                this.switchToSlot(obbySlot);
            }
            BlockUtil.placeBlock(people.add(-1, -1, 0), EnumHand.MAIN_HAND, false, true, false);
            this.switchToSlot(old);
        } else if (this.getBlock(people.add(0, -1, 0)).getBlock() != Blocks.AIR && this.getBlock(people.add(0, -1, 1)).getBlock() == Blocks.AIR && this.getBlock(people.add(0, 0, 1)).getBlock() == Blocks.AIR) {
            if (this.negative.getValue().booleanValue() && InventoryUtil.findHotbarBlock(BlockEnderChest.class) != -1) {
                this.switchToSlot(chestSlot);
            } else {
                this.switchToSlot(obbySlot);
            }
            BlockUtil.placeBlock(people.add(0, -1, 1), EnumHand.MAIN_HAND, false, true, false);
            this.switchToSlot(old);
        } else if (this.getBlock(people.add(0, -1, 0)).getBlock() != Blocks.AIR && this.getBlock(people.add(0, -1, -1)).getBlock() == Blocks.AIR && this.getBlock(people.add(0, 0, -1)).getBlock() == Blocks.AIR) {
            if (this.xzchestplace.getValue().booleanValue() && InventoryUtil.findHotbarBlock(BlockEnderChest.class) != -1) {
                this.switchToSlot(chestSlot);
            } else {
                this.switchToSlot(obbySlot);
            }
            BlockUtil.placeBlock(people.add(0, -1, -1), EnumHand.MAIN_HAND, false, true, false);
            this.switchToSlot(old);
        }
        if (this.autoDisable.getValue().booleanValue()) {
            this.disable();
        }
    }

    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        for (EntityPlayer player : new ArrayList<>(mc.world.playerEntities)) {
            if (EntityUtil.isntValid(player, range)) continue;
            if (mc.player.getDistance(player) > range) continue;
            target = player;
            if (player != null) {
                break;
            }
            return player;
        }
        return target;
    }

    private void switchToSlot(int slot) {
        if (FeetFiller.fullNullCheck()) {
            return;
        }
        FeetFiller.mc.player.inventory.currentItem = slot;
        FeetFiller.mc.playerController.updateController();
    }

    private IBlockState getBlock(BlockPos block) {
        return FeetFiller.mc.world.getBlockState(block);
    }
}

