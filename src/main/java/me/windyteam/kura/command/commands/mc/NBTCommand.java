package me.windyteam.kura.command.commands.mc;

import me.windyteam.kura.command.Command;
import me.windyteam.kura.command.syntax.ChunkBuilder;
import me.windyteam.kura.command.syntax.parsers.EnumParser;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class NBTCommand
extends Command {
    Minecraft mc = Minecraft.getMinecraft();
    private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    StringSelection nbt;

    public NBTCommand() {
        super("nbt", new ChunkBuilder().append("action", true, new EnumParser("get", "copy", "wipe")).build());
        this.setDescription("Does NBT related stuff (§fGet§7, §fCopy§7, §fSet§7)");
    }

    @Override
    public void call(String[] args2) {
        if (args2[0].isEmpty()) {
            ChatUtil.NoSpam.sendWarnMessage("Invalid Syntax!");
            return;
        }
        ItemStack item = this.mc.player.inventory.getCurrentItem();
        if (args2[0].equalsIgnoreCase("get")) {
            if (item.getTagCompound() != null) {
                ChatUtil.sendMessage("§6§lNBT:\n" + item.getTagCompound() + "");
            } else {
                ChatUtil.NoSpam.sendErrorMessage("No NBT on " + item.getDisplayName());
            }
        } else if (args2[0].equalsIgnoreCase("copy")) {
            if (item.getTagCompound() != null) {
                this.nbt = new StringSelection(item.getTagCompound() + "");
                this.clipboard.setContents(this.nbt, this.nbt);
                ChatUtil.sendMessage("§6Copied\n§f" + item.getTagCompound() + "\n" + "§6to clipboard.");
            } else {
                ChatUtil.NoSpam.sendWarnMessage("No NBT on " + item.getDisplayName());
            }
        } else if (args2[0].equalsIgnoreCase("wipe")) {
            ChatUtil.sendMessage("§6Wiped\n§f" + item.getTagCompound() + "\n" + "§6from " + item.getDisplayName() + ".");
            item.setTagCompound(new NBTTagCompound());
        }
    }
}

