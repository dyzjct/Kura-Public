package me.dyzjct.kura.setting;

import me.dyzjct.kura.module.IModule;

import java.util.function.Predicate;

public class StringSetting
        extends Setting<String> {
    public StringSetting(String name, IModule contain, String defaultValue) {
        super(name, contain, defaultValue);
    }

    public StringSetting v(Predicate<Object> predicate) {
        return (StringSetting) super.v(predicate);
    }

    public StringSetting setOnChange(onChangeListener listener) {
        return (StringSetting) super.setOnChange(listener);
    }

    public StringSetting b(BooleanSetting value) {
        return (StringSetting) super.v((Object v) -> value.getValue());
    }

    public StringSetting r(BooleanSetting value) {
        return (StringSetting) super.v((Object v) -> value.getValue() == false);
    }

    public StringSetting c(double min, Setting setting, double max) {
        if (setting instanceof IntegerSetting) {
            return (StringSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue().intValue() <= max && (double) ((IntegerSetting) setting).getValue().intValue() >= min);
        }
        if (setting instanceof FloatSetting) {
            return (StringSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue().floatValue() <= max && (double) ((FloatSetting) setting).getValue().floatValue() >= min);
        }
        if (setting instanceof DoubleSetting) {
            return (StringSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() <= max && ((DoubleSetting) setting).getValue() >= min);
        }
        return (StringSetting) super.v((Object v) -> true);
    }

    public StringSetting c(double min, Setting setting) {
        if (setting instanceof IntegerSetting) {
            return (StringSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue().intValue() >= min);
        }
        if (setting instanceof FloatSetting) {
            return (StringSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue().floatValue() >= min);
        }
        if (setting instanceof DoubleSetting) {
            return (StringSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() >= min);
        }
        return (StringSetting) super.v((Object v) -> true);
    }

    public StringSetting c(Setting setting, double max) {
        if (setting instanceof IntegerSetting) {
            return (StringSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue().intValue() <= max);
        }
        if (setting instanceof FloatSetting) {
            return (StringSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue().floatValue() <= max);
        }
        if (setting instanceof DoubleSetting) {
            return (StringSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() <= max);
        }
        return (StringSetting) super.v((Object v) -> true);
    }

    public StringSetting m(ModeSetting value, Enum mode) {
        this.visibility.add(v -> value.getValue() == mode);
        return this;
    }
}

