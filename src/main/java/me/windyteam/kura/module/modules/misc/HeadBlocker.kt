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
import me.windyteam.kura.module.modules.crystalaura.KuraAura
import me.windyteam.kura.utils.Timer
import me.windyteam.kura.utils.block.BlockUtil2
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
    private val cdelay = isetting("CrystalDelay", 0, 0, 300)
    private val antiFriend = bsetting("AntiFriend", false)
    private val antiSelf = bsetting("AntiSelf", false)
    private val antifaceplace = bsetting("AntiFacePlace",false)
    private val jump = bsetting("Jump",false)
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
                    BlockUtil2.getRayTraceFacing(pos.add(0, -1, 0))
                )
            } else if (getBlock(pos.add(0, 0, 0)).block == Blocks.WEB && pos.add(0, 0, 0) != breakpos) {
                Module.mc.playerController.onPlayerDamageBlock(pos.add(0, 0, 0), BlockUtil2.getRayTraceFacing(pos.add(0, 0, 0)))
            } else if (getBlock(pos.add(0, 1, 0)).block == Blocks.WEB && pos.add(0, 1, 0) != breakpos) {
                Module.mc.playerController.onPlayerDamageBlock(pos.add(0, 1, 0), BlockUtil2.getRayTraceFacing(pos.add(0, 1, 0)))
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
//            val antiList = ArrayList<Int>()
//            val antiList2 = ArrayList<Int>()
//            val antiList3 = ArrayList<Int>()
//            antiList.add(-2)
//            antiList.add(2)
//            antiList.add(-1)
//            antiList.add(1)
//            antiList2.add(-1)
//            antiList2.add(1)
//            antiList2.add(-2)
//            antiList2.add(2)
//            antiList3.add(-3)
//            antiList3.add(3)
//            antiList.forEach(){ a ->
//                antiList2.forEach(){ b ->
//                    antiList3.forEach() { c ->
//                        if (pos.add(a,0,0) == minepos){
//                            if (getBlock(pos.add(b,0,0)).block == Blocks.AIR || getBlock(pos.add(b,0,0)).block == Blocks.AIR && pos.add(b,0,0) == minepos){
//                                if (getBlock(pos.add(a,0,b)).block == Blocks.AIR){
//                                    perform(pos.add(a,0,b),a,0,b)
//                                }
//                                if (getBlock(pos.add(c,0,0)).block == Blocks.AIR){
//                                    perform(pos.add(c,0,0),c,0,0)
//                                }
//                                if (getBlock(pos.add(c,1,0)).block == Blocks.AIR){
//                                    perform(pos.add(c,1,0),c,1,0)
//                                }
//                                if (getBlock(pos.add(a,1,0)).block == Blocks.AIR){
//                                    perform(pos.add(a,1,0),a,1,0)
//                                }
//                                if (getBlock(pos.add(b,1,0)).block == Blocks.AIR){
//                                    perform(pos.add(b,1,0),b,1,0)
//                                }
//                            } else {
//                                if (getBlock(pos.add(b,0,b)).block == Blocks.AIR){
//                                    perform(pos.add(b,0,b),b,0,0)
//                                }
//                            }
//                        }
//                    }
//                }
//            }
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

//          AntiFacePlaceCrystal
            if (SpeedManager.getPlayerSpeed(mc.player)<=6 && antifaceplace.value && this.timer.passedMs(this.cdelay.value.toLong())){
                val antiList = ArrayList<Int>()
                antiList.add(-1)
                antiList.add(1)

                antiList.forEach { x ->
                    antiList.forEach { z ->
                        if (checkCrystal(a, EntityUtil.getVarOffsets(x, 1, 0)) != null && getBlock(pos.add(x, 0, 0)).block != Blocks.BEDROCK) {
                            if (getBlock(pos.add(x, 1, 0)).block == Blocks.AIR) {
                                EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(x, 1, 0)), true)
                                facePlace(pos.add(x, 1, 0), x, 1, 0)
                            }
                        }
                        if (checkCrystal(a, EntityUtil.getVarOffsets(0, 1, z)) != null  && getBlock(pos.add(0, 0, z)).block != Blocks.BEDROCK) {
                            if (getBlock(pos.add(0, 1, z)).block == Blocks.AIR) {
                                EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(0, 1, z)), true)
                                facePlace(pos.add(0, 1, z), 0, 1, z)
                            }
                        }
                    }
                }
            }

//          AntiCev's
            val a: Vec3d = mc.player.positionVector
//            y
            if (pos.add(0, 2, 0) == minepos) {
                if (getBlock(pos.add(0, 3, 0)).block == Blocks.AIR) {
                    if (checkCrystal(a, EntityUtil.getVarOffsets(0,3,0)) != null){
                        if (jump.value){
                            mc.player.jump()
                        }
                        if (checkCrystal(a, EntityUtil.getVarOffsets(0,3,0)) != null && this.timer.passedMs(this.cdelay.value.toLong())){
                            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(0,3,0)), true)
                        }
                    }
                    facePlace(pos.add(0, 3, 0), 0, 3, 0)
                }
            }
//            y2
            if (pos.add(0, 3, 0) == minepos) {
                if (getBlock(pos.add(0, 4, 0)).block == Blocks.AIR) {
                    if (checkCrystal(a, EntityUtil.getVarOffsets(0,4,0)) != null){
                        if (jump.value && getBlock(pos.add(0,2,0)).block != Blocks.AIR){
                            mc.player.jump()
                        }
                        if (checkCrystal(a, EntityUtil.getVarOffsets(0,4,0)) != null && this.timer.passedMs(this.cdelay.value.toLong())){
                            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(0,4,0)), true)
                        }
                    }
                    facePlace(pos.add(0, 4, 0), 0, 4, 0)
                }
            }
