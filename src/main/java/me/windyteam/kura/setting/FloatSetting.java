package me.windyteam.kura.setting;

import me.windyteam.kura.module.IModule;
import me.windyteam.kura.module.IModule;

import java.util.function.Predicate;

public class FloatSetting
extends Setting<Float> {
    protected Float min;
    protected Float max;

    public FloatSetting(String name, IModule contain, Float defaultValue, Float min, Float max) {
        super(name, contain, defaultValue);
        this.min = min;
        this.max = max;
    }

    public FloatSetting v(Predicate<Object> predicate) {
        return (FloatSetting)super.v(predicate);
    }

    public FloatSetting setOnChange(onChangeListener listener) { return (FloatSetting) super.setOnChange(listener); }

    public FloatSetting b(BooleanSetting value) {
        return (FloatSetting)super.v((Object v) -> value.getValue());
    }

    public FloatSetting b2(BooleanSetting value) {
        return (FloatSetting)super.v((Object v) -> !value.getValue());
    }

    public FloatSetting r(BooleanSetting value) {
        return (FloatSetting)super.v((Object v) -> value.getValue() == false);
    }

    public FloatSetting c(double min, Setting setting, double max) {
        if (setting instanceof IntegerSetting) {
            return (FloatSetting)super.v((Object v) -> (double)((Integer)((IntegerSetting)setting).getValue()).intValue() <= max && (double)((Integer)((IntegerSetting)setting).getValue()).intValue() >= min);
        }
        if (setting instanceof FloatSetting) {
            return (FloatSetting)super.v((Object v) -> (double)((Float)((FloatSetting)setting).getValue()).floatValue() <= max && (double)((Float)((FloatSetting)setting).getValue()).floatValue() >= min);
        }
        if (setting instanceof DoubleSetting) {
            return (FloatSetting)super.v((Object v) -> (Double)((DoubleSetting)setting).getValue() <= max && (Double)((DoubleSetting)setting).getValue() >= min);
        }
        return (FloatSetting)super.v((Object v) -> true);
    }

    public FloatSetting c(double min, Setting setting) {
        if (setting instanceof IntegerSetting) {
            return (FloatSetting)super.v((Object v) -> (double)((Integer)((IntegerSetting)setting).getValue()).intValue() >= min);
        }
        if (setting instanceof FloatSetting) {
            return (FloatSetting)super.v((Object v) -> (double)((Float)((FloatSetting)setting).getValue()).floatValue() >= min);
        }
        if (setting instanceof DoubleSetting) {
            return (FloatSetting)super.v((Object v) -> (Double)((DoubleSetting)setting).getValue() >= min);
        }
        return (FloatSetting)super.v((Object v) -> true);
    }

    public FloatSetting c(Setting setting, double max) {
        if (setting instanceof IntegerSetting) {
            return (FloatSetting)super.v((Object v) -> (double)((Integer)((IntegerSetting)setting).getValue()).intValue() <= max);
        }
        if (setting instanceof FloatSetting) {
            return (FloatSetting)super.v((Object v) -> (double)((Float)((FloatSetting)setting).getValue()).floatValue() <= max);
        }
        if (setting instanceof DoubleSetting) {
            return (FloatSetting)super.v((Object v) -> (Double)((DoubleSetting)setting).getValue() <= max);
        }
        return (FloatSetting)super.v((Object v) -> true);
    }

    public FloatSetting m(ModeSetting value, Enum mode) {
        this.visibility.add(v -> value.getValue() == mode);
        return this;
    }

    public FloatSetting m2(ModeSetting value, Enum mode) {
        this.visibility.add(v -> value.getValue() != mode);
        return this;
    }

    public Float getMin() {
        return this.min;
    }

    public Float getMax() {
        return this.max;
    }
}

