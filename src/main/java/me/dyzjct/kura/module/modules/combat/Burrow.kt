package me.dyzjct.kura.module.modules.combat

import me.dyzjct.kura.event.events.client.PacketEvents
import me.dyzjct.kura.event.events.entity.MotionUpdateEvent
import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.module.ModuleManager
import me.dyzjct.kura.module.modules.movement.ReverseStep
import me.dyzjct.kura.setting.ModeSetting
import me.dyzjct.kura.setting.Setting
import me.dyzjct.kura.utils.block.BlockUtil
import me.dyzjct.kura.utils.inventory.InventoryUtil
import me.dyzjct.kura.utils.mc.ChatUtil
import net.minecraft.block.BlockEnderChest
import net.minecraft.block.BlockObsidian
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.item.EntityItem
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.network.play.server.SPacketExplosion
import net.minecraft.network.play.server.SPacketPlayerPosLook
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.stream.Collectors
import kotlin.math.abs

@Module.Info(name = "Burrow", category = Category.COMBAT, description = "Selffill urself in ur mom's pussy")
class Burrow : Module() {
    private val rotate: Setting<Boolean> = bsetting("Rotate", true)
    private val breakcrystal: Setting<Boolean> = bsetting("BreakCrystal", true)
    private val toggleRStep: Setting<Boolean> = bsetting("ToggleRStep", true)
    private val safe: Setting<Boolean> = bsetting("ToggleWhileInObi", true)
    private val safeCheck: Setting<Boolean> = bsetting("LagCheck", true)
    private var clientMode: ModeSetting<*> = msetting("ClientMode", Client.Melon)
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

    override fun onEnable() {
        if (fullNullCheck()) {
            return
        }
        if (breakcrystal.value){
            this.breakcrystal()
        }
        if (toggleRStep.value) {
            ModuleManager.getModuleByClass(ReverseStep::class.java).disable()
        }
        originalPos = BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)
        if (mc.world.getBlockState(
                BlockPos(
                    mc.player.posX,
                    mc.player.posY,
                    mc.player.posZ
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

    @SubscribeEvent
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
    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent.FastTick?){
        if (fullNullCheck()) {
            return
        }
        oldSlot = mc.player.inventory.currentItem
        if (mc.world.getBlockState(
                BlockPos(
                    mc.player.posX,
                    mc.player.posY,
                    mc.player.posZ
                )
            ).block !== Blocks.AIR && mc.world.getBlockState(
                BlockPos(
                    mc.player.posX,
                    mc.player.posY,
                    mc.player.posZ
                )
            ).block !== Blocks.ENDER_CHEST && mc.world.getBlockState(
                BlockPos(
                    mc.player.posX,
                    mc.player.posY,
                    mc.player.posZ
                )
            ).block !== Blocks.LAVA && mc.world.getBlockState(
                BlockPos(
                    mc.player.posX,
                    mc.player.posY,
                    mc.player.posZ
                )
            ).block !== Blocks.WATER && mc.world.getBlockState(
                BlockPos(
                    mc.player.posX,
                    mc.player.posY,
                    mc.player.posZ
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
                        0.0,
                        y.toDouble(),
                        0.0
                    )
                ).block !== Blocks.LAVA && mc.world.getBlockState(
                    BlockPos(mc.player.positionVector).add(
                        0.0,
                        y.toDouble(),
                        0.0
                    )
                ).block !== Blocks.FLOWING_LAVA && mc.world.getBlockState(
                    BlockPos(mc.player.positionVector).add(
                        0.0,
                        y.toDouble(),
                        0.0
                    )
                ).block !== Blocks.WATER && mc.world.getBlockState(
                    BlockPos(mc.player.positionVector).add(
                        0.0,
                        y.toDouble(),
                        0.0
                    )
                ).block !== Blocks.FLOWING_WATER && mc.world.getBlockState(
                    BlockPos(mc.player.positionVector).add(
                        0.0,
                        y.toDouble(),
                        0.0
                    )
                ).block !== Blocks.AIR && mc.world.getBlockState(
                    BlockPos(mc.player.positionVector).add(
                        0.0,
                        (y + 1f).toDouble(),
                        0.0
                    )
                ).block !== Blocks.AIR && mc.world.getBlockState(
                    BlockPos(mc.player.positionVector).add(
                        0.0,
                        (y + 2f).toDouble(),
                        0.0
                    )
                ).block !== Blocks.AIR && safeCheck.value
            ) {
                ChatUtil.NoSpam.sendMessage("Prevented Burrow LagOut!")
                InventoryUtil.switchToHotbarSlot(oldSlot,false)
                disable()
                return
            }
        }
        if (InventoryUtil.findHotbarBlock(BlockObsidian::class.java) != -1) {
            InventoryUtil.switchToHotbarSlot(InventoryUtil.findHotbarBlock(BlockObsidian::class.java),false)
        } else if (InventoryUtil.findHotbarBlock(BlockObsidian::class.java) == -1 && InventoryUtil.findHotbarBlock(
                BlockEnderChest::class.java
            ) != -1
        ) {
            InventoryUtil.switchToHotbarSlot(InventoryUtil.findHotbarBlock(BlockEnderChest::class.java),false)
        }
        when (clientMode.value) {
            Client.Melon -> {
                //InventoryUtil.switchToHotbarSlot(mode.value.equals(Mode.Obsidian) ? InventoryUtil.findHotbarBlock(BlockObsidian.class) : InventoryUtil.findHotbarBlock(BlockEnderChest.class), false);
                if (mc.connection != null && fakeJump.value) {
                    mc.player.connection.sendPacket(
                        CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY + 0.41999998688698,
                            mc.player.posZ,
                            false
                        )
                    )
                    mc.player.connection.sendPacket(
                        CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY + 0.7500019,
                            mc.player.posZ,
                            false
                        )
                    )
                    mc.player.connection.sendPacket(
                        CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY + 0.9999962,
                            mc.player.posZ,
                            false
                        )
                    )
                    mc.player.connection.sendPacket(
                        CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY + 1.17000380178814,
                            mc.player.posZ,
                            false
                        )
                    )
                    mc.player.connection.sendPacket(
                        CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY + 1.17000380178815,
                            mc.player.posZ,
                            false
                        )
                    )
                }
