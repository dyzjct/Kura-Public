package me.dyzjct.kura.utils.entity;

import me.dyzjct.kura.event.events.entity.MotionUpdateEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class PlayerUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static BlockPos GetLocalPlayerPosFloored() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static boolean IsEating() {
        return mc.player != null && (mc.player.getHeldItemMainhand().getItem() instanceof ItemFood || mc.player.getHeldItemOffhand().getItem() instanceof ItemFood) && mc.player.isHandActive();
    }

    public static boolean isInHole(Entity e) {
        BlockPos pos = new BlockPos(Math.floor(e.getPositionVector().x), Math.floor(e.getPositionVector().y + 0.2D), Math.floor(e.getPositionVector().z));
        return mc.world.getBlockState(pos.down()).getBlock().blockResistance >= 1200.0F && mc.world.getBlockState(pos.east()).getBlock().blockResistance >= 1200.0F && mc.world.getBlockState(pos.west()).getBlock().blockResistance >= 1200.0F && mc.world.getBlockState(pos.north()).getBlock().blockResistance >= 1200.0F && mc.world.getBlockState(pos.south()).getBlock().blockResistance >= 1200.0F;
    }

    public static boolean isAirUnder(Entity ent) {
        return mc.world.getBlockState(new BlockPos(ent.posX, ent.posY - 1, ent.posZ)).getBlock() == Blocks.AIR;
    }

    public static int findObiInHotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (block instanceof BlockEnderChest)
                    return i;
                else if (block instanceof BlockObsidian)
                    return i;
            }
        }
        return -1;
    }

    public static void sendMovementPackets(MotionUpdateEvent.Tick event) {
        final boolean flag = mc.player.isSprinting();
        if (flag != mc.player.serverSprintState) {
            if (flag) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
            } else {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            }
            mc.player.serverSprintState = flag;
        }
        final boolean flag2 = mc.player.isSneaking();
        if (flag2 != mc.player.serverSneakState) {
            if (flag2) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            } else {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }
            mc.player.serverSneakState = flag2;
        }
        if (mc.getRenderViewEntity() == mc.player) {
            final double d0 = mc.player.posX - mc.player.lastReportedPosX;
            final double d2 = event.getY() - mc.player.lastReportedPosY;
            final double d3 = mc.player.posZ - mc.player.lastReportedPosZ;
            final double d4 = event.getYaw() - mc.player.lastReportedYaw;
            final double d5 = event.getPitch() - mc.player.lastReportedPitch;
            final EntityPlayerSP player = mc.player;
            ++player.positionUpdateTicks;
            boolean flag3 = d0 * d0 + d2 * d2 + d3 * d3 > 9.0E-4 || mc.player.positionUpdateTicks >= 20;
            final boolean flag4 = d4 != 0.0 || d5 != 0.0;
            if (mc.player.isRiding()) {
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.motionX, -999.0, mc.player.motionZ, event.getYaw(), event.getPitch(), mc.player.onGround));
                flag3 = false;
            } else if (flag3 && flag4) {
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, event.getY(), mc.player.posZ, event.getYaw(), event.getPitch(), mc.player.onGround));
            } else if (flag3) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, event.getY(), mc.player.posZ, mc.player.onGround));
            } else if (flag4) {
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(event.getYaw(), event.getPitch(), mc.player.onGround));
            } else if (mc.player.prevOnGround != mc.player.onGround) {
                mc.player.connection.sendPacket(new CPacketPlayer(mc.player.onGround));
            }
            if (flag3) {
                mc.player.lastReportedPosX = mc.player.posX;
                mc.player.lastReportedPosY = event.getY();
                mc.player.lastReportedPosZ = mc.player.posZ;
                mc.player.positionUpdateTicks = 0;
            }
            if (flag4) {
                mc.player.lastReportedYaw = event.getYaw();
                mc.player.lastReportedPitch = event.getPitch();
            }
            mc.player.prevOnGround = mc.player.onGround;
            mc.player.autoJumpEnabled = mc.player.mc.gameSettings.autoJump;
        }
    }

    public static FacingDirection GetFacing() {
        switch (MathHelper.floor((double) (mc.player.rotationYaw * 8.0F / 360.0F) + 0.5D) & 7) {
            case 0:
            case 1:
                return FacingDirection.South;
            case 2:
            case 3:
                return FacingDirection.West;
            case 4:
            case 5:
                return FacingDirection.North;
            case 6:
            case 7:
                return FacingDirection.East;
        }
        return FacingDirection.North;
    }

    public static boolean IsPlayerInHole() {
        final BlockPos blockPos = GetLocalPlayerPosFloored();
        final IBlockState blockState = PlayerUtil.mc.world.getBlockState(blockPos);
        if (blockState.getBlock() != Blocks.AIR) {
            return false;
        }
        if (PlayerUtil.mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR) {
            return false;
        }
        if (PlayerUtil.mc.world.getBlockState(blockPos.down()).getBlock() == Blocks.AIR) {
            return false;
        }
        final BlockPos[] touchingBlocks = {blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west()};
        int validHorizontalBlocks = 0;
        for (final BlockPos touching : touchingBlocks) {
            final IBlockState touchingState = PlayerUtil.mc.world.getBlockState(touching);
            if (touchingState.getBlock() != Blocks.AIR && touchingState.isFullBlock()) {
                ++validHorizontalBlocks;
            }
        }
        return validHorizontalBlocks >= 4;
    }

    public static void damageHypixel() {
        if (mc.getConnection() == null) return;

        if (mc.player.onGround) {
            final double x = mc.player.posX;
            final double y = mc.player.posY;
            final double z = mc.player.posZ;
            for (int i = 0; i < 9; i++) {
                mc.getConnection().sendPacket(new CPacketPlayer.Position(x, y + 0.4122222218322211111111F, z, false));
                mc.getConnection().sendPacket(new CPacketPlayer.Position(x, y + 0.000002737272, z, false));
                mc.getConnection().sendPacket(new CPacketPlayer(false));
            }
            mc.getConnection().sendPacket(new CPacketPlayer(true));
        }
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public enum FacingDirection {
        North,
        South,
        East,
        West,
    }

}