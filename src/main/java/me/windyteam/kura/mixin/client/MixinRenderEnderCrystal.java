//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

// 
// Decompiled by Procyon v0.5.36
// 

package me.windyteam.kura.mixin.client;

import me.windyteam.kura.module.modules.render.CrystalChams;
import me.windyteam.kura.module.modules.render.Wireframe;
import me.windyteam.kura.utils.color.ColorUtil;
import me.windyteam.kura.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;
import java.awt.*;

@Mixin({RenderEnderCrystal.class})
public class MixinRenderEnderCrystal extends Render<EntityEnderCrystal> {
    @Shadow
    private static final ResourceLocation ENDER_CRYSTAL_TEXTURES;

    static {
        ENDER_CRYSTAL_TEXTURES = new ResourceLocation("textures/entity/endercrystal/endercrystal.png");
    }

    @Shadow
    private final ModelBase modelEnderCrystal;
    @Shadow
    private final ModelBase modelEnderCrystalNoBase;

    protected MixinRenderEnderCrystal(final RenderManager renderManager) {
        super(renderManager);
        this.modelEnderCrystal = new ModelEnderCrystal(0.0f, true);
        this.modelEnderCrystalNoBase = new ModelEnderCrystal(0.0f, false);
    }

    @Redirect(method = {"doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderModelBaseHook(final ModelBase model, final Entity entity, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
        final CrystalChams mod = CrystalChams.INSTANCE;
        final float newLimbSwingAmount = CrystalChams.changeSpeed.getValue() ? (limbSwingAmount * CrystalChams.spinSpeed.getValue()) : limbSwingAmount;
        final float newAgeInTicks = CrystalChams.changeSpeed.getValue() ? ((CrystalChams.floatFactor.getValue() == 0.0f) ? 0.15f : (ageInTicks * CrystalChams.floatFactor.getValue())) : ageInTicks;
        if (mod.isEnabled()) {
            GlStateManager.scale(CrystalChams.scale.getValue(), CrystalChams.scale.getValue(), CrystalChams.scale.getValue());
            if (CrystalChams.model.getValue() == CrystalChams.Model.VANILLA) {
                model.render(entity, limbSwing, newLimbSwingAmount, newAgeInTicks, netHeadYaw, headPitch, scale);
            } else if (CrystalChams.model.getValue() == CrystalChams.Model.XQZ) {
                GL11.glEnable(32823);
                GlStateManager.enablePolygonOffset();
                GL11.glPolygonOffset(1.0f, -1000000.0f);
                if (CrystalChams.modelColor.getValue()) {
                    final Color rainbow = ColorUtil.rainbow(255);
                    final Color color = CrystalChams.rainbow.getValue() ? new Color(rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), CrystalChams.modelAlpha.getValue()) : new Color(CrystalChams.modelRed.getValue(), CrystalChams.modelGreen.getValue(), CrystalChams.modelBlue.getValue(), CrystalChams.modelAlpha.getValue());
                    RenderUtil.glColor(color);
                }
                model.render(entity, limbSwing, newLimbSwingAmount, newAgeInTicks, netHeadYaw, headPitch, scale);
                GL11.glDisable(32823);
                GlStateManager.disablePolygonOffset();
                GL11.glPolygonOffset(1.0f, 1000000.0f);
            }
            if (CrystalChams.wireframe.getValue()) {
                final Color rainbow = ColorUtil.rainbow(255);
                final Color color = CrystalChams.rainbow.getValue() ? new Color(rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), CrystalChams.alpha.getValue()) : (CrystalChams.lineColor.getValue() ? new Color(CrystalChams.lineRed.getValue(), CrystalChams.lineGreen.getValue(), CrystalChams.lineBlue.getValue(), CrystalChams.lineAlpha.getValue()) : new Color(CrystalChams.red.getValue(), CrystalChams.green.getValue(), CrystalChams.blue.getValue(), CrystalChams.alpha.getValue()));
                GL11.glPushMatrix();
                GL11.glPushAttrib(1048575);
                GL11.glPolygonMode(1032, 6913);
                GL11.glDisable(3553);
                GL11.glDisable(2896);
                GL11.glDisable(2929);
                GL11.glEnable(2848);
                GL11.glEnable(3042);
                GlStateManager.blendFunc(770, 771);
                RenderUtil.glColor(color);
                GlStateManager.glLineWidth(CrystalChams.lineWidth.getValue());
                model.render(entity, limbSwing, newLimbSwingAmount, newAgeInTicks, netHeadYaw, headPitch, scale);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
            if (CrystalChams.fill.getValue()) {
                final Color rainbow = ColorUtil.rainbow(255);
                final Color color = CrystalChams.rainbow.getValue() ? new Color(rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), CrystalChams.alpha.getValue()) : new Color(CrystalChams.red.getValue(), CrystalChams.green.getValue(), CrystalChams.blue.getValue(), CrystalChams.alpha.getValue());
                GL11.glPushAttrib(1048575);
                GL11.glDisable(3008);
                GL11.glDisable(3553);
                GL11.glDisable(2896);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glLineWidth(1.5f);
                GL11.glEnable(2960);
                if (CrystalChams.xqz.getValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }
                GL11.glEnable(10754);
                RenderUtil.glColor(color);
                model.render(entity, limbSwing, newLimbSwingAmount, newAgeInTicks, netHeadYaw, headPitch, scale);
                if (CrystalChams.xqz.getValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
                GL11.glEnable(3042);
                GL11.glEnable(2896);
                GL11.glEnable(3553);
                GL11.glEnable(3008);
                GL11.glPopAttrib();
            }
            if (CrystalChams.glint.getValue() && entity instanceof EntityEnderCrystal) {
                final Color rainbow = ColorUtil.rainbow(255);
                final Color color = CrystalChams.rainbow.getValue() ? new Color(rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), CrystalChams.alpha.getValue()) : new Color(CrystalChams.red.getValue(), CrystalChams.green.getValue(), CrystalChams.blue.getValue(), CrystalChams.alpha.getValue());
                GL11.glPushMatrix();
                GL11.glPushAttrib(1048575);
                GL11.glPolygonMode(1032, 6914);
                GL11.glDisable(2896);
                GL11.glDepthRange(0.0, 0.1);
                GL11.glEnable(3042);
                RenderUtil.glColor(color);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
                final float f = entity.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
                Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(new ResourceLocation("textures/misc/enchanted_item_glint.png"));
                for (int i = 0; i < 2; ++i) {
                    GlStateManager.matrixMode(5890);
                    GlStateManager.loadIdentity();
                    GL11.glScalef(1.0f, 1.0f, 1.0f);
                    GlStateManager.rotate(30.0f - i * 60.0f, 0.0f, 0.0f, 1.0f);
                    GlStateManager.translate(0.0f, f * (0.001f + i * 0.003f) * 20.0f, 0.0f);
                    GlStateManager.matrixMode(5888);
                    model.render(entity, limbSwing, newLimbSwingAmount, newAgeInTicks, netHeadYaw, headPitch, scale);
                }
                GlStateManager.matrixMode(5890);
                GlStateManager.loadIdentity();
                GlStateManager.matrixMode(5888);
                GL11.glDisable(3042);
                GL11.glDepthRange(0.0, 1.0);
                GL11.glEnable(2896);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
            GlStateManager.scale(1.0f / CrystalChams.scale.getValue(), 1.0f / CrystalChams.scale.getValue(), 1.0f / CrystalChams.scale.getValue());
        } else {
            model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    /**
     * @author ???
     * @reason ???
     */
    @Overwrite
    public void doRender(final EntityEnderCrystal entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        final float f = entity.innerRotation + partialTicks;
        GlStateManager.pushMatrix();
        GL11.glShadeModel(GL11.GL_FLAT);
        GlStateManager.translate((float) x, (float) y, (float) z);
        this.bindTexture(MixinRenderEnderCrystal.ENDER_CRYSTAL_TEXTURES);
        float f2 = MathHelper.sin(f * 0.2f) / 2.0f + 0.5f;
        f2 += f2 * f2;
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }
        if (Wireframe.getINSTANCE().isEnabled() && Wireframe.getINSTANCE().crystals.getValue()) {
            final float red = Wireframe.getInstance().red.getValue() / 255.0f;
            final float green = Wireframe.getInstance().green.getValue() / 255.0f;
            final float blue = Wireframe.getInstance().blue.getValue() / 255.0f;
            if (Wireframe.getINSTANCE().cMode.getValue().equals(Wireframe.RenderMode.WIREFRAME) && Wireframe.getINSTANCE().crystalModel.getValue()) {
                this.modelEnderCrystalNoBase.render(entity, 0.0f, f * 3.0f, f2 * 0.2f, 0.0f, 0.0f, 0.0625f);
            }
            GlStateManager.pushMatrix();
            GL11.glPushAttrib(1048575);
            if (Wireframe.getINSTANCE().cMode.getValue().equals(Wireframe.RenderMode.WIREFRAME)) {
                GL11.glPolygonMode(1032, 6913);
            }
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            if (Wireframe.getINSTANCE().cMode.getValue().equals(Wireframe.RenderMode.WIREFRAME)) {
                GL11.glEnable(2848);
            }
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glColor4f(Wireframe.getInstance().rainbow.getValue() ? (ColorUtil.rainbow(Wireframe.getInstance().rainbowHue.getValue()).getRed() / 255.0f) : red, Wireframe.getInstance().rainbow.getValue() ? (ColorUtil.rainbow(Wireframe.getInstance().rainbowHue.getValue()).getGreen() / 255.0f) : green, Wireframe.getInstance().rainbow.getValue() ? (ColorUtil.rainbow(Wireframe.getInstance().rainbowHue.getValue()).getBlue() / 255.0f) : blue, Wireframe.getINSTANCE().cAlpha.getValue() / 255.0f);
            if (Wireframe.getINSTANCE().cMode.getValue().equals(Wireframe.RenderMode.WIREFRAME)) {
                GL11.glLineWidth(Wireframe.getINSTANCE().crystalLineWidth.getValue());
            }
            this.modelEnderCrystalNoBase.render(entity, 0.0f, f * 3.0f, f2 * 0.2f, 0.0f, 0.0f, 0.0625f);
            GL11.glDisable(2896);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glColor4f(Wireframe.getInstance().rainbow.getValue() ? (ColorUtil.rainbow(Wireframe.getInstance().rainbowHue.getValue()).getRed() / 255.0f) : red, Wireframe.getInstance().rainbow.getValue() ? (ColorUtil.rainbow(Wireframe.getInstance().rainbowHue.getValue()).getGreen() / 255.0f) : green, Wireframe.getInstance().rainbow.getValue() ? (ColorUtil.rainbow(Wireframe.getInstance().rainbowHue.getValue()).getBlue() / 255.0f) : blue, Wireframe.getINSTANCE().cAlpha.getValue() / 255.0f);
            if (Wireframe.getINSTANCE().cMode.getValue().equals(Wireframe.RenderMode.WIREFRAME)) {
                GL11.glLineWidth(Wireframe.getINSTANCE().crystalLineWidth.getValue());
            }
            this.modelEnderCrystalNoBase.render(entity, 0.0f, f * 3.0f, f2 * 0.2f, 0.0f, 0.0f, 0.0625f);
            GlStateManager.enableDepth();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        } else {
            this.modelEnderCrystalNoBase.render(entity, 0.0f, f * 3.0f, f2 * 0.2f, 0.0f, 0.0f, 0.0625f);
        }
        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        final BlockPos blockpos = entity.getBeamTarget();
        if (blockpos != null) {
            this.bindTexture(RenderDragon.ENDERCRYSTAL_BEAM_TEXTURES);
            final float f3 = blockpos.getX() + 0.5f;
            final float f4 = blockpos.getY() + 0.5f;
            final float f5 = blockpos.getZ() + 0.5f;
            final double d0 = f3 - entity.posX;
            final double d2 = f4 - entity.posY;
            final double d3 = f5 - entity.posZ;
            RenderDragon.renderCrystalBeams(x + d0, y - 0.3 + f2 * 0.4f + d2, z + d3, partialTicks, f3, f4, f5, entity.innerRotation, entity.posX, entity.posY, entity.posZ);
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    protected ResourceLocation getEntityTexture(final EntityEnderCrystal entityEnderCrystal) {
        return null;
    }
}
