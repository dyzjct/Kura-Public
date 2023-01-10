package me.windyteam.kura.setting;

import me.windyteam.kura.module.IModule;
import me.windyteam.kura.module.IModule;

import java.awt.*;
import java.util.function.Predicate;

public class ColorSetting
        extends Setting<Color> {
    public ColorSetting(String name, IModule contain, Color defaultValue) {
        super(name, contain, defaultValue);
    }

    public ColorSetting v(Predicate<Object> predicate) {
        return (ColorSetting) super.v(predicate);
    }

    public ColorSetting setOnChange(onChangeListener listener) {
        return (ColorSetting) super.setOnChange(listener);
    }

    public ColorSetting b(BooleanSetting value) {
        return (ColorSetting) super.v((Object v) -> value.getValue());
    }

    public ColorSetting r(BooleanSetting value) {
        return (ColorSetting) super.v((Object v) -> !value.getValue());
    }

    public ColorSetting c(double min, Setting setting, double max) {
        if (setting instanceof IntegerSetting) {
            return (ColorSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue() <= max && (double) ((IntegerSetting) setting).getValue() >= min);
        }
        if (setting instanceof FloatSetting) {
            return (ColorSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue() <= max && (double) ((FloatSetting) setting).getValue() >= min);
        }
        if (setting instanceof DoubleSetting) {
            return (ColorSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() <= max && ((DoubleSetting) setting).getValue() >= min);
        }
        return (ColorSetting) super.v((Object v) -> true);
    }

    public ColorSetting c(double min, Setting setting) {
        if (setting instanceof IntegerSetting) {
            return (ColorSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue() >= min);
        }
        if (setting instanceof FloatSetting) {
            return (ColorSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue() >= min);
        }
        if (setting instanceof DoubleSetting) {
            return (ColorSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() >= min);
        }
        return (ColorSetting) super.v((Object v) -> true);
    }

    public ColorSetting c(Setting setting, double max) {
        if (setting instanceof IntegerSetting) {
            return (ColorSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue() <= max);
        }
        if (setting instanceof FloatSetting) {
            return (ColorSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue() <= max);
        }
        if (setting instanceof DoubleSetting) {
            return (ColorSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() <= max);
        }
        return (ColorSetting) super.v((Object v) -> true);
    }

    public ColorSetting m(ModeSetting value, Enum mode) {
        this.visibility.add(v -> value.getValue() == mode);
        return this;
    }
}

