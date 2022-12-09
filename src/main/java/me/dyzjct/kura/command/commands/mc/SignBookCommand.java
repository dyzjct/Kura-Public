package me.dyzjct.kura.command.commands.mc;

import me.dyzjct.kura.command.Command;
import me.dyzjct.kura.command.syntax.ChunkBuilder;
import me.dyzjct.kura.utils.mc.ChatUtil;
import me.dyzjct.kura.utils.Wrapper;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

public class SignBookCommand extends Command {

    public SignBookCommand() {
        super("signbook", new ChunkBuilder().append("name").build(), "sign");
        this.setDescription("Colored book names. &f#n&7 for a new line and &f&&7 for colour codes");
    }

    @Override
    public void call(String[] args2) {
        ItemStack is = Wrapper.getPlayer().inventory.getCurrentItem();
        int c = 167;
        if (args2.length == 1) {
            ChatUtil.NoSpam.sendWarnMessage("Please specify a title.");
            return;
        }
        if (is.getItem() instanceof ItemWritableBook) {
            ArrayList<String> toAdd = new ArrayList<>(Arrays.asList(args2));
            String futureTitle = String.join(" ", toAdd);
            futureTitle = futureTitle.replaceAll("&", Character.toString((char)c));
            futureTitle = futureTitle.replaceAll("#n", "\n");
            if ((futureTitle = futureTitle.replaceAll("null", "")).length() > 31) {
                ChatUtil.NoSpam.sendWarnMessage("Title cannot be over 31 characters.");
                return;
            }
            NBTTagList pages = new NBTTagList();
            String pageText = "";
            pages.appendTag(new NBTTagString(pageText));
            NBTTagCompound bookData = is.getTagCompound();
            if (is.hasTagCompound()) {
                if (bookData != null) {
                    is.setTagCompound(bookData);
                }
                is.getTagCompound().setTag("title", new NBTTagString(futureTitle));
                is.getTagCompound().setTag("author", new NBTTagString(Wrapper.getPlayer().getName()));
            } else {
                is.setTagInfo("pages", pages);
                is.setTagInfo("title", new NBTTagString(futureTitle));
                is.setTagInfo("author", new NBTTagString(Wrapper.getPlayer().getName()));
            }
            PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
            buf.writeItemStack(is);
            Wrapper.getPlayer().connection.sendPacket(new CPacketCustomPayload("MC|BSign", buf));
            ChatUtil.sendMessage("Signed book with title: " + futureTitle + "&r");
        } else {
            ChatUtil.NoSpam.sendWarnMessage("You must be holding a writable book.");
        }
    }
}

