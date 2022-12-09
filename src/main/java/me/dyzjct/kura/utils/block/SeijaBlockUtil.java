package me.dyzjct.kura.utils.block;

import com.google.common.collect.ImmutableMap;
import me.dyzjct.kura.module.modules.misc.InstantMine;
import me.dyzjct.kura.utils.combat.CombatUtil;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SeijaBlockUtil {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static ArrayList<BlockPos> haveNeighborBlock(BlockPos pos, Block neighbor) {
        ArrayList<BlockPos> blockList = new ArrayList<>();
        if (mc.world.getBlockState(pos.add(1, 0, 0)).getBlock().equals(neighbor))
            blockList.add(pos.add(1, 0, 0));
        if (mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock().equals(neighbor))
            blockList.add(pos.add(-1, 0, 0));
        if (mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(neighbor))
            blockList.add(pos.add(0, 1, 0));
        if (mc.world.getBlockState(pos.add(0, -1, 0)).getBlock().equals(neighbor))
            blockList.add(pos.add(0, -1, 0));
        if (mc.world.getBlockState(pos.add(0, 0, 1)).getBlock().equals(neighbor))
            blockList.add(pos.add(0, 0, 1));
        if (mc.world.getBlockState(pos.add(0, 0, -1)).getBlock().equals(neighbor))
            blockList.add(pos.add(0, 0, -1));
        return blockList;
    }

    public static boolean isPlaceable(BlockPos pos, boolean helpBlock, boolean bBoxCheck) {
        if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
            //Command.sendMessage("false1");
            return false;
        }
        if (helpBlock) {
            if (haveNeighborBlock(pos, Blocks.AIR).size() >= 6) {
                //Command.sendMessage("false2");
                return false;
            }
        }
        if (bBoxCheck) {
            if (!isNoBBoxBlocked(pos)) {
                //Command.sendMessage("false3");
                return false;
            }
        }
        return true;
    }

    public static boolean isFacing(BlockPos pos, EnumFacing enumFacing) {
        ImmutableMap<IProperty<?>, Comparable<?>> properties = mc.world.getBlockState(pos).getProperties();
        for (IProperty<?> prop : properties.keySet()) {
            if (prop.getValueClass() == EnumFacing.class && (prop.getName().equals("facing") || prop.getName().equals("rotation"))) {
                if (properties.get(prop) == enumFacing) {
                    return true;

                }
            }
        }
        return false;
    }
    public static EnumFacing getFacing(BlockPos pos) {
        ImmutableMap<IProperty<?>, Comparable<?>> properties = mc.world.getBlockState(pos).getProperties();
        for (IProperty<?> prop : properties.keySet()) {
            if (prop.getValueClass() == EnumFacing.class && (prop.getName().equals("facing") || prop.getName().equals("rotation"))) {

                    return (EnumFacing) properties.get(prop);


            }
        }
        return null;
    }

    public static boolean isNoBBoxBlocked(BlockPos pos) {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos);
        List l = mc.world.getEntitiesWithinAABBExcludingEntity(null, axisAlignedBB);
        if (l.size() == 0) {
            return true;
        }
        return false;
    }

    public static void mine(BlockPos pos1, BlockPos pos2) {
        //if (timer.passedDs(switchDelay.getValue())&&switchDelay.getValue()>0.0){
        int oldslot = mc.player.inventory.currentItem;
        int dSlot = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
        if (dSlot != -1) {
            InventoryUtil.switchToHotbarSlot(dSlot, false);
        }
        InventoryUtil.switchToHotbarSlot(oldslot, false);
        //timer.reset();
        //}

        if (InstantMine.breakPos2 == null) {
            mc.playerController.onPlayerDamageBlock(pos1, BlockUtil.getRayTraceFacing(pos1));

            mc.playerController.onPlayerDamageBlock(pos2, BlockUtil.getRayTraceFacing(pos2));

        }
    }

    public static void mine(BlockPos minePos) {
        //if (timer.passedDs(switchDelay.getValue())&&switchDelay.getValue()>0.0){
        int oldslot = mc.player.inventory.currentItem;
        int dSlot = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
        if (dSlot != -1) {
            InventoryUtil.switchToHotbarSlot(dSlot, false);
        }
        InventoryUtil.switchToHotbarSlot(oldslot, false);
        //timer.reset();
        //}
        if (InstantMine.breakPos2 == null) {
            mc.playerController.onPlayerDamageBlock(minePos, BlockUtil.getRayTraceFacing(minePos));
        }
    }

    public static BlockPos getFlooredPosition(Entity entity) {
        return new BlockPos(Math.floor(entity.posX), Math.round(entity.posY), Math.floor(entity.posZ));
    }

    public static boolean isNoBBoxBlocked(BlockPos pos, boolean ignoreSomeEnt) {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos);
        List<Entity> l = mc.world.getEntitiesWithinAABBExcludingEntity(null, axisAlignedBB);
        if (ignoreSomeEnt) {
            for (Entity entity : l) {
                if (entity instanceof EntityEnderCrystal
                        || entity instanceof EntityItem
                        || entity instanceof EntityArrow
                        || entity instanceof EntityTippedArrow
                        || entity instanceof EntityArrow
                        || entity instanceof EntityXPOrb
                ) {
                    //Command.sendMessage("continue");
                    continue;
                }
                return false;
            }
            return true;
        } else {
            if (l.size() == 0)
                return true;
            return false;
        }
    }

    public static boolean isPlaceable(BlockPos pos, ArrayList<Block> ignoreBlock, boolean bBoxCheck, boolean helpBlockCheck, boolean rayTrace) {
        boolean placeable = false;
        for (Block iGB : ignoreBlock) {
            if (mc.world.getBlockState(pos).getBlock().equals(iGB) || mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                placeable = true;
                break;
            }
        }
        if (bBoxCheck) {
            if (!isNoBBoxBlocked(pos, true))
                placeable = false;
        }
        if (helpBlockCheck) {
            if (haveNeighborBlock(pos, Blocks.AIR).size() >= 6)
                placeable = false;
        }
        if (rayTrace) {
            if (!CombatUtil.rayTraceRangeCheck(pos, 0, 0.0d)) {
                placeable = false;
            }
        }
        return placeable;
    }
}

