package me.dyzjct.kura.module.modules.combat;

import me.dyzjct.kura.manager.FriendManager;
import me.dyzjct.kura.manager.RotationManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.IntegerSetting;
import me.dyzjct.kura.setting.ModeSetting;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.block.BlockUtil;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Module.Info(name = "AutoTrap", category = Category.COMBAT)
public class AutoTrap extends Module {
    public int slot = -1;
    public int oldslot = -1;
    public Vec3d[] offsets_default = new Vec3d[]{
            new Vec3d(0.0, 0.0, -1.0),
            new Vec3d(1.0, 0.0, 0.0),
            new Vec3d(0.0, 0.0, 1.0),
            new Vec3d(-1.0, 0.0, 0.0),
            new Vec3d(0.0, 1.0, -1.0),
            new Vec3d(1.0, 1.0, 0.0),
            new Vec3d(0.0, 1.0, 1.0),
            new Vec3d(-1.0, 1.0, 0.0),
            new Vec3d(0.0, 2.0, -1.0),
            new Vec3d(1.0, 2.0, 0.0),
            new Vec3d(0.0, 2.0, 1.0),
            new Vec3d(-1.0, 2.0, 0.0),
            new Vec3d(0.0, 3.0, -1.0),
            new Vec3d(0.0, 3.0, 1.0),
            new Vec3d(1.0, 3.0, 0.0),
            new Vec3d(-1.0, 3.0, 0.0),
            new Vec3d(0.0, 3.0, 0.0)
    };
    public Vec3d[] offsets_face = new Vec3d[]{
            new Vec3d(0.0, 0.0, -1.0),
            new Vec3d(1.0, 0.0, 0.0),
            new Vec3d(0.0, 0.0, 1.0),
            new Vec3d(-1.0, 0.0, 0.0),
            new Vec3d(0.0, 1.0, -1.0),
            new Vec3d(1.0, 1.0, 0.0),
            new Vec3d(0.0, 1.0, 1.0),
            new Vec3d(-1.0, 1.0, 0.0),
            new Vec3d(0.0, 2.0, -1.0),
            new Vec3d(0.0, 3.0, -1.0),
            new Vec3d(0.0, 3.0, 1.0),
            new Vec3d(1.0, 3.0, 0.0),
            new Vec3d(-1.0, 3.0, 0.0),
            new Vec3d(0.0, 3.0, 0.0)
    };
    public Vec3d[] offsets_feet = new Vec3d[]{
            new Vec3d(0.0, 0.0, -1.0),
            new Vec3d(1.0, 0.0, 0.0),
            new Vec3d(0.0, 0.0, 1.0),
            new Vec3d(-1.0, 0.0, 0.0),
            new Vec3d(0.0, 1.0, -1.0),
            new Vec3d(0.0, 2.0, -1.0),
            new Vec3d(1.0, 2.0, 0.0),
            new Vec3d(0.0, 2.0, 1.0),
            new Vec3d(-1.0, 2.0, 0.0),
            new Vec3d(0.0, 3.0, -1.0),
            new Vec3d(0.0, 3.0, 1.0),
            new Vec3d(1.0, 3.0, 0.0),
            new Vec3d(-1.0, 3.0, 0.0),
            new Vec3d(0.0, 3.0, 0.0)
    };
    public Vec3d[] offsets_extra = new Vec3d[]{
            new Vec3d(0.0, 0.0, -1.0),
            new Vec3d(1.0, 0.0, 0.0),
            new Vec3d(0.0, 0.0, 1.0),
            new Vec3d(-1.0, 0.0, 0.0),
            new Vec3d(0.0, 1.0, -1.0),
            new Vec3d(1.0, 1.0, 0.0),
            new Vec3d(0.0, 1.0, 1.0),
            new Vec3d(-1.0, 1.0, 0.0),
            new Vec3d(0.0, 2.0, -1.0),
            new Vec3d(1.0, 2.0, 0.0),
            new Vec3d(0.0, 2.0, 1.0),
            new Vec3d(-1.0, 2.0, 0.0),
            new Vec3d(0.0, 3.0, -1.0),
            new Vec3d(0.0, 3.0, 0.0),
            new Vec3d(0.0, 4.0, 0.0)
    };
    public ModeSetting<?> place_mode = msetting("Mode", Mode.Normal);
    public Setting<Boolean> rotate = bsetting("Rotate", false);
    public Setting<Boolean> packet = bsetting("Packet", false);
    public IntegerSetting range = isetting("EnemyRange", 4, 0, 6);
    public IntegerSetting Blockrange = isetting("PlaceRange", 4, 0, 6);
    public Setting<Integer> blocks_per_tick = isetting("BlockPerTick", 6, 0, 8);
    public String last_tick_target_name = "";
    public int offset_step = 0;
    public int timeout_ticker = 0;
    public boolean first_run = true;

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        RotationManager.resetRotation();
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        oldslot = mc.player.inventory.currentItem;
        slot = mc.player.inventory.currentItem;
        timeout_ticker = 0;
        first_run = true;
        if (InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN) == -1) {
            disable();
        }
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        oldslot = mc.player.inventory.currentItem;
        slot = mc.player.inventory.currentItem;
        EntityPlayer closest_target = find_closest_target();
        if (closest_target == null) {
            disable();
            return;
        }
        if (first_run) {
            first_run = false;
            last_tick_target_name = closest_target.getName();
        } else if (!last_tick_target_name.equals(closest_target.getName())) {
            last_tick_target_name = closest_target.getName();
            offset_step = 0;
        }
        List<Vec3d> place_targets = new ArrayList<>();
        if (place_mode.getValue().equals(Mode.Normal)) {
            Collections.addAll(place_targets, offsets_default);
        } else if (place_mode.getValue().equals(Mode.Extra)) {
            Collections.addAll(place_targets, offsets_extra);
        } else if (place_mode.getValue().equals(Mode.Feet)) {
            Collections.addAll(place_targets, offsets_feet);
        } else {
            Collections.addAll(place_targets, offsets_face);
        }
        int blocks_placed = 0;
        while (blocks_placed < blocks_per_tick.getValue()) {
            if (offset_step >= place_targets.size()) {
                offset_step = 0;
                break;
            }
            BlockPos offset_pos = new BlockPos(place_targets.get(offset_step));
            BlockPos target_pos = new BlockPos(closest_target.getPositionVector()).down().add(offset_pos.getX(), offset_pos.getY(), offset_pos.getZ());
            boolean should_try_place = mc.world.getBlockState(target_pos).getMaterial().isReplaceable();
            for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(target_pos))) {
                if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                    should_try_place = false;
                    break;
                }
            }
            if (mc.player.getDistance(target_pos.x, target_pos.y, target_pos.z) > Blockrange.getValue()) return;
            if (should_try_place && target_pos != null) {
                InventoryUtil.switchToHotbarSlot(InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN), false);
                BlockUtil.placeBlock(target_pos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue());
                InventoryUtil.switchToHotbarSlot(oldslot, false);
                ++blocks_placed;
            }
            offset_step++;
        }
        timeout_ticker++;
    }

    public EntityPlayer find_closest_target() {
        if (mc.world.playerEntities.isEmpty()) {
            return null;
        }
        EntityPlayer closestTarget = null;
        for (EntityPlayer target : mc.world.playerEntities) {
            if (target == mc.player)
                continue;
            if (FriendManager.isFriend(target.getName()))
                continue;
            if (mc.player.getDistance(target) > range.getValue()) {
                continue;
            }
            if (target.getHealth() <= 0.0f)
                continue;
            if (closestTarget != null)
                if (mc.player.getDistance(target) > mc.player.getDistance(closestTarget))
                    continue;
            closestTarget = target;
        }
        return closestTarget;
    }

    public enum Mode {
        Extra,
        Normal,
        Feet
    }
}

