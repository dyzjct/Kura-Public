package me.windyteam.kura.module.hud.info;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.HUDModule;
import me.windyteam.kura.utils.inventory.Pair;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.List;

@HUDModule.Info(name = "HoleHud", x = 160, y = 160, width = 48, height = 48, category = Category.HUD)
public class HoleHud extends HUDModule {
    // $FF: synthetic field
    BlockPos SOUTH = new BlockPos(0, 0, 1);
    // $FF: synthetic field
    BlockPos EAST = new BlockPos(1, 0, 0);
    // $FF: synthetic field
    BlockPos NORTH = new BlockPos(0, 0, -1);
    // $FF: synthetic field
    BlockPos WEST = new BlockPos(-1, 0, 0);

    private static void preitemrender() {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.scale(1.0F, 1.0F, 0.01F);
    }

    private static void postitemrender() {
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5D, 0.5D, 0.5D);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        GL11.glPopMatrix();
    }

    @Override
    public void onRender() {
        if (mc.player != null && mc.world != null && mc.getRenderViewEntity() != null) {
            switch (mc.getRenderViewEntity().getHorizontalFacing()) {
                case NORTH:
                    this.itemRender(this.getNorth(), this.x, this.y);
                    break;
                case EAST:
                    this.itemRender(this.getEast(), this.x, this.y);
                    break;
                case SOUTH:
                    this.itemRender(this.getSouth(), this.x, this.y);
                    break;
                case WEST:
                    this.itemRender(this.getWest(), this.x, this.y);
            }

        }
    }

    private List<ItemStack> getEast() {
        BlockPos PlayerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        return Arrays.asList(
                this.isBrockOrObby(PlayerPos.add(this.EAST.x, this.EAST.y, this.EAST.z))
                        ? new ItemStack(mc.world.getBlockState(PlayerPos.add(this.EAST.x, this.EAST.y, this.EAST.z)).getBlock())
                        : new ItemStack(Items.AIR), this.isBrockOrObby(PlayerPos.add(this.SOUTH.x, this.SOUTH.y, this.SOUTH.z))
                        ? new ItemStack(mc.world.getBlockState(PlayerPos.add(this.SOUTH.x, this.SOUTH.y, this.SOUTH.z)).getBlock())
                        : new ItemStack(Items.AIR), this.isBrockOrObby(PlayerPos.add(this.WEST.x, this.WEST.y, this.WEST.z))
                        ? new ItemStack(mc.world.getBlockState(PlayerPos.add(this.WEST.x, this.WEST.y, this.WEST.z)).getBlock())
                        : new ItemStack(Items.AIR), this.isBrockOrObby(PlayerPos.add(this.NORTH.x, this.NORTH.y, this.NORTH.z))
                        ? new ItemStack(mc.world.getBlockState(PlayerPos.add(this.NORTH.x, this.NORTH.y, this.NORTH.z)).getBlock())
                        : new ItemStack(Items.AIR));
    }

    private List<ItemStack> getNorth() {
        BlockPos var1 = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        return Arrays.asList(this.isBrockOrObby(var1.add(this.NORTH.x, this.NORTH.y, this.NORTH.z)) ? new ItemStack(mc.world.getBlockState(var1.add(this.NORTH.x, this.NORTH.y, this.NORTH.z)).getBlock()) : new ItemStack(Items.AIR), this.isBrockOrObby(var1.add(this.EAST.x, this.EAST.y, this.EAST.z)) ? new ItemStack(mc.world.getBlockState(var1.add(this.EAST.x, this.EAST.y, this.EAST.z)).getBlock()) : new ItemStack(Items.AIR), this.isBrockOrObby(var1.add(this.SOUTH.x, this.SOUTH.y, this.SOUTH.z)) ? new ItemStack(mc.world.getBlockState(var1.add(this.SOUTH.x, this.SOUTH.y, this.SOUTH.z)).getBlock()) : new ItemStack(Items.AIR), this.isBrockOrObby(var1.add(this.WEST.x, this.WEST.y, this.WEST.z)) ? new ItemStack(mc.world.getBlockState(var1.add(this.WEST.x, this.WEST.y, this.WEST.z)).getBlock()) : new ItemStack(Items.AIR));
    }

    private boolean isBrockOrObby(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN;
    }

    private List<ItemStack> getWest() {
        BlockPos var1 = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        return Arrays.asList(this.isBrockOrObby(var1.add(this.WEST.x, this.WEST.y, this.WEST.z)) ? new ItemStack(mc.world.getBlockState(var1.add(this.WEST.x, this.WEST.y, this.WEST.z)).getBlock()) : new ItemStack(Items.AIR), this.isBrockOrObby(var1.add(this.NORTH.x, this.NORTH.y, this.NORTH.z)) ? new ItemStack(mc.world.getBlockState(var1.add(this.NORTH.x, this.NORTH.y, this.NORTH.z)).getBlock()) : new ItemStack(Items.AIR), this.isBrockOrObby(var1.add(this.EAST.x, this.EAST.y, this.EAST.z)) ? new ItemStack(mc.world.getBlockState(var1.add(this.EAST.x, this.EAST.y, this.EAST.z)).getBlock()) : new ItemStack(Items.AIR), this.isBrockOrObby(var1.add(this.SOUTH.x, this.SOUTH.y, this.SOUTH.z)) ? new ItemStack(mc.world.getBlockState(var1.add(this.SOUTH.x, this.SOUTH.y, this.SOUTH.z)).getBlock()) : new ItemStack(Items.AIR));
    }

    private List<ItemStack> getSouth() {
        BlockPos var1 = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        return Arrays.asList(this.isBrockOrObby(var1.add(this.SOUTH.x, this.SOUTH.y, this.SOUTH.z)) ? new ItemStack(mc.world.getBlockState(var1.add(this.SOUTH.x, this.SOUTH.y, this.SOUTH.z)).getBlock()) : new ItemStack(Items.AIR), this.isBrockOrObby(var1.add(this.WEST.x, this.WEST.y, this.WEST.z)) ? new ItemStack(mc.world.getBlockState(var1.add(this.WEST.x, this.WEST.y, this.WEST.z)).getBlock()) : new ItemStack(Items.AIR), this.isBrockOrObby(var1.add(this.NORTH.x, this.NORTH.y, this.NORTH.z)) ? new ItemStack(mc.world.getBlockState(var1.add(this.NORTH.x, this.NORTH.y, this.NORTH.z)).getBlock()) : new ItemStack(Items.AIR), this.isBrockOrObby(var1.add(this.EAST.x, this.EAST.y, this.EAST.z)) ? new ItemStack(mc.world.getBlockState(var1.add(this.EAST.x, this.EAST.y, this.EAST.z)).getBlock()) : new ItemStack(Items.AIR));
    }

    private void itemRender(List<ItemStack> itemList, int x, int y) {
        List<Pair<Integer, Integer>> var4
                = Arrays.asList(
                        new Pair<>(x + 16, y)
                        , new Pair<>(x + 32, y + 16)
                        , new Pair<>(x + 16, y + 32)
                        , new Pair<>(x, y + 16));

        for (int i = 0; i < 4; ++i) {
            preitemrender();
            mc.getRenderItem().renderItemAndEffectIntoGUI(itemList.get(i), var4.get(i).getKey(), var4.get(i).getValue());
            postitemrender();
        }

    }
}
