package me.windyteam.kura.utils.render.gui;

import me.windyteam.kura.gui.font.CFontRenderer;
import me.windyteam.kura.gui.font.CFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtlis {

    private static final ResourceLocation CoolFontLocation = new ResourceLocation("textures/font/shengkunji.ttf");
    private static final ResourceLocation NormalFontLocation = new ResourceLocation("textures/font/shengkunji.ttf");

    private static Font CoolFont;
    public static CFontRenderer SFontRenderer = new CFontRenderer(CoolFont, true, false);
    public static RainbowFont RainbowFontRender = new RainbowFont(CoolFont, true, false);
    private static Font NormalFont;
    public static CFontRenderer FontRenderer = new CFontRenderer(NormalFont, true, false);
    public static RainbowFont SRainbowFontRender = new RainbowFont(NormalFont, true, false);

    static {
        try {
            InputStream CoolFontStream = Minecraft.getMinecraft().getResourceManager().getResource(CoolFontLocation).getInputStream();
            InputStream NormalFontStream = Minecraft.getMinecraft().getResourceManager().getResource(NormalFontLocation).getInputStream();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            CoolFont = Font.createFont(Font.TRUETYPE_FONT, CoolFontStream).deriveFont(38.3f);
            ge.registerFont(CoolFont);
            NormalFont = Font.createFont(Font.TRUETYPE_FONT, NormalFontStream).deriveFont(28.8f);
            ge.registerFont(NormalFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void drawArc(float cx, float cy, float r, float start_angle, float end_angle, int num_segments) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBegin(GL_TRIANGLES);
        {
            for (int i = (int) (num_segments / (360 / start_angle)) + 1; i <= num_segments / (360 / end_angle); i++) {
                double previousangle = 2 * Math.PI * (i - 1) / num_segments;
                double angle = 2 * Math.PI * i / num_segments;
                glVertex2d(cx, cy);
                glVertex2d(cx + Math.cos(angle) * r, cy + Math.sin(angle) * r);
                glVertex2d(cx + Math.cos(previousangle) * r, cy + Math.sin(previousangle) * r);
            }
        }
        glEnd();
        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawRoundedRectangle(float x, float y, float width, float height, float radius, int topcolor, int bottomcolor) {

        float tcolora = (float) (topcolor >> 24 & 255) / 255.0F;
        float tcolorr = (float) (topcolor >> 16 & 255) / 255.0F;
        float tcolorg = (float) (topcolor >> 8 & 255) / 255.0F;
        float tcolorb = (float) (topcolor & 255) / 255.0F;

        float bcolora = (float) (bottomcolor >> 24 & 255) / 255.0F;
        float bcolorr = (float) (bottomcolor >> 16 & 255) / 255.0F;
        float bcolorg = (float) (bottomcolor >> 8 & 255) / 255.0F;
        float bcolorb = (float) (bottomcolor & 255) / 255.0F;

        glShadeModel(GL_SMOOTH);

        glColor4f(bcolorr, bcolorg, bcolorb, bcolora);
        drawArc((x + width - radius), (y + height - radius), radius, 0, 90, 16); // bottom right
        drawArc((x + radius), (y + height - radius), radius, 90, 180, 16); // bottom left

        glColor4f(tcolorr, tcolorg, tcolorb, tcolora);
        drawArc(x + radius, y + radius, radius, 180, 270, 16); // top left
        drawArc((x + width - radius), (y + radius), radius, 270, 360, 16); // top right

        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glShadeModel(GL_SMOOTH);

        glBegin(GL_TRIANGLES);
        {
            //top
            {
                glColor4f(tcolorr, tcolorg, tcolorb, tcolora);

                glVertex2d(x + width - radius, y);
                glVertex2d(x + radius, y);
                glVertex2d(x + width - radius, y + radius);

                glVertex2d(x + width - radius, y + radius);
                glVertex2d(x + radius, y);
                glVertex2d(x + radius, y + radius);
            }

            //bottom
            {
                glColor4f(bcolorr, bcolorg, bcolorb, bcolora);

                glVertex2d(x + width - radius, y + height - radius);
                glVertex2d(x + radius, y + height - radius);
                glVertex2d(x + width - radius, y + height);

                glVertex2d(x + width - radius, y + height);
                glVertex2d(x + radius, y + height - radius);
                glVertex2d(x + radius, y + height);
            }
        }
        glEnd();

        glBegin(GL_QUADS);
        {
            glColor4f(tcolorr, tcolorg, tcolorb, tcolora);

            glVertex2d(x + width, y + radius);// top right
            glVertex2d(x, y + radius); //d top left

            glColor4f(bcolorr, bcolorg, bcolorb, bcolora);

            glVertex2d(x, y + height - radius);// bottom left
            glVertex2d(x + width, y + height - radius); //d bottom right
        }
        glEnd();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    public static void drawFilledRectangle(float x, float y, float width, float height) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glBegin(GL_QUADS);
        {
            glVertex2d(x + width, y);               // bottom right
            glVertex2d(x, y);                        //bottom left
            glVertex2d(x, y + height);             //top left
            glVertex2d(x + width, y + height);    //top right
        }
        glEnd();
    }

    public static void drawGradientRect(int mode, double left, double top, double right, double bottom, int startColor, int endColor) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float) (startColor >> 24 & 255) / 255.0F;
        float f = (float) (startColor >> 16 & 255) / 255.0F;
        float f1 = (float) (startColor >> 8 & 255) / 255.0F;
        float f2 = (float) (startColor & 255) / 255.0F;

        float f7 = (float) (endColor >> 24 & 255) / 255.0F;
        float f4 = (float) (endColor >> 16 & 255) / 255.0F;
        float f5 = (float) (endColor >> 8 & 255) / 255.0F;
        float f6 = (float) (endColor & 255) / 255.0F;


        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glShadeModel(GL_SMOOTH);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glBegin(mode);
        {
            glColor4f(f4, f5, f6, f7);
            glVertex2d(left, bottom);
            glColor4f(f, f1, f2, f3);
            glVertex2d(right, bottom);
            glColor4f(f, f1, f2, f3);
            glVertex2d(right, top);
            glColor4f(f4, f5, f6, f7);
            glVertex2d(left, top);
        }
        glEnd();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    public static void drawRect(int mode, double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(f, f1, f2, f3);
        glBegin(mode);
        {
            glVertex2d(left, bottom);
            glVertex2d(right, bottom);
            glVertex2d(right, top);
            glVertex2d(left, top);
        }
        glEnd();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    public static void drawTexture(double x, double y, double width, double height) {
        glEnable(GL_TEXTURE_2D);
        glColor4f(1, 1, 1, 1);
        glPushMatrix();
        {
            glBegin(GL_TRIANGLES);
            {
                glTexCoord2f(1f, 0f);//Bottom right
                glVertex2d(x + width, y);// bottom right

                glTexCoord2f(0f, 0f);//bottom left
                glVertex2d(x, y);// bottom left

                glTexCoord2f(0f, 1f);//top left
                glVertex2d(x, y + height); // top left

                glTexCoord2f(0f, 1f);//top left
                glVertex2d(x, y + height); // top left

                glTexCoord2f(1f, 1f);//top right
                glVertex2d(x + width, y + height); // top right

                glTexCoord2f(1f, 0f);//Bottom right
                glVertex2d(x + width, y);// bottom right
            }
            glEnd();
        }
        glPopMatrix();
    }

}
