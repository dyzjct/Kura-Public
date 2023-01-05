package me.dyzjct.kura.module.modules.player;

import me.dyzjct.kura.event.events.entity.MotionUpdateEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.module.modules.misc.InstantMine;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.NTMiku.BlockUtil;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import me.dyzjct.kura.utils.math.MathUtil;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "AntiShulkerBox",category = Category.PLAYER)
public class AntiShulkerBox
        extends Module {
    private static AntiShulkerBox INSTANCE = new AntiShulkerBox();
    private final Setting<Integer> range = isetting("Range", 5, 1, 6);
    private final Setting<Integer> saferange = isetting("SafeRange", 2, 0, 6);
//    private final Setting<Boolean> MineMod = this.register(new Setting("PacketMine+",true));

    public AntiShulkerBox() {
        this.setInstance();
    }

    public static AntiShulkerBox getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AntiShulkerBox();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onTick(MotionUpdateEvent event) {
        if (AntiShulkerBox.fullNullCheck()) {
            return;
        }
        int mainSlot = mc.player.inventory.currentItem;
        if (!ModuleManager.getModuleByClass(InstantMine.class).isEnabled()) {
            return;
        }
        for (BlockPos blockPos : this.breakPos(this.range.getValue().intValue())) {
            int slotPick = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
            if (slotPick == -1) {
                return;
            }
            if (mc.player.getDistanceSq(blockPos) < MathUtil.square(this.saferange.getValue().intValue()) || blockPos == null) continue;
            if (mc.world.getBlockState(blockPos).getBlock() instanceof BlockShulkerBox) {
                mc.player.inventory.currentItem = slotPick;
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.playerController.onPlayerDamageBlock(blockPos, BlockUtil.getRayTraceFacing(blockPos));
                continue;
            }
            mc.player.inventory.currentItem = mainSlot;
        }
    }

    private NonNullList<BlockPos> breakPos(float placeRange) {
        NonNullList positions = NonNullList.create();
        positions.addAll(BlockUtil.getSphere(new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ)), placeRange, 0, false, true, 0));
        return positions;
    }
}

