package me.windyteam.kura.module.modules.combat

import me.windyteam.kura.concurrent.utils.Timer
import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.ModuleManager
import me.windyteam.kura.module.modules.movement.ReverseStep
import me.windyteam.kura.utils.block.BlockInteractionHelper
import me.windyteam.kura.utils.block.BlockUtil
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.inventory.InventoryUtil
import me.windyteam.kura.utils.mc.ChatUtil
import net.minecraft.block.BlockEnderChest
import net.minecraft.block.BlockObsidian
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.item.EntityItem
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.server.SPacketExplosion
import net.minecraft.network.play.server.SPacketPlayerPosLook
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.abs
import kotlin.math.floor

@Module.Info(
    name = "Burrow", category = Category.COMBAT, description = "Self-fill ur self in ur mom's pussy"
)
object Burrow : Module() {
    private val breakCrystal = bsetting("BreakCrystal", true)
    private val toggleRStep = bsetting("ToggleRStep", true)
    private val safe = bsetting("ToggleWhileInObi", true)
    private val safeCheck = bsetting("LagCheck", true)
    private var clientMode = msetting("ClientMode", Client.Other)
    private var offSet = dsetting("OffSet", -7.0, -10.0, 10.0).m(clientMode, Client.New)
    private var fakeJump = bsetting("FakeJump", true).m(clientMode, Client.Melon)
    private var offsetCheck = isetting("Offset", 3, -20, 10).m(clientMode, Client.Melon)
    private var getPlayerHeight = bsetting("PlayerHeight", false).m(clientMode, Client.Negative)
    private var getHeight = isetting("GetHeight", 4, 0, 10).b(getPlayerHeight).m(clientMode, Client.Negative)

    private var teleportID = 0
    private var velocityTime = 0L
    private var safeLevel = 0
    private var tempHeight = 0
    private var originalPos: BlockPos? = null
    private var oldSlot = -1
    private var timer = Timer()

