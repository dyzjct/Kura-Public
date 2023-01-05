package me.dyzjct.kura.utils.fn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import me.dyzjct.kura.utils.NTMiku.HoleUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class CombatUtil {
  public static final List<Block> blackList = Arrays.asList(new Block[] { 
        (Block)Blocks.TALLGRASS, Blocks.ENDER_CHEST, (Block)Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, (Block)Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, 
        Blocks.TRAPDOOR });
  
  public static final List<Block> shulkerList = Arrays.asList(new Block[] { 
        Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, 
        Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX });
  
  private static Minecraft mc = Minecraft.getMinecraft();
  
  public static final Vec3d[] cityOffsets = new Vec3d[] { new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(2.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 2.0D), new Vec3d(-2.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -2.0D) };
  
  private static final List<Integer> invalidSlots = Arrays.asList(new Integer[] { Integer.valueOf(0), Integer.valueOf(5), Integer.valueOf(6), Integer.valueOf(7), Integer.valueOf(8) });
  
  public static int findCrapple() {
    if (mc.player == null)
      return -1; 
    for (int x = 0; x < mc.player.inventoryContainer.getInventory().size(); x++) {
      if (!invalidSlots.contains(Integer.valueOf(x))) {
        ItemStack stack = (ItemStack)mc.player.inventoryContainer.getInventory().get(x);
        if (!stack.isEmpty())
          if (stack.getItem().equals(Items.GOLDEN_APPLE) && stack.getItemDamage() != 1)
            return x;  
      } 
    } 
    return -1;
  }
  
  public static int findItemSlotDamage1(Item i) {
    if (mc.player == null)
      return -1; 
    for (int x = 0; x < mc.player.inventoryContainer.getInventory().size(); x++) {
      if (!invalidSlots.contains(Integer.valueOf(x))) {
        ItemStack stack = (ItemStack)mc.player.inventoryContainer.getInventory().get(x);
        if (!stack.isEmpty())
          if (stack.getItem().equals(i) && stack.getItemDamage() == 1)
            return x;  
      } 
    } 
    return -1;
  }
  
  public static int findItemSlot(Item i) {
    if (mc.player == null)
      return -1; 
    for (int x = 0; x < mc.player.inventoryContainer.getInventory().size(); x++) {
      if (!invalidSlots.contains(Integer.valueOf(x))) {
        ItemStack stack = (ItemStack)mc.player.inventoryContainer.getInventory().get(x);
        if (!stack.isEmpty())
          if (stack.getItem().equals(i))
            return x;  
      } 
    } 
    return -1;
  }
  
  public static boolean isHoldingCrystal(boolean onlyMainHand) {
    if (onlyMainHand)
      return (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL); 
    return (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
  }
  
  public static boolean requiredDangerSwitch(double dangerRange) {
    int dangerousCrystals = (int)mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal).filter(entity -> (mc.player.getDistance(entity) <= dangerRange)).filter(entity -> (calculateDamage(entity.posX, entity.posY, entity.posZ, (Entity)mc.player) >= mc.player.getHealth() + mc.player.getAbsorptionAmount())).count();
    return (dangerousCrystals > 0);
  }
  
  public static boolean passesOffhandCheck(double requiredHealth, Item item, boolean isCrapple) {
    double totalPlayerHealth = (mc.player.getHealth() + mc.player.getAbsorptionAmount());
    if (!isCrapple) {
      if (findItemSlot(item) == -1)
        return false; 
    } else if (findCrapple() == -1) {
      return false;
    } 
    if (totalPlayerHealth < requiredHealth)
      return false; 
    return true;
  }
  
  public static void switchOffhandStrict(int targetSlot, int step) {
    switch (step) {
      case 0:
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, targetSlot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
        break;
      case 1:
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
        break;
      case 2:
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, targetSlot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
        mc.playerController.updateController();
        break;
    } 
  }
  
  public static void switchOffhandTotemNotStrict() {
    int targetSlot = findItemSlot(Items.TOTEM_OF_UNDYING);
    if (targetSlot != -1) {
      mc.playerController.windowClick(mc.player.inventoryContainer.windowId, targetSlot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      mc.playerController.windowClick(mc.player.inventoryContainer.windowId, targetSlot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      mc.playerController.updateController();
    } 
  }
  
  public static void switchOffhandNonStrict(int targetSlot) {
    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, targetSlot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, targetSlot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
    mc.playerController.updateController();
  }
  
  public static boolean canSeeBlock(BlockPos pos) {
    return (mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX(), (pos.getY() + 1.0F), pos.getZ()), false, true, false) == null);
  }
  
  public static boolean placeBlock(BlockPos blockPos, boolean offhand, boolean rotate, boolean packetRotate, boolean doSwitch, boolean silentSwitch, int toSwitch) {
    if (!checkCanPlace(blockPos))
      return false; 
    EnumFacing placeSide = getPlaceSide(blockPos);
    BlockPos adjacentBlock = blockPos.offset(placeSide);
    EnumFacing opposingSide = placeSide.getOpposite();
    if (!mc.world.getBlockState(adjacentBlock).getBlock().canCollideCheck(mc.world.getBlockState(adjacentBlock), false))
      return false; 
    if (doSwitch)
      if (silentSwitch) {
        mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(toSwitch));
      } else if (mc.player.inventory.currentItem != toSwitch) {
        mc.player.inventory.currentItem = toSwitch;
      }  
    boolean isSneak = false;
    if (blackList.contains(mc.world.getBlockState(adjacentBlock).getBlock()) || shulkerList.contains(mc.world.getBlockState(adjacentBlock).getBlock())) {
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
      isSneak = true;
    } 
    Vec3d hitVector = getHitVector(adjacentBlock, opposingSide);
    if (rotate) {
      float[] angle = getLegitRotations(hitVector);
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(angle[0], angle[1], mc.player.onGround));
    } 
    EnumHand actionHand = offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    mc.playerController.processRightClickBlock(mc.player, mc.world, adjacentBlock, opposingSide, hitVector, actionHand);
    mc.player.connection.sendPacket((Packet)new CPacketAnimation(actionHand));
    if (isSneak)
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING)); 
    return true;
  }
  
  private static Vec3d getHitVector(BlockPos pos, EnumFacing opposingSide) {
    return (new Vec3d((Vec3i)pos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposingSide.getDirectionVec())).scale(0.5D));
  }
  
  public static Vec3d getHitAddition(double x, double y, double z, BlockPos pos, EnumFacing opposingSide) {
    return (new Vec3d((Vec3i)pos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposingSide.getDirectionVec())).scale(0.5D));
  }
  
  public static void betterRotate(BlockPos blockPos, EnumFacing opposite, boolean packetRotate) {
    float offsetZ = 0.0F, offsetY = offsetZ, offsetX = offsetY;
    switch (getPlaceSide(blockPos)) {
      case UP:
        offsetX = offsetZ = 0.5F;
        offsetY = 0.0F;
        break;
      case DOWN:
        offsetX = offsetZ = 0.5F;
        offsetY = -0.5F;
        break;
      case NORTH:
        offsetX = 0.5F;
        offsetY = -0.5F;
        offsetZ = -0.5F;
        break;
      case EAST:
        offsetX = 0.5F;
        offsetY = -0.5F;
        offsetZ = 0.5F;
        break;
      case SOUTH:
        offsetX = 0.5F;
        offsetY = -0.5F;
        offsetZ = 0.5F;
        break;
      case WEST:
        offsetX = -0.5F;
        offsetY = -0.5F;
        offsetZ = 0.5F;
        break;
    } 
    float[] angle = getLegitRotations(getHitAddition(offsetX, offsetY, offsetZ, blockPos, opposite));
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(angle[0], angle[1], mc.player.onGround));
  }
  
  private static EnumFacing getPlaceSide(BlockPos blockPos) {
    EnumFacing placeableSide = null;
    for (EnumFacing side : EnumFacing.values()) {
      BlockPos adjacent = blockPos.offset(side);
      if (mc.world.getBlockState(adjacent).getBlock().canCollideCheck(mc.world.getBlockState(adjacent), false) && !mc.world.getBlockState(adjacent).getMaterial().isReplaceable())
        placeableSide = side; 
    } 
    return placeableSide;
  }
  
  public static boolean checkCanPlace(BlockPos pos) {
    if (!(mc.world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockAir) && !(mc.world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockLiquid))
      return false; 
    for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
      if (!(entity instanceof net.minecraft.entity.item.EntityItem) && !(entity instanceof net.minecraft.entity.item.EntityXPOrb) && !(entity instanceof net.minecraft.entity.projectile.EntityArrow))
        return false; 
    } 
    return (getPlaceSide(pos) != null);
  }
  
  public static boolean isInCity(EntityPlayer player, double range, double placeRange, boolean checkFace, boolean topBlock, boolean checkPlace, boolean checkRange) {
    BlockPos pos = new BlockPos(player.getPositionVector());
    for (EnumFacing face : EnumFacing.values()) {
      if (face != EnumFacing.UP && face != EnumFacing.DOWN) {
        BlockPos pos1 = pos.offset(face);
        BlockPos pos2 = pos1.offset(face);
        if ((mc.world.getBlockState(pos1).getBlock() == Blocks.AIR && ((mc.world
          .getBlockState(pos2).getBlock() == Blocks.AIR && isHard(mc.world.getBlockState(pos2.up()).getBlock())) || !checkFace) && !checkRange) || (mc.player
          .getDistanceSq(pos2) <= placeRange * placeRange && mc.player
          .getDistanceSq((Entity)player) <= range * range && 
          isHard(mc.world.getBlockState(pos.up(3)).getBlock())) || !topBlock)
          return true; 
      } 
    } 
    return false;
  }
  
  public static boolean isHard(Block block) {
    return (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK);
  }
  
  public static boolean canLegPlace(EntityPlayer player, double range) {
    int safety = 0;
    int blocksInRange = 0;
    for (Vec3d vec : HoleUtil.cityOffsets) {
      BlockPos pos = getFlooredPosition((Entity)player).add(vec.x, vec.y, vec.z);
      if (mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || mc.world
        .getBlockState(pos).getBlock() == Blocks.BEDROCK)
        safety++; 
      if (mc.player.getDistanceSq(pos) >= range * range)
        blocksInRange++; 
    } 
    return (safety == 4 && blocksInRange >= 1);
  }
  
  public static int getSafetyFactor(BlockPos pos) {
    return 0;
  }
  
  public static boolean canPlaceCrystal(BlockPos pos, double range, double wallsRange, boolean raytraceCheck) {
    BlockPos up = pos.up();
    BlockPos up1 = up.up();
    AxisAlignedBB bb = (new AxisAlignedBB(up)).expand(0.0D, 1.0D, 0.0D);
    return (((mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || mc.world
      .getBlockState(pos).getBlock() == Blocks.BEDROCK) && mc.world
      .getBlockState(up).getBlock() == Blocks.AIR && mc.world
      .getBlockState(up1).getBlock() == Blocks.AIR && mc.world
      .getEntitiesWithinAABB(Entity.class, bb).isEmpty() && mc.player
      .getDistanceSq(pos) <= range * range && !raytraceCheck) || 
      rayTraceRangeCheck(pos, wallsRange, 0.0D));
  }
  
  public static int getVulnerability(EntityPlayer player, double range, double placeRange, double wallsRange, double maxSelfDamage, double maxFriendDamage, double minDamage, double friendRange, double facePlaceHP, int minArmor, boolean cityCheck, boolean rayTrace, boolean lowArmorCheck, boolean antiSuicide, boolean antiFriendPop) {
    if (isInCity(player, range, placeRange, true, true, true, false) && cityCheck)
      return 5; 
    if (getClosestValidPos(player, maxSelfDamage, maxFriendDamage, minDamage, placeRange, wallsRange, friendRange, rayTrace, antiSuicide, antiFriendPop, true) != null)
      return 4; 
    if ((player.getHealth() + player.getAbsorptionAmount()) <= facePlaceHP)
      return 3; 
    if (isArmorLow(player, minArmor, true) && lowArmorCheck)
      return 2; 
    return 0;
  }
  
  public static Map<BlockPos, Double> mapBlockDamage(EntityPlayer player, double maxSelfDamage, double maxFriendDamage, double minDamage, double placeRange, double wallsRange, double friendRange, boolean rayTrace, boolean antiSuicide, boolean antiFriendPop) {
    Map<BlockPos, Double> damageMap = new HashMap<>();
    for (BlockPos pos : getSphere(new BlockPos((Vec3i)getFlooredPosition((Entity)mc.player)), (float)placeRange, (int)placeRange, false, true, 0)) {
      if (!canPlaceCrystal(pos, placeRange, wallsRange, rayTrace) || 
        !checkFriends(pos, maxFriendDamage, friendRange, antiFriendPop) || 
        !checkSelf(pos, maxSelfDamage, antiSuicide) || (
        rayTrace && !rayTraceRangeCheck(pos, wallsRange, 0.0D)))
        continue; 
      double damage = calculateDamage(pos, (Entity)player);
      if (damage < minDamage)
        continue; 
      damageMap.put(pos, Double.valueOf(damage));
    } 
    return damageMap;
  }
  
  public static boolean checkFriends(BlockPos pos, double maxFriendDamage, double friendRange, boolean antiFriendPop) {
    for (EntityPlayer player : mc.world.playerEntities) {
      if (mc.player.getDistanceSq((Entity)player) > friendRange * friendRange)
        continue; 
      if (calculateDamage(pos, (Entity)player) > maxFriendDamage)
        return false; 
      if (calculateDamage(pos, (Entity)player) > player.getHealth() + player.getAbsorptionAmount() && antiFriendPop)
        return false; 
    } 
    return true;
  }
  
  public static boolean checkFriends(EntityEnderCrystal crystal, double maxFriendDamage, double friendRange, boolean antiFriendPop) {
    for (EntityPlayer player : mc.world.playerEntities) {
      if (mc.player.getDistanceSq((Entity)player) > friendRange * friendRange)
        continue; 
      if (calculateDamage((Entity)crystal, (Entity)player) > maxFriendDamage)
        return false; 
      if (calculateDamage((Entity)crystal, (Entity)player) > player.getHealth() + player.getAbsorptionAmount() && antiFriendPop)
        return false; 
    } 
    return true;
  }
  
  public static boolean checkSelf(BlockPos pos, double maxSelfDamage, boolean antiSuicide) {
    boolean willPopSelf = (calculateDamage(pos, (Entity)mc.player) > mc.player.getHealth() + mc.player.getAbsorptionAmount());
    boolean willDamageSelf = (calculateDamage(pos, (Entity)mc.player) > maxSelfDamage);
    return ((!antiSuicide || !willPopSelf) && !willDamageSelf);
  }
  
  public static boolean checkSelf(EntityEnderCrystal crystal, double maxSelfDamage, boolean antiSuicide) {
    boolean willPopSelf = (calculateDamage((Entity)crystal, (Entity)mc.player) > mc.player.getHealth() + mc.player.getAbsorptionAmount());
    boolean willDamageSelf = (calculateDamage((Entity)crystal, (Entity)mc.player) > maxSelfDamage);
    return ((!antiSuicide || !willPopSelf) && !willDamageSelf);
  }
  
  public static boolean isPosValid(EntityPlayer player, BlockPos pos, double maxSelfDamage, double maxFriendDamage, double minDamage, double placeRange, double wallsRange, double friendRange, boolean rayTrace, boolean antiSuicide, boolean antiFriendPop) {
    if (pos == null)
      return false; 
    if (!isHard(mc.world.getBlockState(pos).getBlock()))
      return false; 
    if (!canPlaceCrystal(pos, placeRange, wallsRange, rayTrace))
      return false; 
    if (!checkFriends(pos, maxFriendDamage, friendRange, antiFriendPop))
      return false; 
    if (!checkSelf(pos, maxSelfDamage, antiSuicide))
      return false; 
    double damage = calculateDamage(pos, (Entity)player);
    if (damage < minDamage)
      return false; 
    if (rayTrace && !rayTraceRangeCheck(pos, wallsRange, 0.0D))
      return false; 
    return true;
  }
  
  public static BlockPos getClosestValidPos(EntityPlayer player, double maxSelfDamage, double maxFriendDamage, double minDamage, double placeRange, double wallsRange, double friendRange, boolean rayTrace, boolean antiSuicide, boolean antiFriendPop, boolean multiplace) {
    double highestDamage = -1.0D;
    BlockPos finalPos = null;
    if (player == null)
      return null; 
    List<BlockPos> placeLocations = getSphere(new BlockPos((Vec3i)getFlooredPosition((Entity)mc.player)), (float)placeRange, (int)placeRange, false, true, 0);
    placeLocations.sort(Comparator.comparing(blockPos -> Double.valueOf(mc.player.getDistanceSq(blockPos))));
    for (BlockPos pos : placeLocations) {
      if (!canPlaceCrystal(pos, placeRange, wallsRange, rayTrace) || (
        rayTrace && !rayTraceRangeCheck(pos, wallsRange, 0.0D)))
        continue; 
      double damage = calculateDamage(pos, (Entity)player);
      if (damage < minDamage || 
        !checkFriends(pos, maxFriendDamage, friendRange, antiFriendPop) || 
        !checkSelf(pos, maxSelfDamage, antiSuicide))
        continue; 
      if (damage > 15.0D)
        return pos; 
      if (damage > highestDamage) {
        highestDamage = damage;
        finalPos = pos;
      } 
    } 
    return finalPos;
  }
  
  public static BlockPos getClosestValidPosMultiThread(EntityPlayer player, double maxSelfDamage, double maxFriendDamage, double minDamage, double placeRange, double wallsRange, double friendRange, boolean rayTrace, boolean antiSuicide, boolean antiFriendPop) {
    List<ValidPosThread> threads = new CopyOnWriteArrayList<>();
    BlockPos finalPos = null;
    for (BlockPos pos : getSphere(new BlockPos(player.getPositionVector()), 13.0F, 13, false, true, 0)) {
      ValidPosThread thread = new ValidPosThread(player, pos, maxSelfDamage, maxFriendDamage, minDamage, placeRange, wallsRange, friendRange, rayTrace, antiSuicide, antiFriendPop);
      threads.add(thread);
      thread.start();
    } 
    boolean areAllInvalid = false;
    do {
      for (ValidPosThread thread : threads) {
        if (thread.isInterrupted() && 
          thread.isValid)
          finalPos = thread.pos; 
      } 
      areAllInvalid = threads.stream().noneMatch(thread -> (thread.isValid && thread.isInterrupted()));
    } while (finalPos == null && !areAllInvalid);
//    LOGGER.info((finalPos == null) ? "pos was null" : finalPos.toString());
    return finalPos;
  }
  
  public static class ValidPosThread extends Thread {
    BlockPos pos;
    
    EntityPlayer player;
    
    double maxSelfDamage;
    
    double maxFriendDamage;
    
    double minDamage;
    
    double placeRange;
    
    double wallsRange;
    
    double friendRange;
    
    boolean rayTrace;
    
    boolean antiSuicide;
    
    boolean antiFriendPop;
    
    public float damage;
    
    public boolean isValid;
    
    public CombatPosInfo info;
    
    public ValidPosThread(EntityPlayer player, BlockPos pos, double maxSelfDamage, double maxFriendDamage, double minDamage, double placeRange, double wallsRange, double friendRange, boolean rayTrace, boolean antiSuicide, boolean antiFriendPop) {
      super("Break");
      this.pos = pos;
      this.maxSelfDamage = maxSelfDamage;
      this.maxFriendDamage = maxFriendDamage;
      this.minDamage = minDamage;
      this.placeRange = placeRange;
      this.wallsRange = wallsRange;
      this.friendRange = friendRange;
      this.rayTrace = rayTrace;
      this.antiSuicide = antiSuicide;
      this.antiFriendPop = antiFriendPop;
      this.player = player;
    }
    
    public void run() {
      if (CombatUtil.mc.player.getDistanceSq(this.pos) <= this.placeRange * this.placeRange && 
        CombatUtil.canPlaceCrystal(this.pos, this.placeRange, this.wallsRange, this.rayTrace) && 
        CombatUtil.checkFriends(this.pos, this.maxFriendDamage, this.friendRange, this.antiFriendPop) && 
        CombatUtil.checkSelf(this.pos, this.maxSelfDamage, this.antiSuicide)) {
        this.damage = CombatUtil.calculateDamage(this.pos, (Entity)this.player);
        if (this.damage >= this.minDamage && (
          !this.rayTrace || CombatUtil.rayTraceRangeCheck(this.pos, this.wallsRange, 0.0D))) {
          this.isValid = true;
          this.info = new CombatPosInfo(this.player, this.pos, this.damage);
//          V12Money.LOGGER.info("Pos was valid.");
          return;
        } 
      } 
      this.isValid = false;
      this.info = new CombatPosInfo(this.player, this.pos, -1.0F);
//      V12Money.LOGGER.info("Pos was invalid.");
    }
  }
  
  public static class CombatPosInfo {
    public EntityPlayer player;
    
    public BlockPos pos;
    
    public float damage;
    
    public CombatPosInfo(EntityPlayer player, BlockPos pos, float damage) {
      this.pos = pos;
      this.damage = damage;
      this.player = player;
    }
  }
  
  public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
    List<BlockPos> circleblocks = new ArrayList<>();
    int cx = pos.getX();
    int cy = pos.getY();
    int cz = pos.getZ();
    for (int x = cx - (int)r; x <= cx + r; x++) {
      for (int z = cz - (int)r; z <= cz + r; ) {
        int y = sphere ? (cy - (int)r) : cy;
        for (;; z++) {
          if (y < (sphere ? (cy + r) : (cy + h))) {
            double dist = ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0));
            if (dist < (r * r) && (!hollow || dist >= ((r - 1.0F) * (r - 1.0F)))) {
              BlockPos l = new BlockPos(x, y + plus_y, z);
              circleblocks.add(l);
            } 
            y++;
            continue;
          } 
        } 
      } 
    } 
    return circleblocks;
  }
  
  public static boolean isArmorLow(EntityPlayer player, int durability, boolean checkDurability) {
    for (ItemStack piece : player.inventory.armorInventory) {
      if (piece == null)
        return true; 
      if (checkDurability && 
        getItemDamage(piece) < durability)
        return true; 
    } 
    return false;
  }
  
  public static int getItemDamage(ItemStack stack) {
    return stack.getMaxDamage() - stack.getItemDamage();
  }
  
  public static boolean rayTraceRangeCheck(Entity target, double range) {
    boolean isVisible = mc.player.canEntityBeSeen(target);
    return (!isVisible || mc.player.getDistanceSq(target) <= range * range);
  }
  
  public static boolean rayTraceRangeCheck(BlockPos pos, double range, double height) {
    RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX(), pos.getY() + height, pos.getZ()), false, true, false);
    return (result == null || mc.player.getDistanceSq(pos) <= range * range);
  }
  
  public static EntityEnderCrystal getClosestValidCrystal(EntityPlayer player, double maxSelfDamage, double maxFriendDamage, double minDamage, double breakRange, double wallsRange, double friendRange, boolean rayTrace, boolean antiSuicide, boolean antiFriendPop) {
    if (player == null)
      return null; 
    List<EntityEnderCrystal> crystals = (List<EntityEnderCrystal>)mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal).filter(entity -> (mc.player.getDistanceSq(entity) <= breakRange * breakRange)).sorted(Comparator.comparingDouble(entity -> mc.player.getDistanceSq(entity))).map(entity -> (EntityEnderCrystal)entity).collect(Collectors.toList());
    for (EntityEnderCrystal crystal : crystals) {
      if ((rayTrace && !rayTraceRangeCheck((Entity)crystal, wallsRange)) || 
        calculateDamage((Entity)crystal, (Entity)player) < minDamage || 
        !checkSelf(crystal, maxSelfDamage, antiSuicide) || 
        !checkFriends(crystal, maxFriendDamage, friendRange, antiFriendPop))
        continue; 
      return crystal;
    } 
    return null;
  }
  
  public static List<BlockPos> getDisc(BlockPos pos, float r) {
    List<BlockPos> circleblocks = new ArrayList<>();
    int cx = pos.getX();
    int cy = pos.getY();
    int cz = pos.getZ();
    for (int x = cx - (int)r; x <= cx + r; x++) {
      for (int z = cz - (int)r; z <= cz + r; z++) {
        double dist = ((cx - x) * (cx - x) + (cz - z) * (cz - z));
        if (dist < (r * r)) {
          BlockPos position = new BlockPos(x, cy, z);
          circleblocks.add(position);
        } 
      } 
    } 
    return circleblocks;
  }
  
  public static BlockPos getFlooredPosition(Entity entity) {
    return new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY), Math.floor(entity.posZ));
  }
  
  public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
    float doubleExplosionSize = 12.0F;
    double distancedsize = entity.getDistance(posX, posY, posZ) / doubleExplosionSize;
    Vec3d vec3d = new Vec3d(posX, posY, posZ);
    double blockDensity = 0.0D;
    try {
      blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
    } catch (Exception exception) {}
    double v = (1.0D - distancedsize) * blockDensity;
    float damage = (int)((v * v + v) / 2.0D * 7.0D * doubleExplosionSize + 1.0D);
    double finald = 1.0D;
    if (entity instanceof EntityLivingBase)
      finald = getBlastReduction((EntityLivingBase)entity, getDamageMultiplied(damage), new Explosion((World)(Minecraft.getMinecraft()).world, null, posX, posY, posZ, 6.0F, false, true)); 
    return (float)finald;
  }
  
  public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
    float damage = damageI;
    if (entity instanceof EntityPlayer) {
      EntityPlayer ep = (EntityPlayer)entity;
      DamageSource ds = DamageSource.causeExplosionDamage(explosion);
      damage = CombatRules.getDamageAfterAbsorb(damage, ep.getTotalArmorValue(), (float)ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
      int k = 0;
      try {
        k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
      } catch (Exception exception) {}
      float f = MathHelper.clamp(k, 0.0F, 20.0F);
      damage *= 1.0F - f / 25.0F;
      if (entity.isPotionActive(MobEffects.RESISTANCE))
        damage -= damage / 4.0F; 
      damage = Math.max(damage, 0.0F);
      return damage;
    } 
    damage = CombatRules.getDamageAfterAbsorb(damage, entity.getTotalArmorValue(), (float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
    return damage;
  }
  
  public static float getDamageMultiplied(float damage) {
    int diff = (Minecraft.getMinecraft()).world.getDifficulty().getId();
    return damage * ((diff == 0) ? 0.0F : ((diff == 2) ? 1.0F : ((diff == 1) ? 0.5F : 1.5F)));
  }
  
  public static float calculateDamage(Entity crystal, Entity entity) {
    return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
  }
  
  public static float calculateDamage(BlockPos pos, Entity entity) {
    return calculateDamage(pos.getX() + 0.5D, (pos.getY() + 1), pos.getZ() + 0.5D, entity);
  }
  
  public static Vec3d interpolateEntity(Entity entity) {
    return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.getRenderPartialTicks(), entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks(), entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.getRenderPartialTicks());
  }
  
  public static float[] calcAngle(Vec3d from, Vec3d to) {
    double difX = to.x - from.x;
    double difY = (to.y - from.y) * -1.0D;
    double difZ = to.z - from.z;
    double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
    return new float[] { (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0D), (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))) };
  }
  
  public static float[] getLegitRotations(Vec3d vec) {
    Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    double diffX = vec.x - eyesPos.x;
    double diffY = vec.y - eyesPos.y;
    double diffZ = vec.z - eyesPos.z;
    double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
    float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
    float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));
    return new float[] { mc.player.rotationYaw + 
        MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + 
        MathHelper.wrapDegrees(pitch - mc.player.rotationPitch) };
  }
  
  public static byte getArmorPieces(EntityPlayer target) {
    byte i = 0;
    if (target.inventoryContainer.getSlot(5).getStack().getItem().equals(Items.DIAMOND_HELMET))
      i = (byte)(i + 1); 
    if (target.inventoryContainer.getSlot(6).getStack().getItem().equals(Items.DIAMOND_CHESTPLATE))
      i = (byte)(i + 1); 
    if (target.inventoryContainer.getSlot(7).getStack().getItem().equals(Items.DIAMOND_LEGGINGS))
      i = (byte)(i + 1); 
    if (target.inventoryContainer.getSlot(8).getStack().getItem().equals(Items.DIAMOND_BOOTS))
      i = (byte)(i + 1); 
    return i;
  }
}
