package me.windyteam.kura.setting;

import me.windyteam.kura.module.IModule;
import me.windyteam.kura.module.IModule;

import java.util.function.Predicate;

public class BooleanSetting
        extends Setting<Boolean> {
    public BooleanSetting(String name, IModule contain, Boolean defaultValue) {
        super(name, contain, defaultValue);
    }

    public BooleanSetting v(Predicate<Object> predicate) {
        return (BooleanSetting) super.v(predicate);
    }

    public BooleanSetting setOnChange(onChangeListener<Boolean> listener) {
        return (BooleanSetting) super.setOnChange(listener);
    }

    public BooleanSetting b(BooleanSetting value) {
        return (BooleanSetting) super.v((Object v) -> value.getValue());
    }

    public BooleanSetting bn(BooleanSetting value) {
        return (BooleanSetting) super.v((Object v) -> !value.getValue());
    }

    public BooleanSetting r(BooleanSetting value) {
        return (BooleanSetting) super.v((Object v) -> !value.getValue());
    }

    public BooleanSetting c(double min, Setting<?> setting, double max) {
        if (setting instanceof IntegerSetting) {
            return (BooleanSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue() <= max && (double) ((IntegerSetting) setting).getValue() >= min);
        }
        if (setting instanceof FloatSetting) {
            return (BooleanSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue() <= max && (double) ((FloatSetting) setting).getValue() >= min);
        }
        if (setting instanceof DoubleSetting) {
            return (BooleanSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() <= max && ((DoubleSetting) setting).getValue() >= min);
        }
        return (BooleanSetting) super.v((Object v) -> true);
    }

    public BooleanSetting c(double min, Setting<?> setting) {
        if (setting instanceof IntegerSetting) {
            return (BooleanSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue() >= min);
        }
        if (setting instanceof FloatSetting) {
            return (BooleanSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue() >= min);
        }
        if (setting instanceof DoubleSetting) {
            return (BooleanSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() >= min);
        }
        return (BooleanSetting) super.v((Object v) -> true);
    }

    public BooleanSetting c(Setting<?> setting, double max) {
        if (setting instanceof IntegerSetting) {
            return (BooleanSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue() <= max);
        }
        if (setting instanceof FloatSetting) {
            return (BooleanSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue() <= max);
        }
        if (setting instanceof DoubleSetting) {
            return (BooleanSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() <= max);
        }
        return (BooleanSetting) super.v((Object v) -> true);
    }

    public BooleanSetting m(ModeSetting<?> value, Enum<?> mode) {
        this.visibility.add(v -> value.getValue() == mode);
        return this;
    }
}

