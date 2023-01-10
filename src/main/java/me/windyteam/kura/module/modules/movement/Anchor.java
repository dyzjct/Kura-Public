package me.windyteam.kura.module.modules.movement;

import me.windyteam.kura.event.events.entity.MoveEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.utils.Hole;
import me.windyteam.kura.event.events.entity.MoveEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.utils.Hole;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "Anchor", category = Category.MOVEMENT)
public class Anchor extends Module {
    public BooleanSetting toggleStrafe = bsetting("ToggleStrafe", false);
    public BooleanSetting disable = bsetting("AutoToggle", false);
    BlockPos pos;
    int i;
    BlockPos newPos;
    IBlockState state;
    Vec3d center;
    double xDiff;
    double zDiff;
    double x;
    double z;
    EntityPlayerSP player;
    Hole.HoleTypes type;
    BlockPos playerPos;

    public static Vec3d GetCenter(final double posX, final double posY, final double posZ) {
        final double x = Math.floor(posX) + 0.5;
        final double y = Math.floor(posY);
        final double z = Math.floor(posZ) + 0.5;
        return new Vec3d(x, y, z);
    }

    public static Hole.HoleTypes isBlockValid(final IBlockState blockState, final BlockPos blockPos) {
        if (blockState.getBlock() != Blocks.AIR) {
            return Hole.HoleTypes.None;
        }
        if (mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR) {
            return Hole.HoleTypes.None;
        }
        if (mc.world.getBlockState(blockPos.up(2)).getBlock() != Blocks.AIR) {
            return Hole.HoleTypes.None;
        }
        if (mc.world.getBlockState(blockPos.down()).getBlock() == Blocks.AIR) {
            return Hole.HoleTypes.None;
        }
        final BlockPos[] touchingBlocks = {blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west()};
        boolean l_Bedrock = true;
        boolean l_Obsidian = true;
        int validHorizontalBlocks = 0;
        for (final BlockPos touching : touchingBlocks) {
            final IBlockState touchingState = mc.world.getBlockState(touching);
            if (touchingState.getBlock() != Blocks.AIR && touchingState.isFullBlock()) {
                ++validHorizontalBlocks;
                if (touchingState.getBlock() != Blocks.BEDROCK && l_Bedrock) {
                    l_Bedrock = false;
                }
                if (!l_Bedrock && touchingState.getBlock() != Blocks.OBSIDIAN && touchingState.getBlock() != Blocks.BEDROCK) {
                    l_Obsidian = false;
                }
            }
        }
        if (validHorizontalBlocks < 4) {
            return Hole.HoleTypes.None;
        }
        if (l_Bedrock) {
            return Hole.HoleTypes.Bedrock;
        }
        if (l_Obsidian) {
            return Hole.HoleTypes.Obsidian;
        }
        return Hole.HoleTypes.Normal;
    }

    @SubscribeEvent
    public void onUpdate(MoveEvent event) {
        if (fullNullCheck()) {
            return;
        }

        if (mc.player.posY < 0) {
            return;
        }

        double newX;
        double newZ;

        //specifies the x and z coordinates to be centered- should prevent people from getting stuck up on side blocks
        if (mc.player.posX > Math.round(mc.player.posX)) {
            newX = Math.round(mc.player.posX) + 0.5;
        } else if (mc.player.posX < Math.round(mc.player.posX)) {
            newX = Math.round(mc.player.posX) - 0.5;
        } else {
            newX = mc.player.posX;
        }

        if (mc.player.posZ > Math.round(mc.player.posZ)) {
            newZ = Math.round(mc.player.posZ) + 0.5;
        } else if (mc.player.posZ < Math.round(mc.player.posZ)) {
            newZ = Math.round(mc.player.posZ) - 0.5;
        } else {
            newZ = mc.player.posZ;
        }

        playerPos = new BlockPos(newX, mc.player.posY, newZ);

        if (mc.world.getBlockState(playerPos).getBlock() != Blocks.AIR) {
            return;
        }

        //looks to see if the block below the player is "surrounded"
        if (mc.world.getBlockState(playerPos.down()).getBlock() == Blocks.AIR //1 block
                && mc.world.getBlockState(playerPos.down().east()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down().west()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down().north()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down().south()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down(2)).getBlock() != Blocks.AIR) {

            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        } else if (mc.world.getBlockState(playerPos.down()).getBlock() == Blocks.AIR //2 block
                && mc.world.getBlockState(playerPos.down(2)).getBlock() == Blocks.AIR
                && mc.world.getBlockState(playerPos.down(2).east()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down(2).west()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down(2).north()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down(2).south()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down(3)).getBlock() != Blocks.AIR) {

            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
    }
}
