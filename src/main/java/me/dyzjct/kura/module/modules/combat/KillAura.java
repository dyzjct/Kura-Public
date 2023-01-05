package me.dyzjct.kura.module.modules.combat;

import me.dyzjct.kura.event.events.entity.MotionUpdateEvent;
import me.dyzjct.kura.event.events.render.RenderEvent;
import me.dyzjct.kura.event.events.render.item.RenderItemAnimationEvent;
import me.dyzjct.kura.friend.FriendManager;
import me.dyzjct.kura.manager.RotationManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.IntegerSetting;
import me.dyzjct.kura.setting.ModeSetting;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.Timer;
import me.dyzjct.kura.utils.block.BlockInteractionHelper;
import me.dyzjct.kura.utils.entity.EntityUtil;
import me.dyzjct.kura.utils.gl.XG42Tessellator;
import me.dyzjct.kura.utils.inventory.ItemUtil;
import me.dyzjct.kura.utils.math.RandomUtil;
import me.dyzjct.kura.utils.math.deneb.LagCompensator;
import me.dyzjct.kura.utils.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

import java.awt.*;
import java.util.Comparator;

import static org.lwjgl.opengl.GL11.*;

@Module.Info(name = "KillAura", category = Category.COMBAT)
public class KillAura extends Module {
    public static KillAura INSTANCE = new KillAura();
    public ModeSetting<?> page = msetting("Page", Page.ONE);
    public ModeSetting<?> Mode = msetting("Mode", Modes.Closest);
    public ModeSetting<?> render = msetting("RenderMode", Render.Novo);
    public ModeSetting<?> attackMode = msetting("AttackMode", AttackMode.Tick);
    public Setting<Float> Distance = fsetting("Range", 5.5f, 0, 8).m(page, Page.ONE);
    public Setting<Boolean> TPSSync = bsetting("TpsSync", true).m(page, Page.ONE);
    public Setting<Boolean> Players = bsetting("Players", true).m(page, Page.ONE);
    public Setting<Boolean> Monsters = bsetting("Monsters", true).m(page, Page.ONE);
    public Setting<Boolean> Neutrals = bsetting("Neutrals", true).m(page, Page.ONE);
    public Setting<Boolean> Animals = bsetting("Animals", true).m(page, Page.ONE);
    public Setting<Boolean> Tamed = bsetting("Tamed", false).m(page, Page.ONE);
    public Setting<Boolean> Projectiles = bsetting("Projectiles", false).m(page, Page.ONE);
    public Setting<Boolean> SwordOnly = bsetting("SwordOnly", false).m(page, Page.ONE);
    public Setting<Boolean> PauseIfCrystal = bsetting("PauseIfCA", true).m(page, Page.ONE);
    public Setting<Boolean> PauseIfEating = bsetting("PauseIfEating", false).m(page, Page.ONE);
    public Setting<Boolean> AutoSwitch = bsetting("AutoSwitch", true).m(page, Page.ONE);
    public Setting<Boolean> Only32k = bsetting("Only32K", false).m(page, Page.ONE);
    //Page Two
    public IntegerSetting minCPS = isetting("MinCPS", 8, 0, 150).m(page, Page.TWO);
    public IntegerSetting maxCPS = isetting("MaxCPS", 14, 0, 300).m(page, Page.TWO);
    public BooleanSetting rotate = bsetting("Rotate", true).m(page, Page.TWO);
    public Setting<Boolean> Hyp = bsetting("HypCheck", false).m(page, Page.TWO);
    public Setting<Boolean> Team = bsetting("Team", false).m(page, Page.TWO);
    public BooleanSetting hurtTimeCheck = bsetting("HurtTimeCheck", false).m(page, Page.TWO);
    public Setting<Boolean> autoblock = bsetting("AutoBlock", false).m(page, Page.TWO);
    public Setting<Boolean> animation = bsetting("Animation", false).m(page, Page.TWO);
    public Setting<Boolean> RenderTarget = bsetting("Render", false).m(page, Page.TWO);
    public Timer attackTimer = new Timer();
    public Timer updateTimer = new Timer();
    public Entity CurrentTarget;
    public boolean canRender = false;
    public boolean step = false;
    public int cps = 0;
    public int delay;
    public int b;

