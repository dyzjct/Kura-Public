package me.windyteam.kura.module.modules.crystalaura

import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.event.events.render.RenderEvent
import me.windyteam.kura.friend.FriendManager
import me.windyteam.kura.manager.FontManager
import me.windyteam.kura.manager.GuiManager
import me.windyteam.kura.manager.HotbarManager.spoofHotbar
import me.windyteam.kura.manager.HotbarManager.spoofHotbarBypass
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.ModuleManager
import me.windyteam.kura.module.modules.chat.AutoGG
import me.windyteam.kura.module.modules.combat.AutoMend
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.CrystalChainPop
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.CrystalDamageCalculator.Companion.calcDamage
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.CrystalHelper.Companion.PredictionHandlerNew
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.CrystalHelper.Companion.checkBreakRange
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.CrystalHelper.Companion.getCrystalPlacingBB
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.CrystalHelper.Companion.isReplaceable
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.CrystalHelper.Companion.placeBoxIntersectsCrystalBox
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.CrystalHelper.Companion.scaledHealth
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.CrystalHelper.Companion.shouldForcePlace
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.CrystalHelper.Companion.totalHealth
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.CrystalTarget
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.FastRayTrace.Companion.rayTraceVisible
import me.windyteam.kura.module.modules.player.PacketMine
import me.windyteam.kura.utils.TimerUtils
import me.windyteam.kura.utils.animations.BlockEasingRender
import me.windyteam.kura.utils.animations.sq
import me.windyteam.kura.utils.block.BlockInteractionHelper
import me.windyteam.kura.utils.entity.CrystalUtil
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.getMiningSide
import me.windyteam.kura.utils.gl.MelonTessellator
import me.windyteam.kura.utils.gl.XG42Tessellator
import me.windyteam.kura.utils.inventory.InventoryUtil
import me.windyteam.kura.utils.mc.BlockUtil
import me.windyteam.kura.utils.mc.ChatUtil
import me.windyteam.kura.utils.render.RenderUtil
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.item.EntityEnderPearl
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityArrow
import net.minecraft.entity.projectile.EntityEgg
import net.minecraft.entity.projectile.EntitySnowball
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.init.MobEffects
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemAppleGold
import net.minecraft.item.ItemStack
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.network.play.server.*
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockPos.MutableBlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import java.util.stream.Collectors
import kotlin.math.abs
import kotlin.math.floor

// lol

@Module.Info(name = "AutoCrystal", category = Category.XDDD, description = "Test Strict :)")
object AutoCrystal : Module() {
    var p = msetting("Page", Page.GENERAL)

    //Page GENERAL
    private var crystalMod = msetting("CrystalMod", CrystalMod.PlaceBreak).m(p, Page.GENERAL)
    private var switchMode = msetting("SwitchMode", Switch.GhostHand).m(p, Page.GENERAL)
    private var antiWeakness = msetting("AntiWeakness", AntiWeaknessMode.Spoof).m(p, Page.GENERAL)
    private var swingMode = msetting("Swing", SwingMode.Auto).m(p, Page.GENERAL)
    private var rotate = bsetting("Rotate", false).m(p, Page.GENERAL)
    private var yawStep = bsetting("YawStep", false).b(rotate).m(p, Page.GENERAL)
    private var yawAngle = fsetting("YawAngle", 0.1f, 0.1f, 0.5f).b(rotate).b(yawStep).m(p, Page.GENERAL)
    private var yawTicks = isetting("YawTicks", 1, 1, 5).b(rotate).b(yawStep).m(p, Page.GENERAL)

    //Page Place
    private var packetPlaceMode = msetting("PacketMode", PacketPlaceMode.Off).m(p, Page.PLACE)
    private var endCrystal = bsetting("1.13Place", false).m(p, Page.PLACE)
    private var placeSwing = bsetting("PlaceSwing", false).m(p, Page.PLACE)
    private var placeSpeed = isetting("PlaceSpeed", 34, 1, 40).m(p, Page.PLACE)
    private var placeRange = isetting("PlaceRange", 6, 0, 6).m(p, Page.PLACE)
    private var minDamage = isetting("PlaceMinDmg", 4, 0, 36).m(p, Page.PLACE)
    private var placeMaxSelf = isetting("PlaceMaxSelfDmg", 10, 0, 36).m(p, Page.PLACE)
    private var strictDirection = bsetting("StrictDirection", false).m(p, Page.PLACE)

    //Page Break
    private var packetExplode = bsetting("PacketExplode", true).m(p, Page.BREAK)
    private var packetDelay = isetting("PacketDelay", 45, 0, 500).b(packetExplode).m(p, Page.BREAK)
    private var hitDelay = isetting("HitDelay", 50, 0, 500).m(p, Page.BREAK)
    private var predictHitFactor = isetting("PredictHitFactor", 0, 0, 20).m(p, Page.BREAK)
    private var breakRange = isetting("BreakRange", 6, 0, 6).m(p, Page.BREAK)
    private var breakMinDmg = isetting("BreakMinDmg", 2, 0, 36).m(p, Page.BREAK)
    private var breakMaxSelf = isetting("BreakMaxSelf", 12, 0, 36).m(p, Page.BREAK)

    //Page Calculation
    private var motionPredict = bsetting("MotionPredict", true).m(p, Page.CALCULATION)
    private var predictTicks = isetting("PredictTicks", 8, 1, 20).b(motionPredict).m(p, Page.CALCULATION)
    private var debug = bsetting("Debug", false).m(p, Page.CALCULATION)
    private var enemyRange = isetting("EnemyRange", 8, 1, 10).m(p, Page.CALCULATION)
    private var noSuicide = fsetting("NoSuicide", 2f, 0f, 20f).m(p, Page.CALCULATION)
    private var wallRange = fsetting("WallRange", 3f, 0f, 8f).m(p, Page.CALCULATION)

    //Page Force
    private var slowFP = bsetting("SlowFacePlace", false).m(p, Page.FORCE)
    private var fpDelay = isetting("FacePlaceDelay", 275, 1, 750).b(slowFP).m(p, Page.FORCE)
    private var forceHealth = isetting("ForceHealth", 2, 0, 20).m(p, Page.FORCE)
    private var forcePlaceMotion = fsetting("ForcePlaceMotion", 4f, 0.25f, 10f).m(p, Page.FORCE)
    private var armorRate = isetting("ForceArmor%", 25, 0, 100).m(p, Page.FORCE)
    private var forcePlaceDmg = dsetting("ForcePlaceDamage", 0.5, 0.1, 10.0).m(p, Page.FORCE)
    private var forcePop = bsetting("ForcePop", false).m(p, Page.FORCE)

