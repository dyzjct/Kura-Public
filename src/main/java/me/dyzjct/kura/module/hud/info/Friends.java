package me.dyzjct.kura.module.hud.info;

import me.dyzjct.kura.manager.FriendManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.HUDModule;
import me.dyzjct.kura.utils.mc.ChatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@HUDModule.Info(name = "Friends", x = 170, y = 170, width = 25, height = 10, category = Category.HUD)
public class Friends extends HUDModule {

    String viewText = "";
    int DefaultWidth = 60;
    int DefaultHeight = 10;

    @Override
    public void onRender() {
        String[] mutliLineText = viewText.split("\n");
        int addY = 0;
        int maxFontWidth = DefaultWidth;
        for (String textt : mutliLineText) {
            fontRenderer.drawString(textt, x, y + addY, Color.WHITE.getRGB());
            maxFontWidth = Math.max(maxFontWidth, fontRenderer.getStringWidth(textt));
            addY += fontRenderer.FONT_HEIGHT;
        }
        if (addY == fontRenderer.FONT_HEIGHT) {
            this.height = fontRenderer.FONT_HEIGHT;
        } else {
            this.height = addY - fontRenderer.FONT_HEIGHT;
        }
        this.width = maxFontWidth;
    }

    @Override
    public void onUpdate() {
        viewText = "";
        if (getFriends().isEmpty()) {
            addLine("You have no friends!");
        } else {
            addLine(ChatUtil.SECTIONSIGN + "3" + ChatUtil.SECTIONSIGN + "l" + "Your Friends");
            Iterator var1 = getFriends().iterator();
            while (var1.hasNext()) {
                Entity e = (Entity) var1.next();
                addLine(ChatUtil.SECTIONSIGN + "6 " + e.getName());
            }
        }
    }

    private void addLine(String str) {
        if (viewText.isEmpty()) {
            viewText = str;
        } else {
            viewText = viewText + "\n" + str;
        }
    }


    public static List<EntityPlayer> getFriends() {
        return mc.world.playerEntities.stream().filter(entityPlayer -> FriendManager.isFriend(entityPlayer.getName())).collect(Collectors.toList());
    }
}
