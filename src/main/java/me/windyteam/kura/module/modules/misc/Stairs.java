package me.windyteam.kura.module.modules.misc;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.setting.IntegerSetting;
import me.windyteam.kura.utils.TimerUtils;
import net.minecraft.block.BlockStairs;
import net.minecraft.util.math.BlockPos;

@Module.Info(name = "Stairs",category = Category.MISC)
public class Stairs extends Module {
    private final IntegerSetting delay = settings( "Delay", 100, 0, 1000);
    private final BooleanSetting whileSneaking = settings("WhileSneaking", false);

    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
    private final TimerUtils timer = new TimerUtils();
    private double currentY;
    private double lastY;

    @Override
    public void onUpdate() {
        if (fullNullCheck()) return;

        if (mc.player.posY != currentY || timer.passed(100)) {
            if (currentY != lastY) {
                lastY = currentY;
            }
            currentY = mc.player.posY;
        }

        if (timer.passed(delay.getValue())
                && mc.player.onGround
                && mc.player.moveForward > 0
                && lastY < currentY
                && !mc.player.isSpectator()
                && !mc.player.isRiding()
                && !mc.player.isOnLadder()
                && (!mc.player.isSneaking() || whileSneaking.getValue())
                && checkForStairs()) {
            mc.player.jump();
            timer.reset();
        }
    }

    private boolean checkForStairs() {
        pos.setPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        if (mc.world.getBlockState(pos).getBlock() instanceof BlockStairs) {
            return true;
        }

        pos.setPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
        return mc.world.getBlockState(pos).getBlock() instanceof BlockStairs;
    }

}
