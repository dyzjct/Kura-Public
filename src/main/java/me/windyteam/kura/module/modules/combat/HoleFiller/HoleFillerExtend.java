package me.windyteam.kura.module.modules.combat.HoleFiller;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.utils.entity.EntityUtil;
import me.windyteam.kura.utils.entity.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

@Module.Info(name = "FeetMiner",category = Category.COMBAT)
public class HoleFillerExtend {
    public static EntityPlayer target;
    private static Minecraft mc = Minecraft.getMinecraft();
    public static Boolean AntiFuck = true;


    public void HoleFillerExtend() {
        target = this.getTarget(6);
        if (target == null) {
            return;
        }
        BlockPos feet = new BlockPos(HoleFillerExtend.target.posX, HoleFillerExtend.target.posY, HoleFillerExtend.target.posZ);
        if (!this.detection(target)) {
            if (this.getBlock(feet.add(0,0,0)).getBlock()==Blocks.WEB&this.getBlock(feet.add(0,0,0)).getBlock()==Blocks.OBSIDIAN){
                AntiFuck = false;
            } else if (this.getBlock(feet.add(0,-1,0)).getBlock()==Blocks.WEB&this.getBlock(feet.add(0,-1,0)).getBlock()==Blocks.OBSIDIAN) {
                AntiFuck = false;
            }
            else {
                AntiFuck=true;
            }
        }
    }



    private boolean detection(EntityPlayer player) {
        return HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX + 1.2, player.posY, player.posZ)).getBlock() == Blocks.AIR & HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX + 1.2, player.posY + 1.0, player.posZ)).getBlock() == Blocks.AIR || HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX - 1.2, player.posY, player.posZ)).getBlock() == Blocks.AIR & HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX - 1.2, player.posY + 1.0, player.posZ)).getBlock() == Blocks.AIR || HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ + 1.2)).getBlock() == Blocks.AIR & HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX, player.posY + 1.0, player.posZ + 1.2)).getBlock() == Blocks.AIR || HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ - 1.2)).getBlock() == Blocks.AIR & HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX, player.posY + 1.0, player.posZ - 1.2)).getBlock() == Blocks.AIR || HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX + 2.2, player.posY + 1.0, player.posZ)).getBlock() == Blocks.AIR & HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX + 2.2, player.posY, player.posZ)).getBlock() == Blocks.AIR & HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX + 1.2, player.posY, player.posZ)).getBlock() == Blocks.AIR || HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX - 2.2, player.posY + 1.0, player.posZ)).getBlock() == Blocks.AIR & HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX - 2.2, player.posY, player.posZ)).getBlock() == Blocks.AIR & HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX - 1.2, player.posY, player.posZ)).getBlock() == Blocks.AIR || HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX, player.posY + 1.0, player.posZ + 2.2)).getBlock() == Blocks.AIR & HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ + 2.2)).getBlock() == Blocks.AIR & HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ + 1.2)).getBlock() == Blocks.AIR || HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX, player.posY + 1.0, player.posZ - 2.2)).getBlock() == Blocks.AIR & HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ - 2.2)).getBlock() == Blocks.AIR & HoleFillerExtend.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ - 1.2)).getBlock() == Blocks.AIR;
    }

    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        for (EntityPlayer player : mc.world.playerEntities) {
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


    private IBlockState getBlock(BlockPos block) {
        return HoleFillerExtend.mc.world.getBlockState(block);
    }
}

