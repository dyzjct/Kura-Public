package me.dyzjct.kura.module.modules.crystalaura.CrystalHelper

import me.dyzjct.kura.module.Module
import me.dyzjct.kura.module.modules.crystalaura.CrystalHelper.FastRayTrace.Companion.fastRaytrace
import me.dyzjct.kura.utils.animations.fastFloor
import net.minecraft.block.state.IBlockState
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.MobEffects
import net.minecraft.util.CombatRules
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.EnumDifficulty
import java.util.*
import kotlin.math.max
import kotlin.math.min

class CrystalDamageCalculator : Module() {

    companion object {

        val reductionMap: MutableMap<EntityLivingBase, DamageReduction> = Collections.synchronizedMap(WeakHashMap())

        class DamageReduction(entity: EntityLivingBase) {
            private val armorValue: Float = entity.totalArmorValue.toFloat()
            private val toughness: Float
            private val resistance: Float
            private val blastReduction: Float

            init {
                toughness = entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).attributeValue
                    .toFloat()
                val potionEffect = entity.getActivePotionEffect(MobEffects.RESISTANCE)
                resistance = if (potionEffect != null) max(1.0f - (potionEffect.amplifier + 1) * 0.2f, 0.0f) else 1.0f
                blastReduction = 1.0f - min(calcTotalEPF(entity), 20) / 25.0f
            }

            fun calcReductionDamage(damage: Float): Float {
                return CombatRules.getDamageAfterAbsorb(damage, armorValue, toughness) *
                        resistance *
                        blastReduction
            }

            companion object {
                private fun calcTotalEPF(entity: EntityLivingBase): Int {
                    var epf = 0
                    for (itemStack in entity.armorInventoryList) {
                        val nbtTagList = itemStack.enchantmentTagList
                        for (i in 0 until nbtTagList.tagCount()) {
                            val nbtTagCompound = nbtTagList.getCompoundTagAt(i)
                            val id = nbtTagCompound.getInteger("id")
                            val level = nbtTagCompound.getShort("lvl").toInt()
                            if (id == 0) {
                                // Protection
                                epf += level
                            } else if (id == 3) {
                                // Blast protection
                                epf += level * 2
                            }
                        }
                    }
                    return epf
                }
            }
        }

        private const val DOUBLE_SIZE = 12.0f
        private const val DAMAGE_FACTOR = 42.0f

        fun calcDamage(
            entity: EntityLivingBase,
            entityPos: Vec3d,
            entityBox: AxisAlignedBB,
            crystalX: Double,
            crystalY: Double,
            crystalZ: Double,
            mutableBlockPos: BlockPos.MutableBlockPos
        ): Float {
            val isPlayer = entity is EntityPlayer
            if (isPlayer && mc.world.difficulty == EnumDifficulty.PEACEFUL) return 0.0f
            var damage: Float

            damage = if (isPlayer
                && crystalY - entityPos.y > 1.5652173822904127
                && isResistant(
                    mc.world.getBlockState(
                        mutableBlockPos.setPos(
                            crystalX.fastFloor(),
                            crystalY.fastFloor() - 1,
                            crystalZ.fastFloor()
                        )
                    )
                )
            ) {
                1.0f
            } else {
                calcRawDamage(entityPos, entityBox, crystalX, crystalY, crystalZ, mutableBlockPos)
            }

            if (isPlayer) damage = calcDifficultyDamage(mc.world, damage)
            return calcReductionDamage(entity, damage)
        }

        private fun calcRawDamage(
            entityPos: Vec3d,
            entityBox: AxisAlignedBB,
            posX: Double,
            posY: Double,
            posZ: Double,
            mutableBlockPos: BlockPos.MutableBlockPos
        ): Float {
            val scaledDist = entityPos.distanceTo(Vec3d(posX, posY, posZ)).toFloat() / DOUBLE_SIZE
            if (scaledDist > 1.0f) return 0.0f

            val factor = (1.0f - scaledDist) * getExposureAmount(entityBox, posX, posY, posZ, mutableBlockPos)
            return ((factor * factor + factor) * DAMAGE_FACTOR + 1.0f)
        }

        private val function: (BlockPos, IBlockState) -> FastRayTrace.Companion.FastRayTraceAction = { _, blockState ->
            if (blockState.block != Blocks.AIR && isResistant(blockState)) {
                FastRayTrace.Companion.FastRayTraceAction.CALC
            } else {
                FastRayTrace.Companion.FastRayTraceAction.SKIP
            }
        }

        private fun getExposureAmount(
            entityBox: AxisAlignedBB,
            posX: Double,
            posY: Double,
            posZ: Double,
            mutableBlockPos: BlockPos.MutableBlockPos
        ): Float {
            val width = entityBox.maxX - entityBox.minX
            val height = entityBox.maxY - entityBox.minY

            val gridMultiplierXZ = 1.0 / (width * 2.0 + 1.0)
            val gridMultiplierY = 1.0 / (height * 2.0 + 1.0)

            val gridXZ = width * gridMultiplierXZ
            val gridY = height * gridMultiplierY

            val sizeXZ = (1.0 / gridMultiplierXZ).fastFloor()
            val sizeY = (1.0 / gridMultiplierY).fastFloor()
            val xzOffset = (1.0 - gridMultiplierXZ * (sizeXZ)) / 2.0

            var total = 0
            var count = 0

            for (yIndex in 0..sizeY) {
                for (xIndex in 0..sizeXZ) {
                    for (zIndex in 0..sizeXZ) {
                        val x = gridXZ * xIndex + xzOffset + entityBox.minX
                        val y = gridY * yIndex + entityBox.minY
                        val z = gridXZ * zIndex + xzOffset + entityBox.minZ

                        total++
                        if (!fastRaytrace(x, y, z, posX, posY, posZ, 20, mutableBlockPos, function)) {
                            count++
                        }
                    }
                }
            }

            return count.toFloat() / total.toFloat()
        }

        private fun calcReductionDamage(entity: EntityLivingBase, damage: Float): Float {
            val reduction = reductionMap[entity]
            return reduction?.calcReductionDamage(damage) ?: damage
        }

        private fun calcDifficultyDamage(world: WorldClient, damage: Float): Float {
            return when (world.difficulty) {
                EnumDifficulty.PEACEFUL -> 0.0f
                EnumDifficulty.EASY -> min(damage * 0.5f + 1.0f, damage)
                EnumDifficulty.HARD -> damage * 1.5f
                else -> damage
            }
        }

        private fun isResistant(blockState: IBlockState) =
            !blockState.material.isLiquid && blockState.block.getExplosionResistance(null) >= 19.7
    }
}