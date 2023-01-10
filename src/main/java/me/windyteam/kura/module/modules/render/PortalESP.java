package me.windyteam.kura.module.modules.render;

import me.windyteam.kura.event.events.render.RenderEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.ArrayList;

@Module.Info(name = "PortalESP", category = Category.RENDER, description = "PortalEsp")
public class PortalESP extends Module {
    private final ArrayList<BlockPos> blockPosArrayList = new ArrayList<>();
    private final Setting<Integer> distance = isetting("Range", 50, 1, 100);
    private final BooleanSetting box = bsetting("Box", true);
    private final Setting<Integer> boxAlpha = isetting("BoxAlpha", 150, 0, 255).b(box);
    private final BooleanSetting outline = bsetting("Outline", true);
    private final Setting<Float> lineWidth = fsetting("OutlineWidth", 1f, 0.1f, 5f).b(outline);
    private int cooldownTicks;

    @SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        if (mc.world == null) {
            return;
        }
        if (this.cooldownTicks < 1) {
            this.blockPosArrayList.clear();
            this.compileDL();
            this.cooldownTicks = 80;
        }
        --this.cooldownTicks;
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (mc.world == null) {
            return;
        }
        blockPosArrayList.forEach(pos -> RenderUtil.drawBoxESP(pos, new Color(204, 0, 153, 255), false, new Color(204, 0, 153, 255), this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false));
    }

    public void compileDL() {
        if (mc.world == null || mc.player == null) {
            return;
        }
        for (int x = (int) mc.player.posX - this.distance.getValue(); x <= (int) mc.player.posX + this.distance.getValue(); ++x) {
            for (int y = (int) mc.player.posY - this.distance.getValue(); y <= (int) mc.player.posY + this.distance.getValue(); ++y) {
                int z = (int) Math.max(mc.player.posZ - (double) this.distance.getValue(), 0.0);
                while ((double) z <= Math.min(mc.player.posZ + (double) this.distance.getValue(), 255.0)) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = mc.world.getBlockState(pos).getBlock();
                    if (block instanceof BlockPortal) {
                        this.blockPosArrayList.add(pos);
                    }
                    ++z;
                }
            }
        }
    }
}

