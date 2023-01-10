package me.windyteam.kura.gui.settingpanel.component.components;

import me.windyteam.kura.gui.settingpanel.Window;
import me.windyteam.kura.gui.settingpanel.component.AbstractComponent;
import me.windyteam.kura.gui.settingpanel.utils.GLUtil;

public class Label
extends AbstractComponent {
    private String text;

    public Label(String text) {
        this.setText(text);
    }

    @Override
    public void render() {
        GLUtil.getFontRenderer().drawString(this.text, this.x, this.y, Window.FONT.getRGB());
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.setWidth(GLUtil.getFontRenderer().getStringWidth(text));
        this.setHeight(GLUtil.getFontRenderer().getHeight());
        this.text = text;
    }
}

