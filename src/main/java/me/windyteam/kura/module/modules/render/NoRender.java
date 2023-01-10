package me.windyteam.kura.module.modules.render;

import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.event.events.render.*;
import me.windyteam.kura.event.events.render.*;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.setting.Setting;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by 086 on 4/02/2018.
 * Updated by S-B99 on 14/12/19
 */
@Module.Info(name = "NoRender", category = Category.RENDER, description = "Ignore entity spawn packets")
public class NoRender extends Module {
    public static NoRender INSTANCE = new NoRender();
    public Setting<Skylight> skylight = msetting("SkyLight", Skylight.ALL);
    public final Setting<Boolean> BlockLayer = bsetting("BlockLayer", true);
    public final Setting<Boolean> mob = bsetting("Mob", false);
    public final Setting<Boolean> sand = bsetting("Sand", false);
    public final Setting<Boolean> gentity = bsetting("GEntity", false);
    public final Setting<Boolean> object = bsetting("Object", false);
    public final Setting<Boolean> xp = bsetting("XP", false);
    public final Setting<Boolean> paint = bsetting("Paintings", false);
    public final Setting<Boolean> fire = bsetting("Fire", true);
    public final Setting<Boolean> explosion = bsetting("Explosions", true);
    public BooleanSetting skylightupdate = bsetting("SkylightUpdate", true);
    public Setting<Boolean> totemPops = bsetting("Totem", false);
    public BooleanSetting table = bsetting("EnchantmentTable", false);
    public BooleanSetting enderChest = bsetting("EnderChest", false);
    public BooleanSetting banner = bsetting("Banner", false);

    public static NoRender getInstance() {
        if (INSTANCE == null)
            INSTANCE = new NoRender();
        return INSTANCE;
    }

    @SubscribeEvent
    public void oao(RenderBlockOverlayEvent event) {
        if (fire.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderOverlayEvent event) {
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void RenderLight(RenderLightEvent event) {
        if (skylightupdate.getValue()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void totemPop(RenderTotemPopEvent event) {
        if (totemPops.getValue()) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent
    public void banner(RenderBannerEvent event) {
        if (banner.getValue()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void enderChest(RenderEnderChestEvent event) {
        if (enderChest.getValue()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void enchantmentTable(RenderEnchantmentTableEvent event) {
        if (table.getValue()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void BlockLayer(RenderLiquidVisionEvent event) {
        if (BlockLayer.getValue()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void awa(PacketEvents.Receive event) {
        Packet<?> packet = event.getPacket();
        if ((packet instanceof SPacketSpawnMob && mob.getValue()) ||
                (packet instanceof SPacketSpawnGlobalEntity && gentity.getValue()) ||
                (packet instanceof SPacketSpawnObject && object.getValue()) ||
                (packet instanceof SPacketSpawnExperienceOrb && xp.getValue()) ||
                (packet instanceof SPacketSpawnObject && sand.getValue()) ||
                (packet instanceof SPacketExplosion && explosion.getValue()) ||
                (packet instanceof SPacketSpawnPainting && paint.getValue()))
            event.setCanceled(true);
    }

    public boolean tryReplaceEnchantingTable(TileEntity tileEntity) {
        if (table.getValue() && tileEntity instanceof TileEntityEnchantmentTable) {
            IBlockState blockState = Blocks.SNOW_LAYER.defaultBlockState.withProperty(BlockSnow.LAYERS, 7);
            mc.world.setBlockState(tileEntity.getPos(), blockState);
            mc.world.markTileEntityForRemoval(tileEntity);
            return true;
        }
        return false;
    }

    public boolean tryReplaceEnderChest(TileEntity tileEntity) {
        if (enderChest.getValue() && tileEntity instanceof TileEntityEnderChest) {
            IBlockState blockState = Blocks.SNOW_LAYER.defaultBlockState.withProperty(BlockSnow.LAYERS, 7);
            mc.world.setBlockState(tileEntity.getPos(), blockState);
            mc.world.markTileEntityForRemoval(tileEntity);
            return true;
        }
        return false;
    }

    public void setInstance() {
        INSTANCE = this;
    }

    public enum Skylight {
        NONE, WORLD, ENTITY, ALL
    }

}
