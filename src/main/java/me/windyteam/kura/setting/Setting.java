package me.windyteam.kura.setting;

import me.windyteam.kura.event.events.client.SettingChangeEvent;
import me.windyteam.kura.module.IModule;
import me.windyteam.kura.event.events.client.SettingChangeEvent;
import me.windyteam.kura.module.IModule;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Setting<T> {
    private final T defaultValue;
    private final String name;
    private final IModule contain;
    List<Predicate<Object>> visibility = new ArrayList<>();
    private T value;
    private onChangeListener<T> listener;

    public Setting(String name, IModule contain, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.contain = contain;
        this.visibility.add(V -> true);
    }

    public String getName() {
        return this.name;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public boolean visible() {
        for (Predicate<Object> predicate : this.visibility) {
            if (predicate.test(this)) continue;
            return false;
        }
        return true;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        SettingChangeEvent event = new SettingChangeEvent(this);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return;
        }
        this.value = value;
        if (this.listener != null) {
            this.listener.onChange(value);
        }
    }

    public IModule getContain() {
        return this.contain;
    }

    public Setting<T> v(Predicate<Object> predicate) {
        this.visibility.add(predicate);
        return this;
    }

    public Setting<T> setOnChange(onChangeListener<T> listener) {
        this.listener = listener;
        return this;
    }

    public interface onChangeListener<T> {
        void onChange(T newValue);
    }
}