    //Page Lethal
    private var lethalOverride = bsetting("LethalOverride", true).m(p, Page.LETHAL)
    private var lethalBalance = fsetting("LethalBalance", 0.5f, -5f, 5f).b(lethalOverride).m(p, Page.LETHAL)
    private var lethalMaxDamage = fsetting("LethalMaxDamage", 16f, 0f, 20f).b(lethalOverride).m(p, Page.LETHAL)
    private var syncHurtTime = bsetting("SyncHurtTime", true).m(p, Page.LETHAL)
    private var chainPop = bsetting("ChainPop", false).m(p, Page.LETHAL)
    private var chainPopRange = isetting("ChainPopRange", 6, 0, 6).b(chainPop).m(p, Page.LETHAL)
    private var chainPopFactor = isetting("ChainPopFactor", 3, 1, 8).b(chainPop).m(p, Page.LETHAL)
    private var chainPopDamage = dsetting("ChainPopDamage", 0.5, 0.1, 20.0).b(chainPop).m(p, Page.LETHAL)
    private var chainPopTime = isetting("ChainPopTime", 350, 1, 1000).b(chainPop).m(p, Page.LETHAL)
    private var antiSurround = bsetting("AntiSurround", true).m(p, Page.LETHAL)

    //Page Render
    private var hudInfoMod = msetting("HudInfo", Mode.Target).m(p, Page.RENDER)
    private var renderTarget = bsetting("RenderTarget",false).m(p, Page.RENDER)
    private var outline = bsetting("Outline", true).m(p, Page.RENDER)
    private var renderText = bsetting("RenderText", true).m(p, Page.RENDER)
    private var customFont = bsetting("CustomFont", false).b(renderText).m(p, Page.RENDER)
    private var textMode = msetting("TextMode", TextMode.Damage).b(renderText).m(p, Page.RENDER)
    private var textcolor = csetting("TextColor", Color(255, 225, 255)).m(p, Page.RENDER).b(renderText)
    private var textscalex = isetting("TextScaleX", 1, 0, 5).b(renderText).m(p, Page.RENDER)
    private var textscaley = isetting("TextScaleY", 1, 0, 5).b(renderText).m(p, Page.RENDER)
    private var textscalez = isetting("TextScaleZ", 1, 0, 5).b(renderText).m(p, Page.RENDER)
    private var renderBreak = bsetting("RenderBreak", true).m(p, Page.RENDER)
    private var xg42OutLineMod = bsetting("XG42OutLineMod", true).m(p, Page.RENDER)
    private var color = csetting("Color", Color(20, 225, 219)).m(p, Page.RENDER)
    private var alpha = isetting("Alpha", 70, 0, 255).m(p, Page.RENDER)
    private var breakAlpha = isetting("BreakAlpha", 70, 0, 255).m(p, Page.RENDER)
    private var rainbow = bsetting("Rainbow", false).m(p, Page.RENDER)
    private var rgbSpeed = fsetting("RGBSpeed", 8f, 0f, 255f).b(rainbow).m(p, Page.RENDER)
    private var saturation = fsetting("Saturation", 0.5f, 0f, 1f).b(rainbow).m(p, Page.RENDER)
    private var brightness = fsetting("Brightness", 1f, 0f, 1f).b(rainbow).m(p, Page.RENDER)
    private var outLineWidth = fsetting("OutLineWidth", 1f, 0f, 5f).m(p, Page.RENDER)
    private var movingLength = fsetting("MovingLength", 350f, 1f, 1000f).m(p, Page.RENDER)
    private var renderMode = msetting("RenderMode", RenderModes.Glide).m(p, Page.RENDER)
    private var blockRenderSmooth = BlockEasingRender(BlockPos(0, 0, 0), 650f, 400f)

    @Transient
    var lastEntityID = AtomicInteger(-1)
    private var packetExplodeTimerUtils = TimerUtils()
    private var explodeTimerUtils = TimerUtils()
    private var placeTimerUtils = TimerUtils()
    private var calcTimerUtils = TimerUtils()
    private var fPDelay = TimerUtils()
    private var lastCrystal: EntityEnderCrystal? = null
    private var crystalTarget: CrystalTarget? = null
    private var predictionTarget: Vec3d? = null
    private var tempSpawnPos: BlockPos? = null
    var render: BlockPos? = null
    private var breakrender: BlockPos? = null
    private var shouldShadeRender = false
    private var switchCooldown = false
    private var canPredictHit = false
    private var isFacePlacing = false
    private lateinit var rotations: FloatArray
    private var pitchTicksPassed = 0
    private var yawTicksPassed = 0
    private var newSlot = -1
    private var cSlot = -1
    private var damageCa = 0
    private var popTicks = 0
    private var selfDamageCA = 0.0
    private var popList = ConcurrentHashMap<CrystalChainPop, Long>()
    private var damageCA = 0.0
    private var shouldInfoLastBreak = false
    private var lastBreakTime = 0L
    private var infoBreakTime = 0L
    private var offsetFacing = arrayOf(EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST)
    private var attackingCrystal: EntityEnderCrystal? = null
    private var ticks = 0

    @SubscribeEvent
    fun onClientDisconnect(event: ClientDisconnectionFromServerEvent?) {
        if (predictHitFactor.value != 0) {
            toggle()
        }
    }

