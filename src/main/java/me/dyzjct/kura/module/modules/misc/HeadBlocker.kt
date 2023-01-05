package me.dyzjct.kura.module.modules.misc

import kura.utils.Wrapper
import kura.utils.isReplaceable
import kura.utils.world
import me.dyzjct.kura.event.events.block.BlockBreakEvent
import me.dyzjct.kura.friend.FriendManager
import me.dyzjct.kura.manager.HotbarManager
import me.dyzjct.kura.manager.SpeedManager
import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.utils.NTMiku.BlockUtil
import me.dyzjct.kura.utils.Timer
import me.dyzjct.kura.utils.entity.EntityUtil
import me.dyzjct.kura.utils.inventory.InventoryUtil
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "HeadBlocker", category = Category.MISC)
class HeadBlocker : Module() {
    private val rotate = bsetting("Rotate", false)
    private val delay = isetting("Delay", 0, 0, 300)
    private val antiFriend = bsetting("AntiFriend", false)
    private val antiSelf = bsetting("AntiSelf", false)
    private val antifaceplace = bsetting("AntiFacePlace",false)
    private var obsidian = -1
    private var minepos: BlockPos? = null
    private var breakpos: BlockPos? = null
    private val timer = Timer()
    private var mc = Minecraft.getMinecraft()