    public static void drawEntityESP(double x, double y, double z, double width, double height, float red, float green, float blue, float alpha, float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWdith) {
        XG42Tessellator.prepare(GL11.GL_QUADS);
        GL11.glLineWidth((float) width);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.color(red / 255f, green / 255f, blue / 255f, alpha / 255f);
        RenderUtil.drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glLineWidth(lineWdith);
        GL11.glColor4f(lineRed, lineGreen, lineBlue, lineAlpha);
        RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GlStateManager.color(red / 255f, green / 255f, blue / 255f, alpha / 255f);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        XG42Tessellator.release();
    }

    public static boolean isTeam(EntityPlayer e, EntityPlayer e2) {
        if (e2.getTeam() != null && e.getTeam() != null) {
            Character target = e2.getDisplayName().getFormattedText().charAt(1);
            Character player = e.getDisplayName().getFormattedText().charAt(1);
            return target.equals(player);
        } else {
            return true;
        }
    }

    public static void glColor(int hex) {
        float alpha = (float) (hex >> 24 & 255) / 255.0f;
        float red = (float) (hex >> 16 & 255) / 255.0f;
        float green = (float) (hex >> 8 & 255) / 255.0f;
        float blue = (float) (hex & 255) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha == 0.0f ? 1.0f : alpha);
    }

    public static void enableSmoothLine(float width) {
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2884);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glLineWidth(width);
    }

    public static void disableSmoothLine() {
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDepthMask(true);
        GL11.glCullFace(1029);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void drawESP(EntityLivingBase entity, int color) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) mc.getRenderPartialTicks()
                - mc.getRenderManager().renderPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) mc.getRenderPartialTicks()
                - mc.getRenderManager().renderPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) mc.getRenderPartialTicks()
                - mc.getRenderManager().renderPosZ;
        float radius = 0.2f;
        int side = 6;
        GL11.glPushMatrix();
        GL11.glTranslated(x, y + 2, z);
        GL11.glRotatef(-entity.width, 0.0f, 1.0f, 0.0f);
        glColor(new Color(Math.max(new Color(color).getRed() - 75, 0), Math.max(new Color(color).getGreen() - 75, 0),
                Math.max(new Color(color).getBlue() - 75, 0), new Color(color).getAlpha()).getRGB());
        enableSmoothLine(1.0f);
        Cylinder c = new Cylinder();
        GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);

        c.setDrawStyle(100012);
        c.draw(0, radius, 0.3f, side, 1);
        glColor(color);
        c.setDrawStyle(100012);
        GL11.glTranslated(0, 0, 0.3);
        c.draw(radius, 0, 0.3f, side, 1);

        GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);

        disableSmoothLine();
        GL11.glPopMatrix();
    }

    public boolean ShouldAttack() {
        int az = RandomUtil.nextInt(minCPS.getValue(), maxCPS.getValue());
        cps = az;
        return attackTimer.passed(az) && attackMode.getValue().equals(AttackMode.Cps);
    }

    public void drawCircle(Entity entity, float partialTicks, double rad, float height) {
        XG42Tessellator.prepare(GL_QUADS);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_LINE_SMOOTH);
        glDepthMask(false);
        glLineWidth(2.0f);
        glBegin(GL_LINE_STRIP);

        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;
        double pix2 = Math.PI * 2.0D;

        GlStateManager.color(224 / 255f, 63 / 255f, 216 / 255f, 255 / 255f);
        for (int i = 0; i <= 180; ++i) {
            glVertex3d(x + rad * Math.cos(i * pix2 / 45), y + height * (i / 180f), z + rad * Math.sin(i * pix2 / 45));
        }
        glEnd();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        XG42Tessellator.release();
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        canRender = false;
        cps = 0;
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        canRender = false;
        RotationManager.resetRotation();
        cps = 0;
    }

    @Override
    public String getHudInfo() {
        return "CPS: " + cps;
    }

    public boolean IsValidTarget(Entity p_Entity) {
        if (p_Entity instanceof EntityArmorStand) {
            return false;
        }
        if (!(p_Entity instanceof EntityLivingBase)) {
            boolean l_IsProjectile = p_Entity instanceof EntityShulkerBullet || p_Entity instanceof EntityFireball;
            if (!l_IsProjectile)
                return false;

            if (!Projectiles.getValue())
                return false;
        }

        if (p_Entity instanceof EntityPlayer) {
            /// Ignore if it's us
            if (p_Entity == mc.player)
                return false;

            if (!Players.getValue())
                return false;

            /// They are a friend, ignore it.
            if (FriendManager.isFriend(p_Entity.getName()))
                return false;

            if (Team.getValue()) {
                if (isTeam(mc.player, (EntityPlayer) p_Entity)) {
                    return false;
                }
            }
        }

        if (EntityUtil.isHostileMob(p_Entity) && !Monsters.getValue()) {
            return false;
        }

        if (EntityUtil.isPassive(p_Entity)) {
            if (p_Entity instanceof AbstractChestHorse) {
                AbstractChestHorse l_Horse = (AbstractChestHorse) p_Entity;

                if (l_Horse.isTame() && !Tamed.getValue()) {
                    return false;
                }
            }

            if (!Animals.getValue()) {
                return false;
            }
        }

        if (EntityUtil.isHostileMob(p_Entity) && !Monsters.getValue()) {
            return false;
        }

        if (EntityUtil.isNeutralMob(p_Entity) && !Neutrals.getValue()) {
            return false;
        }

        boolean l_HealthCheck = true;

        if (p_Entity instanceof EntityLivingBase) {
            EntityLivingBase l_Base = (EntityLivingBase) p_Entity;

            l_HealthCheck = !l_Base.isDead && l_Base.getHealth() > 0.0f;
        }


        return l_HealthCheck && p_Entity.getDistance(p_Entity) <= Distance.getValue();
    }

    @SubscribeEvent
    public void onUpdate(MotionUpdateEvent.Tick event) {
        if (fullNullCheck()) {
            return;
        }
        if (updateTimer.passed(1000)) {
            cps = 0;
            updateTimer.reset();
        }
        for (int oao = 0; oao < 360; oao++) {
            oao = ++b;
        }
        if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword)) {
            if (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && PauseIfCrystal.getValue())
                return;
            if (mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE && PauseIfEating.getValue())
                return;
        }
        if (Only32k.getValue()) {
            if (!ItemUtil.Is32k(mc.player.getHeldItemMainhand()))
                return;
        }
        if (Mode.getValue() == Modes.Closest) {
            CurrentTarget = mc.world.loadedEntityList.stream()
                    .filter(this::IsValidTarget)
                    .min(Comparator.comparing(p_Entity -> mc.player.getDistance(p_Entity)))
                    .orElse(null);
        }
        if (Mode.getValue() == Modes.Priority) {
            if (CurrentTarget == null) {
                CurrentTarget = mc.world.loadedEntityList.stream()
                        .filter(this::IsValidTarget)
                        .min(Comparator.comparing(p_Entity -> mc.player.getDistance(p_Entity)))
                        .orElse(null);
            }
        }
        if (Mode.getValue() == Modes.Switch) {
            CurrentTarget = mc.world.loadedEntityList.stream()
                    .filter(this::IsValidTarget)
                    .min(Comparator.comparing(p_Entity -> mc.player.getDistance(p_Entity)))
                    .orElse(null);
        }
        if (CurrentTarget == null || CurrentTarget.getDistance(mc.player) > Distance.getValue()) {
            CurrentTarget = null;
            cps = 0;
            canRender = false;
            RotationManager.resetRotation();
            return;
        }
        if (CurrentTarget != null) {
            if (AutoSwitch.getValue()) {
                for (int l_I = 0; l_I < 9; ++l_I) {
                    if (mc.player.inventory.getStackInSlot(l_I).getItem() instanceof ItemSword) {
                        mc.player.inventory.currentItem = l_I;
                        mc.playerController.updateController();
                        break;
                    }
                }
            }
            if (SwordOnly.getValue() && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword)) {
                return;
            }
            if (rotate.getValue()) {
                mc.player.rotationYawHead = BlockInteractionHelper.getLegitRotations(new Vec3d(CurrentTarget.posX, CurrentTarget.posY, CurrentTarget.posZ))[0];
                mc.player.renderYawOffset = BlockInteractionHelper.getLegitRotations(new Vec3d(CurrentTarget.posX, CurrentTarget.posY, CurrentTarget.posZ))[0];
                event.setYaw(BlockInteractionHelper.getLegitRotations(new Vec3d(CurrentTarget.posX, CurrentTarget.posY, CurrentTarget.posZ))[0]);
                event.setPitch(BlockInteractionHelper.getLegitRotations(new Vec3d(CurrentTarget.posX, CurrentTarget.posY, CurrentTarget.posZ))[1]);
            }
            float l_Ticks = 20.0f - LagCompensator.INSTANCE.getTickRate();
            boolean l_IsAttackReady = mc.player.getCooledAttackStrength(TPSSync.getValue() ? -l_Ticks : 0.0f) >= 1;
            if (attackMode.getValue().equals(AttackMode.Tick)) {
                if (!l_IsAttackReady) {
                    cps = 0;
                    return;
                }
            }
            if (mc.getConnection() != null) {
                if (Hyp.getValue()) {
                    if (!mc.player.canEntityBeSeen(CurrentTarget)) {
                        CurrentTarget = null;
                        canRender = false;
                        return;
                    }
                }
                if (hurtTimeCheck.getValue()) {
                    if (!(((EntityLivingBase) CurrentTarget).hurtTime < 0)) {
                        return;
                    }
                }
                if (attackMode.getValue().equals(AttackMode.Cps)) {
                    if (ShouldAttack()) {
                        attack(CurrentTarget);
                        attackTimer.reset();
                    }
                } else if (attackMode.getValue().equals(AttackMode.Tick)) {
                    attack(CurrentTarget);
                    ++cps;
                }
                if (!animation.getValue()) {
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                } else {
                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                }
            }
            mc.player.resetCooldown();
        }
    }

    public void attack(Entity e) {
        canRender = true;
        if (autoblock.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
        mc.player.connection.sendPacket(new CPacketUseEntity(e));
    }

    @SubscribeEvent
    public void onTransformItem(RenderItemAnimationEvent.Transform event) {
        if (fullNullCheck() || CurrentTarget == null || !canRender) return;
        if (event.getHand() == EnumHand.MAIN_HAND && CurrentTarget != null && animation.getValue()) {
            float i;
            if (mc.player.getPrimaryHand().equals(EnumHandSide.RIGHT)) {
                i = 1f;
            } else {
                i = -1f;
            }
            GlStateManager.translate(0.15f * i, 0.3f, 0.0f);
            GlStateManager.rotate(5f * i, 0.0f, 0.0f, 0.0f);
            if (i > 0F) GlStateManager.translate(0.56f, -0.52f, -0.72f * i);
            else GlStateManager.translate(
                    0.56f,
                    -0.52f,
                    0.5F
            );
            GlStateManager.translate(0.0f, 0.2f * 0.6f, 0.0f);
            GlStateManager.rotate(++b, b / 2f, b / i * 2f, b / 2f);
            GlStateManager.scale(1.625f, 1.625f, 1.625f);
        }
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (CurrentTarget != null && canRender && RenderTarget.getValue()) {
            if (delay > 200) {
                step = false;
            }
            if (delay < 0) {
                step = true;
            }
            if (step) {
                delay += 3;
            } else {
                delay -= 3;
            }
            double x = CurrentTarget.lastTickPosX
                    + (CurrentTarget.posX - CurrentTarget.lastTickPosX) * mc.timer.renderPartialTicks
                    - mc.renderManager.renderPosX;
            double y = CurrentTarget.lastTickPosY
                    + (CurrentTarget.posY - CurrentTarget.lastTickPosY) * mc.timer.renderPartialTicks
                    - mc.renderManager.renderPosY;
            double z = CurrentTarget.lastTickPosZ
                    + (CurrentTarget.posZ - CurrentTarget.lastTickPosZ) * mc.timer.renderPartialTicks
                    - mc.renderManager.renderPosZ;
            if (render.getValue().equals(Render.Novo)) {
                if (CurrentTarget instanceof EntityPlayer) {
                    double width = CurrentTarget.getEntityBoundingBox().maxX
                            - CurrentTarget.getEntityBoundingBox().minX;
                    double height = CurrentTarget.getEntityBoundingBox().maxY
                            - CurrentTarget.getEntityBoundingBox().minY + 0.25;
                    float red = ((EntityPlayer) CurrentTarget).hurtTime > 0 ? 1.0f : 0.0f;
                    float green = ((EntityPlayer) CurrentTarget).hurtTime > 0 ? 0.2f : 0.5f;
                    float blue = ((EntityPlayer) CurrentTarget).hurtTime > 0 ? 0.0f : 1.0f;
                    float alpha = 0.2f;
                    float lineRed = ((EntityPlayer) CurrentTarget).hurtTime > 0 ? 1.0f : 0.0f;
                    float lineGreen = ((EntityPlayer) CurrentTarget).hurtTime > 0 ? 0.2f : 0.5f;
                    float lineBlue = ((EntityPlayer) CurrentTarget).hurtTime > 0 ? 0.0f : 1.0f;
                    float lineAlpha = 1.0f;
                    float lineWdith = 2.0f;
                    drawEntityESP(x, y, z, width, height, red, green, blue, alpha, lineRed, lineGreen, lineBlue,
                            lineAlpha, lineWdith);
                } else {
                    double width = CurrentTarget.getEntityBoundingBox().maxZ
                            - CurrentTarget.getEntityBoundingBox().minZ;
                    double height = 0.1;
                    float red = 0.0f;
                    float green = 0.5f;
                    float blue = 1.0f;
                    float alpha = 0.5f;
                    float lineRed = 0.0f;
                    float lineGreen = 0.5f;
                    float lineBlue = 1.0f;
                    float lineAlpha = 1.0f;
                    float lineWdith = 2.0f;
                    drawEntityESP(x, y + CurrentTarget.getEyeHeight() + 0.25, z, width, height, red, green,
                            blue, alpha, lineRed, lineGreen, lineBlue, lineAlpha, lineWdith);
                }
            }
            if (render.getValue().equals(Render.Circle)) {
                drawCircle(CurrentTarget, mc.getRenderPartialTicks(), 0.8, delay / 100f);
            }
            if (render.getValue().equals(Render.New)) {
                EntityLivingBase entity = (EntityLivingBase) CurrentTarget;
                drawESP(entity, entity.hurtTime >= 1 ? (new Color(255, 0, 0, 160).getRGB()) : (new Color(47, 116, 253, 255).getRGB()));
            }
        }
    }

    public enum AttackMode {
        Tick,
        Cps
    }

    public enum Page {
        ONE,
        TWO
    }

    public enum Modes {
        Closest,
        Priority,
        Switch
    }

    public enum Render {
        Circle,
        Novo,
        New,
        Off
    }

}