    @SubscribeEvent
    fun onPacketSend(event: PacketEvents.Send) {
        if (fullNullCheck()) return
        if (event.getPacket<Packet<*>>() is CPacketPlayerDigging) {
            val action = event.getPacket<CPacketPlayerDigging>().action
            if (action == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK || action == CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                doAntiSurround(event.getPacket<CPacketPlayerDigging>().position)
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPacketReceive(event: PacketEvents.Receive) {
        if (fullNullCheck()) {
            return
        }
        if (event.getPacket<Packet<*>>() is SPacketSpawnObject) {
            val packet = event.getPacket<SPacketSpawnObject>()
            if (predictHitFactor.value != 0) {
                ArrayList(mc.world.loadedEntityList).forEach(Consumer { e: Entity ->
                    if (e is EntityItem || e is EntityArrow || e is EntityEnderPearl || e is EntitySnowball || e is EntityEgg) {
                        if (e.getDistance(packet.x, packet.y, packet.z) <= 6) {
                            lastEntityID.set(-1)
                            canPredictHit = false
                            event.isCanceled = true
                        }
                    }
                })
            }
            if (packet.type == 51 && !event.isCanceled) {
                lastEntityID.getAndUpdate { it: Int -> Math.max(it, packet.entityID) }
                if (packetExplode.value && packetExplodeTimerUtils.passed(packetDelay.value)) {
                    packetExplode(packet.entityID)
                    packetExplodeTimerUtils.reset()
                }
            } else {
                lastEntityID.set(-1)
            }
        } else if (event.getPacket<Packet<*>>() is SPacketSoundEffect) {
            val packet5 = event.getPacket<SPacketSoundEffect>()
            if (packet5.getSound() == SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW || packet5.getSound() == SoundEvents.ENTITY_ARROW_SHOOT || packet5.getSound() == SoundEvents.ENTITY_ITEM_BREAK) {
                canPredictHit = false
            }
            if (packet5.getCategory() == SoundCategory.BLOCKS && packet5.getSound() === SoundEvents.ENTITY_GENERIC_EXPLODE && render != null) {
                shouldInfoLastBreak = true
                ArrayList(mc.world.loadedEntityList).forEach(Consumer { e: Entity ->
                    if (e is EntityEnderCrystal) {
                        if (e.getDistance(packet5.x, packet5.y, packet5.z) <= 6.0f) {
                            tempSpawnPos = BlockPos(e)
                            e.setDead()
                            if (placeBoxIntersectsCrystalBox(tempSpawnPos!!, tempSpawnPos!!)) {
                                if (packetPlaceMode.value == PacketPlaceMode.Weak || packetPlaceMode.value == PacketPlaceMode.Strong && render != null && tempSpawnPos!!.down() == render) {
                                    place(MotionUpdateEvent.Tick.INSTANCETick, tempSpawnPos!!.down())
                                    if (debug.value) {
                                        ChatUtil.sendMessage("ForcePlacing!")
                                    }
                                }
                            }
                        }
                    }
                })
            }
        } else if (event.getPacket<Packet<*>>() is SPacketSpawnExperienceOrb || event.getPacket<Packet<*>>() is SPacketSpawnPainting) {
            lastEntityID.set(-1)
            canPredictHit = false
        } else if (event.packet is SPacketEntityStatus) {
            if ((event.packet as SPacketEntityStatus).opCode.toInt() == 35) {
                val entity = (event.packet as SPacketEntityStatus).getEntity(mc.world)
                if (EntityUtil.isValid(entity, enemyRange.value.toDouble())) {
                    if (entity is EntityLivingBase) {
                        if (render != null && chainPop.value) {
                            popList[CrystalChainPop(
                                entity, render!!, damageCA
                            )] = System.currentTimeMillis()
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onCrystal(event: MotionUpdateEvent.Tick?) {
        if (fullNullCheck()) {
            return
        }
        newSlot = mc.player.inventory.currentItem
        cSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL)
        --yawTicksPassed
        --pitchTicksPassed
        crystalTarget = calc()
        for (target in mc.world.playerEntities) {
            if (EntityUtil.isntValid(target, placeRange.value.toDouble())) continue
            if (ModuleManager.getModuleByClass(PacketMine::class.java).isEnabled && PacketMine.currentPos != null) {
                if (EntityUtil.isInHole(target)) doAntiSurround(PacketMine.currentPos)
            }
        }

        if (crystalMod.value == CrystalMod.PlaceBreak || strictDirection.value) {
            place(event, null)
            explode(event)
        } else if (crystalMod.value == CrystalMod.BreakPlace) {
            explode(event)
            place(event, null)
        }

        if (popList.isNotEmpty()) {
            popList.forEach {
                if (!mc.world.playerEntities.contains(it.key.target)) {
                    popList.remove(it.key, it.value)
                }
                if (it.key.target != null) {
                    if (mc.player.getDistance(it.key.target) > enemyRange.value) {
                        popList.remove(it.key, it.value)
                    }
                }
            }
        }
    }

    private fun explode(event: MotionUpdateEvent.Tick?) {
        if (renderEnt == null) {
            return
        }
        renderEnt?.let {
            val crystal = getExplodeCrystal(syncHurtTime.value, it)
            if (mc.player != null && crystal != null) {
                if (mc.player.getDistance(crystal) <= breakRange.value) {
                    lastCrystal = crystal
                    if (antiWeakness.value != AntiWeaknessMode.Off && mc.player.isPotionActive(MobEffects.WEAKNESS) && (!mc.player.isPotionActive(
                            MobEffects.STRENGTH
                        ) || Objects.requireNonNull(
                            mc.player.getActivePotionEffect(MobEffects.STRENGTH)
                        )!!.amplifier < 1)
                    ) {
                        spoofHotbar(
                            if (InventoryUtil.findHotbarItem(Items.DIAMOND_SWORD) != -1) InventoryUtil.findHotbarItem(
                                Items.DIAMOND_SWORD
                            ) else InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE), true
                        )
                    }
                    explodeCrystal(event)
                    if (antiWeakness.value == AntiWeaknessMode.Spoof && mc.player.isPotionActive(MobEffects.WEAKNESS) && (!mc.player.isPotionActive(
                            MobEffects.STRENGTH
                        ) || Objects.requireNonNull(
                            mc.player.getActivePotionEffect(MobEffects.STRENGTH)
                        )!!.amplifier < 1)
                    ) {
                        spoofHotbar(newSlot, true)
                    }
                    if (antiWeakness.value == AntiWeaknessMode.SpoofTest && mc.player.isPotionActive(MobEffects.WEAKNESS) && (!mc.player.isPotionActive(
                            MobEffects.STRENGTH
                        ) || Objects.requireNonNull(
                            mc.player.getActivePotionEffect(MobEffects.STRENGTH)
                        )!!.amplifier < 1)
                    ){
                        spoofHotbarBypass(newSlot){
                        }
                    }
                }
                attackingCrystal = crystal
            }
            if (crystal == null) attackingCrystal = null
        }
    }

    private fun getPlaceSide(pos: BlockPos): EnumFacing {
        return if (strictDirection.value) {
            getMiningSide(pos) ?: EnumFacing.UP
        } else {
            EnumFacing.UP
        }
    }

    private fun place(event: MotionUpdateEvent.Tick?, pos: BlockPos?) {
        runCatching {
            var crystalSlot =
                if (mc.player.heldItemMainhand.getItem() === Items.END_CRYSTAL) mc.player.inventory.currentItem else -1
            if (crystalSlot == -1) {
                for (l in 0..8) {
                    if (mc.player.inventory.getStackInSlot(l).getItem() === Items.END_CRYSTAL) {
                        crystalSlot = l
                        break
                    }
                }
            }
            var offhand = false
            if (mc.player.heldItemOffhand.getItem() === Items.END_CRYSTAL) {
                offhand = true
            } else if (crystalSlot == -1) {
                return
            }

            if (pos == null) {
                if (crystalTarget != null) {
                    renderEnt = crystalTarget!!.target
                    render = crystalTarget!!.blockPos
                }
            } else {
                render = pos
            }

            if ((renderEnt == null && pos == null) || render == null) {
                shouldShadeRender = true
                blockRenderSmooth.end()
                renderEnt = null
                render = null
                renderBlockDmg.clear()
                return
            }
            if (shouldShadeRender) {
                blockRenderSmooth.resetFade()
                shouldShadeRender = false
            }
            --ticks
            if (render != null) {
                if (switchMode.value == Switch.GhostHand && mc.connection != null && cSlot != -1) {
                    spoofHotbar(cSlot, true)
                }
                if (rotate.value) {
//                    rotations = BlockInteractionHelper.getLegitRotations(render!!.down())
                    rotations =
                        BlockInteractionHelper.getLegitRotations(Vec3d(render!!).add(0.5, 1.0, 0.5))
//                if (yawAngle.value < 1.0f) {
//                    if (yawTicksPassed > 0) {
//                        rotations[0] = mc.player.lastReportedYaw
//                    } else {
//                        val f: Float =
//                            MathHelper.wrapDegrees(rotations[0] - mc.player.lastReportedYaw)
//                        if (abs(f) > 180.0f * yawAngle.value) {
//                            rotations[0] =
//                                (mc.player.lastReportedYaw + f * (180.0f * yawAngle.value / abs(
//                                    f
//                                )))
//                            yawTicksPassed = yawTicks.value
//                        }
//                    }
//                }
                    if (yawAngle.value < 1.0f && yawStep.value && attackingCrystal != null) {
                        if (ticks > 0) {
                            rotations[0] =
                                mc.player.lastReportedYaw
                            attackingCrystal = null
                            attackingCrystal = null
                        } else {
                            val f: Float =
                                MathHelper.wrapDegrees(rotations[0] - mc.player.lastReportedYaw)
                            if (abs(f) > 180.0f * yawAngle.value) {
                                rotations[0] =
                                    mc.player.lastReportedYaw + f * (180.0f * yawAngle.value / Math.abs(
                                        f
                                    ))
                                attackingCrystal = null
                                attackingCrystal = null
                                ticks = yawTicks.value
                            }
                        }
                    }
                    event!!.setRotation(rotations[0], rotations[1])
                }
                if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
                    if (switchMode.value == Switch.AutoSwitch) {
                        if (mc.player.heldItemMainhand.getItem() is ItemAppleGold && mc.player.isHandActive) {
                            return
                        }
                        mc.player.inventory.currentItem = crystalSlot
                        switchCooldown = true
                    }
                    return
                }
                if (switchCooldown) {
                    switchCooldown = false
                    return
                }
                if (slowFP.value && isFacePlacing && renderEnt != null) {
                    if (!fPDelay.passed(fpDelay.value)) {
                        if (switchMode.value == Switch.GhostHand) {
                            spoofHotbar(newSlot, true)
                        }
                        return
                    }
                    fPDelay.reset()
                }
                if (mc.connection != null) {
                    if (hasDelayRunPlace(placeSpeed.value.toDouble())) {
                        mc.player.connection.sendPacket(
                            CPacketPlayerTryUseItemOnBlock(
                                render!!,
                                getPlaceSide(render!!),
                                if (mc.player.heldItemOffhand.getItem() == Items.END_CRYSTAL) EnumHand.OFF_HAND else EnumHand.MAIN_HAND,
                                0.5f,
                                1f,
                                0.5f
                            )
                        )
                        if (placeSwing.value) {
                            swing()
                        }
                        blockRenderSmooth.updatePos(render!!)
                        if (predictHitFactor.value != 0 && renderEnt != null) {
                            runCatching {
                                if (renderEnt!!.isDead || !canPredictHit || !canHitCrystal(
                                        lastCrystal!!.positionVector
                                    )
                                ) {
                                    if (switchMode.value == Switch.GhostHand) {
                                        spoofHotbar(newSlot, true)
                                    }
                                    placeTimerUtils.reset()
                                    return
                                }
                                if (mc.player.health + mc.player.absorptionAmount > placeMaxSelf.value && lastEntityID.get() != -1 && lastCrystal != null && canPredictHit) {
                                    val syncedId = lastEntityID.get()
                                    for (spam in 0 until predictHitFactor.value) {
                                        if (syncedId != -1) {
                                            packetExplode(syncedId + spam + 1)
                                        }
                                    }
                                }
                            }
                        }
                        placeTimerUtils.reset()
                    }
                }
                if (switchMode.value == Switch.GhostHand) {
                    spoofHotbar(newSlot, true)
                }
            }
        }
    }

    private fun calc(): CrystalTarget {
        var damage = 0.5
        var selfDamage = 0.0
        var target: EntityLivingBase? = null
        var tempBlock: BlockPos? = null
        var totemCount =
            mc.player.inventory.mainInventory.stream().filter { t: ItemStack -> t.getItem() == Items.TOTEM_OF_UNDYING }
                .mapToInt { obj: ItemStack -> obj.count }.sum()
        if (mc.player.heldItemOffhand.getItem() === Items.TOTEM_OF_UNDYING) {
            totemCount += mc.player.heldItemOffhand.stackSize
        }
        for (entity2 in ArrayList(entities)) {
            if (entity2 !== mc.player) {
                predictionTarget = if (entity2 is EntityPlayer && motionPredict.value) PredictionHandlerNew(
                    entity2, predictTicks.value
                ) else Vec3d(0.0, 0.0, 0.0)
                if (entity2.health <= 0.0f || entity2.isDead) continue
                canPredictHit =
                    entity2.heldItemMainhand.getItem() != Items.EXPERIENCE_BOTTLE && entity2.heldItemOffhand.getItem() != Items.EXPERIENCE_BOTTLE || ModuleManager.getModuleByClass(
                        AutoMend::class.java
                    ).isDisabled

                for (blockPos in ArrayList(renderTions(placeRange.value.toDouble()))) {
                    if (entity2.getDistanceSq(blockPos) >= enemyRange.value * enemyRange.value) continue
                    if (mc.player.getDistance(
                            blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble()
                        ) > placeRange.value
                    ) continue
                    val d = calcDamage(
                        entity2,
                        entity2.positionVector.add(predictionTarget!!),
                        entity2.entityBoundingBox,
                        blockPos.x + 0.5,
                        (blockPos.y + 1).toDouble(),
                        blockPos.z + 0.5,
                        MutableBlockPos()
                    ).toDouble()
                    damageCa = d.toInt()
                    if (popList.isNotEmpty() && chainPop.value) {
                        popList.forEach {
                            if (it.key.target != null && it.key.target == entity2) {
                                if (System.currentTimeMillis() - it.value >= chainPopTime.value) {
                                    for (pos in renderTions(placeRange.value.toDouble())) {
                                        if (d / chainPopFactor.value > it.key.dmg) continue
                                        if (pos == it.key.targetPos) continue
                                        if (damage > chainPopDamage.value) continue
                                        if (it.key.target.getDistanceSq(pos) >= enemyRange.value.sq) continue
                                        if (mc.player.getDistance(
                                                pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()
                                            ) > chainPopRange.value
                                        ) continue
                                        if (entity2.entityBoundingBox.intersects(getCrystalPlacingBB(pos))) continue
                                        if (getCrystalPlacingBB(pos).intersects(
                                                entity2.positionVector.add(predictionTarget!!), Vec3d(pos)
                                            )
                                        ) continue
                                        popList.remove(it.key, it.value)
                                        ChatUtil.sendMessage("Trying To ChainPop")
                                        return CrystalTarget(
                                            pos, it.key.target, selfDamageCA, d
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (popList.isNotEmpty()) continue
                    if (d < damage) continue
                    isFacePlacing =
                        EntityUtil.isInHole(entity2) //HoleUtil.is2HoleB(entity2.getPosition()) || HoleUtil.isHole(entity2.getPosition());
                    if (shouldForcePlace(
                            entity2, forceHealth.value.toFloat(), armorRate.value.toFloat(), forcePlaceMotion.value
                        )
                    ) {
                        if (d < forcePlaceDmg.value) continue
                    } else {
                        if (d < minDamage.value) continue
                    }

                    val healthTarget = entity2.health + entity2.absorptionAmount
                    val targetDamage = calcDamage(
                        entity2!!,
                        entity2.positionVector.add(predictionTarget!!),
                        entity2.entityBoundingBox,
                        blockPos.x + 0.5,
                        (blockPos.y + 1).toDouble(),
                        blockPos.z + 0.5,
                        MutableBlockPos()
                    ).toDouble()
                    val self = calcDamage(
                        mc.player,
                        mc.player.positionVector,
                        mc.player.entityBoundingBox,
                        blockPos.x + 0.5,
                        (blockPos.y + 1).toDouble(),
                        blockPos.z + 0.5,
                        MutableBlockPos()
                    ).toDouble()
                    selfDamage = self
                    selfDamageCA = selfDamage
                    damageCA = targetDamage
                    if (self > d && d < healthTarget) continue
                    //if (self - noSuicide.getValue() > healthSelf) continue;
                    if (forcePop.value && totemCount > 1) {
                        if (entity2.health <= targetDamage && entity2.getDistance(mc.player) <= 2 && (mc.player.heldItemOffhand.getItem() == Items.TOTEM_OF_UNDYING || mc.player.heldItemMainhand.getItem() == Items.TOTEM_OF_UNDYING)) {
                            if (mc.player.position.getDistance(
                                    blockPos.up().getX(), blockPos.up().getY(), blockPos.up().getZ()
                                ) < 3
                            ) {
                                ChatUtil.NoSpam.sendMessage("ForcePopping")
                                if (renderText.value) renderBlockDmg[blockPos] = targetDamage
                                return CrystalTarget(
                                    blockPos, target, self, targetDamage
                                )
                            }
                        }
                    }
                    if (mc.player.scaledHealth - self <= noSuicide.value) continue
                    //if (!lethalOverride.getValue() && self > placeMaxSelf.getValue()) continue;
                    if (self > placeMaxSelf.value) continue
                    if (mc.player.positionVector.squareDistanceTo(
                            Vec3d(
                                blockPos.add(
                                    0.5, 1.0, 0.5
                                )
                            )
                        ) > wallRange.value * wallRange.value && !rayTraceVisible(
                            mc.player.positionVector.add(0.0, mc.player.getEyeHeight().toDouble(), 0.0),
                            blockPos.getX() + 0.5,
                            (blockPos.getY() + 1f + 1.7f).toDouble(),
                            blockPos.getZ() + 0.5,
                            20,
                            MutableBlockPos()
                        )
                    ) continue
                    if (entity2.entityBoundingBox.intersects(getCrystalPlacingBB(blockPos))) continue
                    if (getCrystalPlacingBB(blockPos).intersects(
                            entity2.positionVector.add(predictionTarget!!), Vec3d(blockPos)
                        )
                    ) continue
                    if (lethalOverride.value && d - mc.player.totalHealth > lethalBalance.value && self <= lethalMaxDamage.value) {
                        if (crystalTarget != null) {
                            if (self < crystalTarget!!.selfDamage) {
                                //crystalTarget.update(blockPos, entity2, selfDamage, d);
                                if (ModuleManager.getModuleByClass(
                                        AutoGG::class.java
                                    ).isEnabled
                                ) {
                                    (ModuleManager.getModuleByClass(
                                        AutoGG::class.java
                                    ) as AutoGG).addTargetedPlayer(
                                        entity2.name
                                    )
                                }
                                if (renderText.value) renderBlockDmg[blockPos] = d
                                return CrystalTarget(
                                    blockPos, entity2, self, d
                                )
                            }
                        }
                    }
                    damage = d
                    tempBlock = blockPos
                    target = entity2
                    if (ModuleManager.getModuleByClass(AutoGG::class.java).isEnabled) {
                        (ModuleManager.getModuleByClass(AutoGG::class.java) as AutoGG).addTargetedPlayer(target.name)
                    }
                    if (renderText.value) renderBlockDmg[tempBlock] = damage
                }
                if (target != null) {
                    break
                }
            }
        }
        return CrystalTarget(
            tempBlock, target, selfDamage, damage
        )
    }


    fun swing() {
        if (fullNullCheck()) {
            return
        }
        when (swingMode.value) {
            SwingMode.Off -> {}
            SwingMode.OffHand -> {
                mc.player.swingArm(EnumHand.OFF_HAND)
            }

            SwingMode.MainHand -> {
                mc.player.swingArm(EnumHand.MAIN_HAND)
            }

            SwingMode.Auto -> {
                mc.player.swingArm(if (mc.player.heldItemMainhand.getItem() == Items.END_CRYSTAL) EnumHand.MAIN_HAND else EnumHand.OFF_HAND)
            }
        }
    }

    private fun hasDelayRunPlace(placeSpeed: Double): Boolean {
        return placeTimerUtils.passed(1000 / placeSpeed)
    }

    val entities: List<EntityLivingBase>
        get() {
            val entities = ArrayList(mc.world.playerEntities).stream()
                .filter { entityPlayer: EntityPlayer -> !FriendManager.isFriend(entityPlayer.name) }
                .filter { entity: EntityPlayer? -> mc.player.getDistance(entity!!) < enemyRange.value }
                .collect(Collectors.toList())
            for (ite2 in ArrayList(entities)) {
                if (mc.player.getDistance(ite2) > enemyRange.value) entities.remove(ite2)
                if (ite2 === mc.player) entities.remove(ite2)
            }
            entities.sortWith(Comparator.comparing { e: EntityLivingBase? -> mc.player.getDistance(e!!) })
            return entities
        }

    private fun explodeCrystal(event: MotionUpdateEvent.Tick?) {
        val crystal = getExplodeCrystal(syncHurtTime.value, renderEnt!!)
        if (crystal != null) {
            if (explodeTimerUtils.passed(hitDelay.value) && mc.connection != null) {
                packetExplode(crystal.getEntityId())
                swing()
                if (packetPlaceMode.value == PacketPlaceMode.Strong) {
                    if (placeBoxIntersectsCrystalBox(BlockPos(crystal), BlockPos(crystal))) {
                        place(event, BlockPos(crystal).down())
                    }
                }
                explodeTimerUtils.reset()
            }
        }
        if (lastBreakTime == 0L) {
            lastBreakTime = System.currentTimeMillis()
            shouldInfoLastBreak = false
        }
    }

    private fun getExplodeCrystal(syncHurtTime: Boolean, target: EntityLivingBase): EntityEnderCrystal? {
        var max: EntityEnderCrystal? = null
        var mid: EntityEnderCrystal? = null
        var min: EntityEnderCrystal? = null
        var damageTarget: Double
        var damageSelf: Double
        mc.world.loadedEntityList.forEach {
            if (it is EntityEnderCrystal) {
                if (mc.player.getDistance(it) <= breakRange.value) {
                    damageTarget = calcDamage(
                        target,
                        target.positionVector,
                        target.entityBoundingBox,
                        it.posX + 0.5,
                        it.posY + 1,
                        it.posZ + 0.5,
                        MutableBlockPos()
                    ).toDouble()

                    damageSelf = calcDamage(
                        mc.player,
                        mc.player.positionVector,
                        mc.player.entityBoundingBox,
                        it.posX + 0.5,
                        it.posY + 1,
                        it.posZ + 0.5,
                        MutableBlockPos()
                    ).toDouble()

                    if (damageTarget > damageSelf) {
                        max = it
                    }
                    if (damageTarget - damageSelf > damageSelf) {
                        mid = it
                    }
                    if (damageTarget - damageSelf > damageSelf / 2) {
                        min = it
                    }
                }
            }
        }

        if (syncHurtTime) {
            if (mc.player.hurtResistantTime > mc.player.maxHurtResistantTime / 2.0 && max != null) {
                return max
            } else {
                if (min != null && max != null) {
                    val minDmg = calcDamage(
                        mc.player,
                        mc.player.positionVector,
                        mc.player.entityBoundingBox,
                        min!!.posX,
                        min!!.posY,
                        min!!.posZ,
                        MutableBlockPos()
                    )
                    val maxDmg = calcDamage(
                        mc.player,
                        mc.player.positionVector,
                        mc.player.entityBoundingBox,
                        max!!.posX,
                        max!!.posY,
                        max!!.posZ,
                        MutableBlockPos()
                    )
                    if (minDmg >= mc.player.lastDamage) return min
                    if (minDmg < maxDmg * 0.9) {
                        if (mid != null) {
                            val midDmg = calcDamage(
                                mc.player,
                                mc.player.positionVector,
                                mc.player.entityBoundingBox,
                                mid!!.posX,
                                mid!!.posY,
                                mid!!.posZ,
                                MutableBlockPos()
                            )
                            return if (midDmg < maxDmg * 0.9) {
                                max
                            } else mid
                        } else {
                            return max
                        }
                    } else {
                        return min
                    }
                }
            }
        } else {
            return ArrayList(mc.world.loadedEntityList).stream().filter {
                it is EntityEnderCrystal && canHitCrystal(it.getPositionVector()) && checkBreakRange(
                    it, breakRange.value.toFloat(), wallRange.value * wallRange.value, 20, MutableBlockPos()
                )
            }.map { it!! as EntityEnderCrystal? }.min(Comparator.comparing { mc.player.getDistance(it!!) }).orElse(null)
        }
        return null
    }

    private fun packetExplode(i: Int) {
        runCatching {
            if (mc.player.getDistance(lastCrystal!!) <= breakRange.value && canHitCrystal(lastCrystal!!.positionVector)) {
                val wdnmd = CPacketUseEntity(lastCrystal!!)
                wdnmd.entityId = i
                wdnmd.action = CPacketUseEntity.Action.ATTACK
                mc.player.connection.sendPacket(wdnmd)
            }
        }
    }

    private fun canHitCrystal(crystal: Vec3d): Boolean {
        val selfDamage = calcDamage(
            mc.player,
            mc.player.positionVector,
            mc.player.entityBoundingBox,
            crystal.x,
            crystal.y,
            crystal.z,
            MutableBlockPos()
        )
        val healthSelf = mc.player.health + mc.player.absorptionAmount
        if (selfDamage >= healthSelf) return false
        for (player in ArrayList(entities)) {
            if (player is EntityPlayer) {
                if (mc.player.isDead || healthSelf <= 0.0f) continue
                var minDamage = breakMinDmg.value.toDouble()
                val maxSelf = breakMaxSelf.value.toDouble()
                if (shouldForcePlace(
                        player, forceHealth.value.toFloat(), armorRate.value.toFloat(), forcePlaceMotion.value
                    )
                ) {
                    minDamage = 1.0
                }
                val target = calcDamage(
                    player,
                    player.getPositionVector(),
                    player.getEntityBoundingBox(),
                    crystal.x,
                    crystal.y,
                    crystal.z,
                    MutableBlockPos()
                ).toDouble()
                if (target > player.getHealth() + player.getAbsorptionAmount() && selfDamage < healthSelf) {
                    return true
                }
                breakrender = MutableBlockPos()
                if (selfDamage > maxSelf) continue
                if (target < minDamage) continue
                if (selfDamage > target) continue
                return true
            }
        }
        return false
    }

    private fun renderTions(range: Double): List<BlockPos> {
        val positions = NonNullList.create<BlockPos>()
        positions.addAll(
            CrystalUtil.getSphere(EntityUtil.getPlayerPos(), range, range, false, true, 0).stream()
                .filter { v: BlockPos -> canPlaceCrystal(v, endCrystal.value) }.collect(Collectors.toList())
        )
        return positions
    }

    fun canPlaceCrystal(blockPos: BlockPos, newPlace: Boolean): Boolean {
        val boost = blockPos.add(0, 1, 0)
        val boost2 = blockPos.add(0, 2, 0)
        val base = mc.world.getBlockState(blockPos).block
        val b1 = mc.world.getBlockState(boost).block
        val b2 = mc.world.getBlockState(boost2).block
        if (base !== Blocks.BEDROCK && base !== Blocks.OBSIDIAN) return false
        if (b1 !== Blocks.AIR && !isReplaceable(b1)) return false
        if (!newPlace && b2 !== Blocks.AIR) return false
        val box = AxisAlignedBB(
            blockPos.getX().toDouble(),
            blockPos.getY() + 1.0,
            blockPos.getZ().toDouble(),
            blockPos.getX() + 1.0,
            blockPos.getY() + 3.0,
            blockPos.getZ() + 1.0
        )
        for (entity in ArrayList(mc.world.loadedEntityList)) {
            if (entity is EntityEnderCrystal) continue
            if (entity.entityBoundingBox.intersects(box)) return false
        }
        return true
    }

    private fun doAntiSurround(pos: BlockPos?) {
        if (antiSurround.value) {
            if (pos == null) return
            for (target in mc.world.playerEntities) {
                if (EntityUtil.isntValid(target, placeRange.value.toDouble())) continue
                if (target.positionVector.y != pos.getY().toDouble()) continue
                val burrowPos = target.position
                val finalPos = if (BlockUtil.canBreak(burrowPos, false)) {
                    burrowPos
                } else {
                    pos
                }
                val isBurrowPos = finalPos == burrowPos
                for (facing in offsetFacing) {
                    if (!EntityUtil.isInHole(target) && !isBurrowPos) continue
                    val placePos = finalPos.offset(facing)
                    if (getAntiSurroundPos(placePos)) {
                        if (!mc.world.noCollision(placePos)) continue
                        render = placePos.down()
                        place(MotionUpdateEvent.Tick.INSTANCETick, placePos.down())
                        render = placePos.down()
                        break
                    }
                }
            }
        }
    }

    private fun getAntiSurroundPos(posOffset: BlockPos): Boolean {
        return mc.world.isAirBlock(posOffset) && mc.world.isAirBlock(posOffset.up()) && canPlaceCrystal(
            posOffset.down(), endCrystal.value
        )
    }

    private fun World.noCollision(pos: BlockPos) = checkNoEntityCollision(AxisAlignedBB(pos), mc.player)

    override fun onWorldRender(event: RenderEvent) {
        if (fullNullCheck()) {
            return
        }
        val hsBtoRGB = Color.HSBtoRGB(
            System.currentTimeMillis() % 11520L / 11520.0f * rgbSpeed.value, saturation.value, brightness.value
        )
        val r = hsBtoRGB shr 16 and 0xFF
        val g = hsBtoRGB shr 8 and 0xFF
        val b = hsBtoRGB and 0xFF
        val c = Color(
            if (rainbow.value) r else color.value.red,
            if (rainbow.value) g else color.value.green,
            if (rainbow.value) b else color.value.blue
        )
        val fonts = GuiManager.getINSTANCE().font
        runCatching {
            if (render != null) {
                blockRenderSmooth.begin()
                if (renderMode.value == RenderModes.Glide) {
                    if (!xg42OutLineMod.value) {
                        MelonTessellator.drawBBBox(
                            blockRenderSmooth.getFullUpdate(), c, alpha.value, outLineWidth.value, outline.value
                        )
                    } else {
                        XG42Tessellator.drawBBBox(
                            blockRenderSmooth.getFullUpdate(), c, alpha.value, outLineWidth.value, outline.value
                        )
                    }
                }
                if (renderMode.value == RenderModes.Normal) {
                    XG42Tessellator.prepare(GL11.GL_QUADS)
                    XG42Tessellator.drawFullBox(
                        render,
                        outLineWidth.value,
                        if (rainbow.value) r else color.value.red,
                        if (rainbow.value) g else color.value.green,
                        if (rainbow.value) b else color.value.blue,
                        alpha.value
                    )
                    XG42Tessellator.release()
                }
                if (renderBreak.value) {
                    val attackingCrystalPosition = attackingCrystal!!.position.down()

                    if (!attackingCrystalPosition.isFullBox) return

                    if (fullNullCheck()) {
                        return
                    }
                    if (attackingCrystal == null) {
                        return
                    }
                    XG42Tessellator.prepare(GL11.GL_QUADS)
                    XG42Tessellator.drawFullBox(
                        attackingCrystalPosition,
                        outLineWidth.value,
                        if (rainbow.value) r else color.value.red,
                        if (rainbow.value) g else color.value.green,
                        if (rainbow.value) b else color.value.blue,
                        breakAlpha.value
                    )
                    XG42Tessellator.release()
                }

                if (renderTarget.value){
                    RenderUtil.drawFade(crystalTarget!!.target, Color(0, 0, 0))
                }

                if (renderText.value) {
                    if (textMode.value == TextMode.Damage) {
                        if (renderBlockDmg.containsKey(render) && renderMode.value == RenderModes.Glide) {
                            if (renderBlockDmg.containsKey(render)) {
                                GlStateManager.pushMatrix()
                                val blockPos = blockRenderSmooth.getFullUpdate()
                                MelonTessellator.glBillboardDistanceScaled(
                                    blockPos.center.x.toFloat(),
                                    blockPos.center.y.toFloat(),
                                    blockPos.center.z.toFloat(),
                                    mc.player,
                                    0.5f
                                )
                                val damage = renderBlockDmg[render]!!
                                val damageText = (if (floor(damage) == damage) damage else String.format(
                                    "%.1f", damage
                                )).toString() + ""
                                GlStateManager.disableDepth()
                                GlStateManager.translate(-(fonts.getStringWidth(damageText) / 2.0), 0.0, 0.0)
                                GlStateManager.scale(
                                    textscalex.value.toFloat(), textscaley.value.toFloat(), textscalez.value.toFloat()
                                )
                                if (!customFont.value) {
                                    fontRenderer.drawString(
                                        damageText,
                                        0,
                                        0,
                                        Color(textcolor.value.red, textcolor.value.green, textcolor.value.blue).rgb
                                    )
                                } else {
                                    FontManager.font2!!.drawString(
                                        damageText,
                                        0F,
                                        0F,
                                        Color(textcolor.value.red, textcolor.value.green, textcolor.value.blue).rgb
                                    )
                                }
                                GlStateManager.popMatrix()
                            }
                        } else {
                            GlStateManager.pushMatrix()
                            XG42Tessellator.glBillboardDistanceScaled(
                                render!!.getX().toFloat() + 0.5f,
                                render!!.getY().toFloat() + 0.5f,
                                render!!.getZ().toFloat() + 0.5f,
                                mc.player,
                                1f
                            )
                            val damage = CrystalUtil.calculateDamage(
                                render!!.getX() + 0.5,
                                (render!!.getY() + 1).toDouble(),
                                render!!.getZ() + 0.5,
                                renderEnt
                            )
                            val damageText =
                                (if (floor(damage.toDouble()) == damage.toDouble()) damage else String.format(
                                    "%.1f", damage
                                )).toString() + ""
                            GlStateManager.disableDepth()
                            GlStateManager.translate(-(fonts.getStringWidth(damageText) / 2.0), 0.0, 0.0)
                            if (!customFont.value) {
                                fontRenderer.drawString(
                                    damageText,
                                    0,
                                    0,
                                    Color(textcolor.value.red, textcolor.value.green, textcolor.value.blue).rgb
                                )
                            } else {
                                FontManager.font2!!.drawString(
                                    damageText,
                                    0F,
                                    0F,
                                    Color(textcolor.value.red, textcolor.value.green, textcolor.value.blue).rgb
                                )
                            }
                            GlStateManager.popMatrix()
                        }
                    } else {
                        if (renderBlockDmg.containsKey(render) && renderMode.value == RenderModes.Glide) {
                            if (renderBlockDmg.containsKey(render)) {
                                GlStateManager.pushMatrix()
                                val blockPos = blockRenderSmooth.getFullUpdate()
                                MelonTessellator.glBillboardDistanceScaled(
                                    blockPos.center.x.toFloat(),
                                    blockPos.center.y.toFloat(),
                                    blockPos.center.z.toFloat(),
                                    mc.player,
                                    0.5f
                                )
                                val targetname = renderEnt!!.name
                                GlStateManager.disableDepth()
                                GlStateManager.translate(-(fonts.getStringWidth(targetname) / 2.0), 0.0, 0.0)
                                GlStateManager.scale(
                                    textscalex.value.toFloat(), textscaley.value.toFloat(), textscalez.value.toFloat()
                                )
                                if (!customFont.value) {
                                    fontRenderer.drawString(
                                        targetname,
                                        0,
                                        0,
                                        Color(textcolor.value.red, textcolor.value.green, textcolor.value.blue).rgb
                                    )
                                } else {
                                    FontManager.font2!!.drawString(
                                        targetname,
                                        0F,
                                        0F,
                                        Color(textcolor.value.red, textcolor.value.green, textcolor.value.blue).rgb
                                    )
                                }
                                GlStateManager.popMatrix()
                            }
                        } else {
                            GlStateManager.pushMatrix()
                            XG42Tessellator.glBillboardDistanceScaled(
                                render!!.getX().toFloat() + 0.5f,
                                render!!.getY().toFloat() + 0.5f,
                                render!!.getZ().toFloat() + 0.5f,
                                mc.player,
                                1f
                            )
                            val targetname = renderEnt!!.name
                            GlStateManager.disableDepth()
                            GlStateManager.translate(-(fonts.getStringWidth(targetname) / 2.0), 0.0, 0.0)
                            if (!customFont.value) {
                                fontRenderer.drawString(
                                    targetname,
                                    0,
                                    0,
                                    Color(textcolor.value.red, textcolor.value.green, textcolor.value.blue).rgb
                                )
                            } else {
                                FontManager.font2!!.drawString(
                                    targetname,
                                    0F,
                                    0F,
                                    Color(textcolor.value.red, textcolor.value.green, textcolor.value.blue).rgb
                                )
                            }
                            GlStateManager.popMatrix()
                        }
                    }
                }
            } else {
                blockRenderSmooth.resetFade()
                blockRenderSmooth.end()
            }
        }
    }

    override fun onEnable() {
        if (fullNullCheck()) {
            return
        }
        blockRenderSmooth = BlockEasingRender(BlockPos(0, 0, 0), movingLength.value, 350f)
        newSlot = mc.player.inventory.currentItem
        cSlot = -1
        blockRenderSmooth.resetFade()
        renderBlockDmg.clear()
        lastEntityID.set(-1)
        shouldShadeRender = false
        switchCooldown = false
        isFacePlacing = false
        shouldInfoLastBreak = false
        canPredictHit = true
        packetExplodeTimerUtils.reset()
        explodeTimerUtils.reset()
        placeTimerUtils.reset()
        calcTimerUtils.reset()
        fPDelay.reset()
        popTicks = 0
    }


    override fun onDisable() {
        if (fullNullCheck()) {
            return
        }
        blockRenderSmooth.resetFade()
        blockRenderSmooth.end()
        renderEnt = null
        render = null
    }

    override fun getHudInfo(): String? {
        if (shouldInfoLastBreak && lastBreakTime != 0L) {
            infoBreakTime = System.currentTimeMillis() - lastBreakTime
            lastBreakTime = 0L
            shouldInfoLastBreak = false
        }
        return when (hudInfoMod.value) {
            Mode.Target -> {
                if (renderEnt != null) {
                    TextFormatting.RED.toString() + "" + renderEnt!!.name
                } else null
            }

            Mode.Action -> {
                if (attackingCrystal != null && lastCrystal != null && render != null){
                    return if (Random().nextBoolean()) "Placing" else "Breaking"
                }
                if (lastCrystal != null && render != null) return "Placing"
                return if (attackingCrystal != null && render != null) "Breaking" else null
            }

            Mode.Differentiation -> if (infoBreakTime != 0L && renderEnt != null) {
                val nmsl = 100.0
                val cnm = 2
                return TextFormatting.YELLOW.toString() + "[" + TextFormatting.GREEN + TextFormatting.OBFUSCATED + String.format(
                    java.lang.String.valueOf(infoBreakTime / nmsl), cnm
                ) + TextFormatting.YELLOW.toString() + "]"
            } else null

            else -> null
        }
    }

    enum class Page {
        GENERAL, CALCULATION, PLACE, BREAK, FORCE, LETHAL, RENDER
    }

    enum class PacketPlaceMode {
        Off, Weak, Strong
    }

    enum class Switch {
        AutoSwitch, GhostHand, Off
    }

    enum class AntiWeaknessMode {
        Swap, Spoof, SpoofTest, Off
    }

    enum class SwingMode {
        OffHand, MainHand, Auto, Off
    }

    enum class RenderModes {
        Glide, Normal
    }

    enum class Mode {
        Target, Action, Differentiation
    }

    enum class TextMode {
        Target, Damage
    }

    enum class CrystalMod {
        PlaceBreak, BreakPlace
    }

    var renderEnt: EntityLivingBase? = null
    private var renderBlockDmg = ConcurrentHashMap<BlockPos?, Double>()

    val BlockPos.state: IBlockState get() = mc.world.getBlockState(this)
    private val BlockPos.collisionBox: AxisAlignedBB
        get() = state.getCollisionBoundingBox(mc.world, this) ?: AxisAlignedBB(this)
    private val BlockPos.isFullBox: Boolean
        get() = mc.world?.let {
            collisionBox
        } == Block.FULL_BLOCK_AABB

}
