package me.dyzjct.kura.module.modules.misc;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.NTMiku.BlockUtil;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;
import java.util.stream.Collectors;

@Module.Info(name = "AntiPiston", category = Category.MISC)
public class AntiPiston extends Module {
    private final Setting<Boolean> rotate = bsetting("Rotate", false);
    private int obsidian = -1;

    public static void breakcrystal() {
        if (fullNullCheck()) return;
        for (Entity crystal : AntiPiston.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal && !e.isDead).sorted(Comparator.comparing(e -> Float.valueOf(AntiPiston.mc.player.getDistance(e)))).collect(Collectors.toList())) {
            if (!(crystal instanceof EntityEnderCrystal) || !(AntiPiston.mc.player.getDistance(crystal) <= 4.0f))
                continue;
            AntiPiston.mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
            AntiPiston.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
        }
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) return;
        AntiPiston.breakcrystal();
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) return;
        if (AntiPiston.mc.player == null || AntiPiston.mc.world == null) {
            return;
        }
        this.obsidian = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        if (this.obsidian == -1) {
            return;
        }
        BlockPos pos = new BlockPos(AntiPiston.mc.player.posX, AntiPiston.mc.player.posY, AntiPiston.mc.player.posZ);
        if (pos == null) {
            return;
        }
        if (this.getBlock(pos.add(1, 1, 0)).getBlock() == Blocks.PISTON) {
            if (this.getBlock(pos.add(-1,1,0)).getBlock()== Blocks.AIR){
                this.perform(pos.add(-1, 1, 0));
            }
            else if (this.getBlock(pos.add(-1,2,0)).getBlock()== Blocks.AIR){
                this.perform(pos.add(-1, 2, 0));
            }
            else if (this.getBlock(pos.add(0,2,0)).getBlock()== Blocks.AIR){
                this.perform(pos.add(0,2,0));
            }
            mc.playerController.onPlayerDamageBlock(pos.add(1, 1, 0), BlockUtil.getRayTraceFacing(pos.add(1, 1, 0)));
        }
        if (this.getBlock(pos.add(-1, 1, 0)).getBlock() == Blocks.PISTON) {
            if (this.getBlock(pos.add(1,1,0)).getBlock()== Blocks.AIR){
                this.perform(pos.add(1, 1, 0));
            }
            else if (this.getBlock(pos.add(1,2,0)).getBlock()== Blocks.AIR){
                this.perform(pos.add(1, 2, 0));
            }
            else if (this.getBlock(pos.add(0,2,0)).getBlock()== Blocks.AIR){
                this.perform(pos.add(0,2,0));
            }
            mc.playerController.onPlayerDamageBlock(pos.add(-1, 1, 0), BlockUtil.getRayTraceFacing(pos.add(-1, 1, 0)));
        }
        if (this.getBlock(pos.add(0, 1, 1)).getBlock() == Blocks.PISTON) {
            if (this.getBlock(pos.add(0,1,-1)).getBlock()== Blocks.AIR){
                this.perform(pos.add(0, 1, -1));
            }
            else if (this.getBlock(pos.add(0,2,-1)).getBlock()== Blocks.AIR){
                this.perform(pos.add(0, 2, -1));
            }
            else if (this.getBlock(pos.add(0,2,0)).getBlock()== Blocks.AIR){
                this.perform(pos.add(0,2,0));
            }
            mc.playerController.onPlayerDamageBlock(pos.add(0, 1, 1), BlockUtil.getRayTraceFacing(pos.add(0, 1, 1)));
        }
        if (this.getBlock(pos.add(0, 1, -1)).getBlock() == Blocks.PISTON) {
            if (this.getBlock(pos.add(0,1,1)).getBlock()== Blocks.AIR){
                this.perform(pos.add(0, 1, 1));
            }
            else if (this.getBlock(pos.add(0,2,1)).getBlock()== Blocks.AIR){
                this.perform(pos.add(0, 2, 1));
            }
            else if (this.getBlock(pos.add(0,2,0)).getBlock()== Blocks.AIR){
                this.perform(pos.add(0,2,0));
            }
            mc.playerController.onPlayerDamageBlock(pos.add(0, 1, -1), BlockUtil.getRayTraceFacing(pos.add(0, 1, -1)));
        }
