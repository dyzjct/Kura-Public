package me.windyteam.kura.utils.combat;

import me.windyteam.kura.utils.block.BlockUtil;
import me.windyteam.kura.utils.block.BlockUtil.BlockResistance;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HoleUtil {

    public static final List<BlockPos> holeBlocks = Arrays.asList(
            new BlockPos(0, -1, 0),
            new BlockPos(0, 0, -1),
            new BlockPos(-1, 0, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(0, 0, 1));
    public static Minecraft mc = Minecraft.getMinecraft();

    public static boolean isInHole(EntityPlayer entityPlayer) {
        return isHole(new BlockPos(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ));
    }

    public static boolean isVoidHole(BlockPos blockPos) {
        return mc.player.dimension == -1 ? (blockPos.getY() == 0 || blockPos.getY() == 127) && BlockUtil.getBlockResistance(blockPos) == BlockResistance.Blank : blockPos.getY() == 0 && BlockUtil.getBlockResistance(blockPos) == BlockResistance.Blank;
    }

    public static List<Hole> getHoles(double range) {
        List<Hole> holes = new ArrayList<>();

        for (BlockPos pos : BlockUtil.getNearbyBlocks(mc.player, range, false)) {
            if (isObsidianHole(pos))
                holes.add(new Hole(Hole.Type.Obsidian, Hole.Facing.None, pos));

            if (isBedRockHole(pos))
                holes.add(new Hole(Hole.Type.Bedrock, Hole.Facing.None, pos));
        }

        return holes;
    }

    public static boolean isDoubleBedrockHoleX(BlockPos blockPos) {
        if (!mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(1, 2, 0)).getBlock().equals(Blocks.AIR))
            return false;

        for (BlockPos blockPos2 : new BlockPos[]{blockPos.add(2, 0, 0), blockPos.add(1, 0, 1), blockPos.add(1, 0, -1), blockPos.add(-1, 0, 0), blockPos.add(0, 0, 1), blockPos.add(0, 0, -1), blockPos.add(0, -1, 0), blockPos.add(1, -1, 0)}) {
            IBlockState iBlockState = mc.world.getBlockState(blockPos2);

            if (iBlockState.getBlock() != Blocks.AIR && (iBlockState.getBlock() == Blocks.BEDROCK))
                continue;

            return false;
        }

        return true;
    }

    public static boolean isDoubleBedrockHoleZ(BlockPos blockPos) {
        if (!mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(0, 2, 1)).getBlock().equals(Blocks.AIR))
            return false;

        for (BlockPos blockPos2 : new BlockPos[]{blockPos.add(0, 0, 2), blockPos.add(1, 0, 1), blockPos.add(-1, 0, 1), blockPos.add(0, 0, -1), blockPos.add(1, 0, 0), blockPos.add(-1, 0, 0), blockPos.add(0, -1, 0), blockPos.add(0, -1, 1)}) {
            IBlockState iBlockState = mc.world.getBlockState(blockPos2);

            if (iBlockState.getBlock() != Blocks.AIR && (iBlockState.getBlock() == Blocks.BEDROCK))
                continue;

            return false;
        }

        return true;
    }

    public static boolean isDoubleObsidianHoleX(BlockPos blockPos) {
        if (!mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(1, 2, 0)).getBlock().equals(Blocks.AIR))
            return false;

        for (BlockPos blockPos2 : new BlockPos[]{blockPos.add(2, 0, 0), blockPos.add(1, 0, 1), blockPos.add(1, 0, -1), blockPos.add(-1, 0, 0), blockPos.add(0, 0, 1), blockPos.add(0, 0, -1), blockPos.add(0, -1, 0), blockPos.add(1, -1, 0)}) {
            IBlockState iBlockState = mc.world.getBlockState(blockPos2);

            if (iBlockState.getBlock() != Blocks.AIR && (iBlockState.getBlock() == Blocks.OBSIDIAN))
                continue;

            return false;
        }

        return true;
    }

    public static boolean isDoubleObsidianHoleZ(BlockPos blockPos) {
        if (!mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(0, 2, 1)).getBlock().equals(Blocks.AIR))
            return false;

        for (BlockPos blockPos2 : new BlockPos[]{blockPos.add(0, 0, 2), blockPos.add(1, 0, 1), blockPos.add(-1, 0, 1), blockPos.add(0, 0, -1), blockPos.add(1, 0, 0), blockPos.add(-1, 0, 0), blockPos.add(0, -1, 0), blockPos.add(0, -1, 1)}) {
            IBlockState iBlockState = mc.world.getBlockState(blockPos2);

            if (iBlockState.getBlock() != Blocks.AIR && (iBlockState.getBlock() == Blocks.OBSIDIAN))
                continue;

            return false;
        }

        return true;
    }


    public static boolean isObsidianHole(BlockPos blockPos) {
        return !(BlockUtil.getBlockResistance(blockPos.add(0, 1, 0)) != BlockResistance.Blank || isBedRockHole(blockPos) || BlockUtil.getBlockResistance(blockPos.add(0, 0, 0)) != BlockResistance.Blank || BlockUtil.getBlockResistance(blockPos.add(0, 2, 0)) != BlockResistance.Blank || BlockUtil.getBlockResistance(blockPos.add(0, 0, -1)) != BlockResistance.Resistant && BlockUtil.getBlockResistance(blockPos.add(0, 0, -1)) != BlockResistance.Unbreakable || BlockUtil.getBlockResistance(blockPos.add(1, 0, 0)) != BlockResistance.Resistant && BlockUtil.getBlockResistance(blockPos.add(1, 0, 0)) != BlockResistance.Unbreakable || BlockUtil.getBlockResistance(blockPos.add(-1, 0, 0)) != BlockResistance.Resistant && BlockUtil.getBlockResistance(blockPos.add(-1, 0, 0)) != BlockResistance.Unbreakable || BlockUtil.getBlockResistance(blockPos.add(0, 0, 1)) != BlockResistance.Resistant && BlockUtil.getBlockResistance(blockPos.add(0, 0, 1)) != BlockResistance.Unbreakable || BlockUtil.getBlockResistance(blockPos.add(0.5, 0.5, 0.5)) != BlockResistance.Blank || BlockUtil.getBlockResistance(blockPos.add(0, -1, 0)) != BlockResistance.Resistant && BlockUtil.getBlockResistance(blockPos.add(0, -1, 0)) != BlockResistance.Unbreakable);
    }

    public static boolean isBedRockHole(BlockPos blockPos) {
        return BlockUtil.getBlockResistance(blockPos.add(0, 1, 0)) == BlockResistance.Blank && BlockUtil.getBlockResistance(blockPos.add(0, 0, 0)) == BlockResistance.Blank && BlockUtil.getBlockResistance(blockPos.add(0, 2, 0)) == BlockResistance.Blank && BlockUtil.getBlockResistance(blockPos.add(0, 0, -1)) == BlockResistance.Unbreakable && BlockUtil.getBlockResistance(blockPos.add(1, 0, 0)) == BlockResistance.Unbreakable && BlockUtil.getBlockResistance(blockPos.add(-1, 0, 0)) == BlockResistance.Unbreakable && BlockUtil.getBlockResistance(blockPos.add(0, 0, 1)) == BlockResistance.Unbreakable && BlockUtil.getBlockResistance(blockPos.add(0.5, 0.5, 0.5)) == BlockResistance.Blank && BlockUtil.getBlockResistance(blockPos.add(0, -1, 0)) == BlockResistance.Unbreakable;
    }

    public static boolean isHole(BlockPos blockPos) {
        return BlockUtil.getBlockResistance(blockPos.add(0, 1, 0)) == BlockResistance.Blank && BlockUtil.getBlockResistance(blockPos.add(0, 0, 0)) == BlockResistance.Blank && BlockUtil.getBlockResistance(blockPos.add(0, 2, 0)) == BlockResistance.Blank && (BlockUtil.getBlockResistance(blockPos.add(0, 0, -1)) == BlockResistance.Resistant || BlockUtil.getBlockResistance(blockPos.add(0, 0, -1)) == BlockResistance.Unbreakable) && ((BlockUtil.getBlockResistance(blockPos.add(1, 0, 0)) == BlockResistance.Resistant || (BlockUtil.getBlockResistance(blockPos.add(1, 0, 0)) == BlockResistance.Unbreakable)) && ((BlockUtil.getBlockResistance(blockPos.add(-1, 0, 0)) == BlockResistance.Resistant) || (BlockUtil.getBlockResistance(blockPos.add(-1, 0, 0)) == BlockResistance.Unbreakable)) && ((BlockUtil.getBlockResistance(blockPos.add(0, 0, 1)) == BlockResistance.Resistant) || (BlockUtil.getBlockResistance(blockPos.add(0, 0, 1)) == BlockResistance.Unbreakable)) && (BlockUtil.getBlockResistance(blockPos.add(0.5, 0.5, 0.5)) == BlockResistance.Blank) && ((BlockUtil.getBlockResistance(blockPos.add(0, -1, 0)) == BlockResistance.Resistant) || (BlockUtil.getBlockResistance(blockPos.add(0, -1, 0)) == BlockResistance.Unbreakable)));
    }

    public static boolean is2HoleB(BlockPos pos) {
        return is2Hole(pos) != null;
    }

    public static BlockPos is2Hole(BlockPos pos) {
        if (isHole(pos)) {
            return null;
        }
        BlockPos blockpos2 = null;
        int size = 0;
        int size2 = 0;

        if (mc.world.getBlockState(pos).getBlock() != Blocks.AIR) {
            return null;
        }
        for (BlockPos bPos : holeBlocks) {
            if (mc.world.getBlockState(pos.add(bPos)).getBlock() == Blocks.AIR && !pos.add(bPos).equals(new BlockPos(bPos.getX(), bPos.getY() - 1, bPos.getZ()))) {
                blockpos2 = pos.add(bPos);
                size++;
            }
        }
        if (size == 1) {

            for (BlockPos bPoss : holeBlocks) {
                if (mc.world.getBlockState(pos.add(bPoss)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.add(bPoss)).getBlock() == Blocks.OBSIDIAN) {
                    size2++;
                }
            }
            for (BlockPos bPoss : holeBlocks) {
                if (mc.world.getBlockState(blockpos2.add(bPoss)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos2.add(bPoss)).getBlock() == Blocks.OBSIDIAN) {
                    size2++;
                }
            }
        }

        if (size2 == 8) {
            return blockpos2;
        }
        return null;
    }

    public static BlockPos getLocalPlayerPosFloored() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static boolean isPlayerInHole() {
        BlockPos blockPos = getLocalPlayerPosFloored();
        IBlockState blockState = mc.world.getBlockState(blockPos);
        if (blockState.getBlock() != Blocks.AIR)
            return false;
        if (mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR)
            return false;
        if (mc.world.getBlockState(blockPos.down()).getBlock() == Blocks.AIR)
            return false;
        final BlockPos[] touchingBlocks = new BlockPos[]
                {blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west()};
        int validHorizontalBlocks = 0;
        for (BlockPos touching : touchingBlocks) {
            final IBlockState touchingState = mc.world.getBlockState(touching);
            if ((touchingState.getBlock() != Blocks.AIR) && touchingState.isFullBlock())
                validHorizontalBlocks++;
        }
        return validHorizontalBlocks >= 4;
    }

    public static BlockSafety isBlockSafe(Block block) {
        if (block == Blocks.BEDROCK) {
            return BlockSafety.UNBREAKABLE;
        }
        if (block == Blocks.OBSIDIAN || block == Blocks.ENDER_CHEST || block == Blocks.ANVIL) {
            return BlockSafety.RESISTANT;
        }
        return BlockSafety.BREAKABLE;
    }

    public static HoleInfo isHole(BlockPos centreBlock, boolean onlyOneWide, boolean ignoreDown) {
        HoleInfo output = new HoleInfo();
        HashMap<BlockOffset, BlockSafety> unsafeSides = HoleUtil.getUnsafeSides(centreBlock);

        if (unsafeSides.containsKey(BlockOffset.DOWN)) {
            if (unsafeSides.remove(BlockOffset.DOWN, BlockSafety.BREAKABLE)) {
                if (!ignoreDown) {
                    output.setSafety(BlockSafety.BREAKABLE);
                    return output;
                }
            }
        }

        int size = unsafeSides.size();

        unsafeSides.entrySet().removeIf(entry -> entry.getValue() == BlockSafety.RESISTANT);

        // size has changed so must have weak side
        if (unsafeSides.size() != size) {
            output.setSafety(BlockSafety.RESISTANT);
        }

        size = unsafeSides.size();

        // is it a perfect hole
        if (size == 0) {
            output.setType(HoleType.SINGLE);
            output.setCentre(new AxisAlignedBB(centreBlock));
            return output;
        }
        // have one open side
        else if (size == 1 && !onlyOneWide) {
            return isDoubleHole(output, centreBlock, unsafeSides.keySet().stream().findFirst().get());
        } else {
            output.setSafety(BlockSafety.BREAKABLE);
            return output;
        }
    }

    private static HoleInfo isDoubleHole(HoleInfo info, BlockPos centreBlock, BlockOffset weakSide) {
        BlockPos unsafePos = weakSide.offset(centreBlock);

        HashMap<BlockOffset, BlockSafety> unsafeSides = HoleUtil.getUnsafeSides(unsafePos);

        int size = unsafeSides.size();

        unsafeSides.entrySet().removeIf(entry -> entry.getValue() == BlockSafety.RESISTANT);

        // size has changed so must have weak side
        if (unsafeSides.size() != size) {
            info.setSafety(BlockSafety.RESISTANT);
        }

        if (unsafeSides.containsKey(BlockOffset.DOWN)) {
            info.setType(HoleType.CUSTOM);
            unsafeSides.remove(BlockOffset.DOWN);
        }

        // is it a safe hole
        if (unsafeSides.size() > 1) {
            info.setType(HoleType.NONE);
            return info;
        }

        // it is
        double minX = Math.min(centreBlock.getX(), unsafePos.getX());
        double maxX = Math.max(centreBlock.getX(), unsafePos.getX()) + 1;
        double minZ = Math.min(centreBlock.getZ(), unsafePos.getZ());
        double maxZ = Math.max(centreBlock.getZ(), unsafePos.getZ()) + 1;

        AxisAlignedBB newBB = new AxisAlignedBB(minX, centreBlock.getY(), minZ, maxX, centreBlock.getY() + 1, maxZ);
        info.setCentre(newBB);
        AxisAlignedBB centerBB = new AxisAlignedBB(newBB.minX, newBB.minY, newBB.minZ, newBB.maxX - 1, newBB.maxY, newBB.maxZ - 1);
        info.setCenterPos(new BlockPos(centerBB.getCenter()));
        if (info.getType() != HoleType.CUSTOM) {
            info.setType(HoleType.DOUBLE);
        }
        return info;
    }

    public static HashMap<BlockOffset, BlockSafety> getUnsafeSides(BlockPos pos) {
        HashMap<BlockOffset, BlockSafety> output = new HashMap<>();
        BlockSafety temp;

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.DOWN.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.DOWN, temp);

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.NORTH.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.NORTH, temp);

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.SOUTH.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.SOUTH, temp);

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.EAST.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.EAST, temp);

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.WEST.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.WEST, temp);

        return output;
    }

    public enum BlockSafety {
        UNBREAKABLE,
        RESISTANT,
        BREAKABLE
    }

    public enum HoleType {
        SINGLE,
        DOUBLE,
        CUSTOM,
        NONE
    }

    public enum BlockOffset {
        DOWN(0, -1, 0),
        UP(0, 1, 0),
        NORTH(0, 0, -1),
        EAST(1, 0, 0),
        SOUTH(0, 0, 1),
        WEST(-1, 0, 0);

        private final int x;
        private final int y;
        private final int z;

        BlockOffset(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public BlockPos offset(BlockPos pos) {
            return pos.add(x, y, z);
        }

        public BlockPos forward(BlockPos pos, int scale) {
            return pos.add(x * scale, 0, z * scale);
        }

        public BlockPos backward(BlockPos pos, int scale) {
            return pos.add(-x * scale, 0, -z * scale);
        }

        public BlockPos left(BlockPos pos, int scale) {
            return pos.add(z * scale, 0, -x * scale);
        }

        public BlockPos right(BlockPos pos, int scale) {
            return pos.add(-z * scale, 0, x * scale);
        }
    }

    public static class Hole {
        public Type type;
        public Facing facing;
        public BlockPos hole;
        public BlockPos offset;

        public Hole(Type type, Facing facing, BlockPos hole, BlockPos offset) {
            this.type = type;
            this.facing = facing;
            this.hole = hole;
            this.offset = offset;
        }

        public Hole(Type type, Facing facing, BlockPos hole) {
            this.type = type;
            this.facing = facing;
            this.hole = hole;
        }

        public Facing opposite() {
            if (this.facing == Facing.West)
                return Facing.East;
            else if (this.facing == Facing.East)
                return Facing.West;
            else if (this.facing == Facing.North)
                return Facing.South;
            else if (this.facing == Facing.South)
                return Facing.North;

            return Facing.None;
        }

        public enum Facing {
            West,
            South,
            North,
            East,
            None
        }

        public enum Type {
            Obsidian,
            Bedrock,
            Double
        }
    }

    public static class HoleInfo {
        private HoleType type;
        private BlockSafety safety;
        private BlockPos centerPos;

        private AxisAlignedBB centre;

        public HoleInfo() {
            this(BlockSafety.UNBREAKABLE, HoleType.NONE);
        }

        public HoleInfo(BlockSafety safety, HoleType type) {
            this.type = type;
            this.safety = safety;
        }

        public HoleType getType() {
            return type;
        }

        public void setType(HoleType type) {
            this.type = type;
        }

        public BlockSafety getSafety() {
            return safety;
        }

        public void setSafety(BlockSafety safety) {
            this.safety = safety;
        }

        public AxisAlignedBB getCentre() {
            return centre;
        }

        public void setCentre(AxisAlignedBB centre) {
            this.centre = centre;
        }

        public BlockPos getCenterPos() {
            return centerPos;
        }

        public void setCenterPos(BlockPos pos) {
            this.centerPos = pos;
        }
    }

}