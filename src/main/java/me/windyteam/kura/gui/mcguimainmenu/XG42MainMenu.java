package me.windyteam.kura.gui.mcguimainmenu;

import me.windyteam.kura.Kura;
import me.windyteam.kura.gui.alt.GuiAlt;
import me.windyteam.kura.gui.mcguimainmenu.bcomponent.NormalButton;
import me.windyteam.kura.utils.color.ColorUtils;
import me.windyteam.kura.utils.gl.GLTexture;
import me.windyteam.kura.utils.MathUtil;
import me.windyteam.kura.utils.gl.RenderUtils;
import me.windyteam.kura.utils.Timer;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class XG42MainMenu
extends GuiScreen {
    private DynamicTexture texturebg;
    private GLTexture texturelogo;
    private final Timer timer = new Timer();
    List<ButtonComponent> buttonlist = new ArrayList<>();
    private DynamicTexture[] BG = new DynamicTexture[0];
    private int indexBG = 0;
    private final Timer changeBGTimer = new Timer();

    public void forwardLoopBG() {
        this.indexBG = this.indexBG < this.BG.length - 1 ? ++this.indexBG : 0;
    }

    public void loadBG() {
        try {
            File BGPath = new File("Kura/gui/");
            if (!BGPath.exists()) {
                BGPath.getParentFile().mkdirs();
            }
            ArrayList<DynamicTexture> findBG = new ArrayList<>();
            for (File picture : Objects.requireNonNull(BGPath.listFiles())) {
                if (!picture.getName().endsWith(".png") && !picture.getName().endsWith(".jpg")) continue;
                findBG.add(new DynamicTexture(ImageIO.read(picture)));
            }
            this.BG = findBG.toArray(this.BG);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public XG42MainMenu() {
        this.loadBG();
        try {
            this.texturebg = new DynamicTexture(ImageIO.read(Kura.class.getResourceAsStream("/assets/kura/background/login.jpg")));
            this.texturelogo = RenderUtils.loadTexture(Kura.class.getResourceAsStream("/assets/kura/logo/Kura.png"));
        }
        catch (IOException ignored) { }
        this.init();
        this.timer.reset();
        this.changeBGTimer.reset();
    }

    private void init() {

        this.buttonlist.add(new NormalButton("Single", 20.0f, 0, 70.0f, 13.0f).setOnClickListener(() -> this.mc.displayGuiScreen(new GuiWorldSelection(this))));

        this.buttonlist.add(new NormalButton("Multi", 20.0f, 0, 70.0f, 13.0f).setOnClickListener(() -> this.mc.displayGuiScreen(new GuiMultiplayer(this))));

        this.buttonlist.add(new NormalButton("Alt", 20.0f, 0, 70.0f, 13.0f).setOnClickListener(() -> this.mc.displayGuiScreen(new GuiAlt(this))));

        this.buttonlist.add(new NormalButton("Settings", 20.0f, 0, 70.0f, 13.0f).setOnClickListener(() -> this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings))));

        this.buttonlist.add(new NormalButton("Exit", 20.0f, 0, 70.0f, 13.0f).setOnClickListener(() -> this.mc.shutdown()));
    }

    @Override
    public void initGui() {
        AtomicReference<Float> startY = new AtomicReference<>((height / 6F) * 2);
        buttonlist.forEach(buttonComponent -> {
            buttonComponent.y = startY.get();
            startY.updateAndGet(v -> v + 20f);
        });
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.BG.length != 0 && this.changeBGTimer.passed(6000L)) {
            this.forwardLoopBG();
            this.changeBGTimer.reset();
        }
        RenderUtils.drawRect(0.0, 0.0, this.width, this.height, Color.BLACK);
        int alpha = !this.timer.passed(950L) ? ColorUtils.calculateAlphaChangeColor(0, 255, 1000, (int)this.timer.getPassedTimeMs()) : 255;
        double scale = !this.timer.passed(4000L) ? MathUtil.calculateDoubleChange(3.0, 1.1, 4000, (int)this.timer.getPassedTimeMs()) : 1.1;
        double move = !this.timer.passed(3000L) ? MathUtil.calculateDoubleChange(200.0, 0.0, 3000, (int)this.timer.getPassedTimeMs()) : 0.0;
        GL11.glPushMatrix();
        GL11.glTranslated(move, move, 0.0);
        GL11.glTranslated((double)this.width / 2.0, (double)this.height / 2.0, 0.0);
        GL11.glScaled(scale, scale, 0.0);
        float xOffset = -1.0f * (((float)mouseX - (float)this.width / 2.0f) / ((float)this.width / 16.0f));
        float yOffset = -1.0f * (((float)mouseY - (float)this.height / 2.0f) / ((float)this.height / 9.0f));
        float width = this.width + 94;
        float height = this.height + 66;
        float x = -47.0f + xOffset - width / 2.0f;
        float y = -33.0f + yOffset - height / 2.0f;
        if (this.BG.length != 0) {
            RenderUtils.bindTexture(this.BG[this.indexBG].getGlTextureId());
            RenderUtils.drawTexture(x, y, width, height, alpha);
        } else if (this.texturebg != null) {
            RenderUtils.bindTexture(this.texturebg.getGlTextureId());
            RenderUtils.drawTexture(x, y, width, height, alpha);
        }
        GL11.glPopMatrix();
        RenderUtils.bindTexture(this.texturelogo.getId());
        RenderUtils.drawTexture(20.0, 10.0, 30.0, 30.0, alpha);
        RenderUtils.getFontRender().drawCenteredString(Kura.MOD_NAME, 35.0f, 45.0f, -1);
        for (ButtonComponent button : this.buttonlist) {
            button.mouseMove(mouseX, mouseY);
        }
        for (ButtonComponent button : this.buttonlist) {
            button.render();
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int buttonID) throws IOException {
        for (ButtonComponent button : this.buttonlist) {
            button.mouseclick(mouseX, mouseY, buttonID);
        }
    }
}

