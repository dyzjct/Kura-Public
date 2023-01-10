package me.windyteam.kura.utils.entity;

import me.windyteam.kura.utils.Wrapper;
import me.windyteam.kura.utils.inventory.InventoryUtil;
import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class CrystalUtil {

    static Minecraft mc = Minecraft.getMinecraft();
    private static final AtomicBoolean tickOngoing = new AtomicBoolean(false);

    public static boolean canSeeBlock(BlockPos p_Pos) {
        return mc.player == null || mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(p_Pos.getX(), p_Pos.getY(), p_Pos.getZ()), false, true, false) != null;
    }

    public static List<BlockPos> possiblePlacePositions(float placeRange, boolean thirteen, boolean specialEntityCheck) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphereVec(getPlayerPos(mc.player), placeRange, (int) placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystal(pos, thirteen, specialEntityCheck)).collect(Collectors.toList()));
        return positions;
    }

    public static List<BlockPos> getSphere(BlockPos loc, double r, double h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.x;
        int cy = loc.y;
        int cz = loc.z;
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static List<BlockPos> getSphere(Vec3d loc, double r, double h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = (int) loc.x;
        int cy = (int) loc.y;
        int cz = (int) loc.z;
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static List<BlockPos> getSphereVec(Vec3d loc, double r, double h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = (int) loc.x;
        int cy = (int) loc.y;
        int cz = (int) loc.z;
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static void targetHUD(Entity entity) {
        boolean b = Wrapper.getMinecraft().getRenderManager().options.thirdPersonView == 2;
        float playerViewY = Wrapper.getMinecraft().getRenderManager().playerViewY;
        GlStateManager.pushMatrix();
        Vec3d interpolatedPos = EntityUtil.getInterpolatedPos(entity, mc.getRenderPartialTicks());
        GlStateManager.translate(interpolatedPos.x - mc.getRenderManager().renderPosX, interpolatedPos.y - mc.getRenderManager().renderPosY, interpolatedPos.z - mc.getRenderManager().renderPosZ);
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate((float) (b ? -1 : 1), 1.0f, 0.0f, 0.0f);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        if (entity instanceof EntityPlayer) {
            GL11.glColor3f(1.0f, 0.1f, 0.1f);
        } else if (EntityUtil.isPassive(entity)) {
            GL11.glColor3f(0.11f, 0.9f, 0.11f);
        } else {
            GL11.glColor3f(0.9f, 0.1f, 0.1f);
        }
        GlStateManager.disableTexture2D();
        GL11.glLineWidth(2.0f);
        GL11.glEnable(2848);
        GL11.glBegin(2);
        GL11.glVertex2d(-entity.width / 2.0f, 0.0);
        GL11.glVertex2d(-entity.width / 2.0f, entity.height);
        GL11.glVertex2d(entity.width / 2.0f, entity.height);
        GL11.glVertex2d(entity.width / 2.0f, 0.0);
        GL11.glEnd();
        GlStateManager.popMatrix();
    }

    public static void targetHUD(EntityLivingBase player, int color, float width) {
        targetHUD(player, color, color, color, color, width);
    }

    public static void targetHUD(EntityLivingBase entity, float red, float green, float blue, float alpha, float width) {
        boolean b = Wrapper.getMinecraft().getRenderManager().options.thirdPersonView == 2;
        float playerViewY = Wrapper.getMinecraft().getRenderManager().playerViewY;
        GlStateManager.pushMatrix();
        Vec3d interpolatedPos = EntityUtil.getInterpolatedPos(entity, mc.getRenderPartialTicks());
        GlStateManager.translate(interpolatedPos.x - mc.getRenderManager().renderPosX, interpolatedPos.y - mc.getRenderManager().renderPosY, interpolatedPos.z - mc.getRenderManager().renderPosZ);
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate((float) (b ? -1 : 1), 1.0f, 0.0f, 0.0f);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GL11.glColor4f(red / 255f, green / 255f, blue / 255f, alpha / 255f);
        GlStateManager.disableTexture2D();
        GL11.glLineWidth(width);
        GL11.glEnable(2848);
        GL11.glBegin(2);
        GL11.glVertex2d(-entity.width, 0.0);
        GL11.glVertex2d(-entity.width, entity.height);
        GL11.glVertex2d(entity.width, entity.height);
        GL11.glVertex2d(entity.width, 0.0);
        GL11.glEnd();
        GlStateManager.popMatrix();
    }

//    public static Vec3d getPlayerPos(EntityPlayer player) {
//        return new Vec3d(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
//    }

    public static float calculateDamages(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0f;
        double distancedsize = entity.getDistance(posX, posY, posZ) / doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0;
        try {
            blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        } catch (Exception ignored) {
        }
        double v = (1.0 - distancedsize) * blockDensity;
        float damage = (float) (int) ((v * v + v) / 2.0 * 7.0 * doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float) finald;
    }

    public static Vec3d getPlayerPos(EntityPlayer player) {
        return new Vec3d(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
    }

    public static boolean canPlaceCrystal(BlockPos blockPos, boolean thirteen, boolean specialEntityCheck) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        BlockPos final_boost = blockPos.add(0, 3, 0);
        try {
            if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
            if ((mc.world.getBlockState(boost).getBlock() != Blocks.AIR || (mc.world.getBlockState(boost2).getBlock() != Blocks.AIR && !thirteen))) {
                return false;
            }
            if (!specialEntityCheck) {
                return mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
            }
            for (Object entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                if (!(entity instanceof EntityEnderCrystal)) {
                    return false;
                }
            }
            for (Object entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                if (!(entity instanceof EntityEnderCrystal)) {
                    return false;
                }
            }
            for (Object entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(final_boost))) {
                if (entity instanceof EntityEnderCrystal) {
                    return false;
                }
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0f;
        double distancedsize = entity.getDistance(posX, posY, posZ) / doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0;
        try {
            blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        } catch (Exception ignored) {
        }
        double v = (1.0 - distancedsize) * blockDensity;
        float damage = (float) (int) ((v * v + v) / 2.0 * 7.0 * doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float) finald;
    }

    public static float calculateDamage(EntityEnderCrystal crystal, Entity entity) {
        return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }

    public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage = damageI;
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = 0;
            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            } catch (Exception ignored) {
            }
            float f = MathHelper.clamp((float) k, 0.0f, 20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0f;
            }
            damage = Math.max(damage, 0.0f);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    public static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * ((diff == 0) ? 0.0f : ((diff == 2) ? 1.0f : ((diff == 1) ? 0.5f : 1.5f)));
    }

    public static boolean ticksOngoing() {
        return tickOngoing.get();
    }

    public static boolean canPlace(BlockPos pos) {
        if (!(mc.world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockEmptyDrops)) {
            return false;
        }
        if (!(mc.world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockObsidian)) {
            return false;
        }
        return mc.world.checkNoEntityCollision(new AxisAlignedBB(0, 0, 0, 1, 2, 1).offset(pos), null);
    }

    public static EnumActionResult doPlace(BlockPos pos) {
        double dx = (pos.getX() + 0.5 - mc.player.posX);
        double dy = (pos.getY() - 1 + 0.5 - mc.player.posY) - .5 - mc.player.getEyeHeight();
        double dz = (pos.getZ() + 0.5 - mc.player.posZ);

        double x = getDirection2D(dz, dx);
        double y = getDirection2D(dy, Math.sqrt(dx * dx + dz * dz));

        Vec3d vec = getVectorForRotation(-y, x - 90);
        return mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(EnumFacing.DOWN), EnumFacing.UP, vec, mc.player.getActiveHand());
    }

    protected static final double getDirection2D(double dx, double dy) {
        double d;
        if (dy == 0) {
            if (dx > 0) {
                d = 90;
            } else {
                d = -90;
            }
        } else {
            d = Math.atan(dx / dy) * 57.2957796;
            if (dy < 0) {
                if (dx > 0) {
                    d += 180;
                } else {
                    if (dx < 0) {
                        d -= 180;
                    } else {
                        d = 180;
                    }
                }
            }
        }
        return d;
    }

    protected static Vec3d getVectorForRotation(double pitch, double yaw) {
        float f = MathHelper.cos((float) (-yaw * 0.017453292F - (float) Math.PI));
        float f1 = MathHelper.sin((float) (-yaw * 0.017453292F - (float) Math.PI));
        float f2 = -MathHelper.cos((float) (-pitch * 0.017453292F));
        float f3 = MathHelper.sin((float) (-pitch * 0.017453292F));
        return new Vec3d(f1 * f2, f3, f * f2);
    }

    public static EnumActionResult placeCrystal(BlockPos pos) {
        pos.offset(EnumFacing.DOWN);
        double dx = (pos.getX() + 0.5 - mc.player.posX);
        double dy = (pos.getY() + 0.5 - mc.player.posY) - .5 - mc.player.getEyeHeight();
        double dz = (pos.getZ() + 0.5 - mc.player.posZ);

        double x = getDirection2D(dz, dx);
        double y = getDirection2D(dy, Math.sqrt(dx * dx + dz * dz));

        Vec3d vec = getVectorForRotation(-y, x - 90);
        if (mc.player.inventory.offHandInventory.get(0).getItem().getClass().equals(Item.getItemById(426).getClass())) {
            return mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(EnumFacing.DOWN), EnumFacing.UP, vec, EnumHand.OFF_HAND);
        } else if (InventoryUtil.pickItem(426, false) != -1) {
            InventoryUtil.setSlot(InventoryUtil.pickItem(426, false));
            return mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(EnumFacing.DOWN), EnumFacing.UP, vec, EnumHand.MAIN_HAND);
        }
        return EnumActionResult.FAIL;
    }

    public static boolean placeCrystalSilent(BlockPos pos) {
        pos.offset(EnumFacing.DOWN);
        double dx = (pos.getX() + 0.5 - mc.player.posX);
        double dy = (pos.getY() + 0.5 - mc.player.posY) - .5 - mc.player.getEyeHeight();
        double dz = (pos.getZ() + 0.5 - mc.player.posZ);

        double x = getDirection2D(dz, dx);
        double y = getDirection2D(dy, Math.sqrt(dx * dx + dz * dz));
        int slot = InventoryUtil.pickItem(426, false);
        if (slot == -1 && mc.player.inventory.offHandInventory.get(0).getItem() != Items.END_CRYSTAL) return false;

        Vec3d vec = getVectorForRotation(-y, x - 90);
        if (mc.player.inventory.offHandInventory.get(0).getItem() == Items.END_CRYSTAL) {
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.offset(EnumFacing.DOWN), EnumFacing.UP, EnumHand.OFF_HAND, 0, 0, 0));
        } else if (InventoryUtil.pickItem(426, false) != -1) {
            mc.getConnection().sendPacket(new CPacketHeldItemChange(slot));
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.offset(EnumFacing.DOWN), EnumFacing.UP, EnumHand.MAIN_HAND, 0, 0, 0));
        }
        return true;
    }

    public static double getDamage(Vec3d pos, Entity target) {
        Entity entity = target == null ? mc.player : target;
        float damage = 6.0F;
        float f3 = damage * 2.0F;
        Vec3d vec3d = pos;

        if (!entity.isImmuneToExplosions()) {
            double d12 = entity.getDistance(pos.x, pos.y, pos.z) / (double) f3;

            if (d12 <= 1.0D) {
                double d5 = entity.posX - pos.x;
                double d7 = entity.posY + (double) entity.getEyeHeight() - pos.y;
                double d9 = entity.posZ - pos.z;
                double d13 = MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);

                if (d13 != 0.0D) {
                    d5 = d5 / d13;
                    d7 = d7 / d13;
                    d9 = d9 / d13;
                    double d14 = mc.world.getBlockDensity(pos, entity.getEntityBoundingBox());
                    double d10 = (1.0D - d12) * d14;
                    return (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f3 + 1.0D));
                }
            }
        }
        return 0;
    }

}
