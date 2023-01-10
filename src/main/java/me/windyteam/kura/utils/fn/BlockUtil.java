package me.windyteam.kura.utils.fn;

import com.google.common.util.concurrent.AtomicDouble;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import me.windyteam.kura.utils.inventory.InventoryUtil;
import me.windyteam.kura.utils.math.RotationUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.utils.mc.EntityUtil;
import me.windyteam.kura.utils.inventory.InventoryUtil;
import me.windyteam.kura.utils.math.RotationUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.utils.mc.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class BlockUtil {
  private static final Minecraft mc = Minecraft.getMinecraft();
  public static final List<Block> blackList = Arrays.asList(new Block[] { 
        Blocks.ENDER_CHEST, (Block)Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, (Block)Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, 
        Blocks.ENCHANTING_TABLE });
  
  public static final List<Block> shulkerList = Arrays.asList(new Block[] { 
        Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, 
        Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX });
  
  public static final List<Block> unSafeBlocks = Arrays.asList(new Block[] { Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.ENDER_CHEST, Blocks.ANVIL });
  
  public static List<Block> unSolidBlocks = Arrays.asList(new Block[] { 
        (Block)Blocks.FLOWING_LAVA, Blocks.FLOWER_POT, Blocks.SNOW, Blocks.CARPET, Blocks.END_ROD, (Block)Blocks.SKULL, Blocks.FLOWER_POT, Blocks.TRIPWIRE, (Block)Blocks.TRIPWIRE_HOOK, Blocks.WOODEN_BUTTON, 
        Blocks.LEVER, Blocks.STONE_BUTTON, Blocks.LADDER, (Block)Blocks.UNPOWERED_COMPARATOR, (Block)Blocks.POWERED_COMPARATOR, (Block)Blocks.UNPOWERED_REPEATER, (Block)Blocks.POWERED_REPEATER, Blocks.UNLIT_REDSTONE_TORCH, Blocks.REDSTONE_TORCH, (Block)Blocks.REDSTONE_WIRE, 
        Blocks.AIR, (Block)Blocks.PORTAL, Blocks.END_PORTAL, (Block)Blocks.WATER, (Block)Blocks.FLOWING_WATER, (Block)Blocks.LAVA, (Block)Blocks.FLOWING_LAVA, Blocks.SAPLING, (Block)Blocks.RED_FLOWER, (Block)Blocks.YELLOW_FLOWER, 
        (Block)Blocks.BROWN_MUSHROOM, (Block)Blocks.RED_MUSHROOM, Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS, (Block)Blocks.REEDS, Blocks.PUMPKIN_STEM, Blocks.MELON_STEM, Blocks.WATERLILY, 
        Blocks.NETHER_WART, Blocks.COCOA, Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT, (Block)Blocks.TALLGRASS, (Block)Blocks.DEADBUSH, Blocks.VINE, (Block)Blocks.FIRE, Blocks.RAIL, Blocks.ACTIVATOR_RAIL, 
        Blocks.DETECTOR_RAIL, Blocks.GOLDEN_RAIL, Blocks.TORCH });
  
  public static List<BlockPos> getBlockSphere(float breakRange, Class clazz) {
    NonNullList positions = NonNullList.create();
    positions.addAll((Collection)getSphere(EntityUtil.getPlayerPos((EntityPlayer)mc.player), breakRange, (int)breakRange, false, true, 0).stream().filter(pos -> clazz.isInstance(mc.world.getBlockState(pos).getBlock())).collect(Collectors.toList()));
    return (List<BlockPos>)positions;
  }
  
  public static List<EnumFacing> getPossibleSides(BlockPos pos) {
    ArrayList<EnumFacing> facings = new ArrayList<>();
    for (EnumFacing side : EnumFacing.values()) {
      BlockPos neighbour = pos.offset(side);
      IBlockState blockState;
      if (mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false) && !(blockState = mc.world.getBlockState(neighbour)).getMaterial().isReplaceable())
        facings.add(side); 
    } 
    return facings;
  }
  
  public static EnumFacing getFacing(BlockPos pos) {
    EnumFacing[] arrayOfEnumFacing;
    int i;
    byte b;
    for (arrayOfEnumFacing = EnumFacing.values(), i = arrayOfEnumFacing.length, b = 0; b < i; ) {
      EnumFacing facing = arrayOfEnumFacing[b];
      RayTraceResult rayTraceResult = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX() + 0.5D + facing.getDirectionVec().getX() * 1.0D / 2.0D, pos.getY() + 0.5D + facing.getDirectionVec().getY() * 1.0D / 2.0D, pos.getZ() + 0.5D + facing.getDirectionVec().getZ() * 1.0D / 2.0D), false, true, false);
      if (rayTraceResult != null && (rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK || !rayTraceResult.getBlockPos().equals(pos))) {
        b++;
        continue;
      } 
      return facing;
    } 
    if (pos.getY() > mc.player.posY + mc.player.getEyeHeight())
      return EnumFacing.DOWN; 
    return EnumFacing.UP;
  }
  
  public static EnumFacing getFirstFacing(BlockPos pos) {
    Iterator<EnumFacing> iterator = getPossibleSides(pos).iterator();
    if (iterator.hasNext()) {
      EnumFacing facing = iterator.next();
      return facing;
    } 
    return null;
  }
  
  public static EnumFacing getRayTraceFacing(BlockPos pos) {
    RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX() + 0.5D, pos.getX() - 0.5D, pos.getX() + 0.5D));
    if (result == null || result.sideHit == null)
      return EnumFacing.UP; 
    return result.sideHit;
  }
  
  public static int isPositionPlaceable(BlockPos pos, boolean rayTrace) {
    return isPositionPlaceable(pos, rayTrace, true);
  }
  
  public static int isPositionPlaceable(BlockPos pos, boolean rayTrace, boolean entityCheck) {
    Block block = mc.world.getBlockState(pos).getBlock();
    if (!(block instanceof net.minecraft.block.BlockAir) && !(block instanceof net.minecraft.block.BlockLiquid) && !(block instanceof net.minecraft.block.BlockTallGrass) && !(block instanceof net.minecraft.block.BlockFire) && !(block instanceof net.minecraft.block.BlockDeadBush) && !(block instanceof net.minecraft.block.BlockSnow))
      return 0; 
    if (!rayTracePlaceCheck(pos, rayTrace, 0.0F))
      return -1; 
    if (entityCheck)
      for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
        if (entity instanceof net.minecraft.entity.item.EntityItem || entity instanceof net.minecraft.entity.item.EntityXPOrb)
          continue; 
        return 1;
      }  
    for (EnumFacing side : getPossibleSides(pos)) {
      if (!canBeClicked(pos.offset(side)))
        continue; 
      return 3;
    } 
    return 2;
  }
  
  public static void placeBlockScaffold(BlockPos pos) {
    Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    for (EnumFacing side : EnumFacing.values()) {
      BlockPos neighbor = pos.offset(side);
      EnumFacing side2 = side.getOpposite();
      Vec3d hitVec;
      if (canBeClicked(neighbor) && eyesPos.squareDistanceTo(hitVec = (new Vec3d((Vec3i)neighbor)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(side2.getDirectionVec())).scale(0.5D))) <= 18.0625D) {
        faceVectorPacketInstant(hitVec);
        processRightClickBlock(neighbor, side2, hitVec);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
        return;
      } 
    } 
  }
  
  static void processRightClickBlock(BlockPos pos, EnumFacing side, Vec3d hitVec) {
    getPlayerController().processRightClickBlock(mc.player, mc.world, pos, side, hitVec, EnumHand.MAIN_HAND);
  }
  
  private static PlayerControllerMP getPlayerController() {
    return (Minecraft.getMinecraft()).playerController;
  }
  
  public static void faceVectorPacketInstant(Vec3d vec) {
    float[] rotations = RotationUtil.getLegitRotations(vec);
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(rotations[0], rotations[1], mc.player.onGround));
  }
  
  public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
    if (packet) {
      float f = (float)(vec.x - pos.getX());
      float f1 = (float)(vec.y - pos.getY());
      float f2 = (float)(vec.z - pos.getZ());
      mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
    } else {
      mc.playerController.processRightClickBlock(mc.player, mc.world, pos, direction, vec, hand);
    } 
    mc.player.swingArm(EnumHand.MAIN_HAND);
    mc.rightClickDelayTimer = 4;
  }
  
  public static boolean placeBlockNotRetarded(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean altRotate) {
    EnumFacing side = getFirstFacing(pos);
    if (side == null)
      return false; 
    BlockPos neighbour = pos.offset(side);
    EnumFacing opposite = side.getOpposite();
    Vec3d hitVec = (new Vec3d((Vec3i)neighbour)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
    Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
    if (!mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
      mc.player.setSneaking(true);
    } 
    if (rotate)
      RotationUtil.faceVector(altRotate ? new Vec3d((Vec3i)pos) : hitVec, true); 
    rightClickBlock(neighbour, hitVec, hand, opposite, packet);
    mc.player.swingArm(EnumHand.MAIN_HAND);
    return true;
  }
  
  public static boolean canBlockBeSeen(double x, double y, double z) {
    return (mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(x, y + 1.7D, z), false, true, false) == null);
  }
  
  public static void rightClickBlockLegit(BlockPos pos, float range, boolean rotate, EnumHand hand, AtomicDouble Yaw, AtomicDouble Pitch, AtomicBoolean rotating) {
    Vec3d eyesPos = RotationUtil.getEyesPos();
    Vec3d posVec = (new Vec3d((Vec3i)pos)).add(0.5D, 0.5D, 0.5D);
    double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
    EnumFacing[] arrayOfEnumFacing;
    int i;
    byte b;
    for (arrayOfEnumFacing = EnumFacing.values(), i = arrayOfEnumFacing.length, b = 0; b < i; ) {
      EnumFacing side = arrayOfEnumFacing[b];
      Vec3d hitVec = posVec.add((new Vec3d(side.getDirectionVec())).scale(0.5D));
      double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);
      if (distanceSqHitVec > MathUtil.square(range) || distanceSqHitVec >= distanceSqPosVec || mc.world.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null) {
        b++;
        continue;
      } 
      if (rotate) {
        float[] rotations = RotationUtil.getLegitRotations(hitVec);
        Yaw.set(rotations[0]);
        Pitch.set(rotations[1]);
        rotating.set(true);
      } 
      mc.playerController.processRightClickBlock(mc.player, mc.world, pos, side, hitVec, hand);
      mc.player.swingArm(hand);
      mc.rightClickDelayTimer = 4;
    } 
  }
  
  public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
    boolean sneaking = false;
    EnumFacing side = getFirstFacing(pos);
    if (side == null)
      return isSneaking; 
    BlockPos neighbour = pos.offset(side);
    EnumFacing opposite = side.getOpposite();
    Vec3d hitVec = (new Vec3d((Vec3i)neighbour)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
    Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
    if (!mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
      mc.player.setSneaking(true);
      sneaking = true;
    } 
    if (rotate)
      RotationUtil.faceVector(hitVec, true); 
    rightClickBlock(neighbour, hitVec, hand, opposite, packet);
    mc.player.swingArm(EnumHand.MAIN_HAND);
    mc.rightClickDelayTimer = 4;
    return (sneaking || isSneaking);
  }
  
  public static boolean placeBlock2(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
    boolean sneaking = false;
    EnumFacing side = getFirstFacing(pos);
    if (side == null)
      return isSneaking; 
    BlockPos neighbour = pos.offset(side);
    EnumFacing opposite = side.getOpposite();
    Vec3d hitVec = (new Vec3d((Vec3i)neighbour)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
    Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
    if (!mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
      mc.player.setSneaking(false);
      sneaking = true;
    } 
    if (rotate)
      RotationUtil.faceVector(hitVec, true); 
    rightClickBlock(neighbour, hitVec, hand, opposite, packet);
    mc.player.swingArm(EnumHand.MAIN_HAND);
    mc.rightClickDelayTimer = 4;
    return (sneaking || isSneaking);
  }

  
  public static Vec3d[] getHelpingBlocks(Vec3d vec3d) {
    return new Vec3d[] { new Vec3d(vec3d.x, vec3d.y - 1.0D, vec3d.z), new Vec3d((vec3d.x != 0.0D) ? (vec3d.x * 2.0D) : vec3d.x, vec3d.y, (vec3d.x != 0.0D) ? vec3d.z : (vec3d.z * 2.0D)), new Vec3d((vec3d.x == 0.0D) ? (vec3d.x + 1.0D) : vec3d.x, vec3d.y, (vec3d.x == 0.0D) ? vec3d.z : (vec3d.z + 1.0D)), new Vec3d((vec3d.x == 0.0D) ? (vec3d.x - 1.0D) : vec3d.x, vec3d.y, (vec3d.x == 0.0D) ? vec3d.z : (vec3d.z - 1.0D)), new Vec3d(vec3d.x, vec3d.y + 1.0D, vec3d.z) };
  }
  
  public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
    ArrayList<BlockPos> circleblocks = new ArrayList<>();
    int cx = pos.getX();
    int cy = pos.getY();
    int cz = pos.getZ();
    int x = cx - (int)r;
    while (x <= cx + r) {
      int z = cz - (int)r;
      while (z <= cz + r) {
        int y = sphere ? (cy - (int)r) : cy;
        while (true) {
          float f = y;
          float f2 = sphere ? (cy + r) : (cy + h);
          if (f >= f2)
            break; 
          double dist = ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0));
          if (dist < (r * r) && (!hollow || dist >= ((r - 1.0F) * (r - 1.0F)))) {
            BlockPos l = new BlockPos(x, y + plus_y, z);
            circleblocks.add(l);
          } 
          y++;
        } 
        z++;
      } 
      x++;
    } 
    return circleblocks;
  }
  
  public static List<BlockPos> possiblePlacePositions(float placeRange, boolean specialEntityCheck, boolean oneDot15) {
    NonNullList positions = NonNullList.create();
    positions.addAll((Collection)getSphere(EntityUtil.getPlayerPos((EntityPlayer)mc.player), placeRange, (int)placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystal(pos, specialEntityCheck, oneDot15)).collect(Collectors.toList()));
    return (List<BlockPos>)positions;
  }
  
  public static boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck, boolean oneDot15) {
    BlockPos boost = blockPos.add(0, 1, 0);
    BlockPos boost2 = blockPos.add(0, 2, 0);
    try {
      if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN)
        return false; 
      if ((!oneDot15 && mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) || mc.world.getBlockState(boost).getBlock() != Blocks.AIR)
        return false; 
      for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
        if (entity.isDead || (specialEntityCheck && entity instanceof net.minecraft.entity.item.EntityEnderCrystal))
          continue; 
        return false;
      } 
      if (!oneDot15)
        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
          if (entity.isDead || (specialEntityCheck && entity instanceof net.minecraft.entity.item.EntityEnderCrystal))
            continue; 
          return false;
        }  
    } catch (Exception ignored) {
      return false;
    } 
    return true;
  }
  
  public static boolean canBeClicked(BlockPos pos) {
    return getBlock(pos).canCollideCheck(getState(pos), false);
  }
  
  private static Block getBlock(BlockPos pos) {
    return getState(pos).getBlock();
  }
  
  private static IBlockState getState(BlockPos pos) {
    return mc.world.getBlockState(pos);
  }
  
  public static boolean isBlockAboveEntitySolid(Entity entity) {
    if (entity != null) {
      BlockPos pos = new BlockPos(entity.posX, entity.posY + 2.0D, entity.posZ);
      return isBlockSolid(pos);
    } 
    return false;
  }
  
  public static void debugPos(String message, BlockPos pos) {
    ChatUtil.sendMessage(message + pos.getX() + "x, " + pos.getY() + "y, " + pos.getZ() + "z");
  }
  
  public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand, boolean swing, boolean exactHand, boolean silent) {
    RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX() + 0.5D, pos.getY() - 0.5D, pos.getZ() + 0.5D));
    EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
    int old = mc.player.inventory.currentItem;
    int crystal = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
    if (hand == EnumHand.MAIN_HAND && silent && crystal != -1 && crystal != mc.player.inventory.currentItem)
      mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(crystal)); 
    mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0F, 0.0F, 0.0F));
    if (hand == EnumHand.MAIN_HAND && silent && crystal != -1 && crystal != mc.player.inventory.currentItem)
      mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(old)); 
    if (swing)
      mc.player.connection.sendPacket((Packet)new CPacketAnimation(exactHand ? hand : EnumHand.MAIN_HAND)); 
  }
  
  public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand) {
    mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, hand, pos.getX(), pos.getY(), pos.getZ()));
  }
  
  public static boolean canPlaceCrystal(BlockPos blockPos) {
    BlockPos boost = blockPos.add(0, 1, 0);
    BlockPos boost2 = blockPos.add(0, 2, 0);
    try {
      if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN)
        return false; 
      if (mc.world.getBlockState(boost).getBlock() != Blocks.AIR || mc.world.getBlockState(boost2).getBlock() != Blocks.AIR)
        return false; 
      for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
        if (entity instanceof net.minecraft.entity.item.EntityEnderCrystal)
          continue; 
        return false;
      } 
      for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
        if (entity instanceof net.minecraft.entity.item.EntityEnderCrystal)
          continue; 
        return false;
      } 
    } catch (Exception ignored) {
      return false;
    } 
    return true;
  }
  
  public static BlockPos[] toBlockPos(Vec3d[] vec3ds) {
    BlockPos[] list = new BlockPos[vec3ds.length];
    for (int i = 0; i < vec3ds.length; i++)
      list[i] = new BlockPos(vec3ds[i]); 
    return list;
  }
  
  public static Vec3d posToVec3d(BlockPos pos) {
    return new Vec3d((Vec3i)pos);
  }
  
  public static BlockPos vec3dToPos(Vec3d vec3d) {
    return new BlockPos(vec3d);
  }
  
  public static Boolean isPosInFov(BlockPos pos) {
    int dirnumber = RotationUtil.getDirection4D();
    if (dirnumber == 0 && pos.getZ() - (mc.player.getPositionVector()).z < 0.0D)
      return Boolean.valueOf(false); 
    if (dirnumber == 1 && pos.getX() - (mc.player.getPositionVector()).x > 0.0D)
      return Boolean.valueOf(false); 
    if (dirnumber == 2 && pos.getZ() - (mc.player.getPositionVector()).z > 0.0D)
      return Boolean.valueOf(false); 
    return Boolean.valueOf((dirnumber != 3 || pos.getX() - (mc.player.getPositionVector()).x >= 0.0D));
  }
  
  public static boolean isBlockBelowEntitySolid(Entity entity) {
    if (entity != null) {
      BlockPos pos = new BlockPos(entity.posX, entity.posY - 1.0D, entity.posZ);
      return isBlockSolid(pos);
    } 
    return false;
  }
  
  public static boolean isBlockSolid(BlockPos pos) {
    return !isBlockUnSolid(pos);
  }
  
  public static boolean isBlockUnSolid(BlockPos pos) {
    return isBlockUnSolid(mc.world.getBlockState(pos).getBlock());
  }
  
  public static boolean isBlockUnSolid(Block block) {
    return unSolidBlocks.contains(block);
  }
  
  public static boolean isBlockUnSafe(Block block) {
    return unSafeBlocks.contains(block);
  }
  
  public static Vec3d[] convertVec3ds(Vec3d vec3d, Vec3d[] input) {
    Vec3d[] output = new Vec3d[input.length];
    for (int i = 0; i < input.length; i++)
      output[i] = vec3d.add(input[i]); 
    return output;
  }
  
  public static Vec3d[] convertVec3ds(EntityPlayer entity, Vec3d[] input) {
    return convertVec3ds(entity.getPositionVector(), input);
  }
  
  public static boolean canBreak(BlockPos pos) {
    IBlockState blockState = mc.world.getBlockState(pos);
    Block block = blockState.getBlock();
    return (block.getBlockHardness(blockState, (World)mc.world, pos) != -1.0F);
  }
  
  public static boolean isValidBlock(BlockPos pos) {
    Block block = mc.world.getBlockState(pos).getBlock();
    return (!(block instanceof net.minecraft.block.BlockLiquid) && block.getMaterial(null) != Material.AIR);
  }
  
  public static boolean isScaffoldPos(BlockPos pos) {
    return (mc.world.isAirBlock(pos) || mc.world.getBlockState(pos).getBlock() == Blocks.SNOW_LAYER || mc.world.getBlockState(pos).getBlock() == Blocks.TALLGRASS || mc.world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockLiquid);
  }
  
  public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
    return (!shouldCheck || mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX(), (pos.getY() + height), pos.getZ()), false, true, false) == null);
  }
  
  public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck) {
    return rayTracePlaceCheck(pos, shouldCheck, 1.0F);
  }
  
  public static boolean rayTracePlaceCheck(BlockPos pos) {
    return rayTracePlaceCheck(pos, true);
  }
  
  public static List<BlockPos> getDisc(BlockPos pos, float r) {
    ArrayList<BlockPos> circleblocks = new ArrayList<>();
    int cx = pos.getX();
    int cy = pos.getY();
    int cz = pos.getZ();
    int x = cx - (int)r;
    while (x <= cx + r) {
      int z = cz - (int)r;
      while (z <= cz + r) {
        double dist = ((cx - x) * (cx - x) + (cz - z) * (cz - z));
        if (dist < (r * r)) {
          BlockPos position = new BlockPos(x, cy, z);
          circleblocks.add(position);
        } 
        z++;
      } 
      x++;
    } 
    return circleblocks;
  }
  
  public static BlockResistance getBlockResistance(BlockPos block) {
    if (mc.world.isAirBlock(block))
      return BlockResistance.Blank; 
    if (mc.world.getBlockState(block).getBlock().getBlockHardness(mc.world.getBlockState(block), (World)mc.world, block) != -1.0F && !mc.world.getBlockState(block).getBlock().equals(Blocks.OBSIDIAN) && !mc.world.getBlockState(block).getBlock().equals(Blocks.ANVIL) && !mc.world.getBlockState(block).getBlock().equals(Blocks.ENCHANTING_TABLE) && !mc.world.getBlockState(block).getBlock().equals(Blocks.ENDER_CHEST))
      return BlockResistance.Breakable; 
    if (mc.world.getBlockState(block).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(block).getBlock().equals(Blocks.ANVIL) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENCHANTING_TABLE) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENDER_CHEST))
      return BlockResistance.Resistant; 
    if (mc.world.getBlockState(block).getBlock().equals(Blocks.BEDROCK))
      return BlockResistance.Unbreakable; 
    return null;
  }
  
  public enum BlockResistance {
    Blank, Breakable, Resistant, Unbreakable;
  }
  
  public static List<BlockPos> getNearbyBlocks(EntityPlayer player, double blockRange, boolean motion) {
    List<BlockPos> nearbyBlocks = new ArrayList<>();
    int range = (int)MathUtil.roundDouble(blockRange, 0);
    if (motion)
      player.getPosition().add(new Vec3i(player.motionX, player.motionY, player.motionZ)); 
    for (int x = -range; x <= range; x++) {
      for (int y = -range; y <= range - range / 2; y++) {
        for (int z = -range; z <= range; z++)
          nearbyBlocks.add(player.getPosition().add(x, y, z)); 
      } 
    } 
    return nearbyBlocks;
  }
}
