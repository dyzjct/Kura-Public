package me.windyteam.kura.utils.fn;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import me.windyteam.kura.module.modules.misc.InstantMine;
import me.windyteam.kura.utils.inventory.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class BlockHelper {
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
    return (mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && (!helpBlock || haveNeighborBlock(pos, Blocks.AIR).size() < 6) && (!bBoxCheck || isNoBBoxBlocked(pos)));
  }
  
  public static boolean isFacing(BlockPos pos, EnumFacing enumFacing) {
    ImmutableMap<IProperty<?>, Comparable<?>> properties = mc.world.getBlockState(pos).getProperties();
    for (UnmodifiableIterator<IProperty<?>> unmodifiableIterator = properties.keySet().iterator(); unmodifiableIterator.hasNext(); ) {
      IProperty<?> prop = unmodifiableIterator.next();
      if (prop.getValueClass() == EnumFacing.class && (prop.getName().equals("facing") || prop.getName().equals("rotation")) && properties.get(prop) == enumFacing)
        return true; 
    } 
    return false;
  }
  
  public static EnumFacing getFacing(BlockPos pos) {
    ImmutableMap<IProperty<?>, Comparable<?>> properties = mc.world.getBlockState(pos).getProperties();
    for (UnmodifiableIterator<IProperty<?>> unmodifiableIterator = properties.keySet().iterator(); unmodifiableIterator.hasNext(); ) {
      IProperty<?> prop = unmodifiableIterator.next();
      if (prop.getValueClass() == EnumFacing.class && (prop.getName().equals("facing") || prop.getName().equals("rotation")))
        return (EnumFacing)properties.get(prop); 
    } 
    return null;
  }
  
  public static boolean isNoBBoxBlocked(BlockPos pos) {
    AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos);
    List l = mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, axisAlignedBB);
    return (l.size() == 0);
  }
  
  public static void mine(BlockPos pos1, BlockPos pos2) {
    int oldslot = mc.player.inventory.currentItem;
    int dSlot = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
    if (dSlot != -1)
      InventoryUtil.switchToHotbarSlot(dSlot, false); 
    InventoryUtil.switchToHotbarSlot(oldslot, false);
    if (InstantMine.breakPos2 == null) {
      mc.playerController.onPlayerDamageBlock(pos1, BlockUtil.getRayTraceFacing(pos1));
      mc.playerController.onPlayerDamageBlock(pos2, BlockUtil.getRayTraceFacing(pos2));
    } 
  }
  
  public static void mine(BlockPos minePos) {
    int oldslot = mc.player.inventory.currentItem;
    int dSlot = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
    if (dSlot != -1)
      InventoryUtil.switchToHotbarSlot(dSlot, false); 
    InventoryUtil.switchToHotbarSlot(oldslot, false);
    if (InstantMine.breakPos2 == null)
      mc.playerController.onPlayerDamageBlock(minePos, BlockUtil.getRayTraceFacing(minePos)); 
  }
  
  public static BlockPos getFlooredPosition(Entity entity) {
    return new BlockPos(Math.floor(entity.posX), Math.round(entity.posY), Math.floor(entity.posZ));
  }
  
  public static boolean isNoBBoxBlocked(BlockPos pos, boolean ignoreSomeEnt) {
    AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos);
    List<Entity> l = mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, axisAlignedBB);
    if (ignoreSomeEnt) {
      for (Entity entity : l) {
        if (entity instanceof net.minecraft.entity.item.EntityEnderCrystal || entity instanceof net.minecraft.entity.item.EntityItem || entity instanceof net.minecraft.entity.projectile.EntityArrow || entity instanceof net.minecraft.entity.projectile.EntityTippedArrow || entity instanceof net.minecraft.entity.projectile.EntityArrow || 
          entity instanceof net.minecraft.entity.item.EntityXPOrb)
          continue; 
        return false;
      } 
      return true;
    } 
    return (l.size() == 0);
  }
  
  public static boolean isPlaceable(BlockPos pos, ArrayList<Block> ignoreBlock, boolean bBoxCheck, boolean helpBlockCheck, boolean rayTrace) {
    boolean placeable = false;
    for (Block iGB : ignoreBlock) {
      if (mc.world.getBlockState(pos).getBlock().equals(iGB) || mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
        placeable = true;
        break;
      } 
    } 
    if (bBoxCheck && !isNoBBoxBlocked(pos, true))
      placeable = false; 
    if (helpBlockCheck && haveNeighborBlock(pos, Blocks.AIR).size() >= 6)
      placeable = false; 
    if (rayTrace && !CombatUtil.rayTraceRangeCheck(pos, 0.0D, 0.0D))
      placeable = false; 
    return placeable;
  }
  
  private static Minecraft mc = Minecraft.getMinecraft();
}
