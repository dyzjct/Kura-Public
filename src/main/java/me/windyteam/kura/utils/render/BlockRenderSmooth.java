package me.windyteam.kura.utils.render;

import net.minecraft.util.math.BlockPos;

public class BlockRenderSmooth {
    private final FadeUtils fade;
    private BlockPos lastPos;
    private BlockPos newPos;

    public BlockRenderSmooth(BlockPos pos, int smoothLength) {
        lastPos = pos;
        newPos = pos;
        fade = new FadeUtils(smoothLength);
    }

    public BlockRenderSmooth(BlockPos pos) {
        this(pos, 100000000);
    }

    public void setNewPos(BlockPos pos) {
        if (pos != newPos) {
            lastPos = newPos;
            newPos = pos;
        }
        fade.reset();
    }

    public BlockPos getRenderPos() {
        double maxX = Math.max(lastPos.x, newPos.x);
        double minX = Math.min(lastPos.x, newPos.x);
        double maxY = Math.max(lastPos.y, newPos.y);
        double minY = Math.min(lastPos.y, newPos.y);
        double maxZ = Math.max(lastPos.z, newPos.z);
        double minZ = Math.min(lastPos.z, newPos.z);
        double x = minX + ((maxX - minX) * fade.easeOutQuad());
        double y = minY + ((maxY - minY) * fade.easeOutQuad());
        double z = minZ + ((maxZ - minZ) * fade.easeOutQuad());
        return new BlockPos(x, y, z);
    }
}
