package me.dyzjct.kura.setting;

import me.dyzjct.kura.module.IModule;

import java.util.function.Predicate;

public class IntegerSetting
        extends Setting<Integer> {
    protected Integer min;
    protected Integer max;

    public IntegerSetting(String name, IModule contain, Integer defaultValue, Integer min, Integer max) {
        super(name, contain, defaultValue);
        this.min = min;
        this.max = max;
    }

    public IntegerSetting v(Predicate<Object> predicate) {
        return (IntegerSetting) super.v(predicate);
    }

    public IntegerSetting setOnChange(onChangeListener listener) {
        return (IntegerSetting) super.setOnChange(listener);
    }

    public IntegerSetting b(BooleanSetting value) {
        return (IntegerSetting) super.v((Object v) -> value.getValue());
    }

    public IntegerSetting bn(BooleanSetting value) {
        return (IntegerSetting) super.v((Object v) -> !value.getValue());
    }

    public IntegerSetting r(BooleanSetting value) {
        return (IntegerSetting) super.v((Object v) -> !value.getValue());
    }

    public IntegerSetting c(double min, Setting setting, double max) {
        if (setting instanceof IntegerSetting) {
            return (IntegerSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue() <= max && (double) ((IntegerSetting) setting).getValue().intValue() >= min);
        }
        if (setting instanceof FloatSetting) {
            return (IntegerSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue() <= max && (double) ((FloatSetting) setting).getValue().floatValue() >= min);
        }
        if (setting instanceof DoubleSetting) {
            return (IntegerSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() <= max && ((DoubleSetting) setting).getValue() >= min);
        }
        return (IntegerSetting) super.v((Object v) -> true);
    }

    public IntegerSetting c(double min, Setting setting) {
        if (setting instanceof IntegerSetting) {
            return (IntegerSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue() >= min);
        }
        if (setting instanceof FloatSetting) {
            return (IntegerSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue() >= min);
        }
        if (setting instanceof DoubleSetting) {
            return (IntegerSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() >= min);
        }
        return (IntegerSetting) super.v((Object v) -> true);
    }

    public IntegerSetting c(Setting setting, double max) {
        if (setting instanceof IntegerSetting) {
            return (IntegerSetting) super.v((Object v) -> (double) ((IntegerSetting) setting).getValue() <= max);
        }
        if (setting instanceof FloatSetting) {
            return (IntegerSetting) super.v((Object v) -> (double) ((FloatSetting) setting).getValue() <= max);
        }
        if (setting instanceof DoubleSetting) {
            return (IntegerSetting) super.v((Object v) -> ((DoubleSetting) setting).getValue() <= max);
        }
        return (IntegerSetting) super.v((Object v) -> true);
    }

    public IntegerSetting m(ModeSetting value, Enum mode) {
        this.visibility.add(v -> value.getValue() == mode);
        return this;
    }

    public IntegerSetting no(ModeSetting value, Enum mode) {
        this.visibility.add(v -> value.getValue() != mode);
        return this;
    }

    public Integer getMin() {
        return this.min;
    }

    public Integer getMax() {
        return this.max;
    }
}

