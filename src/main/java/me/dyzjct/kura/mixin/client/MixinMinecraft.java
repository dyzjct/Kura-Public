package me.dyzjct.kura.mixin.client;

import me.dyzjct.kura.Kura;
import me.dyzjct.kura.event.events.entity.MotionUpdateEvent;
import me.dyzjct.kura.event.events.gui.GuiScreenEvent;
import me.dyzjct.kura.event.events.world.WorldEvent;
import me.dyzjct.kura.manager.FileManager;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.utils.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = {Minecraft.class})
public abstract class MixinMinecraft {

    @Shadow
    public WorldClient world;
    @Shadow
    public EntityPlayerSP player;
    @Shadow
    public GuiScreen currentScreen;
    @Shadow
    public GameSettings gameSettings;
    @Shadow
    public boolean skipRenderWorld;
    @Shadow
    public SoundHandler soundHandler;


    @Shadow
    public PlayerControllerMP playerController;

//    @Shadow
    @Nullable
//    public abstract NetHandlerPlayClient getConnection();

    public Minecraft mc;

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void loadWorld(WorldClient p_loadWorld_1_, String p_loadWorld_2_, final CallbackInfo callbackInfo) {
        MinecraftForge.EVENT_BUS.post(new WorldEvent(p_loadWorld_1_));
    }

    @Inject(method = "displayGuiScreen", at = @At("HEAD"), cancellable = true)
    public void displayGuiScreen(GuiScreen guiScreenIn, CallbackInfo info) {
        GuiScreenEvent.Closed screenEvent = new GuiScreenEvent.Closed(Wrapper.getMinecraft().currentScreen);
        MinecraftForge.EVENT_BUS.post(screenEvent);
        GuiScreenEvent.Displayed screenEvent1 = new GuiScreenEvent.Displayed(guiScreenIn);
        MinecraftForge.EVENT_BUS.post(screenEvent1);
        guiScreenIn = screenEvent1.getScreen();

        if (guiScreenIn == null && this.world == null) {
            guiScreenIn = new GuiMainMenu();
        } else if (guiScreenIn == null && this.player.getHealth() <= 0.0F) {
            guiScreenIn = new GuiGameOver(null);
        }

        GuiScreen old = this.currentScreen;
        GuiOpenEvent event = new GuiOpenEvent(guiScreenIn);

        if (MinecraftForge.EVENT_BUS.post(event)) return;

        guiScreenIn = event.getGui();
        if (old != null && guiScreenIn != old) {
            old.onGuiClosed();
        }

        this.currentScreen = guiScreenIn;

        if (guiScreenIn != null) {
            Minecraft.getMinecraft().setIngameNotInFocus();
            KeyBinding.unPressAllKeys();
            ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            guiScreenIn.setWorldAndResolution(Minecraft.getMinecraft(), i, j);
            this.skipRenderWorld = false;
        } else {
            this.soundHandler.resumeSounds();
            Minecraft.getMinecraft().setIngameFocus();
        }

        info.cancel();
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Timer;updateTimer()V", shift = At.Shift.BEFORE))
    public void runGameLoop$Inject$INVOKE$updateTimer(CallbackInfo ci) {
        Wrapper.getMinecraft().profiler.endStartSection("melonRunGameLoop");
        MinecraftForge.EVENT_BUS.post(new MotionUpdateEvent.StartTick());
        Wrapper.getMinecraft().profiler.endSection();
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V", ordinal = 0, shift = At.Shift.AFTER))
    public void runGameLoopTick(CallbackInfo ci) {
        Wrapper.getMinecraft().profiler.endStartSection("melonRunGameLoop");
        if (player != null && world != null && playerController != null) {
            MinecraftForge.EVENT_BUS.post(new MotionUpdateEvent.FastTick(player, playerController , world));
        }
        Wrapper.getMinecraft().profiler.endSection();
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 0, shift = At.Shift.BEFORE))
    public void runGameLoop$Inject$INVOKE$endStartSection(CallbackInfo ci) {
        Wrapper.getMinecraft().profiler.endStartSection("melonRunGameLoop");
        MinecraftForge.EVENT_BUS.post(new MotionUpdateEvent.RenderTick());
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isFramerateLimitBelowMax()Z", shift = At.Shift.BEFORE))
    public void runGameLoop$Inject$INVOKE$isFramerateLimitBelowMax(CallbackInfo ci) {
        Wrapper.getMinecraft().profiler.endStartSection("melonRunGameLoop");
        MinecraftForge.EVENT_BUS.post(new MotionUpdateEvent.EndTick());
        Wrapper.getMinecraft().profiler.endSection();
    }


    @Redirect(method = {"run"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"))
    public void displayCrashReport(Minecraft minecraft, CrashReport crashReport) {
        this.save();
    }

    @Inject(method = {"shutdown"}, at = @At(value = "HEAD"))
    public void shutdown(CallbackInfo info) {
        this.save();
    }

    public void save() {
        Kura.logger.warn("Saving Kura configuration please wait...");
        FileManager.saveAll();
        Kura.logger.warn("Configuration saved!");
    }

    @Redirect(method = {"sendClickBlockToController"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"))
    public boolean isHandActiveWrapper(EntityPlayerSP playerSP) {
        return (!ModuleManager.getModuleByName("MultiTask").isEnabled() && playerSP.isHandActive());
    }

    @Redirect(method = {"rightClickMouse"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;getIsHittingBlock()Z", ordinal = 0), require = 1)
    public boolean isHittingBlockHook(PlayerControllerMP playerControllerMP) {
        return (!ModuleManager.getModuleByName("MultiTask").isEnabled() && playerControllerMP.getIsHittingBlock());
    }
}

