package me.dyzjct.kura.event.events.client;

import me.dyzjct.kura.event.EventStage;
import me.dyzjct.kura.module.IModule;
import me.dyzjct.kura.setting.Setting;

public class SettingChangeEvent
extends EventStage {
    private IModule iModule;
    private Setting setting;

    public SettingChangeEvent(int stage, IModule iModule) {
        super(stage);
        this.iModule = iModule;
    }

    public SettingChangeEvent(Setting setting) {
        super(2);
        this.setting = setting;
    }

    public IModule getModule() {
        return this.iModule;
    }

    public Setting getSetting() {
        return this.setting;
    }
}

