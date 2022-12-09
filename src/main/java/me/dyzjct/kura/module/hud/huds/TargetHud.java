package me.dyzjct.kura.module.hud.huds;

import me.dyzjct.kura.event.events.player.AttackEvent;
import me.dyzjct.kura.event.events.render.Render2DEvent;
import me.dyzjct.kura.gui.clickgui.guis.HUDEditorScreen;
import me.dyzjct.kura.module.HUDModule;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.NTMiku.Timer;
import me.dyzjct.kura.utils.math.MathUtil;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.entity.*;
import net.minecraftforge.fml.common.eventhandler.*;
import org.lwjgl.input.*;
import net.minecraft.client.gui.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.renderer.*;
import java.awt.*;
import java.util.*;

@HUDModule.Info(name = "TargetHUD", x = 20, y = 20)
public class TargetHud extends HUDModule
{
    private final Setting<Double> pos = dsetting("Position",0.5f, 0.5f,0.5f);
    private final Timer timer = new Timer();
    private Entity target;
    private Entity lastTarget;
    private float displayHealth;
    private float health;
    private final ArrayList<Particles> particles = new ArrayList<Particles>();
    private boolean sentParticles;
    private double scale = 1.0;
    private final Timer timeUtil = new Timer();
    float ticks;
    int dragX = 0;
    int dragY;
    boolean mousestate = false;
    float posX = 0.0f;
    float posY = 0.0f;