    override fun onUpdate() {
        if (fullNullCheck()) return
        if (mc.player == null || mc.world == null) {
            return
        }
        val pos = BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)
        if (InventoryUtil.getItemHotbar(Items.DIAMOND_SWORD) != -1) {
            if (getBlock(pos.add(0, -1, 0)).block == Blocks.WEB && pos.add(0, -1, 0) != breakpos) {
                Module.mc.playerController.onPlayerDamageBlock(
                    pos.add(0, -1, 0),
                    BlockUtil.getRayTraceFacing(pos.add(0, -1, 0))
                )
            } else if (getBlock(pos.add(0, 0, 0)).block == Blocks.WEB && pos.add(0, 0, 0) != breakpos) {
                Module.mc.playerController.onPlayerDamageBlock(pos.add(0, 0, 0), BlockUtil.getRayTraceFacing(pos.add(0, 0, 0)))
            } else if (getBlock(pos.add(0, 1, 0)).block == Blocks.WEB && pos.add(0, 1, 0) != breakpos) {
                Module.mc.playerController.onPlayerDamageBlock(pos.add(0, 1, 0), BlockUtil.getRayTraceFacing(pos.add(0, 1, 0)))
            }
        }
        if (!mc.player.onGround) {
            return
        }
        obsidian = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN)
        if (obsidian == -1) {
            return
        }
        val a: Vec3d = mc.player.positionVector
        if (minepos != null) {
//          AntiCity's:
//          Alpha
//            +x
            if (pos.add(1, 0, 0) == minepos) {
                if (getBlock(pos.add(1, 0, 0)).block != Blocks.BEDROCK) {
                    if (getBlock(pos.add(1, 0, 1)).block == Blocks.AIR) {
                        perform(pos.add(1, 0, 1), 1, 0, 1)
                    }
                    if (getBlock(pos.add(1, 0, -1)).block == Blocks.AIR) {
                        perform(pos.add(1, 0, -1), 1, 0, -1)
                    }
                    if (getBlock(pos.add(2, 0, 0)).block == Blocks.AIR) {
                        if (getBlock(pos.add(2, -1, 0)).block == Blocks.AIR) {
                            perform(pos.add(2, -1, 0), 2, -1, 0)
                        }
                        perform(pos.add(2, 0, 0), 2, 0, 0)
                    }
                    if (getBlock(pos.add(2, 1, 0)).block == Blocks.AIR) {
                        perform(pos.add(2, 1, 0), 2, 1, 0)
                    }
                    if (getBlock(pos.add(1, 1, 0)).block == Blocks.AIR) {
                        perform(pos.add(1, 1, 0), 1, 1, 0)
                    }
                }
            }
//            -x
            if (pos.add(-1, 0, 0) == minepos) {
                if (getBlock(pos.add(-1, 0, 0)).block != Blocks.BEDROCK) {
                    if (getBlock(pos.add(-1, 0, 1)).block == Blocks.AIR) {
                        perform(pos.add(-1, 0, 1), -1, 0, 1)
                    }
                    if (getBlock(pos.add(-1, 0, -1)).block == Blocks.AIR) {
                        perform(pos.add(-1, 0, -1), -1, 0, -1)
                    }
                    if (getBlock(pos.add(-2, 0, 0)).block == Blocks.AIR) {
                        if (getBlock(pos.add(-2, -1, 0)).block == Blocks.AIR) {
                            perform(pos.add(-2, -1, 0), -2, -1, 0)
                        }
                        perform(pos.add(-2, 0, 0), -2, 0, 0)
                    }
                    if (getBlock(pos.add(-2, 1, 0)).block == Blocks.AIR) {
                        perform(pos.add(-2, 1, 0), -2, 1, 0)
                    }
                    if (getBlock(pos.add(-1, 1, 0)).block == Blocks.AIR) {
                        perform(pos.add(-1, 1, 0), -1, 1, 0)
                    }
                }
            }
//            +z
            if (pos.add(0, 0, 1) == minepos) {
                if (getBlock(pos.add(0, 0, 1)).block != Blocks.BEDROCK) {
                    if (getBlock(pos.add(1, 0, 1)).block == Blocks.AIR) {
                        perform(pos.add(1, 0, 1), 1, 0, 1)
                    }
                    if (getBlock(pos.add(-1, 0, 1)).block == Blocks.AIR) {
                        perform(pos.add(-1, 0, 1), -1, 0, 1)
                    }
                    if (getBlock(pos.add(0, 0, 2)).block == Blocks.AIR) {
                        if (getBlock(pos.add(0, -1, 2)).block == Blocks.AIR) {
                            perform(pos.add(0, -1, 2), 0, -1, 2)
                        }
                        perform(pos.add(0, 0, 2), 0, 0, 2)
                    }
                    if (getBlock(pos.add(0, 1, 2)).block == Blocks.AIR) {
                        perform(pos.add(0, 1, 2), 0, 1, 2)
                    }
                    if (getBlock(pos.add(0, 1, 1)).block == Blocks.AIR) {
                        perform(pos.add(0, 1, 1), 0, 1, 1)
                    }
                }
            }
//            -z
            if (pos.add(0, 0, -1) == minepos) {
                if (getBlock(pos.add(0, 0, -1)).block != Blocks.BEDROCK) {
                    if (getBlock(pos.add(1, 0, -1)).block == Blocks.AIR) {
                        perform(pos.add(1, 0, -1), 1, 0, -1)
                    }
                    if (getBlock(pos.add(-1, 0, -1)).block == Blocks.AIR) {
                        perform(pos.add(-1, 0, -1), -1, 0, -1)
                    }
                    if (getBlock(pos.add(0, 0, -2)).block == Blocks.AIR) {
                        if (getBlock(pos.add(0, -1, -2)).block == Blocks.AIR) {
                            perform(pos.add(0, -1, -2), 0, -1, -2)
                        }
                        perform(pos.add(0, 0, -2), 0, 0, -2)
                    }
                    if (getBlock(pos.add(0, 1, -2)).block == Blocks.AIR) {
                        perform(pos.add(0, 1, -2), 0, 1, -2)
                    }
                    if (getBlock(pos.add(0, 1, -1)).block == Blocks.AIR) {
                        perform(pos.add(0, 1, -1), 0, 1, -1)
                    }
                }
            }
//          BETA
//            +x
            if (pos.add(1, 1, 0) == minepos) {
                if (getBlock(pos.add(1, 0, 0)).block != Blocks.BEDROCK) {
                    if (getBlock(pos.add(1, 0, 0)).block == Blocks.AIR) {
                        perform(pos.add(1, 0, 0), 1, 0, 0)
                    }
                }
            }
//            -x
            if (pos.add(-1, 1, 0) == minepos) {
                if (getBlock(pos.add(-1, 0, 0)).block != Blocks.BEDROCK) {
                    if (getBlock(pos.add(-1, 0, 0)).block == Blocks.AIR) {
                        perform(pos.add(-1, 0, 0), -1, 0, 0)
                    }
                }
            }
//            +z
            if (pos.add(0, 1, 1) == minepos) {
                if (getBlock(pos.add(0, 0, 1)).block != Blocks.BEDROCK) {
                    if (getBlock(pos.add(0, 0, 1)).block == Blocks.AIR) {
                        perform(pos.add(0, 0, 1), 0, 0, 1)
                    }
                }
            }
//            -z
            if (pos.add(0, 1, -1) == minepos) {
                if (getBlock(pos.add(0, 0, -1)).block != Blocks.BEDROCK) {
                    if (getBlock(pos.add(0, 0, -1)).block == Blocks.AIR) {
                        perform(pos.add(0, 0, -1), 0, 0, -1)
                    }
                }
            }
//          GAMMA
//            +x-z
            if (pos.add(1, 0, -1) == minepos) {
                if (getBlock(pos.add(1, 0, 0)).block == Blocks.AIR) {
                    if (getBlock(pos.add(1, 0, -1)).block != Blocks.BEDROCK) {
                        if (getBlock(pos.add(2, 0, -1)).block == Blocks.AIR) {
                            perform(pos.add(2, 0, -1), 2, 0, -1)
                        }
                        if (getBlock(pos.add(1, 0, -2)).block == Blocks.AIR) {
                            perform(pos.add(1, 0, -2), 1, 0, -2)
                        }
                        if (getBlock(pos.add(1, 1, -1)).block == Blocks.AIR) {
                            perform(pos.add(1, 1, -1), 1, 1, -1)
                        }
                    }
                }
            }
//            +x+z
            if (pos.add(1, 0, 1) == minepos) {
                if (getBlock(pos.add(1, 0, 0)).block == Blocks.AIR) {
                    if (getBlock(pos.add(1, 0, 1)).block != Blocks.BEDROCK) {
                        if (getBlock(pos.add(2, 0, 1)).block == Blocks.AIR) {
                            perform(pos.add(2, 0, 1), 2, 0, 1)
                        }
                        if (getBlock(pos.add(1, 0, 2)).block == Blocks.AIR) {
                            perform(pos.add(1, 0, 2), 1, 0, 2)
                        }
                        if (getBlock(pos.add(1, 1, 1)).block == Blocks.AIR) {
                            perform(pos.add(1, 1, 1), 1, 1, 1)
                        }
                    }
                }
            }
//            +z-x
            if (pos.add(-1, 0, 1) == minepos) {
                if (getBlock(pos.add(0, 0, 1)).block == Blocks.AIR) {
                    if (getBlock(pos.add(-1, 0, 1)).block != Blocks.BEDROCK) {
                        if (getBlock(pos.add(-1, 0, 2)).block == Blocks.AIR) {
                            perform(pos.add(-1, 0, 2), -1, 0, 2)
                        }
                        if (getBlock(pos.add(-2, 0, 1)).block == Blocks.AIR) {
                            perform(pos.add(-2, 0, 1), -2, 0, 1)
                        }
                        if (getBlock(pos.add(-1, 1, 1)).block == Blocks.AIR) {
                            perform(pos.add(-1, 1, 1), -1, 1, 1)
                        }
                    }
                }
            }
//            -z-x
            if (pos.add(-1, 0, -1) == minepos) {
                if (getBlock(pos.add(0, 0, -1)).block == Blocks.AIR) {
                    if (getBlock(pos.add(-1, 0, -1)).block != Blocks.BEDROCK) {
                        if (getBlock(pos.add(-1, 0, -2)).block == Blocks.AIR) {
                            perform(pos.add(-1, 0, -2), -1, 0, -2)
                        }
                        if (getBlock(pos.add(-2, 0, -1)).block == Blocks.AIR) {
                            perform(pos.add(-2, 0, -1), -2, 0, -1)
                        }
                        if (getBlock(pos.add(-1, 1, -1)).block == Blocks.AIR) {
                            perform(pos.add(-1, 1, -1), -1, 1, -1)
                        }
                    }
                }
            }
//          DELTA
//            +x-z
            if (pos.add(1, 1, -1) == minepos) {
                if (getBlock(pos.add(1, 1, 0)).block == Blocks.AIR) {
                    if (getBlock(pos.add(1, 0, -1)).block != Blocks.BEDROCK) {
                        if (getBlock(pos.add(1, 0, -1)).block == Blocks.AIR) {
                            perform(pos.add(1, 0, -1), 1, 0, -1)
                        }
                    }
                }
            }
//            +x+z
            if (pos.add(1, 1, 1) == minepos) {
                if (getBlock(pos.add(1, 0, 0)).block == Blocks.AIR) {
                    if (getBlock(pos.add(1, 0, 1)).block != Blocks.BEDROCK) {
                        if (getBlock(pos.add(1, 0, 1)).block == Blocks.AIR) {
                            perform(pos.add(1, 0, 1), 1, 0, 1)
                        }
                    }
                }
            }
//            +z-x
            if (pos.add(-1, 1, 1) == minepos) {
                if (getBlock(pos.add(0, 0, 1)).block == Blocks.AIR) {
                    if (getBlock(pos.add(-1, 0, 1)).block != Blocks.BEDROCK) {
                        if (getBlock(pos.add(-1, 0, 1)).block == Blocks.AIR) {
                            perform(pos.add(-1, 0, 1), -1, 0, 1)
                        }
                    }
                }
            }
//            -z-x
            if (pos.add(-1, 1, -1) == minepos) {
                if (getBlock(pos.add(0, 0, -1)).block == Blocks.AIR) {
                    if (getBlock(pos.add(-1, 0, -1)).block != Blocks.BEDROCK) {
                        if (getBlock(pos.add(-1, 0, -1)).block == Blocks.AIR) {
                            perform(pos.add(-1, 0, -1), -1, 0, -1)
                        }
                    }
                }
            }
//          EPSILON
//            +x
            if (pos.add(2, 0, 0) == minepos) {
                if (getBlock(pos.add(1, 0, 0)).block == Blocks.AIR) {
                    if (pos.add(1, 0, 0) == minepos) {
                        if (getBlock(pos.add(2, 0, -1)).block == Blocks.AIR) {
                            perform(pos.add(2, 0, -1), 2, 0, -1)
                        }
                        if (getBlock(pos.add(2, 0, 1)).block == Blocks.AIR) {
                            perform(pos.add(2, 0, 1), 2, 0, 1)
                        }
                        if (getBlock(pos.add(3, 0, 0)).block == Blocks.AIR) {
                            perform(pos.add(3, 0, 0), 3, 0, 0)
                        }
                        if (getBlock(pos.add(3, 1, 0)).block == Blocks.AIR) {
                            perform(pos.add(3, 1, 0), 3, 1, 0)
                        }
                        if (getBlock(pos.add(2, 1, 0)).block == Blocks.AIR) {
                            perform(pos.add(2, 1, 0), 2, 1, 0)
                        }
                    } else {
                        if (getBlock(pos.add(1, 0, 0)).block == Blocks.AIR) {
                            perform(pos.add(1, 0, 0), 1, 0, 0)
                        }
                    }
                }
            }
//            -x
            if (pos.add(-2, 0, 0) == minepos) {
                if (getBlock(pos.add(-1, 0, 0)).block == Blocks.AIR) {
                    if (pos.add(-1, 0, 0) == minepos) {
                        if (getBlock(pos.add(-2, 0, -1)).block == Blocks.AIR) {
                            perform(pos.add(-2, 0, -1), -2, 0, -1)
                        }
                        if (getBlock(pos.add(-2, 0, 1)).block == Blocks.AIR) {
                            perform(pos.add(-2, 0, 1), -2, 0, 1)
                        }
                        if (getBlock(pos.add(-3, 0, 0)).block == Blocks.AIR) {
                            perform(pos.add(-3, 0, 0), -3, 0, 0)
                        }
                        if (getBlock(pos.add(-3, 1, 0)).block == Blocks.AIR) {
                            perform(pos.add(-3, 1, 0), -3, 1, 0)
                        }
                        if (getBlock(pos.add(-2, 1, 0)).block == Blocks.AIR) {
                            perform(pos.add(-2, 1, 0), -2, 1, 0)
                        }
                    } else {
                        if (getBlock(pos.add(-1, 0, 0)).block == Blocks.AIR) {
                            perform(pos.add(-1, 0, 0), -1, 0, 0)
                        }
                    }
                }
            }
//            +z
            if (pos.add(0, 0, 2) == minepos) {
                if (getBlock(pos.add(0, 0, 1)).block == Blocks.AIR) {
                    if (pos.add(0, 0, 1) == minepos) {
                        if (getBlock(pos.add(-1, 0, 2)).block == Blocks.AIR) {
                            perform(pos.add(-1, 0, 2), -1, 0, 2)
                        }
                        if (getBlock(pos.add(1, 0, 2)).block == Blocks.AIR) {
                            perform(pos.add(1, 0, 2), 1, 0, 2)
                        }
                        if (getBlock(pos.add(0, 0, 3)).block == Blocks.AIR) {
                            perform(pos.add(0, 0, 3), 0, 0, 3)
                        }
                        if (getBlock(pos.add(0, 1, 3)).block == Blocks.AIR) {
                            perform(pos.add(0, 1, 3), 0, 1, 3)
                        }
                        if (getBlock(pos.add(0, 1, 2)).block == Blocks.AIR) {
                            perform(pos.add(0, 1, 2), 0, 1, 2)
                        }
                    } else {
                        if (getBlock(pos.add(0, 0, 1)).block == Blocks.AIR) {
                            perform(pos.add(0, 0, 1), 0, 0, 1)
                        }
                    }
                }
            }
//            -z
            if (pos.add(0, 0, -2) == minepos) {
                if (getBlock(pos.add(0, 0, -1)).block == Blocks.AIR) {
                    if (pos.add(0, 0, -1) == minepos) {
                        if (getBlock(pos.add(-1, 0, -2)).block == Blocks.AIR) {
                            perform(pos.add(-1, 0, -2), -1, 0, -2)
                        }
                        if (getBlock(pos.add(1, 0, -2)).block == Blocks.AIR) {
                            perform(pos.add(1, 0, -2), 1, 0, -2)
                        }
                        if (getBlock(pos.add(0, 0, -3)).block == Blocks.AIR) {
                            perform(pos.add(0, 0, -3), 0, 0, -3)
                        }
                        if (getBlock(pos.add(0, 1, -3)).block == Blocks.AIR) {
                            perform(pos.add(0, 1, -3), 0, 1, -3)
                        }
                        if (getBlock(pos.add(0, 1, -2)).block == Blocks.AIR) {
                            perform(pos.add(0, 1, -2), 0, 1, -2)
                        }
                    } else {
                        if (getBlock(pos.add(0, 0, -1)).block == Blocks.AIR) {
                            perform(pos.add(0, 0, -1), 0, 0, -1)
                        }
                    }
                }
            }

//          ZETA
            if (SpeedManager.getPlayerSpeed(mc.player)<=6 && antifaceplace.value){
                if (checkCrystal(a,EntityUtil.getVarOffsets(0,1,1)) != null && getBlock(pos.add(0,0,1)).block == Blocks.OBSIDIAN){
                    EntityUtil.attackEntity(checkCrystal(a,EntityUtil.getVarOffsets(0,1,1)),true)
                    if (getBlock(pos.add(0,1,1)).block == Blocks.AIR){
                        perform(pos.add(0,1,1), 0, 1, 1)
                    }
                }

                if (checkCrystal(a,EntityUtil.getVarOffsets(0,1,-1)) != null && getBlock(pos.add(0,0,-1)).block == Blocks.OBSIDIAN){
                    EntityUtil.attackEntity(checkCrystal(a,EntityUtil.getVarOffsets(0,1,-1)),true)
                    if (getBlock(pos.add(0,1,-1)).block == Blocks.AIR){
                        perform(pos.add(0,1,-1), 0, 1, -1)
                    }
                }

                if (checkCrystal(a,EntityUtil.getVarOffsets(1,1,0)) != null && getBlock(pos.add(1,0,0)).block == Blocks.OBSIDIAN){
                    EntityUtil.attackEntity(checkCrystal(a,EntityUtil.getVarOffsets(1,1,0)),true)
                    if (getBlock(pos.add(1,1,0)).block == Blocks.AIR){
                        perform(pos.add(1,1,0), 1, 1, 0)
                    }
                }

                if (checkCrystal(a,EntityUtil.getVarOffsets(-1, 1,0)) != null && getBlock(pos.add(-1,0,0)).block == Blocks.OBSIDIAN){
                    EntityUtil.attackEntity(checkCrystal(a,EntityUtil.getVarOffsets(-1, 1,0)),true)
                    if (getBlock(pos.add(-1,1,0)).block == Blocks.AIR){
                        perform(pos.add(-1,1,0), -1, 1, 0)
                    }
                }
            }

//          AntiCev's
//            y
            if (pos.add(0, 2, 0) == minepos) {
                if (getBlock(pos.add(0, 3, 0)).block == Blocks.AIR) {
                    perform(pos.add(0, 3, 0), 0, 3, 0)
                }
            }
//            y2
            if (pos.add(0, 3, 0) == minepos) {
                if (getBlock(pos.add(0, 4, 0)).block == Blocks.AIR) {
                    perform(pos.add(0, 4, 0), 0, 4, 0)
                }
            }
//            y3
            if (pos.add(0, 4, 0) == minepos) {
                if (getBlock(pos.add(0, 5, 0)).block == Blocks.AIR) {
                    perform(pos.add(0, 5, 0), 0, 5, 0)
                }
            }
//            +x
            if (pos.add(1, 1, 0) == minepos) {
                if (getBlock(pos.add(1, 2, 0)).block == Blocks.AIR) {
                    perform(pos.add(1, 2, 0), 1, 2, 0)
                }
            }
            if (pos.add(1, 2, 0) == minepos) {
                if (getBlock(pos.add(1, 3, 0)).block == Blocks.AIR) {
                    perform(pos.add(1, 3, 0), 1, 3, 0)
                }
            }
//            -x
            if (pos.add(-1, 1, 0) == minepos) {
                if (getBlock(pos.add(-1, 2, 0)).block == Blocks.AIR) {
                    perform(pos.add(-1, 2, 0), -1, 2, 0)
                }
            }
            if (pos.add(-1, 2, 0) == minepos) {
                if (getBlock(pos.add(-1, 3, 0)).block == Blocks.AIR) {
                    perform(pos.add(-1, 3, 0), -1, 3, 0)
                }
            }
//            +z
            if (pos.add(0, 1, 1) == minepos) {
                if (getBlock(pos.add(0, 2, 1)).block == Blocks.AIR) {
                    perform(pos.add(0, 2, 1), 0, 2, 1)
                }
            }
            if (pos.add(0, 2, 1) == minepos) {
                if (getBlock(pos.add(0, 3, 1)).block == Blocks.AIR) {
                    perform(pos.add(0, 3, 1), 0, 3, 1)
                }
            }
//            -z
            if (pos.add(0, 1, -1) == minepos) {
                if (getBlock(pos.add(0, 2, -1)).block == Blocks.AIR) {
                    perform(pos.add(0, 2, -1), 0, 2, -1)
                }
            }
            if (pos.add(0, 2, -1) == minepos) {
                if (getBlock(pos.add(0, 3, -1)).block == Blocks.AIR) {
                    perform(pos.add(0, 3, -1), 0, 3, -1)
                }
            }
        }
    }

    @SubscribeEvent
    fun onBreak(event: BlockBreakEvent) {
        if (fullNullCheck()) {
            return
        }
        if (event.position != null) {
            breakpos = event.position
        }
        if (!antiSelf.value && event.breakerId == mc.player.entityId) {
            return
        }
        if (!antiFriend.value && FriendManager.isFriend(mc.world.getEntityByID(event.breakerId))) {
            return
        }
        if (event.position != null) {
            minepos = event.position
        }
    }

    private fun getBlock(block: BlockPos): IBlockState {
        return mc.world.getBlockState(block)
    }

    private fun perform(pos: BlockPos, x: Int, y: Int, z: Int) {
        if (fullNullCheck()) return
        if (pos == breakpos) return
        val a: Vec3d = mc.player.positionVector
        if (checkCrystal(a, EntityUtil.getVarOffsets(x,y,z)) != null){
            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(x, y, z)), true)
        }
        if (!world.isPlaceable(pos)) {
            return
        }
        val old = mc.player.inventory.currentItem
        HotbarManager.spoofHotbar(obsidian)
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.value, true, false)
        HotbarManager.spoofHotbar(old)
        if (!this.timer.passedMs(this.delay.value.toLong())) return
    }

    fun World.isPlaceable(pos: BlockPos, ignoreSelfCollide: Boolean = false) =
        this.getBlockState(pos).isReplaceable && this.checkNoEntityCollision(
        AxisAlignedBB(pos),
        if (ignoreSelfCollide) Wrapper.player else null
    )

    private fun checkCrystal(pos: Vec3d?, list: Array<Vec3d>): Entity? {
        var crystal: Entity? = null
        val var5 = list.size
        for (var6 in 0 until var5) {
            val vec3d = list[var6]
            val position = BlockPos(pos!!).add(vec3d.x, vec3d.y, vec3d.z)
            for (entity in mc.world.getEntitiesWithinAABB(
                Entity::class.java, AxisAlignedBB(position)
            )) {
                if (entity !is EntityEnderCrystal || crystal != null) continue
                crystal = entity
            }
        }
        return crystal
    }

}