package me.windyteam.kura.module.modules.combat;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.modules.misc.AntiPiston;
import me.windyteam.kura.setting.IntegerSetting;
import me.windyteam.kura.utils.block.BlockUtil;
import me.windyteam.kura.utils.entity.EntityUtil;
import me.windyteam.kura.utils.inventory.InventoryUtil;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;
import java.util.stream.Collectors;


@Module.Info(name = "PistonCrystal+",category = Category.COMBAT)
public class PistonCrystalPlus extends Module{
    private final IntegerSetting range = isetting("Range",5,1,16);
    public EntityPlayer target;
    private static Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void onUpdate(){
        if (InventoryUtil.findHotbarBlock(BlockPistonBase.class) == -1) {
            return;
        }
        if (InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK) == -1) {
            return;
        }
        if (InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN) == -1) {
            return;
        }
        target = this.getTarget(this.range.getValue().floatValue());
        if (target == null) {
            return;
        }
        BlockPos pos = new BlockPos(this.target.posX, this.target.posY, this.target.posZ);
//        if ()
    }
    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0) + 1.0;
        for (EntityPlayer player : mc.world.playerEntities) {
            if (EntityUtil.isntValid(player, range))
                continue;
            if (target == null) {
                target = player;
                distance = mc.player.getDistanceSq(player);
                continue;
            }
            if (!(mc.player.getDistanceSq(player) < distance)) continue;
            target = player;
            distance = mc.player.getDistanceSq(player);
        }
        return target;
    }
    public static void breakcrystal() {
        if (fullNullCheck()) return;
        for (Entity crystal : AntiPiston.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal && !e.isDead).sorted(Comparator.comparing(e -> Float.valueOf(AntiPiston.mc.player.getDistance(e)))).collect(Collectors.toList())) {
            if (!(crystal instanceof EntityEnderCrystal) || !(AntiPiston.mc.player.getDistance(crystal) <= 4.0f))
                continue;
            AntiPiston.mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
            AntiPiston.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
        }
    }
    private void perform(BlockPos pos) {
        int old = mc.player.inventory.currentItem;
        if (mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN) {
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(BlockPistonBase.class);
            mc.playerController.updateController();
            BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, false, true, false);
            mc.player.inventory.currentItem = old;
            mc.playerController.updateController();
        }
    }
    private void perform1(BlockPos pos) {
        int old = mc.player.inventory.currentItem;
        if (mc.world.getBlockState(pos).getBlock() == Blocks.AIR) {
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
            mc.playerController.updateController();
            BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, false, true, false);
            mc.player.inventory.currentItem = old;
            mc.playerController.updateController();
        }
    }
    private void perform2(BlockPos pos) {
        int old = mc.player.inventory.currentItem;
        if (mc.world.getBlockState(pos).getBlock() == Blocks.AIR) {
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK);
            mc.playerController.updateController();
            BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, false, true, false);
            mc.player.inventory.currentItem = old;
            mc.playerController.updateController();
        }
    }
    private void perform3(BlockPos pos) {
        int old = mc.player.inventory.currentItem;
        if (mc.world.getBlockState(pos).getBlock() == Blocks.AIR) {
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(EntityEnderCrystal.class);
            mc.playerController.updateController();
            BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, false, true, false);
            mc.player.inventory.currentItem = old;
            mc.playerController.updateController();
        }
    }

}
