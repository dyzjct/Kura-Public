package me.windyteam.kura.event.events.client;

import me.windyteam.kura.event.EventStage;
import me.windyteam.kura.module.IModule;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.module.IModule;

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

