package me.dyzjct.kura.gui.clickgui.component;

import me.dyzjct.kura.setting.ModeSetting;
import me.dyzjct.kura.setting.Setting;

public abstract class SettingButton<T>
extends Component {
    private Setting<T> value;

    public Setting<T> getValue() {
        return this.value;
    }

    public ModeSetting getAsModeValue() {
        return (ModeSetting)this.value;
    }

    public void setValue(Setting<T> value) {
        this.value = value;
    }
}

