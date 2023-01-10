package me.windyteam.kura.module.modules.render;

import com.google.common.collect.Sets;
import me.windyteam.kura.event.events.render.RenderEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.Pair;
import me.windyteam.kura.utils.color.GSColor;
import me.windyteam.kura.utils.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Module.Info(name = "HoleESP", category = Category.RENDER, description = "Show safe holes")
public class HoleESP extends Module {
    public static HoleESP INSTANCE = new HoleESP();

    public Setting<Mode> customHoles = msetting("HoleMode", Mode.Double);
    public Setting<Integer> bedrockColorR = isetting("BedrockRed", 255, 0, 255);
    public Setting<Integer> bedrockColorG = isetting("BedrockGreen", 198, 0, 255);
    public Setting<Integer> bedrockColorB = isetting("BedrockBlue", 203, 0, 255);
    public Setting<Integer> bedrockAlpha = isetting("BedrockAlpha", 53, 0, 255);
    public Setting<Integer> obsidianColorR = isetting("ObsidianRed", 255, 0, 255);
    public Setting<Integer> obsidianColorG = isetting("ObsidianGreen", 198, 0, 255);
    public Setting<Integer> obsidianColorB = isetting("ObsidianBlue", 203, 0, 255);
    public Setting<Integer> obsidianAlpha = isetting("ObsidianAlpha", 65, 0, 255);
    public Setting<Integer> rangeS = isetting("Range", 6, 1, 20);
    public ConcurrentHashMap<BlockPos, GSColor> holes = new ConcurrentHashMap<>();

    // gets the entities location
    public static BlockPos getEntityPos(Entity entity) {
        return new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY), Math.floor(entity.posZ));
    }

    // defines the area for the client to search
    public java.util.List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        java.util.List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    @Override
    public String getHudInfo() {
        return "[ " + holes.size() + " ]";
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (mc.player == null || mc.world == null || holes == null || holes.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        holes.forEach(this::renderFill);
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (holes == null) {
            holes = new ConcurrentHashMap<>();
        } else {
            holes.clear();
        }
        int range = (int) Math.ceil(rangeS.getValue());
        HashSet<BlockPos> possibleFullHoles = Sets.newHashSet();
        HashMap<BlockPos, Pair<BlockOffset, GSColor>> possibleWideHoles = new HashMap<>();
        List<BlockPos> blockPosList = getSphere(getEntityPos(mc.player), range, range, false, true, 0);

        for (BlockPos pos : blockPosList) {
            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            if (mc.world.getBlockState(pos.add(0, -1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            if (mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                possibleFullHoles.add(pos);
            }
        }

        possibleFullHoles.forEach(pos -> {
            GSColor color = new GSColor(bedrockColorR.getValue(), bedrockColorG.getValue(), bedrockColorB.getValue(), bedrockAlpha.getValue());
            HashMap<BlockOffset, BlockSafety> unsafeSides = getUnsafeSides(pos);
            if (unsafeSides.containsKey(BlockOffset.DOWN)) {
                if (unsafeSides.remove(BlockOffset.DOWN, BlockSafety.BREAKABLE)) {
                    return;
                }
            }
            int size = unsafeSides.size();
            unsafeSides.entrySet().removeIf(entry -> entry.getValue() == BlockSafety.RESISTANT);
            if (unsafeSides.size() != size)
                color = new GSColor(obsidianColorR.getValue(), obsidianColorG.getValue(), obsidianColorB.getValue(), obsidianAlpha.getValue());
            size = unsafeSides.size();
            if (size == 0) {
                holes.put(pos, color);
            }
            if (size == 1) {
                possibleWideHoles.put(pos, new Pair<>(unsafeSides.keySet().stream().findFirst().get(), color));
            }
        });
        Mode customHoleMode = customHoles.getValue();
        if (!customHoleMode.equals(Mode.Single)) {
            possibleWideHoles.forEach((pos, pair) -> {
                GSColor color = pair.getValue();
                BlockPos unsafePos = pair.getKey().offset(pos);
                HashMap<BlockOffset, BlockSafety> unsafeSides = getUnsafeSides(unsafePos);
                int size = unsafeSides.size();
                unsafeSides.entrySet().removeIf(entry -> entry.getValue() == BlockSafety.RESISTANT);
                if (unsafeSides.size() != size)
                    color = new GSColor(obsidianColorR.getValue(), obsidianColorG.getValue(), obsidianColorB.getValue(), obsidianAlpha.getValue());
                if (unsafeSides.size() > 1)
                    return;
                holes.put(pos, color);
            });
        }
    }

    public BlockSafety isBlockSafe(Block block) {
        if (block == Blocks.BEDROCK) {
            return BlockSafety.UNBREAKABLE;
        }
        if (block == Blocks.OBSIDIAN || block == Blocks.ENDER_CHEST || block == Blocks.ANVIL) {
            return BlockSafety.RESISTANT;
        }
        return BlockSafety.BREAKABLE;
    }

    public HashMap<BlockOffset, BlockSafety> getUnsafeSides(BlockPos pos) {
        HashMap<BlockOffset, BlockSafety> output = new HashMap<>();
        BlockSafety temp;

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.DOWN.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.DOWN, temp);

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.NORTH.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.NORTH, temp);

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.SOUTH.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.SOUTH, temp);

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.EAST.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.EAST, temp);

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.WEST.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.WEST, temp);

        return output;
    }

    public void renderFill(BlockPos hole, GSColor color) {
        GSColor fillColor = new GSColor(color, 50);
        RenderUtil.drawBoxESP(hole, fillColor, color, 2f, true, true, true, 0f, true, true, false, false, 0);
    }

    public enum Mode {
        Single,
        Double,
    }

    public enum BlockSafety {
        UNBREAKABLE,
        RESISTANT,
        BREAKABLE
    }

    public enum BlockOffset {
        DOWN(0, -1, 0),
        UP(0, 1, 0),
        NORTH(0, 0, -1),
        SOUTH(0, 0, 1),
        WEST(-1, 0, 0),
        EAST(1, 0, 0);

        public int x;
        public int y;
        public int z;

        BlockOffset(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public BlockPos offset(BlockPos pos) {
            return pos.add(x, y, z);
        }
    }
}
