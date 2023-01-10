package me.windyteam.kura.utils.render.gui;

import me.windyteam.kura.gui.font.CFont;
import me.windyteam.kura.gui.font.CFont;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RainbowFont extends CFont {

    protected CharData[] boldChars = new CharData[256];
    protected CharData[] italicChars = new CharData[256];
    protected CharData[] boldItalicChars = new CharData[256];

    private final int[] colorCode = new int[32];
    private final String colorcodeIdentifiers = "0123456789abcdefklmnor";

    public RainbowFont(Font font, boolean antiAlias, boolean fractionalMetrics) {
        super(font, antiAlias, fractionalMetrics);
        setupMinecraftColorcodes();
        setupBoldItalicIDs();
    }

    public float drawStringWithShadow(String text, double x, double y, float speed, float saturation, float brightness, long add, int alpha) {
        float shadowWidth = drawString(text, x + 1D, y + 1D, speed, saturation, brightness, add, alpha, true);
        return Math.max(shadowWidth, drawString(text, x, y, speed, saturation, brightness, add, alpha, false));
    }

    public float drawString(String text, float x, float y, float speed, float saturation, float brightness, long add, int alpha) {
        return drawString(text, x, y, speed, saturation, brightness, add, alpha, false);
    }

    public float drawCenteredStringWithShadow(String text, float x, float y, float speed, float saturation, float brightness, long add, int alpha) {
        return drawStringWithShadow(text, x - getStringWidth(text) / 2, y, speed, saturation, brightness, add, alpha);
    }

    public float drawCenteredString(String text, float x, float y, float speed, float saturation, float brightness, long add, int alpha) {
        return drawString(text, x - getStringWidth(text) / 2, y, speed, saturation, brightness, add, alpha);
    }

    public float drawString(String text, double x, double y, float speed, float saturation, float brightness, long add, int alpha, boolean shadow) {
        x -= 1;
        y -= 2;

        if (text == null) {
            return 0.0F;
        }

        CharData[] currentData = this.charData;
        boolean randomCase = false;
        boolean bold = false;
        boolean italic = false;
        boolean strikethrough = false;
        boolean underline = false;
        boolean render = true;
        x *= 2.0D;
        y *= 2.0D;
        int color = Rainbow.getRainbow(4, 1, 1);
        if (render) {
            GL11.glPushMatrix();
            GL11.glScaled(0.5D, 0.5D, 0.5D);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, alpha);
            int size = text.length();
            GlStateManager.bindTexture(tex.getGlTextureId());
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getGlTextureId());
            for (int i = 0; i < size; i++) {
                char character = text.charAt(i);
                int rainbowcolor = Rainbow.getRainbow(speed, saturation, brightness, i * -add);
                if (rainbowcolor == 553648127) {
                    rainbowcolor = 16777215;
                }
                if ((rainbowcolor & 0xFC000000) == 0) {
                    rainbowcolor |= -16777216;
                }
                if (shadow) {
                    rainbowcolor = (rainbowcolor & 0xFCFCFC) >> 2 | rainbowcolor & 0xFF000000;
                }
                GL11.glColor4f((rainbowcolor >> 16 & 0xFF) / 255.0F, (rainbowcolor >> 8 & 0xFF) / 255.0F, (rainbowcolor & 0xFF) / 255.0F, alpha);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getGlTextureId());
                GL11.glBegin(4);
                drawChar(currentData, character, (float) x, (float) y);
                GL11.glEnd();
                if (strikethrough) drawLine(x, y + currentData[character].height / 2, x + currentData[character].width - 8.0D, y + currentData[character].height / 2, 1.0F);
                if (underline) drawLine(x, y + currentData[character].height - 2.0D, x + currentData[character].width - 8.0D, y + currentData[character].height - 2.0D, 1.0F);
                x += currentData[character].width - 8 + this.charOffset;
            }
            GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
            GL11.glPopMatrix();
        }
        GL11.glDisable(GL11.GL_BLEND);
        return (float) x / 2.0F;
    }

    @Override
    public int getStringWidth(String text) {
        if (text == null) {
            return 0;
        }
        int width = 0;
        CharData[] currentData = this.charData;
        boolean bold = false;
        boolean italic = false;
        int size = text.length();

        for (int i = 0; i < size; i++) {
            char character = text.charAt(i);
            if ((character == '\u00A7') && (i < size)) {
                int colorIndex = "0123456789abcdefklmnor".indexOf(character);
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                } else if (colorIndex == 17) {
                    bold = true;
                    if (italic) currentData = this.boldItalicChars;
                    else currentData = this.boldChars;
                } else if (colorIndex == 20) {
                    italic = true;
                    if (bold) currentData = this.boldItalicChars;
                    else currentData = this.italicChars;
                } else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    currentData = this.charData;
                }
                i++;
            } else if ((character < currentData.length) && (character >= 0)) {
                width += currentData[character].width - 8 + this.charOffset;
            }
        }

        return width / 2;
    }

    public void setFont(Font font) {
        super.setFont(font);
        setupBoldItalicIDs();
    }

    public void setAntiAlias(boolean antiAlias) {
        super.setAntiAlias(antiAlias);
        setupBoldItalicIDs();
    }

    public void setFractionalMetrics(boolean fractionalMetrics) {
        super.setFractionalMetrics(fractionalMetrics);
        setupBoldItalicIDs();
    }

    protected DynamicTexture texBold;
    protected DynamicTexture texItalic;
    protected DynamicTexture texItalicBold;

    private void setupBoldItalicIDs() {
        texBold = setupTexture(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics, this.boldChars);
        texItalic = setupTexture(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics, this.italicChars);
        texItalicBold = setupTexture(this.font.deriveFont(3), this.antiAlias, this.fractionalMetrics, this.boldItalicChars);
    }

    private void drawLine(double x, double y, double x1, double y1, float width) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(width);
        GL11.glBegin(1);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x1, y1);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public List<String> wrapWords(String text, double width) {
        List finalWords = new ArrayList();
        if (getStringWidth(text) > width) {
            String[] words = text.split(" ");
            String currentWord = "";
            char lastColorCode = 65535;

            for (String word : words) {
                for (int i = 0; i < word.toCharArray().length; i++) {
                    char c = word.toCharArray()[i];

                    if ((c == '\u00A7') && (i < word.toCharArray().length - 1)) {
                        lastColorCode = word.toCharArray()[(i + 1)];
                    }
                }
                if (getStringWidth(currentWord + word + " ") < width) {
                    currentWord = currentWord + word + " ";
                } else {
                    finalWords.add(currentWord);
                    currentWord = "\u00A7" + lastColorCode + word + " ";
                }
            }
            if (currentWord.length() > 0) if (getStringWidth(currentWord) < width) {
                finalWords.add("\u00A7" + lastColorCode + currentWord + " ");
                currentWord = "";
            } else {
                for (String s : formatString(currentWord, width))
                    finalWords.add(s);
            }
        } else {
            finalWords.add(text);
        }
        return finalWords;
    }

    public List<String> formatString(String string, double width) {
        List finalWords = new ArrayList();
        String currentWord = "";
        char lastColorCode = 65535;
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if ((c == '\u00A7') && (i < chars.length - 1)) {
                lastColorCode = chars[(i + 1)];
            }

            if (getStringWidth(currentWord + c) < width) {
                currentWord = currentWord + c;
            } else {
                finalWords.add(currentWord);
                currentWord = "\u00A7" + lastColorCode + String.valueOf(c);
            }
        }

        if (currentWord.length() > 0) {
            finalWords.add(currentWord);
        }

        return finalWords;
    }

    private void setupMinecraftColorcodes() {
        for (int index = 0; index < 32; index++) {
            int noClue = (index >> 3 & 0x1) * 85;
            int red = (index >> 2 & 0x1) * 170 + noClue;
            int green = (index >> 1 & 0x1) * 170 + noClue;
            int blue = (index >> 0 & 0x1) * 170 + noClue;

            if (index == 6) {
                red += 85;
            }

            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }

            this.colorCode[index] = ((red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF);
        }
    }

    public void drawStringWithShadow(String s) {
    }
}