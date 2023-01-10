package me.windyteam.kura.gui.xg42guistart.component;

import me.windyteam.kura.Kura;
import me.windyteam.kura.gui.xg42guistart.Lcomponent;
import me.windyteam.kura.utils.gl.RenderUtils;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FeatMelonGui
extends Lcomponent {
    private BufferedImage image;
    private DynamicTexture texture;

    public FeatMelonGui(int length) {
        super(length);
        try {
            this.image = ImageIO.read(Kura.class.getResourceAsStream("/assets/kura/feat.png"));
            this.texture = new DynamicTexture(this.image);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Override
    public void render(int displayWidth, int displayHeight) {
        double height = displayHeight / 2;
        double width = displayWidth / 2;
        GL11.glBindTexture((int)3553, (int)this.texture.getGlTextureId());
        RenderUtils.drawTexture(width - 50.0, height - 50.0 + 7.0, 100.0, 100.0, this.getAlpha());
    }
}

