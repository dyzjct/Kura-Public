package me.windyteam.kura.mixin.client;

import net.minecraft.world.Explosion;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {Explosion.class}, priority = Integer.MAX_VALUE)
public class MixinExplosion {
    /*
    @Final
    @Shadow
    public WorldClient world;
    @Final
    @Shadow
    public double x;
    @Final
    @Shadow
    public double y;
    @Final
    @Shadow
    public double z;
    @Final
    @Shadow
    public float size;
    @Final
    @Shadow
    public boolean damagesTerrain;
    @Final
    @Shadow
    public List<BlockPos> affectedBlockPositions;
    @Final
    @Shadow
    public boolean causesFire;
    @Final
    @Shadow
    public Random random;
    @Final
    @Shadow
    public Entity exploder;
    @Overwrite
    public void doExplosionB(boolean spawnParticles) {
        try {
            this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 0f, 0f);

            if (this.size >= 2.0F && this.damagesTerrain) {
                this.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
            } else {
                this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
            }

            if (this.damagesTerrain) {
                for (BlockPos blockpos : this.affectedBlockPositions) {
                    IBlockState iblockstate = this.world.getBlockState(blockpos);
                    Block block = iblockstate.getBlock();

                    if (spawnParticles) {
                        double d0 = (float) blockpos.getX() + this.world.rand.nextFloat();
                        double d1 = (float) blockpos.getY() + this.world.rand.nextFloat();
                        double d2 = (float) blockpos.getZ() + this.world.rand.nextFloat();
                        double d3 = d0 - this.x;
                        double d4 = d1 - this.y;
                        double d5 = d2 - this.z;
                        double d6 = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                        d3 = d3 / d6;
                        d4 = d4 / d6;
                        d5 = d5 / d6;
                        double d7 = 0.5D / (d6 / (double) this.size + 0.1D);
                        d7 = d7 * (double) (this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
                        d3 = d3 * d7;
                        d4 = d4 * d7;
                        d5 = d5 * d7;
                        this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + this.x) / 2.0D, (d1 + this.y) / 2.0D, (d2 + this.z) / 2.0D, d3, d4, d5);
                        this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5);
                    }

                    if (iblockstate.getMaterial() != Material.AIR) {
                        if (block.canDropFromExplosion(explosion)) {
                            block.dropBlockAsItemWithChance(this.world, blockpos, this.world.getBlockState(blockpos), 1.0F / this.size, 0);
                        }

                        block.onBlockExploded(this.world, blockpos, explosion);
                    }
                }
            }

            if (this.causesFire) {
                for (BlockPos blockpos1 : this.affectedBlockPositions) {
                    if (this.world.getBlockState(blockpos1).getMaterial() == Material.AIR && this.world.getBlockState(blockpos1.down()).isFullBlock() && this.random.nextInt(3) == 0) {
                        this.world.setBlockState(blockpos1, Blocks.FIRE.getDefaultState());
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
    */
}