//            y3
            if (pos.add(0, 4, 0) == minepos) {
                if (getBlock(pos.add(0, 5, 0)).block == Blocks.AIR) {
                    if (checkCrystal(a, EntityUtil.getVarOffsets(0,5,0)) != null){
                        if (jump.value && getBlock(pos.add(0,2,0)).block != Blocks.AIR){
                            mc.player.jump()
                        }
                        if (checkCrystal(a, EntityUtil.getVarOffsets(0,5,0)) != null && this.timer.passedMs(this.cdelay.value.toLong())){
                            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(0,5,0)), true)
                        }
                    }
                    facePlace(pos.add(0, 5, 0), 0, 5, 0)
                }
            }
//            +x
            if (pos.add(1, 1, 0) == minepos) {
                if (this.timer.passedMs(this.cdelay.value.toLong())){
                    if (checkCrystal(a, EntityUtil.getVarOffsets(1,2,0)) != null){
                        EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(1,2,0)), true)
                    }
                }
                if (getBlock(pos.add(1, 2, 0)).block == Blocks.AIR) {
                    facePlace(pos.add(1, 2, 0), 1, 2, 0)
                }
            }
            if (pos.add(1, 2, 0) == minepos) {
                if (this.timer.passedMs(this.cdelay.value.toLong())){
                    if (checkCrystal(a, EntityUtil.getVarOffsets(1,3,0)) != null){
                        EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(1,3,0)), true)
                    }
                }
                if (getBlock(pos.add(1, 3, 0)).block == Blocks.AIR) {
                    facePlace(pos.add(1, 3, 0), 1, 3, 0)
                }
            }
//            -x
            if (pos.add(-1, 1, 0) == minepos) {
                if (this.timer.passedMs(this.cdelay.value.toLong())){
                    if (checkCrystal(a, EntityUtil.getVarOffsets(-1,2,0)) != null){
                        EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(-1,2,0)), true)
                    }
                }
                if (getBlock(pos.add(-1, 2, 0)).block == Blocks.AIR) {
                    facePlace(pos.add(-1, 2, 0), -1, 2, 0)
                }
            }
            if (pos.add(-1, 2, 0) == minepos) {
                if (this.timer.passedMs(this.cdelay.value.toLong())){
                    if (checkCrystal(a, EntityUtil.getVarOffsets(-1,3,0)) != null){
                        EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(-1,3,0)), true)
                    }
                }
                if (getBlock(pos.add(-1, 3, 0)).block == Blocks.AIR) {
                    facePlace(pos.add(-1, 3, 0), -1, 3, 0)
                }
            }
//            +z
            if (pos.add(0, 1, 1) == minepos) {
                if (this.timer.passedMs(this.cdelay.value.toLong())){
                    if (checkCrystal(a, EntityUtil.getVarOffsets(0,2,1)) != null){
                        EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(0,2,1)), true)
                    }
                }
                if (getBlock(pos.add(0, 2, 1)).block == Blocks.AIR) {
                    facePlace(pos.add(0, 2, 1), 0, 2, 1)
                }
            }
            if (pos.add(0, 2, 1) == minepos) {
                if (getBlock(pos.add(0, 3, 1)).block == Blocks.AIR) {
                    if (this.timer.passedMs(this.cdelay.value.toLong())){
                        if (checkCrystal(a, EntityUtil.getVarOffsets(0,3,1)) != null){
                            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(0,3,1)), true)
                        }
                    }
                    facePlace(pos.add(0, 3, 1), 0, 3, 1)
                }
            }
//            -z
            if (pos.add(0, 1, -1) == minepos) {
                if (getBlock(pos.add(0, 2, -1)).block == Blocks.AIR) {
                    if (this.timer.passedMs(this.cdelay.value.toLong())){
                        if (checkCrystal(a, EntityUtil.getVarOffsets(0,2,-1)) != null){
                            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(0,2,-1)), true)
                        }
                    }
                    facePlace(pos.add(0, 2, -1), 0, 2, -1)
                }
            }
            if (pos.add(0, 2, -1) == minepos) {
                if (getBlock(pos.add(0, 3, -1)).block == Blocks.AIR) {
                    if (this.timer.passedMs(this.cdelay.value.toLong())){
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
        if (getBlock(pos).block != Blocks.AIR) return
        if (pos == breakpos) return
        if (pos == InstantMine.breakPos) return
        if (!this.timer.passedMs(this.delay.value.toLong())) return
        val a: Vec3d = mc.player.positionVector
        if (checkCrystal(a, EntityUtil.getVarOffsets(x,y,z)) != null && this.timer.passedMs(this.cdelay.value.toLong())){
            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(x, y, z)), true)
        }
        if (!world.isPlaceable(pos)) {
            return
        }
        val old = mc.player.inventory.currentItem
        HotbarManager.spoofHotbar(obsidian)
        BlockUtil2.placeBlock(pos, EnumHand.MAIN_HAND, rotate.value, true, false)
        HotbarManager.spoofHotbar(old)
    }
    private fun facePlace(pos: BlockPos, x: Int, y: Int, z: Int) {
        if (fullNullCheck()) return
        if (pos == breakpos) return
        if (pos == InstantMine.breakPos) return
        if (!this.timer.passedMs(this.delay.value.toLong())) return
        val a: Vec3d = mc.player.positionVector
        if (checkCrystal(a, EntityUtil.getVarOffsets(x,y,z)) != null){
            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(x, y, z)), true)
        }
        val old = mc.player.inventory.currentItem
        HotbarManager.spoofHotbar(obsidian)
        BlockUtil2.placeBlock(pos, EnumHand.MAIN_HAND, rotate.value, true, false)
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