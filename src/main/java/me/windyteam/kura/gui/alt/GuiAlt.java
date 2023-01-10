package me.windyteam.kura.gui.alt;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.windyteam.kura.gui.alt.utils.*;
import me.windyteam.kura.gui.settingpanel.component.components.Pane;
import me.windyteam.kura.gui.settingpanel.component.components.ScrollPane;
import me.windyteam.kura.gui.settingpanel.layout.GridLayout;
import me.windyteam.kura.gui.window.EmptyWindowNoDrag;
import me.windyteam.kura.gui.window.components.ButtonAlt;
import me.windyteam.kura.utils.font.FontUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public final class GuiAlt extends GuiScreen {

    private PasswordField password;
    private final GuiScreen previousScreen;
    private AltLoginThread thread;
    private GuiTextField username;
    private final EmptyWindowNoDrag window = new EmptyWindowNoDrag(0, 0, width/8*3+10, height);

    public GuiAlt(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
        updateAlt();
        window.getContentPane().getComponent().forEach(abstractComponent ->{
            if (abstractComponent instanceof ButtonAlt){
                ((ButtonAlt) abstractComponent).reset();
            }
        });
    }

    public void updateAlt() {
        Pane contentPane = new ScrollPane(new GridLayout(1));
        for (Alt alts : AltSystem.getAlts()) {
            ButtonAlt dir = new ButtonAlt(alts.getUsername(), width/8*3-35, 15);
            dir.setOnClickListener(() -> {
                this.username.setText(alts.getUsername());
                this.password.setText(alts.getPassword());
                shouldRemove = true;
            });
            contentPane.addComponent(dir);
        }
        window.setContentPane(contentPane);
    }



    private boolean shouldRemove = false;

    @Override
    public void actionPerformed(GuiButton button) {

        switch (button.id) {
            case 21: {
                if (!shouldRemove) {
                    if (!this.username.getText().equals(""))
                        AltSystem.getAlts().add(new Alt(this.username.getText(), this.password.getText()));
                }else {
                    AltSystem.getAlts().removeIf(alts -> alts.getUsername().equalsIgnoreCase(this.username.getText()));
                    shouldRemove = false;
                }
                updateAlt();
                break;
            }
            case 1: {
                this.mc.displayGuiScreen(this.previousScreen);
                break;
            }
            case 0: {
                this.thread = new AltLoginThread(this.username.getText(), this.password.getText());
                this.thread.start();
                //TODO: Add Player Viewer
                //MelonMod.getInstance().newMcGuiManager.refresh();
            }
        }
    }

    @Override
    public void drawScreen(int x2, int y2, float z2) {
        this.drawDefaultBackground();
        this.username.drawTextBox();
        this.password.drawTextBox();
        FontUtils.Montserrat.drawCenteredString("Alt Login", width / 2F, 20, -1);
        FontUtils.Montserrat.drawCenteredString(this.thread == null ? ChatFormatting.GRAY + "Idle...": this.thread.getStatus(), width / 2F, 29, -1);
        this.drawCenteredString(this.mc.fontRenderer, "[Current user: " + mc.session.getUsername() + "]", width / 2, 40, -1);
        if (this.username.getText().isEmpty()) {
            this.drawString(this.mc.fontRenderer, "Username / E-Mail", width / 2 - 96, 66, -7829368);
        }
        if (this.password.getText().isEmpty()) {
            this.drawString(this.mc.fontRenderer, "Password", width / 2 - 96, 106, -7829368);
        }

        super.drawScreen(x2, y2, z2);

        window.render();
        window.mouseMoved(x2 , y2);

        for (Alt ats : AltSystem.getAlts()){
            if (ats.getUsername().equalsIgnoreCase(this.username.getText())){
                shouldRemove = true;
                break;
            }else {
                shouldRemove = false;
            }
        }

        if (shouldRemove){
            buttonList.stream().filter(button -> button.id == 21).forEach(e -> e.displayString = "Remove From List");
        }else {
            buttonList.stream().filter(button -> button.id == 21).forEach(e -> e.displayString = "Add To List");
        }
    }

    @Override
    public void initGui() {
        int var3 = height / 4 + 24;
        this.buttonList.add(new GuiButton(0, width / 2 - 100, var3 + 72 + 12, "Login"));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, var3 + 72 + 12 + 24, "Back"));
        this.buttonList.add(new GuiButton(21, width / 2 - 100, var3 + 72 + 12 + 24 + 24, "Add To List"));
        this.username = new UserNameField(var3, this.mc.fontRenderer, width / 2 - 100, 60, 200, 20);
        this.password = new PasswordField(this.mc.fontRenderer, width / 2 - 100, 100, 200, 20);
        this.username.setFocused(true);
        Keyboard.enableRepeatEvents(true);
        window.setWidth(width/8*3-35+10);
        window.setHeight(height);
        updateAlt();
    }

    @Override
    public void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t') {
            if (!this.username.isFocused() && !this.password.isFocused()) {
                this.username.setFocused(true);
            } else {
                this.username.setFocused(this.password.isFocused());
                this.password.setFocused(!this.username.isFocused());
            }
        }
        if (character == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }
        this.username.textboxKeyTyped(character, key);
        this.password.textboxKeyTyped(character, key);

        window.keyPressed(key, character);
    }

    @Override
    public void mouseClicked(int x2, int y2, int button) {
        try {
            super.mouseClicked(x2, y2, button);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.username.mouseClicked(x2, y2, button);
        this.password.mouseClicked(x2, y2, button);

        window.mousePressed(button, x2, y2);
    }

    @Override
    public void mouseReleased(int p_mouseReleased_1_, int p_mouseReleased_2_, int p_mouseReleased_3_) {
        window.mouseReleased(p_mouseReleased_3_, p_mouseReleased_1_, p_mouseReleased_2_);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int eventDWheel = Mouse.getEventDWheel();

        window.mouseWheel(eventDWheel);

    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
    }
}

