package me.windyteam.kura.module.hud.info;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.windyteam.kura.friend.FriendManager;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.HUDModule;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;

@HUDModule.Info(name = "TextRadar", x = 170, y = 170, width = 75, height = 10, category = Category.HUD)
public class TextRadar extends HUDModule {

    DecimalFormat dfHealth;
    StringBuilder healthSB = new StringBuilder();
    String viewText = "";
    int DefaultWidth = 75;

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public void onInit() {
        dfHealth = new DecimalFormat("#.#");
        dfHealth.setRoundingMode(RoundingMode.HALF_UP);
    }

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

    private void addLine(String str) {
        if (viewText.isEmpty()) {
            viewText = str;
        } else {
            viewText = viewText + "\n" + str;
        }
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        viewText = "";
        List<EntityPlayer> entityList = mc.world.playerEntities;

        Map<String, Integer> players = new HashMap<>();
        for (EntityPlayer e : entityList) {
            if (e.getName().equals(mc.player.getName())) {
                continue;
            }
            String posString = (e.posY > mc.player.posY ? ChatFormatting.DARK_GREEN + "+ " : (e.posY == mc.player.posY ? " " : ChatFormatting.DARK_RED + "- "));

            String strengthfactor = "";
            if (e.isPotionActive(MobEffects.STRENGTH)) {
                strengthfactor = "S";
            }
            float hpRaw = e.getHealth() + ((EntityLivingBase) e).getAbsorptionAmount();
            String hp = dfHealth.format(hpRaw);
            healthSB.append(ChatUtil.SECTIONSIGN);
            if (hpRaw >= 20) {
                healthSB.append("a");
            } else if (hpRaw >= 10) {
                healthSB.append("e");
            } else if (hpRaw >= 5) {
                healthSB.append("6");
            } else {
                healthSB.append("c");
            }
            healthSB.append(hp);
            players.put(ChatFormatting.AQUA + posString + "Player " + healthSB + " " + ChatFormatting.RED + strengthfactor + (strengthfactor.equals("S") ? " " : "") + (FriendManager.isFriend(e.getName()) ? ChatFormatting.GREEN : ChatFormatting.DARK_BLUE) + e.getName(), (int) mc.player.getDistance(e));
            healthSB.setLength(0);
        }

        if (players.isEmpty()) {
            viewText = "";
            return;
        }

        players = sortByValue(players);

        for (Map.Entry<String, Integer> player : players.entrySet()) {
            addLine(ChatUtil.SECTIONSIGN + "7" + player.getKey() + " " + ChatUtil.SECTIONSIGN + "4" + player.getValue());
        }
    }
}
