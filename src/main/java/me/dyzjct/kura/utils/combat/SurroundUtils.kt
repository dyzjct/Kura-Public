package me.dyzjct.kura.utils.combat

import me.dyzjct.kura.utils.animations.fastFloor
import me.dyzjct.kura.utils.animations.floorToInt
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos

object SurroundUtils {
    var mc: Minecraft = Minecraft.getMinecraft()
    val Entity.betterPosition get() = BlockPos(this.posX.fastFloor(), (this.posY + 0.25).fastFloor(), this.posZ.fastFloor())
    val Entity.flooredPosition get() = BlockPos(posX.floorToInt(), posY.floorToInt(), posZ.floorToInt())

    val surroundOffset = arrayOf(
        BlockPos(0, -1, 0), // down
        BlockPos(0, 0, -1), // north
        BlockPos(1, 0, 0),  // east
        BlockPos(0, 0, 1),  // south
        BlockPos(-1, 0, 0)  // west
    )

    fun checkHole(entity: Entity) =
        checkHole(entity.flooredPosition)

    fun checkHole(pos: BlockPos): HoleType {
        // Must be a 1 * 3 * 1 empty space
        if (!mc.world.isAirBlock(pos) || !mc.world.isAirBlock(pos.up()) || !mc.world.isAirBlock(
                pos.up().up()
            )
        ) return HoleType.NONE

        var type = HoleType.BEDROCK

        for (offset in surroundOffset) {
            val block = mc.world.getBlockState(pos.add(offset)).block

            if (!checkBlock(block)) {
                type = HoleType.NONE
                break
            }

            if (block != Blocks.BEDROCK || HoleUtil.is2HoleB(pos)) type = HoleType.OBBY
        }

        return type
    }

    private fun checkBlock(block: Block): Boolean {
        return block == Blocks.BEDROCK || block == Blocks.OBSIDIAN || block == Blocks.ENDER_CHEST || block == Blocks.ANVIL
    }

    enum class HoleType {
        NONE, OBBY, BEDROCK
    }
}