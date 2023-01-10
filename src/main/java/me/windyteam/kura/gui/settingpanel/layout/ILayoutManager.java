package me.windyteam.kura.gui.settingpanel.layout;

import me.windyteam.kura.gui.settingpanel.component.AbstractComponent;
import me.windyteam.kura.gui.settingpanel.component.AbstractComponent;

import java.util.List;

public interface ILayoutManager {
    public int[] getOptimalDiemension(List<AbstractComponent> var1, int var2);

    public Layout buildLayout(List<AbstractComponent> var1, int var2, int var3);
}

