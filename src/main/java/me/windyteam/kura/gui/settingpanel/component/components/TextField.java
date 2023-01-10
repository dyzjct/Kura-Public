package me.windyteam.kura.gui.settingpanel.component.components;

import me.windyteam.kura.gui.settingpanel.Window;
import me.windyteam.kura.gui.settingpanel.component.AbstractComponent;
import me.windyteam.kura.gui.settingpanel.component.ValueChangeListener;
import me.windyteam.kura.gui.settingpanel.utils.GLUtil;
import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.utils.KeyboardUtils;
import me.windyteam.kura.utils.Timer;
import me.windyteam.kura.utils.gl.RenderUtils;
import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.utils.gl.RenderUtils;
import net.minecraft.client.Minecraft;

public class TextField
        extends AbstractComponent {
    private static final int PREFERRED_WIDTH = 180;
    private static final int PREFERRED_HEIGHT = 22;
    private final int preferredWidth;
    private final int preferredHeight;
    Timer timer = new Timer();
    private boolean typing;
    private String title;
    private String value;
    private boolean hovered;
    private ValueChangeListener<String> listener;
    private String TypeDir = "";

    public TextField(String title, int preferredWidth, int preferredHeight) {
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
        this.setWidth(preferredWidth);
        this.setHeight(preferredHeight);
        this.setTitle(title);
    }

    public TextField(String title) {
        this(title, 180, 22);
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    @Override
    public void render() {
        RenderUtils.drawRoundedRectangle(this.x, this.y, this.getWidth(), this.getHeight(), 7.0, this.hovered ? Window.TERTIARY_FOREGROUND : Window.SECONDARY_FOREGROUND);
        if (GuiManager.getINSTANCE().isRainbow()) {
            RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.getWidth(), this.getHeight(), 7f, 1.0f, RenderUtils.GradientDirection.LeftToRight, GuiManager.getINSTANCE().getRainbowColor(), GuiManager.getINSTANCE().getAddRainbowColor());
        } else {
            RenderUtils.drawRoundedRectangleOutline(this.x, this.y, this.getWidth(), this.getHeight(), 7f, 1.0f, this.hovered ? Window.TERTIARY_OUTLINE : Window.SECONDARY_OUTLINE);
        }
        Minecraft.getMinecraft().fontRenderer.drawString(this.title, this.x + this.getWidth() / 2 - Minecraft.getMinecraft().fontRenderer.getStringWidth(this.title) / 2, this.y + this.getHeight() / 2 - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2, Window.FONT.getRGB());
    }

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        this.updateHovered(x, y, offscreen);
        return false;
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        this.hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + this.getWidth() && y <= this.y + this.getHeight();
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        if (button == 0) {
            this.updateHovered(x, y, offscreen);
            if (this.hovered) {
                this.typing = !this.typing;
                this.TypeDir = this.value;
                this.update();
                return true;
            }
        }
        return this.typing;
    }

    @Override
    public boolean keyPressed(int key, char c) {
        if (this.typing) {
            if (key == 211 || key == 14) {
                if (this.timer.passed(700L)) {
                    this.TypeDir = TextField.removeLastChar(this.TypeDir);
                    this.timer.reset();
                } else {
                    this.TypeDir = TextField.removeLastChar(this.TypeDir);
                }
            } else if (key == 28) {
                this.typing = false;
            } else if (!String.valueOf(c).contains("\u0000")) {
                this.TypeDir = this.TypeDir + c;
            } else if (KeyboardUtils.isCtrlDown() && KeyboardUtils.isDown(47)) {
                this.TypeDir = this.TypeDir + KeyboardUtils.getClipboardString();
            }
            this.update();
        }
        return this.typing;
    }

    public void changeValue(String newValue) {
        boolean change = this.listener.onValueChange(newValue);
        if (change) {
            this.value = newValue;
        }
    }

    public void update() {
        if (this.typing) {
            this.setTitle(this.TypeDir + "...");
        } else {
            this.setTitle(this.value);
        }
        this.changeValue(this.TypeDir);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.setWidth(Math.max(GLUtil.getFontRenderer().getStringWidth(title), this.preferredWidth));
        this.setHeight(Math.max(GLUtil.getFontRenderer().getHeight() * 5 / 4, this.preferredHeight));
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setListener(ValueChangeListener<String> listener) {
        this.listener = listener;
    }
}

