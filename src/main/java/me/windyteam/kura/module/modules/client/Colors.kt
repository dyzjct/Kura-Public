package me.windyteam.kura.module.modules.client

import com.mojang.realmsclient.gui.ChatFormatting
import me.windyteam.kura.event.events.gui.GuiScreenEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

@Module.Info(name = "Colors", category = Category.CLIENT, visible = false)
object Colors : Module() {
    @JvmField
    var color = csetting("Color", Color(210, 100, 165))
    @JvmField
    var rainbow = bsetting("Rainbow", false)
    @JvmField
    var particle = bsetting("Particle", true)
    var blur = bsetting("Blur", false)
    @JvmField
    var chat = bsetting("ToggleChat", false)
    var chatColor = msetting("ChatColor", ChatColor.AQUA)
    @JvmField
    var rainbowSpeed = fsetting("RainbowSpeed", 5.0f, 0.0f, 30.0f).b(rainbow)
    @JvmField
    var rainbowHue = isetting("RainbowHue", 1, 0, 1).b(rainbow)
    @JvmField
    var rainbowSaturation = fsetting("Saturation", 0.65f, 0.0f, 1.0f).b(rainbow)
    @JvmField
    var rainbowBrightness = fsetting("Brightness", 1.0f, 0.0f, 1.0f).b(rainbow)

    @JvmField
    var GradientIntensity = isetting("GIntensity", 50, 0, 500).b(rainbow)
    @JvmField
    var background = msetting("Background", Background.Shadow)
    @JvmField
    var setting = msetting("Setting", SettingViewType.SIDE)

//    var fadeColor = csetting("FadeColor", Color(100, 181, 210))

    @SubscribeEvent
    fun onGuiScreenEvent(event: GuiScreenEvent.Displayed) {
        if (fullNullCheck()) {
            return
        }
        if (blur.value && !mc.entityRenderer.isShaderActive && event.screen != null) {
            mc.entityRenderer.loadShader(ResourceLocation("shader/blur/blur.json"))
        } else if (mc.entityRenderer.isShaderActive && event.screen == null) {
            mc.entityRenderer.stopUseShader()
        }
    }

    fun chatColorMode(): ChatFormatting {
        when (chatColor.value) {
            ChatColor.BLACK -> {
                return ChatFormatting.BLACK
            }
            ChatColor.DARK_BLUE -> {
                return ChatFormatting.DARK_AQUA
            }
            ChatColor.DARK_GREEN -> {
                return ChatFormatting.DARK_GREEN
            }
            ChatColor.DARK_AQUA -> {
                return ChatFormatting.DARK_AQUA
            }
            ChatColor.DARK_RED -> {
                return ChatFormatting.DARK_RED
            }
            ChatColor.DARK_PURPLE -> {
                return ChatFormatting.DARK_PURPLE
            }
            ChatColor.GOLD -> {
                return ChatFormatting.GOLD
            }
            ChatColor.GRAY -> {
                return ChatFormatting.GRAY
            }
            ChatColor.DARK_GRAY -> {
                return ChatFormatting.DARK_GRAY
            }
            ChatColor.BLUE -> {
                return ChatFormatting.BLUE
            }
            ChatColor.GREEN -> {
                return ChatFormatting.GREEN
            }
            ChatColor.AQUA -> {
                return ChatFormatting.AQUA
            }
            ChatColor.RED -> {
                return ChatFormatting.RED
            }
            ChatColor.LIGHT_PURPLE -> {
                return ChatFormatting.LIGHT_PURPLE
            }
            ChatColor.YELLOW -> {
                return  ChatFormatting.YELLOW
            }
            else -> {
                return ChatFormatting.WHITE
            }
        }
    }

//    enum class Background {
//        SHADOW,
//        BLUR,
//        BOTH,
//        NONE
//    }


    enum class Background {
        Shadow,
        Blur,
        Both,
        None
    }


    enum class SettingViewType {
        RECT,
        SIDE,
        NONE
    }

    enum class ChatColor {
        BLACK,
        DARK_BLUE,
        DARK_GREEN,
        DARK_AQUA,
        DARK_RED,
        DARK_PURPLE,
        GOLD,
        GRAY,
        DARK_GRAY,
        BLUE,
        GREEN,
        AQUA,
        RED,
        LIGHT_PURPLE,
        YELLOW,
        WHITE
    }
}
