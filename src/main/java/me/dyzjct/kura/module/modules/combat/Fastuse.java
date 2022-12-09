package me.dyzjct.kura.module.modules.combat;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.Setting;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;

/**
 * Created by S-B99 on 23/10/2019
 *
 * @author S-B99
 * Updated by S-B99 on 03/12/19
 * Updated by d1gress/Qther on 4/12/19
 */
@Module.Info(category = Category.COMBAT, description = "Use items faster", name = "FastUse")
public class Fastuse extends Module {

    private static long time = 0;
    private final Setting<Integer> delay = isetting("Delay", 0, 0, 10);
    private final Setting<Boolean> all = bsetting("All", false);
    private final Setting<Boolean> bow = bsetting("Bow", false).bn((BooleanSetting) all);
    private final Setting<Boolean> expBottles = bsetting("XP", true).bn((BooleanSetting) all);
    private final Setting<Boolean> endCrystals = bsetting("Crystal", false).bn((BooleanSetting) all);
    private final Setting<Boolean> fireworks = bsetting("FireWorks", false).bn((BooleanSetting) all);

    @Override
    public void onDisable() {
        mc.rightClickDelayTimer = 2;
    }

    @Override
    public void onUpdate() {
        if (mc.player == null) return;

        if (all.getValue() || bow.getValue() && mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 3) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
            mc.player.stopActiveHand();
        }
        if (all.getValue() || bow.getValue() && mc.player.getHeldItemOffhand().getItem() instanceof ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 3) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
            mc.player.stopActiveHand();
        }
        if (all.getValue() || expBottles.getValue() && mc.player.getHeldItemOffhand().getItem() instanceof ItemExpBottle && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 3) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
            mc.player.stopActiveHand();
        }

        if (!(delay.getValue() <= 0)) {
            if (time <= 0) time = Math.round((2 * (Math.round((float) delay.getValue() / 2))));
            else {
                time--;
                mc.rightClickDelayTimer = 1;
                return;
            }
        }

        if (passItemCheck(mc.player.getHeldItemMainhand().getItem()) || passItemCheck(mc.player.getHeldItemOffhand().getItem())) {
            mc.rightClickDelayTimer = 0;
        }
        if (passItemCheck(mc.player.getHeldItemOffhand().getItem())) {
            mc.rightClickDelayTimer = 0;
        }
    }

    private boolean passItemCheck(Item item) {
        if (all.getValue()) return true;
        if (expBottles.getValue() && item instanceof ItemExpBottle) return true;
        if (endCrystals.getValue() && item instanceof ItemEndCrystal) return true;
        return fireworks.getValue() && item instanceof ItemFirework;
    }
}
