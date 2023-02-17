package me.windyteam.kura.module;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.windyteam.kura.event.events.client.SettingChangeEvent;
import me.windyteam.kura.event.events.render.RenderEvent;
import me.windyteam.kura.gui.Notification;
import me.windyteam.kura.module.modules.client.Colors;
import me.windyteam.kura.module.modules.client.CustomFont;
import me.windyteam.kura.setting.*;
import me.windyteam.kura.utils.Wrapper;
import me.windyteam.kura.utils.font.CFontRenderer;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

import java.awt.*;
import java.util.ArrayList;

public abstract class IModule {
    public static final Minecraft mc = Wrapper.mc;
    public static final FontRenderer fontRenderer = IModule.mc.fontRenderer;
    public static FontRenderer font = fontRenderer;
    private final ArrayList<Setting> settings = new ArrayList<>();
    public float remainingAnimation = 0.0f;
    public String name;
    public boolean toggled;
    public String description;
    public Category category;
    public int keyCode;
    public boolean isHUD;
    public int x;
    public int y;
    public int width;
    public int height;

    public ArrayList<Setting> getSettingList() {
        return this.settings;
    }

    public String getName() {
        return this.name;
    }

    public void enable() {
        remainingAnimation = 0.0f;
        this.toggled = true;
        if (ModuleManager.getModuleByName("Notification").isEnabled()) {
            ChatUtil.sendClientMessage(ChatFormatting.AQUA + "[" + name + "] is Enable", Notification.Type.SUCCESS);
        }
        if (Colors.INSTANCE.chat.getValue()) {
            ChatUtil.NoSpam.sendMessage(ChatFormatting.AQUA + name + ChatFormatting.WHITE + " is" + ChatFormatting.GREEN + " Enabled!");
        }
        this.onEnable();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void disable() {
        remainingAnimation = 0.0f;
        this.toggled = false;
        if (ModuleManager.getModuleByName("Notification").isEnabled()) {
            ChatUtil.sendClientMessage(ChatFormatting.RED + "[" + name + "] is Disable", Notification.Type.DISABLE);
        }
        if (Colors.INSTANCE.chat.getValue()) {
            ChatUtil.NoSpam.sendMessage(ChatFormatting.AQUA + name + ChatFormatting.WHITE + " is" + ChatFormatting.RED + " Disabled!");
        }
        this.onDisable();
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public BindSetting bindsetting(String name, int keyboard) {
        BindSetting value = new BindSetting(name, this, keyboard);
        this.getSettingList().add(value);
        return value;
    }

    public BooleanSetting bsetting(String name, boolean defaultValue) {
        BooleanSetting value = new BooleanSetting(name, this, defaultValue);
        this.getSettingList().add(value);
        return value;
    }


    public IntegerSetting isetting(String name, int defaultValue, int minValue, int maxValue) {
        IntegerSetting value = new IntegerSetting(name, this, defaultValue, minValue, maxValue);
        this.getSettingList().add(value);
        return value;
    }

    public FloatSetting fsetting(String name, float defaultValue, float minValue, float maxValue) {
        FloatSetting value = new FloatSetting(name, this, Float.valueOf(defaultValue), Float.valueOf(minValue), Float.valueOf(maxValue));
        this.getSettingList().add(value);
        return value;
    }

    public DoubleSetting dsetting(String name, double defaultValue, double minValue, double maxValue) {
        DoubleSetting value = new DoubleSetting(name, this, defaultValue, minValue, maxValue);
        this.getSettingList().add(value);
        return value;
    }

//    public PositionSetting psetting(final float x, final float y){
//        PositionSetting value = new PositionSetting(this.x,this.y);
//        this.getSettingList().add(value);
//        return value;
//    }

    public ModeSetting msetting(String name, Enum modes) {
        ModeSetting value = new ModeSetting(name, this, modes);
        this.getSettingList().add(value);
        return value;
    }

    public StringSetting ssetting(String name, String defaultValue) {
        StringSetting value = new StringSetting(name, this, defaultValue);
        this.getSettingList().add(value);
        return value;
    }

    public ColorSetting csetting(String name, Color defaultValue) {
        ColorSetting value = new ColorSetting(name, this, defaultValue);
        this.getSettingList().add(value);
        return value;
    }

//

    public BindSetting settings(String name, int keyboard) {
        BindSetting value = new BindSetting(name, this, keyboard);
        this.getSettingList().add(value);
        return value;
    }

    public IntegerSetting settings(String name, int defaultValue, int minValue, int maxValue) {
        IntegerSetting value = new IntegerSetting(name, this, defaultValue, minValue, maxValue);
        this.getSettingList().add(value);
        return value;
    }

    public FloatSetting settings(String name, float defaultValue, float minValue, float maxValue) {
        FloatSetting value = new FloatSetting(name, this, Float.valueOf(defaultValue), Float.valueOf(minValue), Float.valueOf(maxValue));
        this.getSettingList().add(value);
        return value;
    }

    public DoubleSetting settings(String name, double defaultValue, double minValue, double maxValue) {
        DoubleSetting value = new DoubleSetting(name, this, defaultValue, minValue, maxValue);
        this.getSettingList().add(value);
        return value;
    }

    public ModeSetting settings(String name, Enum modes) {
        ModeSetting value = new ModeSetting(name, this, modes);
        this.getSettingList().add(value);
        return value;
    }

    public ColorSetting settings(String name, Color defaultValue) {
        ColorSetting value = new ColorSetting(name, this, defaultValue);
        this.getSettingList().add(value);
        return value;
    }

    public StringSetting settings(String name, String defaultValue) {
        StringSetting value = new StringSetting(name, this, defaultValue);
        this.getSettingList().add(value);
        return value;
    }

    public BooleanSetting settings(String name, boolean defaultValue) {
        BooleanSetting value = new BooleanSetting(name, this, defaultValue);
        this.getSettingList().add(value);
        return value;
    }

    public boolean isEnabled() {
        return this.toggled;
    }

    public boolean isDisabled() {
        return !this.toggled;
    }

    public void onConfigLoad() {
    }

    public void onConfigSave() {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onUpdate() {
    }

    public void onLogout() {
    }

    public void onLogin() {
    }

    public void onRender() {
    }

    public void onRender2D(RenderGameOverlayEvent.Post event) {
    }

    public void onWorldRender(RenderEvent event) {
    }

    public String getHudInfo() {
        return null;
    }

    public void toggle() {
        SettingChangeEvent event = new SettingChangeEvent(!this.isEnabled() ? 1 : 0, this);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return;
        }
        this.toggled = !this.toggled;
        if (this.toggled) {
            this.enable();
        } else {
            this.disable();
        }
    }

    public void onKey(InputUpdateEvent event) {
    }

    public int getBind() {
        return this.keyCode;
    }

    public void setBind(int keycode) {
        this.keyCode = keycode;
    }

    public void setEnable(boolean toggled) {
        this.toggled = toggled;
    }

    public CFontRenderer getFont() {
        return CustomFont.getHUDFont();
    }
}

