package me.windyteam.kura.command.commands.mc;

import me.windyteam.kura.command.Command;
import me.windyteam.kura.command.syntax.SyntaxChunk;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.utils.Wrapper;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.block.Block;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityShulkerBox;

public class PeekCommand
extends Command {
    public static TileEntityShulkerBox sb;

    public PeekCommand() {
        super("peek", SyntaxChunk.EMPTY);
        this.setDescription("Look inside the contents of a shulker box without opening it");
    }

    @Override
    public void call(String[] args2) {
        ItemStack is = Wrapper.getPlayer().inventory.getCurrentItem();
        if (is.getItem() instanceof ItemShulkerBox) {
            Test entityBox = new Test();
            entityBox.setBlockType(((ItemShulkerBox)is.getItem()).getBlock());
            entityBox.setWorld(Wrapper.getWorld());
            assert is.getTagCompound() != null;
            entityBox.readFromNBT(is.getTagCompound().getCompoundTag("BlockEntityTag"));
            sb = entityBox;
        } else {
            ChatUtil.NoSpam.sendWarnMessage("You aren't carrying a shulker box.");
        }
    }

    private class Test
    extends TileEntityShulkerBox {
        private Test() {
        }

        public void setBlockType(Block blockType) {
            this.blockType = blockType;
        }
    }
}

