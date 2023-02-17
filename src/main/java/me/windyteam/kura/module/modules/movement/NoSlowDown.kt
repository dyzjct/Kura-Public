package me.windyteam.kura.module.modules.movement

import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.event.events.gui.KeyEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.Setting
import net.minecraft.client.gui.*
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.settings.KeyBinding
import net.minecraft.item.ItemBow
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemPotion
import net.minecraft.network.Packet
import net.minecraft.network.play.client.*
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.InputUpdateEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard

@Module.Info(
    name = "NoSlowDown",
    category = Category.MOVEMENT,
    description = "Prevents being slowed down when using an item or going through cobwebs"
)
class NoSlowDown : Module() {
    private var guiMove: Setting<Boolean> = bsetting("GuiMove", true)
    @JvmField
    var soulSand: Setting<Boolean> = bsetting("SoulSand", true)
    private var strict: Setting<Boolean> = bsetting("Strict", true)
    var superStrict: Setting<Boolean> = bsetting("SuperStrict", false)
    var sneakPacket: Setting<Boolean> = bsetting("SneakPacket", false)
    var sneaking = false
    override fun onUpdate() {
        if (fullNullCheck()) return
        if (guiMove.value) if (mc.currentScreen is GuiOptions || mc.currentScreen is GuiVideoSettings || mc.currentScreen is GuiScreenOptionsSounds || mc.currentScreen is GuiContainer || mc.currentScreen is GuiIngameMenu) {
            for (bind in keys) KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()))
        } else if (mc.currentScreen == null) {
            for (bind in keys) {
                if (!Keyboard.isKeyDown(bind.getKeyCode())) KeyBinding.setKeyBindState(bind.getKeyCode(), false)
            }
        }
        if (sneaking && !mc.player.isHandActive && sneakPacket.value) {
            mc.player.connection.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING))
            sneaking = false
        }
    }

    @SubscribeEvent
    fun onWorldEvent(event: EntityJoinWorldEvent?) {
        if (sneakPacket.value && sneaking && !mc.player.isHandActive) {
            mc.player.connection.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING))
            sneaking = false
        }
    }

    @SubscribeEvent
    fun onUseItem(event: RightClickItem) {
        val item = mc.player.getHeldItem(event.hand).getItem()
        if (sneakPacket.value && !sneaking && (item is ItemFood || item is ItemBow || item is ItemPotion)) {
            mc.player.connection.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING))
            sneaking = true
        }
    }

    @SubscribeEvent
    fun onInput(event: InputUpdateEvent) {
        if (fullNullCheck()) return
        if (mc.player.isHandActive && !mc.player.isRiding) {
            event.movementInput.moveStrafe *= 5.0f
            event.movementInput.moveForward *= 5.0f
        }
    }

    @SubscribeEvent
    fun onKeyEvent(event: KeyEvent) {
        if (fullNullCheck()) return
        if (guiMove.value && event.stage == 0 && mc.currentScreen !is GuiChat) event.info = event.pressed
    }

    @SubscribeEvent
    fun onPacket(event: PacketEvents.Send) {
        if (fullNullCheck()) return
        if (event.getPacket<Packet<*>>() is CPacketPlayer && strict.value && mc.player.isHandActive && !mc.player.isRiding) mc.player.connection.sendPacket(
            CPacketPlayerDigging(
                CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, BlockPos(
                    Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ)
                ), EnumFacing.DOWN
            )
        )
        if (event.stage == 1 && (event.getPacket<Packet<*>>() is CPacketPlayerTryUseItem || event.getPacket<Packet<*>>() is CPacketPlayerTryUseItemOnBlock)) {
            val item = mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem()
            if (superStrict.value && (item is ItemFood || item is ItemBow || item is ItemPotion)) mc.player.connection.sendPacket(
                CPacketHeldItemChange(
                    mc.player.inventory.currentItem
                )
            )
        }
    }

    companion object {
        var keys = arrayOf(
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindJump,
            mc.gameSettings.keyBindSprint
        )
        var INSTANCE = NoSlowDown()
    }
}