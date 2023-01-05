package me.dyzjct.kura.module.modules.render;

import me.dyzjct.kura.event.events.render.Render3DEvent;
import me.dyzjct.kura.friend.FriendManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.FloatSetting;
import me.dyzjct.kura.setting.IntegerSetting;
import me.dyzjct.kura.utils.NTMiku.RenderUtil;
import me.dyzjct.kura.utils.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Module.Info(name = "BurrowESP",category = Category.RENDER)
public class BurrowESP
        extends Module {

    private final BooleanSetting name = bsetting("Name",false);
    private final BooleanSetting box = bsetting("Box", true);
    private final IntegerSetting boxRed = isetting("BoxRed", 255, 0, 255);
    private final IntegerSetting boxGreen = isetting("BoxGreen", 255, 0, 255);
    private final IntegerSetting boxBlue = isetting("BoxBlue", 255, 0, 255);
    private final IntegerSetting boxAlpha = isetting("BoxAlpha", 125, 0, 255);
    private final BooleanSetting outline = bsetting("Outline", true);
    private final FloatSetting outlineWidth = fsetting("OutlineWidth", 1.0f, 0.0f, 5.0f).b(outline);
    private final BooleanSetting cOutline = bsetting("CustomOutline", false).b(outline);
    private final IntegerSetting outlineRed = isetting("OutlineRed", 255, 0, 255).b(cOutline);
    private final IntegerSetting outlineGreen = isetting("OutlineGreen", 255, 0, 255).b(cOutline);
    private final IntegerSetting outlineBlue = isetting("OutlineBlue", 255, 0, 255).b(cOutline);
    private final IntegerSetting outlineAlpha = isetting("OutlineAlpha", 255, 0, 255).b(cOutline);
    private final HashMap burrowedPlayers = new HashMap<EntityPlayer, BlockPos>();

    private void getPlayers() {
        for (EntityPlayer entityPlayer : BurrowESP.mc.world.playerEntities) {
            if (entityPlayer == BurrowESP.mc.player || FriendManager.isFriend(entityPlayer.getName()) || !EntityUtil.isLiving((Entity)entityPlayer) || !this.isBurrowed(entityPlayer)) continue;
            this.burrowedPlayers.put(entityPlayer, new BlockPos(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ));
        }
    }

    @Override
    public void onEnable() {
        this.burrowedPlayers.clear();
    }

    private void lambda$onRender3D$8(Map.Entry entry) {
        this.renderBurrowedBlock((BlockPos)entry.getValue());
        if (name.getValue().booleanValue()) {
            RenderUtil.drawText((BlockPos)entry.getValue(), ((EntityPlayer)entry.getKey()).getGameProfile().getName());
        }
    }

    private boolean isBurrowed(EntityPlayer entityPlayer) {
        BlockPos blockPos = new BlockPos(Math.floor(entityPlayer.posX), Math.floor(entityPlayer.posY + 0.2), Math.floor(entityPlayer.posZ));
        return BurrowESP.mc.world.getBlockState(blockPos).getBlock() == Blocks.ENDER_CHEST || BurrowESP.mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN || BurrowESP.mc.world.getBlockState(blockPos).getBlock() == Blocks.CHEST;
    }

    @Override
    public void onUpdate() {
        if (BurrowESP.fullNullCheck()) {
            return;
        }
        this.burrowedPlayers.clear();
        this.getPlayers();
    }

    private void renderBurrowedBlock(BlockPos blockPos) {
        RenderUtil.drawBoxESP(blockPos, new Color(this.boxRed.getValue(), this.boxGreen.getValue(), this.boxBlue.getValue(), this.boxAlpha.getValue()), true, new Color(this.outlineRed.getValue(), this.outlineGreen.getValue(), this.outlineBlue.getValue(), this.outlineAlpha.getValue()), this.outlineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true);
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent render3DEvent) {
        if (!this.burrowedPlayers.isEmpty()) {
            this.burrowedPlayers.entrySet().forEach(entry -> lambda$onRender3D$8((Map.Entry) entry));
        }
    }
}

