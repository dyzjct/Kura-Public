package me.dyzjct.kura.module.modules.crystalaura.CrystalHelper

import me.dyzjct.kura.module.Module
import me.dyzjct.kura.module.modules.crystalaura.CrystalHelper.FastRayTrace.Companion.rayTraceVisible
import me.dyzjct.kura.utils.animations.fastFloor
import me.dyzjct.kura.utils.animations.sq
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.init.Blocks
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.sqrt

class CrystalHelper : Module() {
    val EntityEnderCrystal.blockPos: BlockPos
        get() = BlockPos(this.posX.fastFloor(), this.posY.fastFloor() - 1, this.posZ.fastFloor())

    companion object {
        @JvmStatic
        val EntityLivingBase.scaledHealth: Float
            get() = this.health + this.absorptionAmount * (this.health / this.maxHealth)

        @JvmStatic
        val EntityLivingBase.totalHealth: Float
            get() = this.health + this.absorptionAmount


        @JvmStatic
        fun checkBreakRange(
            entity: EntityEnderCrystal,
            breakRange: Float,
            wallRange: Float,
            attempt: Int,
            mutableBlockPos: BlockPos.MutableBlockPos
        ): Boolean {
            return checkBreakRange(
                entity.posX,
                entity.posY,
                entity.posZ,
                breakRange,
                wallRange,
                attempt,
                mutableBlockPos
            )
        }

        @JvmStatic
        fun checkBreakRange(
            x: Double,
            y: Double,
            z: Double,
            breakRange: Float,
            wallRange: Float,
            attempt: Int,
            mutableBlockPos: BlockPos.MutableBlockPos
        ): Boolean {
            return mc.player.getDistanceSq(x, y, z) <= breakRange.sq
                    && (mc.player.getDistanceSq(x, y, z) <= wallRange.sq || rayTraceVisible(
                mc.player.positionVector.add(0.0, mc.player.eyeHeight.toDouble(), 0.0),
                x,
                y + 1.7,
                z,
                attempt,
                mutableBlockPos
            ))
        }

        @JvmStatic
        fun isValidPos(
            newPlacement: Boolean,
            breakRange: Float,
            wallRange: Float,
            attempt: Int,
            pos: BlockPos,
            entity: EntityLivingBase,
            mutableBlockPos: BlockPos.MutableBlockPos
        ): Boolean {
            if (!isPlaceable(pos, newPlacement, mutableBlockPos)) {
                return false
            }

            val minX = pos.x + 0.001
            val minY = pos.y + 1.0
            val minZ = pos.z + 0.001
            val maxX = pos.x + 0.999
            val maxY = pos.y + 3.0
            val maxZ = pos.z + 0.999

            if (entity.isEntityAlive && entity.entityBoundingBox.intersects(minX, minY, minZ, maxX, maxY, maxZ)) {
                if (entity !is EntityEnderCrystal) return false
                if (!checkBreakRange(entity, breakRange, wallRange, attempt, mutableBlockPos)) return false
            }

            return true
        }

        @JvmStatic
        fun isPlaceable(pos: BlockPos, newPlacement: Boolean, mutableBlockPos: BlockPos.MutableBlockPos): Boolean {
            if (!canPlaceCrystalOn(pos)) {
                return false
            }
            val posUp = mutableBlockPos.setAndAdd(pos, 0, 1, 0)
            return if (newPlacement) {
                mc.world.isAirBlock(posUp)
            } else {
                isValidMaterial(mc.world.getBlockState(posUp)) && isValidMaterial(
                    mc.world.getBlockState(
                        posUp.add(
                            0,
                            1,
                            0
                        )
                    )
                )
            }
        }

        @JvmStatic
        fun BlockPos.MutableBlockPos.setAndAdd(set: BlockPos, add: BlockPos): BlockPos.MutableBlockPos {
            return this.setPos(set.x + add.x, set.y + add.y, set.z + add.z)
        }

        @JvmStatic
        fun BlockPos.MutableBlockPos.setAndAdd(set: BlockPos, x: Int, y: Int, z: Int): BlockPos.MutableBlockPos {
            return this.setPos(set.x + x, set.y + y, set.z + z)
        }

        @JvmStatic
        private val mutableBlockPos = ThreadLocal.withInitial {
            BlockPos.MutableBlockPos()
        }

        @JvmStatic
                /** Checks colliding with blocks and given entity */
        fun canPlaceCrystal(pos: BlockPos, entity: EntityLivingBase? = null): Boolean {
            return canPlaceCrystalOn(pos)
                    && (entity == null || !getCrystalPlacingBB(pos).intersects(entity.entityBoundingBox))
                    && hasValidSpaceForCrystal(pos)
        }

        @JvmStatic
                /** Checks if the block is valid for placing crystal */
        fun canPlaceCrystalOn(pos: BlockPos): Boolean {
            val block = mc.world.getBlockState(pos).block
            return block == Blocks.BEDROCK || block == Blocks.OBSIDIAN
        }

        @JvmStatic
        fun hasValidSpaceForCrystal(pos: BlockPos): Boolean {
            val mutableBlockPos = mutableBlockPos.get()
            return isValidMaterial(mc.world.getBlockState(mutableBlockPos.setAndAdd(pos, 0, 1, 0)))
                    && isValidMaterial(mc.world.getBlockState(mutableBlockPos.add(0, 1, 0)))
        }

        @JvmStatic
        fun isValidMaterial(blockState: IBlockState): Boolean {
            return !blockState.material.isLiquid && blockState.material.isReplaceable
        }

        @JvmStatic
        fun isReplaceable(block: Block): Boolean {
            return block === Blocks.FIRE || block === Blocks.DOUBLE_PLANT || block === Blocks.VINE
        }

        @JvmStatic
        fun getVecDistance(a: BlockPos, posX: Double, posY: Double, posZ: Double): Double {
            val x1 = a.getX() - posX
            val y1 = a.getY() - posY
            val z1 = a.getZ() - posZ
            return sqrt(x1 * x1 + y1 * y1 + z1 * z1)
        }

        @JvmStatic
        fun getVecDistance(pos: BlockPos, entity: Entity): Double {
            return getVecDistance(pos, entity.posX, entity.posY, entity.posZ)
        }

        @JvmStatic
        fun EntityLivingBase.getMinArmorRate(): Int {
            return this.armorInventoryList.toList().asSequence()
                .filter { it.isItemStackDamageable }
                .map { ((it.maxDamage - it.itemDamage) * 100.0f / it.maxDamage.toFloat()).toInt() }
                .maxOrNull() ?: 0
        }

        @JvmStatic
        fun shouldForcePlace(
            entity: EntityLivingBase,
            forcePlaceHealth: Float,
            forcePlaceArmorRate: Float,
            forcePlaceMotion: Float
        ): Boolean {
            return (entity.totalHealth <= forcePlaceHealth
                    || entity.getMinArmorRate() <= forcePlaceArmorRate
                    || entity.realSpeed >= forcePlaceMotion)
        }

        private inline val Entity.realSpeed get() = hypot(posX - prevPosX, posZ - prevPosZ)

        @JvmStatic
        fun shouldForcePlace(entity: EntityLivingBase, forcePlaceHealth: Float): Boolean {
            return (entity.health + entity.absorptionAmount) <= forcePlaceHealth
        }

        @JvmStatic
        fun normalizeAngle(angleIn: Double): Double {
            var angle = angleIn
            angle %= 360.0
            if (angle >= 180.0) {
                angle -= 360.0
            }
            if (angle < -180.0) {
                angle += 360.0
            }
            return angle
        }

        @JvmStatic
        fun normalizeAngle(angleIn: Float): Float {
            var angle = angleIn
            angle %= 360f
            if (angle >= 180f) {
                angle -= 360f
            }
            if (angle < -180f) {
                angle += 360f
            }
            return angle
        }

        @JvmStatic
        fun placeBoxIntersectsCrystalBox(placePos: BlockPos, crystalPos: BlockPos): Boolean {
            return crystalPos.y - placePos.y in 0..2
                    && abs(crystalPos.x - placePos.x) < 2
                    && abs(crystalPos.z - placePos.z) < 2
        }

        @JvmStatic
        fun placeBoxIntersectsCrystalBox(placePos: Vec3d, crystalPos: BlockPos): Boolean {
            return crystalPos.y - placePos.y in 0.0..2.0
                    && abs(crystalPos.x - placePos.x) < 2.0
                    && abs(crystalPos.z - placePos.z) < 2.0
        }

        @JvmStatic
        fun placeBoxIntersectsCrystalBox(
            placeX: Double,
            placeY: Double,
            placeZ: Double,
            crystalPos: BlockPos
        ): Boolean {
            return crystalPos.y - placeY in 0.0..2.0
                    && abs(crystalPos.x - placeX) < 2.0
                    && abs(crystalPos.z - placeZ) < 2.0
        }

        @JvmStatic
        fun placeBoxIntersectsCrystalBox(
            placeX: Double,
            placeY: Double,
            placeZ: Double,
            crystalX: Double,
            crystalY: Double,
            crystalZ: Double
        ): Boolean {
            return crystalY - placeY in 0.0..2.0
                    && abs(crystalX - placeX) < 2.0
                    && abs(crystalZ - placeZ) < 2.0
        }

        @JvmStatic
        fun canPlaceCollide(pos: BlockPos): Boolean {
            val placingBB = getCrystalPlacingBB(pos.up())
            return mc.world?.let { world ->
                world.getEntitiesWithinAABBExcludingEntity(null, placingBB).all {
                    it.isDead || it is EntityLivingBase && it.health <= 0.0f
                }
            } ?: false
        }

        @JvmStatic
        fun getCrystalPlacingBB(pos: BlockPos): AxisAlignedBB {
            return getCrystalPlacingBB(pos.x, pos.y, pos.z)
        }

        @JvmStatic
        fun getCrystalPlacingBB(x: Int, y: Int, z: Int): AxisAlignedBB {
            return AxisAlignedBB(
                x + 0.001, y + 1.0, z + 0.001,
                x + 0.999, y + 3.0, z + 0.999
            )
        }

        @JvmStatic
        fun getCrystalPlacingBB(pos: Vec3d): AxisAlignedBB {
            return getCrystalPlacingBB(pos.x, pos.y, pos.z)
        }

        @JvmStatic
        fun getCrystalPlacingBB(x: Double, y: Double, z: Double): AxisAlignedBB {
            return AxisAlignedBB(
                x - 0.499, y, z - 0.499,
                x + 0.499, y + 2.0, z + 0.499
            )
        }

        @JvmStatic
        fun getCrystalBB(pos: BlockPos): AxisAlignedBB {
            return getCrystalBB(pos.x, pos.y, pos.z)
        }

        @JvmStatic
        fun getCrystalBB(x: Int, y: Int, z: Int): AxisAlignedBB {
            return AxisAlignedBB(
                x - 0.5, y + 1.0, z - 0.5,
                x + 1.5, y + 3.0, z + 1.5
            )
        }

        @JvmStatic
        fun PredictionHandler(target: Entity): Vec3d {
            val motionX = (target.posX - target.lastTickPosX).coerceIn(-0.6, 0.6)
            val motionY = (target.posY - target.lastTickPosY).coerceIn(-0.5, 0.5)
            val motionZ = (target.posZ - target.lastTickPosZ).coerceIn(-0.6, 0.6)
            val posX: Double = target.posX + (motionX * 0.6f)
            val posY: Double = target.posY + (motionY * 0.5f)
            val posZ: Double = target.posZ + (motionZ * 0.6f)
            return Vec3d(posX, posY, posZ)
        }

        @JvmStatic
        fun PredictionHandlerNew(target: Entity, ticks: Int): Vec3d {
            val motionX = (target.posX - target.lastTickPosX).coerceIn(-0.6, 0.6)
            val motionY = (target.posY - target.lastTickPosY).coerceIn(-0.5, 0.5)
            val motionZ = (target.posZ - target.lastTickPosZ).coerceIn(-0.6, 0.6)
            val entityBox = target.entityBoundingBox
            var targetBox = entityBox
            for (tick in 0..ticks) {
                targetBox = canMove(targetBox, motionX, motionY, motionZ)
                    ?: canMove(targetBox, motionX, 0.0, motionZ)
                            ?: canMove(targetBox, 0.0, motionY, 0.0)
                            ?: break
            }
            val offsetX = targetBox.minX - entityBox.minX
            val offsetY = targetBox.minY - entityBox.minY
            val offsetZ = targetBox.minZ - entityBox.minZ
            return if (ticks > 0) {
                Vec3d(offsetX, offsetY, offsetZ)
            } else {
                Vec3d(motionX, motionY, motionZ)
            }
        }

        @JvmStatic
        fun canMove(box: AxisAlignedBB, x: Double, y: Double, z: Double): AxisAlignedBB? {
            return box.offset(x, y, z).takeIf { !mc.world.collidesWithAnyBlock(it) }
        }
    }
}