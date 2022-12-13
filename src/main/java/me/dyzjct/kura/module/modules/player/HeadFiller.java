package me.dyzjct.kura.module.modules.player;

import me.dyzjct.kura.friend.FriendManager;
import me.dyzjct.kura.module.*;
import me.dyzjct.kura.setting.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.math.*;
import me.dyzjct.kura.event.events.block.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.dyzjct.kura.event.events.entity.*;
import net.minecraft.block.*;
import me.dyzjct.kura.utils.inventory.*;
import net.minecraft.init.*;
import net.minecraft.util.*;
import me.dyzjct.kura.utils.block.*;
import me.dyzjct.kura.utils.entity.*;
import net.minecraft.entity.*;
import me.dyzjct.kura.manager.*;
import java.util.*;

@Module.Info(name = "HeadFiller", category = Category.PLAYER)
public class HeadFiller extends Module
{
    public DoubleSetting range;
    public BooleanSetting packet;
    public BooleanSetting rot;
    public int slot;
    public int oldslot;
    public EntityPlayer target;
    public BlockPos minePos;

    public HeadFiller() {
        this.range = this.dsetting("Range", 4.0, 1.0, 6.0);
        this.packet = this.bsetting("Packet", true);
        this.rot = this.bsetting("Rotate", false);
    }

    public String getHudInfo() {
        if (this.target == null) {
            return null;
        }
        return this.target.getName();
    }

    @SubscribeEvent
    public void onBreak(final BlockBreakEvent event) {
        if (fullNullCheck()) {
            return;
        }
        this.minePos = event.getPosition();
    }

    @SubscribeEvent
    public void onMotion(final MotionUpdateEvent.Tick event) {
        if (fullNullCheck()) {
            return;
        }
        this.target = this.getTarget(this.range.getValue());
        if (this.target == null) {
            return;
        }
        final BlockPos people = new BlockPos(this.target.posX, this.target.posY, this.target.posZ);
        final int obiSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        if (obiSlot == -1) {
            return;
        }
        final int old = HeadFiller.mc.player.inventory.currentItem;
        if (HeadFiller.mc.world.getBlockState(people.add(0, 2, 0)).getBlock() == Blocks.AIR) {
            if (HeadFiller.mc.world.getBlockState(people.add(1, 2, 0)).getBlock() != Blocks.AIR || HeadFiller.mc.world.getBlockState(people.add(0, 2, 1)).getBlock() != Blocks.AIR || HeadFiller.mc.world.getBlockState(people.add(-1, 2, 0)).getBlock() != Blocks.AIR || HeadFiller.mc.world.getBlockState(people.add(0, 2, -1)).getBlock() != Blocks.AIR) {
                HotbarManager.spoofHotbar(obiSlot);
                BlockUtil.placeBlock(people.add(0, 2, 0), EnumHand.MAIN_HAND, false, true);
                HotbarManager.spoofHotbar(old);
            }
            else if (HeadFiller.mc.world.getBlockState(people.add(1, 1, 0)).getBlock() != Blocks.AIR) {
                HotbarManager.spoofHotbar(obiSlot);
                BlockUtil.placeBlock(people.add(1, 2, 0), EnumHand.MAIN_HAND, false, true);
                HotbarManager.spoofHotbar(old);
            }
            else if (HeadFiller.mc.world.getBlockState(people.add(-1, 1, 0)).getBlock() != Blocks.AIR) {
                HotbarManager.spoofHotbar(obiSlot);
                BlockUtil.placeBlock(people.add(-1, 2, 0), EnumHand.MAIN_HAND, false, true);
                HotbarManager.spoofHotbar(old);
            }
            else if (HeadFiller.mc.world.getBlockState(people.add(0, 1, 1)).getBlock() != Blocks.AIR) {
                HotbarManager.spoofHotbar(obiSlot);
                BlockUtil.placeBlock(people.add(0, 2, 1), EnumHand.MAIN_HAND, false, true);
                HotbarManager.spoofHotbar(old);
            }
            else if (HeadFiller.mc.world.getBlockState(people.add(0, 1, -1)).getBlock() != Blocks.AIR) {
                HotbarManager.spoofHotbar(obiSlot);
                BlockUtil.placeBlock(people.add(0, 2, -1), EnumHand.MAIN_HAND, false, true);
                HotbarManager.spoofHotbar(old);
            }
        }
    }

    private EntityPlayer getTarget(final double range) {
        EntityPlayer target = null;
        double distance = range;
        for (final EntityPlayer player : new ArrayList<EntityPlayer>(HeadFiller.mc.world.playerEntities)) {
            if (EntityUtil.isntValid((Entity)player, range)) {
                continue;
            }
            if (FriendManager.isFriend(player.getName())) {
                continue;
            }
            if (HeadFiller.mc.player.posY - player.posY >= 5.0) {
                continue;
            }
            if (target == null) {
                target = player;
                distance = HeadFiller.mc.player.getDistanceSq((Entity)player);
            }
            else {
                if (HeadFiller.mc.player.getDistanceSq((Entity)player) >= distance) {
                    continue;
                }
                target = player;
                distance = HeadFiller.mc.player.getDistanceSq((Entity)player);
            }
        }
        return target;
    }
}
