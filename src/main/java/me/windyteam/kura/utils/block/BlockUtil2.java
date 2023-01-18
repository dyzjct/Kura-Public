package me.windyteam.kura.utils.block;

import com.google.common.util.concurrent.AtomicDouble;
//import dev.dyzjct.ArkHack.ArkTeam;
//import dev.dyzjct.ArkHack.features.command.Command;
import me.windyteam.kura.utils.MathUtil;
import me.windyteam.kura.utils.entity.EntityUtil;
import me.windyteam.kura.utils.inventory.InventoryUtil;
import me.windyteam.kura.utils.math.RotationUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.utils.MathUtil;
import me.windyteam.kura.utils.entity.EntityUtil;
import me.windyteam.kura.utils.math.RotationUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class BlockUtil2 {
    final static Minecraft mc = Minecraft.getMinecraft();
    public static final List<Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);
    public static final List<Block> shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
    public static final List<Block> unSafeBlocks = Arrays.asList(Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.ENDER_CHEST, Blocks.ANVIL);
    public static List<Block> unSolidBlocks = Arrays.asList(Blocks.FLOWING_LAVA, Blocks.FLOWER_POT, Blocks.SNOW, Blocks.CARPET, Blocks.END_ROD, Blocks.SKULL, Blocks.FLOWER_POT, Blocks.TRIPWIRE, Blocks.TRIPWIRE_HOOK, Blocks.WOODEN_BUTTON, Blocks.LEVER, Blocks.STONE_BUTTON, Blocks.LADDER, Blocks.UNPOWERED_COMPARATOR, Blocks.POWERED_COMPARATOR, Blocks.UNPOWERED_REPEATER, Blocks.POWERED_REPEATER, Blocks.UNLIT_REDSTONE_TORCH, Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WIRE, Blocks.AIR, Blocks.PORTAL, Blocks.END_PORTAL, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.SAPLING, Blocks.RED_FLOWER, Blocks.YELLOW_FLOWER, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS, Blocks.REEDS, Blocks.PUMPKIN_STEM, Blocks.MELON_STEM, Blocks.WATERLILY, Blocks.NETHER_WART, Blocks.COCOA, Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT, Blocks.TALLGRASS, Blocks.DEADBUSH, Blocks.VINE, Blocks.FIRE, Blocks.RAIL, Blocks.ACTIVATOR_RAIL, Blocks.DETECTOR_RAIL, Blocks.GOLDEN_RAIL, Blocks.TORCH);

    public static List<BlockPos> getBlockSphere(float breakRange, Class clazz) {
        NonNullList positions = NonNullList.create();
        positions.addAll((Collection) BlockUtil2.getSphere(EntityUtil.getPlayerPos((EntityPlayer) mc.player), breakRange, (int) breakRange, false, true, 0).stream().filter(pos -> clazz.isInstance(mc.world.getBlockState(pos).getBlock())).collect(Collectors.toList()));
        return positions;
    }
    public static boolean canBreak(BlockPos pos, boolean air) {
        IBlockState blockState = mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        if (mc.world.getBlockState(pos).getBlock() == Blocks.AIR) {
            return air;
        }
        if (mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK) {
            return false;
        }
        if (mc.world.getBlockState(pos).getBlock() == Blocks.END_PORTAL_FRAME) {
            return false;
        }
        if (mc.world.getBlockState(pos).getBlock() == Blocks.END_PORTAL) {
            return false;
        }
        if (mc.world.getBlockState(pos).getBlock() == Blocks.WATER) {
            return false;
        }
        if (mc.world.getBlockState(pos).getBlock() == Blocks.FLOWING_WATER) {
            return false;
        }
        if (mc.world.getBlockState(pos).getBlock() == Blocks.LAVA) {
            return false;
        }
        if (mc.world.getBlockState(pos).getBlock() == Blocks.FLOWING_LAVA) {
            return false;
        }
        return block.getBlockHardness(blockState, mc.world, pos) != -1.0f;
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        ArrayList<EnumFacing> facings = new ArrayList<EnumFacing>();
        for (EnumFacing side : EnumFacing.values()) {
            IBlockState blockState;
            BlockPos neighbour = pos.offset(side);
            try {
                if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false) || (blockState = mc.world.getBlockState(neighbour)).getMaterial().isReplaceable())
                    continue;
                facings.add(side);
            }catch (Exception e){
                //shit
            }
        }
        return facings;
    }

    public static EnumFacing getFacing(BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            RayTraceResult rayTraceResult = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) pos.getX() + 0.5 + (double) facing.getDirectionVec().getX() * 1.0 / 2.0, (double) pos.getY() + 0.5 + (double) facing.getDirectionVec().getY() * 1.0 / 2.0, (double) pos.getZ() + 0.5 + (double) facing.getDirectionVec().getZ() * 1.0 / 2.0), false, true, false);
            if (rayTraceResult != null && (rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK || !rayTraceResult.getBlockPos().equals((Object) pos)))
                continue;
            return facing;
        }
        if ((double) pos.getY() > mc.player.posY + (double) mc.player.getEyeHeight()) {
            return EnumFacing.DOWN;
        }
        return EnumFacing.UP;
    }
    public static boolean placeBlockSmartRotate(BlockPos pos, EnumHand hand , boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = BlockUtil2.getFirstFacing(pos);
        ChatUtil.sendMessage(side.toString());
        if (side == null) {
            return isSneaking;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d((Vec3i) neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
        if (!mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            mc.player.connection.sendPacket((Packet) new CPacketEntityAction((Entity) mc.player, CPacketEntityAction.Action.START_SNEAKING));
            sneaking = true;
        }
        BlockUtil2.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }

    public static EnumFacing getFirstFacing(BlockPos pos) {
        Iterator<EnumFacing> iterator = BlockUtil2.getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            EnumFacing facing = iterator.next();
            return facing;
        }
        return null;
    }

    public static EnumFacing getRayTraceFacing(BlockPos pos) {
        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) pos.getX() + 0.5, (double) pos.getX() - 0.5, (double) pos.getX() + 0.5));
        if (result == null || result.sideHit == null) {
            return EnumFacing.UP;
        }
        return result.sideHit;
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace) {
        return BlockUtil2.isPositionPlaceable(pos, rayTrace, true);
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace, boolean entityCheck) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow)) {
            return 0;
        }
        if (!BlockUtil2.rayTracePlaceCheck(pos, rayTrace, 0.0f)) {
            return -1;
        }
        if (entityCheck) {
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                return 1;
            }
        }
        for (EnumFacing side : BlockUtil2.getPossibleSides(pos)) {
            if (!BlockUtil2.canBeClicked(pos.offset(side))) continue;
            return 3;
        }
        return 2;
    }

    public static void placeBlockScaffold(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ);
        for (EnumFacing side : EnumFacing.values()) {
            Vec3d vec3d = null;
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();
            if (!BlockUtil2.canBeClicked(neighbor)) continue;
            Vec3d hitVec = new Vec3d((Vec3i) neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
            if (!(eyesPos.squareDistanceTo(vec3d) <= 18.0625)) continue;
            BlockUtil2.faceVectorPacketInstant(hitVec);
            BlockUtil2.processRightClickBlock(neighbor, side2, hitVec);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.rightClickDelayTimer = 4;
            return;
        }
    }

    static void processRightClickBlock(BlockPos pos, EnumFacing side, Vec3d hitVec) {
        BlockUtil2.getPlayerController().processRightClickBlock(mc.player, mc.world, pos, side, hitVec, EnumHand.MAIN_HAND);
    }

    private static PlayerControllerMP getPlayerController() {
        return Minecraft.getMinecraft().playerController;
    }

    public static void faceVectorPacketInstant(Vec3d vec) {
        float[] rotations = RotationUtil.getLegitRotations(vec);
        mc.player.connection.sendPacket((Packet) new CPacketPlayer.Rotation(rotations[0], rotations[1], mc.player.onGround));
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float) (vec.x - (double) pos.getX());
            float f1 = (float) (vec.y - (double) pos.getY());
            float f2 = (float) (vec.z - (double) pos.getZ());
            mc.player.connection.sendPacket((Packet) new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, direction, vec, hand);
        }
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
    }

    public static boolean placeBlockNotRetarded(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean altRotate) {
        EnumFacing side = BlockUtil2.getFirstFacing(pos);
        if (side == null) {
            return false;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d((Vec3i) neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
        if (!mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            mc.player.connection.sendPacket((Packet) new CPacketEntityAction((Entity) mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.setSneaking(true);
        }
        if (rotate) {
            RotationUtil.faceVector(altRotate ? new Vec3d((Vec3i) pos) : hitVec, true);
        }
        BlockUtil2.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        return true;
    }

    public static boolean canBlockBeSeen(double x, double y, double z) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(x, y + 1.7, z), false, true, false) == null;
    }

    public static void rightClickBlockLegit(BlockPos pos, float range, boolean rotate, EnumHand hand, AtomicDouble Yaw, AtomicDouble Pitch, AtomicBoolean rotating) {
        Vec3d eyesPos = RotationUtil.getEyesPos();
        Vec3d posVec = new Vec3d((Vec3i) pos).add(0.5, 0.5, 0.5);
        double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
        for (EnumFacing side : EnumFacing.values()) {
            Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
            double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);
            if (!(distanceSqHitVec <= MathUtil.square(range)) || !(distanceSqHitVec < distanceSqPosVec) || mc.world.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null)
                continue;
            if (rotate) {
                float[] rotations = RotationUtil.getLegitRotations(hitVec);
                Yaw.set((double) rotations[0]);
                Pitch.set((double) rotations[1]);
                rotating.set(true);
            }
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, side, hitVec, hand);
            mc.player.swingArm(hand);
            mc.rightClickDelayTimer = 4;
            break;
        }
    }

    public static boolean placeBlock(final BlockPos pos, final EnumHand hand, final boolean rotate, final boolean packet, final boolean isSneaking) {
        boolean sneaking = false;
        final EnumFacing side = getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        final Vec3d hitVec = new Vec3d((Vec3i) neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
        if (!mc.player.isSneaking() && (BlockUtil2.blackList.contains(neighbourBlock) || BlockUtil2.shulkerList.contains(neighbourBlock))) {
            mc.player.connection.sendPacket((Packet) new CPacketEntityAction((Entity) mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.setSneaking(true);
            sneaking = true;
        }
        if (rotate) {
            RotationUtil.faceVector(hitVec, true);
        }
        rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }

    public static boolean placeBlock2(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = BlockUtil2.getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d((Vec3i) neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
        if (!mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            mc.player.connection.sendPacket((Packet) new CPacketEntityAction((Entity) mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            mc.player.setSneaking(false);
            sneaking = true;
        }
        if (rotate) {
            RotationUtil.faceVector(hitVec, true);
        }
        BlockUtil2.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }

//    public static boolean placeBlockSmartRotate(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
//        boolean sneaking = false;
//        EnumFacing side = BlockUtil.getFirstFacing(pos);
//        Command.sendMessage(side.toString());
//        if (side == null) {
//            return isSneaking;
//        }
//        BlockPos neighbour = pos.offset(side);
//        EnumFacing opposite = side.getOpposite();
//        Vec3d hitVec = new Vec3d((Vec3i) neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
//        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
//        if (!mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
//            mc.player.connection.sendPacket((Packet) new CPacketEntityAction((Entity) mc.player, CPacketEntityAction.Action.START_SNEAKING));
//            sneaking = true;
//        }
//        if (rotate) {
//            ArkTeam.rotationManager.lookAtVec3d(hitVec);
//        }
//        BlockUtil.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
//        mc.player.swingArm(EnumHand.MAIN_HAND);
//        mc.rightClickDelayTimer = 4;
//        return sneaking || isSneaking;
//    }
//
//    public static void placeBlockStopSneaking(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
//        boolean sneaking = BlockUtil.placeBlockSmartRotate(pos, hand, rotate, packet, isSneaking);
//        if (!isSneaking && sneaking) {
//            mc.player.connection.sendPacket((Packet) new CPacketEntityAction((Entity) mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
//        }
//    }

    public static Vec3d[] getHelpingBlocks(Vec3d vec3d) {
        return new Vec3d[]{new Vec3d(vec3d.x, vec3d.y - 1.0, vec3d.z), new Vec3d(vec3d.x != 0.0 ? vec3d.x * 2.0 : vec3d.x, vec3d.y, vec3d.x != 0.0 ? vec3d.z : vec3d.z * 2.0), new Vec3d(vec3d.x == 0.0 ? vec3d.x + 1.0 : vec3d.x, vec3d.y, vec3d.x == 0.0 ? vec3d.z : vec3d.z + 1.0), new Vec3d(vec3d.x == 0.0 ? vec3d.x - 1.0 : vec3d.x, vec3d.y, vec3d.x == 0.0 ? vec3d.z : vec3d.z - 1.0), new Vec3d(vec3d.x, vec3d.y + 1.0, vec3d.z)};
    }

    public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();
        int x = cx - (int) r;
        while ((float) x <= (float) cx + r) {
            int z = cz - (int) r;
            while ((float) z <= (float) cz + r) {
                int y = sphere ? cy - (int) r : cy;
                while (true) {
                    float f2;
                    float f = y;
                    float f3 = f2 = sphere ? (float) cy + r : (float) (cy + h);
                    if (!(f < f2)) break;
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < (double) (r * r) && (!hollow || dist >= (double) ((r - 1.0f) * (r - 1.0f)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return circleblocks;
    }

    public static List<BlockPos> possiblePlacePositions(float placeRange, boolean specialEntityCheck, boolean oneDot15) {
        NonNullList positions = NonNullList.create();
        positions.addAll((Collection) BlockUtil2.getSphere(EntityUtil.getPlayerPos((EntityPlayer) mc.player), placeRange, (int) placeRange, false, true, 0).stream().filter(pos -> BlockUtil2.canPlaceCrystal(pos, specialEntityCheck, oneDot15)).collect(Collectors.toList()));
        return positions;
    }

    public static boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck, boolean oneDot15) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
            if (!oneDot15 && mc.world.getBlockState(boost2).getBlock() != Blocks.AIR || mc.world.getBlockState(boost).getBlock() != Blocks.AIR) {
                return false;
            }
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                if (entity.isDead || specialEntityCheck && entity instanceof EntityEnderCrystal) continue;
                return false;
            }
            if (!oneDot15) {
                for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                    if (entity.isDead || specialEntityCheck && entity instanceof EntityEnderCrystal) continue;
                    return false;
                }
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    public static boolean canBeClicked(BlockPos pos) {
        return BlockUtil2.getBlock(pos).canCollideCheck(BlockUtil2.getState(pos), false);
    }

    private static Block getBlock(BlockPos pos) {
        return BlockUtil2.getState(pos).getBlock();
    }

    private static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static boolean isBlockAboveEntitySolid(Entity entity) {
        if (entity != null) {
            BlockPos pos = new BlockPos(entity.posX, entity.posY + 2.0, entity.posZ);
            return BlockUtil2.isBlockSolid(pos);
        }
        return false;
    }

//    public static void debugPos(String message, BlockPos pos) {
//        Command.sendMessage(message + pos.getX() + "x, " + pos.getY() + "y, " + pos.getZ() + "z");
//    }

//    public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand, boolean swing, boolean exactHand, boolean silent) {
//        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) pos.getX() + 0.5, (double) pos.getY() - 0.5, (double) pos.getZ() + 0.5));
//        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
//        int old = mc.player.inventory.currentItem;
//        int crystal = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
//        if (hand == EnumHand.MAIN_HAND && silent && crystal != -1 && crystal != mc.player.inventory.currentItem) {
//            mc.player.connection.sendPacket((Packet) new CPacketHeldItemChange(crystal));
//        }
//        mc.player.connection.sendPacket((Packet) new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0f, 0.0f, 0.0f));
//        if (hand == EnumHand.MAIN_HAND && silent && crystal != -1 && crystal != mc.player.inventory.currentItem) {
//            mc.player.connection.sendPacket((Packet) new CPacketHeldItemChange(old));
//        }
//        if (swing) {
//            mc.player.connection.sendPacket((Packet) new CPacketAnimation(exactHand ? hand : EnumHand.MAIN_HAND));
//        }
//    }

    public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand) {
        mc.player.connection.sendPacket((Packet) new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, hand, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ()));
    }

    public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand, boolean swing, boolean exactHand, boolean silent) {
        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) pos.getX() + 0.5, (double) pos.getY() - 0.5, (double) pos.getZ() + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        int old = mc.player.inventory.currentItem;
        int crystal = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
        if (hand == EnumHand.MAIN_HAND && silent && crystal != -1 && crystal != mc.player.inventory.currentItem) {
            mc.player.connection.sendPacket((Packet) new CPacketHeldItemChange(crystal));
        }
        mc.player.connection.sendPacket((Packet) new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0f, 0.0f, 0.0f));
        if (hand == EnumHand.MAIN_HAND && silent && crystal != -1 && crystal != mc.player.inventory.currentItem) {
            mc.player.connection.sendPacket((Packet) new CPacketHeldItemChange(old));
        }
        if (swing) {
            mc.player.connection.sendPacket((Packet) new CPacketAnimation(exactHand ? hand : EnumHand.MAIN_HAND));
        }
    }

    public static boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
            if (mc.world.getBlockState(boost).getBlock() != Blocks.AIR || mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
                return false;
            }
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                if (entity instanceof EntityEnderCrystal) continue;
                return false;
            }
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                if (entity instanceof EntityEnderCrystal) continue;
                return false;
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    public static BlockPos[] toBlockPos(Vec3d[] vec3ds) {
        BlockPos[] list = new BlockPos[vec3ds.length];
        for (int i = 0; i < vec3ds.length; ++i) {
            list[i] = new BlockPos(vec3ds[i]);
        }
        return list;
    }

    public static Vec3d posToVec3d(BlockPos pos) {
        return new Vec3d((Vec3i) pos);
    }

    public static BlockPos vec3dToPos(Vec3d vec3d) {
        return new BlockPos(vec3d);
    }

//    public static Boolean isPosInFov(BlockPos pos) {
//        int dirnumber = RotationUtil.getDirection4D();
//        if (dirnumber == 0 && (double) pos.getZ() - mc.player.getPositionVector().z < 0.0) {
//            return false;
//        }
//        if (dirnumber == 1 && (double) pos.getX() - mc.player.getPositionVector().x > 0.0) {
//            return false;
//        }
//        if (dirnumber == 2 && (double) pos.getZ() - mc.player.getPositionVector().z > 0.0) {
//            return false;
//        }
//        return dirnumber != 3 || (double) pos.getX() - mc.player.getPositionVector().x >= 0.0;
//    }

    public static boolean isBlockBelowEntitySolid(Entity entity) {
        if (entity != null) {
            BlockPos pos = new BlockPos(entity.posX, entity.posY - 1.0, entity.posZ);
            return BlockUtil2.isBlockSolid(pos);
        }
        return false;
    }

    public static boolean isBlockSolid(BlockPos pos) {
        return !BlockUtil2.isBlockUnSolid(pos);
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
        for (int i = 0; i < input.length; ++i) {
            output[i] = vec3d.add(input[i]);
        }
        return output;
    }

    public static Vec3d[] convertVec3ds(EntityPlayer entity, Vec3d[] input) {
        return BlockUtil2.convertVec3ds(entity.getPositionVector(), input);
    }

    public static boolean canBreak(BlockPos pos) {
        IBlockState blockState = mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, (World) mc.world, pos) != -1.0f;
    }

    public static boolean isValidBlock(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        return !(block instanceof BlockLiquid) && block.getMaterial(null) != Material.AIR;
    }

    public static boolean isScaffoldPos(BlockPos pos) {
        return mc.world.isAirBlock(pos) || mc.world.getBlockState(pos).getBlock() == Blocks.SNOW_LAYER || mc.world.getBlockState(pos).getBlock() == Blocks.TALLGRASS || mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid;
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
        return !shouldCheck || mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) pos.getX(), (double) ((float) pos.getY() + height), (double) pos.getZ()), false, true, false) == null;
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck) {
        return BlockUtil2.rayTracePlaceCheck(pos, shouldCheck, 1.0f);
    }

    public static boolean rayTracePlaceCheck(BlockPos pos) {
        return BlockUtil2.rayTracePlaceCheck(pos, true);
    }

    public static double getDistance32k(Entity entity, BlockPos autoPos) {
        return 0;
    }

    public static Boolean isPosInFov(BlockPos pos) {
        int dirnumber = RotationUtil.getDirection4D();
        if (dirnumber == 0 && (double) pos.getZ() - BlockUtil2.mc.player.getPositionVector().z < 0.0) {
            return false;
        }
        if (dirnumber == 1 && (double) pos.getX() - BlockUtil2.mc.player.getPositionVector().x > 0.0) {
            return false;
        }
        if (dirnumber == 2 && (double) pos.getZ() - BlockUtil2.mc.player.getPositionVector().z > 0.0) {
            return false;
        }
        return dirnumber != 3 || (double) pos.getX() - BlockUtil2.mc.player.getPositionVector().x >= 0.0;
    }

}