//        STICKY_PISTON
        if (this.getBlock(pos.add(1, 1, 0)).getBlock() == Blocks.STICKY_PISTON) {
            if (this.getBlock(pos.add(-1,1,0)).getBlock()== Blocks.AIR){
                this.perform(pos.add(-1, 1, 0));
            }
            else if (this.getBlock(pos.add(-1,2,0)).getBlock()== Blocks.AIR){
                this.perform(pos.add(-1, 2, 0));
            }
            else if (this.getBlock(pos.add(0,2,0)).getBlock()== Blocks.AIR){
                this.perform(pos.add(0,2,0));
            }
            mc.playerController.onPlayerDamageBlock(pos.add(1, 1, 0), BlockUtil.getRayTraceFacing(pos.add(1, 1, 0)));
        }
        if (this.getBlock(pos.add(-1, 1, 0)).getBlock() == Blocks.STICKY_PISTON) {
            if (this.getBlock(pos.add(1,1,0)).getBlock()== Blocks.AIR){
                this.perform(pos.add(1, 1, 0));
            }
            else if (this.getBlock(pos.add(1,2,0)).getBlock()== Blocks.AIR){
                this.perform(pos.add(1, 2, 0));
            }
            else if (this.getBlock(pos.add(0,2,0)).getBlock()== Blocks.AIR){
                this.perform(pos.add(0,2,0));
            }
            mc.playerController.onPlayerDamageBlock(pos.add(-1, 1, 0), BlockUtil.getRayTraceFacing(pos.add(-1, 1, 0)));
        }
        if (this.getBlock(pos.add(0, 1, 1)).getBlock() == Blocks.STICKY_PISTON) {
            if (this.getBlock(pos.add(0,1,-1)).getBlock()== Blocks.AIR){
                this.perform(pos.add(0, 1, -1));
            }
            else if (this.getBlock(pos.add(0,2,-1)).getBlock()== Blocks.AIR){
                this.perform(pos.add(0, 2, -1));
            }
            else if (this.getBlock(pos.add(0,2,0)).getBlock()== Blocks.AIR){
                this.perform(pos.add(0,2,0));
            }
            mc.playerController.onPlayerDamageBlock(pos.add(0, 1, 1), BlockUtil.getRayTraceFacing(pos.add(0, 1, 1)));
        }
        if (this.getBlock(pos.add(0, 1, -1)).getBlock() == Blocks.STICKY_PISTON) {
            if (this.getBlock(pos.add(0,1,1)).getBlock()== Blocks.AIR){
                this.perform(pos.add(0, 1, 1));
            }
            else if (this.getBlock(pos.add(0,2,1)).getBlock()== Blocks.AIR){
                this.perform(pos.add(0, 2, 1));
            }
            else if (this.getBlock(pos.add(0,2,0)).getBlock()== Blocks.AIR){
                this.perform(pos.add(0,2,0));
            }
            mc.playerController.onPlayerDamageBlock(pos.add(0, 1, -1), BlockUtil.getRayTraceFacing(pos.add(0, 1, -1)));
        }
    }

    private void switchToSlot(int slot) {
        if (fullNullCheck()) return;
        AntiPiston.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        AntiPiston.mc.player.inventory.currentItem = slot;
        AntiPiston.mc.playerController.updateController();
    }

    private IBlockState getBlock(BlockPos block) {
        return AntiPiston.mc.world.getBlockState(block);
    }


    private void perform(BlockPos pos) {
        if (fullNullCheck()) return;
        int old = AntiPiston.mc.player.inventory.currentItem;
        this.switchToSlot(this.obsidian);
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), true, false);
        this.switchToSlot(old);
    }
}