package kura.utils

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

val shulkerList: Set<Block> = hashSetOf(
    Blocks.WHITE_SHULKER_BOX,
    Blocks.ORANGE_SHULKER_BOX,
    Blocks.MAGENTA_SHULKER_BOX,
    Blocks.LIGHT_BLUE_SHULKER_BOX,
    Blocks.YELLOW_SHULKER_BOX,
    Blocks.LIME_SHULKER_BOX,
    Blocks.PINK_SHULKER_BOX,
    Blocks.GRAY_SHULKER_BOX,
    Blocks.SILVER_SHULKER_BOX,
    Blocks.CYAN_SHULKER_BOX,
    Blocks.PURPLE_SHULKER_BOX,
    Blocks.BLUE_SHULKER_BOX,
    Blocks.BROWN_SHULKER_BOX,
    Blocks.GREEN_SHULKER_BOX,
    Blocks.RED_SHULKER_BOX,
    Blocks.BLACK_SHULKER_BOX
)

val blockBlacklist: Set<Block> = hashSetOf(
    Blocks.ENDER_CHEST,
    Blocks.CHEST,
    Blocks.TRAPPED_CHEST,
    Blocks.CRAFTING_TABLE,
    Blocks.ANVIL,
    Blocks.BREWING_STAND,
    Blocks.HOPPER,
    Blocks.DROPPER,
    Blocks.DISPENSER,
    Blocks.TRAPDOOR,
    Blocks.ENCHANTING_TABLE
).apply {
    addAll(shulkerList)
}

private val hashMap = Object2IntOpenHashMap<Block>().apply { defaultReturnValue(-1) }

val Block.item: Item get() = Item.getItemFromBlock(this)

val Block.id: Int
    get() {
        var result = runCatching { hashMap.getInt(this) }.getOrDefault(-1)

        if (result == -1) {
            result = Block.getIdFromBlock(this)
            synchronized(hashMap) { hashMap[this] = result }
        }

        return result
    }


inline val IBlockState.isBlacklisted: Boolean
    get() = blockBlacklist.contains(this.block)

inline val IBlockState.isLiquid: Boolean
    get() = this.material.isLiquid

inline val IBlockState.isWater: Boolean
    get() = this.block == Blocks.WATER

inline val IBlockState.isFullBox: Boolean
    get() = Wrapper.world?.let {
        this.getCollisionBoundingBox(it, BlockPos.ORIGIN)
    } == Block.FULL_BLOCK_AABB

inline fun World.getBlockState(x: Int, y: Int, z: Int): IBlockState {
    return if (y !in 0..255) {
        Blocks.AIR.defaultState
    } else {
        val chunk = getChunk(x shr 4, z shr 4)
        return if (chunk.isEmpty) Blocks.AIR.defaultState else chunk.getBlockState(x, y, z)
    }
}

inline fun World.isAir(x: Int, y: Int, z: Int): Boolean {
    return getBlockState(x, y, z).block == Blocks.AIR
}

//inline fun World.isAir(pos: BlockPos): Boolean {
//    return getBlockState(pos).block == Blocks.AIR
//}

inline fun World.getBlock(pos: BlockPos): Block =
    this.getBlockState(pos).block

inline fun World.getMaterial(pos: BlockPos): Material =
    this.getBlockState(pos).material

inline fun WorldClient.getSelectedBox(pos: BlockPos): AxisAlignedBB =
    this.getBlockState(pos).getSelectedBoundingBox(this, pos)

inline fun WorldClient.getCollisionBox(pos: BlockPos): AxisAlignedBB? =
    this.getBlockState(pos).getCollisionBoundingBox(this, pos)

inline val IBlockState.isReplaceable: Boolean
    get() = this.material.isReplaceable
