package me.windyteam.kura.utils;

import me.windyteam.kura.gui.clickgui.font.FontRenderer;
import me.windyteam.kura.gui.clickgui.font.FontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public class Wrapper {
    private static FontRenderer fontRenderer;
    public static Minecraft mc = Minecraft.getMinecraft();

    public static EntityPlayerSP getPlayer() {
        return Wrapper.getMinecraft().player;
    }

    public static Minecraft getMinecraft() {
        return mc;
    }

    public static World getWorld() {
        return Wrapper.getMinecraft().world;
    }

    public static int getKey(String keyname) {
        return Keyboard.getKeyIndex((String)keyname.toUpperCase());
    }

    public static FontRenderer getFontRenderer() {
        return fontRenderer;
    }
}