    override fun onEnable() {
        if (fullNullCheck()) {
            return
        }
        if (toggleRStep.value) {
            ModuleManager.getModuleByClass(ReverseStep::class.java).disable()
        }
        if (breakCrystal.value){
            breakCrystal(BlockPos(0,0,0))
        }
        originalPos = BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)
        if (mc.world.getBlockState(
                BlockPos(
                    mc.player.posX, mc.player.posY, mc.player.posZ
                )
            ).block == Blocks.OBSIDIAN || intersectsWithEntity(
                originalPos
            )
        ) {
            disable()
            return
        }
        oldSlot = mc.player.inventory.currentItem
        safeLevel = 0
    }

    override fun onDisable() {
        if (fullNullCheck()) {
            return
        }
        mc.player.isSneaking = false
        if (toggleRStep.value) {
            ModuleManager.getModuleByClass(ReverseStep::class.java).enable()
        }
        velocityTime = 0L
    }

    fun onPacketReceive(event: PacketEvents.Receive) {
        if (fullNullCheck()) {
            return
        }
        if (event.packet is SPacketPlayerPosLook) {
            teleportID = (event.packet as SPacketPlayerPosLook).getTeleportId()
        } else if (event.packet is SPacketExplosion) {
            if ((event.packet as SPacketExplosion).y > 2 && velocityTime <= System.currentTimeMillis()) {
                velocityTime = System.currentTimeMillis() + 3000L
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onTick(event: MotionUpdateEvent.FastTick?) {
        if (fullNullCheck()) {
            return
        }
        if (!mc.player.onGround) {
            return
        }
        runCatching {
            oldSlot = mc.player.inventory.currentItem
            if (mc.world.getBlockState(
                    BlockPos(
                        mc.player.posX, mc.player.posY, mc.player.posZ
                    )
                ).block !== Blocks.AIR && mc.world.getBlockState(
                    BlockPos(
                        mc.player.posX, mc.player.posY, mc.player.posZ
                    )
                ).block !== Blocks.ENDER_CHEST && mc.world.getBlockState(
                    BlockPos(
                        mc.player.posX, mc.player.posY, mc.player.posZ
                    )
                ).block !== Blocks.LAVA && mc.world.getBlockState(
                    BlockPos(
                        mc.player.posX, mc.player.posY, mc.player.posZ
                    )
                ).block !== Blocks.WATER && mc.world.getBlockState(
                    BlockPos(
                        mc.player.posX, mc.player.posY, mc.player.posZ
                    )
                ).block !== Blocks.GRASS && safe.value
            ) {
                ChatUtil.sendMessage("Prevented Burrow While In Block!")
                disable()
                return
            }
            for (y in 0..offsetCheck.value) {
                if (mc.world.getBlockState(
                        BlockPos(mc.player.positionVector).add(
                            0.0, y.toDouble(), 0.0
                        )
                    ).block !== Blocks.LAVA && mc.world.getBlockState(
                        BlockPos(mc.player.positionVector).add(
                            0.0, y.toDouble(), 0.0
                        )
                    ).block !== Blocks.FLOWING_LAVA && mc.world.getBlockState(
                        BlockPos(mc.player.positionVector).add(
                            0.0, y.toDouble(), 0.0
                        )
                    ).block !== Blocks.WATER && mc.world.getBlockState(
                        BlockPos(mc.player.positionVector).add(
                            0.0, y.toDouble(), 0.0
                        )
                    ).block !== Blocks.FLOWING_WATER && mc.world.getBlockState(
                        BlockPos(mc.player.positionVector).add(
                            0.0, y.toDouble(), 0.0
                        )
                    ).block !== Blocks.AIR && mc.world.getBlockState(
                        BlockPos(mc.player.positionVector).add(
                            0.0, (y + 1f).toDouble(), 0.0
                        )
                    ).block !== Blocks.AIR && mc.world.getBlockState(
                        BlockPos(mc.player.positionVector).add(
                            0.0, (y + 2f).toDouble(), 0.0
                        )
                    ).block !== Blocks.AIR && safeCheck.value
                ) {
                    ChatUtil.NoSpam.sendMessage("Prevented Burrow LagOut!")
                    InventoryUtil.switchToHotbarSlot(oldSlot, false)
                    disable()
                    return
                }
            }
            if (InventoryUtil.findHotbarBlock(BlockObsidian::class.java) != -1) {
                InventoryUtil.switchToHotbarSlot(InventoryUtil.findHotbarBlock(BlockObsidian::class.java), false)
            } else if (InventoryUtil.findHotbarBlock(BlockObsidian::class.java) == -1 && InventoryUtil.findHotbarBlock(
                    BlockEnderChest::class.java
                ) != -1
            ) {
                InventoryUtil.switchToHotbarSlot(InventoryUtil.findHotbarBlock(BlockEnderChest::class.java), false)
            }
            when (clientMode.value) {
                Client.Melon -> {
                    if (mc.connection != null && fakeJump.value) {
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                mc.player.posX,
                                mc.player.posY + 0.41999998688698,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                90f,
                                false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                mc.player.posX,
                                mc.player.posY + 0.7500019,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                90f,
                                false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                mc.player.posX,
                                mc.player.posY + 0.9999962,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                90f,
                                false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                mc.player.posX,
                                mc.player.posY + 1.17000380178814,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                90f,
                                false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                mc.player.posX,
                                mc.player.posY + 1.17000380178815,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                90f,
                                false
                            )
                        )
                    }

                    BlockUtil.placeBlock(originalPos, EnumHand.MAIN_HAND, false, true)
                    InventoryUtil.switchToHotbarSlot(oldSlot, false)
                    var head = 0
                    while (head < 20) {
                        if (!mc.world.getBlockState(mc.player.position.add(0, head, 0)).block.equals(Blocks.AIR)) {
                            tempHeight = head - 5
                            safeLevel = 1
                        } else {
                            safeLevel = 2
                        }
                        head++
                    }
                    if (mc.connection != null) {/*
                double boost;
                if (offset.value < 0 && getPlayerHeight.value && mc.player.posY >= getHeight.value) {
                    boost = -(Math.abs(mc.player.posY - getHeight.value));
                    if (mc.player.posY >= 65) {
                        boost = boost - (boost - mc.player.posY - 2);
                    }
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + (offset.value + boost), mc.player.posZ, false));
                } else {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + offset.value, mc.player.posZ, false));
                }

                 */
                        when (safeLevel) {
                            1 -> {
                                if (tempHeight != 0) {
                                    mc.player.connection.sendPacket(
                                        CPacketPlayer.PositionRotation(
                                            mc.player.posX,
                                            mc.player.posY + MathHelper.clamp(tempHeight, 0, 15),
                                            mc.player.posZ,
                                            mc.player.rotationYaw,
                                            90f,
                                            false
                                        )
                                    )
                                    mc.player.connection.sendPacket(
                                        CPacketPlayer.PositionRotation(
                                            mc.player.posX,
                                            mc.player.posY - 1.0,
                                            mc.player.posZ,
                                            mc.player.rotationYaw,
                                            90f,
                                            false
                                        )
                                    )
                                    //ChatUtil.sendMessage(tempHeight + "");
                                }
                            }

                            2 -> {
                                mc.player.connection.sendPacket(
                                    CPacketPlayer.PositionRotation(
                                        mc.player.posX,
                                        mc.player.posY + 26,
                                        mc.player.posZ,
                                        mc.player.rotationYaw,
                                        90f,
                                        false
                                    )
                                )
                                mc.player.connection.sendPacket(
                                    CPacketPlayer.Position(
                                        mc.player.posX, mc.player.posY - 1.0, mc.player.posZ, false
                                    )
                                )
                            }
                        }
                    }
                }

                Client.Negative -> {
                    if (mc.connection != null && fakeJump.value) {
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                mc.player.posX,
                                mc.player.posY + 0.41999998688698,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                90f,
                                false
                            )
                        )

                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                mc.player.posX,
                                mc.player.posY + 0.7500019,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                90f,
                                false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                mc.player.posX,
                                mc.player.posY + 0.9999962,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                90f,
                                false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                mc.player.posX,
                                mc.player.posY + 1.17000380178814,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                90f,
                                false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                mc.player.posX,
                                mc.player.posY + 1.17000380178815,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                90f,
                                false
                            )
                        )
                    }
                    BlockUtil.placeBlock(originalPos, EnumHand.MAIN_HAND, false, true)
                    InventoryUtil.switchToHotbarSlot(oldSlot, false)
                    if (mc.connection != null) {
                        var boost: Double
                        if (offsetCheck.value < 0 && getPlayerHeight.value && mc.player.posY >= getHeight.value) {
                            boost = -(abs(mc.player.posY - getHeight.value))
                            if (mc.player.posY >= 65) {
                                boost -= (boost - mc.player.posY - 2)
                            }
                            mc.player.connection.sendPacket(
                                CPacketPlayer.Position(
                                    mc.player.posX, mc.player.posY + (offsetCheck.value + boost), mc.player.posZ, false
                                )
                            )
                        } else {
                            mc.player.connection.sendPacket(
                                CPacketPlayer.Position(
                                    mc.player.posX, mc.player.posY + offsetCheck.value, mc.player.posZ, false
                                )
                            )
                        }
                    }
                }

                Client.Other -> {
                    if (mc.connection != null) {
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                mc.player.posX,
                                mc.player.posY + 0.41999998688698,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                90f,
                                false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                mc.player.posX,
                                mc.player.posY + 0.7500019,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                90f,
                                false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                mc.player.posX,
                                mc.player.posY + 0.9999962,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                90f,
                                false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                mc.player.posX,
                                mc.player.posY + 1.17000380178814,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                90f,
                                false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                mc.player.posX,
                                mc.player.posY + 1.17000380178815,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                90f,
                                false
                            )
                        )
                    }
                    BlockUtil.placeBlock(originalPos, EnumHand.MAIN_HAND, false, true)
                    InventoryUtil.switchToHotbarSlot(oldSlot, false)
                    mc.player.connection.sendPacket(
                        CPacketPlayer.Position(
                            mc.player.posX, mc.player.posY + 1.2426308013947485, mc.player.posZ, false
                        )
                    )
                    if (velocityTime > System.currentTimeMillis()) {
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX, mc.player.posY + 3.3400880035762786, mc.player.posZ, false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX, mc.player.posY - 1.0, mc.player.posZ, false
                            )
                        )
                    } else {
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX, mc.player.posY + 2.3400880035762786, mc.player.posZ, false
                            )
                        )
                    }
                }

                Client.New -> {
                    if (mc.connection != null) {
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                floor(mc.player.posX) + 0.5, mc.player.posY + 0.419999986886978, floor(
                                    mc.player.posZ
                                ) + 0.5, mc.player.rotationYaw, 90f, false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                floor(mc.player.posX) + 0.5, mc.player.posY + 0.7531999805212015, floor(
                                    mc.player.posZ
                                ) + 0.5, mc.player.rotationYaw, 90f, false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                floor(mc.player.posX) + 0.5, mc.player.posY + 1.001335979112147, floor(
                                    mc.player.posZ
                                ) + 0.5, mc.player.rotationYaw, 90f, false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                floor(mc.player.posX) + 0.5, mc.player.posY + 1.166109260938214, floor(
                                    mc.player.posZ
                                ) + 0.5, mc.player.rotationYaw, 90f, false
                            )
                        )
                        BlockUtil.placeBlock(originalPos, EnumHand.MAIN_HAND, false, true)
                        InventoryUtil.switchToHotbarSlot(oldSlot, false)
                        mc.playerController.updateController()
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                mc.player.posX,
                                mc.player.posY + 1.2426308013947485,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                90f,
                                false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.PositionRotation(
                                mc.player.posX + offSet.value,
                                mc.player.posY + offSet.value,
                                mc.player.posZ + offSet.value,
                                mc.player.rotationYaw,
                                90f,
                                true
                            )
                        )
                    }
                }
            }
        }
        disable()
    }

    private fun intersectsWithEntity(pos: BlockPos?): Boolean {
        try {
            for (entity in ArrayList(mc.world.loadedEntityList)) {
                if (entity != null) {
                    if (entity == mc.player) continue
                    if (entity is EntityItem) continue
                    if (AxisAlignedBB(pos!!).intersects(entity.entityBoundingBox)) return true
                }
            }
        } catch (ignored: Exception) {
        }
        return false
    }


    enum class Client {
        New, Melon, Other, Negative
    }

    private fun breakCrystal(pos: BlockPos) {
        val a: Vec3d = mc.player.positionVector
        if (checkCrystal(a, EntityUtil.getVarOffsets(pos.x,pos.y,pos.z)) != null && timer.passedMs(202L)){
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(
                BlockInteractionHelper.getLegitRotations(pos.add(0.5,0.5,0.5))[0],
                BlockInteractionHelper.getLegitRotations(pos.add(0.5,0.5,0.5))[1],true))
            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(pos.x,pos.y,pos.z)), true)
            HoleKicker.crystalTimer.reset()
            timer.reset()
        }
    }

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