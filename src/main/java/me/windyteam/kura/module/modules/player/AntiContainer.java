/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.minecraft.block.BlockShulkerBox
 *  net.minecraft.client.Minecraft
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.windyteam.kura.module.modules.player;

import me.windyteam.kura.event.events.player.PacketEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "AntiChest", category = Category.PLAYER)
public class AntiContainer
        extends Module {
    public Setting<Boolean> Chest = bsetting("Chest", true);
    public Setting<Boolean> EnderChest = bsetting("EnderChest", true);
    public Setting<Boolean> Trapped_Chest = bsetting("Trapped_Chest", true);
    public Setting<Boolean> Hopper = bsetting("Hopper", true);
    public Setting<Boolean> Dispenser = bsetting("Dispenser", true);
    public Setting<Boolean> Furnace = bsetting("Furnace", true);
    public Setting<Boolean> Beacon = bsetting("Beacon", true);
    public Setting<Boolean> Crafting_Table = bsetting("Crafting_Table", true);
    public Setting<Boolean> Anvil = bsetting("Anvil", true);
    public Setting<Boolean> Enchanting_table = bsetting("Enchanting_table", true);
    public Setting<Boolean> Brewing_Stand = bsetting("Brewing_Stand", true);
    public Setting<Boolean> ShulkerBox = bsetting("ShulkerBox", true);

    @SubscribeEvent
    public void onCheck(PacketEvent.Send packet) {
        BlockPos pos;
        if (packet.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && this.check(pos = ((CPacketPlayerTryUseItemOnBlock)packet.getPacket()).getPos())) {
            packet.setCanceled(true);
        }
    }

    public boolean check(BlockPos pos) {
        return Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.CHEST && this.Chest.getValue() != false || Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST && this.EnderChest.getValue() != false || Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.TRAPPED_CHEST && this.Trapped_Chest.getValue() != false || Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.HOPPER && this.Hopper.getValue() != false || Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.DISPENSER && this.Dispenser.getValue() != false || Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.FURNACE && this.Furnace.getValue() != false || Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.BEACON && this.Beacon.getValue() != false || Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.CRAFTING_TABLE && this.Crafting_Table.getValue() != false || Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.ANVIL && this.Anvil.getValue() != false || Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.ENCHANTING_TABLE && this.Enchanting_table.getValue() != false || Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.BREWING_STAND && this.Brewing_Stand.getValue() != false || Minecraft.getMinecraft().world.getBlockState(pos).getBlock() instanceof BlockShulkerBox && this.ShulkerBox.getValue() != false;
    }
}

