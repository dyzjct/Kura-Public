package me.windyteam.kura.module.modules.misc

import me.windyteam.kura.event.events.block.BlockBreakEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.utils.block.BlockUtil
import me.windyteam.kura.utils.inventory.InventoryUtil
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "AntiPiston", category = Category.MISC)
class AntiPiston : Module() {
    private val rotate = bsetting("Rotate", false)
    private val breakpiston = bsetting("BreakPiston", false)
    private var obsidian = -1
    private var breakpos: BlockPos? = null

    override fun onUpdate() {
        if (fullNullCheck()) return
        if (mc.player == null || mc.world == null) {
            return
        }
        obsidian = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN)
        if (obsidian == -1) {
            return
        }
        val pos = BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)
        if (getBlock(pos.add(1, 1, 0)).block === Blocks.PISTON) {
            if (getBlock(pos.add(-1, 0, 0)).block === Blocks.AIR) {
                perform(pos.add(-1, 0, 0))
            } else if (getBlock(pos.add(-1, 1, 0)).block === Blocks.AIR) {
                perform(pos.add(-1, 1, 0))
            } else if (getBlock(pos.add(-1, 2, 0)).block === Blocks.AIR) {
                perform(pos.add(-1, 2, 0))
            } else if (getBlock(pos.add(0, 2, 0)).block === Blocks.AIR) {
                perform(pos.add(0, 2, 0))
            }
            if (breakpiston.value) {
                mc.playerController.onPlayerDamageBlock(pos.add(1, 1, 0), BlockUtil.getRayTraceFacing(pos.add(1, 1, 0)))
            }
        }
        if (getBlock(pos.add(-1, 1, 0)).block === Blocks.PISTON) {
            if (getBlock(pos.add(1, 0, 0)).block === Blocks.AIR) {
                perform(pos.add(1, 0, 0))
            } else if (getBlock(pos.add(1, 1, 0)).block === Blocks.AIR) {
                perform(pos.add(1, 1, 0))
            } else if (getBlock(pos.add(1, 2, 0)).block === Blocks.AIR) {
                perform(pos.add(1, 2, 0))
            } else if (getBlock(pos.add(0, 2, 0)).block === Blocks.AIR) {
                perform(pos.add(0, 2, 0))
            }
            if (breakpiston.value) {
                mc.playerController.onPlayerDamageBlock(
                    pos.add(-1, 1, 0),
                    BlockUtil.getRayTraceFacing(pos.add(-1, 1, 0))
                )
            }
        }
        if (getBlock(pos.add(0, 1, 1)).block === Blocks.PISTON) {
            if (getBlock(pos.add(0, 0, -1)).block === Blocks.AIR) {
                perform(pos.add(0, 0, -1))
            } else if (getBlock(pos.add(0, 1, -1)).block === Blocks.AIR) {
                perform(pos.add(0, 1, -1))
            } else if (getBlock(pos.add(0, 2, -1)).block === Blocks.AIR) {
                perform(pos.add(0, 2, -1))
            } else if (getBlock(pos.add(0, 2, 0)).block === Blocks.AIR) {
                perform(pos.add(0, 2, 0))
            }
            if (breakpiston.value) {
                mc.playerController.onPlayerDamageBlock(pos.add(0, 1, 1), BlockUtil.getRayTraceFacing(pos.add(0, 1, 1)))
            }
        }
        if (getBlock(pos.add(0, 1, -1)).block === Blocks.PISTON) {
            if (getBlock(pos.add(0, 0, 1)).block === Blocks.AIR) {
                perform(pos.add(0, 0, 1))
            } else if (getBlock(pos.add(0, 1, 1)).block === Blocks.AIR) {
                perform(pos.add(0, 1, 1))
            } else if (getBlock(pos.add(0, 2, 1)).block === Blocks.AIR) {
                perform(pos.add(0, 2, 1))
            } else if (getBlock(pos.add(0, 2, 0)).block === Blocks.AIR) {
                perform(pos.add(0, 2, 0))
            }
            if (breakpiston.value) {
                mc.playerController.onPlayerDamageBlock(
                    pos.add(0, 1, -1),
                    BlockUtil.getRayTraceFacing(pos.add(0, 1, -1))
                )
            }
        }
        //        STICKY_PISTON
        if (getBlock(pos.add(1, 1, 0)).block === Blocks.STICKY_PISTON) {
            if (getBlock(pos.add(-1, 0, 0)).block === Blocks.AIR) {
                perform(pos.add(-1, 0, 0))
            } else if (getBlock(pos.add(-1, 1, 0)).block === Blocks.AIR) {
                perform(pos.add(-1, 1, 0))
            } else if (getBlock(pos.add(-1, 2, 0)).block === Blocks.AIR) {
                perform(pos.add(-1, 2, 0))
            } else if (getBlock(pos.add(0, 2, 0)).block === Blocks.AIR) {
                perform(pos.add(0, 2, 0))
            }
            if (breakpiston.value) {
                mc.playerController.onPlayerDamageBlock(pos.add(1, 1, 0), BlockUtil.getRayTraceFacing(pos.add(1, 1, 0)))
            }
        }
        if (getBlock(pos.add(-1, 1, 0)).block === Blocks.STICKY_PISTON) {
            if (getBlock(pos.add(1, 0, 0)).block === Blocks.AIR) {
                perform(pos.add(1, 0, 0))
            } else if (getBlock(pos.add(1, 1, 0)).block === Blocks.AIR) {
                perform(pos.add(1, 1, 0))
            } else if (getBlock(pos.add(1, 2, 0)).block === Blocks.AIR) {
                perform(pos.add(1, 2, 0))
            } else if (getBlock(pos.add(0, 2, 0)).block === Blocks.AIR) {
                perform(pos.add(0, 2, 0))
            }
            if (breakpiston.value) {
                mc.playerController.onPlayerDamageBlock(
                    pos.add(-1, 1, 0),
                    BlockUtil.getRayTraceFacing(pos.add(-1, 1, 0))
                )
            }
        }
        if (getBlock(pos.add(0, 1, 1)).block === Blocks.STICKY_PISTON) {
            if (getBlock(pos.add(0, 0, -1)).block === Blocks.AIR) {
                perform(pos.add(0, 0, -1))
            } else if (getBlock(pos.add(0, 1, -1)).block === Blocks.AIR) {
                perform(pos.add(0, 1, -1))
            } else if (getBlock(pos.add(0, 2, -1)).block === Blocks.AIR) {
                perform(pos.add(0, 2, -1))
            } else if (getBlock(pos.add(0, 2, 0)).block === Blocks.AIR) {
                perform(pos.add(0, 2, 0))
            }
            if (breakpiston.value) {
                mc.playerController.onPlayerDamageBlock(pos.add(0, 1, 1), BlockUtil.getRayTraceFacing(pos.add(0, 1, 1)))
            }
        }
        if (getBlock(pos.add(0, 1, -1)).block === Blocks.STICKY_PISTON) {
            if (getBlock(pos.add(0, 1, 1)).block === Blocks.AIR) {
                perform(pos.add(0, 1, 1))
            } else if (getBlock(pos.add(0, 1, 1)).block === Blocks.AIR) {
                perform(pos.add(0, 1, 1))
            } else if (getBlock(pos.add(0, 2, 1)).block === Blocks.AIR) {
                perform(pos.add(0, 2, 1))
            } else if (getBlock(pos.add(0, 2, 0)).block === Blocks.AIR) {
                perform(pos.add(0, 2, 0))
            }
            if (breakpiston.value) {
                mc.playerController.onPlayerDamageBlock(
                    pos.add(0, 1, -1),
                    BlockUtil.getRayTraceFacing(pos.add(0, 1, -1))
                )
            }
        }
    }

    private fun switchToSlot(slot: Int) {
        if (fullNullCheck()) return
        mc.player.connection.sendPacket(CPacketHeldItemChange(slot))
        mc.player.inventory.currentItem = slot
        mc.playerController.updateController()
    }

    private fun getBlock(block: BlockPos): IBlockState {
        return mc.world.getBlockState(block)
    }

    private fun perform(pos: BlockPos) {
        if (fullNullCheck()) return
        if (pos == breakpos) return
        if (pos == InstantMine.breakPos) return
        val old = mc.player.inventory.currentItem
        switchToSlot(obsidian)
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.value, true, false)
        switchToSlot(old)
    }


    @SubscribeEvent
    fun onBreak(event: BlockBreakEvent) {
        if (fullNullCheck()) {
            return
        }
        if (event.position != null) {
            breakpos = event.position
        }
    }

}