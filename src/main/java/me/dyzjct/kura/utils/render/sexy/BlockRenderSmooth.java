package me.dyzjct.kura.utils.render.sexy;

import me.dyzjct.kura.utils.Timer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BlockRenderSmooth {

    public BlockPos lastPos;
    public BlockPos newPos;
    public FadeUtils fade;
    public static Timer timer = new Timer();

    public BlockRenderSmooth(BlockPos pos, long smoothLength) {
        lastPos = pos;
        newPos = pos;
        fade = new FadeUtils(smoothLength);
    }

    public void setNewPos(BlockPos pos) {
        if (!pos.equals(newPos) && timer.passed(200)) {
            lastPos = newPos;
            newPos = pos;
            fade.reset();
            timer.reset();
        }
    }

    public Vec3d getRenderPos() {
        return Lerp(PosToVec(lastPos), PosToVec(newPos), (float) fade.easeOutQuad());
    }

    public static Vec3d Lerp(Vec3d from, Vec3d to, float t) {
        return new Vec3d(t * to.x + (1 - t) * from.x, t * to.y + (1 - t) * from.y, t * to.z + (1 - t) * from.z);
    }

    public static Vec3d PosToVec(BlockPos pos) {
        return new Vec3d(pos.x, pos.y, pos.z);
    }
}
