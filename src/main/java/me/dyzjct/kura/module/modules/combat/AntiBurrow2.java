package me.dyzjct.kura.module.modules.combat;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.modules.misc.InstantMine;
import me.dyzjct.kura.setting.DoubleSetting;
import me.dyzjct.kura.utils.block.BlockUtil;
import me.dyzjct.kura.utils.entity.EntityUtil;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

@Module.Info(name = "BurrowMiner", category = Category.COMBAT)
public class AntiBurrow2
        extends Module {
    public DoubleSetting range = dsetting("Range", 4.0, 0.0, 10.0);
    public static BlockPos pos;
    int ticked = 0;
    EntityPlayer player;



//    private EntityPlayer getTarget(double range) {
//        EntityPlayer target = null;
//        double distance = Math.pow(range, 2.0) + 1.0;
//        for (EntityPlayer player : AntiBurrow2.mc.world.playerEntities) {
//            if (EntityUtil.isntValid((Entity)player, range) || SpeedManager.getPlayerSpeed(player) > 10.0) continue;
//            if (target == null) {
//                target = player;
//                distance = AntiBurrow2.mc.player.getDistanceSq((Entity)player);
//                continue;
//            }
//            if (AntiBurrow2.mc.player.getDistanceSq((Entity)player) >= distance) continue;
//            target = player;
//            distance = AntiBurrow2.mc.player.getDistanceSq((Entity)player);
//        }
//        return target;
//    }
    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        for (EntityPlayer player : new ArrayList<>(mc.world.playerEntities)) {
            if (EntityUtil.isntValid(player, range)) continue;
            if (mc.player.getDistance(player) > range) continue;
            target = player;
            if (player != null) {
                break;
            }
            return player;
        }
        return target;
    }


    @Override
    public void onUpdate() {
        if (AntiBurrow2.fullNullCheck()) {
            return;
        }
        try {
            EntityPlayer player = getTarget(range.getValue());
        } catch (Exception ignored) {
        }
//
        if (AntiBurrow2.mc.currentScreen instanceof GuiHopper) {
            return;
        }
        this.player = this.getTarget(this.range.getValue());
        if (this.player == null) {
            return;
        }
        pos = new BlockPos(this.player.posX, this.player.posY + 0.5, this.player.posZ);
        if (this.ticked >= 0) {
            ++this.ticked;
        }
        if (InstantMine.breakPos != null && InstantMine.breakPos.equals((Object)pos) && this.ticked >= 60 && AntiBurrow2.mc.world.getBlockState(pos).getBlock() != Blocks.BEDROCK && AntiBurrow2.mc.world.getBlockState(pos).getBlock() != Blocks.AIR && AntiBurrow2.mc.world.getBlockState(pos).getBlock() != Blocks.WEB && AntiBurrow2.mc.world.getBlockState(pos).getBlock() != Blocks.REDSTONE_WIRE && !this.isOnLiquid() && !this.isInLiquid() && AntiBurrow2.mc.world.getBlockState(pos).getBlock() != Blocks.WATER && AntiBurrow2.mc.world.getBlockState(pos).getBlock() != Blocks.LAVA) {
            AntiBurrow2.mc.player.swingArm(EnumHand.MAIN_HAND);
            AntiBurrow2.mc.playerController.onPlayerDamageBlock(pos, BlockUtil.getRayTraceFacing(pos));
            this.ticked = 1;
        }
        if (InstantMine.breakPos2 != null && InstantMine.breakPos2.equals((Object)pos)) {
            return;
        }
        if (InstantMine.breakPos != null) {
            if (InstantMine.breakPos.equals((Object)pos)) {
                return;
            }
            if (InstantMine.breakPos.equals((Object)new BlockPos(AntiBurrow2.mc.player.posX, AntiBurrow2.mc.player.posY + 2.0, AntiBurrow2.mc.player.posZ))) {
                return;
            }
            if (InstantMine.breakPos.equals((Object)new BlockPos(AntiBurrow2.mc.player.posX, AntiBurrow2.mc.player.posY - 1.0, AntiBurrow2.mc.player.posZ))) {
                return;
            }
            if (AntiBurrow2.mc.world.getBlockState(InstantMine.breakPos).getBlock() == Blocks.WEB) {
                return;
            }
        }
        if (AntiBurrow2.mc.world.getBlockState(pos).getBlock() != Blocks.AIR && AntiBurrow2.mc.world.getBlockState(pos).getBlock() != Blocks.BEDROCK && AntiBurrow2.mc.world.getBlockState(pos).getBlock() != Blocks.WEB && AntiBurrow2.mc.world.getBlockState(pos).getBlock() != Blocks.REDSTONE_WIRE && !this.isOnLiquid() && !this.isInLiquid() && AntiBurrow2.mc.world.getBlockState(pos).getBlock() != Blocks.WATER && AntiBurrow2.mc.world.getBlockState(pos).getBlock() != Blocks.LAVA) {
            AntiBurrow2.mc.player.swingArm(EnumHand.MAIN_HAND);
            AntiBurrow2.mc.playerController.onPlayerDamageBlock(pos, BlockUtil.getRayTraceFacing(pos));
            this.ticked = 1;
        }
    }

//    @Override
    public String getDisplayInfo() {
//        if (!HUD.getInstance().moduleInfo.getValue().booleanValue()) {
//            return null;
//        }
        if (this.player != null) {
            return this.player.getName();
        }
        return null;
    }

    private boolean isOnLiquid() {
        double y = AntiBurrow2.mc.player.posY - 0.03;
        for (int x = MathHelper.floor((double)AntiBurrow2.mc.player.posX); x < MathHelper.ceil((double)AntiBurrow2.mc.player.posX); ++x) {
            for (int z = MathHelper.floor((double)AntiBurrow2.mc.player.posZ); z < MathHelper.ceil((double)AntiBurrow2.mc.player.posZ); ++z) {
                BlockPos pos = new BlockPos(x, MathHelper.floor((double)y), z);
                if (!(AntiBurrow2.mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean isInLiquid() {
        double y = AntiBurrow2.mc.player.posY + 0.01;
        for (int x = MathHelper.floor((double)AntiBurrow2.mc.player.posX); x < MathHelper.ceil((double)AntiBurrow2.mc.player.posX); ++x) {
            for (int z = MathHelper.floor((double)AntiBurrow2.mc.player.posZ); z < MathHelper.ceil((double)AntiBurrow2.mc.player.posZ); ++z) {
                BlockPos pos = new BlockPos(x, (int)y, z);
                if (!(AntiBurrow2.mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid)) continue;
                return true;
            }
        }
        return false;
    }
}

