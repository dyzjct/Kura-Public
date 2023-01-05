package kura.utils

import me.dyzjct.kura.utils.block.BlockUtil.getHitVec
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

class PlaceInfo(
    val pos: BlockPos,
    val side: EnumFacing,
    val dist: Double,
    val hitVecOffset: Vec3f,
    val hitVec: Vec3d,
    val placedPos: BlockPos
) {
    companion object {
        fun newPlaceInfo(pos: BlockPos, side: EnumFacing): PlaceInfo {
            val hitVecOffset = getHitVecOffset(side)
            val hitVec = getHitVec(pos, side)

            return PlaceInfo(pos, side, Wrapper.player!!.positionVector.add(
                0.0,
                Wrapper.player!!.getEyeHeight().toDouble(), 0.0
            ).distanceTo(hitVec), hitVecOffset, hitVec, pos.offset(side))
        }
    }
}