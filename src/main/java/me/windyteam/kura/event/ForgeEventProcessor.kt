package me.windyteam.kura.event

import com.google.common.base.Strings
import me.windyteam.kura.Kura
import me.windyteam.kura.command.Command
import me.windyteam.kura.command.commands.mc.PeekCommand
import me.windyteam.kura.event.events.client.ConnectEvent
import me.windyteam.kura.event.events.client.DisconnectEvent
import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.module.ModuleManager
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.CrystalDamageCalculator
import me.windyteam.kura.notification.NotificationManager
import me.windyteam.kura.utils.Timer
import me.windyteam.kura.utils.Utils
import me.windyteam.kura.utils.Wrapper
import me.windyteam.kura.utils.gl.XG42Tessellator
import me.windyteam.kura.utils.mc.ChatUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.gui.inventory.GuiShulkerBox
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.passive.AbstractHorse
import net.minecraft.network.play.server.SPacketPlayerListItem
import net.minecraft.network.play.server.SPacketPlayerListItem.AddPlayerData
import net.minecraftforge.client.event.ClientChatEvent
import net.minecraftforge.client.event.InputUpdateEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.util.*

object ForgeEventProcessor : Event() {
    private var logoutTimer = Timer()
    var yaw = 0f
    var pitch = 0f

    @SubscribeEvent
    fun onWorldRender(event: RenderWorldLastEvent) {
        if (event.isCanceled || Utils.nullCheck()) {
            return
        }
        try {
            ModuleManager.onWorldRender(event)
        } catch (ex: RuntimeException) {
            ex.printStackTrace()
        }
    }

    @SubscribeEvent
    fun onClientDisconnect(event: ClientDisconnectionFromServerEvent?) {
        logoutTimer.reset()
        ModuleManager.onLogout()
    }

    @SubscribeEvent
    fun onClientConnect(event: ClientConnectedToServerEvent?) {
        ModuleManager.onLogin()
    }

    @SubscribeEvent
    fun onKey(event: InputUpdateEvent?) {
        try {
            ModuleManager.onKey(event)
        } catch (ex: RuntimeException) {
            ex.printStackTrace()
        }
    }

    @SubscribeEvent
    fun onRender(event: RenderGameOverlayEvent.Post) {
        if (!event.isCanceled || !Utils.nullCheck()) {
            try {
                var target = RenderGameOverlayEvent.ElementType.EXPERIENCE
                if (!Wrapper.mc.player.isCreative && Wrapper.mc.player.getRidingEntity() is AbstractHorse) {
                    target = RenderGameOverlayEvent.ElementType.HEALTHMOUNT
                }
                if (event.type == target) {
                    ModuleManager.onRender(event)
                    GL11.glPushMatrix()
                    ChatUtil.drawNotifications()
                    NotificationManager.render()
                    GL11.glPopMatrix()
                    XG42Tessellator.releaseGL()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        } else {
            NotificationManager.render()
        }
    }

    @SubscribeEvent
    fun onUpdate(event: ClientTickEvent) {
        if (event.isCanceled || Utils.nullCheck()) {
            return
        }
        try {
            ModuleManager.onUpdate()
            for (entity in ArrayList(mc.world.loadedEntityList)) {
                if (entity !is EntityLivingBase) continue
                CrystalDamageCalculator.reductionMap[entity] = CrystalDamageCalculator.Companion.DamageReduction(entity)
            }
            if (PeekCommand.sb != null) {
                val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
                val i = scaledResolution.scaledWidth
                val j = scaledResolution.scaledHeight
                val gui = GuiShulkerBox(Wrapper.mc.player.inventory, PeekCommand.sb)
                gui.setWorldAndResolution(Wrapper.getMinecraft(), i, j)
                Minecraft.getMinecraft().displayGuiScreen(gui)
                PeekCommand.sb = null
            }
        } catch (ignored: RuntimeException) {
        }
    }

    @SubscribeEvent
    fun onPacketReceive(event: PacketEvents.Receive) {
        if (event.packet is SPacketPlayerListItem && logoutTimer.passed(1)) {
            event.packet
            if (SPacketPlayerListItem.Action.ADD_PLAYER != (event.packet as SPacketPlayerListItem).action && SPacketPlayerListItem.Action.REMOVE_PLAYER != (event.packet as SPacketPlayerListItem).action) {
                return
            }
            (event.packet as SPacketPlayerListItem).entries.stream().filter { obj: AddPlayerData? -> Objects.nonNull(obj) }
                .filter { data: AddPlayerData -> !Strings.isNullOrEmpty(data.profile.name) || data.profile.id != null }
                .forEach { data: AddPlayerData ->
                    val id = data.profile.id
                    when ((event.packet as SPacketPlayerListItem).action) {
                        SPacketPlayerListItem.Action.ADD_PLAYER -> {
                            val name = data.profile.name
                            MinecraftForge.EVENT_BUS.post(
                                ConnectEvent(
                                    name
                                )
                            )
                        }

                        SPacketPlayerListItem.Action.REMOVE_PLAYER -> {
                            val entity = Minecraft.getMinecraft().world.getPlayerEntityByUUID(id)
                            if (entity != null) {
                                val logoutName = entity.name
                                MinecraftForge.EVENT_BUS.post(
                                    DisconnectEvent(
                                        logoutName
                                    )
                                )
                            }
                            MinecraftForge.EVENT_BUS.post(
                                DisconnectEvent(
                                    null
                                )
                            )
                        }
                    }
                }
        }
    }

    @SubscribeEvent
    fun onKeyInput(event: KeyInputEvent?) {
        if (Utils.nullCheck()) {
            return
        }
        try {
            if (Keyboard.getEventKeyState()) {
                ModuleManager.onBind(Keyboard.getEventKey())
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onChatSent(event: ClientChatEvent) {
        if (event.message.startsWith(Command.getCommandPrefix())) {
            event.isCanceled = true
            try {
                Wrapper.getMinecraft().ingameGUI.chatGUI.addToSentMessages(event.message)
                if (event.message.length > 1) {
                    Kura.instance.commandManager!!
                        .callCommand(event.message.substring(Command.getCommandPrefix().length - 1))
                } else {
                    ChatUtil.NoSpam.sendWarnMessage("Please enter a command.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            event.message = ""
        }
    }

    private var mc = Minecraft.getMinecraft()
}