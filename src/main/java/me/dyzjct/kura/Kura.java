package me.dyzjct.kura;

import me.dyzjct.kura.command.CommandManager;
import me.dyzjct.kura.event.ForgeEventProcessor;
import me.dyzjct.kura.event.events.player.EventPlayerTravel;
import me.dyzjct.kura.gui.clickgui.GUIRender;
import me.dyzjct.kura.gui.clickgui.HUDRender;
import me.dyzjct.kura.gui.settingpanel.XG42SettingPanel;
import me.dyzjct.kura.manager.FileManager;
import me.dyzjct.kura.manager.FriendManager;
import me.dyzjct.kura.manager.GuiManager;
import me.dyzjct.kura.manager.RotationManager;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.module.modules.client.SettingPanel;
import me.dyzjct.kura.notification.NotificationManager;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.setting.StringSetting;
import me.dyzjct.kura.utils.Utils;
import me.dyzjct.kura.utils.font.CFontRenderer;
import me.dyzjct.kura.utils.math.deneb.LagCompensator;
import net.minecraft.util.Util;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@Mod(modid = Kura.MOD_ID, name = Kura.MOD_NAME, version = Kura.VERSION)
public class Kura {
    public static final String MOD_ID = "kura";
    public static final String MOD_NAME = "Kura";
    public static final String VERSION = "1.7";
    public static final String DISPLAY_NAME = MOD_NAME + " " + VERSION;
    public static final String KANJI = "Kura";
    //public static final String YOUTUBE_API_KEY = "AIzaSyD9zQgKJLI-LwOoe4Up1W9Rg4fXDBiAGok";
    public static final String ALT_Encrypt_Key = "Kura";
    public static final Logger logger = LogManager.getLogger("Kura");
    public static Kura INSTANCE = new Kura();
    public static Setting<String> commandPrefix = new StringSetting("CommandPrefix", null, ".");
    public static CFontRenderer fontRenderer;
    //public AuthClient auth = new AuthClient("192.144.217.217" , 25565);
    public ModuleManager moduleManager;
    public FriendManager friendManager;
    public CommandManager commandManager;
    public RotationManager rotationManager;
    public FileManager configManager;
    public GUIRender guiRender;
    public HUDRender hudEditor;
    public GuiManager guiManager;

    public static Kura getInstance() {
        return INSTANCE;
    }


    public static void setTitleAndIcon() {
        Display.setTitle(Kura.DISPLAY_NAME);
        setIcon();
    }

    public static void setIcon() {
        Util.EnumOS OS = Util.getOSType();
        if (OS != Util.EnumOS.OSX) {
            try {
                InputStream inputstream = Kura.class.getResourceAsStream("/assets/Kura/logo/Kura.png");
                if (inputstream != null) {
                    Display.setIcon(new ByteBuffer[]{Utils.readImageToBuffer(inputstream)});
                }
            } catch (IOException e) {
                e.getStackTrace();
            }
        }
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public FriendManager getFriendManager() {
        return friendManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        setTitleAndIcon();
        new LagCompensator();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Kura.getInstance().rotationManager = new RotationManager();
        Kura.getInstance().moduleManager = new ModuleManager();
        Kura.getInstance().commandManager = new CommandManager();
        Kura.getInstance().friendManager = new FriendManager();
        Kura.getInstance().guiManager = new GuiManager();
        MinecraftForge.EVENT_BUS.register(new ForgeEventProcessor());
        MinecraftForge.EVENT_BUS.register(new RotationManager());
//        MinecraftForge.EVENT_BUS.register(new EventPlayerTravel());
        Kura.getInstance().guiRender = new GUIRender();
        Kura.getInstance().hudEditor = new HUDRender();
        Kura.getInstance().configManager = new FileManager();
        FileManager.loadAll();
    }

    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        SettingPanel.INSTANCE.setGUIScreen(new XG42SettingPanel());
        NotificationManager.pendingNotifications.clear();
    }

}