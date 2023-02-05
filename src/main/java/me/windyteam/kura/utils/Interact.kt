package me.windyteam.kura.utils

import kura.utils.eyePosition
import me.windyteam.kura.utils.vector.VectorUtils.toVec3dCenter
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import java.util.*

private val mc = Minecraft.getMinecraft()
fun getMiningSide(pos: BlockPos): EnumFacing? {
    val eyePos = mc.player.eyePosition

    return getVisibleSides(pos)
        .filter { !mc.world.getBlockState(pos.offset(it)).isFullBox }
        .minByOrNull { eyePos.squareDistanceTo(getHitVec(pos, it)) }
}

inline val IBlockState.isFullBox: Boolean
    get() = Minecraft.getMinecraft().world?.let {
        this.getCollisionBoundingBox(it, BlockPos.ORIGIN)
    } == Block.FULL_BLOCK_AABB


fun getHitVec(pos: BlockPos, facing: EnumFacing): Vec3d {
    val vec = facing.getDirectionVec()
    return Vec3d(vec.x * 0.5 + 0.5 + pos.x, vec.y * 0.5 + 0.5 + pos.y, vec.z * 0.5 + 0.5 + pos.z)
}

fun getVisibleSides(pos: BlockPos, assumeAirAsFullBox: Boolean = false): Set<EnumFacing> {
    val visibleSides = EnumSet<EnumFacing>()

    val eyePos = mc.player.eyePosition
    val blockCenter = pos.toVec3dCenter()
    val blockState = mc.world.getBlockState(pos)
    val isFullBox = assumeAirAsFullBox && blockState.block == Blocks.AIR || blockState.isFullBox

    return visibleSides
        .checkAxis(eyePos.x - blockCenter.x, EnumFacing.WEST, EnumFacing.EAST, !isFullBox)
        .checkAxis(eyePos.y - blockCenter.y, EnumFacing.DOWN, EnumFacing.UP, true)
        .checkAxis(eyePos.z - blockCenter.z, EnumFacing.NORTH, EnumFacing.SOUTH, !isFullBox)
}

private fun EnumSet<EnumFacing>.checkAxis(
    diff: Double,
    negativeSide: EnumFacing,
    positiveSide: EnumFacing,
    bothIfInRange: Boolean
) =
    this.apply {
        when {
            diff < -0.5 -> {
                add(negativeSide)
            }

            diff > 0.5 -> {
                add(positiveSide)
            }

            else -> {
                if (bothIfInRange) {
                    add(negativeSide)
                    add(positiveSide)
                }
            }
        }
    }
