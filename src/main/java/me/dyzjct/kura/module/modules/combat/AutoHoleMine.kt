package me.dyzjct.kura.module.modules.combat

import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.module.modules.misc.InstantMine
import me.dyzjct.kura.utils.NTMiku.BlockUtil
import me.dyzjct.kura.utils.player.getTarget
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos

@Module.Info(name = "AutoHoleMine", category = Category.COMBAT)
class AutoHoleMine : Module() {
    private val range = isetting("Range", 5, 1, 8)
    override fun onUpdate() {
        if (fullNullCheck()) {
            return
        }
        target = getTarget(range.value)
        if (target == null) {
            return
        }
        val feet = BlockPos(target!!.posX, target!!.posY, target!!.posZ)
        if (!detection(target)) {
            if (InstantMine.instance!!.db.value && getBlock(feet.add(0, 0, 0)).block === Blocks.AIR) {
                if (getBlock(feet.add(0, 1, 2)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            1
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(0, 0, 2)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            1
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 0, 1)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(0, 0, 1))
                } else if (getBlock(feet.add(0, 1, -2)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            -1
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(0, 0, -2)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            -1
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 0, -1)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(0, 0, -1))
                } else if (getBlock(feet.add(2, 1, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            1,
                            0,
                            0
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(2, 0, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            1,
                            0,
                            0
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(1, 0, 0)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(1, 0, 0))
                } else if (getBlock(feet.add(-2, 1, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            -1,
                            0,
                            0
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(-2, 0, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            -1,
                            0,
                            0
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(-1, 0, 0)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(-1, 0, 0))
                } else if (getBlock(feet.add(2, 1, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            2,
                            0,
                            0
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(1, 0, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            1,
                            0,
                            0
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(2, 0, 0)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(2, 0, 0))
                } else if (getBlock(feet.add(-2, 1, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            -2,
                            0,
                            0
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(-1, 0, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            -1,
                            0,
                            0
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(-2, 0, 0)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(-2, 0, 0))
                } else if (getBlock(feet.add(0, 1, -2)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            -2
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(0, 0, -1)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            -1
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 0, -2)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(0, 0, -2))
                } else if (getBlock(feet.add(0, 1, 2)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            2
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(0, 0, 1)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            1
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 0, 2)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(0, 0, 2))
                } else if (getBlock(feet.add(2, 1, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            1,
                            0,
                            0
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(2, 0, 0)).block !== Blocks.AIR && getBlock(
                        feet.add(
                            1,
                            0,
                            0
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(2, 0, 0)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(2, 0, 0))
                    if (InstantMine.breakPos2 == null) {
                        surroundMine(feet.add(1, 0, 0))
                    }
                } else if (getBlock(feet.add(-2, 1, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            -1,
                            0,
                            0
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(-2, 0, 0)).block !== Blocks.AIR && getBlock(
                        feet.add(
                            -1,
                            0,
                            0
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(-2, 0, 0)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(-2, 0, 0))
                    if (InstantMine.breakPos2 == null) {
                        surroundMine(feet.add(-1, 0, 0))
                    }
                } else if (getBlock(feet.add(0, 1, -2)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            -1
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(0, 0, -2)).block !== Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            -1
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 0, -2)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(0, 0, -2))
                    if (InstantMine.breakPos2 == null) {
                        surroundMine(feet.add(0, 0, -1))
                    }
                } else if (getBlock(feet.add(0, 1, 2)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            1
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(0, 0, 2)).block !== Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            1
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 0, 2)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(0, 0, 2))
                    if (InstantMine.breakPos2 == null) {
                        surroundMine(feet.add(0, 0, 1))
                    }
                } else if (getBlock(feet.add(0, 2, 1)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            1,
                            1
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(0, 0, 1)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            1,
                            1
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 1, 1)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(0, 1, 1))
                } else if (getBlock(feet.add(0, 2, 1)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            1
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(0, 1, 1)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            1
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 0, 1)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(0, 0, 1))
                } else if (getBlock(feet.add(0, 2, -1)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            -1
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(0, 1, -1)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            -1
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 0, -1)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(0, 0, -1))
                } else if (getBlock(feet.add(1, 2, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            1,
                            0,
                            0
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(1, 1, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            1,
                            0,
                            0
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(1, 0, 0)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(1, 0, 0))
                } else if (getBlock(feet.add(-1, 2, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            -1,
                            0,
                            0
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(-1, 1, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            -1,
                            0,
                            0
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(-1, 0, 0)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(-1, 0, 0))
                } else if (getBlock(feet.add(1, 2, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            1,
                            1,
                            0
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(1, 0, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            1,
                            1,
                            0
                        )
                    ).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(1, 1, 0))
                } else if (getBlock(feet.add(-1, 2, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            -1,
                            1,
                            0
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(-1, 0, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            -1,
                            1,
                            0
                        )
                    ).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(-1, 1, 0))
                } else if (getBlock(feet.add(0, 2, -1)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            1,
                            -1
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(0, 0, -1)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            1,
                            -1
                        )
                    ).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(0, 1, -1))
                } else if (getBlock(feet.add(1, 2, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            1,
                            0,
                            0
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(1, 1, 0)).block !== Blocks.AIR && getBlock(
                        feet.add(
                            1,
                            0,
                            0
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(1, 1, 0)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(1, 1, 0))
                    if (InstantMine.breakPos2 == null) {
                        surroundMine(feet.add(1, 0, 0))
                    }
                } else if (getBlock(feet.add(-1, 2, 0)).block === Blocks.AIR && getBlock(
                        feet.add(
                            -1,
                            0,
                            0
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(-1, 1, 0)).block !== Blocks.AIR && getBlock(
                        feet.add(
                            -1,
                            0,
                            0
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(-1, 1, 0)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(-1, 1, 0))
                    if (InstantMine.breakPos2 == null) {
                        surroundMine(feet.add(-1, 0, 0))
                    }
                } else if (getBlock(feet.add(0, 2, -1)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            -1
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(0, 1, -1)).block !== Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            -1
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 1, -1)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(0, 1, -1))
                    if (InstantMine.breakPos2 == null) {
                        surroundMine(feet.add(0, 0, -1))
                    }
                } else if (getBlock(feet.add(0, 2, 1)).block === Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            1
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(0, 1, 1)).block !== Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            0,
                            1
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 1, 1)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(0, 1, 1))
                    if (InstantMine.breakPos2 == null) {
                        surroundMine(feet.add(0, 0, 1))
                    }
                } else if (getBlock(feet.add(-1, 0, 0)).block !== Blocks.BEDROCK && getBlock(
                        feet.add(
                            -2,
                            0,
                            0
                        )
                    ).block !== Blocks.BEDROCK && getBlock(
                        feet.add(
                            -2,
                            1,
                            0
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(-2, 1, 0)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(-2, 1, 0))
                } else if (getBlock(feet.add(1, 0, 0)).block !== Blocks.BEDROCK && getBlock(
                        feet.add(
                            2,
                            0,
                            0
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(2, 1, 0)).block !== Blocks.AIR && getBlock(
                        feet.add(
                            2,
                            1,
                            0
                        )
                    ).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(2, 1, 0))
                } else if (getBlock(feet.add(0, 0, 1)).block !== Blocks.BEDROCK && getBlock(
                        feet.add(
                            0,
                            0,
                            2
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 1, 2)).block !== Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            1,
                            2
                        )
                    ).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(0, 1, 2))
                } else if (getBlock(feet.add(0, 0, -1)).block !== Blocks.BEDROCK && getBlock(
                        feet.add(
                            0,
                            0,
                            -2
                        )
                    ).block !== Blocks.BEDROCK && getBlock(
                        feet.add(
                            0,
                            1,
                            -2
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(0, 1, -2)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(0, 1, -2))
                } else if (getBlock(feet.add(-1, 0, 0)).block !== Blocks.BEDROCK && getBlock(
                        feet.add(
                            -1,
                            1,
                            0
                        )
                    ).block !== Blocks.BEDROCK && getBlock(
                        feet.add(
                            -1,
                            2,
                            0
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(-1, 2, 0)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(-1, 2, 0))
                } else if (getBlock(feet.add(1, 0, 0)).block !== Blocks.BEDROCK && getBlock(
                        feet.add(
                            1,
                            1,
                            0
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(1, 2, 0)).block !== Blocks.AIR && getBlock(
                        feet.add(
                            1,
                            2,
                            0
                        )
                    ).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(1, 2, 0))
                } else if (getBlock(feet.add(0, 0, 1)).block !== Blocks.BEDROCK && getBlock(
                        feet.add(
                            0,
                            1,
                            1
                        )
                    ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 2, 1)).block !== Blocks.AIR && getBlock(
                        feet.add(
                            0,
                            2,
                            1
                        )
                    ).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(0, 2, 1))
                } else if (getBlock(feet.add(0, 0, -1)).block !== Blocks.BEDROCK && getBlock(
                        feet.add(
                            0,
                            1,
                            -1
                        )
                    ).block !== Blocks.BEDROCK && getBlock(
                        feet.add(
                            0,
                            2,
                            -1
                        )
                    ).block !== Blocks.AIR && getBlock(feet.add(0, 2, -1)).block !== Blocks.BEDROCK
                ) {
                    surroundMine(feet.add(0, 2, -1))
                }
            } else if (getBlock(feet.add(0, 1, 2)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        1
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(0, 0, 2)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        1
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 0, 1)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(0, 0, 1))
            } else if (getBlock(feet.add(0, 1, -2)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        -1
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(0, 0, -2)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        -1
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 0, -1)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(0, 0, -1))
            } else if (getBlock(feet.add(2, 1, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        1,
                        0,
                        0
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(2, 0, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        1,
                        0,
                        0
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(1, 0, 0)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(1, 0, 0))
            } else if (getBlock(feet.add(-2, 1, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        -1,
                        0,
                        0
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(-2, 0, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        -1,
                        0,
                        0
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(-1, 0, 0)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(-1, 0, 0))
            } else if (getBlock(feet.add(2, 1, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        2,
                        0,
                        0
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(1, 0, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        1,
                        0,
                        0
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(2, 0, 0)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(2, 0, 0))
            } else if (getBlock(feet.add(-2, 1, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        -2,
                        0,
                        0
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(-1, 0, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        -1,
                        0,
                        0
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(-2, 0, 0)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(-2, 0, 0))
            } else if (getBlock(feet.add(0, 1, -2)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        -2
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(0, 0, -1)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        -1
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 0, -2)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(0, 0, -2))
            } else if (getBlock(feet.add(0, 1, 2)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        2
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(0, 0, 1)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        1
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 0, 2)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(0, 0, 2))
            } else if (getBlock(feet.add(2, 1, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        1,
                        0,
                        0
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(2, 0, 0)).block !== Blocks.AIR && getBlock(
                    feet.add(
                        1,
                        0,
                        0
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(2, 0, 0)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(2, 0, 0))
            } else if (getBlock(feet.add(-2, 1, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        -1,
                        0,
                        0
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(-2, 0, 0)).block !== Blocks.AIR && getBlock(
                    feet.add(
                        -1,
                        0,
                        0
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(-2, 0, 0)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(-2, 0, 0))
            } else if (getBlock(feet.add(0, 1, -2)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        -1
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(0, 0, -2)).block !== Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        -1
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 0, -2)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(0, 0, -2))
            } else if (getBlock(feet.add(0, 1, 2)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        1
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(0, 0, 2)).block !== Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        1
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 0, 2)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(0, 0, 2))
            } else if (getBlock(feet.add(0, 2, 1)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        1,
                        1
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(0, 0, 1)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        1,
                        1
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 1, 1)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(0, 1, 1))
            } else if (getBlock(feet.add(0, 2, 1)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        1
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(0, 1, 1)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        1
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 0, 1)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(0, 0, 1))
            } else if (getBlock(feet.add(0, 2, -1)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        -1
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(0, 1, -1)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        -1
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 0, -1)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(0, 0, -1))
            } else if (getBlock(feet.add(1, 2, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        1,
                        0,
                        0
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(1, 1, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        1,
                        0,
                        0
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(1, 0, 0)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(1, 0, 0))
            } else if (getBlock(feet.add(-1, 2, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        -1,
                        0,
                        0
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(-1, 1, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        -1,
                        0,
                        0
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(-1, 0, 0)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(-1, 0, 0))
            } else if (getBlock(feet.add(1, 2, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        1,
                        1,
                        0
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(1, 0, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        1,
                        1,
                        0
                    )
                ).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(1, 1, 0))
            } else if (getBlock(feet.add(-1, 2, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        -1,
                        1,
                        0
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(-1, 0, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        -1,
                        1,
                        0
                    )
                ).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(-1, 1, 0))
            } else if (getBlock(feet.add(0, 2, -1)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        1,
                        -1
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(0, 0, -1)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        1,
                        -1
                    )
                ).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(0, 1, -1))
            } else if (getBlock(feet.add(1, 2, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        1,
                        0,
                        0
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(1, 1, 0)).block !== Blocks.AIR && getBlock(
                    feet.add(
                        1,
                        0,
                        0
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(1, 1, 0)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(1, 1, 0))
            } else if (getBlock(feet.add(-1, 2, 0)).block === Blocks.AIR && getBlock(
                    feet.add(
                        -1,
                        0,
                        0
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(-1, 1, 0)).block !== Blocks.AIR && getBlock(
                    feet.add(
                        -1,
                        0,
                        0
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(-1, 1, 0)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(-1, 1, 0))
            } else if (getBlock(feet.add(0, 2, -1)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        -1
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(0, 1, -1)).block !== Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        -1
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 1, -1)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(0, 1, -1))
            } else if (getBlock(feet.add(0, 2, 1)).block === Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        1
                    )
                ).block !== Blocks.AIR && getBlock(feet.add(0, 1, 1)).block !== Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        0,
                        1
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 1, 1)).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(0, 1, 1))
            } else if (getBlock(feet.add(-1, 0, 0)).block !== Blocks.BEDROCK && getBlock(
                    feet.add(
                        -2,
                        0,
                        0
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(-2, 1, 0)).block !== Blocks.AIR && getBlock(
                    feet.add(
                        -2,
                        1,
                        0
                    )
                ).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(-2, 1, 0))
            } else if (getBlock(feet.add(1, 0, 0)).block !== Blocks.BEDROCK && getBlock(
                    feet.add(
                        2,
                        0,
                        0
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(2, 1, 0)).block !== Blocks.AIR && getBlock(
                    feet.add(
                        2,
                        1,
                        0
                    )
                ).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(2, 1, 0))
            } else if (getBlock(feet.add(0, 0, 1)).block !== Blocks.BEDROCK && getBlock(
                    feet.add(
                        0,
                        0,
                        2
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 1, 2)).block !== Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        1,
                        2
                    )
                ).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(0, 1, 2))
            } else if (getBlock(feet.add(0, 0, -1)).block !== Blocks.BEDROCK && getBlock(
                    feet.add(
                        0,
                        0,
                        -2
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 1, -2)).block !== Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        1,
                        -2
                    )
                ).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(0, 1, -2))
            } else if (getBlock(feet.add(-1, 0, 0)).block !== Blocks.BEDROCK && getBlock(
                    feet.add(
                        -1,
                        1,
                        0
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(-1, 2, 0)).block !== Blocks.AIR && getBlock(
                    feet.add(
                        -1,
                        2,
                        0
                    )
                ).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(-1, 2, 0))
            } else if (getBlock(feet.add(1, 0, 0)).block !== Blocks.BEDROCK && getBlock(
                    feet.add(
                        1,
                        1,
                        0
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(1, 2, 0)).block !== Blocks.AIR && getBlock(
                    feet.add(
                        1,
                        2,
                        0
                    )
                ).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(1, 2, 0))
            } else if (getBlock(feet.add(0, 0, 1)).block !== Blocks.BEDROCK && getBlock(
                    feet.add(
                        0,
                        1,
                        1
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 2, 1)).block !== Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        2,
                        1
                    )
                ).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(0, 2, 1))
            } else if (getBlock(feet.add(0, 0, -1)).block !== Blocks.BEDROCK && getBlock(
                    feet.add(
                        0,
                        1,
                        -1
                    )
                ).block !== Blocks.BEDROCK && getBlock(feet.add(0, 2, -1)).block !== Blocks.AIR && getBlock(
                    feet.add(
                        0,
                        2,
                        -1
                    )
                ).block !== Blocks.BEDROCK
            ) {
                surroundMine(feet.add(0, 2, -1))
            }
        }
    }


    private fun surroundMine(position: BlockPos) {
        if (InstantMine.breakPos2 != null && InstantMine.breakPos2 == position as Any) {
            return
        }
        if (InstantMine.breakPos != null) {
            if (InstantMine.breakPos == position as Any) {
                return
            }
            if (InstantMine.breakPos == BlockPos(
                    target!!.posX,
                    target!!.posY,
                    target!!.posZ
                ) as Any && mc.world.getBlockState(
                    BlockPos(
                        target!!.posX, target!!.posY, target!!.posZ
                    )
                ).block !== Blocks.AIR
            ) {
                return
            }
            if (InstantMine.breakPos == BlockPos(mc.player.posX, mc.player.posY + 2.0, mc.player.posZ) as Any) {
                return
            }
            if (InstantMine.breakPos == BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ) as Any) {
                return
            }
            if (mc.player.rotationPitch in 80.0f..90.0f) {
                return
            }
            if (mc.world.getBlockState(InstantMine.breakPos!!).block === Blocks.WEB) {
                return
            }
        }
        mc.playerController.onPlayerDamageBlock(position, BlockUtil.getRayTraceFacing(position))
    }

    private fun detection(player: EntityPlayer?): Boolean {
        return (mc.world.getBlockState(
            BlockPos(player!!.posX + 1.2, player.posY, player.posZ)
        ).block === Blocks.AIR) and (mc.world.getBlockState(
            BlockPos(
                player!!.posX + 1.2,
                player.posY + 1.0,
                player.posZ
            )
        ).block === Blocks.AIR) || (mc.world.getBlockState(
            BlockPos(
                player!!.posX - 1.2,
                player.posY,
                player.posZ
            )
        ).block === Blocks.AIR) and (mc.world.getBlockState(
            BlockPos(
                player!!.posX - 1.2,
                player.posY + 1.0,
                player.posZ
            )
        ).block === Blocks.AIR) || (mc.world.getBlockState(
            BlockPos(
                player!!.posX,
                player.posY,
                player.posZ + 1.2
            )
        ).block === Blocks.AIR) and (mc.world.getBlockState(
            BlockPos(
                player!!.posX,
                player.posY + 1.0,
                player.posZ + 1.2
            )
        ).block === Blocks.AIR) || (mc.world.getBlockState(
            BlockPos(
                player!!.posX,
                player.posY,
                player.posZ - 1.2
            )
        ).block === Blocks.AIR) and (mc.world.getBlockState(
            BlockPos(
                player!!.posX,
                player.posY + 1.0,
                player.posZ - 1.2
            )
        ).block === Blocks.AIR) || (mc.world.getBlockState(
            BlockPos(
                player!!.posX + 2.2,
                player.posY + 1.0,
                player.posZ
            )
        ).block === Blocks.AIR) and (mc.world.getBlockState(
            BlockPos(
                player!!.posX + 2.2,
                player.posY,
                player.posZ
            )
        ).block === Blocks.AIR) and (mc.world.getBlockState(
            BlockPos(
                player!!.posX + 1.2,
                player.posY,
                player.posZ
            )
        ).block === Blocks.AIR) || (mc.world.getBlockState(
            BlockPos(
                player!!.posX - 2.2,
                player.posY + 1.0,
                player.posZ
            )
        ).block === Blocks.AIR) and (mc.world.getBlockState(
            BlockPos(
                player!!.posX - 2.2,
                player.posY,
                player.posZ
            )
        ).block === Blocks.AIR) and (mc.world.getBlockState(
            BlockPos(
                player!!.posX - 1.2,
                player.posY,
                player.posZ
            )
        ).block === Blocks.AIR) || (mc.world.getBlockState(
            BlockPos(
                player!!.posX,
                player.posY + 1.0,
                player.posZ + 2.2
            )
        ).block === Blocks.AIR) and (mc.world.getBlockState(
            BlockPos(
                player!!.posX,
                player.posY,
                player.posZ + 2.2
            )
        ).block === Blocks.AIR) and (mc.world.getBlockState(
            BlockPos(
                player!!.posX,
                player.posY,
                player.posZ + 1.2
            )
        ).block === Blocks.AIR) || (mc.world.getBlockState(
            BlockPos(
                player!!.posX,
                player.posY + 1.0,
                player.posZ - 2.2
            )
        ).block === Blocks.AIR) and (mc.world.getBlockState(
            BlockPos(
                player!!.posX,
                player.posY,
                player.posZ - 2.2
            )
        ).block === Blocks.AIR) and (mc.world.getBlockState(
            BlockPos(
                player!!.posX,
                player.posY,
                player.posZ - 1.2
            )
        ).block === Blocks.AIR)
    }


    private fun getBlock(block: BlockPos): IBlockState {
        return mc.world.getBlockState(block)
    }

    companion object {
        var target: EntityPlayer? = null
    }
}