package me.dyzjct.kura.setting;

import me.dyzjct.kura.module.IModule;

import java.util.Arrays;
import java.util.function.Predicate;

public class ModeSetting<T extends Enum<T>> extends Setting<T> {

    private int index;

    public ModeSetting(String modeName, IModule contain, T clazz) {
        super(modeName, contain, clazz);
        this.index = getIndexEnum(clazz);
    }

    public T[] getModes() {
        return (T[])this.getValue().getClass().getEnumConstants();
    }
    public String[] getModesAsStrings() {
        return Arrays.stream(getModes()).map(Enum::toString).toArray(String[]::new);
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
        index = getIndexEnum(getValue());
    }
    public void setValueByString(String str){
        setValue((T)Enum.valueOf(getValue().getClass(), str));
    }
    public void setValueByIndex(int index){
        int id = Math.max(0, Math.min(getModes().length-1, index));
        setValue(getModes()[id]);
    }
    public void forwardLoop() {
        this.index = this.index < this.getModes().length - 1 ? ++this.index : 0;
        setValue(this.getModes()[index]);
    }

    public int getIndexEnum(T clazz){
        for (int E = 0; E < getModes().length; E++){
            if (getModes()[E] == clazz){
                return E;
            }
        }
        return 0;
    }
    public String getValueAsString() {
        return getValue().toString();
    }

    public ModeSetting setOnChange(onChangeListener<T> listener) { return (ModeSetting) super.setOnChange(listener); }

    public ModeSetting<T> v(Predicate<Object> predicate) {
        return (ModeSetting<T>)super.v(predicate);
    }
    public ModeSetting<T> b(BooleanSetting value) {
        return (ModeSetting<T>)super.v((Object v) -> value.getValue());
    }
    public ModeSetting<T> r(BooleanSetting value) {
        return (ModeSetting<T>)super.v((Object v) -> !value.getValue());
    }
    public ModeSetting<T> c(double min, Setting setting, double max) {
        if (setting instanceof IntegerSetting) {
            return (ModeSetting<T>)super.v((Object v) -> (double) ((IntegerSetting) setting).getValue() <= max && (double) ((IntegerSetting) setting).getValue() >= min);
        }
        if (setting instanceof FloatSetting) {
            return (ModeSetting<T>)super.v((Object v) -> (double) ((FloatSetting) setting).getValue() <= max && (double) ((FloatSetting) setting).getValue() >= min);
        }
        if (setting instanceof DoubleSetting) {
            return (ModeSetting<T>)super.v((Object v) -> ((DoubleSetting)setting).getValue() <= max && ((DoubleSetting)setting).getValue() >= min);
        }
        return (ModeSetting<T>)super.v((Object v) -> true);
    }
    public ModeSetting<T> c(double min, Setting setting) {
        if (setting instanceof IntegerSetting) {
            return (ModeSetting)super.v((Object v) -> (double) ((IntegerSetting) setting).getValue() >= min);
        }
        if (setting instanceof FloatSetting) {
            return (ModeSetting)super.v((Object v) -> (double) ((FloatSetting) setting).getValue() >= min);
        }
        if (setting instanceof DoubleSetting) {
            return (ModeSetting)super.v((Object v) -> ((DoubleSetting)setting).getValue() >= min);
        }
        return (ModeSetting)super.v((Object v) -> true);
    }
    public ModeSetting<T> c(Setting setting, double max) {
        if (setting instanceof IntegerSetting) {
            return (ModeSetting)super.v((Object v) -> (double) ((IntegerSetting) setting).getValue() <= max);
        }
        if (setting instanceof FloatSetting) {
            return (ModeSetting)super.v((Object v) -> (double) ((FloatSetting) setting).getValue() <= max);
        }
        if (setting instanceof DoubleSetting) {
            return (ModeSetting)super.v((Object v) -> ((DoubleSetting)setting).getValue() <= max);
        }
        return (ModeSetting)super.v((Object v) -> true);
    }
    public ModeSetting<T> m(ModeSetting<T> value, Enum mode) {
        this.visibility.add(v -> value.getValue() == mode);
        return this;
    }
}

