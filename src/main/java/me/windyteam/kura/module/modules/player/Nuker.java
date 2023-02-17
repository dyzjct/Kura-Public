package me.windyteam.kura.module.modules.player;

import me.windyteam.kura.event.events.block.BlockEvent;
import me.windyteam.kura.event.events.entity.MotionUpdateEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.setting.IntegerSetting;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.TimerUtils;
import me.windyteam.kura.utils.block.BlockInteractionHelper;
import me.windyteam.kura.utils.block.BlockUtil;
import me.windyteam.kura.utils.entity.EntityUtil;
import me.windyteam.kura.utils.inventory.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Module.Info(name = "Nuker", category = Category.MISC)
public class Nuker extends Module {
    private final TimerUtils timerUtils;
    private final Setting<Boolean> autoSwitch;
    public Setting<Boolean> rotate;
    public IntegerSetting distance;
    public Setting<Integer> blockPerTick;
    public Setting<Integer> delay;
    public Setting<Mode> mode;
    public Setting<Boolean> antiRegear;
    public Setting<Boolean> hopperNuker;
    public BooleanSetting bedFuker;
    public int picslot;
    private int oldSlot;
    private Block selected;

    public Nuker() {
        this.timerUtils = new TimerUtils();
        this.autoSwitch = this.bsetting("AutoSwitch", true);
        this.rotate = this.bsetting("Rotate", true);
        this.distance = this.isetting("Range", 6, 1, 6);
        this.blockPerTick = this.isetting("BlocksPerTick", 50, 1, 100);
        this.delay = this.isetting("Delay", 50, 1, 100);
        this.mode = (Setting<Mode>) this.msetting("Mode", Mode.NUKE);
        this.antiRegear = this.bsetting("AntiRegear", false);
        this.hopperNuker = this.bsetting("HopperNuker", false);
        this.bedFuker = this.bsetting("BedFucker", false);
        this.picslot = -1;
        this.oldSlot = -1;
    }

    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        this.selected = null;
        this.picslot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE);
        this.oldSlot = Nuker.mc.player.inventory.currentItem;
    }

    @SubscribeEvent
    public void onClickBlock(final BlockEvent event) {
        if (fullNullCheck()) {
            return;
        }
        final Block block;
        if ((this.mode.getValue() == Mode.SELECTION || this.mode.getValue() == Mode.NUKE) && (block = Nuker.mc.world.getBlockState(event.getPos()).getBlock()) != null && block != this.selected) {
            this.selected = block;
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onUpdate(final MotionUpdateEvent.Tick event) {
        if (fullNullCheck()) {
            return;
        }
        this.picslot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE);
        this.oldSlot = Nuker.mc.player.inventory.currentItem;
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
                    event.setRotation(BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[0], BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[1]);
                }
                if (this.canBreak(pos)) {
                    Nuker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, BlockUtil.getFacing(pos)));
                    Nuker.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                }
            } else {
                while (i < this.blockPerTick.getValue()) {
                    pos = this.getClosestBlockSelection();
                    if (pos == null) {
                        continue;
                    }
                    if (this.rotate.getValue()) {
                        event.setRotation(BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[0], BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[1]);
                    }
                    if (!this.timerUtils.passedMs(this.delay.getValue())) {
                        continue;
                    }
                    Nuker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, BlockUtil.getFacing(pos)));
                    Nuker.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                    this.timerUtils.reset();
                    ++i;
                }
            }
        }
        if (this.antiRegear.getValue()) {
            this.breakBlocks(event, BlockUtil.shulkerList);
        }
        if (this.hopperNuker.getValue()) {
            final ArrayList<Block> blocklist = new ArrayList<Block>();
            blocklist.add(Blocks.HOPPER);
            this.breakBlocks(event, blocklist);
        }
        if (this.bedFuker.getValue()) {
            final ArrayList<Block> blocklist = new ArrayList<Block>();
            blocklist.add(Blocks.BED);
            this.breakBlocks(event, blocklist);
        }
    }

    public void breakBlocks(final MotionUpdateEvent event, final List<Block> blocks) {
        this.picslot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE);
        this.oldSlot = Nuker.mc.player.inventory.currentItem;
        final BlockPos pos = BlockInteractionHelper.getSphere(EntityUtil.getPlayerPos(Nuker.mc.player), this.distance.getValue(), this.distance.getValue(), false, true, 0).stream().filter(e -> !blocks.contains(Nuker.mc.world.getBlockState(e).getBlock())).min(Comparator.comparing(e -> Nuker.mc.player.getDistanceSq(e))).orElse(null);
        if (pos != null) {
            if (Nuker.mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ()) > this.distance.getValue()) {
                return;
            }
            if (this.rotate.getValue()) {
                event.setRotation(BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[0], BlockInteractionHelper.getLegitRotations(new Vec3d(pos))[1]);
            }
            if (this.canBreak(pos)) {
                Nuker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, BlockUtil.getFacing(pos)));
                if (this.autoSwitch.getValue()) {
                    InventoryUtil.switchToHotbarSlot(this.picslot, false);
                }
//                mc.playerController.onPlayerDamageBlock(
//                       pos,
//                        BlockUtil.getRayTraceFacing(pos)
//                );
                Nuker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, BlockUtil.getFacing(pos)));
                Nuker.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                if (this.autoSwitch.getValue()) {
                    InventoryUtil.switchToHotbarSlot(this.oldSlot, false);
                }
            }
        }
    }

    public boolean canBreak(final BlockPos pos) {
        final IBlockState blockState = Nuker.mc.world.getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, Nuker.mc.world, pos) != -1.0f;
    }

    private BlockPos getClosestBlockAll() {
        float maxDist = this.distance.getValue();
        BlockPos ret = null;
        for (float x = maxDist; x >= -maxDist; --x) {
            for (float y = maxDist; y >= -maxDist; --y) {
                for (float z = maxDist; z >= -maxDist; --z) {
                    final BlockPos pos = new BlockPos(Nuker.mc.player.posX + x, Nuker.mc.player.posY + y, Nuker.mc.player.posZ + z);
                    final double dist = Nuker.mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ());
                    if (dist <= maxDist && Nuker.mc.world.getBlockState(pos).getBlock() != Blocks.AIR && !(Nuker.mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) && this.canBreak(pos)) {
                        if (pos.getY() >= Nuker.mc.player.posY) {
                            maxDist = (float) dist;
                            ret = pos;
                        }
                    }
                }
            }
        }
        return ret;
    }

    private BlockPos getClosestBlockSelection() {
        float maxDist = this.distance.getValue();
        BlockPos ret = null;
        for (float x = maxDist; x >= -maxDist; --x) {
            for (float y = maxDist; y >= -maxDist; --y) {
                for (float z = maxDist; z >= -maxDist; --z) {
                    final BlockPos pos = new BlockPos(Nuker.mc.player.posX + x, Nuker.mc.player.posY + y, Nuker.mc.player.posZ + z);
                    final double dist = Nuker.mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ());
                    if (dist <= maxDist && Nuker.mc.world.getBlockState(pos).getBlock() != Blocks.AIR && !(Nuker.mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) && Nuker.mc.world.getBlockState(pos).getBlock() == this.selected && this.canBreak(pos)) {
                        if (pos.getY() >= Nuker.mc.player.posY) {
                            maxDist = (float) dist;
                            ret = pos;
                        }
                    }
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
