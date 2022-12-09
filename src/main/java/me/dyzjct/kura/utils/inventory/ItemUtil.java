package me.dyzjct.kura.utils.inventory;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

// == would suffice probably
public class ItemUtil {

    public static boolean areSame(Block block1, Block block2) {
        return Block.getIdFromBlock(block1) == Block.getIdFromBlock(block2);
    }

    public static boolean areSame(Item item1, Item item2) {
        return Item.getIdFromItem(item1) == Item.getIdFromItem(item2);
    }

    public static boolean areSame(Block block, Item item) {
        return item instanceof ItemBlock && areSame(block, ((ItemBlock) item).getBlock());
    }

    public static boolean areSame(ItemStack stack, Block block) {
        return stack != null && areSame(block, stack.getItem());
    }

    public static boolean areSame(ItemStack stack, Item item) {
        return stack != null && areSame(stack.getItem(), item);
    }

    public static boolean Is32k(ItemStack p_Stack) {
        if (p_Stack.getEnchantmentTagList() != null) {
            final NBTTagList tags = p_Stack.getEnchantmentTagList();
            for (int i = 0; i < tags.tagCount(); i++) {
                final NBTTagCompound tagCompound = tags.getCompoundTagAt(i);
                if (tagCompound != null && Enchantment.getEnchantmentByID(tagCompound.getByte("id")) != null) {
                    final Enchantment enchantment = Enchantment.getEnchantmentByID(tagCompound.getShort("id"));
                    final short lvl = tagCompound.getShort("lvl");
                    if (enchantment != null) {
                        if (enchantment.isCurse())
                            continue;

                        if (lvl >= 1000)
                            return true;
                    }
                }
            }
        }
        return false;
    }
}
