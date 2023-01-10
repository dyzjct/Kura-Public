package me.windyteam.kura.module.modules.combat;

import me.windyteam.kura.event.events.block.BlockEvents;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.module.modules.misc.InstantMine;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.setting.IntegerSetting;
import me.windyteam.kura.utils.block.BlockUtil;
import me.windyteam.kura.utils.entity.EntityUtil;
import me.windyteam.kura.utils.inventory.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.ArrayList;

@Module.Info(name = "HoleMiner", category = Category.COMBAT)
public class HoleMiner extends Module
{
    public IntegerSetting range;
    public BooleanSetting disable;
    public EntityPlayer target;
    
    public HoleMiner() {
        this.range = this.isetting("Range", 4, 0, 6);
        this.disable = this.bsetting("Toggle", false);
    }
    
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        try {
            if (ModuleManager.getModuleByClass((Class)CevBreaker.class).isEnabled()) {
                return;
            }
            if (this.disable.getValue()) {
                this.disable();
            }
            if (InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE) == -1) {
                return;
            }
            this.target = this.getTarget(this.range.getValue());
            if (this.target != null) {
                this.surroundMineBlock(this.target);
            }
        }
        catch (Exception ex) {}
    }
    
    public String getHudInfo() {
        if (this.target == null) {
            return null;
        }
        return this.target.getName();
    }
    
    public void surroundMineBlock(final EntityPlayer player) {
        final Vec3d a = player.getPositionVector();
        if (EntityUtil.getSurroundWeakness(a, 1, -1)) {
            this.surroundMine(a, -1.0, 0.0, 0.0);
            return;
        }
        if (EntityUtil.getSurroundWeakness(a, 2, -1)) {
            this.surroundMine(a, 1.0, 0.0, 0.0);
            return;
        }
        if (EntityUtil.getSurroundWeakness(a, 3, -1)) {
            this.surroundMine(a, 0.0, 0.0, -1.0);
            return;
        }
        if (EntityUtil.getSurroundWeakness(a, 4, -1)) {
            this.surroundMine(a, 0.0, 0.0, 1.0);
            return;
        }
        if (EntityUtil.getSurroundWeakness(a, 5, -1)) {
            this.surroundMine(a, -1.0, 0.0, 0.0);
            return;
        }
        if (EntityUtil.getSurroundWeakness(a, 6, -1)) {
            this.surroundMine(a, 1.0, 0.0, 0.0);
            return;
        }
        if (EntityUtil.getSurroundWeakness(a, 7, -1)) {
            this.surroundMine(a, 0.0, 0.0, -1.0);
            return;
        }
        if (!EntityUtil.getSurroundWeakness(a, 8, -1)) {
            return;
        }
        this.surroundMine(a, 0.0, 0.0, 1.0);
    }
    
    public void surroundMine(final Vec3d pos, final double x, final double y, final double z) {
        final BlockPos position = new BlockPos(pos).add(x, y, z);
        if (!BlockUtil.canBreak(position, false)) {
            return;
        }
        if (ModuleManager.getModuleByClass((Class) InstantMine.class).isDisabled()) {
            return;
        }
        if (InstantMine.breakPos != null || InstantMine.breakPos2 != null) {
            if (InstantMine.breakPos.equals((Object)position)) {
                return;
            }
            if (InstantMine.breakPos.equals((Object)new BlockPos(this.target.posX, this.target.posY, this.target.posZ)) && HoleMiner.mc.world.getBlockState(new BlockPos(this.target.posX, this.target.posY, this.target.posZ)).getBlock() != Blocks.AIR) {
                return;
            }
        }
        MinecraftForge.EVENT_BUS.post((Event)new BlockEvents(position, BlockUtil.getRayTraceFacing(position)));
    }
    
    public EntityPlayer getTarget(final double range) {
        for (final EntityPlayer player : new ArrayList<EntityPlayer>(HoleMiner.mc.world.playerEntities)) {
            if (!EntityUtil.isntValid((Entity)player, range)) {
                if (!EntityUtil.isInHole((Entity)player)) {
                    continue;
                }
                if (HoleMiner.mc.player.getDistance((Entity)player) > range) {
                    continue;
                }
                return player;
            }
        }
        return null;
    }
}
