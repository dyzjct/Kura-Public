package me.dyzjct.kura.module.modules.misc;

import me.dyzjct.kura.event.events.block.BlockEvent;
import me.dyzjct.kura.event.events.entity.MotionUpdateEvent;
import me.dyzjct.kura.manager.RotationManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.Timer;
import me.dyzjct.kura.utils.block.BlockInteractionHelper;
import me.dyzjct.kura.utils.block.BlockUtil;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import me.dyzjct.kura.utils.math.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Module.Info(name = "Nuker", category = Category.MISC)
public class Nuker extends Module {
    private final Timer timer = new Timer();
    private final Setting<Boolean> autoSwitch = bsetting("AutoSwitch", true);
    public Setting<Boolean> rotate = bsetting("Rotate", true);
    public Setting<Float> distance = fsetting("Range", 6, 0.1f, 10);
    public Setting<Integer> blockPerTick = isetting("BlocksPerTick", 50, 1, 100);
    public Setting<Integer> delay = isetting("Delay", 50, 1, 100);
    public Setting<Mode> mode = msetting("Mode", Mode.NUKE);
    public Setting<Boolean> antiRegear = bsetting("AntiRegear", false);
    public Setting<Boolean> hopperNuker = bsetting("HopperNuker", false);
    public BooleanSetting bedFuker = bsetting("BedFucker", false);
    public int picslot = -1;
    private int oldSlot = -1;
    private Block selected;

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        this.selected = null;
        picslot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE);
        oldSlot = mc.player.inventory.currentItem;
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        RotationManager.resetRotation();
    }

    @SubscribeEvent
    public void onClickBlock(BlockEvent event) {
        if (fullNullCheck()) {
            return;
        }
        Block block;
        if (event.getStage() == 3 && (this.mode.getValue() == Mode.SELECTION || this.mode.getValue() == Mode.NUKE) && (block = mc.world.getBlockState(event.pos).getBlock()) != null && block != this.selected) {
            this.selected = block;
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onUpdate(MotionUpdateEvent.Tick.Tick event) {
        if (fullNullCheck()) {
            return;
        }
        picslot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE);
        oldSlot = mc.player.inventory.currentItem;
        int i = 0;
        BlockPos pos = null;
        switch (this.mode.getValue()) {
            case SELECTION:
            case NUKE: {
                pos = this.getClosestBlockSelection();
                break;
            }
            case ALL: {
                pos = this.getClosestBlockAll();
                break;
            }
        }
        if (pos != null) {
            if (this.mode.getValue() == Mode.SELECTION || this.mode.getValue() == Mode.ALL) {
                if (this.rotate.getValue()) {
                    mc.player.rotationYawHead = BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[0];
                    mc.player.renderYawOffset = BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[0];
                    event.setYaw(BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[0]);
                    event.setPitch(BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[1]);
                }
                if (this.canBreak(pos)) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK,
                            pos, mc.player.getHorizontalFacing()));
                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                }
            } else {
                while (i < blockPerTick.getValue()) {
                    pos = this.getClosestBlockSelection();
                    if (pos == null) continue;
                    if (this.rotate.getValue()) {
                        mc.player.rotationYawHead = BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[0];
                        mc.player.renderYawOffset = BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[0];
                        event.setYaw(BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[0]);
                        event.setPitch(BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[1]);
                    }
                    if (!this.timer.passedMs(this.delay.getValue())) continue;
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK,
                            pos, mc.player.getHorizontalFacing()));
                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                    this.timer.reset();
                    ++i;
                }
            }
        }
        if (this.antiRegear.getValue()) {
            this.breakBlocks(event, BlockUtil.shulkerList);
        }
        if (this.hopperNuker.getValue()) {
            ArrayList<Block> blocklist = new ArrayList<>();
            blocklist.add(Blocks.HOPPER);
            this.breakBlocks(event, blocklist);
        }
        if (bedFuker.getValue()) {
            ArrayList<Block> blocklist = new ArrayList<>();
            blocklist.add(Blocks.BED);
            this.breakBlocks(event, blocklist);
        }
    }

    public void breakBlocks(MotionUpdateEvent.Tick event, List<Block> blocks) {
        picslot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE);
        oldSlot = mc.player.inventory.currentItem;
        BlockPos pos = this.getNearestBlock(blocks);
        if (pos != null) {
            if (this.rotate.getValue()) {
                mc.player.rotationYawHead = BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[0];
                mc.player.renderYawOffset = BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[0];
                event.setYaw(BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[0]);
                event.setPitch(BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[1]);
            }
            if (this.canBreak(pos)) {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK,
                        pos, mc.player.getHorizontalFacing()));
                if (this.autoSwitch.getValue()) {
                    InventoryUtil.switchToHotbarSlot(picslot, false);
                }
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                        pos, mc.player.getHorizontalFacing()));
                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                if (this.autoSwitch.getValue()) {
                    InventoryUtil.switchToHotbarSlot(oldSlot, false);
                }
            }
        }
    }

    @SuppressWarnings("all")
    public boolean canBreak(BlockPos pos) {
        IBlockState blockState = mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, mc.world, pos) != -1.0f;
    }

    private BlockPos getNearestBlock(List<Block> blocks) {
        double maxDist = MathUtil.square(this.distance.getValue());
        BlockPos ret = null;
        for (double x = maxDist; x >= -maxDist; x -= 1.0) {
            for (double y = maxDist; y >= -maxDist; y -= 1.0) {
                for (double z = maxDist; z >= -maxDist; z -= 1.0) {
                    BlockPos pos = new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
                    double dist = mc.player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ());
                    if (!(dist <= maxDist) || !blocks.contains(mc.world.getBlockState(pos).getBlock()) || !this.canBreak(pos))
                        continue;
                    maxDist = dist;
                    ret = pos;
                }
            }
        }
        return ret;
    }

    private BlockPos getClosestBlockAll() {
        float maxDist = this.distance.getValue();
        BlockPos ret = null;
        for (float x = maxDist; x >= -maxDist; x -= 1.0f) {
            for (float y = maxDist; y >= -maxDist; y -= 1.0f) {
                for (float z = maxDist; z >= -maxDist; z -= 1.0f) {
                    BlockPos pos = new BlockPos(mc.player.posX + (double) x, mc.player.posY + (double) y, mc.player.posZ + (double) z);
                    double dist = mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ());
                    if (!(dist <= (double) maxDist) || mc.world.getBlockState(pos).getBlock() == Blocks.AIR || mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid || !this.canBreak(pos) || !((double) pos.getY() >= mc.player.posY))
                        continue;
                    maxDist = (float) dist;
                    ret = pos;
                }
            }
        }
        return ret;
    }

    private BlockPos getClosestBlockSelection() {
        float maxDist = this.distance.getValue();
        BlockPos ret = null;
        for (float x = maxDist; x >= -maxDist; x -= 1.0f) {
            for (float y = maxDist; y >= -maxDist; y -= 1.0f) {
                for (float z = maxDist; z >= -maxDist; z -= 1.0f) {
                    BlockPos pos = new BlockPos(mc.player.posX + (double) x, mc.player.posY + (double) y, mc.player.posZ + (double) z);
                    double dist = mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ());
                    if (!(dist <= (double) maxDist) || mc.world.getBlockState(pos).getBlock() == Blocks.AIR || mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid || mc.world.getBlockState(pos).getBlock() != this.selected || !this.canBreak(pos) || !((double) pos.getY() >= mc.player.posY))
                        continue;
                    maxDist = (float) dist;
                    ret = pos;
                }
            }
        }
        return ret;
    }


    public enum Mode {
        SELECTION,
        ALL,
        NUKE

    }
}

