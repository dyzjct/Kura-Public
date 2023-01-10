package me.windyteam.kura.gui.clickgui.component;

import me.windyteam.kura.gui.clickgui.Panel;
import net.minecraft.client.Minecraft;

public abstract class Component {
    public Minecraft mc = Minecraft.getMinecraft();
    public int x;
    public int y;
    public int width;
    public int height;
    public Panel father;
    public double add;
    public boolean isToggled;
    public boolean isExtended;

    public abstract void render(int var1, int var2, float var3);

    public abstract boolean mouseClicked(int var1, int var2, int var3);

    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    public void keyTyped(char typedChar, int keyCode) {
    }

    public void setAdd(double add2) {
        this.add = add2;
    }

    public void solvePos() {
        this.x = this.father.x;
        this.y = this.father.y;
    }

    protected boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= Math.min(this.x, this.x + this.width) && mouseX <= Math.max(this.x, this.x + this.width) && mouseY >= Math.min(this.y, this.y + this.height) && mouseY <= Math.max(this.y, this.y + this.height);
    }
}

