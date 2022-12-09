package me.dyzjct.kura.gui.xg42guistart.component;

import me.dyzjct.kura.Kura;
import me.dyzjct.kura.gui.xg42guistart.Lcomponent;
import me.dyzjct.kura.utils.font.CFont;
import me.dyzjct.kura.utils.font.CFontRenderer;
import me.dyzjct.kura.utils.gl.RenderUtils;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ProtectByEskidGUI
extends Lcomponent {
    private BufferedImage image;
    private DynamicTexture texture;
    private CFontRenderer fontRenderer;

    public ProtectByEskidGUI(int length) {
        super(length);
        try {
            this.image = ImageIO.read(Kura.class.getResourceAsStream("/assets/eridani/icon.png"));
            this.texture = new DynamicTexture(this.image);
            this.fontRenderer = new CFontRenderer(new CFont.CustomFont("/assets/fonts/Roboto.ttf", 38.0f, 0), true, false);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Override
    public void render(int realDisplayWidth, int realDisplayHeight) {
        double height = (double)realDisplayHeight / 2.0;
        double width = (double)realDisplayWidth / 2.0;
        GL11.glBindTexture((int)3553, (int)this.texture.getGlTextureId());
        RenderUtils.drawTexture(width - 50.0, height - 50.0 - 21.0, 100.0, 100.0, this.getAlpha());
        this.fontRenderer.drawCenteredString("Protected By ESkid", (float)width, (float)height + 30.0f, new Color(255, 255, 255, this.getAlpha()).getRGB());
    }
}

