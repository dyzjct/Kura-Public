package me.windyteam.kura.module.modules.render;

import me.windyteam.kura.event.events.render.RenderEvent;
import me.windyteam.kura.friend.FriendManager;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.setting.IntegerSetting;
import me.windyteam.kura.setting.ModeSetting;
import me.windyteam.kura.utils.color.GSColor;
import me.windyteam.kura.utils.entity.CrystalUtil;
import me.windyteam.kura.utils.font.FontUtil;
import me.windyteam.kura.utils.render.RenderUtils3D;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@Module.Info(name = "Nametags", description = "Draws descriptive nametags above entities", category = Category.RENDER)
public class Nametags extends Module {

    public static Nametags INSTANCE = new Nametags();
    public ModeSetting<Mode> nametags = msetting("Mode", Mode.Normal);
    public BooleanSetting renderSelf = bsetting("RenderSelf", false);
    public BooleanSetting showDurability = bsetting("Durability", true);
    public BooleanSetting showItems = bsetting("Items", true);
    public BooleanSetting showEnchantName = bsetting("Enchants", true);
    public BooleanSetting showItemName = bsetting("ItemName", false);
    public BooleanSetting showGameMode = bsetting("Gamemode", false);
    public BooleanSetting showHealth = bsetting("Health", true);
    public BooleanSetting showPing = bsetting("Ping", true);
    public BooleanSetting showIQ = bsetting("IQ", false);
    public BooleanSetting showEntityID = bsetting("EntityID", false);
    public IntegerSetting range = isetting("Range", 100, 10, 260);
    public IntegerSetting red = isetting("Red", 255, 0, 255);
    public IntegerSetting green = isetting("Green", 197, 0, 255);
    public IntegerSetting blue = isetting("Blue", 237, 0, 255);
    public IntegerSetting alpha = isetting("Alpha", 80, 0, 255);
    public IntegerSetting width = isetting("Width", 3, 1, 10).m(nametags, Mode.ESP);

    @Override
    public void onWorldRender(RenderEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        try {
            mc.world.playerEntities.stream().filter(this::shouldRender).forEach(entityPlayer -> {
                Vec3d vec3d = findEntityVec3d(entityPlayer);
                renderNameTags(entityPlayer, vec3d.x, vec3d.y, vec3d.z);
            });
        } catch (Exception ignored) {
        }
    }

