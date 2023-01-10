package me.windyteam.kura.utils.block;

import net.minecraft.client.*;
import net.minecraft.block.*;
import net.minecraft.init.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.*;
import java.util.*;

public class BlockHelper
{
    private static Minecraft mc;
    
    public static ArrayList<BlockPos> haveNeighborBlock(final BlockPos pos, final Block neighbor) {
        final ArrayList<BlockPos> blockList = new ArrayList<BlockPos>();
        if (BlockHelper.mc.world.getBlockState(pos.add(1, 0, 0)).getBlock().equals(neighbor)) {
            blockList.add(pos.add(1, 0, 0));
        }
        if (BlockHelper.mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock().equals(neighbor)) {
            blockList.add(pos.add(-1, 0, 0));
        }
        if (BlockHelper.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(neighbor)) {
            blockList.add(pos.add(0, 1, 0));
        }
        if (BlockHelper.mc.world.getBlockState(pos.add(0, -1, 0)).getBlock().equals(neighbor)) {
            blockList.add(pos.add(0, -1, 0));
        }
        if (BlockHelper.mc.world.getBlockState(pos.add(0, 0, 1)).getBlock().equals(neighbor)) {
            blockList.add(pos.add(0, 0, 1));
        }
        if (BlockHelper.mc.world.getBlockState(pos.add(0, 0, -1)).getBlock().equals(neighbor)) {
            blockList.add(pos.add(0, 0, -1));
        }
        return blockList;
    }
    
    public static boolean isPlaceable(final BlockPos pos, final boolean helpBlock, final boolean bBoxCheck) {
        return BlockHelper.mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && (!helpBlock || haveNeighborBlock(pos, Blocks.AIR).size() < 6) && (!bBoxCheck || isNoBBoxBlocked(pos));
    }
    
    public static boolean isNoBBoxBlocked(final BlockPos pos) {
        final AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos);
        final List l = BlockHelper.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, axisAlignedBB);
        return l.size() == 0;
    }
    
    public static BlockPos getFlooredPosition(final Entity entity) {
        return new BlockPos(Math.floor(entity.posX), (double)Math.round(entity.posY), Math.floor(entity.posZ));
    }
    
    static {
        BlockHelper.mc = Minecraft.getMinecraft();
    }
}
