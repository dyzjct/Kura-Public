package me.dyzjct.kura.module.modules.render;

import me.dyzjct.kura.event.events.render.RenderEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.FloatSetting;
import me.dyzjct.kura.setting.IntegerSetting;
import me.dyzjct.kura.utils.gl.XG42Tessellator;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.awt.*;

@Module.Info(name = "BlockHighlight", category = Category.RENDER)
public class BlockHighlight
        extends Module {
    private final BooleanSetting boundingbox = this.bsetting("BoundingBox", true);
    private final BooleanSetting box = this.bsetting("FullBlock", true);
    private final FloatSetting width = this.fsetting("Width", 1.5f, 0.0f, 10.0f);
    private final IntegerSetting alpha = this.isetting("Alpha", 28, 1, 255);
    private final IntegerSetting Red = this.isetting("Red", 255, 1, 255);
    private final IntegerSetting Green = this.isetting("Green", 255, 1, 255);
    private final IntegerSetting Blue = this.isetting("Blue", 255, 1, 255);
    private final IntegerSetting alpha2 = this.isetting("Alpha", 255, 1, 255);
    private final BooleanSetting rainbow = this.bsetting("Rainbow", true);

    @Override
    public void onWorldRender(RenderEvent event) {
        BlockPos blockpos;
        float[] hue = new float[]{(float) (System.currentTimeMillis() % 11520L) / 11520.0f};
        int rgb = Color.HSBtoRGB(hue[0], 1.0f, 1.0f);
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        Minecraft mc = Minecraft.getMinecraft();
        RayTraceResult ray = mc.objectMouseOver;
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK && mc.world.getBlockState(blockpos = ray.getBlockPos()).getMaterial() != Material.AIR && mc.world.getWorldBorder().contains(blockpos)) {
            if (this.box.getValue()) {
                XG42Tessellator.prepare(7);
                if (this.rainbow.getValue()) {
                    XG42Tessellator.drawBox(blockpos, r, g, b, this.alpha.getValue(), 63);
                } else {
                    XG42Tessellator.drawBox(blockpos, this.Red.getValue(), this.Green.getValue(), this.Blue.getValue(), this.alpha.getValue(), 63);
                }
                XG42Tessellator.release();
            }
            if (this.boundingbox.getValue()) {
                XG42Tessellator.prepare(7);
                if (this.rainbow.getValue()) {
                    XG42Tessellator.drawBoundingBoxBlockPos(blockpos, this.width.getValue(), r, g, b, this.alpha2.getValue());
                } else {
                    XG42Tessellator.drawBoundingBoxBlockPos(blockpos, this.width.getValue(), this.Red.getValue(), this.Green.getValue(), this.Blue.getValue(), this.alpha2.getValue());
                }
                XG42Tessellator.release();
            }
        }
    }
}

