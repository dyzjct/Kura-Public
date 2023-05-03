package me.windyteam.kura.module.modules.render

import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.event.events.render.*
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.Setting
import net.minecraft.block.BlockSnow
import net.minecraft.init.Blocks
import net.minecraft.network.Packet
import net.minecraft.network.play.server.*
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityEnchantmentTable
import net.minecraft.tileentity.TileEntityEnderChest
import net.minecraftforge.client.event.RenderBlockOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "NoRender", category = Category.RENDER, description = "Ignore entity spawn packets")
object NoRender : Module() {

    @JvmField
    var skylight = msetting("SkyLight", Skylight.NONE)
    val BlockLayer: Setting<Boolean> = bsetting("BlockLayer", true)
    val mob: Setting<Boolean> = bsetting("Mob", false)
    val sand: Setting<Boolean> = bsetting("Sand", false)
    val gentity: Setting<Boolean> = bsetting("GEntity", false)
    val `object`: Setting<Boolean> = bsetting("Object", false)
    val xp: Setting<Boolean> = bsetting("XP", false)
    val paint: Setting<Boolean> = bsetting("Paintings", false)
    val fire: Setting<Boolean> = bsetting("Fire", true)
    val explosion: Setting<Boolean> = bsetting("Explosions", true)
    var skylightupdate = bsetting("SkylightUpdate", true)
    var totemPops: Setting<Boolean> = bsetting("Totem", false)
    var table = bsetting("EnchantmentTable", false)
    var enderChest = bsetting("EnderChest", false)
    var banner = bsetting("Banner", false)
    val armor: Setting<Boolean> = bsetting("Armor",true)
    @SubscribeEvent
    fun oao(event: RenderBlockOverlayEvent) {
        if (fire.value && event.overlayType == RenderBlockOverlayEvent.OverlayType.FIRE) event.isCanceled = true
    }

    @SubscribeEvent
    fun onRenderOverlay(event: RenderOverlayEvent) {
        event.isCanceled = true
    }

    @SubscribeEvent
    fun RenderLight(event: RenderLightEvent) {
        if (skylightupdate.value) {
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun totemPop(event: RenderTotemPopEvent) {
        if (totemPops.value) {
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun banner(event: RenderBannerEvent) {
        if (banner.value) {
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun enderChest(event: RenderEnderChestEvent) {
        if (enderChest.value) {
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun enchantmentTable(event: RenderEnchantmentTableEvent) {
        if (table.value) {
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun BlockLayer(event: RenderLiquidVisionEvent) {
        if (BlockLayer.value) {
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun awa(event: PacketEvents.Receive) {
        val packet = event.getPacket<Packet<*>>()
        if (packet is SPacketSpawnMob && mob.value || packet is SPacketSpawnGlobalEntity && gentity.value || packet is SPacketSpawnObject && `object`.value || packet is SPacketSpawnExperienceOrb && xp.value || packet is SPacketSpawnObject && sand.value || packet is SPacketExplosion && explosion.value || packet is SPacketSpawnPainting && paint.value) event.isCanceled =
            true
    }

    fun tryReplaceEnchantingTable(tileEntity: TileEntity): Boolean {
        if (table.value && tileEntity is TileEntityEnchantmentTable) {
            val blockState = Blocks.SNOW_LAYER.defaultBlockState.withProperty(BlockSnow.LAYERS, 7)
            mc.world.setBlockState(tileEntity.getPos(), blockState)
            mc.world.markTileEntityForRemoval(tileEntity)
            return true
        }
        return false
    }

    fun tryReplaceEnderChest(tileEntity: TileEntity): Boolean {
        if (enderChest.value && tileEntity is TileEntityEnderChest) {
            val blockState = Blocks.SNOW_LAYER.defaultBlockState.withProperty(BlockSnow.LAYERS, 7)
            mc.world.setBlockState(tileEntity.getPos(), blockState)
            mc.world.markTileEntityForRemoval(tileEntity)
            return true
        }
        return false
    }

    enum class Skylight {
        NONE,
        WORLD,
        ENTITY,
        ALL
    }

}
