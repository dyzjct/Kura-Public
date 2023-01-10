package me.windyteam.kura.command.commands.module;

import me.windyteam.kura.command.Command;
import me.windyteam.kura.command.syntax.ChunkBuilder;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.utils.Wrapper;
import io.netty.buffer.Unpooled;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

public class DupeBookCommand
extends Command {
    public DupeBookCommand() {
        super("dupebook", new ChunkBuilder().append("name").build());
        this.setDescription("Generates books used for chunk savestate dupe.");
    }

    @Override
    public void call(String[] args2) {
        ItemStack heldItem = Wrapper.getPlayer().inventory.getCurrentItem();
        if (heldItem.getItem() instanceof ItemWritableBook) {
            IntStream characterGenerator = new Random().ints(128, 1112063).map(i -> i < 55296 ? i : i + 2048);
            NBTTagList pages = new NBTTagList();
            String joinedPages = characterGenerator.limit(10500L).mapToObj(i -> String.valueOf((char)i)).collect(Collectors.joining());
            for (int page = 0; page < 50; ++page) {
                pages.appendTag((NBTBase)new NBTTagString(joinedPages.substring(page * 210, (page + 1) * 210)));
            }
            if (heldItem.hasTagCompound()) {
                assert (heldItem.getTagCompound() != null);
                heldItem.getTagCompound().setTag("pages", (NBTBase)pages);
                heldItem.getTagCompound().setTag("title", (NBTBase)new NBTTagString(""));
                heldItem.getTagCompound().setTag("author", (NBTBase)new NBTTagString(Wrapper.getPlayer().getName()));
            } else {
                heldItem.setTagInfo("pages", (NBTBase)pages);
                heldItem.setTagInfo("title", (NBTBase)new NBTTagString(""));
                heldItem.setTagInfo("author", (NBTBase)new NBTTagString(Wrapper.getPlayer().getName()));
            }
            PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
            buf.writeItemStack(heldItem);
            Wrapper.getPlayer().connection.sendPacket((Packet)new CPacketCustomPayload("MC|BEdit", buf));
            ChatUtil.NoSpam.sendWarnMessage("Dupe book generated.");
        } else {
            ChatUtil.NoSpam.sendErrorMessage("You must be holding a writable book.");
        }
    }
}

