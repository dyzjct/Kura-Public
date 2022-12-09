package me.dyzjct.kura.module.modules.misc

import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.module.ModuleManager
import me.dyzjct.kura.module.modules.movement.Step
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos

@Module.Info(name = "HeadBlocker", category = Category.MISC)
class HeadBlocker : Module() {
    private val AntiCity = bsetting("AntiHoleMine", true)
    private val checkstep = bsetting("stepdisable", true)
    override fun onUpdate() {
        if (fullNullCheck()) {
            return
        }
        if (ModuleManager.getModuleByClass(Step::class.java).isEnabled && checkstep.value) {
            if (ModuleManager.getModuleByClass(AntiHoleMine::class.java).isEnabled) {
                ModuleManager.getModuleByClass(AntiHoleMine::class.java).disable()
            }
            return
        }
        val pos = BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)
        if (mc.player.onGround) {
            if ((getBlock(pos.add(-1, 0, 0)).block === Blocks.OBSIDIAN) or (getBlock(
                    pos.add(-1, 0, 0)
                ).block === Blocks.BEDROCK) && (getBlock(
                    pos.add(1, 0, 0)
                ).block === Blocks.OBSIDIAN) or (getBlock(
                    pos.add(1, 0, 0)
                ).block === Blocks.BEDROCK) && (getBlock(
                    pos.add(0, 0, 1)
                ).block === Blocks.OBSIDIAN) or (getBlock(
                    pos.add(0, 0, 1)
                ).block === Blocks.BEDROCK) && (getBlock(
                    pos.add(
                        0,
                        0,
                        -1
                    )
                ).block === Blocks.OBSIDIAN) or (getBlock(pos.add(0, 0, -1)).block === Blocks.BEDROCK)
            ) {
                if (AntiCity.value && ModuleManager.getModuleByClass(Step::class.java).isDisabled) {
                    if (ModuleManager.getModuleByClass(AntiHoleMine::class.java).isDisabled) {
                        ModuleManager.getModuleByClass(AntiHoleMine::class.java).enable()
                    }
                }
            }
            if ((getBlock(pos.add(-1, 1, 0)).block === Blocks.OBSIDIAN) or (getBlock(
                    pos.add(
                        -1,
                        0,
                        0
                    )
                ).block === Blocks.BEDROCK) && (getBlock(
                    pos.add(
                        1,
                        0,
                        0
                    )
                ).block === Blocks.OBSIDIAN) or (getBlock(
                    pos.add(
                        1,
                        0,
                        0
                    )
                ).block === Blocks.BEDROCK) && (getBlock(
                    pos.add(
                        0,
                        0,
                        1
                    )
                ).block === Blocks.OBSIDIAN) or (getBlock(
                    pos.add(
                        0,
                        1,
                        1
                    )
                ).block === Blocks.BEDROCK) && (getBlock(
                    pos.add(
                        0,
                        0,
                        -1
                    )
                ).block === Blocks.OBSIDIAN) or (getBlock(pos.add(0, 0, -1)).block === Blocks.BEDROCK)
            ) {
                if (AntiCity.value && ModuleManager.getModuleByClass(Step::class.java).isDisabled) {
                    if (ModuleManager.getModuleByClass(AntiHoleMine::class.java).isDisabled) {
                        ModuleManager.getModuleByClass(AntiHoleMine::class.java).enable()
                    }
                }
            }
            if ((getBlock(pos.add(-1, 0, 0)).block === Blocks.OBSIDIAN) or (getBlock(
                    pos.add(
                        -1,
                        1,
                        0
                    )
                ).block === Blocks.BEDROCK) && (getBlock(
                    pos.add(
                        1,
                        0,
                        0
                    )
                ).block === Blocks.OBSIDIAN) or (getBlock(
                    pos.add(
                        1,
                        0,
                        0
                    )
                ).block === Blocks.BEDROCK) && (getBlock(
                    pos.add(
                        0,
                        0,
                        1
                    )
                ).block === Blocks.OBSIDIAN) or (getBlock(
                    pos.add(
                        0,
                        0,
                        1
                    )
                ).block === Blocks.BEDROCK) && (getBlock(
                    pos.add(
                        0,
                        1,
                        -1
                    )
                ).block === Blocks.OBSIDIAN) or (getBlock(pos.add(0, 0, -1)).block === Blocks.BEDROCK)
            ) {
                if (AntiCity.value && ModuleManager.getModuleByClass(Step::class.java).isDisabled) {
                    if (ModuleManager.getModuleByClass(AntiHoleMine::class.java).isDisabled) {
                        ModuleManager.getModuleByClass(AntiHoleMine::class.java).enable()
                    }
                }
            }
            if ((getBlock(pos.add(-1, 0, 0)).block === Blocks.OBSIDIAN) or (getBlock(
                    pos.add(
                        -1,
                        0,
                        0
                    )
                ).block === Blocks.BEDROCK) && (getBlock(
                    pos.add(
                        1,
                        1,
                        0
                    )
                ).block === Blocks.OBSIDIAN) or (getBlock(
                    pos.add(
                        1,
                        0,
                        0
                    )
                ).block === Blocks.BEDROCK) && (getBlock(
                    pos.add(
                        0,
                        0,
                        1
                    )
                ).block === Blocks.OBSIDIAN) or (getBlock(
                    pos.add(
                        0,
                        0,
                        1
                    )
                ).block === Blocks.BEDROCK) && (getBlock(
                    pos.add(
                        0,
                        0,
                        -1
                    )
                ).block === Blocks.OBSIDIAN) or (getBlock(pos.add(0, 1, -1)).block === Blocks.BEDROCK)
            ) {
                if (AntiCity.value && ModuleManager.getModuleByClass(Step::class.java).isDisabled) {
                    if (ModuleManager.getModuleByClass(AntiHoleMine::class.java).isDisabled) {
                        ModuleManager.getModuleByClass(AntiHoleMine::class.java).enable()
                    }
                }
            }
            if ((getBlock(pos.add(-1, 0, 0)).block === Blocks.OBSIDIAN) or (getBlock(
                    pos.add(
                        -1,
                        0,
                        0
                    )
                ).block === Blocks.BEDROCK) && (getBlock(
                    pos.add(
                        1,
                        0,
                        0
                    )
                ).block === Blocks.OBSIDIAN) or (getBlock(
                    pos.add(
                        1,
                        1,
                        0
                    )
                ).block === Blocks.BEDROCK) && (getBlock(
                    pos.add(
                        0,
                        0,
                        1
                    )
                ).block === Blocks.OBSIDIAN) or (getBlock(
                    pos.add(
                        0,
                        0,
                        1
                    )
                ).block === Blocks.BEDROCK) && (getBlock(
                    pos.add(
                        0,
                        0,
                        -1
                    )
                ).block === Blocks.OBSIDIAN) or (getBlock(pos.add(0, 1, -1)).block === Blocks.BEDROCK)
            ) {
                if (AntiCity.value && ModuleManager.getModuleByClass(Step::class.java).isDisabled) {
                    if (ModuleManager.getModuleByClass(AntiHoleMine::class.java).isDisabled) {
                        ModuleManager.getModuleByClass(AntiHoleMine::class.java).enable()
                    }
                }
            }
        }
    }

    override fun onDisable() {
        if (fullNullCheck()) {
            return
        }
        if (AntiCity.value) {
            if (ModuleManager.getModuleByClass(AntiHoleMine::class.java).isEnabled) {
                ModuleManager.getModuleByClass(AntiHoleMine::class.java).disable()
            }
            if (ModuleManager.getModuleByClass(Step::class.java).isEnabled) {
                ModuleManager.getModuleByClass(AntiHoleMine::class.java).disable()
            }
        }
    }

    private fun getBlock(block: BlockPos): IBlockState {
        return mc.world.getBlockState(block)
    }
}