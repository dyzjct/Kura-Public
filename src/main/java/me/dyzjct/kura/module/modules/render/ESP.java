package me.dyzjct.kura.module.modules.render;

import me.dyzjct.kura.event.events.render.RenderEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.ColorSetting;
import me.dyzjct.kura.setting.IntegerSetting;
import me.dyzjct.kura.utils.entity.EntityUtil;
import me.dyzjct.kura.utils.gl.MelonTessellator;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

@Module.Info(name = "ESP", category = Category.RENDER)
public class ESP extends Module {
    public BooleanSetting items = bsetting("Items", true);
    public BooleanSetting xpbottles = bsetting("XpBottles", true);
    public BooleanSetting pearl = bsetting("Pearls", true);
    public ColorSetting color = csetting("Color", new Color(239, 53, 88));
    public IntegerSetting alpha = isetting("Alpha", 255, 0, 255);
    public IntegerSetting boxAlpha = isetting("BoxAlpha", 120, 0, 255);

    @Override
    public void onWorldRender(RenderEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (this.items.getValue()) {
            for (Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
                if (!(entity instanceof EntityItem) || mc.player.getDistanceSq(entity) >= 2500.0D)
                    continue;
                Vec3d interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
                AxisAlignedBB bb = new AxisAlignedBB((entity.getEntityBoundingBox()).minX - 0.05D - entity.posX + interp.x, (entity.getEntityBoundingBox()).minY - 0.0D - entity.posY + interp.y, (entity.getEntityBoundingBox()).minZ - 0.05D - entity.posZ + interp.z, (entity.getEntityBoundingBox()).maxX + 0.05D - entity.posX + interp.x, (entity.getEntityBoundingBox()).maxY + 0.1D - entity.posY + interp.y, (entity.getEntityBoundingBox()).maxZ + 0.05D - entity.posZ + interp.z);
                MelonTessellator.prepare(7);
                GL11.glLineWidth(1f);
                RenderGlobal.renderFilledBox(bb, color.getValue().getRed() / 255f, color.getValue().getGreen() / 255f, color.getValue().getBlue() / 255f, boxAlpha.getValue() / 255F);
                MelonTessellator.drawBlockOutline(bb, color.getValue(), alpha.getValue(), 2f);
                MelonTessellator.release();
            }
        }
        if (this.pearl.getValue()) {
            for (Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
                if (!(entity instanceof EntityEnderPearl) || mc.player.getDistanceSq(entity) >= 2500.0D)
                    continue;
                Vec3d interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
                AxisAlignedBB bb = new AxisAlignedBB((entity.getEntityBoundingBox()).minX - 0.05D - entity.posX + interp.x, (entity.getEntityBoundingBox()).minY - 0.0D - entity.posY + interp.y, (entity.getEntityBoundingBox()).minZ - 0.05D - entity.posZ + interp.z, (entity.getEntityBoundingBox()).maxX + 0.05D - entity.posX + interp.x, (entity.getEntityBoundingBox()).maxY + 0.1D - entity.posY + interp.y, (entity.getEntityBoundingBox()).maxZ + 0.05D - entity.posZ + interp.z);
                MelonTessellator.prepare(7);
                GL11.glLineWidth(1f);
                RenderGlobal.renderFilledBox(bb, color.getValue().getRed() / 255f, color.getValue().getGreen() / 255f, color.getValue().getBlue() / 255f, boxAlpha.getValue() / 255F);
                MelonTessellator.drawBlockOutline(bb, color.getValue(), alpha.getValue(), 2f);
                MelonTessellator.release();
            }
        }
        if (this.xpbottles.getValue()) {
            for (Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
                if (!(entity instanceof EntityExpBottle) || mc.player.getDistanceSq(entity) >= 2500.0D)
                    continue;
                Vec3d interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
                AxisAlignedBB bb = new AxisAlignedBB((entity.getEntityBoundingBox()).minX - 0.05D - entity.posX + interp.x, (entity.getEntityBoundingBox()).minY - 0.0D - entity.posY + interp.y, (entity.getEntityBoundingBox()).minZ - 0.05D - entity.posZ + interp.z, (entity.getEntityBoundingBox()).maxX + 0.05D - entity.posX + interp.x, (entity.getEntityBoundingBox()).maxY + 0.1D - entity.posY + interp.y, (entity.getEntityBoundingBox()).maxZ + 0.05D - entity.posZ + interp.z);
                MelonTessellator.prepare(7);
                GL11.glLineWidth(1f);
                RenderGlobal.renderFilledBox(bb, color.getValue().getRed() / 255f, color.getValue().getGreen() / 255f, color.getValue().getBlue() / 255f, boxAlpha.getValue() / 255F);
                MelonTessellator.drawBlockOutline(bb, color.getValue(), alpha.getValue(), 2f);
                MelonTessellator.release();
            }
        }
    }
}
