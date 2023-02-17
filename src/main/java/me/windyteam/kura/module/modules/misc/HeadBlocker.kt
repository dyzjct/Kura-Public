package me.windyteam.kura.module.modules.misc

import kura.utils.Wrapper
import kura.utils.isReplaceable
import kura.utils.world
import me.windyteam.kura.event.events.block.BlockBreakEvent
import me.windyteam.kura.friend.FriendManager
import me.windyteam.kura.manager.HotbarManager
import me.windyteam.kura.manager.SpeedManager
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.utils.Timer
import me.windyteam.kura.utils.block.BlockUtil
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.inventory.InventoryUtil
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
    private val cDelay = isetting("CrystalDelay", 0, 0, 300)
    private val antiCity = bsetting("AntiCity",true)
    private val antiCev = bsetting("AntiCev",true)
    private val antiWeb = bsetting("AntiWeb",true)
    private val antiFriend = bsetting("AntiFriend", false)
    private val antiSelf = bsetting("AntiSelf", false)
    private val antiFacePlace = bsetting("AntiFacePlace",false)
    private val jump = bsetting("Jump",false)
    private var obsidian = -1
    private var minePos: BlockPos? = null
    private var breakPos: BlockPos? = null
    private val timer = Timer()
    private var mc = Minecraft.getMinecraft()

    override fun onUpdate() {
        if (fullNullCheck()) return
        if (mc.player == null || mc.world == null) {
            return
        }
        val pos = BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)
        if (InventoryUtil.getItemHotbar(Items.DIAMOND_SWORD) != -1 && antiWeb.value) {
            if (getBlock(pos.add(0, -1, 0)).block == Blocks.WEB && pos.add(0, -1, 0) != breakPos) {
                Module.mc.playerController.onPlayerDamageBlock(
                    pos.add(0, -1, 0),
                    BlockUtil.getRayTraceFacing(pos.add(0, -1, 0))
                )
            } else if (getBlock(pos.add(0, 0, 0)).block == Blocks.WEB && pos.add(0, 0, 0) != breakPos) {
                Module.mc.playerController.onPlayerDamageBlock(pos.add(0, 0, 0), BlockUtil.getRayTraceFacing(pos.add(0, 0, 0)))
            } else if (getBlock(pos.add(0, 1, 0)).block == Blocks.WEB && pos.add(0, 1, 0) != breakPos) {
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
        if (minePos != null && antiCity.value) {
//          AntiCity's:
//          Alpha
//            +x
            if (pos.add(1, 0, 0) == minePos) {
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
            if (pos.add(-1, 0, 0) == minePos) {
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
            if (pos.add(0, 0, 1) == minePos) {
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
            if (pos.add(0, 0, -1) == minePos) {
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
            if (pos.add(1, 1, 0) == minePos) {
                if (getBlock(pos.add(1, 0, 0)).block != Blocks.BEDROCK) {
                    if (getBlock(pos.add(1, 0, 0)).block == Blocks.AIR) {
                        perform(pos.add(1, 0, 0), 1, 0, 0)
                    }
                }
            }
//            -x
            if (pos.add(-1, 1, 0) == minePos) {
                if (getBlock(pos.add(-1, 0, 0)).block != Blocks.BEDROCK) {
                    if (getBlock(pos.add(-1, 0, 0)).block == Blocks.AIR) {
                        perform(pos.add(-1, 0, 0), -1, 0, 0)
                    }
                }
            }
//            +z
            if (pos.add(0, 1, 1) == minePos) {
                if (getBlock(pos.add(0, 0, 1)).block != Blocks.BEDROCK) {
                    if (getBlock(pos.add(0, 0, 1)).block == Blocks.AIR) {
                        perform(pos.add(0, 0, 1), 0, 0, 1)
                    }
                }
            }
//            -z
            if (pos.add(0, 1, -1) == minePos) {
                if (getBlock(pos.add(0, 0, -1)).block != Blocks.BEDROCK) {
                    if (getBlock(pos.add(0, 0, -1)).block == Blocks.AIR) {
                        perform(pos.add(0, 0, -1), 0, 0, -1)
                    }
                }
            }
//          GAMMA
//            +x-z
            if (pos.add(1, 0, -1) == minePos) {
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
            if (pos.add(1, 0, 1) == minePos) {
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
            if (pos.add(-1, 0, 1) == minePos) {
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
            if (pos.add(-1, 0, -1) == minePos) {
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
            if (pos.add(1, 1, -1) == minePos) {
                if (getBlock(pos.add(1, 1, 0)).block == Blocks.AIR) {
                    if (getBlock(pos.add(1, 0, -1)).block != Blocks.BEDROCK) {
                        if (getBlock(pos.add(1, 0, -1)).block == Blocks.AIR) {
                            perform(pos.add(1, 0, -1), 1, 0, -1)
                        }
                    }
                }
            }
//            +x+z
            if (pos.add(1, 1, 1) == minePos) {
                if (getBlock(pos.add(1, 0, 0)).block == Blocks.AIR) {
                    if (getBlock(pos.add(1, 0, 1)).block != Blocks.BEDROCK) {
                        if (getBlock(pos.add(1, 0, 1)).block == Blocks.AIR) {
                            perform(pos.add(1, 0, 1), 1, 0, 1)
                        }
                    }
                }
            }
//            +z-x
            if (pos.add(-1, 1, 1) == minePos) {
                if (getBlock(pos.add(0, 0, 1)).block == Blocks.AIR) {
                    if (getBlock(pos.add(-1, 0, 1)).block != Blocks.BEDROCK) {
                        if (getBlock(pos.add(-1, 0, 1)).block == Blocks.AIR) {
                            perform(pos.add(-1, 0, 1), -1, 0, 1)
                        }
                    }
                }
            }
//            -z-x
            if (pos.add(-1, 1, -1) == minePos) {
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
            if (pos.add(2, 0, 0) == minePos) {
                if (getBlock(pos.add(1, 0, 0)).block == Blocks.AIR) {
                    if (pos.add(1, 0, 0) == minePos) {
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
            if (pos.add(-2, 0, 0) == minePos) {
                if (getBlock(pos.add(-1, 0, 0)).block == Blocks.AIR) {
                    if (pos.add(-1, 0, 0) == minePos) {
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
            if (pos.add(0, 0, 2) == minePos) {
                if (getBlock(pos.add(0, 0, 1)).block == Blocks.AIR) {
                    if (pos.add(0, 0, 1) == minePos) {
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
            if (pos.add(0, 0, -2) == minePos) {
                if (getBlock(pos.add(0, 0, -1)).block == Blocks.AIR) {
                    if (pos.add(0, 0, -1) == minePos) {
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

//          AntiFacePlaceCrystal
            if (SpeedManager.getPlayerSpeed(mc.player)<=6 && antiFacePlace.value && this.timer.passedMs(this.cDelay.value.toLong())){
                val antiList = ArrayList<Int>()
                antiList.add(-1)
                antiList.add(1)

                for (x in antiList) {
                    for (z in antiList) {
                        if (checkCrystal(a, EntityUtil.getVarOffsets(x, 1, 0)) != null && getBlock(pos.add(x, 0, 0)).block != Blocks.BEDROCK) {
                            if (getBlock(pos.add(x, 1, 0)).block == Blocks.AIR) {
                                facePlace(pos.add(x, 1, 0), x, 1, 0)
                                break
                            }
                        }
                        if (checkCrystal(a, EntityUtil.getVarOffsets(0, 1, z)) != null  && getBlock(pos.add(0, 0, z)).block != Blocks.BEDROCK) {
                            if (getBlock(pos.add(0, 1, z)).block == Blocks.AIR) {
                                facePlace(pos.add(0, 1, z), 0, 1, z)
                                break
                            }
                        }
                    }
                }
            }
        }
        if (minePos != null && antiCev.value){
            //          AntiCev's
            val a: Vec3d = mc.player.positionVector
//            y
            if (pos.add(0, 2, 0) == minePos) {
                if (getBlock(pos.add(0, 3, 0)).block == Blocks.AIR) {
                    if (checkCrystal(a, EntityUtil.getVarOffsets(0,3,0)) != null){
                        if (jump.value){
                            mc.player.jump()
                        }
                        if (checkCrystal(a, EntityUtil.getVarOffsets(0,3,0)) != null && this.timer.passedMs(this.cDelay.value.toLong())){
                            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(0,3,0)), true)
                        }
                    }
                    facePlace(pos.add(0, 3, 0), 0, 3, 0)
                }
            }
//            y2
            if (pos.add(0, 3, 0) == minePos) {
                if (getBlock(pos.add(0, 4, 0)).block == Blocks.AIR) {
                    if (checkCrystal(a, EntityUtil.getVarOffsets(0,4,0)) != null){
                        if (jump.value && getBlock(pos.add(0,2,0)).block != Blocks.AIR){
                            mc.player.jump()
                        }
                        if (checkCrystal(a, EntityUtil.getVarOffsets(0,4,0)) != null && this.timer.passedMs(this.cDelay.value.toLong())){
                            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(0,4,0)), true)
                        }
                    }
                    facePlace(pos.add(0, 4, 0), 0, 4, 0)
                }
            }
//            y3
            if (pos.add(0, 4, 0) == minePos) {
                if (getBlock(pos.add(0, 5, 0)).block == Blocks.AIR) {
                    if (checkCrystal(a, EntityUtil.getVarOffsets(0,5,0)) != null){
                        if (jump.value && getBlock(pos.add(0,2,0)).block != Blocks.AIR){
                            mc.player.jump()
                        }
                        if (checkCrystal(a, EntityUtil.getVarOffsets(0,5,0)) != null && this.timer.passedMs(this.cDelay.value.toLong())){
                            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(0,5,0)), true)
                        }
                    }
                    facePlace(pos.add(0, 5, 0), 0, 5, 0)
                }
            }
//            +x
            if (pos.add(1, 1, 0) == minePos) {
                if (this.timer.passedMs(this.cDelay.value.toLong())){
                    if (checkCrystal(a, EntityUtil.getVarOffsets(1,2,0)) != null){
                        EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(1,2,0)), true)
                    }
                }
                if (getBlock(pos.add(1, 2, 0)).block == Blocks.AIR) {
                    facePlace(pos.add(1, 2, 0), 1, 2, 0)
                }
            }
            if (pos.add(1, 2, 0) == minePos) {
                if (this.timer.passedMs(this.cDelay.value.toLong())){
                    if (checkCrystal(a, EntityUtil.getVarOffsets(1,3,0)) != null){
                        EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(1,3,0)), true)
                    }
                }
                if (getBlock(pos.add(1, 3, 0)).block == Blocks.AIR) {
                    facePlace(pos.add(1, 3, 0), 1, 3, 0)
                }
            }
//            -x
            if (pos.add(-1, 1, 0) == minePos) {
                if (this.timer.passedMs(this.cDelay.value.toLong())){
                    if (checkCrystal(a, EntityUtil.getVarOffsets(-1,2,0)) != null){
                        EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(-1,2,0)), true)
                    }
                }
                if (getBlock(pos.add(-1, 2, 0)).block == Blocks.AIR) {
                    facePlace(pos.add(-1, 2, 0), -1, 2, 0)
                }
            }
            if (pos.add(-1, 2, 0) == minePos) {
                if (this.timer.passedMs(this.cDelay.value.toLong())){
                    if (checkCrystal(a, EntityUtil.getVarOffsets(-1,3,0)) != null){
                        EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(-1,3,0)), true)
                    }
                }
                if (getBlock(pos.add(-1, 3, 0)).block == Blocks.AIR) {
                    facePlace(pos.add(-1, 3, 0), -1, 3, 0)
                }
            }
//            +z
            if (pos.add(0, 1, 1) == minePos) {
                if (this.timer.passedMs(this.cDelay.value.toLong())){
                    if (checkCrystal(a, EntityUtil.getVarOffsets(0,2,1)) != null){
                        EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(0,2,1)), true)
                    }
                }
                if (getBlock(pos.add(0, 2, 1)).block == Blocks.AIR) {
                    facePlace(pos.add(0, 2, 1), 0, 2, 1)
                }
            }
            if (pos.add(0, 2, 1) == minePos) {
                if (getBlock(pos.add(0, 3, 1)).block == Blocks.AIR) {
                    if (this.timer.passedMs(this.cDelay.value.toLong())){
                        if (checkCrystal(a, EntityUtil.getVarOffsets(0,3,1)) != null){
                            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(0,3,1)), true)
                        }
                    }
                    facePlace(pos.add(0, 3, 1), 0, 3, 1)
                }
            }
//            -z
            if (pos.add(0, 1, -1) == minePos) {
                if (getBlock(pos.add(0, 2, -1)).block == Blocks.AIR) {
                    if (this.timer.passedMs(this.cDelay.value.toLong())){
                        if (checkCrystal(a, EntityUtil.getVarOffsets(0,2,-1)) != null){
                            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(0,2,-1)), true)
                        }
                    }
                    facePlace(pos.add(0, 2, -1), 0, 2, -1)
                }
            }
            if (pos.add(0, 2, -1) == minePos) {
                if (getBlock(pos.add(0, 3, -1)).block == Blocks.AIR) {
                    if (this.timer.passedMs(this.cDelay.value.toLong())){
                        if (checkCrystal(a, EntityUtil.getVarOffsets(0,3,-1)) != null){
                            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(0,3,-1)), true)
                        }
                    }
                    facePlace(pos.add(0, 3, -1), 0, 3, -1)
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
            breakPos = event.position
        }
        if (!antiSelf.value && event.breakerId == mc.player.entityId) {
            return
        }
        if (!antiFriend.value && FriendManager.isFriend(mc.world.getEntityByID(event.breakerId))) {
            return
        }
        if (event.position != null) {
            minePos = event.position
        }
    }

    private fun getBlock(block: BlockPos): IBlockState {
        return mc.world.getBlockState(block)
    }

    private fun perform(pos: BlockPos, x: Int, y: Int, z: Int) {
        if (fullNullCheck()) return
        if (getBlock(pos).block != Blocks.AIR) return
        if (pos == breakPos) return
        if (pos == InstantMine.breakPos) return
        if (!this.timer.passedMs(this.delay.value.toLong())) return
        val a: Vec3d = mc.player.positionVector
        if (checkCrystal(a, EntityUtil.getVarOffsets(x,y,z)) != null && this.timer.passedMs(this.cDelay.value.toLong())){
            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(x, y, z)), true)
        }
        if (!world.isPlaceable(pos)) {
            return
        }
        val old = mc.player.inventory.currentItem
        HotbarManager.spoofHotbar(obsidian)
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.value, true, false)
        HotbarManager.spoofHotbar(old)
    }
    private fun facePlace(pos: BlockPos, x: Int, y: Int, z: Int) {
        if (fullNullCheck()) return
        if (pos == breakPos) return
        if (pos == InstantMine.breakPos) return
        if (!this.timer.passedMs(this.delay.value.toLong())) return
        val a: Vec3d = mc.player.positionVector
        if (checkCrystal(a, EntityUtil.getVarOffsets(x,y,z)) != null){
            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(x, y, z)), true)
        }
        val old = mc.player.inventory.currentItem
        HotbarManager.spoofHotbar(obsidian)
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.value, true, false)
        HotbarManager.spoofHotbar(old)
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