    public void renderNameTags(EntityPlayer entityPlayer, double posX, double posY, double posZ) {
        double adjustedY = posY + (entityPlayer.isSneaking() ? 1.9 : 2.2);
        int color = new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue()).getRGB();
        double dist = mc.player.getDistance(posX, posY, posZ);
        double scale;
        scale = 0.0018 + 0.003 * dist;
        if (dist <= 8.0) scale = 0.0245;
        switch (nametags.getValue()) {
            case Normal: {
                RenderUtils3D.drawNametag(posX, adjustedY, posZ, buildEntityNameString(entityPlayer), findTextColor(entityPlayer));
                renderItemsAndArmor(entityPlayer, 0, -2);
                GlStateManager.popMatrix();
                break;
            }
            case ESP: {
                CrystalUtil.targetHUD(entityPlayer, red.getValue(), green.getValue(), blue.getValue(), alpha.getValue(), width.getValue());
                GlStateManager.translate(posX - mc.getRenderManager().viewerPosX, adjustedY - mc.getRenderManager().viewerPosY, posZ - mc.getRenderManager().viewerPosZ);
                GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1, 0);
                GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1 : 1, 0, 0);
                GlStateManager.scale(-scale, -scale, scale);
                mc.fontRenderer.drawString(entityPlayer.getName(), (int) (-mc.fontRenderer.getStringWidth(entityPlayer.getName()) / 2f), -mc.fontRenderer.FONT_HEIGHT, color);
                mc.fontRenderer.drawString(String.valueOf(entityPlayer.getHealth()), (int) (-mc.fontRenderer.getStringWidth(entityPlayer.getName()) + 5f), -mc.fontRenderer.FONT_HEIGHT, entityPlayer.getHealth() <= 16 ? new Color(255, 0, 0, 100).getRGB() : new Color(0, 255, 0, 100).getRGB());
                break;
            }
        }
    }


    public boolean shouldRender(EntityPlayer entityPlayer) {
        if (entityPlayer == mc.player && !renderSelf.getValue()) return false;

        if (entityPlayer.isDead || entityPlayer.getHealth() <= 0) return false;

        return !(entityPlayer.getDistance(mc.player) > range.getValue());
    }

    public Vec3d findEntityVec3d(EntityPlayer entityPlayer) {
        double posX = balancePosition(entityPlayer.posX, entityPlayer.lastTickPosX);
        double posY = balancePosition(entityPlayer.posY, entityPlayer.lastTickPosY);
        double posZ = balancePosition(entityPlayer.posZ, entityPlayer.lastTickPosZ);

        return new Vec3d(posX, posY, posZ);
    }

    public double balancePosition(double newPosition, double oldPosition) {
        return oldPosition + (newPosition - oldPosition) * mc.timer.renderPartialTicks;
    }

    public String buildEntityNameString(EntityPlayer entityPlayer) {
        String name = entityPlayer.getName();
        if (showEntityID.getValue()) {
            name = name + " ID: " + entityPlayer.getEntityId();
        }

        if (showGameMode.getValue()) {
            if (entityPlayer.isCreative()) {
                name = name + " [C]";
            } else if (entityPlayer.isSpectator()) {
                name = name + " [I]";
            } else {
                name = name + " [S]";
            }
        }
        if (showPing.getValue()) {
            int value = 0;

            if (mc.getConnection() != null && mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()) != null) {
                value = mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()).getResponseTime();
            }

            name = name + " " + value + "ms";
        }

        if (showHealth.getValue()) {
            int health = (int) (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount());
            TextFormatting textFormatting = findHealthColor(health);

            name = name + " " + textFormatting + health;
        }

        if (showIQ.getValue()) {
            int health = (int) (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount());
            TextFormatting textFormatting = findHealthColor(health);

            name = name + " " + textFormatting + health + "IQ";
        }

        return name;
    }

    public TextFormatting findHealthColor(int health) {
        if (health <= 0) {
            return TextFormatting.DARK_RED;
        } else if (health <= 5) {
            return TextFormatting.RED;
        } else if (health <= 10) {
            return TextFormatting.GOLD;
        } else if (health <= 15) {
            return TextFormatting.YELLOW;
        } else if (health <= 20) {
            return TextFormatting.DARK_GREEN;
        }

        return TextFormatting.GREEN;
    }

    public GSColor findTextColor(EntityPlayer entityPlayer) {
        if (FriendManager.isFriend(entityPlayer.getName())) {
            return new GSColor(0, 255, 255);
        } else if (entityPlayer.isInvisible()) {
            return new GSColor(128, 128, 128);
        } else if (mc.getConnection() != null && mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()) == null) {
            return new GSColor(239, 1, 71);
        } else if (entityPlayer.isSneaking()) {
            return new GSColor(255, 153, 0);
        }
        return new GSColor(255, 255, 255);
    }

    public void renderItemsAndArmor(EntityPlayer entityPlayer, int posX, int posY) {
        ItemStack mainHandItem = entityPlayer.getHeldItemMainhand();
        ItemStack offHandItem = entityPlayer.getHeldItemOffhand();

        int armorCount = 3;
        for (int i = 0; i <= 3; i++) {
            ItemStack itemStack = entityPlayer.inventory.armorInventory.get(armorCount);
            if (!itemStack.isEmpty()) {
                posX -= 8;
                int size = EnchantmentHelper.getEnchantments(itemStack).size();
                if (showItems.getValue() && size > posY) {
                    posY = size;
                }
            }
            armorCount--;
        }

        if (!mainHandItem.isEmpty() && (showItems.getValue() || showDurability.getValue() && offHandItem.isItemStackDamageable())) {
            posX -= 8;
            int enchantSize = EnchantmentHelper.getEnchantments(offHandItem).size();
            if (showItems.getValue() && enchantSize > posY) {
                posY = enchantSize;
            }
        }

        if (!mainHandItem.isEmpty()) {
            int enchantSize = EnchantmentHelper.getEnchantments(mainHandItem).size();
            if (showItems.getValue() && enchantSize > posY) {
                posY = enchantSize;
            }
            int armorY = findArmorY(posY);
            if (showItems.getValue() || (showDurability.getValue() && mainHandItem.isItemStackDamageable())) {
                posX -= 8;
            }
            if (showItems.getValue()) {
                renderItem(mainHandItem, posX, armorY, posY);
                armorY -= 32;
            }
            if (showDurability.getValue() && mainHandItem.isItemStackDamageable()) {
                renderItemDurability(mainHandItem, posX, armorY);
            }
            armorY -= (mc.fontRenderer.FONT_HEIGHT);
            if (showItemName.getValue()) {
                renderItemName(mainHandItem, armorY);
            }
            if (showItems.getValue() || (showDurability.getValue() && mainHandItem.isItemStackDamageable())) {
                posX += 16;
            }
        }
        int armorCount2 = 3;
        for (int i = 0; i <= 3; i++) {
            ItemStack itemStack = entityPlayer.inventory.armorInventory.get(armorCount2);
            if (!itemStack.isEmpty()) {
                int armorY = findArmorY(posY);
                if (showItems.getValue()) {
                    renderItem(itemStack, posX, armorY, posY);
                    armorY -= 32;
                }
                if (showDurability.getValue() && itemStack.isItemStackDamageable()) {
                    renderItemDurability(itemStack, posX, armorY);
                }
                posX += 16;
            }
            armorCount2--;
        }

        if (!offHandItem.isEmpty()) {
            int armorY = findArmorY(posY);
            if (showItems.getValue()) {
                renderItem(offHandItem, posX, armorY, posY);
                armorY -= 32;
            }
            if (showDurability.getValue() && offHandItem.isItemStackDamageable()) {
                renderItemDurability(offHandItem, posX, armorY);
            }
        }
    }

    public int findArmorY(int posY) {
        int posY2 = showItems.getValue() ? -26 : -27;
        if (posY > 4) {
            posY2 -= (posY - 4) * 8;
        }
        return posY2;
    }

    public void renderItemName(ItemStack itemStack, int posY) {
        GlStateManager.enableTexture2D();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        FontUtil.drawString(itemStack.getDisplayName(), -FontUtil.getStringWidth(itemStack.getDisplayName()) / 2, posY, new GSColor(255, 255, 255));
        GlStateManager.popMatrix();
        GlStateManager.disableTexture2D();
    }

    public void renderItemDurability(ItemStack itemStack, int posX, int posY) {
        float damagePercent = (itemStack.getMaxDamage() - itemStack.getItemDamage()) / (float) itemStack.getMaxDamage();

        float green = damagePercent;
        if (green > 1) green = 1;
        else if (green < 0) green = 0;

        float red = 1 - green;

        GlStateManager.enableTexture2D();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        FontUtil.drawString((int) (damagePercent * 100) + "%", posX * 2, posY, new GSColor((int) (red * 255), (int) (green * 255), 0));
        GlStateManager.popMatrix();
        GlStateManager.disableTexture2D();
    }

    public void renderItem(ItemStack itemStack, int posX, int posY, int posY2) {
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();

        final int posY3 = (posY2 > 4) ? ((posY2 - 4) * 8 / 2) : 0;

        mc.getRenderItem().zLevel = -150.0f;
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, posX, posY + posY3);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, itemStack, posX, posY + posY3);
        RenderHelper.disableStandardItemLighting();
        mc.getRenderItem().zLevel = 0.0f;
        RenderUtils3D.prepare();
        GlStateManager.pushMatrix();
        GlStateManager.scale(.5, .5, .5);
        renderEnchants(itemStack, posX, posY - 24);
        GlStateManager.popMatrix();
    }

    public void renderEnchants(ItemStack itemStack, int posX, int posY) {
        GlStateManager.enableTexture2D();

        for (Enchantment enchantment : EnchantmentHelper.getEnchantments(itemStack).keySet()) {
            if (enchantment == null) {
                continue;
            }

            if (showEnchantName.getValue()) {
                int level = EnchantmentHelper.getEnchantmentLevel(enchantment, itemStack);
                FontUtil.drawString(findStringForEnchants(enchantment, level), posX * 2, posY, new GSColor(255, 255, 255));
            }
            posY += 8;
        }

        if (itemStack.getItem().equals(Items.GOLDEN_APPLE) && itemStack.hasEffect()) {
            FontUtil.drawString("God", posX * 2, posY, new GSColor(195, 77, 65));
        }

        GlStateManager.disableTexture2D();
    }

    public String findStringForEnchants(Enchantment enchantment, int level) {
        ResourceLocation resourceLocation = Enchantment.REGISTRY.getNameForObject(enchantment);

        String string = resourceLocation == null ? enchantment.getName() : resourceLocation.toString();

        int charCount = (level > 1) ? 12 : 13;

        if (string.length() > charCount) {
            string = string.substring(10, charCount);
        }

        return string.substring(0, 1).toUpperCase() + string.substring(1) + TextFormatting.RED + ((level > 1) ? level : "");
    }

    public enum Mode {
        Normal,
        ESP
    }
}
