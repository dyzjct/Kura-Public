package me.dyzjct.kura.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

public class Utils {
    public static boolean nullCheck() {
        return Wrapper.getPlayer() == null || Wrapper.getWorld() == null;
    }

    public static ByteBuffer readImageToBuffer(InputStream p_readImageToBuffer_1_) throws IOException {
        BufferedImage bufferedimage = ImageIO.read(p_readImageToBuffer_1_);
        int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);
        int[] var5 = aint;
        int var6 = aint.length;
        for (int var7 = 0; var7 < var6; ++var7) {
            int i = var5[var7];
            bytebuffer.putInt(i << 8 | i >> 24 & 0xFF);
        }
        bytebuffer.flip();
        return bytebuffer;
    }
}