    public static void renderSteveModelTexture(final double x, final double y, final float u, final float v, final int uWidth, final int vHeight, final int width, final int height, final float tileWidth, final float tileHeight) {
        final ResourceLocation skin = new ResourceLocation("textures/entity/steve.png");
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        GL11.glEnable(3042);
        Gui.drawScaledCustomSizeModalRect((int)x, (int)y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
        GL11.glDisable(3042);
    }

    public static void renderPlayerModelTexture(final double x, final double y, final float u, final float v, final int uWidth, final int vHeight, final int width, final int height, final float tileWidth, final float tileHeight, final AbstractClientPlayer target) {
        final ResourceLocation skin = target.getLocationSkin();
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        GL11.glEnable(3042);
        Gui.drawScaledCustomSizeModalRect((int)x, (int)y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
        GL11.glDisable(3042);
    }

    @SubscribeEvent
    public void onAttackEvent(final AttackEvent event) {
        this.target = event.getEntity();
    }

    public int normaliseX() {
        return (int)(Mouse.getX() / 2.0f);
    }

    public int normaliseY() {
        final ScaledResolution sr = new ScaledResolution(TargetHud.mc);
        return (-Mouse.getY() + sr.getScaledHeight() + sr.getScaledHeight()) / 2;
    }

    public boolean isHovering() {
        return this.normaliseX() > this.posX + 38.0f + 2.0f && this.normaliseX() < this.posX + 129.0f && this.normaliseY() > this.posY - 34.0f && this.normaliseY() < this.posY + 14.0f;
    }

    @SubscribeEvent
//    @Override
    public void onRender2D(final Render2DEvent event) {
        final ScaledResolution sr = new ScaledResolution(TargetHud.mc);
        this.posX = (float) (sr.getScaledWidth() * this.pos.getValue());
        this.posY = (float) (sr.getScaledHeight() * this.pos.getValue());
        if (TargetHud.mc.currentScreen instanceof GuiChat || TargetHud.mc.currentScreen instanceof HUDEditorScreen) {
            this.target = (Entity)TargetHud.mc.player;
//            if (this.isHovering() && Mouse.isButtonDown(0) && this.mousestate) {
//                this.pos.getValue().setX((this.normaliseX() - this.dragX) / (float)sr.getScaledWidth());
//                this.pos.getValue().setY((this.normaliseY() - this.dragY) / (float)sr.getScaledHeight());
//            }
        }
        else if (this.target == TargetHud.mc.player) {
            this.target = null;
        }
        if (Mouse.isButtonDown(0) && this.isHovering()) {
            if (!this.mousestate) {
                this.dragX = (int)(this.normaliseX() - this.pos.getValue() * sr.getScaledWidth());
                this.dragY = (int)(this.normaliseY() - this.pos.getValue() * sr.getScaledHeight());
            }
            this.mousestate = true;
        }
        else {
            this.mousestate = false;
        }
        final float nameWidth = 38.0f;
        if (this.timer.passedMs(9L)) {
            if (this.target != null && (this.target.getDistance((Entity)TargetHud.mc.player) > 10.0f || TargetHud.mc.world.getEntityByID(Objects.requireNonNull(this.target).getEntityId()) == null)) {
                this.scale = Math.max(0.0, this.scale - this.timeUtil.getPassedTimeMs() / 8.0E13 - (1.0 - this.scale) / 10.0);
                this.particles.clear();
                this.timer.reset();
            }
            else {
                this.scale = Math.min(1.0, this.scale + this.timeUtil.getPassedTimeMs() / 4.0E14 + (1.0 - this.scale) / 10.0);
            }
        }
        if (this.target == null || !(this.target instanceof EntityPlayer)) {
            this.particles.clear();
            return;
        }
        if (this.scale == 0.0) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate((this.posX + 38.0f + 2.0f + 64.5f) * (1.0 - this.scale), (this.posY - 34.0f + 24.0f) * (1.0 - this.scale), 0.0);
        GlStateManager.scale(this.scale, this.scale, 0.0);
        final EntityPlayer en = (EntityPlayer)this.target;
        final double dist = TargetHud.mc.player.getDistance(this.target);
        final String name = ((EntityPlayer)this.target).getName();
        Particles.roundedRect((double)(this.posX + 38.0f + 2.0f), (double)(this.posY - 34.0f), 129.0, 48.0, 8.0, new Color(0, 0, 0, 110));
        GlStateManager.popMatrix();
        final int scaleOffset = (int)(((EntityPlayer)this.target).hurtTime * 0.35f);
        for (final Particles p : this.particles) {
            if (p.opacity > 4.0) {
                p.render2D();
            }
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate((this.posX + 38.0f + 2.0f + 64.5f) * (1.0 - this.scale), (this.posY - 34.0f + 24.0f) * (1.0 - this.scale), 0.0);
        GlStateManager.scale(this.scale, this.scale, 0.0);
        if (this.target instanceof AbstractClientPlayer) {
            final double offset = -(((AbstractClientPlayer)this.target).hurtTime * 23);
            Particles.color(new Color(255, (int)(255.0 + offset), (int)(255.0 + offset)));
            try {
                renderPlayerModelTexture(this.posX + 38.0f + 6.0f + scaleOffset / 2.0f, this.posY - 34.0f + 5.0f + scaleOffset / 2.0f, 3.0f, 3.0f, 3, 3, 30 - scaleOffset, 30 - scaleOffset, 24.0f, 24.5f, (AbstractClientPlayer)en);
            }
            catch (Exception ex) {}
            renderPlayerModelTexture(this.posX + 38.0f + 6.0f + scaleOffset / 2.0f, this.posY - 34.0f + 5.0f + scaleOffset / 2.0f, 15.0f, 3.0f, 3, 3, 30 - scaleOffset, 30 - scaleOffset, 24.0f, 24.5f, (AbstractClientPlayer)en);
            Particles.color(Color.WHITE);
        }
        final double fontHeight = 7.0;
        fontRenderer.drawString("Distance: " + MathUtil.round(dist, 1), (int)(this.posX + 38.0f + 6.0f + 30.0f + 3.0f), (int)(this.posY - 34.0f + 5.0f + 15.0f + 2.0f), Color.WHITE.hashCode());
        GlStateManager.pushMatrix();
        GL11.glEnable(3089);
        Particles.scissor((double)(this.posX + 38.0f + 6.0f + 30.0f + 3.0f), this.posY - 34.0f + 5.0f + 15.0f - 7.0, 91.0, 30.0);
        fontRenderer.drawString("Name: " + name, (int)(this.posX + 38.0f + 6.0f + 30.0f + 3.0f), (int)(this.posY - 34.0f + 5.0f + 15.0f - 7.0), Color.WHITE.hashCode());
        GL11.glDisable(3089);
        GlStateManager.popMatrix();
        if (!String.valueOf(((EntityPlayer)this.target).getHealth()).equals("NaN")) {
            this.health = Math.min(20.0f, ((EntityPlayer)this.target).getHealth());
        }
        if (String.valueOf(this.displayHealth).equals("NaN")) {
            this.displayHealth = (float)(Math.random() * 20.0);
        }
        if (dist > 20.0 || this.target.isDead) {
            this.health = 0.0f;
        }
        final int speed = 6;
        if (this.timer.passedMs(16L)) {
            this.displayHealth = (this.displayHealth * 5.0f + this.health) / 6.0f;
            this.ticks += 0.1f;
            for (final Particles p2 : this.particles) {
                p2.updatePosition();
                if (p2.opacity < 1.0) {
                    this.particles.remove(p2);
                }
            }
            this.timer.reset();
        }
        float offset2 = 6.0f;
        final float drawBarPosX = this.posX + 38.0f;
        if (this.displayHealth > 0.1) {
            for (int i = 0; i < this.displayHealth * 4.0f; ++i) {
                int color = -1;
                final Color color2 = new Color(78, 161, 253, 100);
                final Color color3 = new Color(78, 253, 154, 100);
                color = Particles.mixColors(color2, color3, (Math.sin(this.ticks + this.posX * 0.4f + i * 0.6f / 14.0f) + 1.0) * 0.5).hashCode();
                Gui.drawRect((int)(drawBarPosX + offset2), (int)(this.posY + 5.0f), (int)(drawBarPosX + 1.0f + offset2 * 1.25), (int)(this.posY + 10.0f), color);
                ++offset2;
            }
        }
        if ((((EntityPlayer)this.target).hurtTime == 9 && !this.sentParticles) || (this.lastTarget != null && ((EntityPlayer)this.lastTarget).hurtTime == 9 && !this.sentParticles)) {
            for (int i = 0; i <= 15; ++i) {
                final Particles p3 = new Particles();
                final Color color4 = new Color(78, 161, 253, 100);
                final Color color5 = new Color(78, 253, 154, 100);
                final Color c = Particles.mixColors(color4, color5, (Math.sin(this.ticks + this.posX * 0.4f + i) + 1.0) * 0.5);
                p3.init((double)(this.posX + 55.0f), (double)(this.posY - 15.0f), (Math.random() - 0.5) * 2.0 * 1.4, (Math.random() - 0.5) * 2.0 * 1.4, Math.random() * 4.0, c);
                this.particles.add(p3);
            }
            this.sentParticles = true;
        }
        if (((EntityPlayer)this.target).hurtTime == 8) {
            this.sentParticles = false;
        }
        if (dist <= 20.0 && !this.target.isDead) {
            fontRenderer.drawString(MathUtil.round(this.displayHealth, 1) + "", (int)(drawBarPosX + 2.0f + offset2 * 1.25), (int)(this.posY + 2.5f), -1);
        }
        if (this.lastTarget != this.target) {
            this.lastTarget = this.target;
        }
        final ArrayList<Particles> removeList = new ArrayList<Particles>();
        for (final Particles p4 : this.particles) {
            if (p4.opacity <= 1.0) {
                removeList.add(p4);
            }
        }
        for (final Particles p4 : removeList) {
            this.particles.remove(p4);
        }
        GlStateManager.popMatrix();
        this.timeUtil.reset();
    }
}
