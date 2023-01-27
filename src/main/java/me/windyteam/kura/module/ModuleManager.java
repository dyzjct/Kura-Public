package me.windyteam.kura.module;

import me.windyteam.kura.Kura;
import me.windyteam.kura.event.events.render.RenderEvent;
import me.windyteam.kura.gui.clickgui.guis.HUDEditorScreen;
import me.windyteam.kura.module.hud.huds.*;
import me.windyteam.kura.module.hud.info.*;
import me.windyteam.kura.module.modules.chat.*;
import me.windyteam.kura.module.modules.client.*;
import me.windyteam.kura.module.modules.combat.*;
import me.windyteam.kura.module.modules.combat.holefillers.HoleFiller;
import me.windyteam.kura.module.modules.crystalaura.KuraAura;
import me.windyteam.kura.module.modules.crystalaura.LunarAura;
import me.windyteam.kura.module.modules.misc.*;
import me.windyteam.kura.module.modules.movement.*;
import me.windyteam.kura.module.modules.player.*;
import me.windyteam.kura.module.modules.render.*;
import me.windyteam.kura.module.modules.xddd.*;
import me.windyteam.kura.utils.gl.XG42Tessellator;
import me.windyteam.kura.utils.mc.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
    public static ModuleManager INSTANCE;
    public static List<IModule> modules = new ArrayList<>();
    public static Minecraft mc = Minecraft.getMinecraft();

    public ModuleManager() {
        INSTANCE = this;
        this.init();
    }

    public static void onKey(InputUpdateEvent event) {
        modules.forEach(mod -> {
            if (mod.isEnabled()) {
                mod.onKey(event);
            }
        });
    }

    public static void registerModule(Module module) {
        try {
            modules.add(module);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't initiate module " + module.getClass().getSimpleName() + "! Err: " + e.getClass().getSimpleName() + ", message: " + e.getMessage());
        }
    }

    public static void registerHUD(HUDModule module) {
        try {
            modules.add(module);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't initiate module " + module.getClass().getSimpleName() + "! Err: " + e.getClass().getSimpleName() + ", message: " + e.getMessage());
        }
    }

    public static List<IModule> getAllIModules() {
        return modules;
    }

    public static List<IModule> getModules() {
        return modules.stream().filter(module -> module instanceof Module).collect(Collectors.toList());
    }

    public static List<IModule> getHUDModules() {
        return modules.stream().filter(module -> module instanceof HUDModule).collect(Collectors.toList());
    }

    public static IModule getModuleByName(String targetName) {
        for (IModule iModule : ModuleManager.getAllIModules()) {
            if (!iModule.name.equalsIgnoreCase(targetName)) continue;
            return iModule;
        }
        //XG42.logger.fatal("Module " + targetName + " is not exist.Please check twice!");
        return new NullModule();
    }

    public static IModule getModuleByClass(Class<?> targetName) {
        for (IModule iModule : ModuleManager.getAllIModules()) {
            if (!iModule.getClass().equals(targetName)) continue;
            return iModule;
        }
        //XG42.logger.fatal("Module " + targetName + " is not exist.Please check twice!");
        return new NullModule();
    }

    public static HUDModule getHUDByName(String targetName) {
        for (IModule iModule : ModuleManager.getHUDModules()) {
            if (!iModule.name.equalsIgnoreCase(targetName)) continue;
            return (HUDModule) iModule;
        }
        //XG42.logger.fatal("HUD " + targetName + " is not exist.Please check twice!");
        return new NullHUD();
    }

    public static void onBind(int bind) {
        if (bind == 0) {
            return;
        }
        modules.forEach(module -> {
            if (module.getBind() == bind) {
                module.toggle();
            }
        });
    }

    public static void onUpdate() {
        modules.forEach(mod -> {
            if (mod.isEnabled()) {
                mod.onUpdate();
            }
        });
    }

    public static void onLogin() {
        modules.forEach(mod -> {
            if (mod.isEnabled()) {
                mod.onLogin();
            }
        });
    }

    public static void onLogout() {
        modules.forEach(mod -> {
            if (mod.isEnabled()) {
                mod.onLogout();
            }
        });
    }

    public static void onRender(RenderGameOverlayEvent.Post event) {
        modules.forEach(mod -> {
            if (mod.isEnabled()) {
                mod.onRender2D(event);
            }
        });
        ModuleManager.onRenderHUD();
    }

    public static void onRenderHUD() {
        if (!(Minecraft.getMinecraft().currentScreen instanceof HUDEditorScreen)) {
            ModuleManager.getHUDModules().forEach(mod -> {
                if (mod.isEnabled()) {
                    mod.onRender();
                }
            });
        }
    }

    public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(EntityUtil.getInterpolatedAmount(entity, ticks));
    }

    public static void onWorldRender(RenderWorldLastEvent event) {
        Minecraft.getMinecraft().profiler.startSection("Kura");
        Minecraft.getMinecraft().profiler.startSection("setup");
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableDepth();
        GlStateManager.glLineWidth(1.0f);
        Vec3d renderPos = getInterpolatedPos(Minecraft.getMinecraft().getRenderViewEntity(), event.getPartialTicks());
        RenderEvent e = new RenderEvent(XG42Tessellator.INSTANCE, renderPos);
        e.resetTranslation();
        Minecraft.getMinecraft().profiler.endSection();
        modules.forEach(mod -> {
            if (mod.isEnabled()) {
                Minecraft.getMinecraft().profiler.startSection(mod.getName());
                mod.onWorldRender(e);
                Minecraft.getMinecraft().profiler.endSection();
            }
        });
        Minecraft.getMinecraft().profiler.startSection("release");
        GlStateManager.glLineWidth(1.0f);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        XG42Tessellator.releaseGL();
        Minecraft.getMinecraft().profiler.endSection();
    }

    public void init() {
        this.loadModules();
        this.loadHUDs();
        modules.sort(Comparator.comparing(IModule::getName));
        Kura.logger.info("Module Initialised");
    }

    public void loadModules() {
        //Chat
        registerModule(new AutoGG());
        registerModule(new ChatSuffix());
        registerModule(new ChatTimeStamps());
        registerModule(new ChatNotifier());
        registerModule(new AutoCNM());
        registerModule(new Spammer());
        //Client
        registerModule(new ClickGui());
        registerModule(new Colors());
        registerModule(new CustomFont());
        registerModule(new HUDEditor());
        registerModule(new NullModule());
        //Render
        registerModule(new Animations());
        registerModule(new AntiPlayerSwing());
        registerModule(new CameraClip());
        registerModule(new SkyColor());
        registerModule(new CustomFov());
        registerModule(new Wireframe());
        registerModule(new HandColor());
        registerModule(new ArmourHUD());
        registerModule(new TabFriends());
        registerModule(new ViewModel());
        registerModule(new NoHurtCam());
        registerModule(new NoRender());
        registerModule(new PopChams());
        registerModule(new Brightness());
        registerModule(new HoleESP());
        registerModule(new LogoutSpots());
        registerModule(new Nametags());
        registerModule(new PearlViewer());
        registerModule(new PortalESP());
        registerModule(new ShulkerPreview());
        registerModule(new Notification());
        registerModule(new BreakESP());
        registerModule(new ESP());
        registerModule(new BurrowESP());
        registerModule(new CityESP());
        registerModule(new Aspect());
        registerModule(new CrystalRender());

        //Combat
        registerModule(new Aimbot());
        registerModule(new Anti32kTotem());
        registerModule(new Aura32K());
        registerModule(new Auto32GAY());
        registerModule(new AutoMend());
        registerModule(new AutoReplenish());
        registerModule(new AutoTotem());
        registerModule(new AutoTrap());
        registerModule(new AutoCity());
        registerModule(new Burrow());
        registerModule(new Burrow2());
        registerModule(new Criticals());
        registerModule(new CevBreaker());
        registerModule(new DispenserMeta());
        registerModule(new Fastuse());
        registerModule(new HoleSnap());
        registerModule(new KillAura());
        registerModule(new PistonCrystal());
        registerModule(new Pull32k());
        registerModule(new Quiver());
        registerModule(new SelfWeb());
        registerModule(new TotemPopCounter());
        registerModule(new VisualRange());
        registerModule(new EzBow());
        registerModule(new AntiBurrow());
        registerModule(new HoleFiller());
        registerModule(new SmartBurrow());
        registerModule(new HoleKicker2());
        registerModule(new TNTHead());
        registerModule(new AutoTopCev());
        registerModule(new HoleKickerRewrite());
        //Player
        registerModule(new AntiShulkerBox());
        registerModule(new StrictPacketMine());
        registerModule(new LiquidInteract());
        registerModule(new Reach());
        registerModule(new Freecam());
        registerModule(new AutoArmour());
        registerModule(new Blink());
        registerModule(new ChestStealer());
        registerModule(new Multitask());
        registerModule(new NoEntityTrace());
        registerModule(new LowOffHand());
        registerModule(new NoFall());
        registerModule(new PacketCancel());
        registerModule(new PingSpoof());
        registerModule(new Scaffold());
        registerModule(new AntiContainer());
        registerModule(new Timer());
        registerModule(new Timer2());
        registerModule(new TpsSync());
        registerModule(new Disabler());
        registerModule(new CancelPearl());
        registerModule(new TargetBuilder());
        registerModule(new Warner());
        //Misc
        registerModule(new AntiAim());
        registerModule(new HeadBlocker());
        registerModule(new AntiBurrowHelper());
        registerModule(new AntiPiston());
        registerModule(new NoteBot());
        registerModule(new EntityDeSync());
        registerModule(new NoPacketKick( ));
        registerModule(new PacketEat());
        registerModule(new ExtraTab());
        registerModule(new XCarry());
        registerModule(new AutoPorn());
        registerModule(new AutoReconnect());
        registerModule(new AutoRespawn());
        registerModule(new AutoWither());
        registerModule(new FakePlayer());
        registerModule(new Nuker());
        registerModule(new MCP());
        registerModule(new MCF());
        registerModule(new NoRotate());
        registerModule(new EasyKitsCrasher());
        registerModule(new InstantMine());
        registerModule(new AntiHoleKicker());
        registerModule(new PacketAnalyzer());
        //Movement
        registerModule(new Velocity());
        registerModule(new Anchor());
        registerModule(new AutoWalk());
        registerModule(new NoSlowDown());
        registerModule(new EntityControl());
        registerModule(new BoatFly());
        registerModule(new EntitySpeed());
        registerModule(new GuiMove());
        registerModule(new AntiVoid());
        registerModule(new ElytraPlus());
        registerModule(new FastSwim());
        registerModule(new Jesus());
        registerModule(new LongJump());
        registerModule(new PacketFlyRewrite());
        registerModule(new Speed());
        registerModule(new Sprint());
        registerModule(new Step());
        registerModule(new Strafe());
        registerModule(new ReverseStep());
        registerModule(new Flight());
        registerModule(new SafeWalk());
        registerModule(new Phase());
        //XDDD
        registerModule(new SurroundRewrite());
        registerModule(new Surround());
        registerModule(new AutoCraftBed());
        registerModule(new IQBooster());
        registerModule(new SmartOffHand());
        //SEXY
        registerModule(new NewBedAura());
        registerModule(new KuraAura());
        registerModule(new LunarAura());
        getModules().sort(Comparator.comparing(IModule::getName));
    }
    public void loadHUDs() {
        registerHUD(new ShowArrayList());
        registerHUD(new Direction());
        registerHUD(new XG42ShowArrayList());
        registerHUD(new Welcomer());
        registerHUD(new WaterMark());
        registerHUD(new CrystalTargetHUD());
        registerHUD(new Player());
        registerHUD(new Ping());
        registerHUD(new FPS());
        registerHUD(new TPS());
        registerHUD(new CoordsHUD());
        registerHUD(new Server());
        registerHUD(new Obsidian());
        registerHUD(new HoleHud());
        registerHUD(new Friends());
        registerHUD(new TextRadar());
        registerHUD(new SpeedHud());
        registerHUD(new Ram());
        getModules().sort(Comparator.comparing(IModule::getName));
    }
}

