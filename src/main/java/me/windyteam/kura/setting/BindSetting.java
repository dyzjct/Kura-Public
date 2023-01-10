package me.windyteam.kura.setting;

import me.windyteam.kura.module.IModule;
import me.windyteam.kura.module.IModule;

import java.util.function.Predicate;

public class BindSetting extends Setting<Integer> {
    public BindSetting(String name, IModule contain, int keyboard) {
        super(name, contain, keyboard);
    }

    public BindSetting v(Predicate<Object> predicate) {
        return (BindSetting) super.v(predicate);
    }

    public BindSetting setOnChange(onChangeListener<Integer> listener) {
        return (BindSetting) super.setOnChange(listener);
    }

    public BindSetting c(double min, Setting<?> setting, double max) {
        if (setting instanceof IntegerSetting) {
            return (BindSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue() <= max && (double) ((IntegerSetting) setting).getValue() >= min);
        }
        if (setting instanceof FloatSetting) {
            return (BindSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue() <= max && (double) ((FloatSetting) setting).getValue() >= min);
        }
        if (setting instanceof DoubleSetting) {
            return (BindSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() <= max && ((DoubleSetting) setting).getValue() >= min);
        }
        return (BindSetting) super.v((Object v) -> true);
    }

    public BindSetting c(double min, Setting<?> setting) {
        if (setting instanceof IntegerSetting) {
            return (BindSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue() >= min);
        }
        if (setting instanceof FloatSetting) {
            return (BindSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue() >= min);
        }
        if (setting instanceof DoubleSetting) {
            return (BindSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() >= min);
        }
        return (BindSetting) super.v((Object v) -> true);
    }

    public BindSetting c(Setting<?> setting, double max) {
        if (setting instanceof IntegerSetting) {
            return (BindSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue() <= max);
        }
        if (setting instanceof FloatSetting) {
            return (BindSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue() <= max);
        }
        if (setting instanceof DoubleSetting) {
            return (BindSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() <= max);
        }
        return (BindSetting) super.v((Object v) -> true);
    }

    public BindSetting m(ModeSetting<?> value, Enum<?> mode) {
        this.visibility.add(v -> value.getValue() == mode);
        return this;
    }
}