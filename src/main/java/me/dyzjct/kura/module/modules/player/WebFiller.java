package me.dyzjct.kura.module.modules.player;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.NTMiku.BlockUtil;
import me.dyzjct.kura.utils.entity.EntityUtil;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

@Module.Info(name = "WebFiller", category = Category.PLAYER)
public class WebFiller extends Module
{
    private final Setting<Float> range;
    private final Setting<Boolean> autoDisable;
    public EntityPlayer target;
    
    public WebFiller() {
        this.range = (Setting<Float>)this.fsetting("Range", (float)5.0f, (float)1.0f, (float)6.0f);
        this.autoDisable = (Setting<Boolean>)this.bsetting("AutoDisable", true);
    }
    
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        this.target = this.getTarget((float)this.range.getValue());
        if (this.target == null) {
            return;
        }
        final BlockPos people = new BlockPos(this.target.posX, this.target.posY, this.target.posZ);
        final int webSlot = InventoryUtil.findHotbarBlock((Class)BlockWeb.class);
        if (webSlot == -1) {
            return;
        }
        final int old = WebFiller.mc.player.inventory.currentItem;
        if (this.getBlock(people.add(0, -1, 0)).getBlock() == Blocks.AIR) {
            this.switchToSlot(webSlot);
            BlockUtil.placeBlock(people.add(0, -1, 0), EnumHand.MAIN_HAND, false, true, false);
            this.switchToSlot(old);
        }
        if (this.autoDisable.getValue()) {
            this.disable();
        }
    }
    
    private EntityPlayer getTarget(final double range) {
        EntityPlayer target = null;
        for (final EntityPlayer player : new ArrayList<EntityPlayer>(WebFiller.mc.world.playerEntities)) {
            if (EntityUtil.isntValid((Entity)player, range)) {
                continue;
            }
            if (WebFiller.mc.player.getDistance((Entity)player) > range) {
                continue;
            }
            if ((target = player) != null) {
                break;
            }
            return player;
        }
        return target;
    }
    
    private void switchToSlot(final int slot) {
        if (fullNullCheck()) {
            return;
        }
        WebFiller.mc.player.inventory.currentItem = slot;
        WebFiller.mc.playerController.updateController();
    }
    
    private IBlockState getBlock(final BlockPos block) {
        return WebFiller.mc.world.getBlockState(block);
    }
}
