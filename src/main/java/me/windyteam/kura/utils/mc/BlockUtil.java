package me.windyteam.kura.utils.mc;

import me.windyteam.kura.event.events.entity.MotionUpdateEvent;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.block.BlockInteractionHelper;
import me.windyteam.kura.utils.math.RotationUtil;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class BlockUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static List<Block> emptyBlocks = Arrays.asList(Blocks.AIR, Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.WATER, Blocks.VINE, Blocks.SNOW_LAYER, Blocks.TALLGRASS, Blocks.FIRE);
    public static List<Block> rightclickableBlocks = Arrays.asList(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.ANVIL, Blocks.WOODEN_BUTTON, Blocks.STONE_BUTTON, Blocks.UNPOWERED_COMPARATOR, Blocks.UNPOWERED_REPEATER, Blocks.POWERED_REPEATER, Blocks.POWERED_COMPARATOR, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.BREWING_STAND, Blocks.DISPENSER, Blocks.DROPPER, Blocks.LEVER, Blocks.NOTEBLOCK, Blocks.JUKEBOX, Blocks.BEACON, Blocks.BED, Blocks.FURNACE, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR, Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR, Blocks.CAKE, Blocks.ENCHANTING_TABLE, Blocks.DRAGON_EGG, Blocks.HOPPER, Blocks.REPEATING_COMMAND_BLOCK, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.CRAFTING_TABLE);
    public static final List<Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER);
    public static final List<Block> shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
    public static List<Block> unSolidBlocks = Arrays.asList(Blocks.FLOWING_LAVA, Blocks.FLOWER_POT, Blocks.SNOW, Blocks.CARPET, Blocks.END_ROD, Blocks.SKULL, Blocks.FLOWER_POT, Blocks.TRIPWIRE, Blocks.TRIPWIRE_HOOK, Blocks.WOODEN_BUTTON, Blocks.LEVER, Blocks.STONE_BUTTON, Blocks.LADDER, Blocks.UNPOWERED_COMPARATOR, Blocks.POWERED_COMPARATOR, Blocks.UNPOWERED_REPEATER, Blocks.POWERED_REPEATER, Blocks.UNLIT_REDSTONE_TORCH, Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WIRE, Blocks.AIR, Blocks.PORTAL, Blocks.END_PORTAL, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.SAPLING, Blocks.RED_FLOWER, Blocks.YELLOW_FLOWER, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS, Blocks.REEDS, Blocks.PUMPKIN_STEM, Blocks.MELON_STEM, Blocks.WATERLILY, Blocks.NETHER_WART, Blocks.COCOA, Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT, Blocks.TALLGRASS, Blocks.DEADBUSH, Blocks.VINE, Blocks.FIRE, Blocks.RAIL, Blocks.ACTIVATOR_RAIL, Blocks.DETECTOR_RAIL, Blocks.GOLDEN_RAIL, Blocks.TORCH);



    public static BlockPos[] toBlockPos(Vec3d[] vec3ds) {
        BlockPos[] list = new BlockPos[vec3ds.length];
        for (int i = 0; i < vec3ds.length; ++i) {
            list[i] = new BlockPos(vec3ds[i]);
        }
        return list;
    }

    public static EnumFacing getFacing(BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            RayTraceResult rayTraceResult = BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + (double) BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d((double) pos.getX() + 0.5 + (double) facing.getDirectionVec().getX() * 1.0 / 2.0, (double) pos.getY() + 0.5 + (double) facing.getDirectionVec().getY() * 1.0 / 2.0, (double) pos.getZ() + 0.5 + (double) facing.getDirectionVec().getZ() * 1.0 / 2.0), false, true, false);
            if (rayTraceResult != null && (rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK || !rayTraceResult.getBlockPos().equals(pos)))
                continue;
            return facing;
        }
        if ((double) pos.getY() > BlockUtil.mc.player.posY + (double) BlockUtil.mc.player.getEyeHeight()) {
            return EnumFacing.DOWN;
        }
        return EnumFacing.UP;
    }

    public static boolean isElseHole(BlockPos blockPos) {
        for (BlockPos pos : BlockUtil.getTouchingBlocks(blockPos)) {
            IBlockState touchingState = BlockUtil.mc.world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.AIR && touchingState.isFullBlock()) continue;
            return false;
        }
        return true;
    }

    public static BlockPos[] getTouchingBlocks(BlockPos blockPos) {
        return new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
    }

    public static boolean isBlockSolid(BlockPos pos) {
        return !BlockUtil.isBlockUnSolid(pos);
    }

    public static boolean isBlockUnSolid(BlockPos pos) {
        return BlockUtil.isBlockUnSolid(BlockUtil.mc.world.getBlockState(pos).getBlock());
    }

    public static boolean isBlockUnSolid(Block block) {
        return unSolidBlocks.contains(block);
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        ArrayList<EnumFacing> facings = new ArrayList<EnumFacing>();
        if (BlockUtil.mc.world == null || pos == null) {
            return facings;
        }
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            IBlockState blockState = BlockUtil.mc.world.getBlockState(neighbour);
            if (blockState == null || !blockState.getBlock().canCollideCheck(blockState, false) || blockState.getMaterial().isReplaceable())
                continue;
            facings.add(side);
        }
        return facings;
    }

    public static EnumFacing getFirstFacing(BlockPos pos) {
        Iterator<EnumFacing> iterator2 = BlockUtil.getPossibleSides(pos).iterator();
        if (iterator2.hasNext()) {
            EnumFacing facing = iterator2.next();
            return facing;
        }
        return null;
    }

    public static boolean placeBlock(MotionUpdateEvent.Tick event, BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = BlockUtil.getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = BlockUtil.mc.world.getBlockState(neighbour).getBlock();
        if (!BlockUtil.mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            BlockUtil.mc.player.setSneaking(true);
            sneaking = true;
        }
        if (rotate) {
            mc.player.rotationYawHead = BlockInteractionHelper.getLegitRotations(new Vec3d(hitVec.x, hitVec.y, hitVec.z))[0];
            mc.player.renderYawOffset = BlockInteractionHelper.getLegitRotations(new Vec3d(hitVec.x, hitVec.y, hitVec.z))[0];
            event.setYaw(BlockInteractionHelper.getLegitRotations(new Vec3d(hitVec.x, hitVec.y, hitVec.z))[0]);
            event.setPitch(BlockInteractionHelper.getLegitRotations(new Vec3d(hitVec.x, hitVec.y, hitVec.z))[1]);
        }
        BlockUtil.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        BlockUtil.mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }

    public static boolean placeBlockSmartRotate(MotionUpdateEvent.Tick event, BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = BlockUtil.getFirstFacing(pos);
        ChatUtil.NoSpam.sendMessage(side.toString());
        if (side == null) {
            return isSneaking;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = BlockUtil.mc.world.getBlockState(neighbour).getBlock();
        if (!BlockUtil.mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            sneaking = true;
        }
        if (rotate) {
            mc.player.rotationYawHead = BlockInteractionHelper.getLegitRotations(new Vec3d(hitVec.x, hitVec.y, hitVec.z))[0];
            mc.player.renderYawOffset = BlockInteractionHelper.getLegitRotations(new Vec3d(hitVec.x, hitVec.y, hitVec.z))[0];
            event.setYaw(BlockInteractionHelper.getLegitRotations(new Vec3d(hitVec.x, hitVec.y, hitVec.z))[0]);
            event.setPitch(BlockInteractionHelper.getLegitRotations(new Vec3d(hitVec.x, hitVec.y, hitVec.z))[1]);
        }
        BlockUtil.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        BlockUtil.mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace) {
        return BlockUtil.isPositionPlaceable(pos, rayTrace, true);
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace, boolean entityCheck) {
        Block block = BlockUtil.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow)) {
            return 0;
        }
        if (!BlockUtil.rayTracePlaceCheck(pos, rayTrace, 0.0f)) {
            return -1;
        }
        if (entityCheck) {
            for (Object entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                return 1;
            }
        }
        for (EnumFacing side : BlockUtil.getPossibleSides(pos)) {
            if (!BlockUtil.canBeClicked(pos.offset(side))) continue;
            return 3;
        }
        return 2;
    }

    public static Vec3d[] convertVec3ds(Vec3d vec3d, Vec3d[] input) {
        Vec3d[] output = new Vec3d[input.length];
        for (int i = 0; i < input.length; ++i) {
            output[i] = vec3d.add(input[i]);
        }
        return output;
    }

    public static Vec3d[] convertVec3ds(EntityPlayer entity, Vec3d[] input) {
        return BlockUtil.convertVec3ds(entity.getPositionVector(), input);
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float) (vec.x - (double) pos.getX());
            float f2 = (float) (vec.y - (double) pos.getY());
            float f3 = (float) (vec.z - (double) pos.getZ());
            BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f2, f3));
        } else {
            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, pos, direction, vec, hand);
        }
        BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        BlockUtil.mc.rightClickDelayTimer = 4;
    }

    public static boolean canBreak(BlockPos pos) {
        IBlockState blockState = BlockUtil.mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, BlockUtil.mc.world, pos) != -1.0f;
    }

    public static EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            IBlockState blockState;
            BlockPos neighbour = pos.offset(side);
            if (!BlockUtil.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(BlockUtil.mc.world.getBlockState(neighbour), false) || BlockUtil.mc.world.getBlockState(neighbour).getMaterial().isReplaceable())
                continue;
            return side;
        }
        return null;
    }

    public static void faceVectorPacketInstant(Vec3d vec) {
        float[] rotations = BlockUtil.getNeededRotations2(vec);
        BlockUtil.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], rotations[1], BlockUtil.mc.player.onGround));
    }

    private static float[] getNeededRotations2(Vec3d vec) {
        Vec3d eyesPos = BlockUtil.getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{BlockUtil.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - BlockUtil.mc.player.rotationYaw), BlockUtil.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - BlockUtil.mc.player.rotationPitch)};
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + (double) BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ);
    }

    public static boolean canBeClicked(BlockPos pos) {
        return BlockUtil.getBlock(pos).canCollideCheck(BlockUtil.getState(pos), false);
    }

    public static IBlockState getState(BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos) {
        return BlockUtil.getState(pos).getBlock();
    }

    public static int getDirection4D() {
        return MathHelper.floor((double) (BlockUtil.mc.player.rotationYaw * 4.0f / 360.0f) + 0.5) & 3;
    }

    public static String getDirection4D(boolean northRed) {
        int dirnumber = BlockUtil.getDirection4D();
        if (dirnumber == 0) {
            return "South (+Z)";
        }
        if (dirnumber == 1) {
            return "West (-X)";
        }
        if (dirnumber == 2) {
            return (northRed ? "\u00c2\u951f\u7d5a" : "") + "North (-Z)";
        }
        if (dirnumber == 3) {
            return "East (+X)";
        }
        return "Loading...";
    }

    public static Boolean isPosInFov(BlockPos pos) {
        int dirnumber = BlockUtil.getDirection4D();
        if (dirnumber == 0 && (double) pos.getZ() - BlockUtil.mc.player.getPositionVector().z < 0.0) {
            return false;
        }
        if (dirnumber == 1 && (double) pos.getX() - BlockUtil.mc.player.getPositionVector().x > 0.0) {
            return false;
        }
        if (dirnumber == 2 && (double) pos.getZ() - BlockUtil.mc.player.getPositionVector().z > 0.0) {
            return false;
        }
        return dirnumber != 3 || (double) pos.getX() - BlockUtil.mc.player.getPositionVector().x >= 0.0;
    }

    public static List<BlockPos> possiblePlacePositions(float placeRange) {
        NonNullList positions = NonNullList.create();
        positions.addAll(BlockUtil.getSphere(EntityUtil.getPlayerPos(BlockUtil.mc.player), placeRange, (int) placeRange, false, true, 0).stream().filter(BlockUtil::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    public static List<BlockPos> possiblePlacePositions(float placeRange, boolean specialEntityCheck, boolean endcrystal) {
        NonNullList positions = NonNullList.create();
        positions.addAll(BlockUtil.getSphere(EntityUtil.getPlayerPos(BlockUtil.mc.player), placeRange, (int) placeRange, false, true, 0).stream().filter(pos -> BlockUtil.canPlaceCrystal(pos, specialEntityCheck, endcrystal)).collect(Collectors.toList()));
        return positions;
    }

    public static boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            return (BlockUtil.mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || BlockUtil.mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && BlockUtil.mc.world.getBlockState(boost).getBlock() == Blocks.AIR && BlockUtil.mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck, boolean oneDot15) {
        block8:
        {
            BlockPos boost = blockPos.add(0, 1, 0);
            BlockPos boost2 = blockPos.add(0, 2, 0);
            try {
                if (BlockUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && BlockUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                    return false;
                }
                if (!(BlockUtil.mc.world.getBlockState(boost).getBlock() == Blocks.AIR && BlockUtil.mc.world.getBlockState(boost2).getBlock() == Blocks.AIR || oneDot15)) {
                    return false;
                }
                if (specialEntityCheck) {
                    for (Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                        if (entity instanceof EntityEnderCrystal) continue;
                        return false;
                    }
                    if (!oneDot15) {
                        for (Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                            if (entity instanceof EntityEnderCrystal) continue;
                            return false;
                        }
                    }
                    break block8;
                }
                return BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && (oneDot15 || BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty());
            } catch (Exception ignored) {
                return false;
            }
        }
        return true;
    }

    public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();
        int x = cx - (int) r;
        while ((float) x <= (float) cx + r) {
            int z = cz - (int) r;
            if ((float) z <= (float) cz + r) {
                int y = sphere ? cy - (int) r : cy;
                while (true) {
                    float f = y;
                    float f2 = sphere ? (float) cy + r : (float) (cy + h);
                    if (f < f2) {
                        double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                        if (dist < (double) (r * r) && (!hollow || dist >= (double) ((r - 1.0f) * (r - 1.0f)))) {
                            BlockPos l = new BlockPos(x, y + plus_y, z);
                            circleblocks.add(l);
                        }
                        ++y;
                    }
                    ++z;
                }
            }
            ++x;
        }
        return circleblocks;
    }

    public static List<BlockPos> getSphere(float radius) {
        return BlockUtil.getSphere(BlockUtil.getPosition(), radius);
    }

    public static BlockPos getPosition() {
        return BlockUtil.getPosition(BlockUtil.mc.player);
    }

    public static BlockPos getPosition(Entity entity) {
        return new BlockPos(entity.posX, entity.posY, entity.posZ);
    }

    public static List<BlockPos> getSphere(BlockPos pos, float radius) {
        ArrayList<BlockPos> sphere = new ArrayList<BlockPos>();
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        int x = posX - (int) radius;
        while ((float) x <= (float) posX + radius) {
            int z = posZ - (int) radius;
            while ((float) z <= (float) posZ + radius) {
                int y = posY - (int) radius;
                while ((float) y < (float) posY + radius) {
                    double dist = (posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y);
                    if (dist < (double) (radius * radius)) {
                        BlockPos position = new BlockPos(x, y, z);
                        sphere.add(position);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return sphere;
    }

    public static boolean canSeeBlock(BlockPos p_Pos) {
        return BlockUtil.mc.player != null && BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + (double) BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d(p_Pos.getX(), p_Pos.getY(), p_Pos.getZ()), false, true, false) == null;
    }

    public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand) {
        RayTraceResult result = BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + (double) BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d((double) pos.getX() + 0.5, (double) pos.getY() - 0.5, (double) pos.getZ() + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0f, 0.0f, 0.0f));
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
        return !shouldCheck || BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + (double) BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d(pos.getX(), (float) pos.getY() + height, pos.getZ()), false, true, false) == null;
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck) {
        return BlockUtil.rayTracePlaceCheck(pos, shouldCheck, 1.0f);
    }

    public static void openBlock(BlockPos pos) {
        EnumFacing[] facings;
        for (EnumFacing f : facings = EnumFacing.values()) {
            Block neighborBlock = BlockUtil.mc.world.getBlockState(pos.offset(f)).getBlock();
            if (!emptyBlocks.contains(neighborBlock)) continue;
            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, pos, f.getOpposite(), new Vec3d(pos), EnumHand.MAIN_HAND);
            return;
        }
    }

    public static void swingArm(Setting setting) {
        BlockUtil.mc.player.swingArm(EnumHand.OFF_HAND);
    }


    public static boolean placeBlock(BlockPos pos, int slot, boolean rotate, boolean rotateBack, Setting setting) {
        if (BlockUtil.isBlockEmpty(pos)) {
            EnumFacing[] facings;
            int old_slot = -1;
            if (slot != BlockUtil.mc.player.inventory.currentItem) {
                old_slot = BlockUtil.mc.player.inventory.currentItem;
                BlockUtil.mc.player.inventory.currentItem = slot;
            }
            for (EnumFacing f : facings = EnumFacing.values()) {
                Block neighborBlock = BlockUtil.mc.world.getBlockState(pos.offset(f)).getBlock();
                Vec3d vec = new Vec3d((double) pos.getX() + 0.5 + (double) f.getXOffset() * 0.5, (double) pos.getY() + 0.5 + (double) f.getYOffset() * 0.5, (double) pos.getZ() + 0.5 + (double) f.getZOffset() * 0.5);
                if (emptyBlocks.contains(neighborBlock) || !(BlockUtil.mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(vec) <= 4.25))
                    continue;
                float[] rot = new float[]{BlockUtil.mc.player.rotationYaw, BlockUtil.mc.player.rotationPitch};
                if (rotate) {
                    BlockUtil.rotatePacket(vec.x, vec.y, vec.z);
                }
                if (rightclickableBlocks.contains(neighborBlock)) {
                    BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }
                BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, pos.offset(f), f.getOpposite(), new Vec3d(pos), EnumHand.MAIN_HAND);
                if (rightclickableBlocks.contains(neighborBlock)) {
                    BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }
                if (rotateBack) {
                    BlockUtil.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], BlockUtil.mc.player.onGround));
                }
                BlockUtil.swingArm(setting);
                if (old_slot != -1) {
                    BlockUtil.mc.player.inventory.currentItem = old_slot;
                }
                return true;
            }
        }
        return false;
    }


    public static boolean isBlockEmpty(BlockPos pos) {
        try {
            if (emptyBlocks.contains(BlockUtil.mc.world.getBlockState(pos).getBlock())) {
                Entity e;
                AxisAlignedBB box = new AxisAlignedBB(pos);
                Iterator<Entity> entityIter = BlockUtil.mc.world.loadedEntityList.iterator();
                do {
                    if (entityIter.hasNext()) continue;
                    return true;
                } while (!((e = entityIter.next()) instanceof EntityLivingBase) || !box.intersects(e.getEntityBoundingBox()));
            }
        } catch (Exception exception) {
            // empty catch block
        }
        return false;
    }

    public static boolean canPlaceBlock(BlockPos pos) {
        if (BlockUtil.isBlockEmpty(pos)) {
            EnumFacing[] facings;
            for (EnumFacing f : facings = EnumFacing.values()) {
                if (emptyBlocks.contains(BlockUtil.mc.world.getBlockState(pos.offset(f)).getBlock())) continue;
                Vec3d vec3d = new Vec3d((double) pos.getX() + 0.5 + (double) f.getXOffset() * 0.5, (double) pos.getY() + 0.5 + (double) f.getYOffset() * 0.5, (double) pos.getZ() + 0.5 + (double) f.getZOffset() * 0.5);
                if (!(BlockUtil.mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(vec3d) <= 4.25))
                    continue;
                return true;
            }
        }
        return false;
    }

    public static void rotatePacket(double x, double y, double z) {
        double diffX = x - BlockUtil.mc.player.posX;
        double diffY = y - (BlockUtil.mc.player.posY + (double) BlockUtil.mc.player.getEyeHeight());
        double diffZ = z - BlockUtil.mc.player.posZ;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        BlockUtil.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, BlockUtil.mc.player.onGround));
    }
}

