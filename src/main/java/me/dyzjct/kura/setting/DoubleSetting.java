package me.dyzjct.kura.setting;

import me.dyzjct.kura.module.IModule;

import java.util.function.Predicate;

public class DoubleSetting
        extends Setting<Double> {
    protected Double min;
    protected Double max;

    public DoubleSetting(String name, IModule contain, Double defaultValue, Double min, Double max) {
        super(name, contain, defaultValue);
        this.min = min;
        this.max = max;
    }

    public DoubleSetting v(Predicate<Object> predicate) {
        return (DoubleSetting) super.v(predicate);
    }

    public DoubleSetting setOnChange(onChangeListener listener) {
        return (DoubleSetting) super.setOnChange(listener);
    }

    public DoubleSetting b(BooleanSetting value) {
        return (DoubleSetting) super.v((Object v) -> value.getValue());
    }

    public DoubleSetting nb(BooleanSetting value) {
        return (DoubleSetting) super.v((Object v) -> !value.getValue());
    }

    public DoubleSetting r(BooleanSetting value) {
        return (DoubleSetting) super.v((Object v) -> value.getValue() == false);
    }

    public DoubleSetting c(double min, Setting setting, double max) {
        if (setting instanceof IntegerSetting) {
            return (DoubleSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue().intValue() <= max && (double) ((IntegerSetting) setting).getValue().intValue() >= min);
        }
        if (setting instanceof FloatSetting) {
            return (DoubleSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue().floatValue() <= max && (double) ((FloatSetting) setting).getValue().floatValue() >= min);
        }
        if (setting instanceof DoubleSetting) {
            return (DoubleSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() <= max && ((DoubleSetting) setting).getValue() >= min);
        }
        return (DoubleSetting) super.v((Object v) -> true);
    }

    public DoubleSetting c(double min, Setting setting) {
        if (setting instanceof IntegerSetting) {
            return (DoubleSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue().intValue() >= min);
        }
        if (setting instanceof FloatSetting) {
            return (DoubleSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue().floatValue() >= min);
        }
        if (setting instanceof DoubleSetting) {
            return (DoubleSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() >= min);
        }
        return (DoubleSetting) super.v((Object v) -> true);
    }

    public DoubleSetting c(Setting setting, double max) {
        if (setting instanceof IntegerSetting) {
            return (DoubleSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue().intValue() <= max);
        }
        if (setting instanceof FloatSetting) {
            return (DoubleSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue().floatValue() <= max);
        }
        if (setting instanceof DoubleSetting) {
            return (DoubleSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() <= max);
        }
        return (DoubleSetting) super.v((Object v) -> true);
    }

    public DoubleSetting m(ModeSetting value, Enum mode) {
        this.visibility.add(v -> value.getValue() == mode);
        return this;
    }

    public Double getMin() {
        return this.min;
    }

    public Double getMax() {
        return this.max;
    }
}

