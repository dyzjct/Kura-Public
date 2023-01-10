package me.windyteam.kura.utils.other;

import me.windyteam.kura.utils.combat.CombatUtil;
import me.windyteam.kura.utils.combat.CombatUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HoleUtil {
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

        // Don't ask me why they work but they do

        public BlockPos left(BlockPos pos, int scale) {
            return pos.add(z * scale, 0, -x * scale);
        }

        public BlockPos right(BlockPos pos, int scale) {
            return pos.add(-z * scale, 0, x * scale);
        }
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
    public static final List<BlockPos> holeBlocks = Arrays.asList(new BlockPos(0, -1, 0), new BlockPos(0, 0, -1), new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1));
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

    private static Minecraft mc = Minecraft.getMinecraft();

    public static final Vec3d[] cityOffsets = {
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, 1),
            new Vec3d(-1, 0, 0),
            new Vec3d(0, 0, -1),
    };


    public static boolean isInHole() {
        Vec3d playerPos = CombatUtil.interpolateEntity(mc.player);
        BlockPos blockpos = new BlockPos(playerPos.x, playerPos.y, playerPos.z);
        int size = 0;
        for(BlockPos bPos : holeBlocks) {
            if(CombatUtil.isHard(mc.world.getBlockState(blockpos.add(bPos)).getBlock())) {
                size++;
            }
        }
        return (size == 5);
    }
    public static BlockPos is2Hole(BlockPos pos) {
        if(isHole(pos)){
            return null;
        }
        BlockPos blockpos =pos;
        BlockPos blockpos2 =null;
        int size = 0;
        int size2 = 0;

        if(mc.world.getBlockState(pos).getBlock()!=Blocks.AIR){
            return null;
        }
        for(BlockPos bPos : holeBlocks) {
            if(mc.world.getBlockState(blockpos.add(bPos)).getBlock()==Blocks.AIR&&blockpos.add(bPos)!=new BlockPos(bPos.getX(),bPos.getY()-1,bPos.getZ())) {
                blockpos2 = blockpos.add(bPos);
                size++;
            }
        }
        if(size==1){

            for(BlockPos bPoss : holeBlocks) {
                if(mc.world.getBlockState(blockpos.add(bPoss)).getBlock()== Blocks.BEDROCK||mc.world.getBlockState(blockpos.add(bPoss)).getBlock()== Blocks.OBSIDIAN) {
                    size2++;
                }
            }
            for(BlockPos bPoss : holeBlocks) {
                if(mc.world.getBlockState(blockpos2.add(bPoss)).getBlock()== Blocks.BEDROCK||mc.world.getBlockState(blockpos2.add(bPoss)).getBlock()== Blocks.OBSIDIAN) {
                    size2++;
                }
            }
        }




        if (size2==8){
            return blockpos2;
        }
        return null;
    }

    public static boolean is2securityHole(BlockPos pos) {
        if(is2Hole(pos)==null){
            return false;
        }
        BlockPos blockpos =pos;
        BlockPos blockpos2 =is2Hole(pos);
        int size = 0;
        for(BlockPos bPoss : holeBlocks) {
            if(mc.world.getBlockState(blockpos.add(bPoss)).getBlock()== Blocks.BEDROCK) {
                size++;
            }
        }
        for(BlockPos bPoss : holeBlocks) {
            if(mc.world.getBlockState(blockpos2.add(bPoss)).getBlock()== Blocks.BEDROCK) {
                size++;
            }
        }
        return (size==8);
    }


    public static boolean isHole(BlockPos pos) {
        BlockPos blockpos =pos;
        int size = 0;
        for(BlockPos bPos : holeBlocks) {
            if(CombatUtil.isHard(mc.world.getBlockState(blockpos.add(bPos)).getBlock())) {
                size++;
            }
        }
        return (size == 5);
    }
}