//                if (rotate.value) {
//                    event.setRotation(mc.player.rotationYaw, 90f);
//                    it.setRotation(mc.player.rotationYaw, 90f)
//                }
                BlockUtil.placeBlock(originalPos, EnumHand.MAIN_HAND, rotate.value, true)
                InventoryUtil.switchToHotbarSlot(oldSlot,false)
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
                if (mc.connection != null) {
                    /*
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
                                    CPacketPlayer.Position(
                                        mc.player.posX,
                                        mc.player.posY + MathHelper.clamp(tempHeight, 0, 15),
                                        mc.player.posZ,
                                        false
                                    )
                                )
                                mc.player.connection.sendPacket(
                                    CPacketPlayer.Position(
                                        mc.player.posX,
                                        mc.player.posY - 1.0,
                                        mc.player.posZ,
                                        false
                                    )
                                )
                                //ChatUtil.sendMessage(tempHeight + "");
                            }
                        }

                        2 -> {
                            mc.player.connection.sendPacket(
                                CPacketPlayer.Position(
                                    mc.player.posX,
                                    mc.player.posY + 26,
                                    mc.player.posZ,
                                    false
                                )
                            )
                            mc.player.connection.sendPacket(
                                CPacketPlayer.Position(
                                    mc.player.posX,
                                    mc.player.posY - 1.0,
                                    mc.player.posZ,
                                    false
                                )
                            )
                        }
                    }
                }
            }

            Client.Negative -> {
                if (mc.connection != null && fakeJump.value) {
                    mc.player.connection.sendPacket(
                        CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY + 0.41999998688698,
                            mc.player.posZ,
                            false
                        )
                    )
                    mc.player.connection.sendPacket(
                        CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY + 0.7500019,
                            mc.player.posZ,
                            false
                        )
                    )
                    mc.player.connection.sendPacket(
                        CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY + 0.9999962,
                            mc.player.posZ,
                            false
                        )
                    )
                    mc.player.connection.sendPacket(
                        CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY + 1.17000380178814,
                            mc.player.posZ,
                            false
                        )
                    )
                    mc.player.connection.sendPacket(
                        CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY + 1.17000380178815,
                            mc.player.posZ,
                            false
                        )
                    )
                }
//                if (rotate.value) {
//                    event.setRotation(mc.player.rotationYaw, 90f)
//                }
                BlockUtil.placeBlock(originalPos, EnumHand.MAIN_HAND, rotate.value, true)
                InventoryUtil.switchToHotbarSlot(oldSlot,false)
                if (mc.connection != null) {
                    var boost: Double
                    if (offsetCheck.value < 0 && getPlayerHeight.value && mc.player.posY >= getHeight.value) {
                        boost = -(abs(mc.player.posY - getHeight.value))
                        if (mc.player.posY >= 65) {
                            boost -= (boost - mc.player.posY - 2)
                        }
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + (offsetCheck.value + boost),
                                mc.player.posZ,
                                false
                            )
                        )
                    } else {
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + offsetCheck.value,
                                mc.player.posZ,
                                false
                            )
                        )
                    }
                }
            }

                Client.Other -> {
                    if (mc.connection != null) {
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + 0.41999998688698,
                                mc.player.posZ,
                                false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + 0.7500019,
                                mc.player.posZ,
                                false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + 0.9999962,
                                mc.player.posZ,
                                false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + 1.17000380178814,
                                mc.player.posZ,
                                false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + 1.17000380178815,
                                mc.player.posZ,
                                false
                            )
                        )
                    }
                    BlockUtil.placeBlock(originalPos, EnumHand.MAIN_HAND, rotate.value, true)
                    InventoryUtil.switchToHotbarSlot(oldSlot,false)
                    mc.player.connection.sendPacket(
                        CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY + 1.2426308013947485,
                            mc.player.posZ,
                            false
                        )
                    )
                    if (velocityTime > System.currentTimeMillis()) {
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + 3.3400880035762786,
                                mc.player.posZ,
                                false
                            )
                        )
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY - 1.0,
                                mc.player.posZ,
                                false
                            )
                        )
                    } else {
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + 2.3400880035762786,
                                mc.player.posZ,
                                false
                            )
                        )
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

    fun breakcrystal() {
        if (fullNullCheck()) return
        for (crystal in mc.world.loadedEntityList.stream().filter { e: Entity -> e is EntityEnderCrystal && !e.isDead }
            .sorted(Comparator.comparing { e: Entity? ->
                java.lang.Float.valueOf(
                    mc.player.getDistance(e)
                )
            }).collect(Collectors.toList())) {
            if (crystal !is EntityEnderCrystal || mc.player.getDistance(crystal) > 4.0f) continue
            mc.player.connection.sendPacket(CPacketUseEntity(crystal))
            mc.player.connection.sendPacket(CPacketAnimation(EnumHand.OFF_HAND))
        }
    }

    enum class Client {
        Melon, Other, Negative
    }

    companion object {
        var INSTANCE = Burrow()
    }
}