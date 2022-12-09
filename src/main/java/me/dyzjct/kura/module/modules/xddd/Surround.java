package me.dyzjct.kura.module.modules.xddd;

import me.dyzjct.kura.event.events.player.UpdateWalkingPlayerEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.utils.NTMiku.TimerUtils;
import me.dyzjct.kura.utils.block.BlockUtil;
import me.dyzjct.kura.utils.entity.EntityUtil;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;
import java.util.stream.Collectors;

@Module.Info(name = "Surround", category = Category.XDDD, description = "Continually places obsidian around your feet")
public class Surround
        extends Module {
    public static TimerUtils delay = new TimerUtils();
    public static BlockPos[] surroundPos = new BlockPos[]{new BlockPos(0, -1, 0), new BlockPos(1, -1, 0), new BlockPos(-1, -1, 0), new BlockPos(0, -1, 1), new BlockPos(0, -1, -1), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
    public int slot;
    public int oldslot;
    public BlockPos startPos;
    public BooleanSetting smart = this.bsetting("Smart", true);
    public BooleanSetting center = this.bsetting("Center", true);
    public BooleanSetting packet = this.bsetting("Packet", true);
    public BooleanSetting rot = this.bsetting("Rotate", false);
    public BooleanSetting breakcry = this.bsetting("BreakCrystal", true);
    public BlockPos newPos2;
    public TimerUtils explodeTimerUtils = new TimerUtils();

    @Override
    public void onEnable() {
        if (Surround.fullNullCheck()) {
            return;
        }
        delay.reset();
        this.startPos = EntityUtil.getPlayerPos();
        BlockPos centerPos = Surround.mc.player.getPosition();
        double y = centerPos.getY();
        double x = centerPos.getX();
        double z = centerPos.getZ();
        Vec3d plusPlus = new Vec3d(x + 0.5, y, z + 0.5);
        Vec3d plusMinus = new Vec3d(x + 0.5, y, z - 0.5);
        Vec3d minusMinus = new Vec3d(x - 0.5, y, z - 0.5);
        Vec3d minusPlus = new Vec3d(x - 0.5, y, z + 0.5);
        if (this.center.getValue().booleanValue()) {
            if (this.getDst(plusPlus) < this.getDst(plusMinus) && this.getDst(plusPlus) < this.getDst(minusMinus) && this.getDst(plusPlus) < this.getDst(minusPlus)) {
                x = (double) centerPos.getX() + 0.5;
                z = (double) centerPos.getZ() + 0.5;
                this.centerPlayer(x, y, z);
            }
            if (this.getDst(plusMinus) < this.getDst(plusPlus) && this.getDst(plusMinus) < this.getDst(minusMinus) && this.getDst(plusMinus) < this.getDst(minusPlus)) {
                x = (double) centerPos.getX() + 0.5;
                z = (double) centerPos.getZ() - 0.5;
                this.centerPlayer(x, y, z);
            }
            if (this.getDst(minusMinus) < this.getDst(plusPlus) && this.getDst(minusMinus) < this.getDst(plusMinus) && this.getDst(minusMinus) < this.getDst(minusPlus)) {
                x = (double) centerPos.getX() - 0.5;
                z = (double) centerPos.getZ() - 0.5;
                this.centerPlayer(x, y, z);
            }
            if (this.getDst(minusPlus) < this.getDst(plusPlus) && this.getDst(minusPlus) < this.getDst(plusMinus) && this.getDst(minusPlus) < this.getDst(minusMinus)) {
                x = (double) centerPos.getX() - 0.5;
                z = (double) centerPos.getZ() + 0.5;
                this.centerPlayer(x, y, z);
            }
        }
        Surround.mc.playerController.syncCurrentPlayItem();
    }

    @Override
    public void onLogout() {
        this.disable();
    }

    @SubscribeEvent
    public void onUpdate(UpdateWalkingPlayerEvent event) {
        if (Surround.fullNullCheck()) {
            this.toggle();
            return;
        }
        if (breakcry.getValue()) {
            this.breakcrystal();
        }
        this.slot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        this.oldslot = Surround.mc.player.inventory.currentItem;
        if (this.startPos != null && this.smart.getValue().booleanValue() && !this.startPos.equals(EntityUtil.getPlayerPos())) {
            this.toggle();
            return;
        }
        if (this.slot == -1) {
            this.toggle();
            return;
        }
        for (BlockPos pos : surroundPos) {
            this.newPos2 = this.addPos(pos);
            if (BlockUtil.isPositionPlaceable(this.newPos2, false) < 2) continue;
            if (this.slot == -1) {
                this.toggle();
            }
            InventoryUtil.switchToHotbarSlot(this.slot, false);
            BlockUtil.placeBlock(this.newPos2, EnumHand.MAIN_HAND, this.rot.getValue(), this.packet.getValue());
            InventoryUtil.switchToHotbarSlot(this.oldslot, false);
        }
    }

    public BlockPos addPos(BlockPos pos) {
        BlockPos pPos = EntityUtil.getPlayerPos(0.2);
        return new BlockPos(pPos.getX() + pos.getX(), pPos.getY() + pos.getY(), pPos.getZ() + pos.getZ());
    }

    public double getDst(Vec3d vec) {
        return Surround.mc.player.getPositionVector().distanceTo(vec);
    }

    public void centerPlayer(double x, double y, double z) {
        Surround.mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));
        Surround.mc.player.setPosition(x, y, z);
    }

    public void breakcrystal() {
        for (Entity crystal : mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal && !e.isDead).sorted(Comparator.comparing(e -> Float.valueOf(mc.player.getDistance(e)))).collect(Collectors.toList())) {
            if (!(crystal instanceof EntityEnderCrystal) || !(mc.player.getDistance(crystal) <= 4.0f))
                continue;
            if (explodeTimerUtils.passed(50) && mc.getConnection() != null) {
                mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                explodeTimerUtils.reset();
            }
        }
    }
}

