package me.dyzjct.kura.utils;

import me.dyzjct.kura.module.Module;

public class ActiveModule {
    public Module mod;
    public String string;

    public ActiveModule(Module module, String string) {
        this.mod = module;
        this.string = string;
    }

    public String getString() {
        return string;
    }
}