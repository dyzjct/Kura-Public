package me.windyteam.kura.utils.math;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

public class DamageUtil {
    final static Minecraft mc = Minecraft.getMinecraft();

    /**
     * Convenience method, calls {@link DamageUtil#calculate(Entity, EntityLivingBase)}
     * for mc.player.
     */
    public static float calculate(Entity crystal) {
        return calculate(crystal.posX, crystal.posY, crystal.posZ, mc.player);
    }

    /**
     * Convenience method, calls {@link DamageUtil#calculate(BlockPos, EntityLivingBase)}
     * for mc.player.
     */
    public static float calculate(BlockPos pos) {
        return calculate(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player);
    }

    /**
     * Convenience method, calls {@link DamageUtil#calculate(double, double, double, EntityLivingBase)}
     * for for the block pos, x + 0.5, y + 1, z + 0.5;
     */
    public static float calculate(BlockPos pos, EntityLivingBase base) {
        return calculate(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, base);
    }

    /**
     * Convenience method, calls {@link DamageUtil#calculate(double, double, double, EntityLivingBase)}
     * for for the entities position.
     */
    public static float calculate(Entity crystal, EntityLivingBase base) {
        return calculate(crystal.posX, crystal.posY, crystal.posZ, base);
    }

    /**
     * Calculates the damage an explosion of size 6.0 (Endcrystal) would deal
     * to the targeted EntityLivingBase. Note that beds(0.5, 0.5, 0.5) explode
     * with a different offset than crystals(0.5, 1, 0.5) at their headpiece.
     * Beds also don't create the same explosion size, but we ignore that for
     * the sake of minDamage settings being the same for all calculations.
     * (FeetPlace does same damage anyways).
     *
     * @param x    the x coordinate of the position.
     * @param y    the y coordinate of the position.
     * @param z    the z coordinate of the position.
     * @param base the targeted entity.
     */
    public static float calculate(double x, double y, double z, EntityLivingBase base) {
        double distance = base.getDistance(x, y, z) / 12.0D;
        if (distance > 1.0D) {
            return 0.0F;
        }
        return 0;
    }

    public static boolean isArmorLow(EntityPlayer player, int durability) {
        for (ItemStack piece : player.inventory.armorInventory) {
            if (piece == null)
                return true;
            if (getItemDamage(piece) < durability)
                return true;
        }
        return false;
    }

    public static boolean isNaked(EntityPlayer player) {
        for (ItemStack piece : player.inventory.armorInventory) {
            if (piece == null || piece.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public static int getItemDamage(ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage();
    }

    public static float getDamageInPercent(ItemStack stack) {
        return getItemDamage(stack) / stack.getMaxDamage() * 100.0F;
    }

    public static int getRoundedDamage(ItemStack stack) {
        return (int) getDamageInPercent(stack);
    }

    public static boolean hasDurability(ItemStack stack) {
        Item item = stack.getItem();
        return (item instanceof net.minecraft.item.ItemArmor || item instanceof net.minecraft.item.ItemSword || item instanceof net.minecraft.item.ItemTool || item instanceof net.minecraft.item.ItemShield);
    }

    public static boolean canBreakWeakness(EntityPlayer player) {
        int strengthAmp = 0;
        PotionEffect effect = mc.player.getActivePotionEffect(MobEffects.STRENGTH);
        if (effect != null)
            strengthAmp = effect.getAmplifier();
        return (!mc.player.isPotionActive(MobEffects.WEAKNESS) || strengthAmp >= 1 || mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemSword || mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemPickaxe || mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemAxe || mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemSpade);
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0F;
        double distancedsize = entity.getDistance(posX, posY, posZ) / doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0D;
        try {
            blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        } catch (Exception exception) {
        }
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (int) ((v * v + v) / 2.0D * 7.0D * doubleExplosionSize + 1.0D);
        double finald = 1.0D;
        if (entity instanceof EntityLivingBase)
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0F, false, true));
        return (float) finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage = damageI;
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = 0;
            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            } catch (Exception exception) {
            }
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;
            if (entity.isPotionActive(MobEffects.RESISTANCE))
                damage -= damage / 4.0F;
            damage = Math.max(damage, 0.0F);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    public static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * ((diff == 0) ? 0.0F : ((diff == 2) ? 1.0F : ((diff == 1) ? 0.5F : 1.5F)));
    }

    public static float calculateDamageAlt(Entity crystal, Entity entity) {
        BlockPos cPos = new BlockPos(crystal.posX, crystal.posY, crystal.posZ);
        return calculateDamage(cPos.getX(), cPos.getY(), cPos.getZ(), entity);
    }

    public static float calculateDamage(Entity crystal, Entity entity) {
        return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }

    public static float calculateDamage(BlockPos pos, Entity entity) {
        return calculateDamage(pos.getX() + 0.5D, (pos.getY() + 1), pos.getZ() + 0.5D, entity);
    }

    public static boolean canTakeDamage(boolean suicide) {
        return (!mc.player.capabilities.isCreativeMode && !suicide);
    }

    public static int getCooldownByWeapon(EntityPlayer player) {
        Item item = player.getHeldItemMainhand().getItem();
        if (item instanceof net.minecraft.item.ItemSword)
            return 600;
        if (item instanceof net.minecraft.item.ItemPickaxe)
            return 850;
        if (item == Items.IRON_AXE)
            return 1100;
        if (item == Items.STONE_HOE)
            return 500;
        if (item == Items.IRON_HOE)
            return 350;
        if (item == Items.WOODEN_AXE || item == Items.STONE_AXE)
            return 1250;
        if (item instanceof net.minecraft.item.ItemSpade || item == Items.GOLDEN_AXE || item == Items.DIAMOND_AXE || item == Items.WOODEN_HOE || item == Items.GOLDEN_HOE)
            return 1000;
        return 250;
    }
}
