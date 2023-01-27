package me.windyteam.kura.module.modules.player

import me.windyteam.kura.event.events.player.PacketEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.Setting
import net.minecraft.block.BlockShulkerBox
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "AntiChest", category = Category.PLAYER)
class AntiContainer : Module() {
    private var chest: Setting<Boolean> = bsetting("Chest", true)
    private var enderChest: Setting<Boolean> = bsetting("EnderChest", true)
    private var trappedChest: Setting<Boolean> = bsetting("Trapped_Chest", true)
    private var hopper: Setting<Boolean> = bsetting("Hopper", true)
    private var dispenser: Setting<Boolean> = bsetting("Dispenser", true)
    private var furnace: Setting<Boolean> = bsetting("Furnace", true)
    private var beacon: Setting<Boolean> = bsetting("Beacon", true)
    private var craftingTable: Setting<Boolean> = bsetting("Crafting_Table", true)
    private var anvil: Setting<Boolean> = bsetting("Anvil", true)
    private var enchantingTable: Setting<Boolean> = bsetting("Enchanting_table", true)
    private var brewingStand: Setting<Boolean> = bsetting("Brewing_Stand", true)
    private var shulkerBox: Setting<Boolean> = bsetting("ShulkerBox", true)
    @SubscribeEvent
    fun onCheck(packet: PacketEvent.Send) {
        var pos: BlockPos?
        if (packet.getPacket<Packet<*>>() is CPacketPlayerTryUseItemOnBlock && this.check((packet.getPacket<Packet<*>>() as CPacketPlayerTryUseItemOnBlock).pos.also { pos = it })) {
            packet.isCanceled = true
        }
    }

    fun check(pos: BlockPos?): Boolean {
        return Minecraft.getMinecraft().world.getBlockState(pos!!).block === Blocks.CHEST && chest.value != false || Minecraft.getMinecraft().world.getBlockState(pos).block === Blocks.ENDER_CHEST && enderChest.value != false || Minecraft.getMinecraft().world.getBlockState(pos).block === Blocks.TRAPPED_CHEST && trappedChest.value != false || Minecraft.getMinecraft().world.getBlockState(pos).block === Blocks.HOPPER && hopper.value != false || Minecraft.getMinecraft().world.getBlockState(pos).block === Blocks.DISPENSER && dispenser.value != false || Minecraft.getMinecraft().world.getBlockState(pos).block === Blocks.FURNACE && furnace.value != false || Minecraft.getMinecraft().world.getBlockState(pos).block === Blocks.BEACON && beacon.value != false || Minecraft.getMinecraft().world.getBlockState(pos).block === Blocks.CRAFTING_TABLE && craftingTable.value != false || Minecraft.getMinecraft().world.getBlockState(pos).block === Blocks.ANVIL && anvil.value != false || Minecraft.getMinecraft().world.getBlockState(pos).block === Blocks.ENCHANTING_TABLE && enchantingTable.value != false || Minecraft.getMinecraft().world.getBlockState(pos).block === Blocks.BREWING_STAND && brewingStand.value != false || Minecraft.getMinecraft().world.getBlockState(pos).block is BlockShulkerBox && shulkerBox.value != false
    }
}